package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

import android.databinding.ObservableField;

import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.application.navigation.args.ExternalOpenFIleArgs;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.SearchableFileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;
import com.afterlogic.aurora.drive.presentation.modules.offline.interactor.OfflineFileListInteractor;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineFileListViewModel extends SearchableFileListViewModel<OfflineFileListViewModel, OfflineFileViewModel, OfflineArgs> {

    public final ObservableField<OfflineHeader> header = new ObservableField<>();

    private final OfflineFileListInteractor interactor;
    private final Subscriber subscriber;
    private final FileMapper mapper;
    private final AppRouter router;

    private final OptionalDisposable thumbnailsDisposable = new OptionalDisposable();

    @Inject
    OfflineFileListViewModel(OfflineFileListInteractor interactor,
                             Subscriber subscriber,
                             ViewModelsConnection<OfflineFileListViewModel> viewModelsConnection,
                             FileMapper mapper,
                             AppRouter router) {
        super(interactor, subscriber, viewModelsConnection);
        this.mapper = mapper;
        this.subscriber = subscriber;
        this.interactor = interactor;
        this.router = router;
    }

    @Override
    public void setArgs(OfflineArgs args) {
        super.setArgs(args);
        header.set(new OfflineHeader(args.isManual()));
    }

    @Override
    protected void handleFiles(List<AuroraFile> files) {
        mapper.clear();
        super.handleFiles(files);
        updateThumbnails(files);
    }

    @Override
    protected OfflineFileViewModel mapFileItem(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        return mapper.map(file, onItemClickListener);
    }

    @Override
    protected void onFileClick(AuroraFile file) {
        interactor.downloadForOpen(file)
                .compose(subscriber::defaultSchedulers)
                .filter(Progressible::isDone)
                .map(Progressible::getData)
                .subscribe(subscriber.subscribe(localFile -> {
                    ExternalOpenFIleArgs args = new ExternalOpenFIleArgs(file, localFile);
                    router.navigateTo(AppRouter.EXTERNAL_OPEN_FILE, args);
                }));
    }

    private void updateThumbnails(List<AuroraFile> files) {
        Stream.of(files)
                .map(file -> interactor.getThumbnail(file)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess(thumbnail -> {
                            OfflineFileViewModel vm = mapper.get(file);
                            if (vm != null) {
                                vm.setThumbnail(thumbnail);
                            }
                        })
                        .toCompletable()
                        .onErrorComplete()
                )
                .collect(Observables.Collectors.concatCompletable())
                .compose(thumbnailsDisposable::disposeAndTrack)
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.justSubscribe());
    }
}
