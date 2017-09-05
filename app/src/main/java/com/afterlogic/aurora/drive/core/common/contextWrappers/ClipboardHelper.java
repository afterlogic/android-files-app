package com.afterlogic.aurora.drive.core.common.contextWrappers;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 03.06.17.
 * mail: mail@sunnydaydev.me
 */

public class ClipboardHelper {

    private final ClipboardManager mClipboardManager;

    @Inject
    ClipboardHelper(Context context) {
        mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public void put(String text) {
        mClipboardManager.setPrimaryClip(ClipData.newPlainText("Aurora", text));
    }
}
