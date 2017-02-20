package com.afterlogic.aurora.drive.presentation.common.modules.model.router;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.modules.view.MVVMView;

/**
 * Created by sashka on 24.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class BaseMVVMRouter<T extends MVVMView>{

    private OptWeakRef<T> mViewContext;

    public BaseMVVMRouter(OptWeakRef<T> viewContext) {
        mViewContext = viewContext;
    }

    protected void ifViewActive(Consumer<T> consumer){
        mViewContext.ifPresent(view -> {
            if (view.isActive()){
                consumer.consume(view);
            }
        });
    }
}
