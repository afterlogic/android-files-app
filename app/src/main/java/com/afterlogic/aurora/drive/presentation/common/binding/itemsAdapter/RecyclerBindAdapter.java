package com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sashka on 13.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public abstract class RecyclerBindAdapter extends RecyclerView.Adapter<RecyclerBindAdapter.BindViewHolder> {

    @Override
    public BindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BindViewHolder(getView(parent, viewType), viewType);
    }

    @LayoutRes
    protected abstract int getViewLayout(int viewType);

    protected View getView(ViewGroup parent, int viewType){
        return LayoutInflater.from(parent.getContext()).inflate(
                getViewLayout(viewType), parent, false
        );
    }

    protected class BindViewHolder extends RecyclerView.ViewHolder {
        private int mType;
        private ViewDataBinding mDataBinding;

        public BindViewHolder(View itemView, int type) {
            super(itemView);
            mDataBinding = DataBindingUtil.bind(itemView);
            mType = type;
        }

        public int getType() {
            return mType;
        }

        public <T extends ViewDataBinding> T getDataBinding() {
            return (T) mDataBinding;
        }
    }
}
