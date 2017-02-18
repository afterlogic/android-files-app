package com.afterlogic.aurora.drive.presentation.modules.fileView.presenter;

import android.content.Context;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.error.FileAlreadyExistError;
import com.afterlogic.aurora.drive.model.error.FileNotExistError;
import com.afterlogic.aurora.drive.presentation.common.modules.presenter.BaseLoadPresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules.fileView.interactor.FileViewInteractor;
import com.afterlogic.aurora.drive.presentation.modules.fileView.router.FileViewRouter;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewPresentationView;

import javax.inject.Inject;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewPresenterImpl extends BaseLoadPresenter<FileViewPresentationView> implements FileViewPresenter {

    private final FileViewInteractor mInteractor;
    private final FileViewRouter mRouter;
    private FileViewModel mModel;

    @Inject
    FileViewPresenterImpl(ViewState<FileViewPresentationView> viewState,
                          FileViewInteractor interactor,
                          Context appContext, FileViewRouter router) {
        super(viewState, appContext);
        mInteractor = interactor;
        mRouter = router;
    }


    @Override
    public void onDownload() {
        AuroraFile file = mModel.getCurrent();

        if (file == null) {
            MyLog.majorException("Download file with null.");
            return;
        }

        mInteractor.downloadToDownloads(file)
                .compose(this::progressibleLoadTask)
                .subscribe(
                        //TODO dialog: open file?
                        localFile -> mRouter.openFile(file, localFile),
                        this::onErrorObtained
                );
    }

    @Override
    public void onSend() {
        AuroraFile file = mModel.getCurrent();

        if (file == null) {
            MyLog.majorException("SendTo file with null.");
            return;
        }

        mInteractor.downloadForOpen(file)
                .compose(this::progressibleLoadTask)
                .subscribe(
                        localFile -> mRouter.openSendTo(file, localFile),
                        this::onErrorObtained
                );
    }

    @Override
    public void onRename() {
        AuroraFile file = mModel.getCurrent();

        getView().showRenameDialog(file, name -> mInteractor.rename(file, name)
                .doOnSubscribe(disposable -> getView().showProgress("Renaming:", file.getName()))
                .doFinally(() -> getView().hideProgress())
                .subscribe(
                        newFile -> handleRenameResult(file, newFile),
                        this::onRenameError
                )
        );
    }

    @Override
    public void onOffline() {
        AuroraFile file = mModel.getCurrent();

        if (file == null) {
            MyLog.majorException("onOffline file with null.");
            return;
        }

        mInteractor.getOfflineStatus(file)
                .map(offline -> !offline)
                .flatMapCompletable(offline -> mInteractor.setOffline(file, offline)
                        .doOnComplete(() -> mModel.setOffline(file, offline))
                )
                .subscribe(() -> {}, this::onErrorObtained);
    }

    @Override
    public void setModel(FileViewModel model) {
        mModel = model;
    }

    @Override
    public void updateOffline(AuroraFile file) {
        mInteractor.getOfflineStatus(file)
                .subscribe(
                        offline -> mModel.setOffline(file, offline),
                        this::onErrorObtained
                );
    }

    //TODO text to resources
    @Override
    public void onDelete() {
        AuroraFile file = mModel.getCurrent();

        if (file == null) {
            MyLog.majorException("Delete file with null.");
            return;
        }

        mInteractor.deleteFile(file)
                .doOnComplete(() -> mModel.remove(file))
                .doOnSubscribe(disposable -> getView().showProgress("Deleting:", file.getName()))
                .doFinally(() -> getView().hideProgress())
                .subscribe(
                        () -> {},
                        this::onErrorObtained
                );
    }

    private void handleRenameResult(AuroraFile previous, AuroraFile newFile){
        mModel.rename(previous, newFile.getName());
    }

    private void onRenameError(Throwable error){
        if (error instanceof FileNotExistError){
            getView().showMessage(
                    R.string.error_default_api_error,
                    PresentationView.TYPE_MESSAGE_MAJOR
            );
        } else if (error instanceof FileAlreadyExistError){
            getView().showMessage(
                    R.string.error_renamed_file_exist,
                    PresentationView.TYPE_MESSAGE_MAJOR
            );
        } else {
            onErrorObtained(error);
        }
    }
}
