package com.afterlogic.aurora.drive.presentation.modules.baseFiles.viewModel;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import android.net.Uri;
import androidx.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.BaseFilesListModel;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.presenter.FilesListPresenter;
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

    protected final ObservableList<T> mFiles = new ObservableArrayList<>();
    private final WeakHashMap<AuroraFile, T> mFilesMap = new WeakHashMap<>();
    private final ObservableBoolean mRefreshing = new ObservableBoolean(true);
    private final ObservableField<AuroraFile> mCurrentFolder = new ObservableField<>();
    private final ObservableBoolean mErrorState = new ObservableBoolean();

    public BaseFilesListBiModel(OptWeakRef<? extends FilesListPresenter> presenter) {
        mPresenter = presenter;
    }

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
    public ObservableBoolean getErrorState() {
        return mErrorState;
    }

    @Override
    public void onRefresh() {
        mPresenter.ifPresent(FilesListPresenter::onRefresh);
    }

    @Override
    public void setFileList(List<AuroraFile> files) {
        mErrorState.set(false);
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

    @Override
    public void setErrorState(boolean errorState) {
        mErrorState.set(errorState);
    }

    protected abstract T viewModel(AuroraFile file);

    @Nullable
    protected T getModel(AuroraFile file){
        if (file == null) return null;

        return mFilesMap.get(file);
    }

    protected void ifModel(AuroraFile file, Consumer<T> consumer){
        T model = getModel(file);
        if (model != null){
            consumer.consume(model);
        }
    }
}
