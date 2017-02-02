package com.afterlogic.aurora.drive.core.common.util;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.interfaces.Creator;

import java.lang.ref.WeakReference;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Optional weak reference.
 */
public class OptWeakRef<T> {

    private WeakReference<T> mReference;


    public static <T> OptWeakRef<T> empty(){
        return new OptWeakRef<>();
    }

    private OptWeakRef(){

    }

    public OptWeakRef(T obj){
        mReference = new WeakReference<>(obj);
    }

    /**
     * Set reference to object.
     */
    public synchronized boolean set(T value){
        if (value == get()) return false;

        if (value == null){
            clear();
        } else {
            mReference = new WeakReference<>(value);
        }
        return true;
    }

    /**
     * Clear reference.
     */
    public synchronized void clear(){
        mReference = null;
    }

    /**
     * Run consumer action with object if reference on it exists.
     * @param consumer - result consumer.
     */
    public void ifPresent(Consumer<T> consumer){
        ifPresent(consumer, null);
    }

    public void ifPresent(Consumer<T> consumer, Runnable emptyConsumer){
        if (isPresent()){
            consumer.consume(mReference.get());
        } else {
            if (emptyConsumer != null) {
                emptyConsumer.run();
            }
        }
    }

    public T get(){
        return mReference == null ? null : mReference.get();
    }

    public synchronized T getOrCreate(Creator<T> creator){
        if (isPresent()){
            return get();
        } else {
            T value = creator.create();
            set(value);
            return value;
        }
    }

    public boolean isPresent(){
        return mReference != null && mReference.get() != null;
    }
}
