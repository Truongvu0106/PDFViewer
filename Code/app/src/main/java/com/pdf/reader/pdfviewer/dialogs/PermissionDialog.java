package com.pdf.reader.pdfviewer.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.pdf.reader.pdfviewer.R;
import com.pdf.reader.pdfviewer.interfaces.PermissionInterface;

/**
 * Created by binhn on 11/6/2017.
 */

public class PermissionDialog extends AlertDialog {
    private PermissionInterface permissionInterface;

    public PermissionDialog(@NonNull Context context, PermissionInterface permissionInterface) {
        super(context);
        this.permissionInterface = permissionInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_permission);

        (findViewById(R.id.tv_permission_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                permissionInterface.cancelClicked();
            }
        });
        (findViewById(R.id.tv_permission_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                permissionInterface.okClicked();
            }
        });
    }
}
