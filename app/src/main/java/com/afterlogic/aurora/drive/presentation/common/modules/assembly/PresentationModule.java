package com.afterlogic.aurora.drive.presentation.common.modules.assembly;

import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;

import java.lang.reflect.ParameterizedType;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Base presentation module's module.
 */
@Module
public abstract class PresentationModule<View extends PresentationView> {

    private ViewState<View> mView;

    protected PresentationModule(){
        sharedConstructor(getGenericClass());
    }

    protected PresentationModule(Class<View> viewClass){
        sharedConstructor(viewClass);
    }

    private void sharedConstructor(Class<View> viewClass){
        mView = ViewState.create(viewClass);
    }

    @Provides
    public ViewState<View> provideViewState(){
        return mView;
    }

    @Provides
    public OptWeakRef<View> provideView(){
        return mView.getView();
    }

    @Provides
    public View provideViewProxy(){
        return mView.getViewProxy();
    }

    @Provides
    public PresentationModule<View> provideSelf(){
        return this;
    }

    public void setView(View view) {
        mView.setView(view);
    }

    protected Class<View> getGenericClass(){
        Class moduleClass = this.getClass();
        ParameterizedType moduleGenericSuperClass = ((ParameterizedType) moduleClass.getGenericSuperclass());
        return (Class<View>) moduleGenericSuperClass.getActualTypeArguments()[0];
    }

}
