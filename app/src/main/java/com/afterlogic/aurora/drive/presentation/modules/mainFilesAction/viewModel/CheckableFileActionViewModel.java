package com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.viewModel;

import androidx.databinding.Bindable;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class CheckableFileActionViewModel extends ButtonFileActionViewModel {

    private boolean checked;
    private final OnCheckChangedListener onCheckChangedListener;

    public CheckableFileActionViewModel(AppResources appResources, int textId, int iconId,  boolean checked, OnCheckChangedListener onCheckChangedListener) {
        super(appResources, textId, iconId, null);
        this.checked = checked;
        this.onCheckChangedListener = onCheckChangedListener;
    }

    @Bindable
    public boolean getChecked() {
        return checked;
    }

    @Override
    public void onClick() {
        onCheckChanged(!checked);
    }

    void setChecked(boolean checked) {
        this.checked = checked;
        notifyPropertyChanged(BR.checked);
    }

    public void onCheckChanged(boolean value) {
        if (value == checked) return;

        checked = value;
        notifyPropertyChanged(BR.checked);
        onCheckChangedListener.onCheckChanged(checked);
    }

    public interface OnCheckChangedListener {
        void onCheckChanged(boolean value);
    }
}
