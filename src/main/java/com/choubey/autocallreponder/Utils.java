package com.choubey.autocallreponder;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by choubey on 7/4/15.
 */
public class Utils {
    private static final String Y = "Y";
    private static final String YES = "Yes";
    private static final String NO = "No";

    public static Float convertDpToPixel(int numberOfDp, Context context)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, numberOfDp, displayMetrics);
        return pixels;
    }

    public static String convertBoolCharToString(String active)
    {
        if(Y.equals(active))
        {
            return YES;
        }
        else
        {
            return NO;
        }
    }
}