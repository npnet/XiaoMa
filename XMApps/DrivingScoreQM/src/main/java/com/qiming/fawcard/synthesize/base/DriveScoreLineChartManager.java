package com.qiming.fawcard.synthesize.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.base.util.ConvertUtil;
import com.qiming.fawcard.synthesize.base.widget.DriverScoreLineChart;
import com.qiming.fawcard.synthesize.base.widget.DriverScoreLineChartRenderer;
import com.qiming.fawcard.synthesize.base.widget.DriverScoreLineChartXAxisRenderer;
import com.qiming.fawcard.synthesize.base.widget.DriverScoreLineDataSet;
import com.qiming.fawcard.synthesize.base.widget.DriverScoreMarkerView;
import com.qiming.fawcard.synthesize.base.widget.DriverScoreTouchListener;
import com.qiming.fawcard.synthesize.base.widget.DriverScoreXAxis;
import com.qiming.fawcard.synthesize.base.widget.OnDriverScoreLineChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import static com.qiming.fawcard.synthesize.base.constant.QMConstant.AVG_FUEL_CONSUMER_SET_INDEX;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.AVG_SPEED_SET_INDEX;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.DASHED_HIGHLIGHT_LINE_LENGTH;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.DASHED_HIGHLIGHT_PHASE;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.DASHED_HIGHLIGHT_SPACE_LENGTH;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.DRAG_TRIGGER_DISTANCE;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.LABEL_MARGIN_HEIGHT;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.LINE_CHAR_DATA_SET_COUNT;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.LINE_WIDTH;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.MAX_AVG_FUEL_CONSUMER;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.MAX_AVG_SPEED;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.MAX_HIGHLIGHT_DISTANCE;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.MAX_SHOW_DRIVER_INFO_COUNT;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.MIN_AVG_FUEL_CONSUMER;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.MIN_AVG_SPEED;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.TIME_MINUTE_30;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.TIME_MINUTE_59;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.XAXIS_MARGIN_TIME;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.XAXIS_TEXT_SIZE;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.XAXIS_Y_OFFSET;

public class DriveScoreLineChartManager {
    private final Context mContext;
    private final DriverScoreLineChart mLineChart;
    private final MarkerView[] mMarkerView = new MarkerView[2];
    private long mXOrigin = 0L;
    private long mStartTime = 0L;
    private boolean mFirstAdd = true;

    /**
     * ????????????
     *
     * @param context   ?????????
     * @param lineChart LineChart??????
     */
    @Inject
    public DriveScoreLineChartManager(Context context, DriverScoreLineChart lineChart) {
        mLineChart = lineChart;
        mContext = context;
    }

    /**
     * ??????????????????
     */
    public void initLineChart() {
        // ?????????????????????
        clearLineChart();
        // ?????????????????????
        initData();
        // ?????????????????????
        setLineChart();
        // MarkerView
        setMarkerView();
        // no legend
        setLegend();
        // Y?????????
        setYAxis();
        // X?????????
        setXAxis();
        // ????????????????????????
        resetLineChartData();
    }

    /**
     * ?????????????????????
     *
     * @param avgSpeed        ????????????
     * @param avgFuelConsumer ????????????
     * @param time            ????????????
     */
    public void addEntry(double avgSpeed, double avgFuelConsumer, long time) {
        if (!checkLineChartValidity()) {
            return;
        }

        if (mFirstAdd) {
            mStartTime = regulateTime(time);
            mFirstAdd = false;
        }

        avgSpeed = regulateAvgSpeed(avgSpeed);
        avgFuelConsumer = regulateAvgFuelConsumer(avgFuelConsumer);
        long xValue = ConvertUtil.millisecondToMinute(time - mStartTime);

        boolean needMove = false;
        if (xValue > mXOrigin + MAX_SHOW_DRIVER_INFO_COUNT) {
            mXOrigin = mXOrigin + TIME_MINUTE_30;
            // ?????????X?????????????????????????????????????????????
            mLineChart.getXAxis().setAxisMaximum((float) (mXOrigin + MAX_SHOW_DRIVER_INFO_COUNT + 2 * XAXIS_MARGIN_TIME));
            needMove = true;
        }

        // ???????????????????????????
        mLineChart.setVisibleXRange((float) TIME_MINUTE_30, (float) (MAX_SHOW_DRIVER_INFO_COUNT + 2 * XAXIS_MARGIN_TIME));

        mLineChart.getData().addEntry(new Entry((float) xValue, (float) avgSpeed), AVG_SPEED_SET_INDEX);
        mLineChart.getData().addEntry(new Entry((float) xValue, (float) avgFuelConsumer), AVG_FUEL_CONSUMER_SET_INDEX);

        // ?????????????????????
        mLineChart.notifyDataSetChanged();
        if (needMove) {
            mLineChart.moveViewToX((float) (mXOrigin - XAXIS_MARGIN_TIME));
        } else {
            mLineChart.postInvalidate();
        }
    }

    /**
     * ????????????????????????
     */
    private void resetLineChartData() {
        // ??????????????????
        mLineChart.setData(new LineData());

        // ?????????????????????
        mLineChart.getData().addDataSet(createAvgSpeedSet());
        mLineChart.getData().addDataSet(createAvgFuelConsumerSet());

        // ????????????
        mLineChart.moveViewToX((float) (mXOrigin - XAXIS_MARGIN_TIME));
    }

    /**
     * ?????????????????????
     */
    private void clearLineChart() {
        // ????????????
        if (mLineChart.getData() != null) {
            mLineChart.clearValues();
        }

        // ????????????
        mLineChart.clear();
    }

    /**
     * ?????????????????????
     */
    private void initData() {
        // ???????????????
        mXOrigin = 0L;
        mStartTime = 0L;
        mFirstAdd = true;

        // ??????????????????
        mStartTime = regulateTime(System.currentTimeMillis());
    }

    /**
     * ?????????????????????
     */
    private void setLineChart() {
        // No chart data Text
        mLineChart.setNoDataText("");

        // no description text
        mLineChart.getDescription().setEnabled(false);

        //?????????????????????
        mLineChart.setTouchEnabled(true);

        // ???????????????
        mLineChart.setScaleEnabled(false);

        // ?????????????????????????????????
        mLineChart.setDoubleTapToZoomEnabled(false);

        // ??????????????????
        mLineChart.setDragEnabled(true);

        // ?????????????????????
        mLineChart.setDrawGridBackground(false);

        // ?????????
        mLineChart.setBackgroundColor(Color.TRANSPARENT);

        // ?????????
        mLineChart.setHighlightPerDragEnabled(true);

        // ????????????
        mLineChart.setDragDecelerationEnabled(false);

        // ??????????????????????????????????????????
        mLineChart.setMaxHighlightDistance(MAX_HIGHLIGHT_DISTANCE);

        // ????????????
        mLineChart.animateX(0);

        // ??????X???
        mLineChart.setXAXis(new DriverScoreXAxis());

        // ??????X???Label???????????????????????????
        DriverScoreLineChartXAxisRenderer driverScoreLineChartXAxisRenderer = new DriverScoreLineChartXAxisRenderer(mLineChart.getViewPortHandler(), mLineChart.getXAxis(), mLineChart.getTransformer(YAxis.AxisDependency.LEFT));
        driverScoreLineChartXAxisRenderer.setLabelMarginHeight(LABEL_MARGIN_HEIGHT);

        // ??????X??????Renderer
        mLineChart.setXAxisRenderer(driverScoreLineChartXAxisRenderer);

        // ??????Renderer
        mLineChart.setRenderer(new DriverScoreLineChartRenderer(mLineChart, mLineChart.getAnimator(), mLineChart.getViewPortHandler()));

        // ???????????????????????????
        mLineChart.setOnTouchListener(new DriverScoreTouchListener(mLineChart, mLineChart.getViewPortHandler().getMatrixTouch(), DRAG_TRIGGER_DISTANCE));
    }

    /**
     * Y?????????
     */
    private void setYAxis() {
        // ??????Y?????????
        YAxis leftAxis = mLineChart.getAxisLeft();
        // ?????????
        leftAxis.setDrawGridLines(false);
        // ??????
        leftAxis.setDrawAxisLine(true);
        // ????????????Y????????????
        leftAxis.setDrawLabels(false);
        // ?????????
        leftAxis.setAxisMinimum((float) MIN_AVG_SPEED);
        // ?????????
        leftAxis.setAxisMaximum((float) MAX_AVG_SPEED);
        // Y?????????
        leftAxis.setAxisLineWidth(LINE_WIDTH);
        // Y?????????
        leftAxis.setAxisLineColor(ContextCompat.getColor(mContext, R.color.custom_white));

        // ??????Y?????????
        YAxis rightAxis = mLineChart.getAxisRight();
        // ?????????
        rightAxis.setAxisMinimum((float) MIN_AVG_FUEL_CONSUMER);
        // ?????????
        rightAxis.setAxisMaximum((float) MAX_AVG_FUEL_CONSUMER);
        // ?????????
        rightAxis.setEnabled(false);
    }

    /**
     * X?????????
     */
    private void setXAxis() {
        XAxis xAxis = mLineChart.getXAxis();
        // ????????????
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // ????????????
        xAxis.setTextSize(XAXIS_TEXT_SIZE);
        // ????????????
        xAxis.setTextColor(ContextCompat.getColor(mContext, R.color.custom_white));
        // X?????????
        xAxis.setAxisLineWidth(LINE_WIDTH);
        // X?????????
        xAxis.setAxisLineColor(ContextCompat.getColor(mContext, R.color.custom_white));
        // ?????????
        xAxis.setDrawGridLines(false);
        // ??????
        xAxis.setDrawAxisLine(true);
        // ??????????????????X??????????????????(??????)
        xAxis.setDrawLabels(true);
        // ??????X????????????????????????????????????????????????
        xAxis.setAvoidFirstLastClipping(false);
        // ???????????????????????????X?????????????????????????????????????????????
        xAxis.setAxisMinimum(0f - XAXIS_MARGIN_TIME);
        // ???????????????????????????X?????????????????????????????????????????????
        xAxis.setAxisMaximum((float) (mXOrigin + MAX_SHOW_DRIVER_INFO_COUNT + 2 * XAXIS_MARGIN_TIME));
        // ??????X???????????????
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity((float) TIME_MINUTE_30);
        // ???????????????
        xAxis.setYOffset(XAXIS_Y_OFFSET);

        // ?????????X???????????????
        xAxis.setValueFormatter(new ValueFormatter() {
            // ??????????????????
            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                long time = mStartTime + ConvertUtil.minuteToMillisecond((long) value);
                return mFormat.format(time);
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param avgFuelConsumer ????????????
     * @return ????????????????????????
     */
    private double regulateAvgFuelConsumer(double avgFuelConsumer) {
        if (avgFuelConsumer >  MAX_AVG_FUEL_CONSUMER) {
            avgFuelConsumer =  MAX_AVG_FUEL_CONSUMER;
        }
        if (avgFuelConsumer <  MIN_AVG_FUEL_CONSUMER) {
            avgFuelConsumer =  MIN_AVG_FUEL_CONSUMER;
        }
        return avgFuelConsumer;
    }

    /**
     * ??????????????????
     *
     * @param avgSpeed ????????????
     * @return ????????????????????????
     */
    private Double regulateAvgSpeed(double avgSpeed) {
        if (avgSpeed > MAX_AVG_SPEED) {
            avgSpeed = MAX_AVG_SPEED;
        }
        if (avgSpeed < MIN_AVG_SPEED) {
            avgSpeed = MIN_AVG_SPEED;
        }
        return avgSpeed;
    }

    /**
     * ???????????????????????????
     *
     * @return true:?????? false:??????
     */
    private boolean checkLineChartValidity() {
        return mLineChart != null && mLineChart.getData() != null
                && mLineChart.getData().getDataSetCount() == LINE_CHAR_DATA_SET_COUNT;
    }

    /**
     * ????????????
     *
     * @param time ??????
     */
    private long regulateTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int minute = calendar.get(Calendar.MINUTE);

        // ????????????????????????????????????????????????????????????
        if (minute > 0 && minute < TIME_MINUTE_30) {
            time = time - ConvertUtil.minuteToMillisecond(minute);
        } else if (minute > TIME_MINUTE_30 && minute <= TIME_MINUTE_59) {
            time = time + ConvertUtil.minuteToMillisecond(TIME_MINUTE_30 - minute);
        }

        return time;
    }

    /**
     * ???????????????
     */
    private void setLegend() {
        Legend legend = mLineChart.getLegend();
        legend.setEnabled(false);
    }

    /**
     * MarkerView??????
     */
    private void setMarkerView() {
        mMarkerView[AVG_SPEED_SET_INDEX] = new DriverScoreMarkerView(mContext, R.layout.custom_marker_view);
        mMarkerView[AVG_FUEL_CONSUMER_SET_INDEX] = new DriverScoreMarkerView(mContext, R.layout.custom_marker_view);

        mMarkerView[AVG_SPEED_SET_INDEX].setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorLineYellow));
        mMarkerView[AVG_FUEL_CONSUMER_SET_INDEX].setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorLineBlue));

        mMarkerView[AVG_SPEED_SET_INDEX].setChartView(mLineChart);
        mMarkerView[AVG_FUEL_CONSUMER_SET_INDEX].setChartView(mLineChart);

        mLineChart.setMarker(mMarkerView);
        mLineChart.setOnDriverScoreLineChartSelectionListener(new OnDriverScoreLineChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }

            @Override
            public void onValueSelected(Entry e1, Entry e2, Highlight h1, Highlight h2) {
                if (h1.getYPx() > h2.getYPx()) {
                    mMarkerView[h1.getDataSetIndex()].setOffset(new MPPointF(-(float) mMarkerView[h1.getDataSetIndex()].getWidth() / 2, (float) mMarkerView[h1.getDataSetIndex()].getHeight() / 2));
                    mMarkerView[h2.getDataSetIndex()].setOffset(new MPPointF(-(float) mMarkerView[h2.getDataSetIndex()].getWidth() / 2, -(float) 1.5 * mMarkerView[h2.getDataSetIndex()].getHeight()));
                } else {
                    mMarkerView[h2.getDataSetIndex()].setOffset(new MPPointF(-(float) mMarkerView[h2.getDataSetIndex()].getWidth() / 2, (float) mMarkerView[h2.getDataSetIndex()].getHeight() / 2));
                    mMarkerView[h1.getDataSetIndex()].setOffset(new MPPointF(-(float) mMarkerView[h1.getDataSetIndex()].getWidth() / 2, -(float) 1.5 * mMarkerView[h1.getDataSetIndex()].getHeight()));
                }
            }
        });
    }

    /**
     * ???????????????????????????
     *
     * @return ????????????LineDataSet
     */
    private LineDataSet createAvgSpeedSet() {
        DriverScoreLineDataSet set = new DriverScoreLineDataSet(null, "avgSpeed");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.enableDashedHighlightLine(DASHED_HIGHLIGHT_LINE_LENGTH, DASHED_HIGHLIGHT_SPACE_LENGTH, DASHED_HIGHLIGHT_PHASE);
        set.setColor(ContextCompat.getColor(mContext, R.color.colorLineYellow));
        set.setLineWidth(LINE_WIDTH);
        set.setDrawCircleHole(false);
        set.setDrawValues(false);
        set.setDrawHorizontalHighlightIndicator(false);
        set.setDrawVerticalHighlightIndicator(true);
        set.setHighlightLineWidth(LINE_WIDTH);
        set.setHighLightColor(ContextCompat.getColor(mContext, R.color.custom_white));
        Bitmap circleBitmap = ConvertUtil.drawableToBitmap(Objects.requireNonNull(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.yellow_point, null)));
        set.setCircleBitmap(circleBitmap);

        return set;
    }

    /**
     * ???????????????????????????
     *
     * @return ????????????LineDataSet
     */
    private LineDataSet createAvgFuelConsumerSet() {
        DriverScoreLineDataSet set = new DriverScoreLineDataSet(null, "avgFuelConsumer");
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set.enableDashedHighlightLine(DASHED_HIGHLIGHT_LINE_LENGTH, DASHED_HIGHLIGHT_SPACE_LENGTH, DASHED_HIGHLIGHT_PHASE);
        set.setColor(ContextCompat.getColor(mContext, R.color.colorLineBlue));
        set.setLineWidth(LINE_WIDTH);
        set.setDrawCircleHole(false);
        set.setDrawValues(false);
        set.setDrawHorizontalHighlightIndicator(false);
        set.setDrawVerticalHighlightIndicator(true);
        set.setHighlightLineWidth(LINE_WIDTH);
        set.setHighLightColor(ContextCompat.getColor(mContext, R.color.custom_white));
        Bitmap circleBitmap = ConvertUtil.drawableToBitmap(Objects.requireNonNull(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.blue_point, null)));
        set.setCircleBitmap(circleBitmap);

        return set;
    }
}
