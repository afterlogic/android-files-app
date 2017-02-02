package com.afterlogic.aurora.drive.core.common.resourceProvders;

import android.content.Context;

import javax.inject.Inject;

/**
 * Created by sashka on 09.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AppStringProviderImpl implements AppStringProvider {
    private Context mContext;

    @Inject public AppStringProviderImpl(Context context) {
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
