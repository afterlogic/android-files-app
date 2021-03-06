package com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.presenter;

import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.model.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.BaseFilesModel;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.interactor.FilesInteractor;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.router.FilesRouter;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;

import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class BaseFilesPresenter<V extends PresentationView> extends BasePresenter<V> implements FilesPresenter {

    private final FilesInteractor mInteractor;
    private final BaseFilesModel mModel;
    private final FilesRouter mRouter;

    private Disposable mCurrentRefresh = null;

    public BaseFilesPresenter(ViewState<V> viewState, FilesInteractor interactor, BaseFilesModel model, FilesRouter router) {
        super(viewState);
        mInteractor = interactor;
        mModel = model;
        mRouter = router;
    }

    @Override
    protected void onPresenterStart() {
        super.onPresenterStart();
        refresh();
    }

    @Override
    protected void onViewStart() {
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
    public void onCurrentFolderChanged(AuroraFile folder) {
        mModel.setCurrentFolder(folder);
    }

    @Override
    public void refresh() {
        if (mCurrentRefresh != null){
            mCurrentRefresh.dispose();
        }

        mCurrentRefresh = mInteractor.getAvailableFileTypes()
                .doOnSubscribe(disposable -> {
                    mModel.setFileTypes(Collections.emptyList());
                    mModel.setRefreshing(true);
                    mModel.setErrorState(false);
                })
                .doFinally(() -> {
                    mCurrentRefresh = null;
                    mModel.setRefreshing(false);
                })
                .subscribe(
                        mModel::setFileTypes,
                        this::handleFilesError
                );
    }

    protected void handleFilesError(Throwable error){
        mModel.setErrorState(true);
        if (error instanceof RuntimeException && error.getCause() != null){
            error = error.getCause();
        }
        if (ObjectsUtil.isExtendsAny(error, SocketTimeoutException.class, HttpException.class, UnknownHostException.class, ConnectException.class)){
            mRouter.goToOfflineError();
        } else {
            onErrorObtained(error);
        }
    }
}
