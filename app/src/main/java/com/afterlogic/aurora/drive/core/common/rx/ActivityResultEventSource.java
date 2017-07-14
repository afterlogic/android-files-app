package com.afterlogic.aurora.drive.core.common.rx;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.model.events.ActivityResultEvent;
import com.annimon.stream.Stream;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by sashka on 14.04.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ActivityResultEventSource {

    public static Observable<ActivityResultEvent> create(EventBus eventBus) {
        List<Runnable> finalizers = new ArrayList<>();
        return Observable.<ActivityResultEvent>create(emitter -> {
            ActivityEventHandler handler = new ActivityEventHandler();

            eventBus.register(handler);
            handler.setConsumer(emitter::onNext);

            finalizers.add(() -> eventBus.unregister(handler));
        })//----|
                .doFinally(() -> Stream.of(finalizers).forEach(Runnable::run));
    }

    public static Single<ActivityResultEvent> create(EventBus eventBus, int requestCode) {
        return create(eventBus)
                .filter(result -> result.getRequestId() == requestCode)
                .firstOrError();
    }

    private static class ActivityEventHandler{

        private Consumer<ActivityResultEvent> consumer;

        private ActivityEventHandler() {
        }

        @Subscribe
        public void onEvent(ActivityResultEvent event) {
            consumer.consume(event);
        }

        public void setConsumer(Consumer<ActivityResultEvent> consumer) {
            this.consumer = consumer;
        }
    }
}
