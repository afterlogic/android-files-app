package com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
public class FileViewBiModel implements FileViewViewModel, FileViewModel {

    private final ObservableInt mCurrentPosition = new ObservableInt();
    private final ObservableList<AuroraFile> mItems = new ObservableArrayList<>();
    private final ObservableField<String> mTitle = new ObservableField<>();
    private final ObservableBoolean mFullscreenMode = new ObservableBoolean();

    @Inject FileViewBiModel() {
        mCurrentPosition.addOnPropertyChangedCallback(new SimpleListener(() -> {
            int current = mCurrentPosition.get();
            if (current != -1 && mItems.size() > 0 && current < mItems.size()) {
                mTitle.set(mItems.get(current).getName());
            } else {
                mTitle.set(null);
            }
        }));
    }

    @Override
    public void setItems(List<AuroraFile> fileList) {
        mItems.clear();
        mItems.addAll(fileList);
    }

    @Override
    public AuroraFile getCurrent() {
        return mItems.get(mCurrentPosition.get());
    }

    @Override
    public void viewCreatedWith(@Nullable AuroraFile target, List<AuroraFile> files) {
        setItems(files);

        int position = 0;
        if (target != null){
            position = Stream.of(files)
                    .filter(file -> target.getPathSpec().equals(file.getPathSpec()))
                    .findFirst()
                    .map(files::indexOf)
                    .orElse(0);
        }
        mCurrentPosition.set(position);
    }

    @Override
    public ObservableList<AuroraFile> getItems() {
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
        mCurrentPosition.set(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
