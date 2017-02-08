package com.afterlogic.aurora.drive.model;

import android.support.annotation.Nullable;

/**
 * Created by sashka on 08.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class Progressible<T> {

    private T mData;
    private long mMax;
    private long mProgress;

    @Nullable
    private String mName;

    public Progressible(T data, long max, long progress, @Nullable String name) {
        mData = data;
        mMax = max;
        mProgress = progress;
        mName = name;
    }

    public T getData() {
        return mData;
    }

    public long getMax() {
        return mMax;
    }

    public long getProgress() {
        return mProgress;
    }

    public boolean isDone(){
        return mData != null;
    }

    @Nullable
    public String getName() {
        return mName;
    }
}
