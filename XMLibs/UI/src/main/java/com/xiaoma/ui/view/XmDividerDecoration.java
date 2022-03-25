package com.xiaoma.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author KY
 * @date 2018/10/25
 */
public class XmDividerDecoration extends DividerItemDecoration {

    private Rect rect;
    private int extraMargin;
    private int spanCount;

    public XmDividerDecoration(Context context, int orientation) {
        super(context, orientation);
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public void setRect(int l, int t, int r, int b) {
        this.rect = new Rect(l, t, r, b);
    }

    /**
     * 设置 头尾item的 margin
     *
     * @param extraMargin 头尾item的 margin
     */
    public void setExtraMargin(int extraMargin, int spanCount) {
        this.extraMargin = extraMargin;
        this.spanCount = spanCount;
    }

    /**
     * 设置 头尾item的 额外margin
     *
     * @param extraMargin 头尾item的 margin
     */
    public void setExtraMargin(int extraMargin) {
        this.extraMargin = extraMargin;
        this.spanCount = 1;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        Rect rect = new Rect(this.rect);
        if (extraMargin != 0) {
            if (parent.getLayoutManager().getPosition(view) < spanCount) {
                rect.left += extraMargin;
            } else if (parent.getAdapter().getItemCount() % spanCount == 0
                    && parent.getLayoutManager().getPosition(view) >= (parent.getAdapter().getItemCount() - spanCount)) {
                rect.right += extraMargin;
            } else if (parent.getAdapter().getItemCount() % spanCount != 0
                    && parent.getLayoutManager().getPosition(view) >= (parent.getAdapter().getItemCount() - parent.getAdapter().getItemCount() % spanCount)) {
                rect.right += extraMargin;
            }
        }
        outRect.set(rect);
        // 通过设置一个空的drawable来隐藏默认分割线
        setDrawable(new ColorDrawable());
    }
}
