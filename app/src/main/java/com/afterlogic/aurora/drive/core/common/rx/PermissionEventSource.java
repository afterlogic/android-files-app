package com.afterlogic.aurora.drive.core.common.rx;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.model.events.PermissionGrantEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.reactivex.Observable;

/**
 * Created by sashka on 14.04.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class PermissionEventSource {

    public static Observable<PermissionGrantEvent> create(EventBus eventBus) {
        PermissionEventHandler handler = new PermissionEventHandler();
        return Observable.<PermissionGrantEvent>create(emitter -> {
            eventBus.register(handler);
            handler.setConsumer(emitter::onNext);
        })//----|
                .doFinally(() -> eventBus.unregister(handler));
    }

    private static class PermissionEventHandler {

        private Consumer<PermissionGrantEvent> consumer;

        private PermissionEventHandler() {
        }

        @Subscribe
        public void onEvent(PermissionGrantEvent event) {
            consumer.consume(event);
        }

        public void setConsumer(Consumer<PermissionGrantEvent> consumer) {
            this.consumer = consumer;
        }
    }
}
