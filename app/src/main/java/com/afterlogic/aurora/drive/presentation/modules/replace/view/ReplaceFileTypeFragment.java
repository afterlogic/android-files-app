package com.afterlogic.aurora.drive.presentation.modules.replace.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.Optional;
import com.afterlogic.aurora.drive.databinding.FragmentReplaceFilesBinding;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMFragment;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ProgressViewModel;
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

        ReplaceFileTypeArgs args = new ReplaceFileTypeArgs.Builder()
                .setType(type)
                .build();

        fragment.setArguments(args.getBundle());

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewInteractor.bind(this);
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
    protected void bindStarted(ReplaceFileTypeViewModel replaceFileTypeViewModel, UnbindableObservable.Bag bag) {
        super.bindStarted(replaceFileTypeViewModel, bag);

        Optional<ProgressDialog> progressDialog = new Optional<>();
        UnbindableObservable.bind(replaceFileTypeViewModel.progress, bag, field -> onProgressChanged(field, progressDialog))
                .addOnUnbindListener(field -> progressDialog.ifPresent(Dialog::dismiss));
    }

    private void onProgressChanged(ObservableField<ProgressViewModel> field, Optional<ProgressDialog> dialogHolder) {
        ProgressViewModel progress = field.get();
        if (progress == null) {
            dialogHolder.ifPresent(Dialog::dismiss);
        } else {
            ProgressDialog dialog = dialogHolder.getValue();
            if (dialog != null) {
                if (dialog.isIndeterminate() == progress.isIndeterminate()) {
                    updateProgressDialog(progress, dialog);
                } else {
                    dialog = null;
                }
            }

            if (dialog == null) {
                dialog = new ProgressDialog(getContext(), R.style.AppTheme_Dialog_CompatBackground);
                dialog.setOnDismissListener(dialogInterface -> dialogHolder.clear());
                updateProgressDialog(progress, dialog);
                dialogHolder.set(dialog);
                dialog.show();
            }
        }
    }

    private void updateProgressDialog(ProgressViewModel progress, ProgressDialog dialog) {
        dialog.setIndeterminate(progress.isIndeterminate());
        dialog.setMessage(progress.getMessage());
        dialog.setTitle(progress.getTitle());
        dialog.setMax(progress.getMax());
        dialog.setProgress(progress.getProgress());
    }
}
