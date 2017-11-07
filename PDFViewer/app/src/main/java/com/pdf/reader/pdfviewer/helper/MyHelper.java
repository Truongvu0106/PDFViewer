package com.pdf.reader.pdfviewer.helper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

/**
 * Created by truon on 10/20/2017.
 */

public class MyHelper {
    /**
     * link to app on CH Play
     *
     * @param context
     */
    public static void rateApp(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static String formatTitle(String name, int numberFormat){
        String formatedName = "";
        if (name.length() < (numberFormat + 1)){
            formatedName = name;
        }else {
            formatedName = name.substring(0, numberFormat) + "...";
        }
        return formatedName;
    }

    public static String getParentDirectoryName(String path){
        String read = "";
        int i = path.length() - 1;
        while (path.charAt(i) != '/'){
            read = read + path.charAt(i);
            i--;
        }
        return reverseString(read);
    }

    private static String reverseString(String s){
        char[] arr = s.toCharArray();
        int begin = 0;
        int end = s.length() - 1;
        char temp;
        while (begin < end) {
            temp = arr[begin];
            arr[begin] = arr[end];
            arr[end] = temp;
            begin++;
            end--;
        }
        String result  = String.valueOf(arr);
        return result;
    }

    public static int checkPermission(String[] permissions, Context context) {
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (String permission : permissions) {
            permissionCheck += ContextCompat.checkSelfPermission(context, permission);
        }
        return permissionCheck;
    }
}
