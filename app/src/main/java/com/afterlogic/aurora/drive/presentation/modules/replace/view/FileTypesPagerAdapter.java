package com.afterlogic.aurora.drive.presentation.modules.replace.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;

import java.util.List;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

class FileTypesPagerAdapter extends FragmentPagerAdapter implements ItemsAdapter<FileType> {

    private List<FileType> fileTypes;

    public FileTypesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return fileTypes == null ? 0 : fileTypes.size();
    }
    @Override
    public void setItems(List<FileType> items) {
        fileTypes = items;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return ReplaceFileTypeFragment.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fileTypes.get(position).getCaption();
    }
}
