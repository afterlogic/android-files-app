package com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.interactor.rx;

import android.content.Context;
import android.os.PowerManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;

/**
 * Created by aleksandrcikin on 27.07.17.
 * mail: mail@sunnydaydev.me
 */

public class WakeLockTransformer<T> implements ObservableTransformer<T, T>, SingleTransformer<T, T>, MaybeTransformer<T, T>, CompletableTransformer {

    private static int sWakeLockId = 0;

    private final Context appContext;

    public WakeLockTransformer(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        AtomicReference<PowerManager.WakeLock> ref = new AtomicReference<>();
        return upstream
                .doOnSubscribe(disposable -> startWakeLock(ref))
                .doFinally(() -> stopWakeLock(ref));
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        AtomicReference<PowerManager.WakeLock> ref = new AtomicReference<>();
        return upstream
                .doOnSubscribe(disposable -> startWakeLock(ref))
                .doFinally(() -> stopWakeLock(ref));
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        AtomicReference<PowerManager.WakeLock> ref = new AtomicReference<>();
        return upstream
                .doOnSubscribe(disposable -> startWakeLock(ref))
                .doFinally(() -> stopWakeLock(ref));
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        AtomicReference<PowerManager.WakeLock> ref = new AtomicReference<>();
        return upstream
                .doOnSubscribe(disposable -> startWakeLock(ref))
                .doFinally(() -> stopWakeLock(ref));
    }

    private void startWakeLock(AtomicReference<PowerManager.WakeLock> wakeLockAtomicReference) {
        PowerManager pm = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wakeLockTransformer:" + sWakeLockId++);
        wakeLockAtomicReference.set(wakeLock);
        wakeLock.acquire(TimeUnit.MINUTES.toMillis(30));
    }

    private void stopWakeLock(AtomicReference<PowerManager.WakeLock> wakeLockAtomicReference) {
        wakeLockAtomicReference.get().release();
    }

    public static class Factory {

        private final Context appContext;

        @Inject
        public Factory(Context appContext) {
            this.appContext = appContext;
        }

        public <T> WakeLockTransformer<T> create() {
            return new WakeLockTransformer<>(appContext);
        }
    }
}
