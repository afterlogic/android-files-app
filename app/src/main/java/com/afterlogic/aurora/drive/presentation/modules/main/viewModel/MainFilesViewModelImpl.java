package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesViewModelImpl;
import com.afterlogic.aurora.drive.presentation.modules.main.model.interactor.MainFilesInteractor;
import com.afterlogic.aurora.drive.presentation.modules.main.model.router.MainFilesRouter;

import javax.inject.Inject;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
public class MainFilesViewModelImpl extends BaseFilesViewModelImpl implements MainFilesViewModel{

    private final MainFilesInteractor mInteractor;
    private final MainFilesRouter mRouter;

    private final ObservableField<String> mLogin = new ObservableField<>();
    private final ObservableBoolean mMultichoiseMode = new ObservableBoolean(false);
    private final ObservableInt mSelectedCount = new ObservableInt(0);
    private final ObservableBoolean mSelectedHasFolder = new ObservableBoolean(false);

    @Inject
    MainFilesViewModelImpl(MainFilesInteractor interactor, MainFilesRouter router) {
        super(interactor, router);
        mInteractor = interactor;
        mRouter = router;

        mInteractor.getUserLogin()
                .subscribe(this::handleLogin, this::onErrorObtained);
    }

    @Override
    public ObservableField<String> getLogin() {
        return mLogin;
    }

    @Override
    public ObservableBoolean getMultichoiseMode() {
        return mMultichoiseMode;
    }

    @Override
    public ObservableInt getSelectedCount() {
        return mSelectedCount;
    }

    @Override
    public ObservableBoolean getSelectedHasFolder() {
        return mSelectedHasFolder;
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
    public void onLogout() {
        mInteractor.logout()
                .doOnError(MyLog::majorException)
                .onErrorComplete()
                .subscribe(mRouter::openLogin);
    }

    @Override
    public void onMultiChoiseAction() {
        mMultichoiseMode.set(true);
    }

    @Override
    public boolean onBackPressed() {
        if (mMultichoiseMode.get()){
            mMultichoiseMode.set(false);
            return true;
        }
        return false;
    }

    @Override
    public void setMultichoiseMode(boolean multichoiseMode) {
        mMultichoiseMode.set(multichoiseMode);
    }

    @Override
    public void setSelectedCount(int count) {
        mSelectedCount.set(count);
    }

    @Override
    public void setSetSelectedHasFolder(boolean hasFolder) {
        mSelectedHasFolder.set(hasFolder);
    }

    private void handleLogin(String login) {
        mLogin.set(login);
    }

}
