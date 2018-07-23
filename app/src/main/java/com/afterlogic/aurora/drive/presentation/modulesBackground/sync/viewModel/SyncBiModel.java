package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel;

import androidx.databinding.ObservableField;
import androidx.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;

import javax.inject.Inject;

/**
 * Created by sashka on 15.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
public class SyncBiModel implements SyncViewModel, SyncModel {

    private final ObservableField<SyncProgress> mCurrentProgress = new ObservableField<>();

    @Inject SyncBiModel() {
    }

    @Override
    public void setCurrentSyncProgress(@Nullable SyncProgress progress) {
        mCurrentProgress.set(progress);
    }

    @Override
    public ObservableField<SyncProgress> getCurrentSyncProgress() {
        return mCurrentProgress;
    }
}
