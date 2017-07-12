package com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.viewModel;

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
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.interactor.MainFileActionCallback;
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.interactor.MainFileActionRequest;
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.interactor.MainFilesActionsInteractor;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.terrakok.cicerone.Router;

/**
 * Created by sashka on 15.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesActionViewModel extends LifecycleViewModel {

    public final ObservableField<MainFileActionRequest> file = new ObservableField<>();
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

        interactor.getFileActionRequest()
                .compose(fileForActionDisposable::track)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber.subscribe(
                        this::handleFileForAction,
                        error -> {
                            router.exit();
                            return false;
                        }
                ));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onStop() {
        fileForActionDisposable.disposeAndClear();
        unbindOffline();
    }

    private void handleFileForAction(RxVariable.Value<MainFileActionRequest> requestValue) {
        unbindOffline();

        MainFileActionRequest request = requestValue.get();
        this.file.set(request);

        if (request == null) {
            fileForActionDisposable.disposeAndClear();
            router.exit();
            return;
        }

        AuroraFile file = request.getFile();
        MainFileActionCallback callback = request.getCallback();

        items.clear();

        items.add(creator.button(R.string.prompt_rename, R.drawable.ic_edit, callback::onRenameFileAction));
        items.add(creator.button(R.string.prompt_delete, R.drawable.ic_delete, callback::onDeleteFileAction));
        if (!file.isFolder()) {
            items.add(creator.button(R.string.prompt_action_download, R.drawable.ic_download_black, callback::onDownloadFileAction));
            items.add(creator.button(R.string.prompt_send, R.drawable.ic_action_share, callback::onShareFileAction));

            CheckableFileActionViewModel offline = creator.checkable(
                    R.string.prompt_offline_mode, R.drawable.ic_offline, request.getOffline().get(), callback::onMakeOfflineFileAction
            );
            items.add(offline);

            offlineUnbindable = UnbindableObservable.create(request.getOffline())
                    .addListener(field -> offline.setChecked(field.get()))
                    .bind()
                    .notifyChanged();
        }

        if (!file.isLink()) {
            items.add(creator.checkable(R.string.prompt_action_public_link, R.drawable.ic_action_public_link, file.isShared(), callback::onMakePublicLink));
            if (file.isShared()) {
                items.add(creator.button(R.string.prompt_action_public_link_copy, View.NO_ID, callback::onCopyPublicLinkFileAction));
            }
        }

        items.add(creator.button(R.string.prompt_action__replace, R.drawable.ic_content_cut, callback::onReplaceFileAction));
        items.add(creator.button(R.string.prompt_action__copy, R.drawable.ic_action_copy, callback::onCopyFileAction));
    }

    private void unbindOffline() {
        if (offlineUnbindable != null) {
            offlineUnbindable.unbind();
            offlineUnbindable = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        interactor.setFileActionRequest(null);
        unbindOffline();
    }
}
