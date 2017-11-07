package com.pdf.reader.pdfviewer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pdf.reader.pdfviewer.R;
import com.pdf.reader.pdfviewer.adapter.ListFileAdapter;
import com.pdf.reader.pdfviewer.dialogs.DeleteDialog;
import com.pdf.reader.pdfviewer.dialogs.InformationDialog;
import com.pdf.reader.pdfviewer.dialogs.LoadingDialog;
import com.pdf.reader.pdfviewer.entity.MyFile;
import com.pdf.reader.pdfviewer.helper.Ads;
import com.pdf.reader.pdfviewer.helper.MyHelper;
import com.pdf.reader.pdfviewer.holder.FileViewHolder;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.util.ArrayList;

public class FolderActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String TAG = "FILE";

    private ImageView imBack;
    private TextView tvFolderName;
    private ImageView imSearch;
    private ImageView imMore;

    private LinearLayout layoutReScan;
    private RelativeLayout layoutSearch;
    private EditText edtSearch;
    private ImageView imCancelSearch;

    private RecyclerView recyclerView;
    private LinearLayout layoutNoData;

    private ArrayList<MyFile> fileList = new ArrayList<>();
    private ArrayList<MyFile> adapterList = new ArrayList<>();
    private ListFileAdapter adapter;
    private ArrayList<String> arrPaths = new ArrayList<>();

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        imBack = (ImageView) findViewById(R.id.im_back);
        tvFolderName = (TextView) findViewById(R.id.tv_folder_name);
        tvFolderName.setText(getIntent().getExtras().getString(MainActivity.FOLDER_NAME));
        imSearch = (ImageView) findViewById(R.id.im_search);
        imMore = (ImageView) findViewById(R.id.im_more);
        layoutSearch = (RelativeLayout) findViewById(R.id.layout_search);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        imCancelSearch = (ImageView) findViewById(R.id.im_cancel_search);

        layoutReScan = (LinearLayout) findViewById(R.id.layout_rescan);
        layoutNoData = (LinearLayout) findViewById(R.id.layout_no_data_folder);
        recyclerView = (RecyclerView) findViewById(R.id.rv_folder);

        initRecyclerView();
        if (MyHelper.checkPermission(PERMISSIONS, this) == PackageManager.PERMISSION_GRANTED) {
            MyAsync async = new MyAsync();
            async.execute();
        }

        initAction();

        IntentFilter filter = new IntentFilter("com.load.file");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case "com.load.file":
                        MyAsync async = new MyAsync();
                        async.execute();
                        break;
                    default:
                        break;
                }
            }
        };
        registerReceiver(mReceiver, filter);
        final RelativeLayout layoutAds = (RelativeLayout) findViewById(R.id.layout_ads);
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

    private void initRecyclerView() {
        adapter = new ListFileAdapter(adapterList, this, new FileViewHolder.FileListener() {
            @Override
            public void onFileResult(MyFile file) {
                Bundle animation = ActivityOptions.makeCustomAnimation(FolderActivity.this, R.anim.slide_in_left, R.anim.slide_in_right).toBundle();
                Intent intent = new Intent(FolderActivity.this, ViewFileActivity.class);
                File file1 = new File(file.getPath());
                intent.putExtra(TAG, file1);
                startActivity(intent,animation);
            }

            @Override
            public void onMoreInfo(MyFile file) {
                File file1 = new File(file.getPath());
                InformationDialog informationDialog = new InformationDialog(FolderActivity.this, file1);
                informationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                informationDialog.show();
                Ads.f(FolderActivity.this);
            }

            @Override
            public void shareFile(MyFile file) {
                try {
                    File file1 = new File(file.getPath());
                    Uri contentUri = FileProvider.getUriForFile(FolderActivity.this,
                            "com.pdf.reader.pdfviewer.provider", file1);
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("application/pdf");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share_with)));
                } catch (Exception e) {
                    Toast.makeText(FolderActivity.this, getString(R.string.cannot_share), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void deleteFile(final MyFile myFile) {
                final File file1 = new File(myFile.getPath());
                DeleteDialog deleteDialog = new DeleteDialog(FolderActivity.this, file1, new DeleteDialog.DeleteListener() {
                    @Override
                    public void onDelete(File file) {
                        if (file1.delete()) {
                            fileList.remove(myFile);
                            adapterList.remove(myFile);
                            adapter.notifyDataSetChanged();
                            sendBroadcast(new Intent("com.load.file"));
                            Toast.makeText(FolderActivity.this, getString(R.string.file_delete), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FolderActivity.this, getString(R.string.cannot_delete), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deleteDialog.show();
            }
        });
        boolean isSwitched = adapter.toggleItemViewType();
        recyclerView.setLayoutManager(isSwitched ? new LinearLayoutManager(FolderActivity.this) : new GridLayoutManager(FolderActivity.this, 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void initAction() {
        imSearch.setOnClickListener(this);
        imCancelSearch.setOnClickListener(this);
        layoutReScan.setOnClickListener(this);
        imBack.setOnClickListener(this);
        imMore.setOnClickListener(this);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("StaticFieldLeak")
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    new AsyncTask<Void, Void, ArrayList<MyFile>>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            adapterList.clear();
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        protected ArrayList<MyFile> doInBackground(Void... voids) {
                            ArrayList<MyFile> searchList = new ArrayList<>();
                            for (int i = 0; i < fileList.size(); i++) {
                                if (fileList.get(i).getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                    searchList.add(fileList.get(i));
                                }
                            }
                            return searchList;
                        }

                        @Override
                        protected void onPostExecute(final ArrayList<MyFile> files) {
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

            case R.id.im_back:
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                break;

            case R.id.im_more:
                PopupMenu popup = new PopupMenu(FolderActivity.this, imMore);
                popup.getMenuInflater().inflate(R.menu.folder_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.switch_view) {
                            boolean isSwitched = adapter.toggleItemViewType();
                            recyclerView.setLayoutManager(isSwitched ? new LinearLayoutManager(FolderActivity.this) : new GridLayoutManager(FolderActivity.this, 2));
                            adapter.notifyDataSetChanged();
                        }
                        return true;
                    }
                });
                popup.show();
                break;

            case R.id.layout_rescan:
                new MyAsync().execute();
                break;

            case R.id.im_cancel_search:
                edtSearch.setText("");
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    private class MyAsync extends AsyncTask<Void, Void, ArrayList<MyFile>> {
        LoadingDialog loadingDialog;

        @Override
        protected void onPreExecute() {
            try {
                loadingDialog = new LoadingDialog(FolderActivity.this);
                loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                loadingDialog.setCancelable(false);
                loadingDialog.setCanceledOnTouchOutside(false);
                loadingDialog.show();
                fileList.clear();
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPreExecute();
        }

        @Override
        protected ArrayList<MyFile> doInBackground(Void... voids) {
            arrPaths = getIntent().getExtras().getStringArrayList(MainActivity.FOLDER_TAG);
            if (arrPaths == null || arrPaths.size() == 0) {
                return new ArrayList<MyFile>();
            }
            ArrayList<MyFile> arrayList = new ArrayList<>();
            for (int i = 0; i < arrPaths.size(); i++) {
                File file = new File(arrPaths.get(i));
                Bitmap bitmap = generateImageFromPdf(Uri.fromFile(file));
                String parent = file.getParent();
                if (bitmap != null) {
                    MyFile myFile = new MyFile(file.getName(), arrPaths.get(i), parent, bitmap);
                    arrayList.add(myFile);
                }
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(final ArrayList<MyFile> data) {
            super.onPostExecute(data);
            if (data == null || data.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                layoutNoData.setVisibility(View.VISIBLE);
            } else {
                layoutNoData.setVisibility(View.GONE);
                fileList.addAll(data);
                adapterList.addAll(fileList);
                adapter.notifyDataSetChanged();
            }
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutSearch.getVisibility() != View.VISIBLE) {
            super.onBackPressed();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        } else {
            edtSearch.setText("");
            layoutSearch.setVisibility(View.GONE);
        }
    }
}
