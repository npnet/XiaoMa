package com.qiming.fawcard.synthesize.base.widget;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class DriverScoreLineChartXAxisRenderer extends XAxisRenderer {

    private float mLabelMarginHeight;

    public DriverScoreLineChartXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    @Override
    protected void computeSize() {

        String longest = mXAxis.getLongestLabel();

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());

        final FSize labelSize = Utils.calcTextSize(mAxisLabelPaint, longest);

        final float labelWidth = labelSize.width;

        // 解决不显示legend时，X轴Label文字底部显示不全的问题，
        // 在文字高度的基础上预留一定的高度，描画Label文字时边框不会紧贴文字，而是留有空白
        final float labelHeight = mLabelMarginHeight + Utils.calcTextHeight(mAxisLabelPaint, "Q");

        final FSize labelRotatedSize = Utils.getSizeOfRotatedRectangleByDegrees(
                labelWidth,
                labelHeight,
                mXAxis.getLabelRotationAngle());

        mXAxis.mLabelWidth = Math.round(labelWidth);
        mXAxis.mLabelHeight = Math.round(labelHeight);
        mXAxis.mLabelRotatedWidth = Math.round(labelRotatedSize.width);
        mXAxis.mLabelRotatedHeight = Math.round(labelRotatedSize.height);

        FSize.recycleInstance(labelRotatedSize);
        FSize.recycleInstance(labelSize);
    }

    /**
     * 设置X轴Label文字距下边界的距离
     *
     * @param labelMarginHeight X轴Label文字距下边界的距离
     */
    public void setLabelMarginHeight(float labelMarginHeight) {
        mLabelMarginHeight = labelMarginHeight;
    }

    /**
     * 计算X轴显示的标签值.
     * <p>
     * 删除了基类中Normalize interval的处理。<br>
     * 修改了标签值的计算规则。<br>
     * <ul>
     * <li>若强制规定标签个数，则以标签个数为准计算。
     * <li>若未强制规定标签个数，但设置了粒度，则以设置的粒度为准计算。
     * <li>若未强制规定标签个数，也没有设置粒度，则按默认标签个数（6）计算。
     * </ul>
     *
     * @param min X轴最小值
     * @param max X轴最大值
     */
    @Override
    protected void computeAxisValues(float min, float max) {

        int labelCount = mAxis.getLabelCount();
        double range = Math.abs(max - min);

        if (labelCount == 0 || range <= 0 || Double.isInfinite(range)) {
            mAxis.mEntries = new float[]{};
            mAxis.mCenteredEntries = new float[]{};
            mAxis.mEntryCount = 0;
            return;
        }

        double interval = mAxis.getGranularity();

        int n = mAxis.isCenterAxisLabelsEnabled() ? 1 : 0;

        // force label count
        if (mAxis.isForceLabelsEnabled()) {

            interval = (float) range / (float) (labelCount - 1);
            mAxis.mEntryCount = labelCount;

            if (mAxis.mEntries.length < labelCount) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = new float[labelCount];
            }

            float v = min;

            for (int i = 0; i < labelCount; i++) {
                mAxis.mEntries[i] = v;
                v += interval;
            }

            n = labelCount;

            // no forced count
        } else {

            if (!mAxis.isGranularityEnabled()) {
                // Find out how much spacing (in y value space) between axis values
                double rawInterval = range / labelCount;
                interval = Utils.roundToNextSignificant(rawInterval);
            }

            double first = interval == 0.0 ? 0.0 : Math.ceil(min / interval) * interval;
            if (mAxis.isCenterAxisLabelsEnabled()) {
                first -= interval;
            }

            double last = interval == 0.0 ? 0.0 : Utils.nextUp(Math.floor(max / interval) * interval);

            double f;
            int i;

            if (interval != 0.0) {
                for (f = first; f <= last; f += interval) {
                    ++n;
                }
            }

            mAxis.mEntryCount = n;

            if (mAxis.mEntries.length < n) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = new float[n];
            }

            for (f = first, i = 0; i < n; f += interval, ++i) {

                // Fix for negative zero case (Where value == -0.0, and 0.0 == -0.0)
                if (f == 0.0) {
                    f = 0.0;
                }

                mAxis.mEntries[i] = (float) f;
            }
        }

        // set decimals
        if (interval < 1) {
            mAxis.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            mAxis.mDecimals = 0;
        }

        if (mAxis.isCenterAxisLabelsEnabled()) {

            if (mAxis.mCenteredEntries.length < n) {
                mAxis.mCenteredEntries = new float[n];
            }

            float offset = (float) interval / 2f;

            for (int i = 0; i < n; i++) {
                mAxis.mCenteredEntries[i] = mAxis.mEntries[i] + offset;
            }
        }

        computeSize();
    }
}
