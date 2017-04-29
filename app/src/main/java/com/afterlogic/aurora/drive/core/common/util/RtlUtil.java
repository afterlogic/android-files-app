package com.afterlogic.aurora.drive.core.common.util;

import android.content.Context;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;

/**
 * Created by sashka on 04.04.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class RtlUtil {
    public static boolean isRtl(Context ctx) {
        return TextUtilsCompat.getLayoutDirectionFromLocale(
                ctx.getResources().getConfiguration().locale
        ) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }
}
