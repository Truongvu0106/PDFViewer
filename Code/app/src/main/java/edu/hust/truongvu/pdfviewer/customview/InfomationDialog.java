package edu.hust.truongvu.pdfviewer.customview;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.hust.truongvu.pdfviewer.R;

/**
 * Created by truon on 10/20/2017.
 */

public class InfomationDialog extends Dialog {
    TextView tvName, tvSize;
    Button btnOk;
    public InfomationDialog(@NonNull Context context, File file) {
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.info_dialog_view);
        tvName = findViewById(R.id.file_name);
        tvSize = findViewById(R.id.file_size);
        btnOk = findViewById(R.id.btn_Ok);

        double size = (double) file.length()/(1024*1024);
        Log.d("Size", size + "");
        NumberFormat formatter = new DecimalFormat("#0.00");
        String sizeText =formatter.format(size);

        tvName.setText(file.getName());
        tvSize.setText(sizeText + " MB");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        show();
    }
}
