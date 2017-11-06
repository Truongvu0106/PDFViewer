package com.pdf.reader.pdfviewer.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by binhn on 11/6/2017.
 */

@SuppressLint("AppCompatCustomView")
public class SplashTextView2 extends TextView {
    public SplashTextView2(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public SplashTextView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public SplashTextView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("font_eight.TTF", context);
        setTypeface(customFont);
    }
}
