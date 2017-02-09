package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afterlogic.aurora.drive.R;
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
 * Base module fragment with base presenter interaction.
 */
public abstract class BaseFragment extends Fragment implements PresentationView {

    private static final String MODULE_UUID = ".MODULE_UUID";

    private Set<Integer> mShowedLoadingTypes = new HashSet<>();
    private ProgressDialog mLoadingDialog;

    private boolean mIsActive;

    private Set<Runnable> mStartWaiters = new HashSet<>();
    private Set<Stoppable> mStoppables = new HashSet<>();
    private Set<SubView> mSubmodules = new HashSet<>();

    private boolean mAddSubmoduleAvailable = true;

    private UUID mModuleUuid;

    private boolean mOnSaveInstanceStateCalled;
    private PresentationModulesStore mPresentationModulesStore;

    private FirstCreateViewInterceptor mFirstCreateViewInterceptor;

    protected final Set<Presenter> mPresenters = new HashSet<>();

    /**
     * Initialize module wireframe and assembly it.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        MyLog.d(this, "onCreate");
        super.onCreate(savedInstanceState);

        //Safe arguments
        if (getArguments() == null){
            setArguments(Bundle.EMPTY);
        }

        mOnSaveInstanceStateCalled = false;
        mPresentationModulesStore = ((App) getActivity().getApplication())
                .modulesFactory().modulesStore();

        mAddSubmoduleAvailable = true;
        onCreateSubView(savedInstanceState);
        mAddSubmoduleAvailable = false;

        if (savedInstanceState != null){
            if (mModuleUuid == null) {
                String uuidString = savedInstanceState.getString(MODULE_UUID, null);
                if (uuidString != null) {
                    mModuleUuid = UUID.fromString(uuidString);
                }
            }
            Stream.of(mSubmodules).forEach(instantiable -> instantiable.restoreInstance(savedInstanceState));
        }

        ModulesFactoryComponent component = ((App) getActivity().getApplication()).modulesFactory();
        assembly(component);

        reflectiveCollectPresenters(this);

        if (savedInstanceState == null && mFirstCreateViewInterceptor != null){
            mFirstCreateViewInterceptor.onPresentationViewCreated(this);
        }
        mFirstCreateViewInterceptor = null;
    }

    protected void onCreateSubView(@Nullable Bundle savedInstanceState){
        //no-op
    }

    /**
     * Assembly module wireframe.
     */
    protected abstract void assembly(ModulesFactoryComponent modulesFactory);

    /**
     * Restore presenter state.
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        MyLog.d(this, "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Stream.of(mPresenters).forEach(presenter -> presenter.onRestoreInstanceState(savedInstanceState));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    /**
     * Tell presenter that presentation module is started.
     */
    @Override
    public void onStart() {
        MyLog.d(this, "onStart");
        super.onStart();
        mIsActive = true;
        mOnSaveInstanceStateCalled = false;

        Stream.of(mStoppables).forEach(Stoppable::onStart);
        Stream.of(mPresenters).forEach(Presenter::onStart);

        Stream.of(mStartWaiters).forEach(Runnable::run);
        mStartWaiters.clear();

    }

    /**
     * Tell presenter that presentation module is stopped.
     */
    @Override
    public void onStop() {
        MyLog.d(this, "onStop");
        Stream.of(mStoppables).forEach(Stoppable::onStop);
        Stream.of(mPresenters).forEach(Presenter::onStop);
        mIsActive = false;
        mShowedLoadingTypes.clear();
        hideProdgressDialog();
        super.onStop();
    }

    /**
     * Save presenter state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        MyLog.d(this, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mOnSaveInstanceStateCalled = true;

        if (mModuleUuid != null) {
            outState.putString(MODULE_UUID, mModuleUuid.toString());
        }
        Stream.of(mSubmodules).forEach(instantiable -> instantiable.saveInstance(outState));
        Stream.of(mPresenters).forEach(presenter -> presenter.onSaveInstanceState(outState));
    }

    @Override
    public void onDestroy() {
        MyLog.d(this, "onDestroy");

        //Remove presentation modules from modulesStore if not saved previously
        if (!mOnSaveInstanceStateCalled){
            mPresentationModulesStore.remove(getModuleUuid());
            Stream.of(mSubmodules).forEach(subView -> mPresentationModulesStore.remove(subView.getModuleUuid()));
        }

        super.onDestroy();
    }

    /**
     * Get base context.
     */
    @Override
    public <T extends Context> T getViewContext() {
        return ((T) getActivity());
    }

    @Override
    public void showLoadingProgress(String message, int type) {
        if (mIsActive) {
            mShowedLoadingTypes.add(type);
            if (mIsActive && mLoadingDialog == null) {
                mLoadingDialog = ProgressDialog.show(getContext(), null, message, true, false);
            } else {
                mLoadingDialog.setMessage(message);
            }
        }
    }

    @Override
    public void hideLoadingProgress(int type) {
        mShowedLoadingTypes.remove(type);
        if (mShowedLoadingTypes.size() == 0){
            hideProdgressDialog();
        }
    }

    private void hideProdgressDialog(){
        if (mLoadingDialog != null && mLoadingDialog.isShowing()){
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    public void showMessage(int messageId, int type) {
        showMessage(getString(messageId), type);
    }

    @Override
    public void showMessage(String message, int type) {
        if (mIsActive) {
            switch (type) {
                case TYPE_MESSAGE_MINOR:
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    break;
                case TYPE_MESSAGE_MAJOR:
                    new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog)
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

    public void setFirstCreateInterceptor(FirstCreateViewInterceptor firstCreateViewInterceptor) {
        mFirstCreateViewInterceptor = firstCreateViewInterceptor;
    }

    protected void registerStoppable(Stoppable stoppable){
        if (stoppable != null){
            mStoppables.add(stoppable);
        }
    }

    protected <T extends Stoppable & SubView> void registerSubmodule(@NonNull T instantiable){
        if (!mAddSubmoduleAvailable){
            MyLog.majorException(this, "Try register submodule when not available.");
            return;
        }
        mSubmodules.add(instantiable);
        mStoppables.add(instantiable);
    }

    protected <T extends ViewDataBinding> T inflateBinding(LayoutInflater inflater, @LayoutRes int layoutId, ViewGroup container){
        return (T) DataBindingUtil.inflate(inflater, layoutId, container, false);
    }

    protected void doOnStart(Runnable runnable){
        if (mIsActive) {
            runnable.run();
        } else {
            mStartWaiters.add(runnable);
        }
    }

    public interface FirstCreateViewInterceptor{
        void onPresentationViewCreated(BaseFragment view);
    }

    private static void reflectiveCollectPresenters(BaseFragment fragment){
        Stream.of(fragment.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ViewPresenter.class) &&
                        Presenter.class.isAssignableFrom(field.getType())
                )
                .forEach(field -> {
                    try {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        Presenter presenter = (Presenter) field.get(fragment);
                        if (presenter != null) {
                            fragment.mPresenters.add(presenter);
                        } else {
                            MyLog.majorException(fragment, "Field marked as ViewPresenter but it is null: " + field.getName());
                        }
                        field.setAccessible(accessible);
                    } catch (IllegalAccessException e) {
                        MyLog.majorException(fragment, e);
                    }
                });
    }
}
