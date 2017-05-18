package com.afterlogic.aurora.drive.presentation.modules.main.model.presenter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.error.ActivityResultError;
import com.afterlogic.aurora.drive.model.error.FileAlreadyExistError;
import com.afterlogic.aurora.drive.model.error.FileNotExistError;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter.BaseFilesListPresenter;
import com.afterlogic.aurora.drive.presentation.modules.main.model.interactor.MainFileListInteractor;
import com.afterlogic.aurora.drive.presentation.modules.main.model.router.MainFileListRouter;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFileListView;
import com.afterlogic.aurora.drive.presentation.modules.main.model.MainFilesListModel;
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
        super(viewState, interactor, model, appContext, router);
        mInteractor = interactor;
        mModel = model;
        mRouter = router;
        mAppResources = appResources;
    }

    @Override
    protected void onPresenterStart() {
        super.onPresenterStart();
        mInteractor.getSyncProgress()
                .subscribe(
                        mModel::setSyncProgress,
                        this::onErrorObtained
                );
    }

    @Override
    protected void onViewStop() {
        super.onViewStop();
        mModel.clearSyncProgress();
    }

    @Override
    protected void handleFilesResult(List<AuroraFile> files) {
        super.handleFilesResult(files);
        Stream.of(files)
                .map(file -> mInteractor.getOfflineStatus(file)
                        .doOnSuccess(offline -> mModel.setOffline(file, offline))
                        .toCompletable()
                        .onErrorComplete()
                )
                .collect(Observables.Collectors.concatCompletable())
                .subscribe(() -> {}, this::onErrorObtained);

    }

    @Override
    public void onFileClick(AuroraFile file) {
        if (mModel.isMultiChoise()){
            mModel.toggleSelected(file);
            return;
        }

        // TODO restore zip when ready
        if (file.isFolder() /*|| file.getActions() != null && file.getActions().isList()*/){
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

                    mInteractor.downloadForOpen(file)
                            .compose(progressibleLoadTask(true))
                            .subscribe(
                                    localFile -> mRouter.openFile(file, localFile),
                                    this::onErrorObtained
                            );
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

        mModel.setFileForActions(file);
    }

    @Override
    public void onDownload() {
        AuroraFile file = onFileAction();

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
                    .compose(progressibleLoadTask(true))
                    .ignoreElements()
                    .subscribe(
                            () -> getView().showMessage(
                                    mAppResources.getPlurals(R.plurals.dialog_files_success_downloaded, files.size(), files.size()),
                                    PresentationView.TYPE_MESSAGE_MAJOR
                            ),
                            this::onErrorObtained
                    );
        } else {
            mInteractor.downloadToDownloads(file)
                    .compose(progressibleLoadTask(true))
                    .subscribe(
                            localFile -> mRouter.openFile(file, localFile),
                            this::onErrorObtained
                    );
        }
    }

    @Override
    public void onSendTo() {
        AuroraFile file = onFileAction();

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
                    .compose(progressibleLoadTask(true))
                    .subscribe(
                            results::add,
                            this::onErrorObtained,
                            () -> mRouter.openSendTo(results)
                    );

        } else {
            mInteractor.downloadForOpen(file)
                    .compose(progressibleLoadTask(true))
                    .subscribe(
                            localFile -> mRouter.openSendTo(file, localFile),
                            this::onErrorObtained
                    );
        }
    }

    @Override
    public void onRename() {
        AuroraFile file = onFileAction();

        getView().showRenameDialog(file, name -> mInteractor.rename(file, name)
                .doOnSubscribe(disposable -> getView().showProgress(mAppResources.getString(
                        R.string.prompt_dialog_title_renaming),
                        file.getName() + " -> " + name
                ))
                .doFinally(() -> getView().hideProgress())
                .subscribe(
                        newFile -> handleRenameResult(file, newFile),
                        this::onRenameError
                )
        );
    }

    @Override
    public void onToggleOffline() {
        AuroraFile file = onFileAction();

        if (file == null && !mModel.isMultiChoise()) {
            MyLog.majorException("onOffline file with null.");
            return;
        }

        if (file == null){
            List<AuroraFile> items = mModel.getMultiChoise();
            //At first get avarage offline status (if any not offline set all to offline)
            Stream.of(items)
                    .map(fileItem -> mInteractor.getOfflineStatus(fileItem).toObservable())
                    .collect(Observables.Collectors.concatObservables())
                    .any(offline -> !offline)
                    .flatMapCompletable(multiOffline ->
                            Stream.of(items)
                            .map(multiFile -> mInteractor.getOfflineStatus(multiFile)
                                    .filter(itemOffline -> itemOffline != multiOffline)
                                    .flatMapCompletable(itemOffline -> mInteractor.setOffline(multiFile, multiOffline))
                                    .doOnComplete(() -> mModel.setOffline(multiFile, multiOffline))
                            )
                            .collect(Observables.Collectors.concatCompletable())
                    )
                    .subscribe(() -> {}, this::onErrorObtained);
        } else {
            mInteractor.getOfflineStatus(file)
                    .map(offline -> !offline)
                    .flatMapCompletable(offline -> mInteractor.setOffline(file, offline)
                            .doOnComplete(() -> mModel.setOffline(file, offline))
                    )
                    .subscribe(() -> {}, this::onErrorObtained);
        }
    }

    @Override
    public void onDelete() {
        AuroraFile file = onFileAction();

        if (file == null && !mModel.isMultiChoise()) {
            MyLog.majorException("Delete file with null.");
            return;
        }

        Completable deleteRequest;
        String message;
        //Check on multichoise (file is null when multichoise)
        if (file == null){
            List<AuroraFile> files = mModel.getMultiChoise();
            message = mAppResources.getPlurals(R.plurals.files, files.size(), files.size());
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
                .doOnSubscribe(disposable -> getView().showProgress(
                        mAppResources.getString(R.string.prompt_dialog_title_deleting),
                        message
                ))
                .doFinally(() -> getView().hideProgress())
                .subscribe(
                        () -> {},
                        this::onErrorObtained
                );
    }

    @Override
    public void onCreateFolder() {
        getView().showNewFolderNameDialog(name -> mInteractor.createFolder(getCurrentFolder(), name)
                .doOnSubscribe(disposable -> getView().showProgress(
                        mAppResources.getString(
                                R.string.prompt_dialog_title_folder_cration),
                        name
                ))
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
                .compose(Observables.emptyOnError(ActivityResultError.class))
                .firstElement()
                .flatMapObservable(activityResult -> mInteractor.uploadFile(
                        getCurrentFolder(),
                        activityResult.getResult().getData()
                ))
                .compose(progressibleLoadTask(false))
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
        } else if (error instanceof ActivityNotFoundException){
            getView().showMessage(
                    R.string.promtp_dialog_error_message_doesnt_have_chooser,
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

    private AuroraFile onFileAction(){
        AuroraFile file = mModel.getFileForActions();
        mModel.setFileForActions(null);
        return file;
    }
}
