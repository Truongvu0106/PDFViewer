package com.pdf.reader.pdfviewer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pdf.reader.pdfviewer.R;

/**
 * Created by truon on 10/20/2017.
 */

public class JumpDialog extends Dialog {
    private Context context;
    private JumpListener listener;

    private TextView btnJump, btnCancel;
    private EditText editText;

    public interface JumpListener {
        void onJump(int numPage);
    }

    public JumpDialog(@NonNull Context context, JumpListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_jump_page);
        btnJump = (TextView)findViewById(R.id.btn_jump);
        btnCancel = (TextView)findViewById(R.id.btn_cancel);
        editText = (EditText) findViewById(R.id.edt_page_number);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numPage = editText.getText().toString();
                if (numPage == null || numPage.matches("")) {
                    Toast.makeText(context, context.getString(R.string.please_enter_number), Toast.LENGTH_SHORT).show();
                } else {
                    dismiss();
                    int num = Integer.parseInt(numPage);
                    listener.onJump(num);
                }
            }
        });
    }
}
