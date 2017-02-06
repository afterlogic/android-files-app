package com.afterlogic.aurora.drive.data.modules.appResources;

import android.content.Context;

import javax.inject.Inject;

/**
 * Created by sashka on 09.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AppResourcesImpl implements AppResources {
    private Context mContext;

    @Inject
    AppResourcesImpl(Context context) {
        mContext = context;
    }

    @Override
    public String getString(int id){
        return mContext.getString(id);
    }

    @Override
    public String getString(int id, Object... args){
        return mContext.getString(id, args);
    }
}
