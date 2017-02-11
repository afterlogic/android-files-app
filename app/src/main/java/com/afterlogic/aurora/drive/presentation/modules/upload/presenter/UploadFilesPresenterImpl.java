package com.afterlogic.aurora.drive.presentation.modules.upload.presenter;

import android.content.Context;
import android.net.Uri;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.error.FileAlreadyExistError;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.presenter.BaseFilesListPresenter;
import com.afterlogic.aurora.drive.presentation.modules.upload.interactor.UploadFilesInteractor;
import com.afterlogic.aurora.drive.presentation.modules.upload.router.UploadFilesRouter;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadFilesView;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadFilesModel;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadFilesPresenterImpl extends BaseFilesListPresenter<UploadFilesView> implements UploadFilesPresenter {

    private final UploadFilesInteractor mInteractor;
    private final UploadFilesModel mModel;
    private final AppResources mAppResources;
    private final UploadFilesRouter mRouter;

    @Inject
    UploadFilesPresenterImpl(ViewState<UploadFilesView> viewState, UploadFilesInteractor interactor, UploadFilesModel model, Context appContext, AppResources appResources, UploadFilesRouter router) {
        super(viewState, interactor, model, appContext);
        mInteractor = interactor;
        mModel = model;
        mAppResources = appResources;
        mRouter = router;
    }

    @Override
    public void onCreateFolder() {
        getView().showNewFolderNameDialog(name -> mInteractor.createFolder(getCurrentFolder(), name)
                .doOnSubscribe(disposable -> getView().showProgress("Folder creation:", name))
                .doFinally(() -> getView().hideProgress())
                .subscribe(
                        file -> {
                            mModel.addFile(file);
                            onFileClick(file);
                        },
                        this::onErrorObtained
                ));
    }

    @Override
    public void onUpload(List<Uri> sources) {
        Stream.of(sources)
                .map(uri -> mInteractor.uploadFile(getCurrentFolder(), uri)
                        .doOnError(this::onUploadError)
                        .onErrorResumeNext(Observable.empty())
                )
                .collect(Observables.Collectors.concatObservables())
                .compose(this::progressibleLoadTask)
                .subscribe(
                        uploadedFile -> {
                            mModel.addFile(uploadedFile);
                            updateFileThumb(uploadedFile).subscribe();
                        },
                        this::onUploadError,
                        mRouter::closeCurrent
                );
    }

    private void onUploadError(Throwable error){
        if (error instanceof FileAlreadyExistError){
            FileAlreadyExistError existError = (FileAlreadyExistError) error;
            getView().showMessage(
                    mAppResources.getString(
                            R.string.prompt_file_already_exist,
                            existError.getCheckedFile().getName()
                    ),
                    PresentationView.TYPE_MESSAGE_MAJOR
            );
        } else {
            onErrorObtained(error);
        }
    }
}
