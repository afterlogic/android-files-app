package com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Storage;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.BaseViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.BaseFilesModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter.FilesPresenter;
import com.annimon.stream.Stream;

import java.util.List;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BaseFilesBiModel extends BaseViewModel implements BaseFilesModel, BaseFilesViewModel {

    private final OptWeakRef<? extends FilesPresenter> mPresenter;

    private final ObservableField<String> mFolderTitle = new ObservableField<>();
    private final ObservableBoolean mRefreshing = new ObservableBoolean(true);
    private final ObservableBoolean mLocked = new ObservableBoolean(false);
    private final ObservableInt mCurrentPosition = new ObservableInt(-1);
    private final ObservableList<Storage> mStorages = new ObservableArrayList<>();
    private final ObservableBoolean mErrorState = new ObservableBoolean();

    private String mCurrentFileType = null;

    public BaseFilesBiModel(OptWeakRef<? extends FilesPresenter> presenter) {
        mPresenter = presenter;
    }

    @Override
    public ObservableList<Storage> getStorages() {
        return mStorages;
    }

    @Override
    public BaseFilesModel getModel(){
        return this;
    }

    @Override
    public ObservableBoolean getRefreshing() {
        return mRefreshing;
    }

    @Override
    public ObservableField<String> getFolderTitle() {
        return mFolderTitle;
    }

    @Override
    public ObservableBoolean getLocked(){
        return mLocked;
    }

    @Override
    public ObservableInt getCurrentPagePosition(){
        return mCurrentPosition;
    }

    @Override
    public ObservableBoolean getErrorState() {
        return mErrorState;
    }

    @Override
    public void onCurrentFolderChanged(AuroraFile folder) {
        //no-op stub
        MyLog.majorException(new Error("TODO Stub triggered."));
    }

    @Override
    public void setFileTypes(List<Storage> types) {
        mStorages.clear();
        mStorages.addAll(types);

        updateCurrentPosition();
        updateLocked();
    }

    @Override
    public void setCurrentFolder(AuroraFile folder) {
        if (folder == null || "".equals(folder.getFullPath())){
            mCurrentFileType = null;
            mFolderTitle.set(null);
        } else {
            mCurrentFileType = folder.getType();
            mFolderTitle.set(folder.getName());
        }
        updateCurrentPosition();
        updateLocked();
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        mRefreshing.set(refreshing);
    }

    @Override
    public void setErrorState(boolean errorState) {
        mErrorState.set(errorState);
    }

    private void updateCurrentPosition(){
        int position = Stream.of(mStorages)
                .filter(type -> type.getFiles().equals(mCurrentFileType))
                .map(mStorages::indexOf)
                .findFirst()
                .orElse(-1);
        mCurrentPosition.set(position);
    }

    private void updateLocked(){
        mLocked.set(mCurrentPosition.get() != -1);
    }

    @Override
    public void onRefresh() {
        mPresenter.ifPresent(FilesPresenter::refresh);
    }
}
