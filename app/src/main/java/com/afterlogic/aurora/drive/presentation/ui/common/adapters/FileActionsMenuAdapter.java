package com.afterlogic.aurora.drive.presentation.ui.common.adapters;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.util.DrawableUtil;
import com.afterlogic.aurora.drive.core.util.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sashka on 24.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FileActionsMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ActionItem> mActionItems;
    private OnItemClickListener<ActionItem> mOnItemClickListener;

    public FileActionsMenuAdapter(@NonNull OnItemClickListener<ActionItem> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        mActionItems = new ArrayList<>();
    }

    public FileActionsMenuAdapter addAction(int id, @StringRes int title, @DrawableRes int icon){
        mActionItems.add(new ActionItem(title, icon, id));
        notifyDataSetChanged();
        return this;
    }

    public FileActionsMenuAdapter addSwitch(int id, @StringRes int title, @DrawableRes int icon,
                                            boolean checked, OnCheckedChangeListener listener){
        mActionItems.add(new ActionSwitch(title, icon, id, checked, listener));
        notifyDataSetChanged();
        return this;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case ActionType.TYPE_SWITCH:
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_file_action_switch, parent, false);
                return new ActionSwitchViewHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_file_action_normal, parent, false);
                return new ActionViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ActionItem item = mActionItems.get(position);
        switch (item.getType()){
            case ActionType.TYPE_SWITCH:
                onBindSwitchViewHolder((ActionSwitchViewHolder) holder, (ActionSwitch) item);
                break;
            default:
                onBindNormalViewHolder((ActionViewHolder) holder, item);
        }
    }

    private void onBindNormalViewHolder(final ActionViewHolder holder, ActionItem item){
        fillBaseData(holder, item);
        //Set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(
                        mActionItems.get(holder.getAdapterPosition())
                );
            }
        });
    }

    /**
     * Bind to switch, set {@link android.widget.CompoundButton.OnCheckedChangeListener}.
     */
    private void onBindSwitchViewHolder(final ActionSwitchViewHolder holder, final ActionSwitch item){
        fillBaseData(holder, item);
        //Clear previous onChecked change listener
        holder.switcher.setOnCheckedChangeListener(null);
        //Set checked state
        holder.switcher.setChecked(item.isChecked);
        //Set new on checked change listener
        holder.switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.isChecked = isChecked;
                if (item.mOnCheckedChangeListener != null){
                    item.mOnCheckedChangeListener.onCheckedChanged(item, isChecked);
                }
            }
        });
    }

    /**
     * Fill title and icon.
     */
    private void fillBaseData(ActionViewHolder holder, ActionItem item){
        if (item.icon != View.NO_ID) {
            Context ctx = holder.itemView.getContext();
            holder.icon.setImageDrawable(
                    DrawableUtil.getTintedDrawable(item.icon, R.color.colorPrimary, ctx)
            );
        }else{
            holder.icon.setImageDrawable(null);
        }
        if (item.title != View.NO_ID) {
            holder.title.setText(item.title);
        }else{
            holder.title.setText(null);
        }
    }

    @Override
    public int getItemCount() {
        return mActionItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mActionItems.get(position).getType();
    }

    protected class ActionViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public ImageView icon;

        public ActionViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text);
            icon = (ImageView) itemView.findViewById(R.id.icon);
        }
    }

    protected class ActionSwitchViewHolder extends ActionViewHolder implements View.OnClickListener{

        public SwitchCompat switcher;

        public ActionSwitchViewHolder(View itemView) {
            super(itemView);
            switcher = (SwitchCompat) itemView.findViewById(R.id.switcher);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switcher.toggle();
        }
    }

    public class ActionItem implements ActionType {
        private @StringRes int title;
        private @DrawableRes int icon;
        private int id;

        public ActionItem(@StringRes int title, @DrawableRes int icon, int id) {
            this.title = title;
            this.icon = icon;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public int getType() {
            return TYPE_NORMAL;
        }
    }

    public class ActionSwitch extends ActionItem{

        private OnCheckedChangeListener mOnCheckedChangeListener;
        private boolean isChecked;

        public ActionSwitch(int title, int icon, int id, boolean isChecked,
                            @Nullable OnCheckedChangeListener onCheckedChangeListener) {
            super(title, icon, id);
            this.isChecked = isChecked;
            mOnCheckedChangeListener = onCheckedChangeListener;
        }

        @Override
        public int getType() {
            return TYPE_SWITCH;
        }
    }

    private interface ActionType {
        int TYPE_NORMAL = 0;
        int TYPE_SWITCH = 1;

        int getType();
    }

    public interface OnCheckedChangeListener{
        void onCheckedChanged(ActionSwitch action, boolean isChecked);
    }
}
