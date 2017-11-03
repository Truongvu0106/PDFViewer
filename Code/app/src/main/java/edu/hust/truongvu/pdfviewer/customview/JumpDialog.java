package edu.hust.truongvu.pdfviewer.customview;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import edu.hust.truongvu.pdfviewer.R;

/**
 * Created by truon on 10/20/2017.
 */

public class JumpDialog extends Dialog {

    private JumpListener listener;
    private View btnJump, btnCancel;
    private EditText editText;
    public interface JumpListener{
        public void onJump(int numPage);
        public void onCancel();
    }
    public JumpDialog(@NonNull final Context context, final JumpListener listener) {
        super(context);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.jump_dialog_view);
        btnJump = this.findViewById(R.id.btn_jump);
        btnCancel = this.findViewById(R.id.btn_cancel);
        editText = this.findViewById(R.id.edt_page_number);
        this.listener = listener;

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
                if (numPage == null || numPage.matches("")){
                    Toast.makeText(context, context.getString(R.string.please_enter_number), Toast.LENGTH_SHORT).show();
                }else {
                    dismiss();
                    int num = Integer.parseInt(numPage);
                    listener.onJump(num);
                }
            }
        });

        show();
    }
}
