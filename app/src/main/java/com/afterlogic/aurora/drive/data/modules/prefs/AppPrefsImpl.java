package com.afterlogic.aurora.drive.data.modules.prefs;

import android.content.Context;

import net.grandcentrix.tray.AppPreferences;

import javax.inject.Inject;

/**
 * Created by sashka on 14.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AppPrefsImpl implements AppPrefs {

    private AppPreferences mAppPreferences;

    @Inject public AppPrefsImpl(Context ctx) {
        mAppPreferences = new AppPreferences(ctx);
    }

    @Override
    public Pref<Integer> appConfigVersion() {
        return new IntPref(mAppPreferences, "appConfigVersion");
    }
}
