package com.afterlogic.aurora.drive.data.modules.prefs;

import net.grandcentrix.tray.AppPreferences;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
class StringPref implements Pref<String> {

    private final AppPreferences mPreferences;
    private final String mName;
    private String mDefaultValue = null;

    StringPref(AppPreferences preferences, String name) {
        mPreferences = preferences;
        mName = name;
    }

    StringPref(AppPreferences preferences, String name, String defaultValue) {
        mPreferences = preferences;
        mName = name;
        mDefaultValue = defaultValue;
    }

    @Override
    public String get() {
        return mPreferences.getString(mName, mDefaultValue);
    }

    @Override
    public void set(String value) {
        mPreferences.put(mName, value);
    }
}
