package com.xiaoma.personal.coin.adapter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Gillben
 * date: 2018/12/07
 * <p>
 * 车币记录列表分割线
 */
public class CarCoinItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private @DrawableRes
    int mDrawable;


    public CarCoinItemDecoration(@DrawableRes int drawable) {
        mDrawable = drawable;
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        Drawable drawable = parent.getResources().getDrawable(mDrawable);
        drawable.draw(c);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(outRect.left, outRect.top, outRect.right, outRect.bottom + 10);
    }
}
