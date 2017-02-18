package com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.Uri;
import android.view.View;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;
import com.afterlogic.aurora.drive.presentation.common.util.LazyProvider;
import com.afterlogic.aurora.drive.presentation.modules.fileView.presenter.FileViewImageItemModel;
import com.afterlogic.aurora.drive.presentation.modules.fileView.presenter.FileViewImageItemPresenter;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;

import javax.inject.Inject;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class FileViewImageItemBiModel implements FileViewImageItemViewModel, FileViewImageItemModel {

    private final ObservableBoolean mProgress = new ObservableBoolean(true);
    private final ObservableBoolean mError = new ObservableBoolean(false);
    private final ObservableField<Uri> mImageContent = new ObservableField<>();

    private AuroraFile mFile;
    private final FileViewViewModel mRootViewModel;

    private final LazyProvider<FileViewImageItemPresenter> mPresenter;

    @Inject FileViewImageItemBiModel(FileViewViewModel viewModel, LazyProvider<FileViewImageItemPresenter> presenter) {
        mRootViewModel = viewModel;
        mPresenter = presenter;
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
        return mRootViewModel.getFullscreenMode();
    }

    @Override
    public void onViewStart() {
        if (mImageContent.get() == null) {
            mPresenter.init();
        }

        mPresenter.ifPresent(presenter -> {
            presenter.setModel(this);
            presenter.onStart();
        });
    }

    @Override
    public void onViewStop() {
        mPresenter.ifPresent(Stoppable::onStop);
    }

    @Override
    public void onClick(View view) {
        mRootViewModel.onItemClick();
    }

    @Override
    public void setUri(Uri uri) {
        mImageContent.set(uri);
        mPresenter.reset();
    }

    @Override
    public void setError() {
        mError.set(true);
        mPresenter.reset();
    }

    @Override
    public void setAuroraFile(AuroraFile file) {
        mFile = file;
    }

    @Override
    public AuroraFile getFile() {
        return mFile;
    }

    @Override
    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
        mError.set(true);
        mProgress.set(false);
        return false;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        mProgress.set(false);
        return false;
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        mRootViewModel.onItemClick();
    }
}
