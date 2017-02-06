package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.model.events.ActivityResultEvent;
import com.afterlogic.aurora.drive.model.events.PermissionGrantEvent;
import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModulesStore;
import com.afterlogic.aurora.drive.presentation.common.modules.presenter.Presenter;
import com.annimon.stream.Stream;

import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Base for all activities in project.
 */
public abstract class BaseActivity extends AppCompatActivity implements PresentationView{

    private static final String MODULE_UUID = ".MODULE_UUID";
    private ViewDataBinding mBinding;

    private Set<Stoppable> mStoppables = new HashSet<>();
    private Set<Runnable> mStartWaiters = new HashSet<>();
    private Set<SubView> mSubmodules = new HashSet<>();

    private boolean mIsActive;

    private UUID mModuleUuid;

    private boolean mAddSubmoduleAvailable = true;
    private PresentationModulesStore mPresentationModulesStore;

    protected final Set<Presenter> mPresenters = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        onPrepareCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        App app = ((App) getApplication());

        mPresentationModulesStore = app.modulesFactory().modulesStore();

        mAddSubmoduleAvailable = true;
        onCreateSubView(savedInstanceState);
        mAddSubmoduleAvailable = false;

        if (savedInstanceState != null){
            String uuidString = savedInstanceState.getString(MODULE_UUID, null);
            if (uuidString != null) {
                mModuleUuid = UUID.fromString(uuidString);
            }
            Stream.of(mSubmodules).forEach(subModule -> subModule.restoreInstance(savedInstanceState));
        }

        ModulesFactoryComponent component = app.modulesFactory();
        assembly(component);
        Stream.of(mSubmodules).forEach(subView -> subView.assembly(component));

        reflectiveCollectPresenters(this);
    }

    protected void assembly(ModulesFactoryComponent modulesFactory){
        //no-op
    }

    protected void onPrepareCreate(@Nullable Bundle savedInstanceState){
        //no-op
    }

    protected void onCreateSubView(@Nullable Bundle savedInstanceState){
        //no-op
    }

    protected <T extends ViewDataBinding> T setContentViewWithBinding(@LayoutRes int layoutId){
        mBinding = DataBindingUtil.setContentView(this, layoutId);
        return getViewBinding();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            Stream.of(mPresenters).forEach(presenter -> presenter.onRestoreInstanceState(savedInstanceState));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        doOnStart(() -> {
            if (EventBus.getDefault().hasSubscriberForEvent(ActivityResultEvent.class)) {
                EventBus.getDefault().post(new ActivityResultEvent(requestCode, resultCode, data));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doOnStart(() -> {
            if (EventBus.getDefault().hasSubscriberForEvent(PermissionGrantEvent.class)) {
                EventBus.getDefault().post(new PermissionGrantEvent(requestCode, permissions, grantResults));
            }
        });
    }

    protected <T extends ViewDataBinding> T getViewBinding(){
        return (T) mBinding;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Stream.of(mStoppables).forEach(Stoppable::onStart);
        mIsActive = true;

        Stream.of(mStartWaiters).forEach(Runnable::run);
        mStartWaiters.clear();

        Stream.of(mPresenters).forEach(Presenter::onStart);
    }

    @Override
    protected void onStop() {
        Stream.of(mStoppables).forEach(Stoppable::onStop);
        Stream.of(mPresenters).forEach(Presenter::onStop);
        mIsActive = false;
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mModuleUuid != null) {
            outState.putString(MODULE_UUID, mModuleUuid.toString());
        }

        Stream.of(mPresenters).forEach(presenter -> presenter.onSaveInstanceState(outState));
    }

    @Override
    public void onDestroy() {
        MyLog.d(this, "onDestroy");
        if (isFinishing()){
            onReleasePresentationModules(mPresentationModulesStore);
        }
        mPresenters.clear();
        super.onDestroy();
    }

    protected void onReleasePresentationModules(PresentationModulesStore store){
        store.remove(getModuleUuid());
        Stream.of(mSubmodules).forEach(subView -> store.remove(subView.getModuleUuid()));
    }

    void removePresentationModule(UUID uuid){
        if (uuid == null) return;
        mPresentationModulesStore.remove(getModuleUuid());
    }

    protected void registerStoppable(Stoppable stoppable){
        if (stoppable != null){
            mStoppables.add(stoppable);
        }
    }

    protected void unregisterStoppable(Stoppable stoppable){
        if (stoppable != null){
            mStoppables.remove(stoppable);
        }
    }

    protected <T extends Stoppable & SubView> void registerSubView(@NonNull T instantiable){
        if (!mAddSubmoduleAvailable){
            MyLog.majorException(this, "Try register submodule when not available.");
            return;
        }
        mSubmodules.add(instantiable);
        mStoppables.add(instantiable);
    }

    @Override
    public void showLoadingProgress(String message, int type) {
        //no-op
    }

    @Override
    public void hideLoadingProgress(int type) {
        //no-op
    }

    @Override
    public void showMessage(String message, int type) {
        if (mIsActive) {
            switch (type) {
                case TYPE_MESSAGE_MINOR:
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    break;
                case TYPE_MESSAGE_MAJOR:
                    new AlertDialog.Builder(this)
                            .setMessage(message)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
            }
        } else {
            MyLog.e(this, "Can't show message. View is not active!");
        }
    }

    @Override
    public boolean isActive() {
        return mIsActive;
    }

    @Override
    public UUID getModuleUuid() {
        return mModuleUuid;
    }

    @Override
    public void setModuleUuid(UUID uuid) {
        mModuleUuid = uuid;
    }

    @Override
    public <T extends Context> T getViewContext() {
        return (T)this;
    }

    protected void doOnStart(Runnable runnable){
        if (mIsActive) {
            runnable.run();
        } else {
            mStartWaiters.add(runnable);
        }
    }

    private static void reflectiveCollectPresenters(BaseActivity activity){
        Stream.of(activity.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ViewPresenter.class) &&
                        Presenter.class.isAssignableFrom(field.getType())
                )
                .forEach(field -> {
                    boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    try {
                        Presenter presenter = (Presenter) field.get(activity);
                        if (presenter != null) {
                            activity.mPresenters.add(presenter);
                        } else {
                            MyLog.majorException(activity, "Field marked as ViewPresenter but it is null: " + field.getName());
                        }
                    } catch (IllegalAccessException e) {
                        MyLog.majorException(e);
                    }
                    field.setAccessible(accessible);
                });
    }
}
