package com.pdf.reader.pdfviewer.activity;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.pdf.reader.pdfviewer.R;
import com.pdf.reader.pdfviewer.dialogs.JumpDialog;
import com.pdf.reader.pdfviewer.helper.Ads;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;

public class ViewFileActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener, View.OnClickListener {
    private TextView tvFileName;
    private ImageView imBack, imjump, imSwitchView;

    private PDFView pdfView;
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

    private void init() {
        tvFileName = (TextView) findViewById(R.id.tv_file_name);
        imBack = (ImageView) findViewById(R.id.im_back);
        imjump = (ImageView) findViewById(R.id.im_jump);
        imSwitchView = (ImageView) findViewById(R.id.im_switch_view);

        pdfView = (PDFView) findViewById(R.id.pdf_view);
        file = (File) getIntent().getExtras().get(FolderActivity.TAG);
        pdfFileName = file.getName();
        tvFileName.setText(pdfFileName);
        displayFromSdcard(isHorizontal);

        imBack.setOnClickListener(this);
        imjump.setOnClickListener(this);
        imSwitchView.setOnClickListener(this);
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
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_back:
                finish();overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                break;

            case R.id.im_jump:
                JumpDialog jumpDialog = new JumpDialog(ViewFileActivity.this, new JumpDialog.JumpListener() {
                    @Override
                    public void onJump(int numPage) {
                        if (numPage > totalPage) {
                            Toast.makeText(ViewFileActivity.this, getString(R.string.no_page_like), Toast.LENGTH_SHORT).show();
                        } else {
                            pdfView.jumpTo(numPage - 1, true);
                        }
                    }
                });
                jumpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                jumpDialog.show();
                break;

            case R.id.im_switch_view:
                isHorizontal = !isHorizontal;
                displayFromSdcard(isHorizontal);
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
