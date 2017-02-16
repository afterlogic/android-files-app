package com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.Uri;
import android.view.View;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.SubModuleScope;
import com.afterlogic.aurora.drive.model.AuroraFile;

import javax.inject.Inject;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@SubModuleScope
public class FileViewImageItemBiModel implements FileViewImageItemViewModel, FileViewImageItemModel {

    private final ObservableBoolean mProgress = new ObservableBoolean(true);
    private final ObservableBoolean mError = new ObservableBoolean(false);
    private final ObservableField<Uri> mImageContent = new ObservableField<>();

    private AuroraFile mFile;
    //private final FileViewViewModel mRootViewModel;

    @Inject FileViewImageItemBiModel() {

    }

    @Override
    public void viewCreatedWith(AuroraFile file) {
        mFile = file;
    }

    @Override
    public ObservableField<Uri> getImageContent() {
        return mImageContent;
    }

    @Override
    public ObservableBoolean getProgress() {
        return mProgress;
    }

    @Override
    public ObservableBoolean getError() {
        return mError;
    }

    @Override
    public ObservableBoolean getFullscreen() {
        return new ObservableBoolean();
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void setProgress(boolean progress) {
        mError.set(false);
        mProgress.set(progress);
    }

    @Override
    public void setUri(Uri uri) {
        mImageContent.set(uri);
    }

    @Override
    public void setError() {
        mError.set(true);
    }

    @Override
    public AuroraFile getFile() {
        return mFile;
    }
}
