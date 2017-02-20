package com.afterlogic.aurora.drive.presentation.modules._baseFiles.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.ObservableField;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.afterlogic.aurora.drive.presentation.common.components.view.SelectionEditText;
import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.dialog.DialogCancelListener;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.dialog.MessageDialogViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.dialog.ProgressDialogViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.dialog.StringInputDialogViewModel;

import java.text.NumberFormat;

import static android.R.string.cancel;
import static android.content.DialogInterface.BUTTON_NEGATIVE;

/**
 * Created by sashka on 20.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FilesViewDialogDelegate implements Stoppable{


    private final Context mContext;
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    private Dialog mCurrentMessageDialog;
    private ObservableField<MessageDialogViewModel> mMessageField;
    private SimpleListener mMessageListener = new SimpleListener(this::updateMessageView);


    private Dialog mCurrentInputDialog;
    private ObservableField<StringInputDialogViewModel> mStringInputField;
    private SimpleListener mStringInputListener = new SimpleListener(this::updateStringInputView);

    private ProgressDialog mProgressDialog;
    private ObservableField<ProgressDialogViewModel> mProgressField;
    private SimpleListener mProgressListener = new SimpleListener(this::updateProgressView);

    private boolean mIsActive = false;

    public FilesViewDialogDelegate(Context context) {
        mContext = context;
    }

    @Override
    public void onStart() {
        mIsActive = true;
        updateProgressView();
        updateMessageView();
        updateStringInputView();
    }

    @Override
    public void onStop() {
        mIsActive = false;
        hideProgress();
        hideMessageDialog();
        hideInputDialog();
    }

    public void bindProgressField(@NonNull ObservableField<ProgressDialogViewModel> field){
        unbindProgressField();

        mProgressField = field;
        mProgressField.addOnPropertyChangedCallback(mProgressListener);
        updateProgressView();
    }

    public void bindMessageField(@NonNull ObservableField<MessageDialogViewModel> field){
        unbindMessageField();

        mMessageField = field;
        mMessageField.addOnPropertyChangedCallback(mMessageListener);
        updateMessageView();
    }

    public void bindStringInputField(@NonNull ObservableField<StringInputDialogViewModel> field){
        unbindStringInputField();

        mStringInputField = field;
        mStringInputField.addOnPropertyChangedCallback(mStringInputListener);
        updateStringInputView();
    }

    public void unbind(){
        unbindProgressField();
        unbindMessageField();
        unbindStringInputField();
    }

    private void unbindProgressField(){
        if (mProgressField == null) return;
        mProgressField.removeOnPropertyChangedCallback(mProgressListener);
        mProgressField = null;
    }

    private void unbindMessageField(){
        if (mMessageField == null) return;
        mMessageField.removeOnPropertyChangedCallback(mMessageListener);
        mMessageField = null;
    }

    private void unbindStringInputField(){
        if (mStringInputField == null) return;
        mStringInputField.removeOnPropertyChangedCallback(mStringInputListener);
        mStringInputField = null;
    }

    private void updateMessageView(){
        if (!mIsActive) return;

        mMainThreadHandler.post(() -> {
            hideMessageDialog();
            MessageDialogViewModel model = mMessageField != null ? mMessageField.get() : null;
            if (model != null){
                Runnable onCancel = () -> {
                    DialogCancelListener listener = model.getCancelListener();
                    if (listener != null){
                        listener.onCancel();
                    }
                };
                mCurrentMessageDialog = new AlertDialog.Builder(mContext, R.style.AppTheme_Dialog)
                        .setTitle(model.getTitle())
                        .setMessage(model.getMessage())
                        .setOnCancelListener(dialogInterface -> onCancel.run())
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> onCancel.run())
                        .show();
            }
        });
    }

    private void updateStringInputView(){
        if (!mIsActive) return;

        mMainThreadHandler.post(() -> {
            hideInputDialog();
            StringInputDialogViewModel model = mStringInputField != null ? mStringInputField.get() : null;
            if (model != null){
                //[START Prepare input view (disallow change file extension)]
                @SuppressLint("InflateParams")
                View inputView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_layout_dialog_input, null);
                final SelectionEditText input = (SelectionEditText) inputView.findViewById(R.id.input);

                if (model.getNonEditableTextEndLenght() > 0) {
                    //Set disallow only for 'normal' file

                    input.setOnSelectionChangeListener((start, end) -> {
                        int lenght = input.getText().length();
                        int max = lenght - model.getNonEditableTextEndLenght() - 1;
                        boolean fixed = false;
                        if (start > max) {
                            start = max;
                            fixed = true;
                        }
                        if (end > max) {
                            end = max;
                            fixed = true;
                        }
                        if (fixed) {
                            input.setSelection(start, end);
                        }
                    });
                }
                //[END Prepare input view (disallow change file extension)]

                if (!TextUtils.isEmpty(model.getMessage())){
                    input.setText(model.getMessage());
                    input.setSelection(model.getMessage().length());
                }

                AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.AppTheme_Dialog)
                        .setView(inputView)
                        .setTitle(model.getTitle())
                        .setPositiveButton(mContext.getString(R.string.dialog_ok), null)
                        .setNegativeButton(mContext.getString(R.string.dialog_cancel), (dialogInterface, which) -> {
                            input.clearFocus();
                            dialogInterface.cancel();
                        })
                        .setOnCancelListener(dialogInterface -> {
                            DialogCancelListener listener = model.getCancelListener();
                            if (listener != null) listener.onCancel();
                        })
                        .create();

                dialog.setOnShowListener(showedDialog -> {

                    input.requestFocus();
                    Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(view -> {

                        String newName = input.getText().toString();
                        if (TextUtils.isEmpty(newName)){
                            input.setError(mContext.getString(R.string.error_field_required));
                            input.requestFocus();
                            return;
                        }

                        input.clearFocus();

                        InputMethodManager keyboard = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.hideSoftInputFromWindow(input.getWindowToken(), 0);

                        dialog.dismiss();

                        final String trimmed = newName.trim();

                        //Check new name if it is same as old closeQuietly dialog without any action
                        if (!newName.equals(model.getMessage()) && !trimmed.equals(model.getMessage())){
                            model.getResultConsumer().consume(trimmed);
                        }
                    });


                    InputMethodManager keyboard = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(input, 0);
                });

                mCurrentInputDialog = dialog;
                dialog.show();
            }
        });
    }

    private void updateProgressView(){
        if (!mIsActive) return;

        mMainThreadHandler.post(() -> {
            ProgressDialogViewModel model = mProgressField != null ? mProgressField.get() : null;

            if (model != null){
                if (mProgressDialog == null){
                    mProgressDialog = new ProgressDialog(mContext, R.style.AppTheme_Dialog);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(false);
                    if (model.getCancelListener() != null) {
                        mProgressDialog.setButton(
                                BUTTON_NEGATIVE,
                                mContext.getString(cancel),
                                (dialogInterface, i) -> model.getCancelListener().onCancel()
                        );
                    }
                }

                mProgressDialog.setTitle(model.getTitle());
                mProgressDialog.setMessage(model.getMessage());

                int progress = model.getProgress();
                int max = model.getMax();
                boolean concrete = progress >= 0 && model.getMax() > 0;

                if (concrete) {
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setMax(max);
                    mProgressDialog.setProgress(progress);
                    mProgressDialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
                    mProgressDialog.setProgressNumberFormat("%1d/%2d");
                } else {
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setProgressPercentFormat(null);
                    mProgressDialog.setProgressNumberFormat(null);
                }

                if (!mProgressDialog.isShowing()){
                    mProgressDialog.show();
                }
            } else {
                hideProgress();
            }
        });
    }

    private void hideInputDialog(){
        if (mCurrentInputDialog != null){
            mCurrentInputDialog.dismiss();
            mCurrentInputDialog = null;
        }
    }

    private void hideMessageDialog(){
        if (mCurrentMessageDialog != null){
            mCurrentMessageDialog.dismiss();
            mCurrentMessageDialog = null;
        }
    }

    private void hideProgress(){
        if (mProgressDialog != null){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
