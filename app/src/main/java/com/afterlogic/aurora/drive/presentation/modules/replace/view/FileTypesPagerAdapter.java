package com.afterlogic.aurora.drive.presentation.modules.replace.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceFileTypeViewModel;

import java.util.List;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

class FileTypesPagerAdapter extends FragmentPagerAdapter implements ItemsAdapter<ReplaceFileTypeViewModel> {

    private List<ReplaceFileTypeViewModel> fileTypes;

    public FileTypesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return fileTypes == null ? 0 : fileTypes.size();
    }
    @Override
    public void setItems(List<ReplaceFileTypeViewModel> items) {
        fileTypes = items;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return ReplaceFileTypeFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fileTypes.get(position).fileType.getCaption();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
