package com.xiaoma.xting.welcome;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.xiaoma.xting.welcome.ui.fragment.PreferenceSelectFragment;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/8
 */
public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int mRow;
    private int mStartMargin, mMiddleMargin, mTopMargin;
    private int mSpace;

    public GridSpaceItemDecoration(int rowEnd) {
        this.mRow = rowEnd;
    }

    public void setDivider(int startMargin, int middleMargin, int topMargin) {
        this.mStartMargin = startMargin;
        this.mMiddleMargin = middleMargin;
        this.mTopMargin = topMargin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = mTopMargin;

        int childCount = parent.getLayoutManager().getItemCount();
        int childIndex = parent.getChildLayoutPosition(view);

        if (childIndex < childCount - mRow) {
            outRect.right = mMiddleMargin;
        } else {
            outRect.right = 0;
        }

        if (childCount < mRow) {
            outRect.left = mStartMargin;
        }
    }
}
