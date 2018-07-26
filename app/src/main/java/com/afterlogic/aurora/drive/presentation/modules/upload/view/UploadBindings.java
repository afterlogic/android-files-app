package com.afterlogic.aurora.drive.presentation.modules.upload.view;

import androidx.databinding.BindingAdapter;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.model.Storage;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadFileViewModel;
import androidx.databinding.library.baseAdapters.BR;
import com.github.nitrico.lastadapter.LastAdapter;

import java.util.List;

/**
 * Created by aleksandrcikin on 19.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadBindings {

    @BindingAdapter("upload_fileTypesAdapter")
    public static void bindUploadFileTypesAdapter(ViewPager pager, List<Storage> storages) {
        FragmentManager fm = ((FragmentActivity) pager.getContext()).getSupportFragmentManager();
        UploadFileTypesAdapter adapter = (UploadFileTypesAdapter) pager.getAdapter();
        if (adapter == null) {
            adapter = new UploadFileTypesAdapter(fm);
            pager.setAdapter(adapter);
        }
        adapter.setItems(storages);
    }


    @BindingAdapter("upload_filesAdapter")
    public static void bindFilesAdapter(RecyclerView list, List<UploadFileViewModel> items) {
        new LastAdapter(items, BR.vm)
                .map(UploadFileViewModel.class, R.layout.upload_item_list_file)
                .into(list);
    }
}
