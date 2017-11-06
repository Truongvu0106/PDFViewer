package com.pdf.reader.pdfviewer.activity;

import android.Manifest;
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
import android.os.ParcelFileDescriptor;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.util.Util;
import com.pdf.reader.pdfviewer.dialogs.LoadingDialog;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.util.ArrayList;

import com.pdf.reader.pdfviewer.adapter.ListFileAdapter;
import com.pdf.reader.pdfviewer.dialogs.DeleteDialog;
import com.pdf.reader.pdfviewer.entity.MyFile;
import com.pdf.reader.pdfviewer.R;
import com.pdf.reader.pdfviewer.dialogs.InfomationDialog;
import com.pdf.reader.pdfviewer.customview.MyActionBar;
import com.pdf.reader.pdfviewer.entity.MyFolder;
import com.pdf.reader.pdfviewer.helper.MyHelper;
import com.pdf.reader.pdfviewer.holder.FileViewHolder;

public class MainActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSIONS = 1;

    public static final String TAG = "FILE";
    private RecyclerView recyclerView;
    private LinearLayout nodata;

//    private ActionBar actionBar;
//    private ActionBar.LayoutParams params;

    private ListFileAdapter adapter;
    private ArrayList<String> arrPaths = new ArrayList<>();

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nodata = (LinearLayout) findViewById(R.id.nodata_main);
        recyclerView = (RecyclerView) findViewById(R.id.list);

//        actionBar = getSupportActionBar();
//        params = new ActionBar.LayoutParams(
//                ActionBar.LayoutParams.MATCH_PARENT,
//                ActionBar.LayoutParams.MATCH_PARENT);
//        actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);

        if (MyHelper.checkPermission(PERMISSIONS, this) == PackageManager.PERMISSION_GRANTED) {
            MyAsync async = new MyAsync();
            async.execute();
        }

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

    private class MyAsync extends AsyncTask<Void, Void, ArrayList<MyFile>> {
        LoadingDialog loadingDialog;

        @Override
        protected void onPreExecute() {
            try {
                loadingDialog = new LoadingDialog(MainActivity.this);
                loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                loadingDialog.setCancelable(false);
                loadingDialog.setCanceledOnTouchOutside(false);
                loadingDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPreExecute();
        }

        @Override
        protected ArrayList<MyFile> doInBackground(Void... voids) {
            arrPaths = getIntent().getExtras().getStringArrayList(StartActivity.FOLDER_TAG);
            if (arrPaths == null || arrPaths.size() == 0) {
                return new ArrayList<MyFile>();
            }
            ArrayList<MyFile> arrayList = new ArrayList<>();
            for (String s : arrPaths) {
                File file = new File(s);
                Bitmap bitmap = generateImageFromPdf(Uri.fromFile(file));
                String parent = file.getParent();
                if (bitmap != null) {
                    MyFile myFile = new MyFile(file.getName(), s, parent, bitmap);
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
                nodata.setVisibility(View.VISIBLE);
            } else {
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
//                actionBar.setCustomView(myActionBar.getView(), params);
//                myActionBar.setTitle(title);
            }

            loadingDialog.dismiss();
        }
    }
}
