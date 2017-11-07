package com.pdf.reader.pdfviewer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pdf.reader.pdfviewer.R;
import com.pdf.reader.pdfviewer.adapter.MultiTypeAdapter;
import com.pdf.reader.pdfviewer.dialogs.DeleteDialog;
import com.pdf.reader.pdfviewer.dialogs.InformationDialog;
import com.pdf.reader.pdfviewer.dialogs.LoadingDialog;
import com.pdf.reader.pdfviewer.dialogs.RateDialog;
import com.pdf.reader.pdfviewer.entity.MyFile;
import com.pdf.reader.pdfviewer.entity.MyFolder;
import com.pdf.reader.pdfviewer.helper.Ads;
import com.pdf.reader.pdfviewer.helper.MyHelper;
import com.pdf.reader.pdfviewer.holder.FileViewHolder;
import com.pdf.reader.pdfviewer.holder.FolderViewHolder;
import com.pdf.reader.pdfviewer.interfaces.RateInterface;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.zer.android.ZAndroidSDK;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private RelativeLayout layoutAds;

    private Toolbar appToolbar;
    private ImageView imSearch;
    private ImageView imMenu;

    private ArrayList<Object> fileList = new ArrayList<>();
    private ArrayList<Object> adapterList = new ArrayList<>();

    private ArrayList<MyFile> allPDF = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private RecyclerView recyclerView;

    private LinearLayout layoutNoData;
    private LinearLayout layoutRescan;
    private RelativeLayout layoutSearch;
    private EditText edtSearch;
    private ImageView imCancelSearch;

    public static final String TAG = "FILE";
    public static final String FOLDER_NAME = "NAME";
    public static final String FOLDER_TAG = "FOLDER";

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, SplashActivity.class));
        setContentView(R.layout.activity_main);

        initUI();

        if (MyHelper.checkPermission(PERMISSIONS, this) == PackageManager.PERMISSION_GRANTED) {
            LoadPDF async = new LoadPDF();
            async.execute();
        }

        imSearch.setOnClickListener(this);
        imMenu.setOnClickListener(this);

        IntentFilter filter = new IntentFilter("com.load.file");
        filter.addAction("com.finish.app");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case "com.load.file":
                        new LoadPDF().execute();
                        break;

                    case "com.finish.app":
                        finish();
                        break;

                    default:
                        break;
                }
            }
        };
        registerReceiver(mReceiver, filter);

        ZAndroidSDK.init(this);
        Ads.b(this, layoutAds, new Ads.OnAdsListener() {
            @Override
            public void onError() {
                layoutAds.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                layoutAds.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdOpened() {
                layoutAds.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initUI() {
        layoutAds = (RelativeLayout) findViewById(R.id.layout_ads);

        appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);
        imSearch = appToolbar.findViewById(R.id.im_search);
        imMenu = appToolbar.findViewById(R.id.im_menu);

        layoutNoData = (LinearLayout) findViewById(R.id.layout_no_data);
        layoutRescan = (LinearLayout) findViewById(R.id.layout_rescan);
        layoutSearch = (RelativeLayout) findViewById(R.id.layout_search);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        imCancelSearch = (ImageView) findViewById(R.id.im_cancel_search);

        initFileRecyclerView();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("StaticFieldLeak")
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    new AsyncTask<Void, Void, ArrayList<Object>>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            adapterList.clear();
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        protected ArrayList<Object> doInBackground(Void... voids) {
                            ArrayList<Object> tempList = new ArrayList<>();
                            for (int i = 0; i < fileList.size(); i++) {
                                if (fileList.get(i) instanceof MyFolder) {
                                    if (((MyFolder) fileList.get(i)).getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                        tempList.add(fileList.get(i));
                                    }
                                }
                                if (fileList.get(i) instanceof MyFile) {
                                    if (((MyFile) fileList.get(i)).getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                        tempList.add(fileList.get(i));
                                    }
                                }
                            }
                            return tempList;
                        }

                        @Override
                        protected void onPostExecute(final ArrayList<Object> files) {
                            super.onPostExecute(files);
                            adapterList.addAll(files);
                            adapter.notifyDataSetChanged();
                        }
                    }.execute();
                } else {
                    adapterList.clear();
                    adapter.notifyDataSetChanged();
                    adapterList.addAll(fileList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        layoutRescan.setOnClickListener(this);
        imCancelSearch.setOnClickListener(this);
    }

    private void initFileRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.list_multiple_view);
        fileListener = new FileViewHolder.FileListener() {
            @Override
            public void onFileResult(MyFile file) {
                Bundle animation = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.slide_in_left, R.anim.slide_in_right).toBundle();
                Intent intent = new Intent(MainActivity.this, ViewFileActivity.class);
                File file1 = new File(file.getPath());
                intent.putExtra(TAG, file1);
                startActivity(intent, animation);
                Ads.f(MainActivity.this);
            }

            @Override
            public void onMoreInfo(MyFile file) {
                File file1 = new File(file.getPath());
                InformationDialog informationDialog = new InformationDialog(MainActivity.this, file1);
                informationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                informationDialog.show();
            }

            @Override
            public void shareFile(MyFile file) {
                try {
                    File file1 = new File(file.getPath());
                    Uri contentUri = FileProvider.getUriForFile(MainActivity.this,
                            "com.pdf.reader.pdfviewer.provider", file1);
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("application/pdf");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share_with) + " "));
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, getString(R.string.cannot_share), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void deleteFile(final MyFile myfile) {
                final File file1 = new File(myfile.getPath());
                DeleteDialog deleteDialog = new DeleteDialog(MainActivity.this, file1, new DeleteDialog.DeleteListener() {
                    @Override
                    public void onDelete(File file) {
                        if (file1.delete()) {
                            fileList.remove(myfile);
                            adapterList.remove(myfile);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, getString(R.string.file_delete), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.cannot_delete), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deleteDialog.show();
            }
        };
        folderListener = new FolderViewHolder.FolderListener() {
            @Override
            public void onFolderResult(MyFolder folder) {
                Bundle animation = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.slide_in_left, R.anim.slide_in_right).toBundle();
                Intent intent = new Intent(MainActivity.this, FolderActivity.class);
                ArrayList<MyFile> myFiles = folder.getListFile();
                ArrayList<String> path = new ArrayList<>();
                for (MyFile file : myFiles) {
                    String s = file.getPath();
                    path.add(s);
                }
                Bundle bundle = new Bundle();
                bundle.putString(FOLDER_NAME, folder.getName());
                bundle.putStringArrayList(FOLDER_TAG, path);
                intent.putExtras(bundle);
                startActivity(intent, animation);
            }
        };

        adapter = new MultiTypeAdapter(adapterList, MainActivity.this, fileListener, folderListener);
        boolean isSwitched = adapter.toggleItemViewType();
        recyclerView.setLayoutManager(isSwitched ? new LinearLayoutManager(MainActivity.this) : new GridLayoutManager(MainActivity.this, 2));
        recyclerView.setAdapter(adapter);
    }

    private FileViewHolder.FileListener fileListener;
    private FolderViewHolder.FolderListener folderListener;

    public ArrayList<MyFile> getAllPDF() {
        ArrayList<MyFile> results = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = null;
        String sortOrder = null;
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String[] selectionArgsPdf = new String[]{mimeType};
        Cursor allPdfFiles = cr.query(uri, projection, selectionMimeType, selectionArgsPdf, sortOrder);
        if (allPdfFiles.moveToFirst()) {
            do {
                String path = allPdfFiles.getString(1);
                Log.d("Path pdf", path);
                File file = new File(path);
                Bitmap bitmap = generateImageFromPdf(Uri.fromFile(file));
                String name = allPdfFiles.getString(8);
                String parent = MyHelper.getParentDirectoryName(file.getParent());
                if (bitmap != null) {
                    MyFile myFile = new MyFile(name, path, parent, bitmap);
                    Log.v("Parent", myFile.getParent());
                    results.add(myFile);
                }
            } while (allPdfFiles.moveToNext());
        }
        return results;
    }

    public ArrayList<MyFolder> filterFolder(ArrayList<MyFile> list) {
        ArrayList<MyFolder> listFolder = new ArrayList<>();
        for (MyFile file : list) {
            String parentName = file.getParent();
            if (!parentName.matches("0")) {
                ArrayList<MyFile> listFileChild = getFileByParent(parentName, list);
                MyFolder folder = new MyFolder(parentName, listFileChild);
                if (isFolderNotExist(listFolder, folder)) {
                    listFolder.add(folder);
                }
            }
        }
        return listFolder;
    }

    public boolean isFolderNotExist(ArrayList<MyFolder> list, MyFolder newItem) {
        boolean flag = true;
        if (list == null || list.size() == 0) {
            flag = true;
        } else {
            for (MyFolder folder : list) {
                if (newItem.getName().matches(folder.getName())) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    public ArrayList<MyFile> filterFile(ArrayList<MyFile> list) {
        ArrayList<MyFile> listFile = new ArrayList<>();
        for (MyFile file : list) {
            if (file.getParent().matches("0")) {
                listFile.add(file);
            }
        }
        return listFile;
    }

    public ArrayList<MyFile> getFileByParent(String parentName, ArrayList<MyFile> files) {
        ArrayList<MyFile> arrayList = new ArrayList<>();
        for (MyFile file : files) {
            if (file.getParent().matches(parentName)) {
                arrayList.add(file);
            }
        }
        return arrayList;
    }

    private Bitmap generateImageFromPdf(Uri pdfUri) {
        int pageNumber = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(this);
        try {
            ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            pdfiumCore.closeDocument(pdfDocument); // important!
            return bmp;
        } catch (Exception e) {
            Log.e("Generate Image False", e.toString());
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutSearch.getVisibility() != View.VISIBLE) {
            RateDialog rateDialog = new RateDialog(MainActivity.this, new RateInterface() {
                @Override
                public void cancelClicked() {
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }

                @Override
                public void rateClicked() {
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    MyHelper.rateApp(MainActivity.this);
                }
            });
            rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            rateDialog.show();
        } else {
            edtSearch.setText("");
            layoutSearch.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_search:
                if (layoutSearch.getVisibility() != View.VISIBLE) {
                    layoutSearch.setVisibility(View.VISIBLE);
                } else {
                    edtSearch.setText("");
                    layoutSearch.setVisibility(View.GONE);
                }
                break;

            case R.id.im_menu:
                PopupMenu popup = new PopupMenu(MainActivity.this, imMenu);
                popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.switch_view) {
                            boolean isSwitched = adapter.toggleItemViewType();
                            recyclerView.setLayoutManager(isSwitched ? new LinearLayoutManager(MainActivity.this) : new GridLayoutManager(MainActivity.this, 2));
                            adapter.notifyDataSetChanged();
                        }
                        if (item.getItemId() == R.id.rate_app) {
                            MyHelper.rateApp(MainActivity.this);
                        }
                        if (item.getItemId() == R.id.menu_rescan) {
                            new LoadPDF().execute();
                        }
                        return true;
                    }
                });
                popup.show();
                break;

            case R.id.layout_rescan:
                new LoadPDF().execute();
                break;

            case R.id.im_cancel_search:
                edtSearch.setText("");
                break;

            default:
                break;
        }
    }

    /**
     * AsyncTask load pdf file
     */
    private class LoadPDF extends AsyncTask<Void, Void, ArrayList<Object>> {
        LoadingDialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = new LoadingDialog(MainActivity.this);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.show();
            try {
                fileList.clear();
                adapterList.clear();
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected ArrayList<Object> doInBackground(Void... voids) {
            ArrayList<Object> listObject = new ArrayList<>();
            allPDF = getAllPDF();
            ArrayList<MyFile> filesOnly = filterFile(allPDF);
            ArrayList<MyFolder> folders = filterFolder(allPDF);

            for (MyFolder folder : folders) {
                listObject.add(folder);
            }

            for (MyFile file : filesOnly) {
                listObject.add(file);
            }
            return listObject;
        }


        @Override
        protected void onPostExecute(final ArrayList<Object> data) {
            super.onPostExecute(data);
            if (data == null || data.size() == 0) {
                loadingDialog.dismiss();
                layoutNoData.setVisibility(View.VISIBLE);
                return;
            }
            layoutNoData.setVisibility(View.GONE);
            fileList.addAll(data);
            adapterList.addAll(fileList);
            adapter.notifyDataSetChanged();
            loadingDialog.dismiss();
            sendBroadcast(new Intent("com.finish.splash"));
        }
    }


}
