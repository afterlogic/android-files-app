package com.afterlogic.aurora.drive.presentation.modules.offline.v2.view;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.OfflineActivityBinding;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.BindingUtil;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules.offline.v2.viewModel.OfflineViewModel;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineActivity extends InjectableMVVMActivity<OfflineViewModel> implements HasSupportFragmentInjector {

    public static Intent intent(Context context, boolean manual) {
        return new Intent(context, OfflineActivity.class)
                .putExtra("manual", manual);
    }

    @Inject
    protected DispatchingAndroidInjector<Fragment> fragmentAndroidInjector;

    private final ObservableField<SearchView> searchView = new ObservableField<>();

    @Override
    public ViewDataBinding createBinding() {
        OfflineActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.offline_activity);
        setSupportActionBar(binding.toolbar);

        boolean manual = getIntent().getBooleanExtra("manual", false);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(manual);
        }

        return binding;
    }

    @Override
    public OfflineViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(OfflineViewModel.class);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentAndroidInjector;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, OfflineFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_offline, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView.set((SearchView) searchMenuItem.getActionView());

        return true;
    }

    @Override
    protected void bindStarted(OfflineViewModel vm, UnbindableObservable.Bag bag) {
        super.bindStarted(vm, bag);
        BindingUtil.bindSearchView(searchView, vm.searchQuery, vm.showSearch, bag);
    }

    @Override
    public void onBackPressed() {
        getViewModel().onBackPressed();
    }
}
