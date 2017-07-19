package com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.interactor;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.RxVariable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

@ModuleScope
public class MainFilesActionsInteractor {

   private RxVariable<MainFileActionsFile> file = new RxVariable<>();
   private OptionalDisposable requestDisposable = new OptionalDisposable();
   private PublishSubject<MainFileAction> actionPublishSubject = PublishSubject.create();

    @Inject
    public MainFilesActionsInteractor() {
    }

    public void finishCurrentRequest() {
        requestDisposable.disposeAndClear();
    }

    public void postAction(MainFileAction action) {
        actionPublishSubject.onNext(action);
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
