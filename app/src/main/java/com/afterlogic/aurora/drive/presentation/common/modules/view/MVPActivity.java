package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.MenuItem;
import android.widget.Toast;

import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.model.events.ActivityResultEvent;
import com.afterlogic.aurora.drive.model.events.PermissionGrantEvent;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModulesStore;
import com.afterlogic.aurora.drive.presentation.common.modules.model.presenter.Presenter;
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
@Deprecated
public abstract class MVPActivity extends AuroraActivity implements PresentationView{

    private static final String MODULE_UUID = ".MODULE_UUID";
    private ViewDataBinding mBinding;

    private Set<Stoppable> mStoppables = new HashSet<>();
    private Set<Runnable> mStartWaiters = new HashSet<>();
    private Set<SubView> mSubmodules = new HashSet<>();

    private boolean mIsActive;

    private UUID mModuleUuid;

    private boolean mAddSubmoduleAvailable = true;
    private PresentationModulesStore mPresentationModulesStore;

    final Set<Presenter> mPresenters = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        onPrepareCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        App app = ((App) getApplication());

        mPresentationModulesStore = app.getInjectors().modulesStore();

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

        InjectorsComponent component = app.getInjectors();
        assembly(component);
        Stream.of(mSubmodules).forEach(subView -> subView.assembly(component));

        PresentationViewUtil.reflectiveCollectPresenters(this);
    }

    protected void assembly(InjectorsComponent modulesFactory){
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
    public void showMessage(int messageId, int type) {
        showMessage(getString(messageId), type);
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
}
