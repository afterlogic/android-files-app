package com.afterlogic.aurora.drive.presentation.modules.filelist.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;

import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.util.FileUtil;
import com.afterlogic.aurora.drive.presentation.modules.filelist.presenter.FileListPresenter;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

import javax.inject.Inject;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileListViewModel implements SwipeRefreshLayout.OnRefreshListener{

    private final AppResources mAppResources;

    private final OptWeakRef<FileListPresenter> mPresenter = OptWeakRef.empty();

    private final ObservableList<FileViewModel> mFiles = new ObservableArrayList<>();
    private final WeakHashMap<AuroraFile, FileViewModel> mFilesMap = new WeakHashMap<>();

    private final ObservableBoolean mRefreshing = new ObservableBoolean(true);

    @Inject
    public FileListViewModel(AppResources appResources) {
        mAppResources = appResources;
    }

    public FileListModel getModel(){
        return new FileListModelImpl();
    }

    public ObservableList<FileViewModel> getFiles(){
        return mFiles;
    }

    public ObservableBoolean getRefreshing(){
        return mRefreshing;
    }

    @Override
    public void onRefresh() {
        mPresenter.ifPresent(FileListPresenter::onRefresh);
    }

    private class FileListModelImpl implements FileListModel{

        @Override
        public void setPresenter(FileListPresenter presenter) {
            mPresenter.set(presenter);
        }

        @Override
        public void setFileList(List<AuroraFile> files) {
            synchronized (FileListViewModel.this){
                mFiles.clear();
                mFilesMap.clear();

                List<FileViewModel> viewModels = Stream.of(files)
                        .map((auroraFile) -> {
                            FileViewModel viewModel = new FileViewModel(auroraFile, mPresenter, mAppResources);
                            mFilesMap.put(auroraFile, viewModel);
                            return viewModel;
                        })
                        .collect(Collectors.toList());

                mFiles.addAll(viewModels);
            }
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
            FileViewModel newModel = new FileViewModel(newFile, mPresenter, mAppResources);
            FileViewModel prevModel = mFilesMap.remove(previous);
            mFiles.set(mFiles.indexOf(prevModel), newModel);
            mFilesMap.put(newFile, newModel);
            Collections.sort(mFiles, (l, r) ->
                    FileUtil.AURORA_FILE_COMPARATOR.compare(l.getModel().getFile(), r.getModel().getFile())
            );
        }

        @Override
        public void removeFile(AuroraFile file) {
            mFiles.remove(mFilesMap.remove(file));
        }

        @Override
        public List<AuroraFile> getFiles() {
            return Stream.of(mFiles)
                    .map(file -> file.getModel().getFile())
                    .collect(Collectors.toList());
        }
    }
}
