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
     * 构造函数
     *
     * @param context   上下文
     * @param lineChart LineChart对象
     */
    @Inject
    public DriveScoreLineChartManager(Context context, DriverScoreLineChart lineChart) {
        mLineChart = lineChart;
        mContext = context;
    }

    /**
     * 初始化折线图
     */
    public void initLineChart() {
        // 清空折线图数据
        clearLineChart();
        // 初始化内部数据
        initData();
        // 设置折线图属性
        setLineChart();
        // MarkerView
        setMarkerView();
        // no legend
        setLegend();
        // Y轴设置
        setYAxis();
        // X轴设置
        setXAxis();
        // 初始化折线图数据
        resetLineChartData();
    }

    /**
     * 追加折线图数据
     *
     * @param avgSpeed        平均时速
     * @param avgFuelConsumer 平均油耗
     * @param time            当前时间
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
            // 为防止X轴标签被屏幕遮挡，留有一定距离
            mLineChart.getXAxis().setAxisMaximum((float) (mXOrigin + MAX_SHOW_DRIVER_INFO_COUNT + 2 * XAXIS_MARGIN_TIME));
            needMove = true;
        }

        // 设置显示数据比例尺
        mLineChart.setVisibleXRange((float) TIME_MINUTE_30, (float) (MAX_SHOW_DRIVER_INFO_COUNT + 2 * XAXIS_MARGIN_TIME));

        mLineChart.getData().addEntry(new Entry((float) xValue, (float) avgSpeed), AVG_SPEED_SET_INDEX);
        mLineChart.getData().addEntry(new Entry((float) xValue, (float) avgFuelConsumer), AVG_FUEL_CONSUMER_SET_INDEX);

        // 显示最新的数据
        mLineChart.notifyDataSetChanged();
        if (needMove) {
            mLineChart.moveViewToX((float) (mXOrigin - XAXIS_MARGIN_TIME));
        } else {
            mLineChart.postInvalidate();
        }
    }

    /**
     * 初始化折线图数据
     */
    private void resetLineChartData() {
        // 设置初始数据
        mLineChart.setData(new LineData());

        // 设置初始数据线
        mLineChart.getData().addDataSet(createAvgSpeedSet());
        mLineChart.getData().addDataSet(createAvgFuelConsumerSet());

        // 回到原点
        mLineChart.moveViewToX((float) (mXOrigin - XAXIS_MARGIN_TIME));
    }

    /**
     * 清空折线图数据
     */
    private void clearLineChart() {
        // 清空数据
        if (mLineChart.getData() != null) {
            mLineChart.clearValues();
        }

        // 清空状态
        mLineChart.clear();
    }

    /**
     * 初始化内部数据
     */
    private void initData() {
        // 恢复初始值
        mXOrigin = 0L;
        mStartTime = 0L;
        mFirstAdd = true;

        // 设置初始时间
        mStartTime = regulateTime(System.currentTimeMillis());
    }

    /**
     * 设置折线图属性
     */
    private void setLineChart() {
        // No chart data Text
        mLineChart.setNoDataText("");

        // no description text
        mLineChart.getDescription().setEnabled(false);

        //是否有触摸事件
        mLineChart.setTouchEnabled(true);

        // 是否可缩放
        mLineChart.setScaleEnabled(false);

        // 是否可通过双击缩放图表
        mLineChart.setDoubleTapToZoomEnabled(false);

        // 是否可以拖动
        mLineChart.setDragEnabled(true);

        // 是否展示网格线
        mLineChart.setDrawGridBackground(false);

        // 背景色
        mLineChart.setBackgroundColor(Color.TRANSPARENT);

        // 定位线
        mLineChart.setHighlightPerDragEnabled(true);

        // 拖动减速
        mLineChart.setDragDecelerationEnabled(false);

        // 折现图手指点击的最大有效范围
        mLineChart.setMaxHighlightDistance(MAX_HIGHLIGHT_DISTANCE);

        // 动画时间
        mLineChart.animateX(0);

        // 设置X轴
        mLineChart.setXAXis(new DriverScoreXAxis());

        // 设置X轴Label文字距下边界的距离
        DriverScoreLineChartXAxisRenderer driverScoreLineChartXAxisRenderer = new DriverScoreLineChartXAxisRenderer(mLineChart.getViewPortHandler(), mLineChart.getXAxis(), mLineChart.getTransformer(YAxis.AxisDependency.LEFT));
        driverScoreLineChartXAxisRenderer.setLabelMarginHeight(LABEL_MARGIN_HEIGHT);

        // 设置X轴的Renderer
        mLineChart.setXAxisRenderer(driverScoreLineChartXAxisRenderer);

        // 设置Renderer
        mLineChart.setRenderer(new DriverScoreLineChartRenderer(mLineChart, mLineChart.getAnimator(), mLineChart.getViewPortHandler()));

        // 设置点击的监听事件
        mLineChart.setOnTouchListener(new DriverScoreTouchListener(mLineChart, mLineChart.getViewPortHandler().getMatrixTouch(), DRAG_TRIGGER_DISTANCE));
    }

    /**
     * Y轴设置
     */
    private void setYAxis() {
        // 左侧Y轴设置
        YAxis leftAxis = mLineChart.getAxisLeft();
        // 网格线
        leftAxis.setDrawGridLines(false);
        // 轴线
        leftAxis.setDrawAxisLine(true);
        // 是否显示Y轴坐标值
        leftAxis.setDrawLabels(false);
        // 最小值
        leftAxis.setAxisMinimum((float) MIN_AVG_SPEED);
        // 最大值
        leftAxis.setAxisMaximum((float) MAX_AVG_SPEED);
        // Y轴宽度
        leftAxis.setAxisLineWidth(LINE_WIDTH);
        // Y轴颜色
        leftAxis.setAxisLineColor(ContextCompat.getColor(mContext, R.color.custom_white));

        // 右侧Y轴设置
        YAxis rightAxis = mLineChart.getAxisRight();
        // 最小值
        rightAxis.setAxisMinimum((float) MIN_AVG_FUEL_CONSUMER);
        // 最大值
        rightAxis.setAxisMaximum((float) MAX_AVG_FUEL_CONSUMER);
        // 不显示
        rightAxis.setEnabled(false);
    }

    /**
     * X轴设置
     */
    private void setXAxis() {
        XAxis xAxis = mLineChart.getXAxis();
        // 下方显示
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 字体大小
        xAxis.setTextSize(XAXIS_TEXT_SIZE);
        // 字体颜色
        xAxis.setTextColor(ContextCompat.getColor(mContext, R.color.custom_white));
        // X轴宽度
        xAxis.setAxisLineWidth(LINE_WIDTH);
        // X轴颜色
        xAxis.setAxisLineColor(ContextCompat.getColor(mContext, R.color.custom_white));
        // 网格线
        xAxis.setDrawGridLines(false);
        // 轴线
        xAxis.setDrawAxisLine(true);
        // 设置是否绘制X轴上的对应值(标签)
        xAxis.setDrawLabels(true);
        // 设置X轴标签不完全显示（可被边界遮盖）
        xAxis.setAvoidFirstLastClipping(false);
        // 设置最小值，为防止X轴标签被屏幕遮挡，留有一定距离
        xAxis.setAxisMinimum(0f - XAXIS_MARGIN_TIME);
        // 设置最大值，为防止X轴标签被屏幕遮挡，留有一定距离
        xAxis.setAxisMaximum((float) (mXOrigin + MAX_SHOW_DRIVER_INFO_COUNT + 2 * XAXIS_MARGIN_TIME));
        // 设置X轴标签间隔
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity((float) TIME_MINUTE_30);
        // 设置偏移量
        xAxis.setYOffset(XAXIS_Y_OFFSET);

        // 自定义X轴标签格式
        xAxis.setValueFormatter(new ValueFormatter() {
            // 设置日期格式
            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                long time = mStartTime + ConvertUtil.minuteToMillisecond((long) value);
                return mFormat.format(time);
            }
        });
    }

    /**
     * 平均油耗校正
     *
     * @param avgFuelConsumer 平均油耗
     * @return 校正后的平均油耗
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
     * 平均时速校正
     *
     * @param avgSpeed 平均时速
     * @return 校正后的平均时速
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
     * 判断折线图的有效性
     *
     * @return true:有效 false:无效
     */
    private boolean checkLineChartValidity() {
        return mLineChart != null && mLineChart.getData() != null
                && mLineChart.getData().getDataSetCount() == LINE_CHAR_DATA_SET_COUNT;
    }

    /**
     * 校准时间
     *
     * @param time 时间
     */
    private long regulateTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int minute = calendar.get(Calendar.MINUTE);

        // 坐标轴起始时间取最近的整点（半小时单位）
        if (minute > 0 && minute < TIME_MINUTE_30) {
            time = time - ConvertUtil.minuteToMillisecond(minute);
        } else if (minute > TIME_MINUTE_30 && minute <= TIME_MINUTE_59) {
            time = time + ConvertUtil.minuteToMillisecond(TIME_MINUTE_30 - minute);
        }

        return time;
    }

    /**
     * 示例图设置
     */
    private void setLegend() {
        Legend legend = mLineChart.getLegend();
        legend.setEnabled(false);
    }

    /**
     * MarkerView设置
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
     * 平均车速数据线设置
     *
     * @return 平均车速LineDataSet
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
     * 平均油耗数据线设置
     *
     * @return 平均油耗LineDataSet
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
