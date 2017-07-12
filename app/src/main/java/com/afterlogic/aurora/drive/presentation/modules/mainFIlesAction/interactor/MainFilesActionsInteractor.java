package com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.interactor;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.rx.RxVariable;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

@ModuleScope
public class MainFilesActionsInteractor {

    private RxVariable<MainFileActionRequest> fileForAction = new RxVariable<>();

    @Inject
    public MainFilesActionsInteractor() {
    }

    public void setFileActionRequest(MainFileActionRequest file) {
        fileForAction.set(file);
    }

    public Observable<RxVariable.Value<MainFileActionRequest>> getFileActionRequest() {
        return fileForAction.asObservable();
    }
}
