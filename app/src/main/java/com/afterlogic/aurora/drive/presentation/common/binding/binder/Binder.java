package com.afterlogic.aurora.drive.presentation.common.binding.binder;

import android.databinding.BaseObservable;
import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.interfaces.Provider;
import com.afterlogic.aurora.drive.core.common.util.Holder;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;

/**
 * Created by sashka on 23.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class Binder<T> extends BaseObservable {

    @NonNull
    private final Provider<T> mGet;

    @NonNull
    private final Consumer<T> mSet;

    public Binder(){
        Holder<T> value = new Holder<>();
        mGet = value::get;
        mSet = value::set;
    }

    public Binder(@NonNull Provider<T> get, @NonNull Consumer<T> set) {
        mGet = get;
        mSet = set;
    }

    public void set(T value){
        if (isNeedSet(value, mGet.get())){
            mSet.consume(value);
            notifyChange();
        }
    }

    public T get(){
        return mGet.get();
    }

    protected boolean isNeedSet(T newValue, T existValue){
        return !ObjectsUtil.equals(newValue, existValue);
    }
}
