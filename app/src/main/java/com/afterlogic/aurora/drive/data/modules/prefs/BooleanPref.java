package com.afterlogic.aurora.drive.data.modules.prefs;

import net.grandcentrix.tray.AppPreferences;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
class BooleanPref implements Pref<Boolean> {

    private final AppPreferences mPreferences;
    private final String mName;
    private boolean mDefaultValue = false;

    BooleanPref(AppPreferences preferences, String name) {
        mPreferences = preferences;
        mName = name;
    }

    BooleanPref(AppPreferences preferences, String name, boolean defaultValue) {
        mPreferences = preferences;
        mName = name;
        mDefaultValue = defaultValue;
    }

    @Override
    public Boolean get() {
        return mPreferences.getBoolean(mName, mDefaultValue);
    }

    @Override
    public void set(Boolean value) {
        mPreferences.put(mName, value);
    }
}
