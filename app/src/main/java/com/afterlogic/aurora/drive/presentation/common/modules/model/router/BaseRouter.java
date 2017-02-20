package com.afterlogic.aurora.drive.presentation.common.modules.model.router;

import android.content.Context;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.util.Holder;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;

/**
 * Created by sashka on 24.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class BaseRouter<VI extends PresentationView, T extends Context>{

    private OptWeakRef<VI> mViewContext;

    public BaseRouter(ViewState<VI> viewContext) {
        mViewContext = viewContext.getView();
    }

    protected boolean ifViewActive(Consumer<T> consumer){
        Holder<Boolean> viewActive = new Holder<>(false);
        mViewContext.ifPresent(view -> {
            if (view.isActive()){
                viewActive.set(true);
                T context = view.getViewContext();
                consumer.consume(context);
            }
        });
        return viewActive.get();
    }
}
