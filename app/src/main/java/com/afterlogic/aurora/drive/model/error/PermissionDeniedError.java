package com.afterlogic.aurora.drive.model.error;

import com.afterlogic.aurora.drive.model.events.PermissionGrantEvent;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

/**
 * Created by sashka on 08.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class PermissionDeniedError extends BaseError{
    public static final int CODE = -4;

    private final String[] mPermissions;
    private int mRequestCode = PermissionGrantEvent.ON_ERROR;
    private boolean mHandled = false;

    public PermissionDeniedError(String... permissions) {
        super("Permissions denied: " + Stream.of(permissions)
                .collect(Collectors.joining(", ", "", ".", "not setted."))
        );
        mPermissions = permissions;
    }

    public PermissionDeniedError(int requestCode, String... permissions) {
        this(permissions);
        mRequestCode = requestCode;
    }

    public void setRequestCode(int requestCode) {
        mRequestCode = requestCode;
    }

    @Override
    public int getErrorCode() {
        return CODE;
    }

    public String[] getPermissions() {
        return mPermissions;
    }

    public int getRequestCode() {
        return mRequestCode;
    }

    public void setHandled(boolean handled) {
        mHandled = handled;
    }

    public boolean isHandled() {
        return mHandled;
    }
}
