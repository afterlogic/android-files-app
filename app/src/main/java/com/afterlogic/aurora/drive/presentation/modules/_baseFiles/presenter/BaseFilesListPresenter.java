package com.afterlogic.aurora.drive.presentation.modules._baseFiles.presenter;

import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.common.util.FileUtil;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.interactor.FilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.FilesListView;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesListModel;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class BaseFilesListPresenter<V extends FilesListView> extends BasePresenter<V> implements FilesListPresenter {

    private final FilesListInteractor mInteractor;
    private final BaseFilesListModel mModel;

    private String mType;

    private String mLastRefreshFolder = null;
    private final List<AuroraFile> mPath = new ArrayList<>();

    private Disposable mThumbnailRequest = null;
    private Disposable mCurrentFileTask = null;

    public BaseFilesListPresenter(ViewState<V> viewState, FilesListInteractor interactor, BaseFilesListModel model) {
        super(viewState);
        mInteractor = interactor;
        mModel = model;
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

    private Completable updateFileThumb(AuroraFile file){
        if (!file.hasThumbnail()) return Completable.complete();

        return mInteractor.getThumbnail(file)
                .doOnSuccess(thumb -> mModel.setThumbNail(file, thumb))
                .toCompletable()
                .onErrorComplete();
    }
}
