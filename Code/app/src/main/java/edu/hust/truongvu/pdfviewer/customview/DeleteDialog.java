package edu.hust.truongvu.pdfviewer.customview;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import edu.hust.truongvu.pdfviewer.R;

/**
 * Created by truon on 11/1/2017.
 */

public class DeleteDialog extends Dialog {
    public interface DeleteListener{
        public void onDelete(File file);
    }
    private DeleteListener listener;
    Button cancel, delete;
    public DeleteDialog(@NonNull final Context context, final File file, final DeleteListener listener) {
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.delete_dialog_view);
        this.listener = listener;
        cancel = findViewById(R.id.cancel_delete);
        delete = findViewById(R.id.ok_delete);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                dismiss();
                listener.onDelete(file);
            }
        });

        show();
    }
}
