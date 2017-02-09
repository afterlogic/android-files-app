package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by sashka on 06.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SwipeRefreshLayoutBinding {

    @BindingAdapter("bind:refreshing")
    public static void setRefreshing(SwipeRefreshLayout view, boolean refreshing){
        view.setRefreshing(refreshing);
    }

    @BindingAdapter("bind:onRefresh")
    public static void setOnRefreshListener(SwipeRefreshLayout view, SwipeRefreshLayout.OnRefreshListener listener){
        view.setOnRefreshListener(listener);
    }

    @BindingAdapter("bind:colorSchemeResources")
    public static void bindColorSchemeResources(SwipeRefreshLayout view, int[] colors){
        view.setColorSchemeColors(colors);
    }
}
