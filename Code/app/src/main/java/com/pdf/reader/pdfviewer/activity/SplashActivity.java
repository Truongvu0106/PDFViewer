package com.pdf.reader.pdfviewer.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.pdf.reader.pdfviewer.R;
import com.pdf.reader.pdfviewer.dialogs.PermissionDialog;
import com.pdf.reader.pdfviewer.helper.MyHelper;
import com.pdf.reader.pdfviewer.interfaces.PermissionInterface;

/**
 * Created by binhn on 11/6/2017.
 */

public class SplashActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSIONS = 1;

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (MyHelper.checkPermission(PERMISSIONS, this) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            }
        }

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(mReceiver, new IntentFilter("com.finish.splash"));
    }

    @Override
    public void onBackPressed() {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (MyHelper.checkPermission(PERMISSIONS, this) != PackageManager.PERMISSION_GRANTED) {
                        PermissionDialog dialog = new PermissionDialog(SplashActivity.this, new PermissionInterface() {
                            @Override
                            public void cancelClicked() {
                                finish();
                                sendBroadcast(new Intent("com.finish.app"));
                            }

                            @Override
                            public void okClicked() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
                                }
                            }
                        });
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    } else {
                        sendBroadcast(new Intent("com.load.file"));
                    }
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }
}
