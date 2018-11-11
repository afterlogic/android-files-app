package com.afterlogic.aurora.drive.presentation.modules.baseFiles.view;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.SimpleRecyclerViewModelAdapter;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.viewModel.BaseFileItemViewModel;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.viewModel.BaseFilesListViewModel;

/**
 * Created by sashka on 20.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BaseFileListAdapter<T extends BaseFileItemViewModel> extends SimpleRecyclerViewModelAdapter<T> {

    private static final int EMPTY = 1;
    private static final int ITEM = 0;

    private final BaseFilesListViewModel mViewModel;

    public BaseFileListAdapter(int itemLayout, BaseFilesListViewModel viewModel) {
        super(itemLayout);
        mViewModel = viewModel;
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        return count == 0 ? 1 : count;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 && super.getItemCount() == 0 ? EMPTY : ITEM;
    }

    @Override
    protected int getViewLayout(int viewType) {
        return viewType == ITEM ? super.getViewLayout(viewType) : R.layout.item_list_empty_folder;
    }

    @Override
    public void onBindViewHolder(BindViewHolder holder, int position) {
        if (holder.getType() == EMPTY){
            holder.getDataBinding().setVariable(BR.viewModel, mViewModel);
        } else {
            super.onBindViewHolder(holder, position);
        }
    }
}
