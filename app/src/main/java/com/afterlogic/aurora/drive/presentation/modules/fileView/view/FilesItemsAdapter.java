package com.afterlogic.aurora.drive.presentation.modules.fileView.view;

import android.databinding.ObservableList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.SimpleOnObservableListChagnedListener;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.WeakObservableListListener;
import com.afterlogic.aurora.drive.presentation.common.components.view.FragmentRecreatePagerAdapter;

import java.util.List;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

class FilesItemsAdapter extends FragmentRecreatePagerAdapter implements ItemsAdapter<AuroraFile>{

    private List<AuroraFile> mItems;

    private final SimpleOnObservableListChagnedListener<AuroraFile> mObservableListener = new SimpleOnObservableListChagnedListener<>(
            this::notifyDataSetChanged
    );
    private final WeakObservableListListener<AuroraFile> mWeak = new WeakObservableListListener<>(mObservableListener);

    public FilesItemsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void setItems(List<AuroraFile> items) {
        if (mItems != null && mItems instanceof ObservableList){
            ObservableList<AuroraFile> observableList = (ObservableList<AuroraFile>) mItems;
            observableList.removeOnListChangedCallback(mWeak);
        }

        mItems = items;

        if (mItems != null && mItems instanceof ObservableList){
            ObservableList<AuroraFile> observableList = (ObservableList<AuroraFile>) mItems;
            observableList.addOnListChangedCallback(mWeak);
        }
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return FileViewImageItemFragment.newInstance(mItems.get(position));
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
