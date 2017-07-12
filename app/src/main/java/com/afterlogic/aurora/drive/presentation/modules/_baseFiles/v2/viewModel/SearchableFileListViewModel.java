package com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel;

import android.databinding.ObservableField;
import android.text.TextUtils;

import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.SearchableFilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.view.BaseFileListArgs;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public abstract class SearchableFileListViewModel<
        FileListVM extends SearchableFileListViewModel,
        FileVM extends AuroraFileViewModel,
        Args extends BaseFileListArgs
> extends FileListViewModel<FileListVM, FileVM, Args> {

    private final SearchableFilesListInteractor interactor;

    private ObservableField<String> searchPattern = new ObservableField<>("");
    private OptionalDisposable setSearchQueryDisposable = new OptionalDisposable();

    protected SearchableFileListViewModel(SearchableFilesListInteractor interactor,
                                          Subscriber subscriber,
                                          ViewModelsConnection<FileListVM> viewModelsConnection) {
        super(interactor, subscriber, viewModelsConnection);
        this.interactor = interactor;

        SimpleOnPropertyChangedCallback.addTo(searchPattern, this::reloadCurrentFolder);
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
