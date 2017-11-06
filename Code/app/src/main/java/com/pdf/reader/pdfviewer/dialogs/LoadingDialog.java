package com.pdf.reader.pdfviewer.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.pdf.reader.pdfviewer.R;

/**
 * Created by binhn on 11/6/2017.
 */

public class LoadingDialog extends AlertDialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
    }
}
