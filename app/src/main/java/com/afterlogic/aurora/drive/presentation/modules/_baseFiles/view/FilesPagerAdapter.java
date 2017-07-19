package com.afterlogic.aurora.drive.presentation.modules._baseFiles.view;

import android.content.Context;
import android.databinding.ObservableList;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.SimpleOnObservableListChangedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FilesPagerAdapter extends FragmentStatePagerAdapter implements ItemsAdapter<FileType>{

    private final FilesContentProvider mFilesContentProvider;

    private List<FileType> mFileTypes;

    private BaseFilesListFragment mPrimaryFragment = null;

    private SparseArray<UUID> mFilesModuleIds = new SparseArray<>();

    private int mDataStateIndex = 0;

    private ObservableList.OnListChangedCallback<ObservableList<FileType>> mOnListChangedCallback = new SimpleOnObservableListChangedListener<>(
            this::notifyDataSetChanged
    );

    private Context mContext;

    public FilesPagerAdapter(FragmentManager fm, FilesContentProvider filesContentProvider, Context ctx) {
        super(fm);
        mFilesContentProvider = filesContentProvider;
        mContext = ctx;
    }

    @Override
    public void setItems(List<FileType> items) {
        if (mFileTypes == items) return;

        if (mFileTypes != null && mFileTypes instanceof ObservableList){
            ObservableList<FileType> observable = (ObservableList<FileType>) mFileTypes;
            observable.removeOnListChangedCallback(mOnListChangedCallback);
        }

        if (items != null) {
            /*
            TODO reverse pages
            if (RtlUtil.isRtl(mContext)) {
                mFileTypes = new ArrayList<>();
                Stream.of(items).forEach(it -> mFileTypes.add(0, it));
            } else {
                mFileTypes = new ArrayList<>(items);
            }*/

            mFileTypes = new ArrayList<>(items);
        } else {
            mFileTypes = null;
        }

        if (mFileTypes != null && mFileTypes instanceof ObservableList){
            ObservableList<FileType> observable = (ObservableList<FileType>) mFileTypes;
            observable.addOnListChangedCallback(mOnListChangedCallback);
        }
        notifyDataSetChanged();
    }

    @Override
    public BaseFilesListFragment getItem(int position) {
        BaseFilesListFragment fragment = mFilesContentProvider.get(mFileTypes.get(position).getFilesType());
        fragment.setArgsType(mFileTypes.get(position).getFilesType());
        fragment.setModuleUuid(mFilesModuleIds.get(position));

        int currentDataStateIndex = mDataStateIndex;
        fragment.setFirstCreateInterceptor(view -> {
            if (currentDataStateIndex != mDataStateIndex) return;
            mFilesModuleIds.put(position, view.getModuleUuid());
        });

        return fragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mPrimaryFragment = (BaseFilesListFragment) object;
    }

    @Override
    public int getCount() {
        return mFileTypes == null ? 0 : mFileTypes.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFileTypes.get(position).getCaption();
    }

    @Nullable
    public BaseFilesListFragment getPrimaryFragment() {
        return mPrimaryFragment;
    }

    @Override
    public void notifyDataSetChanged() {
        mDataStateIndex++;
        mFilesModuleIds.clear();
        super.notifyDataSetChanged();
    }

    interface FilesContentProvider{
        BaseFilesListFragment get(String type);
    }
}
