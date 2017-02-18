package com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.streams.ExtCollectors;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.afterlogic.aurora.drive.presentation.modules.fileView.presenter.FileViewModel;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
public class FileViewBiModel implements FileViewViewModel, FileViewModel {

    private final ObservableInt mCurrentPosition = new ObservableInt(-1);
    private final ObservableField<String> mTitle = new ObservableField<>();
    private final ObservableBoolean mFullscreenMode = new ObservableBoolean();

    private final Provider<FileViewImageItemBiModel> mItemsProvider;
    private final ObservableList<FileViewImageItemViewModel> mItems = new ObservableArrayList<>();

    private int mUntrackedCurrentPosition;

    @Inject FileViewBiModel(Provider<FileViewImageItemBiModel> itemsProvider) {
        mItemsProvider = itemsProvider;
        mCurrentPosition.addOnPropertyChangedCallback(new SimpleListener(this::updateTitleByCurrent));
    }

    @Override
    public void setItems(List<AuroraFile> fileList) {
        Stream.of(fileList)
                .map(file -> {
                    FileViewImageItemBiModel model = mItemsProvider.get();
                    model.setAuroraFile(file);
                    return model;
                })
                .collect(ExtCollectors.set(mItems));
    }

    @Override
    public AuroraFile getCurrent() {
        return getItemModel(mCurrentPosition.get()).getFile();
    }

    @Override
    public void viewCreatedWith(@Nullable AuroraFile target, List<AuroraFile> files) {
        setItems(files);

        int position = -1;
        if (target != null){
            position = Stream.of(files)
                    .filter(file -> target.getPathSpec().equals(file.getPathSpec()))
                    .findFirst()
                    .map(files::indexOf)
                    .orElse(-1);
        }
        mCurrentPosition.set(position);
        updateTitleByCurrent();
    }

    @Override
    public ObservableList<FileViewImageItemViewModel> getItems() {
        return mItems;
    }

    @Override
    public ObservableField<String> getTitle() {
        return mTitle;
    }

    @Override
    public ObservableInt getCurrentPosition() {
        return mCurrentPosition;
    }

    @Override
    public ObservableBoolean getFullscreenMode() {
        return mFullscreenMode;
    }

    @Override
    public void onItemClick() {
        mFullscreenMode.set(!mFullscreenMode.get());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mUntrackedCurrentPosition = position;

        mCurrentPosition.set(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE){
            mCurrentPosition.set(mUntrackedCurrentPosition);
        }
    }

    private FileViewImageItemBiModel getItemModel(int position){
        return (FileViewImageItemBiModel) mItems.get(position);
    }

    private void updateTitleByCurrent(){
        int current = mCurrentPosition.get();
        if (current >= 0 && mItems.size() > 0 && current < mItems.size()) {
            FileViewImageItemBiModel model = getItemModel(current);
            AuroraFile file = model.getFile();
            mTitle.set(file.getName());
        } else {
            mTitle.set(null);
        }
    }
}
