package com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileType;
import com.annimon.stream.Stream;

import java.util.List;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BaseFilesBiModel implements BaseFilesModel, BaseFilesViewModel {

    private final ObservableField<String> mFolderTitle = new ObservableField<>();
    private final ObservableBoolean mRefreshing = new ObservableBoolean(true);
    private final ObservableBoolean mLocked = new ObservableBoolean(false);
    private final ObservableInt mCurrentPosition = new ObservableInt(-1);
    private final ObservableList<FileType> mFileTypes = new ObservableArrayList<>();

    private String mCurrentFileType = null;

    @Override
    public ObservableList<FileType> getFileTypes() {
        return mFileTypes;
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
    public void setFileTypes(List<FileType> types) {
        mFileTypes.clear();
        mFileTypes.addAll(types);

        mRefreshing.set(false);

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

    private void updateCurrentPosition(){
        int position = Stream.of(mFileTypes)
                .filter(type -> type.getFilesType().equals(mCurrentFileType))
                .map(mFileTypes::indexOf)
                .findFirst()
                .orElse(-1);
        mCurrentPosition.set(position);
    }

    private void updateLocked(){
        mLocked.set(mCurrentPosition.get() != -1);
    }
}
