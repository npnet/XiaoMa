package com.xiaoma.pet.adapter;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Gillben on 2018/12/25 0025
 * <p>
 * desc:
 */
public class RVGridItemDecoration extends RecyclerView.ItemDecoration {


    private int dividerSize;

    public RVGridItemDecoration(int dividerSize) {
        this.dividerSize = dividerSize;
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, dividerSize, dividerSize, 0);
    }
}
