package com.qiming.fawcard.synthesize.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

public class DriverScoreLineChart extends LineChart {
    private final IMarker[] mMarker = new IMarker[2];
    private OnDriverScoreLineChartValueSelectedListener mDriverScoreLineChartSelectionListener;

    public DriverScoreLineChart(Context context) {
        super(context);
    }

    public DriverScoreLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DriverScoreLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置X轴对象
     *
     * @param xaXis X轴对象
     */
    public void setXAXis(XAxis xaXis) {
        mXAxis = xaXis;
    }

    @Override
    public void highlightValues(Highlight[] highs) {
        Entry e1 = null;
        Entry e2 = null;

        if (highs == null) {
            mIndicesToHighlight = null;
        } else {
            e1 = mData.getEntryForHighlight(highs[0]);
            e2 = mData.getEntryForHighlight(highs[1]);
            if (e1 == null) {
                mIndicesToHighlight = null;
                highs[0] = null;
            } else if (e2 == null) {
                mIndicesToHighlight = null;
                highs[1] = null;
            } else {
                // set the indices to highlight
                mIndicesToHighlight = new Highlight[]{
                        highs[0], highs[1]
                };
            }
        }

        setLastHighlighted(mIndicesToHighlight);

        if (!valuesToHighlight()) {
            mSelectionListener.onNothingSelected();
        } else {
            // notify the listener
            mDriverScoreLineChartSelectionListener.onValueSelected(e1, e2, mIndicesToHighlight[0], mIndicesToHighlight[1]);
        }

        // redraw the chart
        invalidate();
    }

    @Override
    public boolean valuesToHighlight() {
        if (mIndicesToHighlight == null || mIndicesToHighlight.length <= 0) {
            return false;
        }

        for (Highlight highlight : mIndicesToHighlight) {
            if (highlight == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * 设置MarkerView
     */
    public void setMarker(IMarker[] marker) {
        mMarker[0] = marker[0];
        mMarker[1] = marker[1];
    }

    /**
     * 设置点击的监听器
     *
     * @param driverScoreLineChartSelectionListener 监听器
     */
    public void setOnDriverScoreLineChartSelectionListener(OnDriverScoreLineChartValueSelectedListener driverScoreLineChartSelectionListener) {
        mDriverScoreLineChartSelectionListener = driverScoreLineChartSelectionListener;
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        if (!isDrawMarkersEnabled() || !valuesToHighlight()) {
            return;
        }

        for (Highlight highlight : mIndicesToHighlight) {

            DataSet set = (DataSet) mData.getDataSetByIndex(highlight.getDataSetIndex());

            Entry e = mData.getEntryForHighlight(highlight);
            int entryIndex = set.getEntryIndex(e);

            // make sure entry not null
            if (e == null || entryIndex > set.getEntryCount() * mAnimator.getPhaseX()) {
                continue;
            }

            float[] pos = getMarkerPosition(highlight);

            // check bounds
            if (!mViewPortHandler.isInBounds(pos[0], pos[1])) {
                continue;
            }

            if (highlight.getDataSetIndex() == 0) {
                // callbacks to update the content
                mMarker[0].refreshContent(e, highlight);

                // draw the marker
                mMarker[0].draw(canvas, pos[0], pos[1]);
            } else {
                // callbacks to update the content
                mMarker[1].refreshContent(e, highlight);

                // draw the marker
                mMarker[1].draw(canvas, pos[0], pos[1]);
            }
        }
    }
}

