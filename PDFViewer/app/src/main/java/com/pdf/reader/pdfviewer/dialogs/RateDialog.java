package com.pdf.reader.pdfviewer.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.pdf.reader.pdfviewer.R;
import com.pdf.reader.pdfviewer.interfaces.RateInterface;

/**
 * Created by binhn on 11/7/2017.
 */

public class RateDialog extends AlertDialog {
    private RateInterface rateInterface;

    public RateDialog(@NonNull Context context, RateInterface rateInterface) {
        super(context);
        this.rateInterface = rateInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rate);

        (findViewById(R.id.tv_later)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                rateInterface.cancelClicked();
            }
        });
        (findViewById(R.id.tv_rate_now)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                rateInterface.rateClicked();
            }
        });
    }
}
