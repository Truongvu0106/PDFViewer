package com.pdf.reader.pdfviewer.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.pdf.reader.pdfviewer.R;

/**
 * Created by truon on 10/20/2017.
 */

public class InfomationDialog extends AlertDialog {
    private TextView tvName, tvPath, tvSize;
    private TextView btnOk;

    private File file;


    public InfomationDialog(@NonNull Context context, File file) {
        super(context);
        this.file = file;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_info);
        tvName = (TextView) findViewById(R.id.tv_file_name);
        tvPath = (TextView) findViewById(R.id.tv_file_path);
        tvSize = (TextView) findViewById(R.id.tv_file_size);
        btnOk = (TextView) findViewById(R.id.tv_ok);

        double size = (double) file.length() / (1024 * 1024);
        Log.d("Size", size + "");
        NumberFormat formatter = new DecimalFormat("#0.00");
        String sizeText = formatter.format(size);

        tvName.setText(file.getName());
        tvPath.setText(file.getPath());
        tvSize.setText(sizeText + " MB");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
