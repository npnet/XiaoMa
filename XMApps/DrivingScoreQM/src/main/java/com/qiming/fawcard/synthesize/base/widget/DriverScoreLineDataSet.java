package com.qiming.fawcard.synthesize.base.widget;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

public class DriverScoreLineDataSet extends LineDataSet implements IDriverScoreLineDataSet{
    @Nullable
    private Bitmap mCircleBitmap;

    public DriverScoreLineDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public Bitmap getCircleBitmap() {
        return mCircleBitmap;
    }

    public void setCircleBitmap(Bitmap circleBitmap) {
        mCircleBitmap = circleBitmap;
    }
}
