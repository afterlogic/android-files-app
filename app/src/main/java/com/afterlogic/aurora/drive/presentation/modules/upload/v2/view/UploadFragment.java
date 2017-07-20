package com.afterlogic.aurora.drive.presentation.modules.upload.v2.view;

import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMFragment;
import com.afterlogic.aurora.drive.presentation.modules.upload.v2.viewModel.UploadArgs;
import com.afterlogic.aurora.drive.presentation.modules.upload.v2.viewModel.UploadFileListViewModel;

/**
 * Created by aleksandrcikin on 19.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadFragment extends InjectableMVVMFragment<UploadFileListViewModel> {

    public static UploadFragment newInstance(String type) {

        UploadFragment fragment = new UploadFragment();

        UploadArgs args = new UploadArgs();
        args.setType(type);

        fragment.setArguments(args.getBundle());

        return fragment;
    }

    @Override
    public ViewDataBinding createBinding(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public UploadFileListViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(UploadFileListViewModel.class);
    }
}
