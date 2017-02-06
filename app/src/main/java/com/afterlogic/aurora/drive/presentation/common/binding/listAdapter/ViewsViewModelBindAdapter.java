package com.afterlogic.aurora.drive.presentation.common.binding.listAdapter;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.annimon.stream.Stream;

import java.util.List;

/**
 * Created by sashka on 05.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public abstract class ViewsViewModelBindAdapter<VM> {

    private final ViewTreeObserver.OnPreDrawListener mLockPreDraw = () -> false;

    private OptWeakRef<ViewGroup> mContainer = OptWeakRef.empty();

    private List<VM> mItems;

    private final ObservableList.OnListChangedCallback<ObservableList<VM>> mOnChangeListener = new ObservableList.OnListChangedCallback<ObservableList<VM>>() {
        @Override
        public void onChanged(ObservableList<VM> vms) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList<VM> vms, int i, int i1) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(ObservableList<VM> vms, int i, int i1) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeMoved(ObservableList<VM> vms, int i, int i1, int i2) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeRemoved(ObservableList<VM> vms, int i, int i1) {
            notifyDataSetChanged();
        }
    };

    private final View.OnAttachStateChangeListener mOnAttachStateChangeListener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View view) {
        }

        @Override
        public void onViewDetachedFromWindow(View view) {
            if (mItems != null && mItems instanceof ObservableList){
                ((ObservableList<VM>) mItems).removeOnListChangedCallback(mOnChangeListener);
            }
        }
    };

    public abstract int getViewLayout();

    public void setItems(List<VM> items) {
        mItems = items;

        if (mItems != null && mItems instanceof ObservableList){
            ((ObservableList<VM>) mItems).addOnListChangedCallback(mOnChangeListener);
        }

        invalidateList();
    }

    public int getCount(){
        return mItems == null ? 0 : mItems.size();
    }

    public View getView(LayoutInflater inflater, int position, ViewGroup container){
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, getViewLayout(), container, false);
        binding.setVariable(getViewModelVariable(), mItems.get(position));
        return binding.getRoot();
    }

    public final void onAttach(ViewGroup container){
        mContainer.set(container);
        View.OnAttachStateChangeListener listener = (View.OnAttachStateChangeListener) container.getTag(R.id.bind_views_adapter_attach_listener);
        if (listener != null){
            listener.onViewDetachedFromWindow(container);
            container.removeOnAttachStateChangeListener(listener);
        }
        container.setTag(R.id.bind_views_adapter_attach_listener, mOnAttachStateChangeListener);
        container.addOnAttachStateChangeListener(mOnAttachStateChangeListener);

        invalidateList();
    }

    private void invalidateList(){
        mContainer.ifPresent(container -> {
            MyLog.d(this, "Remove all views.");
            //container.getViewTreeObserver().addOnPreDrawListener(mLockPreDraw);
            container.removeAllViews();

            MyLog.d(this, "Start add views.");
            if (getCount() > 0) {
                LayoutInflater inflater = LayoutInflater.from(container.getContext());

                Stream.range(0, getCount())
                        .map(position -> getView(inflater, position, container))
                        .forEach(container::addView);
            }
            //container.getViewTreeObserver().addOnPreDrawListener(mLockPreDraw);
            container.requestLayout();

            MyLog.d(this, "Finish add views.");
        });
    }

    protected int getViewModelVariable(){
        return BR.viewModel;
    }

    public final void notifyDataSetChanged(){
        mContainer.ifPresent(this::onAttach);
    }
}
