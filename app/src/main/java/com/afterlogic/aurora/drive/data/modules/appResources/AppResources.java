package com.afterlogic.aurora.drive.data.modules.appResources;

import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;

/**
 * Created by sashka on 09.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface AppResources {
    String getString(@StringRes int id);

    String getString(@StringRes int id, Object... args);

    String[] getStringArray(@ArrayRes int id);
}
