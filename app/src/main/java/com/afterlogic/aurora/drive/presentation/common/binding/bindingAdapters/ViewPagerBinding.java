package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import android.databinding.BindingAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.common.components.view.DisablableViewPager;

import java.util.List;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ViewPagerBinding {

    @BindingAdapter({"adapter", "items"})
    public static <T, A extends ItemsAdapter<T>>
    void bindAdapter(ViewPager pager, ViewProvider<A, ViewPager> adapterProvider, List<T> items){
        bindAdapter(pager, adapterProvider, items, -1);
    }

    @BindingAdapter({"adapter", "items", "currentItem"})
    public static <T, A extends ItemsAdapter<T>>
    void bindAdapter(ViewPager pager, ViewProvider<A, ViewPager> adapterProvider, List<T> items, int currentItem){
        bindAdapter(pager, adapterProvider.provide(pager), items, currentItem);
    }

    @BindingAdapter({"adapter", "items"})
    public static <T> void bindAdapter(ViewPager pager, ItemsAdapter<T> adapter, List<T> items){
        bindAdapter(pager, adapter, items, -1);
    }

    @BindingAdapter({"adapter", "items", "currentItem"})
    public static <T> void bindAdapter(ViewPager pager, ItemsAdapter<T> adapter, List<T> items, int currentItem){
        if (adapter instanceof PagerAdapter) {
            bindAdapter(pager, (PagerAdapter & ItemsAdapter<T>)adapter, items, currentItem);
        } else {
            throw new IllegalArgumentException("Adapter must extend PagerAdapter.");
        }
    }

    @BindingAdapter({"adapter", "items"})
    public static <T, A extends PagerAdapter & ItemsAdapter<T>>
    void bindAdapter(ViewPager pager, A adapter, List<T> items){
        bindAdapter(pager, adapter, items, -1);
    }

    @BindingAdapter({"adapter", "items", "currentItem"})
    public static <T, A extends PagerAdapter & ItemsAdapter<T>>
    void bindAdapter(ViewPager pager, A adapter, List<T> items, int currentItem){
        if (adapter != null){
            adapter.setItems(items);
        }
        if (adapter != pager.getAdapter()) {
            pager.setAdapter(adapter);
        }
        bindCurrentItem(pager, currentItem);
    }

    @BindingAdapter("swipeEnabled")
    public static void bindSwipeEnabled(DisablableViewPager pager, boolean enabled){
        pager.setSwipeEnabled(enabled);
    }

    @BindingAdapter("onPageChanged")
    public static void bindOnPageChanged(ViewPager pager, ViewPager.OnPageChangeListener listener){
        ViewPager.OnPageChangeListener previous = (ViewPager.OnPageChangeListener) pager.getTag(R.id.bind_pager_listener);
        if (previous != null){
            pager.removeOnPageChangeListener(previous);
        }
        pager.setTag(R.id.bind_pager_listener, listener);
        pager.addOnPageChangeListener(listener);
    }

    @BindingAdapter({"currentItem"})
    public static void bindCurrentItem(ViewPager pager, int position){
        PagerAdapter adapter = pager.getAdapter();
        if (adapter != null && position >= 0){
            if (pager.getCurrentItem() != position) {
                pager.setCurrentItem(position, false);
            }
        }
    }
}
