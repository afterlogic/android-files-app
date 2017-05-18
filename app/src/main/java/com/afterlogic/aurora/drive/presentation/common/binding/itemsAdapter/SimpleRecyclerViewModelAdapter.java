package com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter;

import android.support.annotation.LayoutRes;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SimpleRecyclerViewModelAdapter<T> extends RecyclerViewModelAdapter<T> {

    @LayoutRes
    private final int mItemLayout;

    public SimpleRecyclerViewModelAdapter(int itemLayout) {
        mItemLayout = itemLayout;
    }

    @Override
    protected int getViewLayout(int viewType) {
        return mItemLayout;
    }
}
