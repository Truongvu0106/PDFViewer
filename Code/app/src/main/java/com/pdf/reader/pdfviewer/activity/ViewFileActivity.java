package com.pdf.reader.pdfviewer.activity;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;

import com.pdf.reader.pdfviewer.helper.MyHelper;
import com.pdf.reader.pdfviewer.entity.MyFile;
import com.pdf.reader.pdfviewer.R;
import com.pdf.reader.pdfviewer.dialogs.JumpDialog;
import com.pdf.reader.pdfviewer.customview.MyActionBar;

public class ViewFileActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    private PDFView pdfView;
    private MyActionBar myActionBar;
    private Integer pageNumber = 0;
    private static int totalPage;
    private static boolean isHorizontal = false;
    private File file;
    private String pdfFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_file);

        init();
//        ActionBar actionBar = getSupportActionBar();
//        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
//                ActionBar.LayoutParams.MATCH_PARENT,
//                ActionBar.LayoutParams.MATCH_PARENT);
//        actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
//        myActionBar = new MyActionBar(this, MyActionBar.FLAG_VIEW_FILE, null, new MyActionBar.ActionBarListener() {
//            @Override
//            public void onSwitchMain() {
//
//            }
//
//            @Override
//            public void onSwitchViewFile() {
//                isHorizontal = !isHorizontal;
//                displayFromSdcard(isHorizontal);
//            }
//
//            @Override
//            public void onJumpViewFile() {
//                new JumpDialog(ViewFileActivity.this, new JumpDialog.JumpListener() {
//                    @Override
//                    public void onJump(int numPage) {
//                        if (numPage > totalPage) {
//                            Toast.makeText(ViewFileActivity.this, "No page like in this document", Toast.LENGTH_SHORT).show();
//                        } else {
//                            pdfView.jumpTo(numPage - 1, true);
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onSelectItemSearch(MyFile file) {
//
//            }
//
//        });
//        actionBar.setCustomView(myActionBar.getView(), params);
    }

    private void init() {
        pdfView = (PDFView) findViewById(R.id.pdf_view);
        file = (File) getIntent().getExtras().get(MainActivity.TAG);
        pdfFileName = file.getName();
        displayFromSdcard(isHorizontal);
    }

    private void displayFromSdcard(boolean flag) {
        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(flag)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    private void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e("abc", String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        totalPage = pageCount;
//        myActionBar.setTitle(String.format("%s %s / %s", MyHelper.formatTitle(pdfFileName, 10), page + 1, pageCount));
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.view_file_menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id){
//            case R.id.jump:
//                new JumpDialog(ViewFileActivity.this, new JumpDialog.JumpListener() {
//                    @Override
//                    public void onJump(int numPage) {
//                        if (numPage > totalPage){
//                            Toast.makeText(ViewFileActivity.this, "No page like in this document", Toast.LENGTH_SHORT).show();
//                        }else {
//                            pdfView.jumpTo(numPage - 1, true);
//                        }
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//                });
//                break;
//            case R.id.switch_view_type:
//                isHorizontal = !isHorizontal;
//                displayFromSdcard(isHorizontal);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
