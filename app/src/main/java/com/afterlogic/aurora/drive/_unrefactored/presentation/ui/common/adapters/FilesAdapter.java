package com.afterlogic.aurora.drive._unrefactored.presentation.ui.common.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.core.util.DrawableUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.FileUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive._unrefactored.core.util.interfaces.OnItemLongClickListener;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Api;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.DBHelper;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.dao.WatchingFileDAO;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.model.WatchingFile;
import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive._unrefactored.presentation.services.SyncService;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FilesAdapter extends RecyclerView.Adapter<ViewHolder> {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_EMPTY = 1;
    public static final int TYPE_HEADER = 2;

    private final FilesRepository mFileRepository = Api.getApiProvider().getFilesRepository();

    private List<AuroraFile> mFiles;
    private DBHelper mDB;
    private WatchingFileDAO mWatchingFileDAO;
    private boolean mShowEmptyView = false;
    private String mEmptyText = null;

    //Multichoise
    private boolean mMultichoiseMode = false;
    private ArrayList<AuroraFile> mSelectedItems;
    private MultichoiseListener mMultichoiseListener;

    private OnItemClickListener<AuroraFile> mOnItemClickListener;
    private OnItemLongClickListener<AuroraFile> mOnItemLongClickListener;

    private HashMap<String, FileViewHolder> mSyncableViewHolders = new HashMap<>();

    private BroadcastReceiver mSyncUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onSyncReceived(intent);
        }
    };

    private View mHeader;

    public FilesAdapter(List<AuroraFile> files, DBHelper db,
                        @Nullable OnItemClickListener<AuroraFile> onItemClickListener,
                        @Nullable OnItemLongClickListener<AuroraFile> onLongClickListener) {
        this(files, db, onItemClickListener, onLongClickListener, null);
    }

    public FilesAdapter(List<AuroraFile> files, DBHelper db,
                        @Nullable OnItemClickListener<AuroraFile> onItemClickListener,
                        @Nullable OnItemLongClickListener<AuroraFile> onLongClickListener,
                        @Nullable MultichoiseListener multichoiseListener) {
        mFiles = files;
        mDB = db;
        mWatchingFileDAO = mDB.getWatchingFileDAO();
        mOnItemClickListener = onItemClickListener;
        mOnItemLongClickListener = onLongClickListener;
        mMultichoiseListener = multichoiseListener;
    }

    public void startListenSync(Context ctx){
        ctx.registerReceiver(mSyncUpdateReceiver,
                new IntentFilter(SyncService.FileSyncAdapter.ACTION_SYNC_STATUS_CHANGED));
    }

    public void stopListenSync(Context ctx){
        ctx.unregisterReceiver(mSyncUpdateReceiver);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_EMPTY:
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_empty_folder, parent, false);
                return new TextViewHolder(v);
            case TYPE_HEADER:
                return new HeaderViewHolder(mHeader);
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_file, parent, false);
                return new FileViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof FileViewHolder){
            if (mHeader != null){
                position --;
            }
            bindItemViewHolder((FileViewHolder) holder, position);
        } else if (holder instanceof TextViewHolder){
            ((TextViewHolder) holder).text.setText(
                    mEmptyText
            );
        }
        //Else its HeaderViewHolder.
    }

    /**
     * Bind normal view type ViewHolder.
     */
    private void bindItemViewHolder(FileViewHolder holder, int position){
        Glide.clear(holder.icon);

        AuroraFile file = mFiles.get(position);
        holder.fileName.setText(file.getName());

        //[START Update listeners and background by multichoise mode]
        Implementer impl = mMultichoiseMode ? new MultichoiseImpl(file) : new SingleChoiseImpl(file);
        holder.itemView.setSelected(mMultichoiseMode && mSelectedItems.contains(file));
        holder.itemView.setOnClickListener(impl);
        holder.itemView.setOnLongClickListener(impl);
        //[END Update listeners and background by multichoise mode]

        //Clear previous syncable
        if (holder.syncId != null){
            mSyncableViewHolders.remove(holder.syncId);
        }
        holder.syncId = file.getFullPath();
        mSyncableViewHolders.put(WatchingFile.Spec.getRemoteUniqueSpec(file), holder);

        //[START Update status icon]
        if (file.isFolder()) {
            holder.status_icon.setImageResource(R.drawable.ic_next);
            holder.progress.setVisibility(View.GONE);
            holder.status_icon.setVisibility(View.VISIBLE);
        } else {
            WatchingFile watchingFile = getWatchingWile(file);
            if (watchingFile != null && watchingFile.getType() == WatchingFile.TYPE_OFFLINE) {
                updateHolderByWatchingFile(watchingFile, 0, 0, holder);
            } else {
                holder.progress.setVisibility(View.GONE);
                holder.status_icon.setVisibility(View.GONE);
            }
        }
        //[END Update status icon]

        //Image thumbnail target
        FileUtil.updateIcon(holder.icon, file, mFileRepository, holder.itemView.getContext());
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        if (holder instanceof FileViewHolder){
            String syncId = ((FileViewHolder) holder).syncId;
            if (syncId != null){
                mSyncableViewHolders.remove(syncId);
            }
        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        int count = mFiles == null ? 0 : mFiles.size();
        if (mShowEmptyView && count == 0){
            count++;
        }
        if (mHeader != null){
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int count = mFiles == null ? 0 : mFiles.size();
        boolean needShowEmpty = mShowEmptyView && count == 0;
        if (mHeader == null){
            if (needShowEmpty){
                return TYPE_EMPTY;
            } else {
                return TYPE_ITEM;
            }
        } else {
            if (position == 0){
                return TYPE_HEADER;
            } else {
                if (needShowEmpty){
                    return TYPE_EMPTY;
                } else {
                    return TYPE_ITEM;
                }
            }
        }
    }

    /**
     * Set empty view showing.
     * @param showEmptyView - if false empty view will not showed on empty array.
     */
    public void setShowEmptyView(boolean showEmptyView) {
        mShowEmptyView = showEmptyView;
        notifyDataSetChanged();
    }

    public void setHeader(View header) {
        mHeader = header;
        notifyDataSetChanged();
    }

    /**
     * Set text for empty view.
     */
    public void setEmptyText(String text){
        mEmptyText = text;
        if (mShowEmptyView){
            notifyDataSetChanged();
        }
    }

    public void setMultichoiseMode(boolean enabled){
        mMultichoiseMode = enabled;
        if (enabled) {
            mSelectedItems = new ArrayList<>();
        } else {
            mSelectedItems = null;
        }
        notifyDataSetChanged();
    }

    /**
     * Toggle file selection.
     * @param file - clicked file.
     */
    private void toggleSelection(AuroraFile file){
        boolean ready = true;

        WatchingFile watchingFile = getWatchingWile(file);
        if (watchingFile != null){
            //Check sync status
            ready = watchingFile.getSyncStatus() == WatchingFile.SYNCED;
        }

        if (ready){

            if (mSelectedItems.contains(file)){
                mSelectedItems.remove(file);
            } else {
                mSelectedItems.add(file);
            }

            if (mMultichoiseListener != null){
                List<AuroraFile> result = new ArrayList<>();
                result.addAll(mSelectedItems);
                mMultichoiseListener.onMultichoiseChanged(result);
            }
            notifyDataSetChanged();
        }
    }

    /**
     * Update sync state visibility.
     * @param target - current {@link WatchingFile} for holder
     * @param progress - sync progress
     * @param maxProgress - sync max progress
     * @param holder - current {@link FileViewHolder}
     */
    private void updateHolderByWatchingFile(WatchingFile target, int progress, int maxProgress,
                                            FileViewHolder holder){
        holder.status_icon.setVisibility(View.VISIBLE);
        if (target.getSyncStatus() == WatchingFile.SYNCED){
            if (target.getType() == WatchingFile.TYPE_OFFLINE) {
                holder.status_icon.setImageDrawable(
                        DrawableUtil.getTintedDrawable(
                                R.drawable.ic_offline,
                                R.color.colorAccent,
                                holder.itemView.getContext())
                );
            } else {
                holder.status_icon.setVisibility(View.GONE);
            }
            holder.progress.setVisibility(View.GONE);
        } else {
            holder.status_icon.setImageDrawable(
                    DrawableUtil.getTintedDrawable(
                            R.drawable.ic_sync,
                            R.color.colorAccent,
                            holder.itemView.getContext())
            );

            switch (target.getSyncStatus()){
                case WatchingFile.SYNC_IN_PROGRESS:
                    if (maxProgress > 0) {
                        holder.progress.setIndeterminate(false);
                        holder.progress.setMax(maxProgress);
                        holder.progress.setProgress(progress);
                        break;
                    } //else do not break because need indeterminate progress
                case WatchingFile.SYNC_WAITING_SYNC:
                    holder.progress.setIndeterminate(true);
                    holder.progress.setVisibility(View.VISIBLE);
                    break;
                default:
                    holder.progress.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Handle sync status change message.
     * @param data - message intent.
     */
    private void onSyncReceived(Intent data){
        WatchingFile target = data.getParcelableExtra(SyncService.FileSyncAdapter.KEY_SYNC_TARGET);
        int progress = data.getIntExtra(SyncService.FileSyncAdapter.KEY_PROGRESS, 0);
        int maxProgress = data.getIntExtra(SyncService.FileSyncAdapter.KEY_MAX_PROGRESS, 0);

        FileViewHolder holder = mSyncableViewHolders.get(target.getRemoteUniqueSpec());
        if (holder != null){
            updateHolderByWatchingFile(target, progress, maxProgress, holder);
        }
    }

    private WatchingFile getWatchingWile(AuroraFile file){
        if (mDB.isOpen()) {
            return mWatchingFileDAO.getWatching(file);
        }
        return null;
    }

    ////////////////////////////////////////////////
    // [START Classes] // <editor-fold desc="Classes">
    ////////////////////////////////////////////////

    /**
     * View holder for basic {@link AuroraFile} item.
     */
    protected class FileViewHolder extends ViewHolder {

        String syncId;

        TextView fileName;
        ImageView icon;
        ImageView status_icon;
        ProgressBar progress;

        public FileViewHolder(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.text);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            status_icon = (ImageView) itemView.findViewById(R.id.status_icon);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
        }


    }

    /**
     * View holder for header view.
     */
    protected class HeaderViewHolder extends ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * Text view holder for error view.
     */
    protected class TextViewHolder extends ViewHolder {

        TextView text;

        public TextViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }

    /**
     * Collected summary view interface.
     */
    private interface ViewInterface extends View.OnClickListener, View.OnLongClickListener{

    }

    /**
     * Implementer of required view interfaces.
     */
    private abstract class Implementer implements ViewInterface {
        protected AuroraFile mFile;

        public Implementer(AuroraFile file) {
            mFile = file;
        }
    }

    /**
     * Implementer for multichoise mode.
     */
    private class MultichoiseImpl extends Implementer {

        public MultichoiseImpl(AuroraFile file) {
            super(file);
        }

        @Override
        public void onClick(View v) {
            toggleSelection(mFile);
        }

        @Override
        public boolean onLongClick(View v) {
            toggleSelection(mFile);
            return true;
        }
    }

    /**
     * Implementer for single choise mode.
     */
    private class SingleChoiseImpl extends Implementer {

        public SingleChoiseImpl(AuroraFile file) {
            super(file);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(mFile);
        }

        @Override
        public boolean onLongClick(View v) {
            return mOnItemLongClickListener != null &&
                    mOnItemLongClickListener.onItemLongClick(mFile);
        }
    }

    /**
     * Multi choise change listener.
     */
    public interface MultichoiseListener {
        void onMultichoiseChanged(List<AuroraFile> selected);
    }

    ////////////////////////////////////////////////
    // [END Classes] // </editor-fold>
    ////////////////////////////////////////////////
}
