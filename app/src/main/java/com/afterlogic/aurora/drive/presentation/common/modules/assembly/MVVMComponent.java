package com.afterlogic.aurora.drive.presentation.common.modules.assembly;

/**
 * Created by sashka on 17.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface MVVMComponent<T> {

    void inject(T target);

    MVVMModule<T> module();
}
