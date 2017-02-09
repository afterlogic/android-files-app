package com.afterlogic.aurora.drive.presentation.modules.filelist.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters.ViewProvider;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.RecyclerViewModelAdapter;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.SimpleRecyclerViewModelAdapter;
import com.afterlogic.aurora.drive.presentation.modules.filelist.viewModel.FileViewModel;

import java.util.WeakHashMap;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileListBindHelper {

    private static final WeakHashMap<RecyclerView, RecyclerViewModelAdapter<FileViewModel>> FILES_ADAPTERS = new WeakHashMap<>();

    public static ViewProvider<RecyclerViewModelAdapter<FileViewModel>, RecyclerView> filesAdapter(){
        return list -> {
            RecyclerViewModelAdapter<FileViewModel> adapter = FILES_ADAPTERS.get(list);
            if (adapter == null){
                adapter = new SimpleRecyclerViewModelAdapter<>(R.layout.item_list_file_bind);
                FILES_ADAPTERS.put(list, adapter);
            }
            return adapter;
        };
    }

    public static ViewProvider<RecyclerView.LayoutManager, RecyclerView> linearLayoutManager(){
        return list -> new LinearLayoutManager(list.getContext());
    }
}