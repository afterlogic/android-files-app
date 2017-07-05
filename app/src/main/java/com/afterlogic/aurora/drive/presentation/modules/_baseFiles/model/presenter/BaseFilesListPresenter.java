package com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter;

import android.content.Context;
import android.os.SystemClock;

import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.model.presenter.BaseLoadPresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.BaseFilesListModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.interactor.FilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.router.FilesRouter;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.FilesListView;
import com.annimon.stream.Stream;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class BaseFilesListPresenter<V extends FilesListView> extends BaseLoadPresenter<V> implements FilesListPresenter {

    private final FilesListInteractor mInteractor;
    private final BaseFilesListModel mModel;
    private final FilesRouter mRouter;

    private String mType;

    private String mLastRefreshFolder = null;
    private final List<AuroraFile> mPath = new ArrayList<>();

    private long mStopTime = 0;

    private Disposable mThumbnailRequest = null;

    public BaseFilesListPresenter(ViewState<V> viewState, FilesListInteractor interactor, BaseFilesListModel model, Context appContext, FilesRouter router) {
        super(viewState, appContext);
        mInteractor = interactor;
        mModel = model;
        mRouter = router;
    }

    @Override
    public void initWith(String type) {
        mType = type;
    }

    @Override
    protected void onPresenterStart() {
        super.onPresenterStart();
        mPath.add(AuroraFile.parse("", mType, true));
    }

    @Override
    protected void onViewStart() {
        super.onViewStart();
        //Do not refresh on fast stop/start
        if (mStopTime == 0 || SystemClock.elapsedRealtime() - mStopTime > 1000) {
            onRefresh();
        }
        mInteractor.onStart();
    }

    @Override
    protected void onViewStop() {
        super.onViewStop();
        mInteractor.onStop();
        mStopTime = SystemClock.elapsedRealtime();
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
                .doOnError(error -> {
                    mModel.setErrorState(true);
                    mModel.setFileList(Collections.emptyList());
                })
                .subscribe(
                        this::handleFilesResult,
                        this::handleFilesError
                );
    }

    @Override
    public void onFileClick(AuroraFile file) {
        if (file.isFolder() || isListAction(file)){
            mPath.add(0, file);
            onRefresh();
        }
    }

    @Override
    public void onFileLongClick(AuroraFile file) {

    }

    protected AuroraFile getCurrentFolder(){
        return mPath.get(0);
    }

    @Override
    public boolean onBackPressed() {
        return popPath();
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

    protected void handleFilesResult(List<AuroraFile> files){
        mModel.setFileList(files);
        mThumbnailRequest = Stream.of(files)
                .filter(AuroraFile::hasThumbnail)
                .map(this::updateFileThumb)
                .collect(Observables.Collectors.concatCompletable())
                .doFinally(() -> mThumbnailRequest = null)
                .subscribe();
    }


    protected void handleFilesError(Throwable error){
        mModel.setErrorState(true);
        if (error instanceof RuntimeException && error.getCause() != null){
            error = error.getCause();
        }
        if (ObjectsUtil.isExtendsAny(error, SocketTimeoutException.class, HttpException.class, UnknownHostException.class)){
            mRouter.goToOfflineError();
        } else {
            onErrorObtained(error);
        }
    }

    protected boolean isInNotFolder() {
        return Stream.of(mPath)
                .anyMatch(file -> !file.isFolder());
    }

    public boolean isListAction(AuroraFile file) {
        return file.getActions() != null && file.getActions().hasList();
    }
}