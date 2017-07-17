package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel;

import android.databinding.ObservableField;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog.ProgressViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.UiObservableField;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.SearchableFilesRootViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;
import com.afterlogic.aurora.drive.presentation.modules.replace.interactor.ReplaceInteractor;
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceArgs;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 29.06.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceViewModel extends SearchableFilesRootViewModel<ReplaceFileTypeViewModel> {

    public final ObservableField<String> subtitle = new UiObservableField<>();

    private final ReplaceInteractor interactor;
    private final Subscriber subscriber;
    private final Router router;
    private final AppResources appResources;
    private final ViewModelsConnection<ReplaceFileTypeViewModel> viewModelsConnection;

    private boolean isCopyMode = false;
    private List<AuroraFile> filesForAction = null;

    @Inject
    ReplaceViewModel(ReplaceInteractor interactor,
                     Subscriber subscriber,
                     Router router,
                     AppResources appResources,
                     ViewModelsConnection<ReplaceFileTypeViewModel> viewModelsConnection) {
        super(interactor, subscriber, router, appResources, viewModelsConnection);
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.router = router;
        this.appResources = appResources;
        this.viewModelsConnection = viewModelsConnection;

        setHasFixedTitle(true);
    }

    public void setArgs(ReplaceArgs args) {
        filesForAction = args.getFiles();

        isCopyMode = args.isCopyMode();
        int titleId = isCopyMode ? R.string.prompt_title__copy : R.string.prompt_title__replace;
        title.set(appResources.getString(titleId));
    }


    public void onCreateFolder() {
        ReplaceFileTypeViewModel vm = viewModelsConnection.get(getCurrentFileType());
        if (vm != null) {
            vm.onCreateFolder();
        }
    }

    public void onPasteAction() {
        ReplaceFileTypeViewModel vm = viewModelsConnection.get(getCurrentFileType());
        if (vm != null) {
            AuroraFile topFolder = vm.foldersStack.get(0);
            Completable action;
            if (isCopyMode) {
                action =  interactor.copyFiles(topFolder, filesForAction);
            } else {
                action = interactor.replaceFiles(topFolder, filesForAction);
            }
            action.doOnSubscribe(disposable -> progress.set(createProgressViewModel()))
                    .doFinally(() -> progress.set(null))
                    .compose(subscriber::defaultSchedulers)
                    .subscribe(subscriber.subscribe(router::exit));
        }
    }

    protected void onStackChanged(List<AuroraFile> files) {
        super.onStackChanged(files);
        if (files.size() > 1) {
            AuroraFile topFolder = files.get(0);
            subtitle.set(topFolder.getFullPath());
        } else {
            subtitle.set(null);
        }
    }

    private ProgressViewModel createProgressViewModel() {
        int size = filesForAction.size();
        return ProgressViewModel.Factory.indeterminateCircle(
                isCopyMode ? appResources.getString(R.string.prompt_coping) : appResources.getString(R.string.prompt_replacing),
                size == 1 ? filesForAction.get(0).getName() : appResources.getPlurals(R.plurals.prompt_files_count, size, size)
        );
    }
}
