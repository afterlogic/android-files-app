package com.afterlogic.aurora.drive.presentation.modules.main.view;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.DialogSheetMainFileActionsBinding;
import com.afterlogic.aurora.drive.model.FileAction;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.FileActionDialogViewModel;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFileItemViewModel;

/**
 * Created by sashka on 23.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FileActionsBottomSheet extends BottomSheetDialogFragment implements FileAction.OnActionListener
{
    private FileActionCallback mListener;

    public static FileActionsBottomSheet newInstance() {
        return new FileActionsBottomSheet();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment target = getTargetFragment();
        if (target != null && target instanceof FileActionCallback){
            mListener = (FileActionCallback) target;
        }
        if (context instanceof FileActionCallback){
            mListener = (FileActionCallback) context;
        }
        if (mListener == null){
            throw new IllegalStateException("Target fragment or activity must implement callback.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_sheet_main_file_actions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DialogSheetMainFileActionsBinding binding = DataBindingUtil.bind(view);
        MainFileItemViewModel targetModel = mListener.getFileActionTarget();
        binding.setViewModel(new FileActionDialogViewModel(targetModel, this));
        binding.executePendingBindings();
    }

    @Override
    public void onFileAction(FileAction action) {
        mListener.onFileAction(action.getId());
        dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        mListener.onCancelFileActions();
    }


    public interface FileActionCallback {
        void onFileAction(int action);
        MainFileItemViewModel getFileActionTarget();
        void onCancelFileActions();
    }
}
