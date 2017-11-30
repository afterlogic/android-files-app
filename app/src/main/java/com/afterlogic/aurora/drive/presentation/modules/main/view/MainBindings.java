package com.afterlogic.aurora.drive.presentation.modules.main.view;

import android.databinding.BindingAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.model.Storage;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFileViewModel;
import com.android.databinding.library.baseAdapters.BR;
import com.github.nitrico.lastadapter.LastAdapter;

import java.util.List;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainBindings {

    @BindingAdapter("main_filesAdapter")
    public static void bindFilesAdapter(RecyclerView list, List<MainFileViewModel> items) {
        new LastAdapter(items, BR.vm)
                .map(MainFileViewModel.class, R.layout.main_item_list_file)
                .into(list);
    }

    @BindingAdapter("main_fileTypesAdapter")
    public static void bindFileTypesAdapter(ViewPager pager, List<Storage> storages) {
        FragmentManager fm = ((FragmentActivity) pager.getContext()).getSupportFragmentManager();
        MainFileTypesPagerAdapter adapter = (MainFileTypesPagerAdapter) pager.getAdapter();
        if (adapter == null) {
            adapter = new MainFileTypesPagerAdapter(fm);
            pager.setAdapter(adapter);
        }
        adapter.setItems(storages);
    }
}
