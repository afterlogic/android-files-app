package com.afterlogic.aurora.drive.presentation.modules.upload.view;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.view.FileTypesPagerAdapter;

/**
 * Created by aleksandrcikin on 19.07.17.
 * mail: mail@sunnydaydev.me
 */

class UploadFileTypesAdapter extends FileTypesPagerAdapter {

    UploadFileTypesAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    protected Fragment getFilesListFragment(String type) {
        MyLog.d("Instantiate fragment for type: " + type);
        return UploadFilesListFragment.newInstance(type);
    }
}
