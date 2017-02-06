package com.afterlogic.aurora.drive._unrefactored.presentation.ui.common.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by sashka on 25.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class SelectionEditText extends EditText {

    private OnSelectionChangeListener mOnSelectionChangeListener;

    public SelectionEditText(Context context) {
        super(context);
    }

    public SelectionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SelectionEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (mOnSelectionChangeListener != null){
            mOnSelectionChangeListener.onSelectionChanged(selStart, selEnd);
        }
    }

    public void setOnSelectionChangeListener(OnSelectionChangeListener onSelectionChangeListener) {
        mOnSelectionChangeListener = onSelectionChangeListener;
    }

    public interface OnSelectionChangeListener {
        void onSelectionChanged(int start, int end);
    }
}
