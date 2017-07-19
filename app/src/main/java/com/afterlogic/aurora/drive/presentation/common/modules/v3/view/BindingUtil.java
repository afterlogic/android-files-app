package com.afterlogic.aurora.drive.presentation.common.modules.v3.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.Optional;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.Bindable;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog.MessageDialogViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog.ProgressViewModel;
import com.afterlogic.aurora.drive.presentation.common.view.AppProgressDialog;

import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by aleksandrcikin on 05.07.17.
 * mail: mail@sunnydaydev.me
 */

public class BindingUtil {

    public static void bindSearchView(ObservableField<SearchView> view, Bindable<String> queryField, Bindable<Boolean> show, UnbindableObservable.Bag bag) {

        UnbindableObservable showBind = UnbindableObservable.bind(show, bag, showSearch -> {
            SearchView searchView = view.get();
            if (searchView == null) return;
            searchView.setIconified(!showSearch.get());
        });

        UnbindableObservable queryBind = UnbindableObservable.bind(queryField, bag, query -> {
            SearchView searchView = view.get();
            if (searchView == null) return;
            searchView.setQuery(query.get(), false);
        });

        UnbindableObservable.bind(view, bag, searchViewField -> {
            SearchView searchView = searchViewField.get();
            if (searchView != null) {

                searchView.setOnSearchClickListener(v -> {
                    show.set(true);
                    searchView.setQuery(queryField.get(), false);
                });

                searchView.setOnCloseListener(() -> {
                    show.set(false);
                    return false;
                });

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        queryField.set(newText);
                        return false;
                    }
                });

                showBind.notifyChanged();
                queryBind.notifyChanged();
            }
        });
    }

    // region Progress

    public static void bindProgressDialog(ObservableField<ProgressViewModel> field, UnbindableObservable.Bag bag, Context context) {
        Optional<AppProgressDialog> progressDialog = new Optional<>();
        UnbindableObservable
                .bind(field, bag, f -> onProgressChanged(f.get(), progressDialog, context))
                .addOnUnbindListener(f -> progressDialog.ifPresent(Dialog::dismiss));
    }

    private static void onProgressChanged(ProgressViewModel progress, Optional<AppProgressDialog> optionalProgressDialog, Context context) {
        if (progress == null) {
            optionalProgressDialog.ifPresent(Dialog::dismiss);
            optionalProgressDialog.set(null);
        } else {

            AppProgressDialog dialog = optionalProgressDialog.get();
            if (dialog != null && needRecreateDialog(dialog, progress)) {
                dialog.dismiss();
                dialog = null;
            }

            if (dialog == null) {

                dialog = instantiateNewDialog(progress, context);

                optionalProgressDialog.set(dialog);
                dialog.setOnDismissListener(dialogInterface -> {
                    if (optionalProgressDialog.get() == dialogInterface) {
                        optionalProgressDialog.clear();
                    }
                });

                dialog.show();
            } else {
                updateProgressDialog(progress, dialog);
            }
        }
    }

    private static boolean needRecreateDialog(AppProgressDialog currentDialog, ProgressViewModel progress) {
        boolean viewModelSpinner = progress.getProgressBar() instanceof ProgressViewModel.CircleProgressBar;
        boolean dialogSpinner = currentDialog.getProgressStyle() == ProgressDialog.STYLE_SPINNER;

        boolean currentCancellable = currentDialog.getButton(ProgressDialog.BUTTON_NEGATIVE) != null;

        return viewModelSpinner != dialogSpinner || currentCancellable != progress.isCancellable();
    }

    private static AppProgressDialog instantiateNewDialog(ProgressViewModel progress, Context context) {
        AppProgressDialog dialog = new AppProgressDialog(context, R.style.AppTheme_Dialog_CompatBackground);

        dialog.setCancelable(false);

        ProgressViewModel.ProgressBar progressBar = progress.getProgressBar();
        if (progressBar instanceof ProgressViewModel.CircleProgressBar) {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        } else {
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }

        if (progress.isCancellable()) {
            dialog.setButton(
                    ProgressDialog.BUTTON_NEGATIVE,
                    context.getString(R.string.dialog_cancel),
                    (di, which) -> progress.onCancel()
            );
        }

        updateProgressDialog(progress, dialog);
        return dialog;
    }

    private static void updateProgressDialog(ProgressViewModel progress, ProgressDialog dialog) {
        dialog.setMessage(progress.getMessage());
        dialog.setTitle(progress.getTitle());

        ProgressViewModel.ProgressBar progressBar = progress.getProgressBar();
        dialog.setIndeterminate(progressBar.isIndeterminate());
        dialog.setMax(progressBar.getMax());
        dialog.setProgress(progressBar.getProgress());

        if (progressBar.isIndeterminate()) {
            dialog.setProgressPercentFormat(null);
            dialog.setProgressNumberFormat(null);
        } else {
            dialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
            dialog.setProgressNumberFormat("%1d/%2d");
        }
    }

    // endregion

    // region Dialogs

    public static void bindDialog(ObservableField<MessageDialogViewModel> field, UnbindableObservable.Bag bag, Context context) {
        AtomicReference<AlertDialog> dialogRef = new AtomicReference<>();

        UnbindableObservable.bind(field, bag, f -> {
            AlertDialog dialog = dialogRef.getAndSet(null);
            if (dialog != null) {
                dialog.dismiss();
            }

            MessageDialogViewModel vm = f.get();
            if (vm != null) {
                dialog = new AlertDialog.Builder(context, R.style.AppTheme_Dialog)
                        .setTitle(vm.getTitle())
                        .setMessage(vm.getMessage())
                        .setPositiveButton(android.R.string.ok, (di, i) -> vm.onClose())
                        .setCancelable(true)
                        .setOnCancelListener(di -> vm.onClose())
                        .show();
                dialogRef.set(dialog);
            }
        })//---|
                .addOnUnbindListener(f -> {
                    AlertDialog dialog = dialogRef.getAndSet(null);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                });
    }

    // endregion
}
