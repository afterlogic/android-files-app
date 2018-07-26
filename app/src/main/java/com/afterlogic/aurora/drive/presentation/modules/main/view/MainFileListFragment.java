package com.afterlogic.aurora.drive.presentation.modules.main.view;

import androidx.lifecycle.ViewModelProvider;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.BindingUtil;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMFragment;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.view.FileListArgs;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFilesListViewModel;

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

}
