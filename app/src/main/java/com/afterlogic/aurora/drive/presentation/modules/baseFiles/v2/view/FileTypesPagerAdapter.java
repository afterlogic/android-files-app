package com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.view;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.afterlogic.aurora.drive.model.Storage;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;

import java.util.List;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public abstract class FileTypesPagerAdapter extends FragmentPagerAdapter implements ItemsAdapter<Storage> {

    private List<Storage> storages;

    public FileTypesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return storages == null ? 0 : storages.size();
    }
    @Override
    public void setItems(List<Storage> items) {
        storages = items;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return getFilesListFragment(storages.get(position).getType());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return storages.get(position).getCaption();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    protected abstract Fragment getFilesListFragment(String type);
}
