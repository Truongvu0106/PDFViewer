package com.pdf.reader.pdfviewer;

import android.app.Application;

import com.zer.android.ZAndroidSDK;

/**
 * Created by binhn on 11/6/2017.
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        ZAndroidSDK.initApplication(this, getApplicationContext().getPackageName());
    }
}

