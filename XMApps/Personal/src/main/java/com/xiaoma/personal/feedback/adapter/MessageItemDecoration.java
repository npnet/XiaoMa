package com.xiaoma.personal.feedback.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Gillben
 * date: 2018/12/05
 * <p>
 * 消息列表分割控制
 */
public class MessageItemDecoration extends RecyclerView.ItemDecoration {

    private int dividerSize;

    public MessageItemDecoration(int dividerSize) {
        this.dividerSize = dividerSize;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.set(outRect.left, outRect.top + dividerSize, outRect.right, outRect.bottom);
        }
    }
}
