package com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter;

import android.databinding.ObservableList;

/**
 * Created by sashka on 06.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SimpleOnObservableListChagnedListener<T> extends  ObservableList.OnListChangedCallback<ObservableList<T>> {

    private final Runnable mOnChangedAction;

    public SimpleOnObservableListChagnedListener(Runnable onChangedAction) {
        mOnChangedAction = onChangedAction;
    }

    @Override
    public void onChanged(ObservableList<T> ts) {
        mOnChangedAction.run();
    }

    @Override
    public void onItemRangeChanged(ObservableList<T> ts, int i, int i1) {
        mOnChangedAction.run();
    }

    @Override
    public void onItemRangeInserted(ObservableList<T> ts, int i, int i1) {
        mOnChangedAction.run();
    }

    @Override
    public void onItemRangeMoved(ObservableList<T> ts, int i, int i1, int i2) {
        mOnChangedAction.run();
    }

    @Override
    public void onItemRangeRemoved(ObservableList<T> ts, int i, int i1) {
        mOnChangedAction.run();
    }
}
