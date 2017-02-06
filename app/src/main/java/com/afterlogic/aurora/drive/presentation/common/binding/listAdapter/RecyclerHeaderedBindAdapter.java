package com.afterlogic.aurora.drive.presentation.common.binding.listAdapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sashka on 13.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public abstract class RecyclerHeaderedBindAdapter extends RecyclerBindAdapter {

    private static final int TYPE_HEADER = -1;
    private static final int TYPE_FOOTER = -2;
    private static final int TYPE_ITEM = 0;

    private View mHeaderView;
    private View mFooterView;

    @Override
    protected View getView(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_FOOTER: return mFooterView;
            case TYPE_HEADER: return mHeaderView;
            default: return super.getView(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(BindViewHolder holder, int position) {
        if (holder.getType() != TYPE_FOOTER && holder.getType() != TYPE_HEADER) {
            int shift = mHeaderView != null ? 1 : 0;
            onBindContentViewHolder(holder, position - shift);
        }
    }

    public abstract void onBindContentViewHolder(BindViewHolder holder, int contentPosition);

    @Override
    public int getItemCount() {
        int size = getContentItemCount();
        if (mHeaderView != null) size++;
        if (mFooterView != null) size++;
        return size;
    }

    public abstract int getContentItemCount();

    @Override
    public int getItemViewType(int position) {
        int offset = 0;
        if (mHeaderView != null) {
            if (position == 0) {
                return TYPE_HEADER;
            }
            offset++;
        }
        if (mFooterView != null && position == getContentItemCount() + offset) {
            return TYPE_FOOTER;
        }
        return getContentItemViewType(position);
    }

    public int getContentItemViewType(int position) {
        return TYPE_ITEM;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
    }
}
