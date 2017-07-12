package com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.view;

import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMBottomSheetDialogFragment;
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.viewModel.MainFilesActionViewModel;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFilesActionBottomSheet extends InjectableMVVMBottomSheetDialogFragment<MainFilesActionViewModel> {

    public static MainFilesActionBottomSheet newInstance() {

        Bundle args = new Bundle();

        MainFilesActionBottomSheet fragment = new MainFilesActionBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public ViewDataBinding createBinding(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return DataBindingUtil.inflate(inflater, R.layout.main_file_actions_fragment, container, false);
    }

    @Override
    public MainFilesActionViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(MainFilesActionViewModel.class);
    }
}
