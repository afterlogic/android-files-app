package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import android.databinding.ObservableField;
import android.text.TextUtils;

import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.view.BaseFileListArgs;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.BaseFileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor.MainFilesListInteractor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFilesListViewModel extends BaseFileListViewModel
        <MainFilesListViewModel, MainFileViewModel, BaseFileListArgs> {

    private final MainFilesListInteractor interactor;
    private final MainViewModelsConnection viewModelsConnection;

    private ObservableField<String> searchPattern = new ObservableField<>("");

    private OptionalDisposable setSearchQueryDisposable = new OptionalDisposable();

    @Inject
    MainFilesListViewModel(MainFilesListInteractor interactor,
                           Subscriber subscriber,
                           MainViewModelsConnection viewModelsConnection) {
        super(interactor, subscriber, viewModelsConnection);
        this.interactor = interactor;
        this.viewModelsConnection = viewModelsConnection;

        SimpleOnPropertyChangedCallback.addTo(searchPattern, this::reloadCurrentFolder);
    }

    @Override
    protected MainFileViewModel mapFileItem(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        return new MainFileViewModel(file, onItemClickListener);
    }

    @Override
    protected void onFileClicked(AuroraFile file) {
        super.onFileClicked(file);
        viewModelsConnection.fileClickedPublisher.onNext(file);
    }

    void onSearchQuery(String query) {

        String checkedQuery = query == null ? "" : query;

        if (!ObjectsUtil.equals(searchPattern.get(), checkedQuery)) {

            if (TextUtils.isEmpty(checkedQuery)) {

                searchPattern.set(checkedQuery);

            } else {

                setSearchQueryDisposable.disposeAndClear();

                Single.timer(500, TimeUnit.MILLISECONDS)
                        .compose(setSearchQueryDisposable::track)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(tick -> searchPattern.set(checkedQuery));
            }
        }
    }

    @Override
    protected Single<List<AuroraFile>> getFilesSource(AuroraFile folder) {
        return interactor.getFiles(folder, searchPattern.get());
    }
}
