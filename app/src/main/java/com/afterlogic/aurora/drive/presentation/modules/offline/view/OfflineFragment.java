package com.afterlogic.aurora.drive.presentation.modules.offline.view;

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
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.OfflineArgs;
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.OfflineFileListViewModel;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineFragment extends InjectableMVVMFragment<OfflineFileListViewModel> {

    public static OfflineFragment newInstance(boolean manual) {

        OfflineArgs args = new OfflineArgs();
        args.setType("offline");
        args.setManual(manual);

        OfflineFragment fragment = new OfflineFragment();
        fragment.setArguments(args.getBundle());
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewModel().setArgs(new OfflineArgs(getArguments()));
    }

    @Override
    public ViewDataBinding createBinding(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return DataBindingUtil.inflate(inflater, R.layout.offline_files_fragment, container, false);
    }

    @Override
    public OfflineFileListViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(OfflineFileListViewModel.class);
    }

    @Override
    protected void bindStarted(OfflineFileListViewModel vm, UnbindableObservable.Bag bag) {
        super.bindStarted(vm, bag);

        BindingUtil.bindDialog(vm.message, bag, getContext());

        BindingUtil.bindContextMenu(vm.fileContextCommand, bag);

    }
}
