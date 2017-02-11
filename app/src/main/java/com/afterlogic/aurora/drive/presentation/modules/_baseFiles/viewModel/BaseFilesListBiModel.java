package com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.net.Uri;

import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.util.FileUtil;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.presenter.FilesListPresenter;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class BaseFilesListBiModel<T extends BaseFileItemViewModel> implements BaseFilesListViewModel<T>, BaseFilesListModel {

    private final OptWeakRef<? extends FilesListPresenter> mPresenter;

    private final ObservableList<T> mFiles = new ObservableArrayList<>();
    private final WeakHashMap<AuroraFile, T> mFilesMap = new WeakHashMap<>();
    private final ObservableBoolean mRefreshing = new ObservableBoolean(true);
    private final ObservableField<AuroraFile> mCurrentFolder = new ObservableField<>();

    public BaseFilesListBiModel(OptWeakRef<? extends FilesListPresenter> presenter) {
        mPresenter = presenter;
    }

    @Override
    public BaseFilesListModel getModel() {
        return this;
    }

    @Override
    public ObservableList<T> getItems() {
        return mFiles;
    }

    @Override
    public ObservableField<AuroraFile> getCurrentFolder() {
        return mCurrentFolder;
    }

    @Override
    public ObservableBoolean getRefreshing() {
        return mRefreshing;
    }

    @Override
    public void onRefresh() {
        mPresenter.ifPresent(FilesListPresenter::onRefresh);
    }

    @Override
    public void setFileList(List<AuroraFile> files) {
        synchronized (this){
            mFiles.clear();
            mFilesMap.clear();

            List<T> viewModels = Stream.of(files)
                    .map((auroraFile) -> {
                        T viewModel = viewModel(auroraFile);
                        mFilesMap.put(auroraFile, viewModel);
                        return viewModel;
                    })
                    .collect(Collectors.toList());

            mFiles.addAll(viewModels);
        }
    }

    @Override
    public void setCurrentFolder(AuroraFile folder) {
        mCurrentFolder.set(folder);
    }

    @Override
    public void setRefreshing(boolean isRefreshing) {
        mRefreshing.set(isRefreshing);
    }

    @Override
    public void setThumbNail(AuroraFile file, Uri thumbUri) {
        mFilesMap.get(file).getModel().setThumbNail(thumbUri);
    }

    @Override
    public void changeFile(AuroraFile previous, AuroraFile newFile) {
        T newModel = viewModel(newFile);
        T prevModel = mFilesMap.remove(previous);
        mFiles.set(mFiles.indexOf(prevModel), newModel);
        mFilesMap.put(newFile, newModel);
        Collections.sort(mFiles, (l, r) ->
                FileUtil.AURORA_FILE_COMPARATOR.compare(l.getModel().getFile(), r.getModel().getFile())
        );
    }

    @Override
    public void removeFile(AuroraFile file) {
        T viewModel = mFilesMap.get(file);
        mFiles.remove(viewModel);
    }

    @Override
    public void addFile(AuroraFile file) {
        T newModel = viewModel(file);
        mFiles.add(newModel);
        Collections.sort(mFiles, (l, r) ->
                FileUtil.AURORA_FILE_COMPARATOR.compare(l.getModel().getFile(), r.getModel().getFile())
        );
        mFilesMap.put(file, newModel);
    }

    @Override
    public List<AuroraFile> getFiles() {
        return Stream.of(mFiles)
                .map(file -> file.getModel().getFile())
                .collect(Collectors.toList());
    }

    protected abstract T viewModel(AuroraFile file);
}
