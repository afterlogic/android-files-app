package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.BaseViewModel;
import com.afterlogic.aurora.drive.presentation.common.util.LazyProvider;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemViewModel;
import com.afterlogic.aurora.drive.presentation.modules.offline.model.OfflineModel;
import com.afterlogic.aurora.drive.presentation.modules.offline.model.OfflinePresenter;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
public class OfflineBiModel extends BaseViewModel implements OfflineViewModel {

    private final ObservableList<BaseFileItemViewModel> mItems = new ObservableArrayList<>();

    private final Provider<OfflineFileItemBiModel> mItemProvider;

    private final LazyProvider<OfflinePresenter> mPresenter;

    @Inject
    public OfflineBiModel(Provider<OfflineFileItemBiModel> itemProvider, LazyProvider<OfflinePresenter> presenter) {
        mItemProvider = itemProvider;
        mPresenter = presenter;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        mPresenter.init();
        if (mItems.size() == 0){
            mPresenter.ifPresent(OfflinePresenter::refresh);
        }
    }

    @Override
    public void onViewDestroyed() {
        super.onViewDestroyed();
        mPresenter.reset();
    }

    @Override
    public ObservableList<BaseFileItemViewModel> getItems() {
        return mItems;
    }

    @Override
    public void onItemClicked(BaseFileItemViewModel item) {

    }

    public OfflineModel getModel(){
        return new Model();
    }

    private void setFiles(List<AuroraFile> files){
        List<BaseFileItemViewModel> models = Stream.of(files)
                .map(file -> {
                    OfflineFileItemBiModel model = mItemProvider.get();
                    model.setAuroraFile(file);
                    return model;
                })
                .collect(Collectors.toList());
        mItems.clear();
        mItems.addAll(models);
    }

    private class Model implements OfflineModel {

        @Override
        public void setFiles(List<AuroraFile> files) {
            OfflineBiModel.this.setFiles(files);
        }
    }
}
