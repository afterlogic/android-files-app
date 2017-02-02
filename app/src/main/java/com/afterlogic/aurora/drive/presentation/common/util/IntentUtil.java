package com.afterlogic.aurora.drive.presentation.common.util;

import android.content.Intent;
import android.support.v4.content.IntentCompat;

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
}
