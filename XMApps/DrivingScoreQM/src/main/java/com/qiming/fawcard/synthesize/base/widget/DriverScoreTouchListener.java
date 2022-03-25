package com.qiming.fawcard.synthesize.base.widget;

import android.graphics.Matrix;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.MPPointD;

public class DriverScoreTouchListener extends BarLineChartTouchListener {

    /**
     * Constructor with initialization parameters.
     *
     * @param chart               instance of the chart
     * @param touchMatrix         the touch-matrix of the chart
     * @param dragTriggerDistance the minimum movement distance that will be interpreted as a "drag" gesture in dp (3dp equals
     */
    public DriverScoreTouchListener(BarLineChartBase<? extends BarLineScatterCandleBubbleData<? extends IBarLineScatterCandleBubbleDataSet<? extends Entry>>> chart, Matrix touchMatrix, float dragTriggerDistance) {
        super(chart, touchMatrix, dragTriggerDistance);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        mLastGesture = ChartGesture.SINGLE_TAP;

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartSingleTapped(e);
        }

        if (!mChart.isHighlightPerTapEnabled()) {
            return false;
        }

        Highlight h1 = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
        Highlight h2 = null;

        int index = 0;
        if (h1 != null && h1.getDataSetIndex() == 0) {
            index = 1;
        }

        LineDataSet dataSet = (LineDataSet) mChart.getData().getDataSetByIndex(index);
        for (int i = 0; i < dataSet.getEntryCount(); i++) {
            if (h1 != null && dataSet.getEntryForIndex(i).getX() == h1.getX()) {
                MPPointD pixels = mChart.getTransformer(
                        dataSet.getAxisDependency()).getPixelForValues(h1.getX(), dataSet.getEntryForIndex(i).getY());

                h2 = new Highlight(
                        h1.getX(), dataSet.getEntryForIndex(i).getY(),
                        (float) pixels.x, (float) pixels.y,
                        index, dataSet.getAxisDependency());
            }
        }

        Highlight[] highlights = {h1, h2};
        if (highlights[0] == null || highlights[1] == null
                || highlights[0].equalTo(mLastHighlighted) || highlights[1].equalTo(mLastHighlighted)) {
            mChart.highlightValue(null, true);
            mLastHighlighted = null;
        } else {
            mChart.highlightValues(highlights);
        }

        return false;
    }
}
