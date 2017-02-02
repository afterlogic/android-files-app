package com.afterlogic.aurora.drive.presentation.common.modules.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;

/**
 * Created by sashka on 15.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public abstract class ViewModelListProvider<V>{

    protected final OptWeakRef<ObservableList<V>> mViewItems = OptWeakRef.empty();

    public ObservableList<V> getViewItems() {
        return mViewItems.getOrCreate(() -> {
            ObservableList<V> list = new ObservableArrayList<>();
            refresh(list);
            return list;
        });
    }

    protected void notifyDataSetChanged(){
        mViewItems.ifPresent(this::refresh);
    }

    protected abstract void refresh(ObservableList<V> viewItems);
}
