package com.afterlogic.aurora.drive.presentation.modules.main.view;

import android.support.v7.widget.RecyclerView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters.ViewProvider;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFileListAdapter;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesListViewModel;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.FileActionItemViewModel;

import java.util.WeakHashMap;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainBindHelper {

    private static final WeakHashMap<RecyclerView, ItemsAdapter<BaseFileItemViewModel>> FILES_ADAPTERS = new WeakHashMap<>();
    private static final WeakHashMap<RecyclerView, ItemsAdapter<FileActionItemViewModel>> FILE_ACTION_ADAPTERS = new WeakHashMap<>();

    @SuppressWarnings("unused")
    public static ViewProvider<ItemsAdapter<BaseFileItemViewModel>, RecyclerView> filesListAdapter(BaseFilesListViewModel viewModel){
        return list -> {
            ItemsAdapter<BaseFileItemViewModel> adapter = FILES_ADAPTERS.get(list);
            if (adapter == null){
                adapter = new BaseFileListAdapter<>(R.layout.item_list_file_main, viewModel);
                FILES_ADAPTERS.put(list, adapter);
            }
            return adapter;
        };
    }

    @SuppressWarnings("unused")
    public static ViewProvider<ItemsAdapter<FileActionItemViewModel>, RecyclerView> fileActionsAdapter(){
        return list -> {
            ItemsAdapter<FileActionItemViewModel> adapter = FILE_ACTION_ADAPTERS.get(list);
            if (adapter == null){
                adapter = new FileActionsMenuAdapter();
                FILE_ACTION_ADAPTERS.put(list, adapter);
            }
            return adapter;
        };
    }
}
