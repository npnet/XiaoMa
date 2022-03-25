package com.qiming.fawcard.synthesize.base.widget;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class DriverScoreLineChartRenderer extends LineChartRenderer {
    public DriverScoreLineChartRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    protected void drawCircles(Canvas c) {
        mRenderPaint.setStyle(Paint.Style.FILL);

        float phaseY = mAnimator.getPhaseY();

        float[] mCirclesBuffer = new float[2];
        mCirclesBuffer[0] = 0;
        mCirclesBuffer[1] = 0;

        List<ILineDataSet> dataSets = mChart.getLineData().getDataSets();

        for (int i = 0; i < dataSets.size(); i++) {
            IDriverScoreLineDataSet dataSet = (IDriverScoreLineDataSet) dataSets.get(i);
            if (!dataSet.isVisible() || !dataSet.isDrawCirclesEnabled() ||
                    dataSet.getEntryCount() == 0) {
                continue;
            }

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
            mXBounds.set(mChart, dataSet);
            Bitmap circleBitmap = dataSet.getCircleBitmap();

            int boundsRangeCount = mXBounds.range + mXBounds.min;

            for (int j = mXBounds.min; j <= boundsRangeCount; j++) {
                Entry e = dataSet.getEntryForIndex(j);
                if (e == null) {
                    break;
                }

                mCirclesBuffer[0] = e.getX();
                mCirclesBuffer[1] = e.getY() * phaseY;

                trans.pointValuesToPixel(mCirclesBuffer);

                if (!mViewPortHandler.isInBoundsRight(mCirclesBuffer[0])) {
                    break;
                }

                if (!mViewPortHandler.isInBoundsLeft(mCirclesBuffer[0]) ||
                        !mViewPortHandler.isInBoundsY(mCirclesBuffer[1])) {
                    continue;
                }

                if (circleBitmap != null) {
                    float left = mCirclesBuffer[0] - (float) circleBitmap.getWidth() / 2;
                    float top = mCirclesBuffer[1] - (float) circleBitmap.getHeight() / 2;
                    c.drawBitmap(circleBitmap, left, top, null);
                }
            }
        }
    }
}
