package com.afterlogic.aurora.drive._unrefactored.core.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Created by sashka on 29.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class DrawableUtil {

    public static Drawable getTintedDrawable(@DrawableRes int drawableId, @ColorRes int colorId, Context ctx){
        Drawable folderIcon = ContextCompat.getDrawable(ctx, drawableId);
        Drawable wrapDrawable = DrawableCompat.wrap(folderIcon);
        wrapDrawable = wrapDrawable.mutate();
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(ctx, colorId));
        return wrapDrawable;
    }
}
