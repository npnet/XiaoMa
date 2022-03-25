package com.xiaoma.music.welcome.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiaoma.music.welcome.ui.PreferenceSelectFragment;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/8
 */
public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;
    private int rightSpace;

    public GridSpaceItemDecoration(int space, int rightSpace) {
        mSpace = space;
        this.rightSpace = rightSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) < PreferenceSelectFragment.SPAN_PREFERENCE) {
            outRect.top = mSpace;
            outRect.right = rightSpace;
        } else {
            outRect.top = mSpace;
            outRect.right = rightSpace;
        }
    }
}
