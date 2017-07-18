package com.afterlogic.aurora.drive.presentation.modules.main.v2.view;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.Optional;
import com.afterlogic.aurora.drive.databinding.MainActivityBinding;
import com.afterlogic.aurora.drive.model.events.ActivityResultEvent;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.di.ForViewInteractor;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.BindingUtil;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel.MainViewModel;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainActivity extends InjectableMVVMActivity<MainViewModel> implements HasSupportFragmentInjector {

    public static Intent intent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Inject
    protected DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Inject
    @ForViewInteractor
    protected EventBus eventBus;

    private MenuItem logoutMenuItem;
    private ObservableField<SearchView> searchView = new ObservableField<>();

    @Override
    public MainViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(MainViewModel.class);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public ViewDataBinding createBinding() {
        MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        setSupportActionBar(binding.toolbar);

        binding.fabCollapser.setOnTouchListener((v, event) -> {
            binding.addMenu.collapse();
            return true;
        });

        binding.addMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                binding.fabCollapser.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                binding.fabCollapser.setVisibility(View.GONE);
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        eventBus.post(new ActivityResultEvent(requestCode, resultCode, data));
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

        bindMultiChoice(vm, bag);
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

            case R.id.action_multichoise:
                getViewModel().onMultiChoice();
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

    private void bindMultiChoice(MainViewModel vm, UnbindableObservable.Bag bag) {
        Optional<ActionMode> optionalMultiChoice = new Optional<>();

        UnbindableObservable.bind(vm.multiChoiceMode, bag, mode -> {
            if (!mode.get()) {
                optionalMultiChoice.ifPresent(ActionMode::finish);
            } else {
                startSupportActionMode(new MultiChoiceActionMode(optionalMultiChoice, vm, this));
            }
        });
    }

}
