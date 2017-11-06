package com.pdf.reader.pdfviewer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import com.pdf.reader.pdfviewer.R;

/**
 * Created by truon on 11/1/2017.
 */

public class DeleteDialog extends AlertDialog {
    public interface DeleteListener {
        void onDelete(File file);
    }

    private DeleteListener listener;
    private TextView cancel, delete;

    private File file;

    public DeleteDialog(@NonNull final Context context, final File file, final DeleteListener listener) {
        super(context);
        this.file = file;
        this.listener = listener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_dialog_view);

        cancel = (TextView) findViewById(R.id.cancel_delete);
        delete = (TextView) findViewById(R.id.ok_delete);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                listener.onDelete(file);
            }
        });
    }
}
