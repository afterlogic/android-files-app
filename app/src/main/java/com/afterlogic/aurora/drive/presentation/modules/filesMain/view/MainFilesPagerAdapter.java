package com.afterlogic.aurora.drive.presentation.modules.filesMain.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.common.components.view.FragmentRecreatePagerAdapter;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel.FileType;

import java.util.List;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesPagerAdapter extends FragmentRecreatePagerAdapter implements ItemsAdapter<FileType>{

    public MainFilesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void setItems(List<FileType> items) {

    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
