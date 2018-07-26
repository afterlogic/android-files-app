package com.afterlogic.aurora.drive.presentation.common.util;

import androidx.annotation.NonNull;

import com.afterlogic.aurora.drive.model.events.PermissionGrantEvent;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnEventListener;
import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by sashka on 14.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class PermisionResultListener implements Stoppable {

    private EventBus mEventBus = EventBus.getDefault();

    private OnEventListener<PermissionGrantEvent> mOnEventListener;

    public PermisionResultListener(@NonNull OnEventListener<PermissionGrantEvent> onEventListener) {
        mOnEventListener = onEventListener;
    }

    @Override
    public void onStart() {
        if (!mEventBus.isRegistered(this)){
            mEventBus.register(this);
        }
    }

    @Override
    public void onStop() {
        if (mEventBus.isRegistered(this)){
            mEventBus.unregister(this);
        }
    }

    @Subscribe
    public void onEvent(PermissionGrantEvent event){
        mOnEventListener.onEvent(event);
    }

}
