package edu.hust.truongvu.pdfviewer.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import java.io.File;
import java.util.ArrayList;

import edu.hust.truongvu.pdfviewer.adapter.ListFileAdapter;
import edu.hust.truongvu.pdfviewer.customview.DeleteDialog;
import edu.hust.truongvu.pdfviewer.entity.MyFile;
import edu.hust.truongvu.pdfviewer.R;
import edu.hust.truongvu.pdfviewer.customview.InfomationDialog;
import edu.hust.truongvu.pdfviewer.customview.MyActionBar;
import edu.hust.truongvu.pdfviewer.entity.MyFolder;
import edu.hust.truongvu.pdfviewer.helper.MyHelper;
import edu.hust.truongvu.pdfviewer.holder.FileViewHolder;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS  = 1;
    public static final String TAG = "FILE";
    RecyclerView recyclerView;
    View nodata;
    ActionBar actionBar;
    ListFileAdapter adapter;
    ActionBar.LayoutParams params;
    boolean boolean_permission;
    MyFolder mFolder;
    ArrayList<String> arrPaths = new ArrayList<>();
//    ArrayList<MyFile> mListFile = new ArrayList<>();
//    ArrayList<MyFile> mListSearch = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mFolder = getIntent().getExtras().getParcelable(StartActivity.FOLDER_TAG);
//        mFolder = StartActivity.folderTransfer;
//        if (mFolder == null){
//            onBackPressed();
//        }
//        mListFile = getIntent().getExtras().getParcelableArrayList(StartActivity.FOLDER_TAG);
////        mListFile = StartActivity.allPDF;
//        mListSearch = getIntent().getExtras().getParcelableArrayList(StartActivity.FOLDER_TAG);
        nodata = findViewById(R.id.nodata_main);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        actionBar = getSupportActionBar();
        params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        MyAsync async = new MyAsync();
        async.execute();

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
        } catch(Exception e) {
            Log.e("Generate Image False", e.toString());
            return null;
        }
    }

    private class MyAsync extends AsyncTask<Void, Void, ArrayList<MyFile>> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MainActivity.this, getString(R.string.dialog), getString(R.string.scanning_file));
        }

        @Override
        protected ArrayList<MyFile> doInBackground(Void... voids) {
            arrPaths = getIntent().getExtras().getStringArrayList(StartActivity.FOLDER_TAG);
            if (arrPaths == null || arrPaths.size() == 0){
                return new ArrayList<MyFile>();
            }
            ArrayList<MyFile> arrayList = new ArrayList<>();
            for (String s : arrPaths){
                File file = new File(s);
                Bitmap bitmap = generateImageFromPdf(Uri.fromFile(file));
                String parent = file.getParent();
                if (bitmap != null){
                    MyFile myFile = new MyFile(file.getName(), s, parent, bitmap);
                    arrayList.add(myFile);
                }

            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(final ArrayList<MyFile> data) {
            super.onPostExecute(data);
            if (data == null || data.size() == 0){
                recyclerView.setVisibility(View.GONE);
                nodata.setVisibility(View.VISIBLE);
            }else {
//                ArrayList<MyFile> mListFile = data;
//                ArrayList <MyFile> mListSearch = data;
                String title = MyHelper.getParentDirectoryName(data.get(0).getParent());
//            ArrayList<MyFile> mListFile = data.getListFile();
//            ArrayList <MyFile> mListSearch = data.getListFile();
                MyActionBar myActionBar = new MyActionBar(MainActivity.this, MyActionBar.FLAG_MAIN, data, new MyActionBar.ActionBarListener() {
                    @Override
                    public void onSwitchMain() {
                        supportInvalidateOptionsMenu();
                        boolean isSwitched = adapter.toggleItemViewType();
                        recyclerView.setLayoutManager(isSwitched ? new LinearLayoutManager(MainActivity.this) : new GridLayoutManager(MainActivity.this, 2));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onSwitchViewFile() {

                    }

                    @Override
                    public void onJumpViewFile() {

                    }

                    @Override
                    public void onSelectItemSearch(MyFile file) {
                        Intent intent = new Intent(MainActivity.this, ViewFileActivity.class);
                        File file1 = new File(file.getPath());
                        intent.putExtra(TAG, file1);
                        startActivity(intent);
                    }
                });
                actionBar.setCustomView(myActionBar.getView(), params);
                myActionBar.setTitle(title);

                adapter = new ListFileAdapter(data, MainActivity.this, new FileViewHolder.FileListener() {
                    @Override
                    public void onFileResult(MyFile myFile) {
                        Intent intent = new Intent(MainActivity.this, ViewFileActivity.class);
                        File file = new File(myFile.getPath());
                        intent.putExtra(TAG, file);
                        startActivity(intent);
                    }

                    @Override
                    public void onMoreInfo(MyFile myFile) {
                        File file = new File(myFile.getPath());
                        new InfomationDialog(MainActivity.this, file);
                    }

                    @Override
                    public void shareFile(MyFile file) {
                        try{
                            File file1 = new File(file.getPath());
                            Uri contentUri = FileProvider.getUriForFile(MainActivity.this,
                                    "edu.hust.truongvu.pdfviewer.provider", file1);
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("application/pdf");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(shareIntent, "Share with"));
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, getString(R.string.cannot_share), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void deleteFile(final MyFile myfile) {
                        final File file1 = new File(myfile.getPath());
                        new DeleteDialog(MainActivity.this, file1, new DeleteDialog.DeleteListener() {
                            @Override
                            public void onDelete(File file) {
                                if (file1.delete()){
                                    Toast.makeText(MainActivity.this, getString(R.string.file_delete), Toast.LENGTH_SHORT).show();
                                    data.remove(myfile);
                                    adapter.notifyDataSetChanged();
                                }else {
                                    Toast.makeText(MainActivity.this, getString(R.string.cannot_delete), Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setAdapter(adapter);
            }
            dialog.dismiss();
        }
    }
}
