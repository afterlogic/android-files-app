package com.afterlogic.aurora.drive.data.modules.appResources;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ArrayRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

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

    @Override
    public String[] getStringArray(@ArrayRes int id) {
        return mContext.getResources().getStringArray(id);
    }

    @Override
    public Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(mContext, id);
    }

    @Override
    public Uri getResourceUri(int id) {
        Resources resources = mContext.getResources();
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(id))
                .appendPath(resources.getResourceTypeName(id))
                .appendPath(resources.getResourceEntryName(id))
                .build();
    }
}
