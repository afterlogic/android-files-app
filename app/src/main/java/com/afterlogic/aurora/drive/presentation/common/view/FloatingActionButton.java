package com.afterlogic.aurora.drive.presentation.common.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewParent;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

/**
 * Created by aleksandrcikin on 14.07.17.
 * mail: mail@sunnydaydev.me
 */

public class FloatingActionButton extends com.getbase.floatingactionbutton.FloatingActionButton {

    private boolean autoCloseMenu = false;

    public FloatingActionButton(Context context) {
        super(context);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAutoCollapseMenu(boolean autoCloseMenu) {
        this.autoCloseMenu = autoCloseMenu;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        if (l == null){
            super.setOnClickListener(null);
        } else {
            super.setOnClickListener(v -> {
                if (autoCloseMenu) {
                    ViewParent parent = getParent();
                    if (parent instanceof FloatingActionsMenu) {
                        ((FloatingActionsMenu) parent).collapse();
                    }
                }
                l.onClick(v);
            });
        }
    }
}
