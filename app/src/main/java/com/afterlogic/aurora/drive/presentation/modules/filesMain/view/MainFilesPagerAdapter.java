package com.afterlogic.aurora.drive.presentation.modules.filesMain.view;

import android.databinding.ObservableList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.afterlogic.aurora.drive._unrefactored.presentation.ui.fragments.FilesListFragment;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.SimpleOnObservableListChagnedListener;
import com.afterlogic.aurora.drive.presentation.common.components.view.FragmentRecreatePagerAdapter;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel.FileType;

import java.util.List;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesPagerAdapter extends FragmentRecreatePagerAdapter implements ItemsAdapter<FileType>{

    private List<FileType> mFileTypes;

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
    public Fragment getItem(int position) {
        FilesListFragment fragment = FilesListFragment.newInstance(mFileTypes.get(position).getFilesType());
        fragment.refreshCurrentFolder();
        return fragment;
    }

    @Override
    public int getCount() {
        return mFileTypes == null ? 0 : mFileTypes.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFileTypes.get(position).getCaption();
    }
}
