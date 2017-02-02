package com.afterlogic.aurora.drive.data.modules.prefs;

import net.grandcentrix.tray.AppPreferences;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
class LongPref implements Pref<Long> {

    private final AppPreferences mPreferences;
    private final String mName;
    private long mDefaultValue = 0;

    LongPref(AppPreferences preferences, String name) {
        mPreferences = preferences;
        mName = name;
    }

    LongPref(AppPreferences preferences, String name, long defaultValue){
        mPreferences = preferences;
        mName = name;
        mDefaultValue = defaultValue;
    }

    @Override
    public Long get() {
        return mPreferences.getLong(mName, mDefaultValue);
    }

    @Override
    public void set(Long value) {
        mPreferences.put(mName, value);
    }
}
