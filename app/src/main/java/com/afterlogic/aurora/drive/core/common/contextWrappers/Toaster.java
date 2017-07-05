package com.afterlogic.aurora.drive.core.common.contextWrappers;

import android.content.Context;
import android.widget.Toast;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 29.04.17.
 */

public class Toaster {

    private final Context mContext;

    @Inject
    public Toaster(Context mContext) {
        this.mContext = mContext;
    }

    public void showLong(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
    }

    public void showLong(int textId) {
        Toast.makeText(mContext, textId, Toast.LENGTH_LONG).show();
    }


    public void showShort(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
    }

    public void showShort(int textId) {
        Toast.makeText(mContext, textId, Toast.LENGTH_LONG).show();
    }
}
