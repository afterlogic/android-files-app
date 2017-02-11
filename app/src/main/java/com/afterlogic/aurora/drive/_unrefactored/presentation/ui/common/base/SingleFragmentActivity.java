package com.afterlogic.aurora.drive._unrefactored.presentation.ui.common.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnBackPressedListener;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */

public abstract class SingleFragmentActivity extends BaseActivity {

    ////////////////////////////////////////////////
    // [START Override superclass] // <editor-fold desc="Override superclass">
    ////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            Fragment f = onCreateFragment();
            if (f != null) {
                fm.beginTransaction()
                        .add(getFragmentContentLayout(), f)
                        .commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment currentFragment = getCurrentFragment(fm);
        boolean needHandle = true;

        //Check fragment on OnBackPressedListener
        if (currentFragment != null && (currentFragment instanceof OnBackPressedListener)) {
            needHandle = !((OnBackPressedListener) currentFragment).onBackPressed();
        }

        //Check need handle onBackPressed
        if (needHandle) {
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStackImmediate();
            } else {
                super.onBackPressed();
            }
        }
    }

    ////////////////////////////////////////////////
    // [END Override superclass] // </editor-fold>
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Own methods] // <editor-fold desc="Own methods">
    ////////////////////////////////////////////////

    /**
     * Create fragment on first Activity instantiation.
     * Either it will be restored from savedInstanceState.
     */
    @Nullable
    public abstract Fragment onCreateFragment();

    /**
     * Get default activity layout.
     */
    @Override
    @LayoutRes
    public int getActivityLayout() {
        return R.layout.activity_single_fragment;
    }

    /**
     * Get content fragment layout id.
     */
    @IdRes
    public int getFragmentContentLayout() {
        return R.id.content_frame;
    }

    public Fragment getCurrentFragment(){
        return getCurrentFragment(getSupportFragmentManager());
    }

    private Fragment getCurrentFragment(FragmentManager fm){
        return fm.findFragmentById(
                getFragmentContentLayout()
        );
    }

    /**
     * Simplified {@link #startTransaction(Fragment, boolean, String)}.
     */
    public void startTransaction(Fragment fragment) {
        startTransaction(fragment, true, fragment.getClass().getSimpleName());
    }

    /**
     * Start fragment transaction for content fragment layout ({@link #getFragmentContentLayout()}).
     *
     * @param fragment       - new content fragment.
     * @param addToBackStack - if true fragment will be added to back stack with.
     * @param tag            - back stack and fragment tag.
     */
    public void startTransaction(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(getFragmentContentLayout(), fragment, tag);
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.commit();
    }

    ////////////////////////////////////////////////
    // [END Own methods] // </editor-fold>
    ////////////////////////////////////////////////
}