package com.afterlogic.aurora.drive.presentation.modules.filesMain.view;

import android.databinding.ObservableList;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.SimpleOnObservableListChagnedListener;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListFragment;

import java.util.List;
import java.util.UUID;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesPagerAdapter extends FragmentStatePagerAdapter implements ItemsAdapter<FileType> {

    private List<FileType> mFileTypes;

    private FileListFragment mPrimaryFragment = null;

    private SparseArray<UUID> mFilesModuleIds = new SparseArray<>();

    private int mDataStateIndex = 0;

    private ObservableList.OnListChangedCallback<ObservableList<FileType>> mOnListChangedCallback = new SimpleOnObservableListChagnedListener<>(
            this::notifyDataSetChanged
    );

    public MainFilesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void setItems(List<FileType> items) {
        if (mFileTypes == items) return;

        if (mFileTypes != null && mFileTypes instanceof ObservableList){
            ObservableList<FileType> observable = (ObservableList<FileType>) mFileTypes;
            observable.removeOnListChangedCallback(mOnListChangedCallback);
        }

        mFileTypes = items;

        if (mFileTypes != null && mFileTypes instanceof ObservableList){
            ObservableList<FileType> observable = (ObservableList<FileType>) mFileTypes;
            observable.addOnListChangedCallback(mOnListChangedCallback);
        }
        notifyDataSetChanged();
    }

    @Override
    public FileListFragment getItem(int position) {
        FileListFragment fragment = FileListFragment.newInstance(mFileTypes.get(position).getFilesType());
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
        mPrimaryFragment = (FileListFragment) object;
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
    public FileListFragment getPrimaryFragment() {
        return mPrimaryFragment;
    }

    @Override
    public void notifyDataSetChanged() {
        mDataStateIndex++;
        mFilesModuleIds.clear();
        super.notifyDataSetChanged();
    }
}
