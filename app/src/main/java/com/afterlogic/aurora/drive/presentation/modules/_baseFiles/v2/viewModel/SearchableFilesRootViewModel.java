package com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel;

import android.text.TextUtils;

import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.Bindable;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.FilesRootInteractor;
import com.annimon.stream.Stream;

import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class SearchableFilesRootViewModel<
        FileListVM extends SearchableFileListViewModel<FileListVM, ?, ?>
> extends FilesRootViewModel<FileListVM> {

    public final Bindable<String> searchQuery = Bindable.create("");
    public final Bindable<Boolean> showSearch = Bindable.create(false);

    private final ViewModelsConnection<FileListVM> viewModelsConnection;

    protected SearchableFilesRootViewModel(FilesRootInteractor interactor,
                                           Subscriber subscriber,
                                           Router router,
                                           AppResources appResources,
                                           ViewModelsConnection<FileListVM> viewModelsConnection) {
        super(interactor, subscriber, router, appResources, viewModelsConnection);
        this.viewModelsConnection = viewModelsConnection;

        SimpleOnPropertyChangedCallback.addTo(searchQuery, this::onSearchQueryChanged);
        SimpleOnPropertyChangedCallback.addTo(showSearch, this::onShowSearchChanged);
    }

    @Override
    public void onBackPressed() {
        if (showSearch.get()) {
            showSearch.set(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onFileClicked(AuroraFile file) {
        super.onFileClicked(file);
        showSearch.set(false);
    }

    private void onSearchQueryChanged() {
        String query = searchQuery.get();
        if (fileTypesLocked.get() && !TextUtils.isEmpty(query)) {
            setSearchQueryForType(getCurrentFileType(), query);
        } else {
            Stream.of(fileTypes)
                    .map(FileType::getFilesType)
                    .forEach(type -> setSearchQueryForType(type, query));
        }
    }

    private void setSearchQueryForType(String type, String query) {
        FileListVM vm = viewModelsConnection.get(type);
        if (vm != null) {
            vm.onSearchQuery(query);
        }
    }

    private void onShowSearchChanged() {
        if (!showSearch.get()) {
            searchQuery.set("");
        }
    }
}
