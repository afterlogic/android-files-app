package com.afterlogic.aurora.drive.presentation.common.util;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.model.events.ActivityResultEvent;
import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by sashka on 14.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ActivityResultListener implements Stoppable {

    private EventBus mEventBus = EventBus.getDefault();
    private boolean mRequestCodeSetted = false;
    private int mRequestCode;

    private Consumer<ActivityResultEvent> mOnEventListener;

    public ActivityResultListener(@NonNull Consumer<ActivityResultEvent> onEventListener) {
        mOnEventListener = onEventListener;
    }

    public ActivityResultListener(int requestCode, @NonNull Consumer<ActivityResultEvent> onEventListener) {
        mOnEventListener = onEventListener;
        mRequestCode = requestCode;
        mRequestCodeSetted = true;
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
    public void onEvent(ActivityResultEvent event){
        if (!mRequestCodeSetted || event.handle(mRequestCode)) {
            mOnEventListener.consume(event);
        }
    }

}
