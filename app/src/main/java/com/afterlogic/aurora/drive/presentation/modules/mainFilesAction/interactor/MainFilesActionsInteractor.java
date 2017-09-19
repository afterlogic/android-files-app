package com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.interactor;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.RxVariable;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.model.AuroraSession;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

@ModuleScope
public class MainFilesActionsInteractor {

    private final RxVariable<MainFileActionsFile> file = new RxVariable<>();
    private final OptionalDisposable requestDisposable = new OptionalDisposable();
    private final PublishSubject<MainFileAction> actionPublishSubject = PublishSubject.create();
    private final SessionManager sessionManager;

    @Inject
    MainFilesActionsInteractor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void finishCurrentRequest() {
        requestDisposable.disposeAndClear();
    }

    public void postAction(MainFileAction action) {
        actionPublishSubject.onNext(action);
    }

    public boolean isP8() {
        AuroraSession session = sessionManager.getSession();
        return session != null && session.getApiVersion() == Const.ApiVersion.API_P8;
    }

    public Observable<RxVariable.Value<MainFileActionsFile>> getFile() {
        return file.asObservable();
    }

    public Observable<MainFileAction> setFileForAction(MainFileActionsFile file) {
        return actionPublishSubject
                .doOnSubscribe(disposable -> {
                    if (this.file.get() != null) {
                        throw new IllegalStateException("Only one action request at one time allowed.");
                    }

                    this.file.set(file);
                })
                .doFinally(() -> this.file.set(null))
                .compose(requestDisposable::track);
    }
}
