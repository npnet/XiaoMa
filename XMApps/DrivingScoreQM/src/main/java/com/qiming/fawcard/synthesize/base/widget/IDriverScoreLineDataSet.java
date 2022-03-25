package com.qiming.fawcard.synthesize.base.widget;

import android.graphics.Bitmap;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public interface IDriverScoreLineDataSet extends ILineDataSet {
    /**
     * 取得Circle的Bitmap
     *
     * @return Bitmap对象
     */
    Bitmap getCircleBitmap();
}
