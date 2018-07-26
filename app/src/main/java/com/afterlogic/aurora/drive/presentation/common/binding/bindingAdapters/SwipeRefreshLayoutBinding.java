package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import androidx.databinding.BindingAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by sashka on 06.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SwipeRefreshLayoutBinding {

    @BindingAdapter("refreshing")
    public static void setRefreshing(SwipeRefreshLayout view, boolean refreshing){
        view.setRefreshing(refreshing);
    }

    @BindingAdapter("onRefresh")
    public static void setOnRefreshListener(SwipeRefreshLayout view, SwipeRefreshLayout.OnRefreshListener listener){
        view.setOnRefreshListener(listener);
    }

    @BindingAdapter("colorSchemeResources")
    public static void bindColorSchemeResources(SwipeRefreshLayout view, int[] colors){
        view.setColorSchemeColors(colors);
    }
}
