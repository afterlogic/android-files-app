package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FilesSelection;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.SimpleOnObservableListChagnedListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesListBiModel;
import com.afterlogic.aurora.drive.presentation.modules.main.presenter.MainFileListPresenter;
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
public class MainFileListBiModel extends BaseFilesListBiModel<MainFileItemViewModel> implements MainFileListViewModel, MainFilesListModel{

    private final Provider<MainFileItemBiModel> mItemProvider;

    private final ObservableList<AuroraFile> mMultiChoiseResult = new ObservableArrayList<>();
    private boolean mMultiChoise = false;
    private ObservableField<FilesSelection> mSelection = new ObservableField<>(new FilesSelection(0, false));

    @Inject
    MainFileListBiModel(OptWeakRef<MainFileListPresenter> presenter, Provider<MainFileItemBiModel> itemProvider) {
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

    private void updateSelected(){
        Stream.of(mFiles).forEach(file -> {
            AuroraFile auroraFile = file.getModel().getFile();
            boolean selected = mMultiChoiseResult.contains(auroraFile);
            file.getSelected().set(selected);
        });
    }

}
