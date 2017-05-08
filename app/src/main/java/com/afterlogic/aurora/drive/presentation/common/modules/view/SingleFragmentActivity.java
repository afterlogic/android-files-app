package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnBackPressedListener;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModulesStore;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Single fragment activity. Create single fragment or restore it.
 */
@Deprecated
public abstract class SingleFragmentActivity extends MVPActivity implements FragmentManager.OnBackStackChangedListener{

    //Activity layout id
    @LayoutRes
    private int mActivityLayout = R.layout.activity_base;

    //Fragment layout id
    @IdRes
    private int mFragmentId = R.id.content;
    private Toolbar mActivityToolbar;
    private Toolbar mCurrentToolbar;

    private final List<UUID> mStackPresentationModulesUuids = new ArrayList<>();

    private int mPreviousBackStackSize = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentViewWithBinding(mActivityLayout);

        mActivityToolbar = (Toolbar) findViewById(R.id.toolbar);
        setCurrentToolbar(mActivityToolbar);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        initFragment(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: onBackPressed(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onReleasePresentationModules(PresentationModulesStore store) {
        super.onReleasePresentationModules(store);
        releaseModulesStore();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getCurrentFragment();

        boolean handled = false;

        if (fragment != null && fragment instanceof OnBackPressedListener){
            handled = ((OnBackPressedListener) fragment).onBackPressed();
        }

        if (!handled){
            super.onBackPressed();
        }
    }

    public Fragment getCurrentFragment(){
        return getSupportFragmentManager().findFragmentById(getFragmentId());
    }

    /**
     * Set activity layout resource id, layout must contain container
     * with id {@link #getFragmentId()}.
     * @param activityLayout - layout id for activity.
     */
    @SuppressWarnings("unused")
    protected void setActivityLayout(@LayoutRes int activityLayout) {
        mActivityLayout = activityLayout;
    }

    /**
     * Set fragment container id, layout ({@link #mActivityLayout}) must contain container
     * with this id.
     * @param fragmentId - content container id.
     */
    @SuppressWarnings("unused")
    protected void setFragmentId(@IdRes int fragmentId) {
        mFragmentId = fragmentId;
    }

    /**
     * Get content container id.
     */
    @IdRes
    public int getFragmentId() {
        return mFragmentId;
    }

    /**
     * Create content fragment or restore it.
     * @param savedInstanceState - saved instance state.
     */
    private void initFragment(Bundle savedInstanceState){
        Fragment fragment;
        if (savedInstanceState == null){
            fragment = creteFragment();
            showFragmentWithClearStack(fragment);
        } else {
            fragment = getCurrentFragment();
            if (fragment != null) {
                onFragmentShowed(fragment);
            }
        }
    }

    /**
     * Configure action bar. Called only if it exist.
     */
    @SuppressWarnings("UnusedParameters")
    protected void onPrepareActionBar(ActionBar actionBar){
        //no-op
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager fm = getSupportFragmentManager();
        int newBackStackSize = fm.getBackStackEntryCount();
        Fragment fragment = getCurrentFragment();

        MyLog.d(this, "onBackStackChanged: prev=" + mPreviousBackStackSize + ", new=" + newBackStackSize + ", currentFragment=" + fragment);

        if (fragment != null){
            onFragmentShowed(fragment);
        }
        mPreviousBackStackSize = newBackStackSize;
    }

    /**
     * Configure fragment if need it.
     */
    protected void onFragmentShowed(@NonNull Fragment fragment){

        MyLog.d(this, "onFragmentShowed: " + fragment);

        //TODO fix fragment title
    }

    private void setCurrentToolbar(Toolbar toolbar){
        if (toolbar == mCurrentToolbar) return;

        mCurrentToolbar = toolbar;
        if (mCurrentToolbar != null){
            setSupportActionBar(mCurrentToolbar);
            ActionBar ab = getSupportActionBar();
            if (ab != null){
                onPrepareActionBar(ab);
            }
            //noinspection RestrictedApi
            invalidateOptionsMenu();
        }
        updateActivityToolbarVisibility();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (mCurrentToolbar != null){
            mCurrentToolbar.setTitle(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    /**
     * Create content fragment.
     */
    @NonNull
    protected abstract Fragment creteFragment();

    public void showFragmentWithClearStack(@NonNull Fragment fragment){
        MyLog.d(this, "showFragmentWithClearStack: " + fragment);

        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        releaseModulesStore();

        registerFragmentCreateListener(fragment);

        fm.beginTransaction()
                .replace(getFragmentId(), fragment)
                .commitNow();

        onFragmentShowed(fragment);
    }

    public void showNextFragment(@NonNull Fragment fragment){
        MyLog.d(this, "showNextFragment: " + fragment);

        FragmentManager fm = getSupportFragmentManager();

        registerFragmentCreateListener(fragment);

        fm.beginTransaction()
                .replace(getFragmentId(), fragment)
                .addToBackStack(null)
                .commit();

        onFragmentShowed(fragment);
    }

    private void registerFragmentCreateListener(Fragment fragment){
        if (fragment instanceof MVPFragment){
            ((MVPFragment) fragment).setFirstCreateInterceptor(view -> {
                UUID moduleUuid = view.getModuleUuid();
                if (moduleUuid != null) {
                    mStackPresentationModulesUuids.add(view.getModuleUuid());
                }
            });
        }
    }

    private void updateActivityToolbarVisibility(){
        if (mActivityToolbar != null){
            int activityToolbarVisibility = mCurrentToolbar == mActivityToolbar ? View.VISIBLE : View.GONE;
            MyLog.d(this, "Update activity toolbar visibility: " + activityToolbarVisibility);
            mActivityToolbar.setVisibility(activityToolbarVisibility);
        }
    }

    private void releaseModulesStore(){
        synchronized (mStackPresentationModulesUuids){
            if (!mStackPresentationModulesUuids.isEmpty()) {
                Stream.of(mStackPresentationModulesUuids).forEach(this::removePresentationModule);
                mStackPresentationModulesUuids.clear();
            }
        }
    }
}
