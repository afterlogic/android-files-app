package com.afterlogic.aurora.drive.core.common.util;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.afterlogic.aurora.drive.BuildConfig;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;

/**
 * Created by sashka on 20.01.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class AppUtil {

    @Nullable
    public static String getCurrentProcessName(Context context) {
        String processName = "";
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                processName = processInfo.processName;
                break;
            }
        }
        return processName;
    }

    public static boolean isProcess(@Nullable String name, Context context) {

        String fullName = TextUtils.isEmpty(name)
                ? BuildConfig.APPLICATION_ID : BuildConfig.APPLICATION_ID + ":" + name;

        return fullName.equals(getCurrentProcessName(context));
    }

    public static void setComponentEnabled(Class<? extends Context> componentClass, boolean enabled, Context context){
        PackageManager pm = context.getPackageManager();
        ComponentName cm = new ComponentName(context, componentClass);
        pm.setComponentEnabledSetting(
                cm,
                enabled ? COMPONENT_ENABLED_STATE_ENABLED : COMPONENT_ENABLED_STATE_DISABLED,
                DONT_KILL_APP
        );
    }

    public static boolean isComponentEnabled(Class<? extends Context> componentClass, Context context){
        PackageManager pm = context.getPackageManager();
        ComponentName cm = new ComponentName(context, componentClass);
        return pm.getComponentEnabledSetting(cm) == COMPONENT_ENABLED_STATE_ENABLED;
    }

    public static boolean isServiceRunning(Class<? extends Service> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
