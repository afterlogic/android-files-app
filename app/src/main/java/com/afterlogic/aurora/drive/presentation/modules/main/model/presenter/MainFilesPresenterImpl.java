package com.afterlogic.aurora.drive.presentation.modules.main.model.presenter;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter.BaseFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.main.model.interactor.MainFilesInteractor;
import com.afterlogic.aurora.drive.presentation.modules.main.model.router.MainFilesRouter;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFilesView;
import com.afterlogic.aurora.drive.presentation.modules.main.model.MainFilesModel;

import javax.inject.Inject;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesPresenterImpl extends BaseFilesPresenter<MainFilesView> implements MainFilesPresenter {

    private final MainFilesInteractor mInteractor;
    private final MainFilesModel mModel;
    private final MainFilesRouter mRouter;

    @Inject MainFilesPresenterImpl(ViewState<MainFilesView> viewState,
                                   MainFilesInteractor interactor,
                                   MainFilesModel model,
                                   MainFilesRouter router) {
        super(viewState, interactor, model, router);
        mInteractor = interactor;
        mModel = model;
        mRouter = router;
    }

    @Override
    protected void onPresenterStart() {
        super.onPresenterStart();
        mInteractor.getUserLogin()
                .subscribe(mModel::setLogin, this::onErrorObtained);
    }

    @Override
    public void onLogout() {
        mInteractor.logout()
                .doOnError(MyLog::majorException)
                .onErrorComplete()
                .subscribe(mRouter::openLogin);
    }

    @Override
    public void onMultiChoiseAction() {
        mModel.setMultiChoiseMode(false);
    }

    @Override
    public void onOfflineModeSelected() {
        mRouter.openOfflineMode();
    }

    @Override
    public void onAbout() {
        mRouter.openAbout();
    }

    @Override
    public boolean onBackPressed() {
        if (mModel.isInMultiChoise()){
            mModel.setMultiChoiseMode(false);
            return true;
        }
        return false;
    }

}
