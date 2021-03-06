package com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.view;

import android.app.Dialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMBottomSheetDialogFragment;
import com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.viewModel.MainFilesActionViewModel;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnCancelListener(di -> getViewModel().onCancel());
        return super.onCreateDialog(savedInstanceState);
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
