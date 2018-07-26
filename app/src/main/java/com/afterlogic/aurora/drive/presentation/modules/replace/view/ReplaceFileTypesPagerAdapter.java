package com.afterlogic.aurora.drive.presentation.modules.replace.view;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.view.FileTypesPagerAdapter;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

class ReplaceFileTypesPagerAdapter extends FileTypesPagerAdapter {

    ReplaceFileTypesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    protected Fragment getFilesListFragment(String type) {
        return ReplaceFileTypeFragment.newInstance(type);
    }
}
