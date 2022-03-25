package com.qiming.fawcard.synthesize.base.widget;


import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;


public interface OnDriverScoreLineChartValueSelectedListener extends OnChartValueSelectedListener {

    /**
     * 选择两个数据
     *
     * @param e1 第一个高亮点的数据
     * @param e2 第二个高亮点的数据
     * @param h1 第一个高亮点的信息
     * @param h2 第二个高亮点的信息
     */
    void onValueSelected(Entry e1, Entry e2, Highlight h1, Highlight h2);
}
