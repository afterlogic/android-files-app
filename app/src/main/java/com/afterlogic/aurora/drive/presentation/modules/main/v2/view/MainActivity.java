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
        initSearchView((SearchView) searchMenuItem.getActionView());
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

        UnbindableObservable.bind(vm.showBackButton, bag, showBack -> {
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(showBack.get());
            }
        });

        UnbindableObservable.bind(vm.showSearch, bag, showSearch -> {
            if (searchView == null) return;
            searchView.setIconified(!showSearch.get());
        });
        UnbindableObservable.bind(vm.searchQuery, bag, query -> {
            if (searchView == null) return;
            searchView.setQuery(query.get(), false);
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

    private void initSearchView(SearchView searchView) {
        this.searchView = searchView;
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnSearchClickListener(view -> {
            getViewModel().showSearch.set(true);
            searchView.setQuery(getViewModel().searchQuery.get(), false);
        });

        searchView.setOnCloseListener(() -> {
            getViewModel().showSearch.set(false);
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getViewModel().searchQuery.set(newText);
                return false;
            }
        });

        searchView.setIconified(!getViewModel().showSearch.get());
        searchView.setQuery(getViewModel().searchQuery.get(), false);
    }
}
