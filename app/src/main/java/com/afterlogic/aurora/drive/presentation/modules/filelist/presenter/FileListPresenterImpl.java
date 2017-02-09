package com.afterlogic.aurora.drive.presentation.modules.filelist.presenter;

import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.error.FileAlreadyExist;
import com.afterlogic.aurora.drive.model.error.FileNotExistError;
import com.afterlogic.aurora.drive.model.error.PermissionDeniedError;
import com.afterlogic.aurora.drive.presentation.common.modules.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.common.util.FileUtil;
import com.afterlogic.aurora.drive.presentation.common.util.PermissionUtil;
import com.afterlogic.aurora.drive.presentation.modules.filelist.interactor.FileListInteractor;
import com.afterlogic.aurora.drive.presentation.modules.filelist.router.FileListRouter;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListView;
import com.afterlogic.aurora.drive.presentation.modules.filelist.viewModel.FileListModel;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.MainFilesCallback;
import com.annimon.stream.Stream;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.afterlogic.aurora.drive.model.events.PermissionGrantEvent.FILES_STORAGE_ACCESS;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileListPresenterImpl extends BasePresenter<FileListView> implements FileListPresenter {

    private final FileListInteractor mInteractor;
    private final FileListModel mModel;
    private final FileListRouter mRouter;
    private final Context mAppContext;

    private MainFilesCallback mFileActionCallback;
    private String mType;

    private final List<AuroraFile> mPath = new ArrayList<>();
    private Disposable mThumbnailRequest = null;
    private Disposable mCurrentFileTask = null;

    private String mLastRefreshFolder = null;

    @Inject FileListPresenterImpl(ViewState<FileListView> viewState,
                                  FileListInteractor interactor,
                                  FileListModel model,
                                  FileListRouter router,
                                  Context appContext) {
        super(viewState);
        mInteractor = interactor;
        mModel = model;
        mRouter = router;
        mAppContext = appContext;
        mModel.setPresenter(this);
    }

    @Override
    public void initWith(String type, MainFilesCallback callback) {
        mType = type;
        mFileActionCallback = callback;
    }

    @Override
    protected void onPresenterStart() {
        super.onPresenterStart();

        mPath.add(AuroraFile.parse("", mType, true));
        onRefresh();
    }

    @Override
    public void onRefresh() {
        if (mThumbnailRequest != null){
            mThumbnailRequest.dispose();
        }

        AuroraFile folder = getCurrentFolder();
        mFileActionCallback.onOpenFolder(folder);

        if (!folder.getFullPath().equals(mLastRefreshFolder)){
            mModel.setFileList(Collections.emptyList());
            mLastRefreshFolder = folder.getFullPath();
        }

        mInteractor.getFilesList(folder)
                .doOnSubscribe(disposable -> mModel.setRefreshing(true))
                .doFinally(() -> mModel.setRefreshing(false))
                .subscribe(
                        this::handleFilesResult,
                        this::onErrorObtained
                );
    }

    @Override
    public void onFileClick(AuroraFile file) {
        if (file.isFolder()){
            mPath.add(0, file);
            onRefresh();
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
                                .compose(this::downloadTask)
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
        getView().showFileActions(file);
    }

    @Override
    public void onCancelCurrentTask() {
        if (mCurrentFileTask != null){
            mCurrentFileTask.dispose();
        }
    }

    @Override
    public void onDownload(AuroraFile file) {
        mInteractor.downloadToDownloads(file)
                .compose(this::downloadTask)
                .subscribe(
                        //TODO dialog: open file?
                        localFile -> mRouter.openFile(file, localFile),
                        this::onErrorObtained
                );
    }

    @Override
    public void onSendTo(AuroraFile file) {
        mInteractor.downloadForOpen(file)
                .compose(this::downloadTask)
                .subscribe(
                        localFile -> mRouter.openSendTo(file, localFile),
                        this::onErrorObtained
                );
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
    public void onToggleOffline(AuroraFile file) {

    }

    @Override
    public void onDelete(AuroraFile file) {
        mInteractor.deleteFile(file)
                .doOnSubscribe(disposable -> getView().showProgress("Deleting:", file.getName()))
                .doFinally(() -> getView().hideProgress())
                .subscribe(
                        () -> mModel.removeFile(file),
                        this::onErrorObtained
                );
    }

    @Override
    public void onCreateFolder() {
        getView().showNewFolderNameDialog(name -> mInteractor.createFolder(getCurrentFolder(), name)
                .doOnSubscribe(disposable -> getView().showProgress("Folder creation:", name))
                .doFinally(() -> getView().hideProgress())
                .subscribe(
                        mModel::addFile,
                        this::onErrorObtained
                ));
    }

    @Override
    public void onFileUpload() {

    }

    private AuroraFile getCurrentFolder(){
        return mPath.get(0);
    }

    @Override
    public boolean onBackPressed() {
        return popPath();
    }

    private boolean popPath(){
        if (mPath.size() == 1) return false;

        mPath.remove(0);
        onRefresh();

        //TODO notify changes
        return true;
    }

    private void handleFilesResult(List<AuroraFile> files){
        Collections.sort(files, FileUtil.AURORA_FILE_COMPARATOR);

        mModel.setFileList(files);
        mThumbnailRequest = Stream.of(files)
                .filter(AuroraFile::hasThumbnail)
                .map(this::updateFileThumb)
                .collect(Observables.Collectors.concatCompletable())
                .doFinally(() -> mThumbnailRequest = null)
                .subscribe();

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
        } else if (error instanceof FileAlreadyExist){
            getView().showMessage(
                    R.string.error_renamed_file_exist,
                    PresentationView.TYPE_MESSAGE_MAJOR
            );
        } else {
            onErrorObtained(error);
        }
    }

    private Completable updateFileThumb(AuroraFile file){
        return mInteractor.getThumbnail(file)
                .doOnSuccess(thumb -> mModel.setThumbNail(file, thumb))
                .toCompletable()
                .onErrorComplete();
    }

    private void onCantOpenFile(){
        getView().showMessage(
                R.string.prompt_cant_open_file,
                PresentationView.TYPE_MESSAGE_MAJOR
        );
    }

    private <T> Observable<T> trackCurrentTask(Observable<T> observable){
        return observable.doOnSubscribe(disposable -> mCurrentFileTask = disposable)
                .doFinally(() -> mCurrentFileTask = null);
    }

    private Observable<File> downloadTask(Observable<Progressible<File>> observable){
        return observable.startWith(checkAndWaitPermissionResult(
                FILES_STORAGE_ACCESS,
                new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}
        ))//----|
                .doOnNext(progress -> {
                    float value = progress.getMax() > 0 ?
                            (float) progress.getProgress() / progress.getMax() : -1;
                    getView().showDownloadProgress(progress.getName(), value * 100);
                })
                .filter(Progressible::isDone)
                .map(Progressible::getData)
                .doFinally(() -> {
                    getView().hideProgress();
                    mCurrentFileTask = null;
                })
                .compose(this::trackCurrentTask);
    }

    private <T> Observable<T> checkAndWaitPermissionResult(int requestId, String... perms){
        return Observable.defer(() -> {
            if (!PermissionUtil.isAllGranted(mAppContext, perms)){
                return Observable.<T>error(new PermissionDeniedError(requestId, perms));
            } else {
                return Observable.<T>empty();
            }
        })//----|
                .doOnError(this::onErrorObtained)
                .retryWhen(attempts -> observePermissions(requestId, true));

    }
}
