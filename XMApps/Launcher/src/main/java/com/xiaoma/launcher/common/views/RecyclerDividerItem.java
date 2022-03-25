package com.xiaoma.launcher.common.views;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author zs
 * @date 2018/10/10 0010.
 */
public class RecyclerDividerItem extends DividerItemDecoration {

    private int left;
    private int top;
    private int right;
    private int bottom;
    private int first;

    public RecyclerDividerItem(Context context, int orientation) {
        super(context, orientation);
    }

    public void setRect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void setRect(int left, int top, int right, int bottom, int first) {
        setRect(left, top, right, bottom);
        this.first = first;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        final int itemCount = parent.getAdapter().getItemCount();
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            final int orientation = gridLayoutManager.getOrientation();
            if (orientation == LinearLayout.HORIZONTAL) {
                final int position = parent.getChildLayoutPosition(view);
                if (position == 0 || position == spanCount - 1) {
                    if (first != 0) {
                        outRect.set(first, top, right, bottom);
                    } else {
                        outRect.set(left, top, right, bottom);
                    }
                } else if (position == itemCount - 1
                        || (itemCount % spanCount == 0 && (position == itemCount - spanCount))) {
                    if (first != 0) {
                        outRect.set(left, top, first, bottom);
                    } else {
                        outRect.set(left, top, right, bottom);
                    }
                } else {
                    outRect.set(left, top, right, bottom);
                }
            } else if (orientation == LinearLayout.VERTICAL) {
                final int position = parent.getChildLayoutPosition(view);
                if ((position + 1) % spanCount == 0) {
                    outRect.set(left, top, 0, bottom);
                } else {
                    outRect.set(left, top, right, bottom);
                }
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            final int position = parent.getChildLayoutPosition(view);
            if (position == 0) {
                if (first != 0) {
                    outRect.set(first, top, right, bottom);
                } else {
                    outRect.set(left, top, right, bottom);
                }
            } else if (position == itemCount - 1) {
                if (first != 0) {
                    outRect.set(left, top, first, bottom);
                } else {
                    outRect.set(left, top, right, bottom);
                }
            } else {
                outRect.set(left, top, right, bottom);
            }
        }
        // 通过设置一个空的drawable来隐藏默认分割线
        setDrawable(new ColorDrawable());
    }
}
