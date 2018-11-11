package com.afterlogic.aurora.drive.presentation.common.util;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

import com.annimon.stream.Stream;

/**
 * Created by sashka on 14.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class PermissionUtil {

    public static boolean isAllGranted(Context ctx, String... perms){
        return getUngrantedPermissions(ctx, perms).length == 0;
    }

    public static String[] getUngrantedPermissions(Context context, String... perms){
        return Stream.of(perms)
                .filter(perm -> !isGranted(context, perm))
                .toArray(String[]::new);
    }

    public static boolean isGranted(Context context, String perm){
        return ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED;
    }
}
