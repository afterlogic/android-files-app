package com.afterlogic.aurora.drive.model.events;

import android.content.pm.PackageManager;

/**
 * Created by sashka on 12.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class PermissionGrantEvent extends HandableRequestEvent {

    public static final int ON_ERROR = 1;
    public static final int FILES_STORAGE_ACCESS = 2;

    private String[] mPermissions;
    private int[] mGrantResults;

    public PermissionGrantEvent(int requestId, String[] permissions, int[] grantResults) {
        super(requestId);
        mPermissions = permissions;
        mGrantResults = grantResults;
    }

    public boolean isAllGranted(){
        for (int res:mGrantResults){
            if (res != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    public String[] getPermissions() {
        return mPermissions;
    }

    public int[] getGrantResults() {
        return mGrantResults;
    }
}
