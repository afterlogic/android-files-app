package com.afterlogic.aurora.drive.presentation.modules._baseFiles.view;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter.FilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesViewModel;

import javax.inject.Inject;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class BaseFilesActivity<
        VM extends BaseFilesViewModel,
        P extends FilesPresenter
> extends BaseActivity implements FilesListCallback {

    @Inject
    protected VM mViewModel;

    @Inject @ViewPresenter
    protected P mPresenter;

    protected FilesPagerAdapter mAdapter;

    private SimpleListener mLocked = new SimpleListener(this::updateHomeButtonByViewModel);
    private SimpleListener mFolderTitle = new SimpleListener(this::updateTitleByViewModel);

    public abstract BaseFilesListFragment getFilesContent(String type);

    public abstract ViewDataBinding onCreateBind();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding binding = onCreateBind();
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

        mAdapter = new FilesPagerAdapter(getSupportFragmentManager(), this::getFilesContent);
        binding.setVariable(BR.adapter, mAdapter);
        binding.setVariable(BR.viewModel, mViewModel);

        updateTitleByViewModel();
        updateHomeButtonByViewModel();

        mViewModel.getLocked().addOnPropertyChangedCallback(mLocked);
        mViewModel.getFolderTitle().addOnPropertyChangedCallback(mFolderTitle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.getLocked().removeOnPropertyChangedCallback(mLocked);
        mViewModel.getFolderTitle().removeOnPropertyChangedCallback(mFolderTitle);
    }

    @Override
    public void onOpenFolder(AuroraFile folder) {
        mPresenter.onCurrentFolderChanged(folder);
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
        String title = mViewModel.getFolderTitle().get();
        if (title == null){
            setTitle(getString(R.string.app_name));
        } else {
            setTitle(title);
        }
    }

    protected void updateHomeButtonByViewModel(){
        ActionBar ab = getSupportActionBar();

        if (ab == null) return;

        ab.setDisplayHomeAsUpEnabled(mViewModel.getLocked().get());
    }

}
