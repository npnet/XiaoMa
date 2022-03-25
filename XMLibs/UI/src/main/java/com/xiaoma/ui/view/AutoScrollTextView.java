package com.xiaoma.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewTreeObserver;

/**
 * Author: loren
 * Date: 2018/11/23 0023
 * 无论字体长度都可以跑马灯的textview
 */

public class AutoScrollTextView extends android.support.v7.widget.AppCompatTextView {

    private float textLength = 0f;//文本长度
    private float viewWidth = 0f;//textview的宽度
    private boolean isDefaultMarquee = true;//是否使用源生的跑马灯效果
    public boolean isStarting = false;//是否开始滚动
    private Paint paint = null;//绘图样式
    private String text = "";//文本内容
    private float initialX = 0f;//最初始时文字的x坐标(变化产生跑马灯效果)
    private float distance = 0f;//文字左移的距离，慢慢递增产生跑马灯效果
    private final long REFRESH_DELAY = 60;//跑马灯的刷新时间（ms）
    private final float INCREMENT = 1;//跑马灯每次增量
    private final float DEFAULT_MARGIN = 10;//滚动字体默认间距


    public AutoScrollTextView(Context context) {
        super(context);
    }

    public AutoScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

    /**
     * 避免popwindow弹出时,失去焦点导致跑马效果暂停
     *
     * @param hasWindowFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(true);
    }

    public void setText(String text) {
        try {
            if (getText() != null && text.equals(getText())) {
                return;
            }
            super.setText(text);
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        initPaint();
        getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            refreshData();
        }
    };

    private void initPaint() {
        paint = getPaint();
        text = getText().toString();
        paint.setColor(getCurrentTextColor());
        paint.setTextSize(getTextSize());
        textLength = paint.measureText(text);
    }

    private void refreshData() {
        viewWidth = getWidth();
//         isDefaultMarquee = textLength > viewWidth;
        if (!isDefaultMarquee) {
            initialX = (viewWidth - textLength) / 2;
        }
    }

    public void startMarquee() {
        if (!isStarting) {
            setEllipsize(TextUtils.TruncateAt.MARQUEE);
            distance = 0f;
            isStarting = true;
            refreshData();
            invalidate();
        }
    }


    public void stopMarquee() {
        setEllipsize(TextUtils.TruncateAt.END);
        isStarting = false;
        postInvalidate();
    }


    @Override
    public void onDraw(Canvas canvas) {
        if (isDefaultMarquee) {
            //文本宽度大于控件宽度，使用默认的marquee
            super.onDraw(canvas);
            setGravity(Gravity.CENTER);
            return;
        }
        if (isStarting) {
            if (paint == null) {
                initPaint();
            }
            setGravity(Gravity.CENTER_VERTICAL);
            if ((initialX - distance) <= 0) {
                //画出右边文字
                canvas.drawText(text, viewWidth - distance + DEFAULT_MARGIN, getBaseline(), paint);
                if ((int) distance >= ((int) ((viewWidth + textLength) / 2) + DEFAULT_MARGIN)) {
                    distance = 0;
                }
            }
            if (distance <= (initialX + textLength)) {
                //画出左边文字
                canvas.drawText(text, initialX - distance, getBaseline(), paint);
            }
            distance += INCREMENT;
            if (distance > 0) {
                postInvalidateDelayed(REFRESH_DELAY);
            }
        } else {
            if (getGravity() != Gravity.CENTER) {
                setGravity(Gravity.CENTER);
            }
            super.onDraw(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopMarquee();
        getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
    }
}
