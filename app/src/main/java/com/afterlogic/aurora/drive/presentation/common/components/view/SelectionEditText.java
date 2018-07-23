package com.afterlogic.aurora.drive.presentation.common.components.view;

import android.content.Context;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * Created by sashka on 25.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class SelectionEditText extends AppCompatEditText {

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
