package com.afterlogic.aurora.drive.data.modules.appResources;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ArrayRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Created by sashka on 09.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface AppResources {
    String getString(@StringRes int id);

    String getString(@StringRes int id, Object... args);

    String[] getStringArray(@ArrayRes int id);

    Drawable getDrawable(@DrawableRes int id);

    Uri getResourceUri(int id);

    String getPlurals(int id, int count, Object... args);
}
