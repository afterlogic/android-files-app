package com.afterlogic.aurora.drive.presentation.modules.main.view;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.RecyclerBindAdapter;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.FileActionItemViewModel;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.FileCheckableActionItemViewModel;

import java.util.List;

/**
 * Created by sashka on 24.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FileActionsMenuAdapter extends RecyclerBindAdapter implements ItemsAdapter<FileActionItemViewModel> {

    private static final int ACTION_SWITCH = 1;
    private static final int ACTION_NORMAL = 0;

    private List<FileActionItemViewModel> mFileActions;

    @Override
    public void onBindViewHolder(BindViewHolder holder, int position) {
        holder.getDataBinding().setVariable(BR.viewModel, mFileActions.get(position));
    }

    @Override
    public int getItemCount() {
        return mFileActions == null ? 0 : mFileActions.size();
    }

    @Override
    public void setItems(List<FileActionItemViewModel> items) {
        mFileActions = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mFileActions.get(position) instanceof FileCheckableActionItemViewModel ?
                ACTION_SWITCH : ACTION_NORMAL;
    }

    @Override
    protected int getViewLayout(int viewType) {
        return viewType == ACTION_SWITCH ? R.layout.item_list_file_action_switch : R.layout.item_list_file_action_normal;
    }
}
