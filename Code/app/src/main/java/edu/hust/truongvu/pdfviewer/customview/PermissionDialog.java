package edu.hust.truongvu.pdfviewer.customview;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import edu.hust.truongvu.pdfviewer.R;

/**
 * Created by truon on 11/3/2017.
 */

public class PermissionDialog extends Dialog {
    Button btnAccept;
    Button btnCancel;
    public interface PermissionListenner{
        public void onDeny();
        public void onAccept();
    }

    private PermissionListenner listenner;
    public PermissionDialog(@NonNull Context context, final PermissionListenner listenner) {
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.permission_dialog);
        this.listenner = listenner;
        btnAccept = findViewById(R.id.accept);
        btnCancel = findViewById(R.id.still_cancel);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenner.onAccept();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenner.onDeny();
            }
        });

        show();

    }
}
