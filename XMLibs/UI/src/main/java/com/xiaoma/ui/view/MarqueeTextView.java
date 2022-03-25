package com.xiaoma.ui.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * @author KY
 * @date 2018/10/12
 */
public class MarqueeTextView extends AppCompatTextView {

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 让 textview 随时以为自己是获取焦点状态
     * 自滚动需要拥有焦点,而同一个页面同一时间只能有一个控件拥有焦点,
     * 解决了原生的跑马灯经常会因为焦点的抢夺导致停止滚动的问题
     *
     * @return true
     */
    @Override
    public boolean isFocused() {
        return true;
    }

    public void stopMarquee() {
        if (getEllipsize() != TextUtils.TruncateAt.END) {
            setEllipsize(TextUtils.TruncateAt.END);
        }
    }

    public void startMarquee() {
        if (getEllipsize() != TextUtils.TruncateAt.MARQUEE) {
            setEllipsize(TextUtils.TruncateAt.MARQUEE);
        }
    }

    public void switchMarqueeState(boolean startMarqueeTF) {
        if (startMarqueeTF) {
            startMarquee();
        } else {
            stopMarquee();
        }
    }
}
