package com.afterlogic.aurora.drive.presentation.modules.upload.viewModel;

import android.net.Uri;
import android.support.v4.util.Pair;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.rx.RxVariable;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;

import java.util.List;

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
    private final RxVariable<List<Uri>> uploads = new RxVariable<>();

    @Inject
    UploadViewModelsConnection() {
    }

    void publishUploads(List<Uri> uploads) {
        this.uploads.set(uploads);
    }

    void sendAction(String targetType, UploadAction action) {
        actionsPublisher.onNext(new Pair<>(targetType, action));
    }

    Observable<UploadAction> listenActions(String type) {
        return actionsPublisher
                .filter(actionBundle -> actionBundle.first.equals(type))
                .map(actionBundle -> actionBundle.second);
    }

    Observable<List<Uri>> getUploads() {
        return uploads.asObservable()
                .filter(RxVariable.Value::nonNull)
                .map(RxVariable.Value::get);
    }
}
