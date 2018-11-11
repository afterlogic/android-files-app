package com.afterlogic.aurora.drive.presentation.modules.upload.viewModel;

import androidx.databinding.ObservableField;
import android.net.Uri;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.DisposableBag;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.error.FileAlreadyExistError;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.OnActionListener;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog.MessageDialogViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog.ProgressViewModel;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.interactor.rx.WakeLockTransformer;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.FileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.rx.FileProgressTransformer;
import com.afterlogic.aurora.drive.presentation.modules.upload.interactor.UploadFilesInteractor;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;
import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 19.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadFileListViewModel extends FileListViewModel<UploadFileListViewModel, UploadFileViewModel, UploadArgs> {

    public final ObservableField<MessageDialogViewModel> message = new ObservableField<>();

    private final UploadFilesInteractor interactor;
    private final Subscriber subscriber;
    private final AppResources appResources;
    private final UploadViewModelsConnection viewModelsConnection;
    private final Router router;
    private final WakeLockTransformer.Factory wakeLockFactory;

    private final FileMap fileMap;

    private final PublishSubject<UploadArgs> argsPublisher = PublishSubject.create();

    private final AtomicBoolean uploading = new AtomicBoolean(false);

    private DisposableBag globalDisposableBag = new DisposableBag();

    @Inject
    UploadFileListViewModel(UploadFilesInteractor interactor,
                            Subscriber subscriber,
                            UploadViewModelsConnection viewModelsConnection,
                            AppResources appResources,
                            Router router,
                            WakeLockTransformer.Factory wakeLockFactory) {

        super(interactor, subscriber, viewModelsConnection);

        this.interactor = interactor;
        this.subscriber = subscriber;
        this.appResources = appResources;
        this.viewModelsConnection = viewModelsConnection;
        this.router = router;
        this.wakeLockFactory = wakeLockFactory;

        this.fileMap = new FileMap(appResources);

        argsPublisher
                .map(UploadArgs::getType)
                .flatMap(viewModelsConnection::listenActions)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(globalDisposableBag::track)
                .subscribe(subscriber.subscribe(this::onAction));
    }

    @Override
    protected UploadFileViewModel mapFileItem(AuroraFile file, OnActionListener<AuroraFile> onClick) {
        return fileMap.mapAndStoreSource(file, onClick);
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

    @Override
    protected void onFileClick(AuroraFile file) {
        if (!file.isFolder()) return;

        super.onFileClick(file);
    }

    @Override
    protected void handleFiles(List<AuroraFile> files) {

        fileMap.clear();

        super.handleFiles(files);

        Stream.of(files)
                .filter(AuroraFile::hasThumbnail)
                .map(file -> interactor.getThumbnail(file)
                        .compose(subscriber::defaultSchedulers)
                        .doOnSuccess(thumb -> {

                            UploadFileViewModel vm = fileMap.getViewModel(file);
                            if (vm != null) {
                                vm.setThumbnail(thumb);
                            }

                        })
                        .ignoreElement()
                        .onErrorComplete()
                )
                .collect(Observables.Collectors.concatCompletable())
                .subscribe(subscriber.justSubscribe());

    }

    private void onAction(UploadAction action) {
        MyLog.d("onAction: " + action);
        switch (action) {
            case CREATE_FOLDER: createFolder(); break;
            case UPLOAD: upload(); break;
        }
    }

    private void createFolder() {

        if (foldersStack.size() == 0) {
            return;
        }

        interactor.getCreateFolderName()
                .subscribe(subscriber.subscribe(this::createFolderWithName));
    }

    private void upload() {
        if (uploading.getAndSet(true)) return;

        viewModelsConnection.getUploads()
                .firstElement()
                .flatMapObservable(this::uploadAll)
                .compose(subscriber::defaultSchedulers)
                .compose(wakeLockFactory.create())
                .compose(new FileProgressTransformer<>(
                        appResources.getString(R.string.dialog_files_title_uploading),
                        progress
                ))
                .filter(Progressible::isDone)
                .map(Progressible::getData)
                .collectInto(new ArrayList<AuroraFile>(), ArrayList::add)
                .doFinally(() -> uploading.set(false))
                .subscribe(subscriber.subscribe(
                        this::handleSuccessUpload,
                        this::handleUploadError
                ));
    }

    private Observable<Progressible<AuroraFile>> uploadAll(List<Uri> uploads) {
        AuroraFile folder = foldersStack.get(0);
        return Stream.of(uploads)
                .map(upload -> interactor.uploadFile(folder, upload))
                .collect(Observables.Collectors.concatObservables());
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

    private void handleSuccessUpload(List<AuroraFile> uploads) {
        router.exit();
    }

    private boolean handleUploadError(Throwable error) {
        if (error instanceof FileAlreadyExistError) {
            FileAlreadyExistError existError = (FileAlreadyExistError) error;
            String messageText = appResources.getString(
                    R.string.prompt_file_already_exist,
                    existError.getCheckedFile().getName()
            );
            MessageDialogViewModel.set(message, null, messageText);
            return true;
        } else {
            String messageText = appResources.getString(R.string.prompt_error_occurred);
            MessageDialogViewModel.set(message, null, messageText);
        }
        return false;
    }
}
