package com.afterlogic.aurora.drive.presentation.common.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.content.IntentCompat;

import java.util.List;

/**
 * Created by sashka on 23.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class IntentUtil {

    public static Intent makeRestartTask(Intent intent){
        Intent result = IntentCompat.makeRestartActivityTask(intent.getComponent());
        result.putExtras(intent);
        return result;
    }

    public static boolean isAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        try {
            List<ResolveInfo> list =
                    mgr.queryIntentActivities(intent,
                            PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }catch (Exception e){
            return false;
        }
    }
}
