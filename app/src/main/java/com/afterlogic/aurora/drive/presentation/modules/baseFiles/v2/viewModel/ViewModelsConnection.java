package com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.model.AuroraFile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

@ModuleScope
public class ViewModelsConnection<FilesListVM extends FileListViewModel> {

    PublishSubject<AuroraFile> fileClickedPublisher = PublishSubject.create();

    private Map<String, FilesListVM> filesListViewModels = new HashMap<>();

    private OnChangedListener<FilesListVM> listener;

    @Inject
    protected ViewModelsConnection() {
    }

    public void setListener(OnChangedListener<FilesListVM> listener) {
        this.listener = listener;
    }

    synchronized public void register(String type, @NonNull FilesListVM vm) {
        FilesListVM previous = filesListViewModels.remove(type);
        if (previous != null) {
            listener.onUnregistered(previous);
        }
        filesListViewModels.put(type, vm);
        if (listener != null) {
            listener.onRegistered(type, vm);
        }
    }

    synchronized public void unregister(@NonNull FilesListVM vm) {
        Iterator<Map.Entry<String, FilesListVM>> iterator  = filesListViewModels.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, FilesListVM> entry = iterator.next();
            if (entry.getValue() == vm) {
                iterator.remove();
                if (listener != null) {
                    listener.onUnregistered(vm);
                }
            }
        }
    }

    @Nullable
    synchronized public FilesListVM get(String type) {
        return filesListViewModels.get(type);
    }

    public interface OnChangedListener<FilesListVM extends FileListViewModel> {
        void onRegistered(String type, FilesListVM vm);
        void onUnregistered(FilesListVM vm);
    }
}
