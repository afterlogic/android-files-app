package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import android.support.v4.util.Pair;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.rx.RxVariable;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by aleksandrcikin on 13.07.17.
 * mail: mail@sunnydaydev.me
 */
@ModuleScope
class MainViewModelsConnection extends ViewModelsConnection<MainFilesListViewModel> {

    private final RxVariable<Boolean> multiChoiceMode = new RxVariable<>(false);

    private final RxVariable<List<MultiChoiceFile>> multiChoice = new RxVariable<>(null);

    private final PublishSubject<Pair<String, MultiChoiceAction>> multiChoiceActionPublisher = PublishSubject.create();

    private final PublishSubject<Pair<String, MainAction>> mainActionPublisher = PublishSubject.create();

    @Inject
    MainViewModelsConnection() {
    }

    void setMultiChoiceMode(boolean multiChoiceMode) {
        this.multiChoiceMode.set(multiChoiceMode);
    }

    void setMultiChoice(List<MultiChoiceFile> multiChoice) {
        this.multiChoice.set(multiChoice);
    }

    void sendMultiChoiceAction(String targetType, MultiChoiceAction action) {
        multiChoiceActionPublisher.onNext(new Pair<>(targetType, action));
    }

    void sendMainAction(String targetFileType, MainAction action) {
        mainActionPublisher.onNext(new Pair<>(targetFileType, action));
    }

    Observable<Boolean> getMultiChoiceMode() {
        return multiChoiceMode.asObservable()
                .map(RxVariable.Value::get);
    }

    Observable<RxVariable.Value<List<MultiChoiceFile>>> getMultiChoice() {
        return multiChoice.asObservable();
    }

    Observable<MultiChoiceAction> listenMultiChoiceAction(String fileType) {
        return multiChoiceActionPublisher.filter(action -> action.first.equals(fileType))
                .map(action -> action.second);
    }

    Observable<MainAction> listenMainAction(String fileType){
        return mainActionPublisher.filter(action -> action.first.equals(fileType))
                .map(action -> action.second);
    }
}
