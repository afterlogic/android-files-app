package com.afterlogic.aurora.drive.presentation.common.modules.v3.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.v7.widget.SearchView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.Optional;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.Bindable;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ProgressViewModel;

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

    public static void bindProgressDialog(ObservableField<ProgressViewModel> field, UnbindableObservable.Bag bag, Context context) {
        Optional<ProgressDialog> progressDialog = new Optional<>();
        UnbindableObservable.bind(field, bag, f -> onProgressChanged(f, progressDialog, context))
                .addOnUnbindListener(f -> progressDialog.ifPresent(Dialog::dismiss));
    }

    private static void onProgressChanged(ObservableField<ProgressViewModel> field, Optional<ProgressDialog> dialogHolder, Context context) {
        ProgressViewModel progress = field.get();
        if (progress == null) {
            dialogHolder.ifPresent(Dialog::dismiss);
        } else {
            ProgressDialog dialog = dialogHolder.get();
            if (dialog != null) {
                if (dialog.isIndeterminate() == progress.isIndeterminate()) {
                    updateProgressDialog(progress, dialog);
                } else {
                    dialog = null;
                }
            }

            if (dialog == null) {
                dialog = new ProgressDialog(context, R.style.AppTheme_Dialog_CompatBackground);
                dialog.setOnDismissListener(dialogInterface -> dialogHolder.clear());
                updateProgressDialog(progress, dialog);
                dialogHolder.set(dialog);
                dialog.show();
            }
        }
    }

    private static void updateProgressDialog(ProgressViewModel progress, ProgressDialog dialog) {
        dialog.setIndeterminate(progress.isIndeterminate());
        dialog.setMessage(progress.getMessage());
        dialog.setTitle(progress.getTitle());
        dialog.setMax(progress.getMax());
        dialog.setProgress(progress.getProgress());
    }
}
