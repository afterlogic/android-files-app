package com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.viewModel;

import androidx.databinding.BaseObservable;
import android.net.Uri;
import android.view.View;

import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ButtonFileActionViewModel extends BaseObservable implements FileActionViewModel {

    private final AppResources appResources;
    private final int textId;
    private final int iconId;
    private final OnClickListener onClickListener;

    public ButtonFileActionViewModel(AppResources appResources, int textId, int iconId, OnClickListener onClickListener) {
        this.appResources = appResources;
        this.textId = textId;
        this.iconId = iconId;
        this.onClickListener = onClickListener;
    }

    public String getText() {
        return appResources.getString(textId);
    }

    public Uri getIcon() {
        if (iconId == View.NO_ID) {
            return null;
        } else {
            return appResources.getResourceUri(iconId);
        }
    }

    public void onClick() {
        if (onClickListener != null) {
            onClickListener.onClick();
        }
    }

    public interface OnClickListener {
        void onClick();
    }
}
