package com.afterlogic.aurora.drive.presentation.modules.main.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.OfflineType;
import com.afterlogic.aurora.drive.model.error.FileAlreadyExistError;
import com.afterlogic.aurora.drive.model.error.FileNotExistError;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.presenter.BaseFilesListPresenter;
import com.afterlogic.aurora.drive.presentation.modules.main.interactor.MainFileListInteractor;
import com.afterlogic.aurora.drive.presentation.modules.main.router.MainFileListRouter;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFileListView;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFilesListModel;
import com.annimon.stream.Stream;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFileListPresenterImpl extends BaseFilesListPresenter<MainFileListView> implements MainFileListPresenter {

    private final MainFileListInteractor mInteractor;
    private final MainFilesListModel mModel;
    private final MainFileListRouter mRouter;
    private final AppResources mAppResources;

    @Inject
    MainFileListPresenterImpl(ViewState<MainFileListView> viewState,
                              MainFileListInteractor interactor,
                              MainFilesListModel model,
                              MainFileListRouter router,
                              AppResources appResources,
                              Context appContext) {
        super(viewState, interactor, model, appContext);
        mInteractor = interactor;
        mModel = model;
        mRouter = router;
        mAppResources = appResources;
    }

    @Override
    public void onFileClick(AuroraFile file) {
        if (mModel.isMultiChoise()){
            mModel.toggleSelected(file);
            return;
        }

        if (file.isFolder()){
            super.onFileClick(file);
        } else {

            if (!mRouter.canOpenFile(file)){
                onCantOpenFile();
                return;
            }

            if (file.isLink()){
                mRouter.openLink(file);
            }else {
                if (file.isPreviewAble()){
                    mRouter.openImagePreview(file, mModel.getFiles());
                }else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType(file.getContentType());

                    if (!file.isOfflineMode()) {
                        mInteractor.downloadForOpen(file)
                                .compose(this::progressibleLoadTask)
                                .subscribe(
                                        localFile -> mRouter.openFile(file, localFile),
                                        this::onErrorObtained
                                );
                    } else {
                        //TODO open offline
                        //File localFile = FileUtil.getOfflineFile(file, getApplicationContext());
                        //if (localFile.exists()){
                        //    onFileDownloaded(file, localFile, DownloadType.DOWNLOAD_OPEN);
                        //} else {
                        //    getView().showMessage(
                        //            R.string.prompt_offline_file_not_exist,
                        //            PresentationView.TYPE_MESSAGE_MAJOR
                        //    );
                        //}
                    }
                }
            }
        }
    }

    @Override
    public void onFileLongClick(AuroraFile file) {
        if (mModel.isMultiChoise()){
            mModel.toggleSelected(file);
            return;
        }

        getView().showFileActions(file);
    }

    @Override
    public void onDownload(@Nullable AuroraFile file) {
        if (file == null && !mModel.isMultiChoise()) {
            MyLog.majorException("Download file with null.");
            return;
        }

        //Check on multichoise (file is null when multichoise)
        if (file == null){
            List<AuroraFile> files = mModel.getMultiChoise();
            Stream.of(files)
                    .map(mInteractor::downloadToDownloads)
                    .collect(Observables.Collectors.concatObservables())
                    .compose(this::progressibleLoadTask)
                    .ignoreElements()
                    .subscribe(
                            () -> getView().showMessage(
                                    String.format("%d files success downloaded.", files.size()),
                                    PresentationView.TYPE_MESSAGE_MAJOR
                            ),
                            this::onErrorObtained
                    );
        } else {
            mInteractor.downloadToDownloads(file)
                    .compose(this::progressibleLoadTask)
                    .subscribe(
                            //TODO dialog: open file?
                            localFile -> mRouter.openFile(file, localFile),
                            this::onErrorObtained
                    );
        }
    }

    @Override
    public void onSendTo(@Nullable AuroraFile file) {
        if (file == null && !mModel.isMultiChoise()) {
            MyLog.majorException("SendTo file with null.");
            return;
        }

        if (file == null){
            List<AuroraFile> files = mModel.getMultiChoise();
            List<File> results = new ArrayList<>();
            Stream.of(files)
                    .map(mInteractor::downloadForOpen)
                    .collect(Observables.Collectors.concatObservables())
                    .compose(this::progressibleLoadTask)
                    .subscribe(
                            results::add,
                            this::onErrorObtained,
                            () -> mRouter.openSendTo(results)
                    );

        } else {
            mInteractor.downloadForOpen(file)
                    .compose(this::progressibleLoadTask)
                    .subscribe(
                            localFile -> mRouter.openSendTo(file, localFile),
                            this::onErrorObtained
                    );
        }
    }

    @Override
    public void onRename(AuroraFile file) {
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
    public void onToggleOffline(@Nullable AuroraFile file) {
        mInteractor.setOffline(file, file.getOfflineInfo().getOfflineType() != OfflineType.OFFLINE)
                .subscribe(
                        () -> {},
                        this::onErrorObtained
                );
    }

    //TODO text to resources
    @Override
    public void onDelete(@Nullable AuroraFile file) {
        if (file == null && !mModel.isMultiChoise()) {
            MyLog.majorException("Delete file with null.");
            return;
        }

        Completable deleteRequest;
        String message;
        //Check on multichoise (file is null when multichoise)
        if (file == null){
            List<AuroraFile> files = mModel.getMultiChoise();
            message = String.format("%d files", files.size());
            deleteRequest = Stream.of(files)
                    .map(deleteFile -> mInteractor.deleteFile(deleteFile)
                            .doOnComplete(() -> mModel.removeFile(deleteFile))
                    )
                    .collect(Observables.Collectors.concatCompletable());
        } else {
            message = file.getName();
            deleteRequest = mInteractor.deleteFile(file)
                    .doOnComplete(() -> mModel.removeFile(file));
        }

        deleteRequest
                .doOnSubscribe(disposable -> getView().showProgress("Deleting:", message))
                .doFinally(() -> getView().hideProgress())
                .subscribe(
                        () -> {},
                        this::onErrorObtained
                );
    }

    @Override
    public void onCreateFolder() {
        getView().showNewFolderNameDialog(name -> mInteractor.createFolder(getCurrentFolder(), name)
                //TODO text to resources
                .doOnSubscribe(disposable -> getView().showProgress("Folder creation:", name))
                .doFinally(() -> getView().hideProgress())
                .subscribe(
                        mModel::addFile,
                        this::onErrorObtained
                ));
    }

    @Override
    public void onFileUpload() {
        observeActivityResult(MainFileListRouter.FILE_SELECT_CODE, true)
                .startWith(Completable.fromAction(mRouter::openUploadFileChooser).toObservable())
                .doOnError(error -> getView().showMessage(
                        //TODO text to resources
                        "Doesn't have application for choose file.",
                        PresentationView.TYPE_MESSAGE_MAJOR
                ))
                .firstElement()
                .flatMapObservable(activityResult -> mInteractor.uploadFile(
                        getCurrentFolder(),
                        activityResult.getResult().getData()
                ))
                .compose(this::progressibleLoadTask)
                .subscribe(
                        uploadedFile -> {
                            mModel.addFile(uploadedFile);
                            updateFileThumb(uploadedFile).subscribe();
                        },
                        this::onUploadError
                );
    }

    @Override
    public void onMultiChoseMode(boolean multiChoiseMode) {
        mModel.setMultiChoiseMode(multiChoiseMode);
    }

    private void handleRenameResult(AuroraFile previous, AuroraFile newFile){
        mModel.changeFile(previous, newFile);
        if (newFile.hasThumbnail()){
            updateFileThumb(newFile).subscribe();
        }
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

    private void onCantOpenFile(){
        getView().showMessage(
                R.string.prompt_cant_open_file,
                PresentationView.TYPE_MESSAGE_MAJOR
        );
    }

}
