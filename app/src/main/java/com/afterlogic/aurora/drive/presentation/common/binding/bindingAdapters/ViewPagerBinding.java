package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import android.databinding.BindingAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.common.components.view.DisablableViewPager;

import java.util.List;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ViewPagerBinding {

    @BindingAdapter({"bind:adapter", "bind:items"})
    public static <T, A extends PagerAdapter & ItemsAdapter<T>>
    void bindAdapter(ViewPager pager, ViewProvider<A, ViewPager> adapterProvider, List<T> items){
        bindAdapter(pager, adapterProvider.provide(pager), items);
    }

    @BindingAdapter({"bind:adapter", "bind:items"})
    public static <T> void bindAdapter(ViewPager pager, ItemsAdapter<T> adapter, List<T> items){
        if (adapter instanceof PagerAdapter) {
            bindAdapter(pager, (PagerAdapter & ItemsAdapter<T>)adapter, items);
        } else {
            throw new IllegalArgumentException("Adapter must extend PagerAdapter.");
        }
    }

    @BindingAdapter({"bind:adapter", "bind:items"})
    public static <T, A extends PagerAdapter & ItemsAdapter<T>>
    void bindAdapter(ViewPager pager, A adapter, List<T> items){
        if (adapter != null){
            adapter.setItems(items);
        }
        if (adapter != pager.getAdapter()) {
            pager.setAdapter(adapter);
        }
    }

    @BindingAdapter("bind:swipeEnabled")
    public static void bindSwipeEnabled(DisablableViewPager pager, boolean enabled){
        pager.setSwipeEnabled(enabled);
    }

    @BindingAdapter({"bind:currentItem"})
    public static void bindCurrentItem(ViewPager pager, int position){
        if (position != -1){
            pager.setCurrentItem(position);
        }
    }
}
