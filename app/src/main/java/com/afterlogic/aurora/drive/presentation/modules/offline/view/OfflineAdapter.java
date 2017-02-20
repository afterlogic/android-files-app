package com.afterlogic.aurora.drive.presentation.modules.offline.view;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.SimpleRecyclerViewModelAdapter;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemViewModel;
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.OfflineViewModel;

/**
 * Created by sashka on 20.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

class OfflineAdapter extends SimpleRecyclerViewModelAdapter<BaseFileItemViewModel> {

    private static final int ITEM = 0;
    private static final int HEADER = 1;

    private final OfflineViewModel mViewModel;

    public OfflineAdapter(OfflineViewModel viewModel) {
        super(R.layout.item_list_file_base);
        mViewModel = viewModel;
    }

    @Override
    public void onBindViewHolder(BindViewHolder holder, int position) {
        if (position == 0){
            holder.getDataBinding().setVariable(BR.viewModel, mViewModel);
        } else {
            super.onBindViewHolder(holder, position - 1);
        }
    }

    @Override
    protected int getViewLayout(int viewType) {
        return viewType == HEADER ? R.layout.item_list_offline_header : super.getViewLayout(viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? HEADER : ITEM;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }
}
