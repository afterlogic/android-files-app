package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesBiModel;
import com.afterlogic.aurora.drive.presentation.modules.main.model.MainFilesModel;
import com.afterlogic.aurora.drive.presentation.modules.main.model.presenter.MainFilesPresenter;

import javax.inject.Inject;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
public class MainFilesBiModel extends BaseFilesBiModel implements MainFilesViewModel, MainFilesModel{

    private final ObservableField<String> mLogin = new ObservableField<>();
    private final ObservableBoolean mMultichoiseMode = new ObservableBoolean(false);
    private final ObservableInt mSelectedCount = new ObservableInt(0);
    private final ObservableBoolean mSelectedHasFolder = new ObservableBoolean(false);
    private OptWeakRef<MainFilesPresenter> mPresenter;

    @Inject MainFilesBiModel(OptWeakRef<MainFilesPresenter> presenter) {
        super(presenter);
        mPresenter = presenter;
    }

    public MainFilesModel getModel(){
        return this;
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
        mPresenter.ifPresent(MainFilesPresenter::onOfflineModeSelected);
    }

    @Override
    public void setLogin(String login) {
        mLogin.set(login);
    }

    @Override
    public void setMultiChoiseMode(boolean multiChoise) {
        mMultichoiseMode.set(multiChoise);
    }

    @Override
    public boolean isInMultiChoise() {
        return mMultichoiseMode.get();
    }

    @Override
    public void setSelectedCount(int count) {
        mSelectedCount.set(count);
    }

    @Override
    public void setSetSelectedHasFolder(boolean hasFolder) {
        mSelectedHasFolder.set(hasFolder);
    }
}
