package com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter;

import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;

/**
 * Created by sashka on 06.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class WeakObservableListListener<T> extends ObservableList.OnListChangedCallback<ObservableList<T>> {

    private final OptWeakRef<ObservableList.OnListChangedCallback<ObservableList<T>>> mOnChangedAction = OptWeakRef.empty();

    public WeakObservableListListener(ObservableList.OnListChangedCallback<ObservableList<T>> listener) {
        mOnChangedAction.set(listener);
    }

    @Override
    public void onChanged(ObservableList<T> ts) {
        ifPresent(ts, listener -> listener.onChanged(ts));
    }

    @Override
    public void onItemRangeChanged(ObservableList<T> ts, int i, int i1) {
        ifPresent(ts, listener -> listener.onItemRangeChanged(ts, i, i1));
    }

    @Override
    public void onItemRangeInserted(ObservableList<T> ts, int i, int i1) {
        ifPresent(ts, listener -> listener.onItemRangeInserted(ts, i, i1));
    }

    @Override
    public void onItemRangeMoved(ObservableList<T> ts, int i, int i1, int i2) {
        ifPresent(ts, listener -> listener.onItemRangeMoved(ts, i, i1, i2));
    }

    @Override
    public void onItemRangeRemoved(ObservableList<T> ts, int i, int i1) {
        ifPresent(ts, listener -> listener.onItemRangeRemoved(ts, i, i1));
    }

    private void ifPresent(ObservableList<T> ts, Consumer<ObservableList.OnListChangedCallback<ObservableList<T>>> consumer){
        mOnChangedAction.ifPresent(
                consumer,
                () -> ts.removeOnListChangedCallback(this)
        );
    }
}
