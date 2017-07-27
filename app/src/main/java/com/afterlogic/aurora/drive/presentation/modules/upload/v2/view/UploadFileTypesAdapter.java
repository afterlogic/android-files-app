package com.afterlogic.aurora.drive.presentation.modules.upload.v2.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.view.FileTypesPagerAdapter;

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
