package com.afterlogic.aurora.drive.model;

import android.support.annotation.Nullable;

/**
 * Created by sashka on 08.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class Progressible<T> {

    private T mData;
    private long max;
    private long progress;

    @Nullable
    private String name;
    private boolean done;

    public Progressible(T data, long max, long progress, @Nullable String name, boolean done) {
        mData = data;
        this.max = max;
        this.progress = Math.min(progress, max);
        this.done = done;
        this.name = name;
    }

    public T getData() {
        return mData;
    }

    public long getMax() {
        return max;
    }

    public long getProgress() {
        return progress;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public boolean isDone(){
        return done;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public <R> Progressible<R> map(R value){
        return new Progressible<>(
                value, max, progress, name, done
        );
    }

    @Override
    public String toString() {
        return "[progress: " + progress + ", max: " + max +
                ", done: " + done + ", name: " + name + "]";
    }
}
