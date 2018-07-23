package com.afterlogic.aurora.drive.presentation.common.decorations;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by sashka on 04.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class HorizontalDividerDecoration extends RecyclerView.ItemDecoration {

    public static final int INTRINSIC_HEIGHT = -1;

    private final int mHeight;
    private final Drawable mDivider;
    private final int mPadding;

    private int mWidth = -1;

    public HorizontalDividerDecoration(Drawable divider) {
        this(INTRINSIC_HEIGHT, divider, 0);
    }

    public HorizontalDividerDecoration(int height, Drawable divider) {
        this(height, divider, 0);
    }

    public HorizontalDividerDecoration(int height, Drawable divider, int padding) {
        if (height > 0) {
            mHeight = height;
        } else {
            mHeight = divider.getIntrinsicHeight();
        }
        mDivider = divider;
        mPadding = padding;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        mWidth = parent.getWidth();

        for (int i = 0; i < parent.getChildCount(); i++) {

            View view = parent.getChildAt(i);

            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();

            int top = -1;

            if (needDrawDividerAfterView(view, parent)) {
                top = view.getBottom() + lp.bottomMargin;
            }
            if (needDrawDividerBefore(view, parent)){
                top = view.getTop() + lp.topMargin - mHeight;
            }

            if (top != -1) {
                draw(c, top);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        boolean drawBefore = needDrawDividerBefore(view, parent);
        boolean drawAfter = needDrawDividerAfterView(view, parent);

        outRect.set(
                0,
                drawBefore ? mHeight : 0,
                0,
                drawAfter ? mHeight : 0
        );
    }

    private void draw(Canvas c, int top){
        mDivider.setBounds(mPadding, top, mWidth - mPadding, top + mHeight);
        mDivider.draw(c);
    }

    protected boolean needDrawDividerAfterView(View view, RecyclerView recyclerView) {
        return recyclerView.getChildAdapterPosition(view) != recyclerView.getAdapter().getItemCount() - 1;
    }

    protected boolean needDrawDividerBefore(View view, RecyclerView recyclerView){
        return false;
    }
}
