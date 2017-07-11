package com.afterlogic.aurora.drive.presentation.modules.main.v2.view;

import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
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

    private SearchView searchView;

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

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
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

        UnbindableObservable.bind(vm.fileTypesLocked, bag, fileTypesLocked -> {
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(fileTypesLocked.get());
            }
        });
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
