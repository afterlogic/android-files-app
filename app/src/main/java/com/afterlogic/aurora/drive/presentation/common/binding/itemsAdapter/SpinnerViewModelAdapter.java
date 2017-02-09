package com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.SpinnerViewModel;

import java.util.List;

/**
 * Created by sashka on 26.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SpinnerViewModelAdapter<VM extends SpinnerViewModel> extends BaseAdapter{

    private List<VM> mItems;

    @LayoutRes private final int mViewLayoutId;
    @LayoutRes private final int mDropDownViewLayoutId;
    private final int mViewModelId;
    private final int mDropDownViewModelId;

    private final ListListener mListListener = new ListListener(this);

    public SpinnerViewModelAdapter() {
        this(R.layout.item_list_base_spinner_drop_down, R.layout.item_list_base_spinner_drop_down, BR.viewModel, BR.viewModel);
    }

    public SpinnerViewModelAdapter(int viewLayoutId, int dropDownViewLayoutId) {
        this(viewLayoutId, dropDownViewLayoutId, BR.viewModel, BR.viewModel);
    }

    public SpinnerViewModelAdapter(int viewLayoutId, int dropDownViewLayoutId, int viewModelId, int dropDownViewModelId) {
        mViewLayoutId = viewLayoutId;
        mDropDownViewLayoutId = dropDownViewLayoutId;
        mViewModelId = viewModelId;
        mDropDownViewModelId = dropDownViewModelId;
    }

    public void setItems(List<VM> items) {
        if (mItems == items) return;

        if (mItems != null && mItems instanceof ObservableList){
            ((ObservableList<VM>) mItems).removeOnListChangedCallback(mListListener);
        }

        mItems = items;

        if (mItems != null && mItems instanceof ObservableList){
            ((ObservableList<VM>) mItems).addOnListChangedCallback(mListListener);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public VM getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewDataBinding binding = getViewDataBinding(view, viewGroup, mViewLayoutId);
        binding.setVariable(mViewModelId, getItem(i));
        return binding.getRoot();
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return mItems == null || mItems.size() == 0;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        ViewDataBinding binding = getViewDataBinding(view, viewGroup, mDropDownViewLayoutId);
        binding.setVariable(mDropDownViewModelId, getDropDownItem(i));
        return binding.getRoot();
    }

    protected VM getDropDownItem(int position){
        return getItem(position);
    }

    private ViewDataBinding getViewDataBinding(View view, ViewGroup viewGroup, int layout){
        if (view == null){
            return DataBindingUtil.inflate(
                    LayoutInflater.from(viewGroup.getContext()),
                    layout,
                    viewGroup,
                    false
            );
        } else {
            return DataBindingUtil.bind(view);
        }
    }

    public int getItemsShift() {
        return 0;
    }

    private class ListListener extends ObservableList.OnListChangedCallback<ObservableList<VM>> {

        private final OptWeakRef<SpinnerViewModelAdapter<VM>> mAdapter = OptWeakRef.empty();

        @SuppressWarnings("WeakerAccess")
        public ListListener(SpinnerViewModelAdapter<VM> adapter) {
            mAdapter.set(adapter);
        }

        @Override
        public void onChanged(ObservableList<VM> sender) {
            mAdapter.ifPresent(SpinnerViewModelAdapter::notifyDataSetChanged);
        }

        @Override
        public void onItemRangeChanged(ObservableList<VM> sender, int positionStart, int itemCount) {
            mAdapter.ifPresent(SpinnerViewModelAdapter::notifyDataSetChanged);
        }

        @Override
        public void onItemRangeInserted(ObservableList<VM> sender, int positionStart, int itemCount) {
            mAdapter.ifPresent(SpinnerViewModelAdapter::notifyDataSetChanged);
        }

        @Override
        public void onItemRangeMoved(ObservableList<VM> sender, int fromPosition, int toPosition, int itemCount) {
            mAdapter.ifPresent(SpinnerViewModelAdapter::notifyDataSetChanged);
        }

        @Override
        public void onItemRangeRemoved(ObservableList<VM> sender, int positionStart, int itemCount) {
            mAdapter.ifPresent(SpinnerViewModelAdapter::notifyDataSetChanged);
        }
    }
}
