package com.afterlogic.aurora.drive.presentation.modules.replace.view;

import androidx.databinding.BindingAdapter;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.model.Storage;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceFileViewModel;
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
    public static void bindFileTypesAdapter(ViewPager pager, List<Storage> storages) {
        FragmentManager fm = ((FragmentActivity) pager.getContext()).getSupportFragmentManager();
        ReplaceFileTypesPagerAdapter adapter = (ReplaceFileTypesPagerAdapter) pager.getAdapter();
        if (adapter == null) {
            adapter = new ReplaceFileTypesPagerAdapter(fm);
            pager.setAdapter(adapter);
        }
        adapter.setItems(storages);
    }
}
