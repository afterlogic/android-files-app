package com.afterlogic.aurora.drive.presentation.modules.replace.view;

import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.FragmentReplaceFilesBinding;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.BindingUtil;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMFragment;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.view.FileListArgs;
import com.afterlogic.aurora.drive.presentation.modules.replace.interactor.ReplaceFileTypeViewInteractor;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceFileTypeViewModel;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileTypeFragment extends InjectableMVVMFragment<ReplaceFileTypeViewModel> {

    @Inject
    ReplaceFileTypeViewInteractor viewInteractor;

    public static ReplaceFileTypeFragment newInstance(String type) {

        ReplaceFileTypeFragment fragment = new ReplaceFileTypeFragment();

        FileListArgs args = new FileListArgs();
        args.setType(type);

        fragment.setArguments(args.getBundle());

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewInteractor.bindView(this);
        getViewModel().setArgs(new FileListArgs(getArguments()));
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
    protected void bindStarted(ReplaceFileTypeViewModel vm, UnbindableObservable.Bag bag) {
        super.bindStarted(vm, bag);
        BindingUtil.bindProgressDialog(vm.progress, bag, getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewInteractor.clearView();
    }
}
