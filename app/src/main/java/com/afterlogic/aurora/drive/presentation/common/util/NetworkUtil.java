package com.afterlogic.aurora.drive.presentation.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by sashka on 13.01.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class NetworkUtil {

    public static boolean checkNetwork(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE
        );

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
