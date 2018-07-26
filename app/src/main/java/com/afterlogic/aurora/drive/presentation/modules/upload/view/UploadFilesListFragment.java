package com.afterlogic.aurora.drive.presentation.modules.upload.view;

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
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadArgs;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadFileListViewModel;

/**
 * Created by aleksandrcikin on 19.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadFilesListFragment extends InjectableMVVMFragment<UploadFileListViewModel> {

    public static UploadFilesListFragment newInstance(String type) {

        UploadFilesListFragment fragment = new UploadFilesListFragment();

        UploadArgs args = new UploadArgs();
        args.setType(type);

        fragment.setArguments(args.getBundle());

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewModel().setArgs(new UploadArgs(getArguments()));
    }

    @Override
    public ViewDataBinding createBinding(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return DataBindingUtil.inflate(inflater, R.layout.upload_files_fragment, container, false);
    }

    @Override
    public UploadFileListViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(UploadFileListViewModel.class);
    }

    @Override
    protected void bindStarted(UploadFileListViewModel vm, UnbindableObservable.Bag bag) {
        super.bindStarted(vm, bag);

        BindingUtil.bindProgressDialog(vm.progress, bag, getActivity());

        BindingUtil.bindDialog(vm.message, bag, getActivity());
    }
}
