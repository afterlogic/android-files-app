package com.afterlogic.aurora.drive.presentation.modules.baseFiles.viewModel;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableList;

import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Storage;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.BaseViewModel;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.BaseFilesModel;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.interactor.FilesInteractor;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.router.FilesRouter;
import com.annimon.stream.Stream;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BaseFilesViewModelImpl extends BaseViewModel implements BaseFilesViewModel {

    private final FilesInteractor mInteractor;
    private final FilesRouter mRouter;

    private final ObservableField<String> mFolderTitle = new ObservableField<>();
    private final ObservableBoolean mRefreshing = new ObservableBoolean(true);
    private final ObservableBoolean mLocked = new ObservableBoolean(false);
    private final ObservableInt mCurrentPosition = new ObservableInt(-1);
    private final ObservableList<Storage> mStorages = new ObservableArrayList<>();
    private final ObservableBoolean mErrorState = new ObservableBoolean();

    private String mCurrentFileType = null;

    private Disposable mCurrentRefresh = null;

    @Inject
    public BaseFilesViewModelImpl(FilesInteractor interactor, FilesRouter router) {
        mInteractor = interactor;
        mRouter = router;

        refresh();
    }

    @Override
    public ObservableList<Storage> getStorages() {
        return mStorages;
    }

    @Override
    public ObservableBoolean getRefreshing() {
        return mRefreshing;
    }

    @Override
    public ObservableField<String> getFolderTitle() {
        return mFolderTitle;
    }

    @Override
    public ObservableBoolean getLocked(){
        return mLocked;
    }

    @Override
    public ObservableInt getCurrentPagePosition(){
        return mCurrentPosition;
    }

    @Override
    public ObservableBoolean getErrorState() {
        return mErrorState;
    }

    @Override
    public void onCurrentFolderChanged(AuroraFile folder) {
        if (folder == null || "".equals(folder.getFullPath())){
            mCurrentFileType = null;
            mFolderTitle.set(null);
        } else {
            mCurrentFileType = folder.getType();
            mFolderTitle.set(folder.getName());
        }
        updateCurrentPosition();
        updateLocked();
    }

    //TODO remove
    @Override
    public BaseFilesModel getModel() {
        return null;
    }

    @Override
    public void onViewStart() {
        super.onViewStart();
        mInteractor.getAuthStatus()
                .subscribe(
                        auth -> {
                            if (!auth){
                                mRouter.openAuth();
                            }
                        },
                        this::onErrorObtained
                );
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {

        if (mCurrentRefresh != null){
            mCurrentRefresh.dispose();
        }

        mCurrentRefresh = mInteractor.getAvailableFileTypes()
                .doOnSubscribe(disposable -> {
                    handleFileTypes(Collections.emptyList());
                    mRefreshing.set(true);
                    mErrorState.set(false);
                })
                .doFinally(() -> {
                    mCurrentRefresh = null;
                    mRefreshing.set(false);
                })
                .subscribe(
                        this::handleFileTypes,
                        this::handleFilesError
                );
    }

    private void handleFileTypes(List<Storage> types) {
        mStorages.clear();
        mStorages.addAll(types);

        updateCurrentPosition();
        updateLocked();
    }

    private void handleFilesError(Throwable error){
        mErrorState.set(true);
        if (error instanceof RuntimeException && error.getCause() != null){
            error = error.getCause();
        }
        if (ObjectsUtil.isExtendsAny(error, SocketTimeoutException.class, HttpException.class, UnknownHostException.class, ConnectException.class)){
            mRouter.goToOfflineError();
        } else {
            onErrorObtained(error);
        }
    }


    private void updateCurrentPosition(){
        int position = Stream.of(mStorages)
                .filter(type -> type.getFiles().equals(mCurrentFileType))
                .map(mStorages::indexOf)
                .findFirst()
                .orElse(-1);
        mCurrentPosition.set(position);
    }

    private void updateLocked(){
        mLocked.set(mCurrentPosition.get() != -1);
    }
}
