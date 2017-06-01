package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.view.View;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemBiModel;
import com.afterlogic.aurora.drive.presentation.modules.main.model.MainFileItemModel;
import com.afterlogic.aurora.drive.presentation.modules.main.model.presenter.MainFileListPresenter;

import javax.inject.Inject;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFileItemBiModel extends BaseFileItemBiModel implements MainFileItemViewModel, MainFileItemModel{

    private final AppResources mAppResources;
    private final ObservableBoolean mSelected = new ObservableBoolean();
    private final ObservableInt mProgress = new ObservableInt(-1);
    private final ObservableBoolean mOffline = new ObservableBoolean(false);
    private final ObservableBoolean mShared = new ObservableBoolean(false);

    @DrawableRes
    private int mStatusIcon = View.NO_ID;

    @Inject
    MainFileItemBiModel(OptWeakRef<MainFileListPresenter> presenter,
                               AppResources appResources) {
        super(presenter, appResources);
        mAppResources = appResources;

        SimpleListener updateStatusIcon = new SimpleListener(this::updateStatusIcon);
        mProgress.addOnPropertyChangedCallback(updateStatusIcon);
        mOffline.addOnPropertyChangedCallback(updateStatusIcon);
    }

    @Override
    public void setAuroraFile(AuroraFile file) {
        super.setAuroraFile(file);
        mShared.set(file != null && file.isShared());
    }

    @Override
    public MainFileItemModel getModel() {
        return this;
    }

    @Override
    public ObservableBoolean getSelected() {
        return mSelected;
    }

    @Override
    public ObservableInt getProgress() {
        return mProgress;
    }

    @Override
    public ObservableBoolean getOffline() {
        return mOffline;
    }

    @Override
    public ObservableBoolean getShared() {
        return mShared;
    }

    @Override
    public void setOffline(boolean offline) {
        mOffline.set(offline);
    }

    @Override
    public void setSyncProgress(@IntRange(from = -1, to = 100) int progress) {
        mProgress.set(progress);
    }

    private void updateStatusIcon(){
        @DrawableRes int icon;
        if (mOffline.get()){
            if (getProgress().get() != -1){
                icon = R.drawable.ic_sync;
            } else {
                icon = R.drawable.ic_offline;
            }
        } else {
            icon = View.NO_ID;
        }

        if (mStatusIcon == icon) return;
        mStatusIcon = icon;

        Uri iconUri = mStatusIcon == View.NO_ID ? null : mAppResources.getResourceUri(mStatusIcon);
        getStatusIcon().set(iconUri);
    }
}
