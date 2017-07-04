package com.afterlogic.aurora.drive.presentation.modules.replace.view;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.FragmentReplaceFilesBinding;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMFragment;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceFileTypeViewModel;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceViewModel;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileTypeFragment extends InjectableMVVMFragment<ReplaceFileTypeViewModel> {

    public static ReplaceFileTypeFragment newInstance(String type) {

        ReplaceFileTypeArgs args = new ReplaceFileTypeArgs();
        args.setType(type);

        ReplaceFileTypeFragment fragment = new ReplaceFileTypeFragment();
        fragment.setArguments(args.getBundle());
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewModel().setArgs(new ReplaceFileTypeArgs(getArguments()));
    }

    @Override
    public ViewDataBinding createBinding(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return DataBindingUtil.<FragmentReplaceFilesBinding>inflate(inflater, R.layout.fragment_replace_files, container, false);
    }

    @Override
    public ReplaceFileTypeViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(ReplaceFileTypeViewModel.class);
    }

    @Override
    protected void bindCreated(ReplaceFileTypeViewModel vm, UnbindableObservable.Bag bag) {
        super.bindCreated(vm, bag);

        UnbindableObservable.bind(vm.stackSize, bag, field -> {
            ReplaceViewModel rootVm = ViewModelProviders.of(getActivity()).get(ReplaceViewModel.class);
            rootVm.onFolderStackChanged(getViewModel().fileType, field.get());
        });
    }
}
