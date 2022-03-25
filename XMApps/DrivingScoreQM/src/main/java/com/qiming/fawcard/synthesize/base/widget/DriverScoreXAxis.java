package com.qiming.fawcard.synthesize.base.widget;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.utils.Utils;

public class DriverScoreXAxis extends XAxis {
    @Override
    public void setTextSize(float size) {
        // 解除基类中对于字体大小的限制（基类中的限制：min = 6f, max = 24f）
        mTextSize = Utils.convertDpToPixel(size);
    }
}
