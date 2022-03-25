package com.xiaoma.xkan.common.base;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Thomas on 2019/7/17 0017
 */

public class RVSpacesItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = 100;
        outRect.right = 100;
    }

}