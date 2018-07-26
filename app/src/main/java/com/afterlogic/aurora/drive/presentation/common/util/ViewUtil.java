package com.afterlogic.aurora.drive.presentation.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import androidx.annotation.Nullable;
import android.view.View;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

public class ViewUtil {

    @Nullable
    public static <T extends Activity> T getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                try {
                    return (T) context;
                } catch (Exception e) {
                    MyLog.majorException(e);
                    return null;
                }
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
}
