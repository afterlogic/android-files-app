package com.afterlogic.aurora.drive.presentation.modules.main.v2.view;

import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.MainActivityBinding;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.BindingUtil;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel.MainViewModel;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainActivity extends InjectableMVVMActivity<MainViewModel> implements HasSupportFragmentInjector {

    @Inject
    protected DispatchingAndroidInjector<Fragment> fragmentInjector;

    private MenuItem logoutMenuItem;
    private ObservableField<SearchView> searchView = new ObservableField<>();

    @Override
    public MainViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(MainViewModel.class);
    }

    @Override
    public ViewDataBinding createBinding() {
        MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        setSupportActionBar(binding.toolbar);
        return binding;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        logoutMenuItem = menu.findItem(R.id.action_logout);
        logoutMenuItem.setTitle(getViewModel().logoutButtonText.get());

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView.set((SearchView) searchMenuItem.getActionView());
        return true;
    }

    @Override
    protected void bindCreated(MainViewModel vm, UnbindableObservable.Bag bag) {
        super.bindCreated(vm, bag);
        UnbindableObservable.bind(vm.title, bag, field -> setTitle(field.get()));
    }

    @Override
    protected void bindStarted(MainViewModel vm, UnbindableObservable.Bag bag) {
        super.bindStarted(vm, bag);

        BindingUtil.bindProgressDialog(vm.progress, bag, this);
        BindingUtil.bindSearchView(searchView, vm.searchQuery, vm.showSearch, bag);

        UnbindableObservable.bind(vm.showBackButton, bag, showBack -> {
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(showBack.get());
            }
        });

        UnbindableObservable.bind(vm.logoutButtonText, bag, logoutText -> {
            if (logoutMenuItem == null) return;
            logoutMenuItem.setTitle(logoutText.get());
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_logout:
                getViewModel().onLogout();
                return true;

            case R.id.action_about:
                getViewModel().onOpenAbout();
                return true;

            case R.id.action_offline_mode:
                getViewModel().onOpenOfflineMode();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        getViewModel().onBackPressed();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

}
