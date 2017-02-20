package com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.Uri;
import android.view.View;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.BaseFileItemModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter.FilesListPresenter;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BaseFileItemBiModel implements BaseFileItemViewModel, BaseFileItemModel {

    private final OptWeakRef<? extends FilesListPresenter> mPresenter;
    private final AppResources mAppResources;

    private final ObservableField<String> mFileName = new ObservableField<>();
    private final ObservableField<Uri> mFileIcon = new ObservableField<>();
    private final ObservableField<Uri> mStatusIcon = new ObservableField<>();
    private final ObservableBoolean mIsFolder = new ObservableBoolean();

    private AuroraFile mAuroraFile;

    public BaseFileItemBiModel(OptWeakRef<? extends FilesListPresenter> presenter, AppResources appResources) {
        mPresenter = presenter;
        mAppResources = appResources;
    }

    public BaseFileItemBiModel(OptWeakRef<? extends FilesListPresenter> presenter, AppResources appResources, AuroraFile auroraFile) {
        mPresenter = presenter;
        mAppResources = appResources;

        setAuroraFile(auroraFile);
    }

    @Override
    public void setThumbNail(Uri thumb) {
        Uri result;
        if (mAuroraFile.isFolder()) {
            result = mAppResources.getResourceUri(R.drawable.ic_folder);
        } else {
            if (thumb != null) {
                result = thumb;
            } else {
                int fileIconRes = FileUtil.getFileIconRes(mAuroraFile);
                result = mAppResources.getResourceUri(fileIconRes);
            }
        }
        mFileIcon.set(result);
    }

    @Override
    public void setAuroraFile(AuroraFile file) {
        if (file == mAuroraFile) return;

        mAuroraFile = file;
        mFileName.set(mAuroraFile.getName());
        setThumbNail(null);
        setStatusIcon(null);
        mIsFolder.set(file.isFolder());
    }

    @Override
    public AuroraFile getFile() {
        return mAuroraFile;
    }

    @Override
    public ObservableField<String> getFileName() {
        return mFileName;
    }

    @Override
    public ObservableField<Uri> getFileIcon() {
        return mFileIcon;
    }

    @Override
    public ObservableField<Uri> getStatusIcon() {
        return mStatusIcon;
    }

    @Override
    public BaseFileItemModel getModel() {
        return this;
    }

    @Override
    public ObservableBoolean getIsFolder() {
        return mIsFolder;
    }

    @Override
    public void onClick(View view) {
        mPresenter.ifPresent(presenter -> presenter.onFileClick(mAuroraFile));
    }

    @Override
    public boolean onLongClick(View view) {
        mPresenter.ifPresent(presenter -> presenter.onFileLongClick(mAuroraFile));
        return mPresenter.isPresent();
    }

    private void setStatusIcon(Uri uri){
        if (mAuroraFile.isFolder()) {
            mStatusIcon.set(mAppResources.getResourceUri(R.drawable.ic_next));
        } else {
            mStatusIcon.set(uri);
        }
    }
}
