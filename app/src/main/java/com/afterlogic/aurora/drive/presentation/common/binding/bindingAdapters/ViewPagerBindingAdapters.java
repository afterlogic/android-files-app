package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import android.databinding.BindingAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;

import java.util.List;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ViewPagerBindingAdapters {

    @BindingAdapter({"bind:adapter", "bind:items"})
    public static <T, A extends PagerAdapter & ItemsAdapter<T>>
    void bindAdapter(ViewPager pager, ViewProvider<A, ViewPager> adapterProvider, List<T> items){
        A adapter = adapterProvider.provide(pager);
        if (adapter != null){
            adapter.setItems(items);
        }
        if (adapter != pager.getAdapter()) {
            pager.setAdapter(adapter);
        }
    }
}
