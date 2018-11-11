package com.afterlogic.aurora.drive.presentation.modules.baseFiles.view;

import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.afterlogic.aurora.drive.presentation.common.modules.view.MVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.viewModel.BaseFilesViewModel;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class BaseFilesMVVMActivity<VM extends BaseFilesViewModel>
        extends MVVMActivity<VM>
        implements FilesListCallback
{
    protected FilesPagerAdapter mAdapter;

    private SimpleListener mLocked = new SimpleListener(this::updateHomeButtonByViewModel);
    private SimpleListener mFolderTitle = new SimpleListener(this::updateTitleByViewModel);

    public abstract BaseFilesListFragment getFilesContent(String type);

    public abstract ViewDataBinding onCreateBind();

    @NonNull
    @Override
    protected final ViewDataBinding onCreateBinding(@Nullable Bundle savedInstanceState) {
        return onCreateBind();
    }

    @Override
    protected void onBindingCreated(@Nullable Bundle savedInstanceState) {
        super.onBindingCreated(savedInstanceState);

        ViewDataBinding binding = getBinding();
        View root = binding.getRoot();

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ViewPager viewPager = (ViewPager) root.findViewById(R.id.viewpager);
        TabLayout tabs = (TabLayout) root.findViewById(R.id.tabs);
        if (viewPager != null && tabs != null) {
            tabs.setupWithViewPager(viewPager, true);
        }

        ViewCompat.setLayoutDirection(tabs, ViewCompat.LAYOUT_DIRECTION_LTR);

        mAdapter = new FilesPagerAdapter(getSupportFragmentManager(), this::getFilesContent, this);
        binding.setVariable(BR.adapter, mAdapter);

        updateTitleByViewModel();
        updateHomeButtonByViewModel();

        getViewModel().getLocked().addOnPropertyChangedCallback(mLocked);
        getViewModel().getFolderTitle().addOnPropertyChangedCallback(mFolderTitle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getViewModel().getLocked().removeOnPropertyChangedCallback(mLocked);
        getViewModel().getFolderTitle().removeOnPropertyChangedCallback(mFolderTitle);
    }

    @Override
    public void onOpenFolder(AuroraFile folder) {
        getViewModel().onCurrentFolderChanged(folder);
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFragment() != null && getCurrentFragment().onBackPressed()) return;

        super.onBackPressed();
    }

    @Nullable
    protected BaseFilesListFragment getCurrentFragment(){
        if (mAdapter.getCount() == 0) return null;

        return mAdapter.getPrimaryFragment();
    }

    private void updateTitleByViewModel(){
        String title = getViewModel().getFolderTitle().get();
        if (title == null){
            setTitle(getString(R.string.app_name));
        } else {
            setTitle(title);
        }
    }

    protected void updateHomeButtonByViewModel(){
        ActionBar ab = getSupportActionBar();

        if (ab == null) return;

        ab.setDisplayHomeAsUpEnabled(getViewModel().getLocked().get());
    }

}
