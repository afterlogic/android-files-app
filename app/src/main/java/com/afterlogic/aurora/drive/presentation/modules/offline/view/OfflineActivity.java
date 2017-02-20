package com.afterlogic.aurora.drive.presentation.modules.offline.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
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
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseMVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.FilesViewDialogDelegate;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemViewModel;
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.OfflineViewModel;

import java.util.WeakHashMap;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class OfflineActivity extends BaseMVVMActivity<OfflineViewModel> {

    private static final String MANUAL_MODE = ".MANUAL_MODE";

    private FilesViewDialogDelegate mFilesViewDelegate = new FilesViewDialogDelegate(this);

    public static Intent intent(boolean manualMode, Context context){
        Intent intent = new Intent(context, OfflineActivity.class);
        intent.putExtra(MANUAL_MODE, manualMode);
        return intent;
    }

    @Override
    public void assembly(InjectorsComponent injectors) {
        injectors.offline().inject(this);
    }

    @Override
    protected ViewDataBinding onCreateBinding(@Nullable Bundle savedInstanceState) {
        ActivityOfflineBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_offline);
        setSupportActionBar(binding.toolbar);
        return binding;
    }

    @Override
    protected void onViewModelCreated(OfflineViewModel viewModel, Bundle savedInstanceState) {
        super.onViewModelCreated(viewModel, savedInstanceState);
        if (savedInstanceState == null) {
            viewModel.viewInitWith(getIntent().getBooleanExtra(MANUAL_MODE, false));
        }
    }

    @Override
    protected void onBindToViewModel(OfflineViewModel viewModel) {
        super.onBindToViewModel(viewModel);
        mFilesViewDelegate.bindProgressField(viewModel.getProgress());
        mFilesViewDelegate.bindMessageField(viewModel.getMessage());

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(viewModel.getManualMode().get());
        }

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
        MenuItem online = menu.getItem(R.id.action_online_mode);
        if (online != null){
            online.setVisible(!getViewModel().getManualMode().get());
        }
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
    protected void onStop() {
        super.onStop();
        mFilesViewDelegate.onStop();
    }

    @Override
    protected void onUnbindViewModel(OfflineViewModel viewModel) {
        super.onUnbindViewModel(viewModel);
        mFilesViewDelegate.unbind();
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
