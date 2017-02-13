package com.afterlogic.aurora.drive.presentation.modules._baseFiles.presenter;

import android.content.Context;

import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.error.PermissionDeniedError;
import com.afterlogic.aurora.drive.presentation.common.modules.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.presentation.common.util.PermissionUtil;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.interactor.FilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.FilesListView;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesListModel;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.afterlogic.aurora.drive.model.events.PermissionGrantEvent.FILES_STORAGE_ACCESS;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class BaseFilesListPresenter<V extends FilesListView> extends BasePresenter<V> implements FilesListPresenter {

    private final FilesListInteractor mInteractor;
    private final BaseFilesListModel mModel;
    private final Context mAppContext;

    private String mType;

    private String mLastRefreshFolder = null;
    private final List<AuroraFile> mPath = new ArrayList<>();

    private Disposable mThumbnailRequest = null;
    private Disposable mCurrentFileTask = null;

    public BaseFilesListPresenter(ViewState<V> viewState, FilesListInteractor interactor, BaseFilesListModel model, Context appContext) {
        super(viewState);
        mInteractor = interactor;
        mModel = model;
        mAppContext = appContext;
    }

    @Override
    public void initWith(String type) {
        mType = type;
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
        mModel.setCurrentFolder(folder);

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
        }
    }

    @Override
    public void onFileLongClick(AuroraFile file) {

    }

    @Override
    public void onCancelCurrentTask() {
        if (mCurrentFileTask != null){
            mCurrentFileTask.dispose();
        }
    }

    protected AuroraFile getCurrentFolder(){
        return mPath.get(0);
    }

    @Override
    public boolean onBackPressed() {
        return popPath();
    }

    public   <T> Observable<T> trackCurrentTask(Observable<T> observable){
        return observable.doOnSubscribe(disposable -> mCurrentFileTask = disposable)
                .doFinally(() -> mCurrentFileTask = null);
    }

    public <T> Observable<T> progressibleLoadTask(Observable<Progressible<T>> observable){
        return observable.startWith(checkAndWaitPermissionResult(
                FILES_STORAGE_ACCESS,
                new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}
        ))//----|
                //TODO load progress title (download/upload)
                .doOnNext(progress -> {
                    float value = progress.getMax() > 0 ?
                            (float) progress.getProgress() / progress.getMax() : -1;
                    getView().showLoadProgress(progress.getName(), value * 100);
                })
                .filter(Progressible::isDone)
                .map(Progressible::getData)
                .doOnSubscribe(disposable -> getView().showLoadProgress("", -1))
                .doFinally(() -> getView().hideProgress())
                .compose(this::trackCurrentTask);
    }

    public  <T> Observable<T> checkAndWaitPermissionResult(int requestId, String... perms){
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

    public Completable updateFileThumb(AuroraFile file){
        if (!file.hasThumbnail()) return Completable.complete();

        return mInteractor.getThumbnail(file)
                .doOnSuccess(thumb -> mModel.setThumbNail(file, thumb))
                .toCompletable()
                .onErrorComplete();
    }

    private boolean popPath(){
        if (mPath.size() == 1) return false;

        mPath.remove(0);
        onRefresh();

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
}
