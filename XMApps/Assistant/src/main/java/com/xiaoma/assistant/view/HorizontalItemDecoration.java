package com.xiaoma.assistant.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by qiuboxiang on 2019/3/6 19:54
 * Desc:
 */
public class HorizontalItemDecoration extends RecyclerView.ItemDecoration {

    private static final int margin = 42;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = 0;
            outRect.right = margin;
        } else {
            outRect.left = margin;
            outRect.right = margin;
        }
    }
}
