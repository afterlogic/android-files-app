package com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.viewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.view.View;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.RxVariable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.LifecycleViewModel;
import com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.interactor.MainFileAction;
import com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.interactor.MainFileActionsFile;
import com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.interactor.MainFilesActionsInteractor;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.terrakok.cicerone.Router;

/**
 * Created by sashka on 15.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesActionViewModel extends LifecycleViewModel {

    public final ObservableField<MainFileActionsFile> file = new ObservableField<>();
    public final ObservableList<FileActionViewModel> items = new ObservableArrayList<>();

    private final MainFilesActionsInteractor interactor;
    private final Subscriber subscriber;
    private final FileActionCreator creator;
    private final Router router;

    private OptionalDisposable fileForActionDisposable = new OptionalDisposable();

    private UnbindableObservable offlineUnbindable;

    @Inject
    public MainFilesActionViewModel(MainFilesActionsInteractor interactor,
                                    Subscriber subscriber,
                                    FileActionCreator creator,
                                    Router router) {
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.creator = creator;
        this.router = router;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onStart() {

        interactor.getFile()
                .map(RxVariable.Value::get)
                .compose(fileForActionDisposable::track)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber.subscribe(
                        this::handleFileForAction,
                        error -> {
                            router.exit();
                            return error instanceof NullPointerException;
                        }
                ));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onStop() {
        fileForActionDisposable.disposeAndClear();
        unbindOffline();
    }

    private void handleFileForAction(MainFileActionsFile fileForAction) {
        unbindOffline();

        this.file.set(fileForAction);

        AuroraFile file = fileForAction.getFile();

        items.clear();

        items.add(creator.button(
                R.string.prompt_rename, R.drawable.ic_edit,
                () -> postAction(MainFileAction.RENAME)
        ));
        items.add(creator.button(
                R.string.prompt_delete, R.drawable.ic_delete,
                () -> postAction(MainFileAction.DELETE)
        ));
        if (!file.isFolder()) {
            items.add(creator.button(
                    R.string.prompt_action_download, R.drawable.ic_download_black,
                    () -> postAction(MainFileAction.DOWNLOAD)
            ));
            items.add(creator.button(
                    R.string.prompt_send, R.drawable.ic_action_share,
                    () -> postAction(MainFileAction.SHARE)
            ));
        }

        if (!file.isLink()) {

            CheckableFileActionViewModel offline = creator.checkable(
                    R.string.prompt_offline_mode, R.drawable.ic_offline, fileForAction.getOffline().get(),
                    checked -> postAction(MainFileAction.OFFLINE)
            );
            items.add(offline);

            offlineUnbindable = UnbindableObservable.create(fileForAction.getOffline())
                    .addListener(field -> offline.setChecked(field.get()))
                    .bind()
                    .notifyChanged();

            items.add(creator.checkable(
                    R.string.prompt_action_public_link, R.drawable.ic_action_public_link, file.isShared(),
                    checked -> postAction(MainFileAction.PUBLIC_LINK)
            ));
            if (file.isShared()) {
                items.add(creator.button(
                        R.string.prompt_action_public_link_copy, View.NO_ID,
                        () -> postAction(MainFileAction.COPY_PUBLIC_LINK)
                ));
            }
        }

        items.add(creator.button(
                R.string.prompt_action__replace, R.drawable.ic_content_cut,
                () -> postAction(MainFileAction.REPLACE)
        ));
        items.add(creator.button(
                R.string.prompt_action__copy, R.drawable.ic_action_copy,
                () -> postAction(MainFileAction.COPY)
        ));
    }

    private void postAction(MainFileAction action) {
        interactor.postAction(action);
    }

    private void unbindOffline() {
        if (offlineUnbindable != null) {
            offlineUnbindable.unbind();
            offlineUnbindable = null;
        }
    }

    public void onCancel() {
        interactor.finishCurrentRequest();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        interactor.finishCurrentRequest();
        unbindOffline();
    }
}
