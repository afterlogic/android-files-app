package com.afterlogic.aurora.drive.presentation.modules.offline.v2.view;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.modules.offline.v2.viewModel.OfflineHeader;
import com.afterlogic.aurora.drive.presentation.modules.offline.v2.viewModel.OfflineFileViewModel;
import com.github.nitrico.lastadapter.LastAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineBindings {

    @BindingAdapter({"offline_filesAdapter", "offline_header"})
    public static void bindFilesAdapter(RecyclerView list, List<OfflineFileViewModel> files, OfflineHeader header) {
        List<Object> items = new ArrayList<>();
        if (header != null) {
            items.add(header);
        }
        items.addAll(files);

        new LastAdapter(items, BR.vm, true)
                .map(OfflineHeader.class, R.layout.offline_item_list_header)
                .map(OfflineFileViewModel.class, R.layout.offline_item_list_file)
                .into(list);
    }
}
