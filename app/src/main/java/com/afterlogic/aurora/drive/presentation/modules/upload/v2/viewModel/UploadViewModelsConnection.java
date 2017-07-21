package com.afterlogic.aurora.drive.presentation.modules.upload.v2.viewModel;

import android.support.v4.util.Pair;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by aleksandrcikin on 21.07.17.
 * mail: mail@sunnydaydev.me
 */
@ModuleScope
class UploadViewModelsConnection extends ViewModelsConnection<UploadFileListViewModel> {

    private final PublishSubject<Pair<String, UploadAction>> actionsPublisher = PublishSubject.create();

    @Inject
    public UploadViewModelsConnection() {
    }

    void sendAction(String targetType, UploadAction action) {
        actionsPublisher.onNext(new Pair<>(targetType, action));
    }

    Observable<UploadAction> listenActions(String type) {
        return actionsPublisher
                .filter(actionBundle -> actionBundle.first.equals(type))
                .map(actionBundle -> actionBundle.second);
    }
}
