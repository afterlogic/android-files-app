package com.afterlogic.aurora.drive.model.events;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by sashka on 09.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ActivityResultEvent extends HandableRequestEvent {
    public static final int CAMERA = 2;
    public static final int GALERY = 3;
    public static final int RINGTONE_CHOOSER = 4;

    private int mResultCode;
    private Intent data;

    public ActivityResultEvent(int requestId, int resultCode, Intent data) {
        super(requestId);
        mResultCode = resultCode;
        this.data = data;
    }

    public int getResultCode() {
        return mResultCode;
    }

    public Intent getResult() {
        return data;
    }

    public boolean isSuccess(){
        return mResultCode == Activity.RESULT_OK;
    }
}
