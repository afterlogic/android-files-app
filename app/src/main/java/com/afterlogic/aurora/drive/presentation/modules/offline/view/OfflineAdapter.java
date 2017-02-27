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
    private static final int EMPTY_ITEM = 2;

    private final OfflineViewModel mViewModel;

    @SuppressWarnings("WeakerAccess")
    public OfflineAdapter(OfflineViewModel viewModel) {
        super(R.layout.item_list_file_base);
        mViewModel = viewModel;
    }

    @Override
    public void onBindViewHolder(BindViewHolder holder, int position) {
        if (holder.getType() == ITEM){
            super.onBindViewHolder(holder, position - 1);
        } else {
            holder.getDataBinding().setVariable(BR.viewModel, mViewModel);
        }
    }

    @Override
    protected int getViewLayout(int viewType) {
        switch (viewType){
            case HEADER: return R.layout.item_list_offline_header;
            case EMPTY_ITEM: return R.layout.item_list_empty_folder;

            default: return super.getViewLayout(viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? HEADER : super.getItemCount() == 0 ? EMPTY_ITEM : ITEM;
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        return itemCount + (itemCount == 0 ? 2 : 1);
    }
}
