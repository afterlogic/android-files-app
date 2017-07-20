package com.afterlogic.aurora.drive.presentation.modules.upload.v2.view;

import android.databinding.BindingAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.afterlogic.aurora.drive.model.FileType;

import java.util.List;

/**
 * Created by aleksandrcikin on 19.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadBindings {

    @BindingAdapter("upload_fileTypesAdapter")
    public static void bindUploadFileTypesAdapter(ViewPager pager, List<FileType> fileTypes) {
        FragmentManager fm = ((FragmentActivity) pager.getContext()).getSupportFragmentManager();
        UploadFileTypesAdapter adapter = (UploadFileTypesAdapter) pager.getAdapter();
        if (adapter == null) {
            adapter = new UploadFileTypesAdapter(fm);
            pager.setAdapter(adapter);
        }
        adapter.setItems(fileTypes);
    }
}
