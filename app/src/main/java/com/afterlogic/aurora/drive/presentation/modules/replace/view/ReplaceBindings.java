package com.afterlogic.aurora.drive.presentation.modules.replace.view;

import android.databinding.BindingAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceFileViewModel;
import com.android.databinding.library.baseAdapters.BR;
import com.github.nitrico.lastadapter.LastAdapter;

import java.util.List;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceBindings {

    @BindingAdapter("replace_filesAdapter")
    public static void bindFilesAdapter(RecyclerView list, List<ReplaceFileViewModel> items) {
        new LastAdapter(items, BR.vm)
                .map(ReplaceFileViewModel.class, R.layout.replace_item_list_file)
                .into(list);
    }

    @BindingAdapter("replace_fileTypesAdapter")
    public static void bindFileTypesAdapter(ViewPager pager, List<FileType> fileTypes) {
        FragmentManager fm = ((FragmentActivity) pager.getContext()).getSupportFragmentManager();
        FileTypesPagerAdapter adapter = (FileTypesPagerAdapter) pager.getAdapter();
        if (adapter == null) {
            adapter = new FileTypesPagerAdapter(fm);
            pager.setAdapter(adapter);
        }
        adapter.setItems(fileTypes);
    }
}
