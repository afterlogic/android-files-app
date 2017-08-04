package com.afterlogic.aurora.drive.presentation.modules.offline.view;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ViewModelState;
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.OfflineHeader;
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.OfflineFileViewModel;
import com.github.nitrico.lastadapter.LastAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineBindings {

    @BindingAdapter({"offline_filesAdapter", "offline_header", "offline_viewModelState"})
    public static void bindFilesAdapter(RecyclerView list, List<OfflineFileViewModel> files,
                                        OfflineHeader header, ViewModelState state) {
        List<Object> items = new ArrayList<>();

        if (files.isEmpty() && state.isContent()) {
            items.add(new OfflineEmptyListItem());
        }

        if (header != null) {
            items.add(0, header);
        }
        items.addAll(files);

        new LastAdapter(items, BR.vm, true)
                .map(OfflineHeader.class, R.layout.offline_item_list_header)
                .map(OfflineFileViewModel.class, R.layout.offline_item_list_file)
                .map(OfflineEmptyListItem.class, R.layout.offline_item_list_empty)
                .into(list);
    }
}
