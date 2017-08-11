package com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core;

import android.Manifest;
import android.os.Build;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

public enum  PermissionRequest {

    READ_AND_WRITE_STORAGE(3, new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Build.VERSION.SDK_INT >= 16
                    ? Manifest.permission.READ_EXTERNAL_STORAGE
                    : "android.permission.READ_EXTERNAL_STORAGE"
    }),

    WRITE_STORAGE(2, new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    }),

    READ_STORAGE(1, new String[]{
            Build.VERSION.SDK_INT >= 16
                    ? Manifest.permission.READ_EXTERNAL_STORAGE
                    : "android.permission.READ_EXTERNAL_STORAGE"
    });

    private final int requestCode;
    private final String[] permissions;

    PermissionRequest(int requestCode, String[] permissions) {
        this.requestCode = requestCode;
        this.permissions = permissions;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public String[] getPermissions() {
        return permissions;
    }
}
