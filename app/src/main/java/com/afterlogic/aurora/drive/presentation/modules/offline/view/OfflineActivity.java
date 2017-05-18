package com.afterlogic.aurora.drive.presentation.modules.offline.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.ActivityOfflineBinding;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters.ViewProvider;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.MVVMActivity;
import com.afterlogic.aurora.drive.presentation.common.util.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.FilesViewDialogDelegate;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemViewModel;
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.OfflineViewModel;

import java.util.WeakHashMap;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class OfflineActivity extends MVVMActivity<OfflineViewModel> {

    private static final String MANUAL_MODE = ".MANUAL_MODE";

    private FilesViewDialogDelegate mFilesViewDelegate = new FilesViewDialogDelegate(this);

    public static Intent intent(boolean manualMode, Context context){
        Intent intent = new Intent(context, OfflineActivity.class);
        intent.putExtra(MANUAL_MODE, manualMode);
        return intent;
    }

    @Nullable
    private MenuItem mOnlineMenuItem;

    @Override
    public void assembly(InjectorsComponent injectors) {
        injectors.offline().inject(this);
    }

    @Override
    @NonNull
    protected ViewDataBinding onCreateBinding(@Nullable Bundle savedInstanceState) {
        return DataBindingUtil.setContentView(this, R.layout.activity_offline);
    }

    @Override
    protected void onBindingCreated(@Nullable Bundle savedInstanceState) {
        super.onBindingCreated(savedInstanceState);
        ActivityOfflineBinding binding = getBinding();
        setSupportActionBar(binding.toolbar);
    }

    @Override
    protected void onViewModelCreated(OfflineViewModel viewModel, Bundle savedInstanceState) {
        super.onViewModelCreated(viewModel, savedInstanceState);
        if (savedInstanceState == null) {
            viewModel.viewInitWith(getIntent().getBooleanExtra(MANUAL_MODE, false));
        }
    }

    @Override
    protected void onBindCreatedBindings(OfflineViewModel offlineViewModel, UnbindableObservable.Bag bag) {
        super.onBindCreatedBindings(offlineViewModel, bag);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(offlineViewModel.getManualMode().get());
        }
    }

    @Override
    protected void onBindStartedBindings(OfflineViewModel offlineViewModel, UnbindableObservable.Bag bag) {
        super.onBindStartedBindings(offlineViewModel, bag);

        UnbindableObservable.create(getViewModel().getNetworkState())
                .addListener(field -> updateOnlineMenuItemVisibility())
                .addTo(bag)
                .notifyChanged();

        mFilesViewDelegate.bindProgressField(offlineViewModel.getProgress());
        mFilesViewDelegate.bindMessageField(offlineViewModel.getMessage());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFilesViewDelegate.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_offline, menu);
        mOnlineMenuItem = menu.findItem(R.id.action_online_mode);
        updateOnlineMenuItemVisibility();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_online_mode:
                getViewModel().onOnline();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onUnbindStartedBindings(OfflineViewModel offlineViewModel, UnbindableObservable.Bag bag) {
        super.onUnbindStartedBindings(offlineViewModel, bag);
        mFilesViewDelegate.unbind();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFilesViewDelegate.onStop();
    }

    private void updateOnlineMenuItemVisibility() {
        if (mOnlineMenuItem == null) return;

        boolean networkEnabled = getViewModel().getNetworkState().get();
        boolean manualMode = getViewModel().getManualMode().get();
        mOnlineMenuItem.setVisible(!manualMode && networkEnabled);
    }

    public static class OfflineBinder {

        private static final WeakHashMap<RecyclerView, ItemsAdapter<BaseFileItemViewModel>> FILES_ADAPTERS = new WeakHashMap<>();

        public static ViewProvider<ItemsAdapter<BaseFileItemViewModel>, RecyclerView> filesListAdapter(OfflineViewModel viewModel){
            return list -> {
                ItemsAdapter<BaseFileItemViewModel> adapter = FILES_ADAPTERS.get(list);
                if (adapter == null){
                    adapter = new OfflineAdapter(viewModel);
                    FILES_ADAPTERS.put(list, adapter);
                }
                return adapter;
            };
        }
    }
}
