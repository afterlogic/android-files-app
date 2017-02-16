package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import android.databinding.BindingAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;

import java.util.List;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class RecyclerViewBinding {

    @BindingAdapter({"bind:adapter"})
    public static void setRecyclerViewAdapter(RecyclerView list, ViewProvider<? extends RecyclerView.Adapter, RecyclerView> adapter){
        setRecyclerViewAdapter(list, adapter.provide(list));
    }

    @BindingAdapter({"bind:adapter"})
    public static <T extends RecyclerView.Adapter> void setRecyclerViewAdapter(RecyclerView list, T adapter){
        if (list.getAdapter() != adapter) {
            list.setAdapter(adapter);
        }
    }

    @BindingAdapter({"bind:adapter", "bind:items"})
    public static <VM> void setRecyclerAdapterWithItems(RecyclerView list, ViewProvider<? extends ItemsAdapter<VM>, RecyclerView> adapter, List<VM> items){
        setRecyclerAdapterWithItems(list, adapter.provide(list), items);
    }

    @BindingAdapter({"bind:adapter", "bind:items"})
    public static <VM, A extends ItemsAdapter<VM>> void setRecyclerAdapterWithItems(RecyclerView list, A adapter, List<VM> items){
        if (adapter != null) {
            adapter.setItems(items);
        }
        setRecyclerViewAdapter(list, (RecyclerView.Adapter)adapter);
    }

    @BindingAdapter("bind:decoration")
    public static void setRecyclerDecoration(RecyclerView list, ViewProvider<RecyclerView.ItemDecoration, RecyclerView> provider){
        RecyclerView.ItemDecoration prev = (RecyclerView.ItemDecoration) list.getTag(R.id.bind_recycler_decoration);
        RecyclerView.ItemDecoration decoration = provider.provide(list);

        if (prev == decoration) return;

        if (prev != null){
            list.removeItemDecoration(prev);
        }

        if (decoration != null) {
            list.addItemDecoration(decoration);
        }
    }

    @BindingAdapter("bind:layoutManager")
    public static void setRecyclerLayoutManager(RecyclerView list, ViewProvider<RecyclerView.LayoutManager, RecyclerView> provider){
        setRecyclerLayoutManager(list, provider.provide(list));
    }

    @BindingAdapter("bind:layoutManager")
    public static void setRecyclerLayoutManager(RecyclerView list, RecyclerView.LayoutManager manger){
        list.setLayoutManager(manger);
    }

    public static ViewProvider<RecyclerView.LayoutManager, RecyclerView> linearLayoutManager(){
        return list -> new LinearLayoutManager(list.getContext());
    }
}
