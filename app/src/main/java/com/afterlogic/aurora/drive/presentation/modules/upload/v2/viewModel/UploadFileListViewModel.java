package com.afterlogic.aurora.drive.presentation.modules.upload.v2.viewModel;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.DisposableBag;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog.ProgressViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.FileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules.upload.v2.interactor.UploadFilesInteractor;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by aleksandrcikin on 19.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadFileListViewModel extends FileListViewModel<UploadFileListViewModel, UploadFileViewModel, UploadArgs> {

    private final UploadFilesInteractor interactor;
    private final Subscriber subscriber;
    private final AppResources appResources;

    private final PublishSubject<UploadArgs> argsPublisher = PublishSubject.create();

    private DisposableBag globalDisposableBag = new DisposableBag();

    @Inject
    UploadFileListViewModel(UploadFilesInteractor interactor,
                            Subscriber subscriber,
                            UploadViewModelsConnection viewModelsConnection,
                            AppResources appResources) {
        super(interactor, subscriber, viewModelsConnection);
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.appResources = appResources;

        argsPublisher.map(UploadArgs::getType)
                .flatMap(viewModelsConnection::listenActions)
                .compose(subscriber::defaultSchedulers)
                .compose(globalDisposableBag::track)
                .subscribe(subscriber.subscribe(this::onAction));
    }

    @Override
    protected UploadFileViewModel mapFileItem(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        return new UploadFileViewModel(file, onItemClickListener);
    }

    @Override
    public void setArgs(UploadArgs args) {
        super.setArgs(args);
        argsPublisher.onNext(args);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        globalDisposableBag.dispose();
    }

    private void onAction(UploadAction action) {
        MyLog.d("onAction: " + action);
        switch (action) {
            case CREATE_FOLDER: createFolder(); break;
        }
    }

    private void createFolder() {

        if (foldersStack.size() == 0) {
            return;
        }

        interactor.getCreateFolderName()
                .subscribe(subscriber.subscribe(this::createFolderWithName));
    }

    private void createFolderWithName(String name) {
        interactor.createFolder(name, foldersStack.get(0))
                .doOnSubscribe(disposable -> progress.set(ProgressViewModel.Factory.indeterminateCircle(
                        appResources.getString(R.string.prompt_dialog_title_folder_creation),
                        name
                )))
                .doFinally(() -> progress.set(null))
                .compose(subscriber::defaultSchedulers)
                .subscribe(this::onFolderCreated);
    }

    private void onFolderCreated(AuroraFile folder) {
        foldersStack.add(0, folder);
    }
}
