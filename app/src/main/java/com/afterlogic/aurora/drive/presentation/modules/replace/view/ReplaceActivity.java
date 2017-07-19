package com.afterlogic.aurora.drive.presentation.modules.replace.view;

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
import com.afterlogic.aurora.drive.databinding.ReplaceActivityBinding;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.BindingUtil;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by aleksandrcikin on 28.06.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceActivity extends InjectableMVVMActivity<ReplaceViewModel> implements HasSupportFragmentInjector {

    private static final String KEY_ARGS = "args";

    @Inject
    protected DispatchingAndroidInjector<Fragment> fragmentInjector;

    private final ObservableField<SearchView> searchView = new ObservableField<>();

    public static Intent newReplaceIntent(Context context, List<AuroraFile> files) {
        ReplaceArgs args = new ReplaceArgs();
        args.setCopyMode(false);
        args.setFiles(files);
        return new Intent(context, ReplaceActivity.class)
                .putExtra(KEY_ARGS, args.getBundle());
    }

    public static Intent newCopyIntent(Context context, List<AuroraFile> files) {
        ReplaceArgs args = new ReplaceArgs();
        args.setCopyMode(true);
        args.setFiles(files);
        return new Intent(context, ReplaceActivity.class)
                .putExtra(KEY_ARGS, args.getBundle());
    }

    @Override
    public ReplaceViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(ReplaceViewModel.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ReplaceArgs args = new ReplaceArgs(getIntent().getBundleExtra(KEY_ARGS));
        getViewModel().setArgs(args);
    }

    @Override
    public ViewDataBinding createBinding() {
        ReplaceActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.replace_activity);

        setSupportActionBar(binding.toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        return binding;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_replace, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView.set((SearchView) searchMenuItem.getActionView());

        return true;
    }

    @Override
    protected void bindCreated(ReplaceViewModel vm, UnbindableObservable.Bag bag) {
        super.bindCreated(vm, bag);
        UnbindableObservable.bind(vm.title, bag, field -> setTitle(field.get()));
        UnbindableObservable.bind(vm.subtitle, bag, field -> {
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.setSubtitle(field.get());
            }
        });
    }

    @Override
    protected void bindStarted(ReplaceViewModel vm, UnbindableObservable.Bag bag) {
        super.bindStarted(vm, bag);
        BindingUtil.bindProgressDialog(vm.progress, bag, this);
        BindingUtil.bindSearchView(searchView, vm.searchQuery, vm.showSearch, bag);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actoin_paste:
                getViewModel().onPasteAction();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
