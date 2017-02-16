package com.afterlogic.aurora.drive.presentation.common.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.afterlogic.aurora.drive.R;

/**
 * Created by sashka on 23.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class DialogUtil {

    public static void showInputDialog(String title, Context ctx, final OnInputListener listener){
        showInputDialog(title, null, ctx, listener);
    }

    public static void showInputDialog(String title, String text, Context ctx, final OnInputListener listener){
        View inputView = LayoutInflater.from(ctx)
                .inflate(R.layout.item_layout_dialog_input, null);
        showInputDialog(inputView, title, text, ctx, listener);
    }

    public static void showInputDialog(View inputView, String title, String text, Context ctx,
                                       final OnInputListener listener){
        final EditText input = (EditText) inputView.findViewById(R.id.input);

        if (!TextUtils.isEmpty(text)){
            input.setText(text);
            input.setSelection(text.length());
        }

        final AlertDialog d = new AlertDialog.Builder(ctx, R.style.AppTheme_Dialog)
                .setView(inputView)
                .setTitle(title)
                .setPositiveButton(ctx.getString(R.string.dialog_ok), null)
                .setNegativeButton(ctx.getString(R.string.dialog_cancel), (dialog, which) -> {
                    input.clearFocus();
                    dialog.dismiss();
                })
                .create();

        d.setOnShowListener(dialog -> {

            input.requestFocus();
            Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(view -> listener.onInput(d, input));


            InputMethodManager keyboard = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(input, 0);
        });

        d.show();
    }

    public interface OnInputListener{
        void onInput(DialogInterface dialogInterface, EditText input);
    }
}
