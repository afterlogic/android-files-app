package com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.viewModel;

import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class FileActionCreator {

    private final AppResources appResources;

    @Inject
    public FileActionCreator(AppResources appResources) {
        this.appResources = appResources;
    }

    public ButtonFileActionViewModel button(int title, int icon, ButtonFileActionViewModel.OnClickListener listener) {
        return new ButtonFileActionViewModel(appResources, title, icon, listener);
    }

    public CheckableFileActionViewModel checkable(int title, int icon, boolean checked, CheckableFileActionViewModel.OnCheckChangedListener listener) {
        return new CheckableFileActionViewModel(appResources, title, icon, checked, listener);
    }
}
