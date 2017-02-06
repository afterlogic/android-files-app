package com.afterlogic.aurora.drive.presentation.common.modules.viewModel;

import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;

/**
 * Created by sashka on 26.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
class ObservableListListener<V, M> extends ObservableList.OnListChangedCallback<ObservableList<M>> {

    private final OptWeakRef<ViewModelList<V, M>> mViewModel = OptWeakRef.empty();

    public ObservableListListener(ViewModelList<V, M> model) {
        mViewModel.set(model);
    }

    @Override
    public void onChanged(ObservableList<M> sender) {
        notifyChanged(sender);
    }

    @Override
    public void onItemRangeChanged(ObservableList<M> sender, int positionStart, int itemCount) {
        notifyChanged(sender);
    }

    @Override
    public void onItemRangeInserted(ObservableList<M> sender, int positionStart, int itemCount) {
        notifyChanged(sender);
    }

    @Override
    public void onItemRangeMoved(ObservableList<M> sender, int fromPosition, int toPosition, int itemCount) {
        notifyChanged(sender);
    }

    @Override
    public void onItemRangeRemoved(ObservableList<M> sender, int positionStart, int itemCount) {
        notifyChanged(sender);
    }

    private void notifyChanged(ObservableList<M> sender) {
        mViewModel.ifPresent(
                ViewModelList::notifyChanged,
                () -> sender.removeOnListChangedCallback(this)
        );
    }
}
