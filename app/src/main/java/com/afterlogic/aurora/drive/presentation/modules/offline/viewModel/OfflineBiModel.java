package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.BaseViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.dialog.MessageDialogViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.dialog.ProgressDialogViewModel;
import com.afterlogic.aurora.drive.presentation.common.util.LazyProvider;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemViewModel;
import com.afterlogic.aurora.drive.presentation.modules.offline.model.OfflineModel;
import com.afterlogic.aurora.drive.presentation.modules.offline.model.presenter.OfflinePresenter;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
public class OfflineBiModel extends BaseViewModel implements OfflineViewModel {

    private final Context mAppContext;
    private final AppResources mAppResources;
    private final LazyProvider<OfflinePresenter> mPresenter;
    private final Provider<OfflineFileItemBiModel> mItemProvider;

    private final Map<AuroraFile, OfflineFileItemBiModel> mItemsMap = new HashMap<>();
    private final ObservableList<BaseFileItemViewModel> mItems = new ObservableArrayList<>();

    private final ObservableField<ProgressDialogViewModel> mProgress = new ObservableField<>();
    private final ObservableBoolean mManualMode = new ObservableBoolean();
    private final ObservableBoolean mRefreshing = new ObservableBoolean();
    private final ObservableField<MessageDialogViewModel> mMessage = new ObservableField<>();

    @Inject
    OfflineBiModel(Context appContext, AppResources appResources, Provider<OfflineFileItemBiModel> itemProvider, LazyProvider<OfflinePresenter> presenter) {
        mAppContext = appContext;
        mAppResources = appResources;
        mItemProvider = itemProvider;
        mPresenter = presenter;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        mPresenter.init();
        mPresenter.ifPresent(OfflinePresenter::checkAuth);
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
    public void viewInitWith(boolean manualMode) {
        mManualMode.set(manualMode);
    }

    @NonNull
    @Override
    public ObservableField<ProgressDialogViewModel> getProgress() {
        return mProgress;
    }

    @NonNull
    @Override
    public ObservableList<BaseFileItemViewModel> getItems() {
        return mItems;
    }

    @NonNull
    @Override
    public ObservableBoolean getManualMode() {
        return mManualMode;
    }

    @NonNull
    @Override
    public ObservableBoolean getRefreshing() {
        return mRefreshing;
    }

    @NonNull
    @Override
    public ObservableField<MessageDialogViewModel> getMessage() {
        return mMessage;
    }

    @Override
    public void onItemClicked(BaseFileItemViewModel item) {
        mPresenter.ifPresent(presenter -> presenter.openFile(item.getModel().getFile()));
    }

    @Override
    public void onOnline() {
        mPresenter.ifPresent(OfflinePresenter::onGoToOnline);
    }

    public OfflineModel getModel(){
        return new Model();
    }

    @Override
    public void onRefresh() {
        if (mManualMode.get()) {
            mPresenter.ifPresent(OfflinePresenter::refresh);
        } else {
            mPresenter.ifPresent(OfflinePresenter::onGoToOnline);
        }
    }

    private class Model implements OfflineModel {

        @Override
        public void notifyRefreshing(boolean refreshing) {
            mRefreshing.set(refreshing);
        }

        @Override
        public void setFiles(List<AuroraFile> files) {
            mItemsMap.clear();
            List<BaseFileItemViewModel> models = Stream.of(files)
                    .map(file -> {
                        OfflineFileItemBiModel model = mItemProvider.get();
                        model.setAuroraFile(file);
                        mItemsMap.put(file, model);
                        return model;
                    })
                    .collect(Collectors.toList());
            mItems.clear();
            mItems.addAll(models);
        }

        @Override
        public void notifyLoadProgress(AuroraFile file, int max, int progress) {
            mProgress.set(new ProgressDialogViewModel(
                    mAppResources.getString(R.string.dialog_downloading),
                    file.getName(),
                    max,
                    progress,
                    () -> mPresenter.ifPresent(OfflinePresenter::cancelCurrentLoad)
            ));
        }

        @Override
        public void notifyLoadFinished() {
            mProgress.set(null);
        }

        @Override
        public void setThumb(AuroraFile file, Uri thumb) {
            mItemsMap.get(file).setThumbNail(thumb);
        }

        @Override
        public void onCantOpenFile(AuroraFile file) {
            mMessage.set(new MessageDialogViewModel(
                    null,
                    mAppResources.getString(R.string.prompt_cant_open_file),
                    () -> mMessage.set(null)
            ));
        }

        @Override
        public void onErrorObtained(Throwable error) {
            Toast.makeText(mAppContext, error.getMessage(), Toast.LENGTH_LONG).show();
            MyLog.majorException(error);
        }

        @Override
        public void onFileLoadError(AuroraFile file) {
            mMessage.set(new MessageDialogViewModel(
                    null,
                    mAppResources.getString(R.string.prompt_offline_file_not_exist),
                    () -> mMessage.set(null)
            ));
        }
    }
}
