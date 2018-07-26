package com.afterlogic.aurora.drive.presentation.modules.baseFiles.view;

import androidx.recyclerview.widget.RecyclerView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters.ViewProvider;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.viewModel.BaseFileItemViewModel;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.viewModel.BaseFilesListViewModel;

import java.util.WeakHashMap;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BindHelper {

    private static final WeakHashMap<RecyclerView, ItemsAdapter<BaseFileItemViewModel>> FILES_ADAPTERS = new WeakHashMap<>();

    public static ViewProvider<ItemsAdapter<BaseFileItemViewModel>, RecyclerView> filesListAdapter(BaseFilesListViewModel viewModel){
        return list -> {
            ItemsAdapter<BaseFileItemViewModel> adapter = FILES_ADAPTERS.get(list);
            if (adapter == null){
                adapter = new BaseFileListAdapter<>(R.layout.item_list_file_base, viewModel);
                FILES_ADAPTERS.put(list, adapter);
            }
            return adapter;
        };
    }
}
