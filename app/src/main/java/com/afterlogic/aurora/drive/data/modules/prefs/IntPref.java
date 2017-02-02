package com.afterlogic.aurora.drive.data.modules.prefs;

import net.grandcentrix.tray.AppPreferences;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
class IntPref implements Pref<Integer> {

    private final AppPreferences mPreferences;
    private final String mName;
    private int mDefaultValue = 0;

    IntPref(AppPreferences preferences, String name) {
        mPreferences = preferences;
        mName = name;
    }

    IntPref(AppPreferences preferences, String name, int defaultValue) {
        this(preferences, name);
        mDefaultValue = defaultValue;
    }

    @Override
    public Integer get() {
        return mPreferences.getInt(mName, mDefaultValue);
    }

    @Override
    public void set(Integer value) {
        mPreferences.put(mName, value);
    }
}
