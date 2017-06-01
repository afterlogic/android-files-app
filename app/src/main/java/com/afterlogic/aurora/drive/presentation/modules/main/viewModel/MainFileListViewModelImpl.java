package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FilesSelection;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.SimpleOnObservableListChagnedListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesListBiModel;
import com.afterlogic.aurora.drive.presentation.modules.main.model.MainFilesListModel;
import com.afterlogic.aurora.drive.presentation.modules.main.model.presenter.MainFileListPresenter;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
public class MainFileListViewModelImpl extends BaseFilesListBiModel<MainFileItemViewModel> implements MainFileListViewModel, MainFilesListModel {

    private final Provider<MainFileItemBiModel> mItemProvider;
    private boolean mMultiChoise = false;
    private AuroraFile mFileForActions;

    private final ObservableList<AuroraFile> mMultiChoiseResult = new ObservableArrayList<>();
    private final ObservableField<FilesSelection> mSelection = new ObservableField<>(new FilesSelection(0, false));
    private final ObservableField<MainFileItemViewModel> mFileForActionModel = new ObservableField<>();
    private final ObservableBoolean mActionsEnabled = new ObservableBoolean(true);

    @Inject
    MainFileListViewModelImpl(OptWeakRef<MainFileListPresenter> presenter, Provider<MainFileItemBiModel> itemProvider) {
        super(presenter);
        mItemProvider = itemProvider;
        mMultiChoiseResult.addOnListChangedCallback(new SimpleOnObservableListChagnedListener<>(
                () -> {
                    updateSelected();
                    mSelection.set(new FilesSelection(
                            mMultiChoiseResult.size(),
                            Stream.of(mMultiChoiseResult).anyMatch(AuroraFile::isFolder)
                    ));
                }
        ));
    }

    public MainFilesListModel getModel(){
        return this;
    }

    @Override
    public ObservableField<FilesSelection> getSelection() {
        return mSelection;
    }

    @Override
    public ObservableField<MainFileItemViewModel> getFileRequeireActions() {
        return mFileForActionModel;
    }

    @Override
    public ObservableBoolean getActionsEnabled() {
        return mActionsEnabled;
    }

    @Override
    public void onCancelFileActions() {
        setFileForActions(null);
    }

    @Override
    protected MainFileItemViewModel viewModel(AuroraFile file) {
        MainFileItemBiModel model = mItemProvider.get();
        model.setAuroraFile(file);
        return model;
    }

    @Override
    public void setFileList(List<AuroraFile> files) {
        mMultiChoiseResult.clear();
        super.setFileList(files);
    }

    @Override
    public void setMultiChoiseMode(boolean mode) {
        if (mode == mMultiChoise) return;
        mMultiChoise = mode;

        if (!mMultiChoise){
            mMultiChoiseResult.clear();
        }
    }

    @Override
    public boolean isMultiChoise() {
        return mMultiChoise;
    }

    @Override
    public List<AuroraFile> getMultiChoise() {
        return new ArrayList<>(mMultiChoiseResult);
    }

    @Override
    public void toggleSelected(AuroraFile file) {
        if (mMultiChoiseResult.contains(file)){
            mMultiChoiseResult.remove(file);
        } else {
            mMultiChoiseResult.add(file);
        }
    }

    @Override
    public void setFileForActions(AuroraFile file) {
        mFileForActions = file;
        mFileForActionModel.set(getModel(file));
    }

    @Override
    public AuroraFile getFileForActions() {
        return mFileForActions;
    }

    @Override
    public void clearSyncProgress() {
        Stream.of(mFiles).forEach(item -> item.getModel().setSyncProgress(-1));
    }

    @Override
    public void setSyncProgress(SyncProgress progress) {
        Stream.of(mFiles)
                .map(MainFileItemViewModel::getModel)
                .filter(model -> {
                    AuroraFile file = model.getFile();
                    return file.getPathSpec().equals(progress.getFile());
                })
                .findFirst()
                .ifPresent(model -> {
                    int progressValue = progress.isDone() ? -1 : progress.getProgress();
                    model.setSyncProgress(progressValue);
                });
    }

    @Override
    public void setOffline(AuroraFile file, boolean offline) {
        ifModel(file, viewModel -> viewModel.getModel().setOffline(offline));
    }

    @Override
    public void setActionsEnabled(boolean enabled) {
        mActionsEnabled.set(enabled);
    }

    private void updateSelected(){
        Stream.of(mFiles).forEach(file -> {
            AuroraFile auroraFile = file.getModel().getFile();
            boolean selected = mMultiChoiseResult.contains(auroraFile);
            file.getSelected().set(selected);
        });
    }

}
