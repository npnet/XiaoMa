package com.xiaoma.xting.player.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


/**
 * <des>
 * 仅限于LinearLayoutManager使用
 *
 * @author YangGang
 * @date 2018/11/8
 */
public class SimpleLinearDivider extends DividerItemDecoration {

    private int mHeadSpace, mMiddleSpace, mTailSpace;
    @RecyclerView.Orientation
    private int mOrientation;
    public static final String TAG = "DIVIDER";

    public SimpleLinearDivider(Context context, int orientation) {
        super(context, orientation);
        this.mOrientation = orientation;
    }

    public void setDividerSpace(int headMargin, int middleHeadMargin, int endMargin) {
        this.mHeadSpace = headMargin;
        this.mMiddleSpace = middleHeadMargin;
        this.mTailSpace = endMargin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int childCount = parent.getLayoutManager().getItemCount();
        int headIndex = 0;
        int tailIndex = childCount - 1;
        int position = parent.getLayoutManager().getPosition(view);
        Log.d(TAG, String.format("total is %1$d,tail is %2$d,position is %3$d", childCount, tailIndex, position));
        if (mOrientation == RecyclerView.HORIZONTAL) {
            //set head margin;
            if (position == headIndex) {
                outRect.left = mHeadSpace;
                //set tail margin
                Log.d(TAG, String.format("pos is %1$s , margin : leftHead", position));
            } else if (position == tailIndex) {
                outRect.left = mMiddleSpace;
                outRect.right = mTailSpace;

                Log.d(TAG, String.format("pos is %1$s , margin : left|right", position));
            } else {
                //set middle margin
                outRect.left = mMiddleSpace;
                Log.d(TAG, String.format("pos is %1$s , margin : leftMiddle", position));
            }
        } else {
            //set head margin;
            if (position == headIndex) {
                outRect.top = mHeadSpace;
                //set tail margin
            } else if (position == tailIndex) {
                outRect.top = mTailSpace;
            } else {
                //set middle margin
                outRect.top = mMiddleSpace;
                outRect.bottom = mTailSpace;
            }
        }

        setDrawable(new ColorDrawable());
    }
}
