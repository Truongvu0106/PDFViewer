package edu.hust.truongvu.pdfviewer.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by truon on 10/20/2017.
 */

public class MyHelper {
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
