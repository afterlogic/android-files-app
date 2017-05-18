package com.afterlogic.aurora.drive.presentation.common.modules.assembly;

import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Base presentation module's module.
 */
@Module
public abstract class MVVMModule<T> {

    private OptWeakRef<T> mView = OptWeakRef.empty();

    @Provides
    public MVVMModule<T> self(){
        return this;
    }

    @Provides
    public OptWeakRef<T> provideView(){
        return mView;
    }

    public void setView(T t) {
        mView.set(t);
    }

}
