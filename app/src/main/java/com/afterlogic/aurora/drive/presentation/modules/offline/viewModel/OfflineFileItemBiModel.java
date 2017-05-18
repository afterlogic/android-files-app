package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

import android.view.View;

import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemBiModel;

import javax.inject.Inject;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class OfflineFileItemBiModel extends BaseFileItemBiModel{

    private final OfflineViewModel mRoot;

    @Inject
    public OfflineFileItemBiModel(AppResources appResources, OfflineViewModel root) {
        super(OptWeakRef.empty(), appResources);
        mRoot = root;
    }

    @Override
    public void onClick(View view) {
        mRoot.onItemClicked(this);
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}
