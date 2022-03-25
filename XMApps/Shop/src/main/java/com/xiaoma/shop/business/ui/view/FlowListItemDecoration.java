package com.xiaoma.shop.business.ui.view;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/04/09
 * @Describe: 流量列表 ItemDecoration
 */

public class FlowListItemDecoration extends RecyclerView.ItemDecoration {

    private int mLeftMargin = 50, mTopMargin = 18;

    public FlowListItemDecoration() {
    }

    public FlowListItemDecoration(int leftMargin, int topMargin) {
        mLeftMargin = leftMargin;
        mTopMargin = topMargin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State
            state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            int pos = parent.getChildViewHolder(view).getLayoutPosition();
            int left=0, top=0, right=0, bottom=0;
            Log.e("TAG", "pos % spanCount --> "+pos % spanCount+ " pos -->"+pos);
            if (pos == 0 || pos % spanCount == 0) {
                left=0;
            }else{
//                left=mLeftMargin;
            }

            if(pos>=spanCount){
                top=mTopMargin;
            }
            outRect.set(left,top,right,bottom);
        }
    }
}
