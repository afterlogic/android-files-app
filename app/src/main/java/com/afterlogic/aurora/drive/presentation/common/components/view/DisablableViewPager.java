package com.afterlogic.aurora.drive.presentation.common.components.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by sashka on 22.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class DisablableViewPager extends ViewPager{
    private boolean mIsEnabled = true;

    public DisablableViewPager(Context context) {
        super(context);
    }

    public DisablableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSwipeEnabled(boolean enabled) {
        mIsEnabled = enabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mIsEnabled && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mIsEnabled && super.onTouchEvent(event);
    }
}
