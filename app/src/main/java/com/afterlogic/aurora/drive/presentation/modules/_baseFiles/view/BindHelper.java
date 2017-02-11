package com.afterlogic.aurora.drive.presentation.modules._baseFiles.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters.ViewProvider;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.RecyclerViewModelAdapter;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.SimpleRecyclerViewModelAdapter;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemViewModel;

import java.util.WeakHashMap;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BindHelper {

    private static final WeakHashMap<RecyclerView, RecyclerViewModelAdapter<BaseFileItemViewModel>> FILES_ADAPTERS = new WeakHashMap<>();

    public static ViewProvider<RecyclerViewModelAdapter<BaseFileItemViewModel>, RecyclerView> filesListAdapter(){
        return list -> {
            RecyclerViewModelAdapter<BaseFileItemViewModel> adapter = FILES_ADAPTERS.get(list);
            if (adapter == null){
                adapter = new SimpleRecyclerViewModelAdapter<>(R.layout.item_list_file_base);
                FILES_ADAPTERS.put(list, adapter);
            }
            return adapter;
        };
    }

    public static ViewProvider<RecyclerView.LayoutManager, RecyclerView> linearLayoutManager(){
        return list -> new LinearLayoutManager(list.getContext());
    }
}
