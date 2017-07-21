package com.afterlogic.aurora.drive.presentation.modules.upload.v2.view;

import android.databinding.BindingAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.modules.upload.v2.viewModel.UploadFileViewModel;
import com.android.databinding.library.baseAdapters.BR;
import com.github.nitrico.lastadapter.LastAdapter;

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


    @BindingAdapter("upload_filesAdapter")
    public static void bindFilesAdapter(RecyclerView list, List<UploadFileViewModel> items) {
        new LastAdapter(items, BR.vm)
                .map(UploadFileViewModel.class, R.layout.upload_item_list_file)
                .into(list);
    }
}
