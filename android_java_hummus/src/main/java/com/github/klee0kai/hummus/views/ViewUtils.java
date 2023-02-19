package com.github.klee0kai.hummus.views;

import android.content.res.Resources;
import android.util.TypedValue;

public class ViewUtils {

    public static float dpToPx(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                Resources.getSystem().getDisplayMetrics()
        );
    }

    public static float spToPx(float sp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                Resources.getSystem().getDisplayMetrics()
        );
    }

}
