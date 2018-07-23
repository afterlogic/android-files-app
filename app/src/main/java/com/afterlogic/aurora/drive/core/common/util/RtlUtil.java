package com.afterlogic.aurora.drive.core.common.util;

import android.content.Context;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;

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
