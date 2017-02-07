package com.afterlogic.aurora.drive.presentation.modules.filelist.presenter;

import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.common.util.FileUtil;
import com.afterlogic.aurora.drive.presentation.modules.filelist.interactor.FileListInteractor;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListView;
import com.afterlogic.aurora.drive.presentation.modules.filelist.viewModel.FileListModel;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.FileActionCallback;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileListPresenterImpl extends BasePresenter<FileListView> implements FileListPresenter {

    private final FileListInteractor mInteractor;
    private final FileListModel mModel;

    private FileActionCallback mFileActionCallback;
    private String mType;

    private final List<AuroraFile> mPath = new ArrayList<>();
    private Disposable mThumbnailRequest = null;

    private String mLastRefreshFolder = null;

    @Inject FileListPresenterImpl(ViewState<FileListView> viewState,
                                  FileListInteractor interactor,
                                  FileListModel model) {
        super(viewState);
        mInteractor = interactor;
        mModel = model;
        mModel.setPresenter(this);
    }

    @Override
    public void initWith(String type, FileActionCallback callback) {
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
        }
    }

    @Override
    public void onFileLongClick(AuroraFile file) {

    }

    private AuroraFile getCurrentFolder(){
        return mPath.get(0);
    }

    @Override
    public boolean onBackPressed() {
        return popPath();
    }

    private void handleFilesResult(List<AuroraFile> files){
        Collections.sort(files, FileUtil.AURORA_FILE_COMPARATOR);

        mModel.setFileList(files);
        mThumbnailRequest = Stream.of(files)
                .filter(AuroraFile::hasThumbnail)
                .map(file -> mInteractor.getThumbnail(file)
                        .doOnSuccess(thumb -> mModel.setThumbNail(file, thumb))
                        .toCompletable()
                        .onErrorComplete()
                )
                .collect(Observables.Collectors.concatCompletable())
                .doFinally(() -> mThumbnailRequest = null)
                .subscribe();

    }

    private boolean popPath(){
        if (mPath.size() == 1) return false;

        mPath.remove(0);
        onRefresh();

        //TODO notify changes
        return true;
    }
}
