package com.afterlogic.aurora.drive.presentation.modules.fileView.view;

import androidx.databinding.ObservableList;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.SimpleOnObservableListChangedListener;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.WeakObservableListListener;
import com.afterlogic.aurora.drive.presentation.common.components.view.FragmentRecreatePagerAdapter;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewImageItemViewModel;

import java.util.List;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

class FilesItemsAdapter extends FragmentRecreatePagerAdapter implements ItemsAdapter<FileViewImageItemViewModel>{

    private List<FileViewImageItemViewModel> mItems;

    private final SimpleOnObservableListChangedListener<FileViewImageItemViewModel> mObservableListener = new SimpleOnObservableListChangedListener<>(
            this::notifyDataSetChanged
    );
    private final WeakObservableListListener<FileViewImageItemViewModel> mWeak = new WeakObservableListListener<>(mObservableListener);

    public FilesItemsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void setItems(List<FileViewImageItemViewModel> items) {
        if (mItems == items) return;

        if (mItems != null && mItems instanceof ObservableList){
            ObservableList<FileViewImageItemViewModel> observableList = (ObservableList<FileViewImageItemViewModel>) mItems;
            observableList.removeOnListChangedCallback(mWeak);
        }

        mItems = items;

        if (mItems != null && mItems instanceof ObservableList){
            ObservableList<FileViewImageItemViewModel> observableList = (ObservableList<FileViewImageItemViewModel>) mItems;
            observableList.addOnListChangedCallback(mWeak);
        }
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return new FileViewImageItemFragment();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        FileViewImageItemFragment fragment = (FileViewImageItemFragment) super.instantiateItem(container, position);
        fragment.setViewModel(mItems.get(position));
        return fragment;
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
