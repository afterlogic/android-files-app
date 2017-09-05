package com.afterlogic.aurora.drive.presentation.common.view;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by aleksandrcikin on 13.07.17.
 * mail: mail@sunnydaydev.me
 */

public class AppProgressDialog extends ProgressDialog {

    private int progressStyle = STYLE_SPINNER;

    public AppProgressDialog(Context context) {
        super(context);
    }

    public AppProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void setProgressStyle(int style) {
        super.setProgressStyle(style);
        progressStyle = style;
    }

    public int getProgressStyle() {
        return progressStyle;
    }
}
