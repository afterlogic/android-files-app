package com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.view;

import androidx.databinding.BindingAdapter;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.viewModel.ButtonFileActionViewModel;
import com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.viewModel.CheckableFileActionViewModel;
import com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.viewModel.FileActionViewModel;
import com.github.nitrico.lastadapter.LastAdapter;

import java.util.List;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFileActionsBindings {

    @BindingAdapter("fileActions_adapter")
    public static void bindFileActionsAdapter(RecyclerView recyclerView, List<FileActionViewModel> actions) {
        new LastAdapter(actions, BR.vm)
                .map(CheckableFileActionViewModel.class, R.layout.main_item_list_file_action_switch)
                .map(ButtonFileActionViewModel.class, R.layout.main_item_list_file_action_button)
                .into(recyclerView);
    }
}
