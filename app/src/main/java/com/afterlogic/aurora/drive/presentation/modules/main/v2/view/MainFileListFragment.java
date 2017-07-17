package com.afterlogic.aurora.drive.presentation.modules.main.v2.view;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.model.events.ActivityResultEvent;
import com.afterlogic.aurora.drive.model.events.PermissionGrantEvent;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.di.ForViewInteractor;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.BindingUtil;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMFragment;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.view.FileListArgs;
import com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor.MainFilesListViewInteractor;
import com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel.MainFilesListViewModel;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFileListFragment extends InjectableMVVMFragment<MainFilesListViewModel> {

    public static MainFileListFragment newInstance(String type) {

        MainFileListFragment fragment = new MainFileListFragment();

        FileListArgs args = new FileListArgs();
        args.setType(type);

        fragment.setArguments(args.getBundle());

        return fragment;
    }

    @Inject
    protected MainFilesListViewInteractor viewInteractor;

    @Inject
    @ForViewInteractor
    protected EventBus eventBus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewModel().setArgs(new FileListArgs(getArguments()));
    }

    @Override
    public ViewDataBinding createBinding(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return DataBindingUtil.inflate(inflater, R.layout.main_files_fragment, container, false);
    }

    @Override
    public MainFilesListViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(MainFilesListViewModel.class);
    }

    @Override
    protected void bindStarted(MainFilesListViewModel vm, UnbindableObservable.Bag bag) {
        super.bindStarted(vm, bag);

        BindingUtil.bindProgressDialog(vm.progress, bag, getContext());

        BindingUtil.bindDialog(vm.messageDialog, bag, getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        viewInteractor.bindView(this);
    }

    @Override
    public void onStop() {
        viewInteractor.clearView();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        eventBus.post(new PermissionGrantEvent(requestCode, permissions, grantResults));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        eventBus.post(new ActivityResultEvent(requestCode, resultCode, data));
    }
}
