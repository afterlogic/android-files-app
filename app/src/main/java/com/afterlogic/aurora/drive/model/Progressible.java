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
    private boolean mDone;

    public Progressible(T data, long max, long progress, @Nullable String name, boolean done) {
        mData = data;
        mMax = max;
        mProgress = Math.min(progress, max);
        mDone = done;
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

    public void setMax(long max) {
        mMax = max;
    }

    public void setProgress(long progress) {
        mProgress = progress;
    }

    public boolean isDone(){
        return mDone;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    public <R> Progressible<R> map(R value){
        return new Progressible<>(
                value, mMax, mProgress, mName, mDone
        );
    }
}
