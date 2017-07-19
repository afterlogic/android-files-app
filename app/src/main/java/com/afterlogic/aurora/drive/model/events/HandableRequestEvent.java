package com.afterlogic.aurora.drive.model.events;

import com.afterlogic.aurora.drive.presentation.common.interfaces.HandableEvent;

/**
 * Created by sashka on 09.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@SuppressWarnings("WeakerAccess")
class HandableRequestEvent implements HandableEvent {

    private boolean mIsHandled = false;
    private int mRequestId;

    public HandableRequestEvent(int requestId) {
        mRequestId = requestId;
    }


    @Override
    public boolean handle(int requestId) {
        if (!mIsHandled && mRequestId == requestId){
            mIsHandled = true;
            return true;
        } else {
            return false;
        }
    }

    public int getRequestCode() {
        return mRequestId;
    }
}
