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
import android.os.Parcelable;
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

import edu.hust.truongvu.pdfviewer.R;
import edu.hust.truongvu.pdfviewer.adapter.ListFileAdapter;
import edu.hust.truongvu.pdfviewer.adapter.MultitypeAdapter;
import edu.hust.truongvu.pdfviewer.customview.DeleteDialog;
import edu.hust.truongvu.pdfviewer.customview.InfomationDialog;
import edu.hust.truongvu.pdfviewer.customview.MyActionBar;
import edu.hust.truongvu.pdfviewer.entity.MyFile;
import edu.hust.truongvu.pdfviewer.entity.MyFolder;
import edu.hust.truongvu.pdfviewer.helper.MyHelper;
import edu.hust.truongvu.pdfviewer.holder.FileViewHolder;
import edu.hust.truongvu.pdfviewer.holder.FolderViewHolder;

public class StartActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS  = 1;
    MyAsync myAsync;
    MultitypeAdapter adapter;
    RecyclerView recyclerView;
    View nodataView;
    ActionBar actionBar;
    ActionBar.LayoutParams params;
    public static final String TAG = "FILE";
    public static final String FOLDER_TAG = "FOLDER";
    public static ArrayList<MyFile> allPDF;
    public static MyFolder folderTransfer;
    public static String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        recyclerView = (RecyclerView) findViewById(R.id.list_multiple_view);
        nodataView = findViewById(R.id.nodata);
        init();
        actionBar = getSupportActionBar();
        params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void init() {
        checkAndroidVersion();
    }

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (MyHelper.checkPermission(PERMISSIONS, StartActivity.this) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            }
        }else {
            myAsync = new MyAsync();
            myAsync.execute();
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(StartActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(StartActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (StartActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (StartActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(StartActivity.this, "Please allow permission", Toast.LENGTH_SHORT).show();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission
                                .READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            myAsync = new MyAsync();
            myAsync.execute();
        }
    }

    public ArrayList<MyFile> getAllPDF(){
        ArrayList<MyFile> results = new ArrayList<>();
        ContentResolver cr = StartActivity.this.getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = null;
        String sortOrder = null;
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String[] selectionArgsPdf = new String[]{ mimeType };
        Cursor allPdfFiles = cr.query(uri, projection, selectionMimeType, selectionArgsPdf, sortOrder);
        if (allPdfFiles.moveToFirst()) {
            do {
                String path = allPdfFiles.getString(1);
                Log.d("Path pdf", path);
                File file = new File(path);
                Bitmap bitmap = generateImageFromPdf(Uri.fromFile(file));
                String name = allPdfFiles.getString(8);
                String parent = MyHelper.getParentDirectoryName(file.getParent());
                if (bitmap != null){
                    MyFile myFile = new MyFile(name, path, parent, bitmap);
                    Log.v("Parent", myFile.getParent());
                    results.add(myFile);
                }
            } while (allPdfFiles.moveToNext());
        }
        return results;
    }

    public ArrayList<MyFolder> filterFolder(ArrayList<MyFile> list){
        ArrayList<MyFolder> listFolder = new ArrayList<>();
        for (MyFile file : list){
            String parentName = file.getParent();
            if (!parentName.matches("0")){
                ArrayList<MyFile> listFileChild = getFileByParent(parentName, list);
                MyFolder folder = new MyFolder(parentName, listFileChild);
                if (isFolderNotExist(listFolder, folder)){
                    listFolder.add(folder);
                }
            }
        }
        return listFolder;
    }

    public boolean isFolderNotExist(ArrayList<MyFolder> list, MyFolder newItem){
        boolean flag = true;
        if (list == null || list.size() == 0){
            flag = true;
        }else {
            for (MyFolder folder : list){
                if (newItem.getName().matches(folder.getName())){
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    public ArrayList<MyFile> filterFile(ArrayList<MyFile> list){
        ArrayList<MyFile> listFile = new ArrayList<>();
        for (MyFile file : list){
            if (file.getParent().matches("0")){
                listFile.add(file);
            }
        }
        return listFile;
    }

    public ArrayList<MyFile> getFileByParent(String parentName, ArrayList<MyFile> files){
        ArrayList<MyFile> arrayList = new ArrayList<>();
        for (MyFile file : files){
            if (file.getParent().matches(parentName)){
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
        } catch(Exception e) {
            Log.e("Generate Image False", e.toString());
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (MyHelper.checkPermission(PERMISSIONS, StartActivity.this) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
                }else {
                    myAsync = new MyAsync();
                    myAsync.execute();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }




    private class MyAsync extends AsyncTask<Void, Void, ArrayList<Object>> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(StartActivity.this, "Dialog", getString(R.string.scanning_file));
        }

        @Override
        protected ArrayList<Object> doInBackground(Void... voids) {
            ArrayList<Object> listObject = new ArrayList<>();
            allPDF = getAllPDF();
            ArrayList<MyFile> filesOnly = filterFile(allPDF);
            ArrayList<MyFolder> folders = filterFolder(allPDF);


            for (MyFolder folder : folders){
                listObject.add(folder);
            }

            for (MyFile file : filesOnly){
                listObject.add(file);
            }
            return listObject;
        }


        @Override
        protected void onPostExecute(final ArrayList<Object> data) {
            super.onPostExecute(data);
            if (data == null || data.size() == 0){
                dialog.dismiss();
                nodataView.setVisibility(View.VISIBLE);
                return;
            }
            nodataView.setVisibility(View.GONE);
            FileViewHolder.FileListener fileListener = new FileViewHolder.FileListener() {
                @Override
                public void onFileResult(MyFile file) {
                    Intent intent = new Intent(StartActivity.this, ViewFileActivity.class);
                    File file1 = new File(file.getPath());
                    intent.putExtra(TAG, file1);
                    startActivity(intent);
                }

                @Override
                public void onMoreInfo(MyFile file) {
                    File file1 = new File(file.getPath());
                    new InfomationDialog(StartActivity.this, file1);
                }

                @Override
                public void shareFile(MyFile file) {
//                    Log.d("Share")
                    try{
                        File file1 = new File(file.getPath());
                        Uri contentUri = FileProvider.getUriForFile(StartActivity.this,
                                "edu.hust.truongvu.pdfviewer.provider", file1);
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setType("application/pdf");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(shareIntent, "Share with"));
                    }catch (Exception e){
                        Toast.makeText(StartActivity.this, getString(R.string.cannot_share), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }

                @Override
                public void deleteFile(final MyFile myfile) {
                    final File file1 = new File(myfile.getPath());
                    new DeleteDialog(StartActivity.this, file1, new DeleteDialog.DeleteListener() {
                        @Override
                        public void onDelete(File file) {
                            if (file1.delete()){
                                Toast.makeText(StartActivity.this, getString(R.string.file_delete), Toast.LENGTH_SHORT).show();
                                data.remove(myfile);
                                adapter.notifyDataSetChanged();
                            }else {
                                Toast.makeText(StartActivity.this, getString(R.string.cannot_delete), Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                }
            };

            FolderViewHolder.FolderListener folderListener = new FolderViewHolder.FolderListener() {
                @Override
                public void onFolderResult(MyFolder folder) {
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
//                    folderTransfer = folder;
                    ArrayList<MyFile> myFiles = folder.getListFile();
                    ArrayList<String> path = new ArrayList<>();
                    for (MyFile file : myFiles){
                        String s = file.getPath();
                        path.add(s);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(FOLDER_TAG, path);
                    intent.putExtras(bundle);
//                    folderTransfer = folder;
                    startActivity(intent);
                }
            };
            adapter = new MultitypeAdapter(data, StartActivity.this, fileListener, folderListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(StartActivity.this));
            recyclerView.setAdapter(adapter);
            actionBar.setCustomView(new MyActionBar(StartActivity.this, MyActionBar.FLAG_START, allPDF, new MyActionBar.ActionBarListener() {
                @Override
                public void onSwitchMain() {
                    supportInvalidateOptionsMenu();
                    boolean isSwitched = adapter.toggleItemViewType();
                    recyclerView.setLayoutManager(isSwitched ? new LinearLayoutManager(StartActivity.this) : new GridLayoutManager(StartActivity.this, 2));
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
                    Intent intent = new Intent(StartActivity.this, ViewFileActivity.class);
                    File file1 = new File(file.getPath());
                    intent.putExtra(TAG, file1);
                    startActivity(intent);
                }

            }).getView(), params);


            dialog.dismiss();
        }
    }


}
