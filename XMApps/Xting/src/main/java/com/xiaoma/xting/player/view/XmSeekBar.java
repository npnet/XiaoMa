package com.xiaoma.xting.player.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.views.XmSkinView;
import com.xiaoma.xting.R;

/**
 * @author KY
 * @date 2018/10/29
 */
public class XmSeekBar extends XmSkinView {
    private static final int DEFAULT_TOTAL_WIDTH = 300;
    private static final int DEFAULT_BOTTOM_PADDING = 20;
    private static final int DEFAULT_TOP_PADDING = 20;
    private static final int DEFAULT_TOTAL_HEIGHT = 23;
    private static final int DEFAULT_LEFT_RIGHT_WIDTH = 12;
    private static final int DEFAULT_BLOCK_WIDTH = 8;
    private static final int DEFAULT_MAX = 100;
    private static final float DEFAULT_PERCENT = 0.6f;
    private int mWidth = DEFAULT_TOTAL_WIDTH;
    private float percent = DEFAULT_PERCENT;
    private int mMax = DEFAULT_MAX;
    private Drawable leftFill;
    private Drawable rightUnfill;
    private Drawable middleFill;
    private Drawable middleUnfill;
    private Drawable shadow;
    private Drawable block;
    private float oldX;
    private boolean onScroll;
    private onSeekListener onSeekListener;
    private boolean isDisable;

    public interface onSeekListener {
        /**
         * percent 实时同步的变更的回调
         *
         * @param progress   进度值大于0，小于{@link XmSeekBar#mMax}
         * @param isFromUser 是否为用户滑动触发
         */
        void onSyncSeek(int progress, boolean isFromUser);

        /**
         * percent 一次按下抬起时的变更的回调，用户手指松开控件
         *
         * @param progress   进度值大于0，小于{@link XmSeekBar#mMax}
         * @param isFromUser 是否为用户滑动触发
         */
        void onOneSeekDone(int progress, boolean isFromUser);

        /**
         * 用户触摸到控件
         */
        void onControl();
    }

    public XmSeekBar(Context context) {
        this(context, null);
    }

    public XmSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XmSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.XmSeekBar, 0, 0);
        mMax = attributes.getInt(R.styleable.XmSeekBar_max, DEFAULT_MAX);
        percent = 1f * attributes.getInt(R.styleable.XmSeekBar_progress, (int) (DEFAULT_PERCENT * DEFAULT_MAX)) / DEFAULT_MAX;
        attributes.recycle();

        initView();
    }

    @Override
    public void applySkin() {
        initView();
        invalidate();
    }
    /**
     * 设置监听回调
     *
     * @param onSeekListener 监听
     */
    public void setOnSeekListener(onSeekListener onSeekListener) {
        this.onSeekListener = onSeekListener;
    }

    /**
     * 设置最大值
     *
     * @param max 最大值
     */
    public void setMax(int max) {
        this.mMax = max;
    }

    public long getMax() {
        return mMax;
    }

    /**
     * 获取当前进度值
     *
     * @return percent 0-{@link XmSeekBar#mMax}
     */
    public long getProgress() {
        return (long) (percent * mMax);
    }

    /**
     * 获取当前进度百分比
     *
     * @return percent 0-1
     */
    public float getPercent() {
        return percent;
    }

    /**
     * 设置seekbar的进度，不会触发监听回调
     *
     * @param toProgress 0-{@link XmSeekBar#mMax}之间的进度
     */
    public void setProgress(int toProgress) {
        seekTo(toProgress * 1f / mMax, false);
    }

    /**
     * 设置seekbar的进度，手动控制是否触发监听回调
     *
     * @param toProgress 0-{@link XmSeekBar#mMax}之间的进度
     */
    public void setProgress(int toProgress, boolean triggeredCallback) {
        seekTo(toProgress * 1f / mMax, triggeredCallback);
    }

    /**
     * 设置禁用用户滑动
     *
     * @param isDisable 是否禁用用户滑动
     */
    public void setDisable(boolean isDisable) {
        this.isDisable = isDisable;
    }

    /**
     * 获取禁用用户滑动状态
     *
     * @return 是否禁用用户滑动
     */
    public boolean isDisable() {
        return isDisable;
    }

    private void seekTo(@FloatRange(from = 0, to = 1) float toProgress, boolean triggeredCallback) {
        if (!onScroll) {
            if (toProgress > 1f) toProgress = 1;
            if (toProgress < 0f) toProgress = 0;
            percent = toProgress;
            if (onSeekListener != null && triggeredCallback) {
                onSeekListener.onSyncSeek((int) (mMax * percent), false);
                onSeekListener.onOneSeekDone((int) (mMax * percent), false);
            }
            postInvalidate();
        }
    }

    private void initView() {
//        leftFill = getContext().getResources().getDrawable(R.drawable.seek_bar_left_fill);
//        rightUnfill = getContext().getResources().getDrawable(R.drawable.seek_bar_right_unfill);
//        block = getContext().getResources().getDrawable(R.drawable.seek_bar_block);
//        middleFill = getContext().getResources().getDrawable(R.drawable.seek_bar_middle_fill);
//        middleUnfill = getContext().getResources().getDrawable(R.drawable.seek_bar_middle_unfill);
//        shadow = getContext().getResources().getDrawable(R.drawable.seek_bar_shadow);
        leftFill = XmSkinManager.getInstance().getDrawable(R.drawable.seek_bar_left_fill);
        rightUnfill = XmSkinManager.getInstance().getDrawable(R.drawable.seek_bar_right_unfill);
        block = XmSkinManager.getInstance().getDrawable(R.drawable.seek_bar_block);
        middleFill = XmSkinManager.getInstance().getDrawable(R.drawable.seek_bar_middle_fill);
        middleUnfill = XmSkinManager.getInstance().getDrawable(R.drawable.seek_bar_middle_unfill);
        shadow = XmSkinManager.getInstance().getDrawable(R.drawable.seek_bar_shadow);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_TOTAL_HEIGHT + DEFAULT_TOP_PADDING + DEFAULT_BOTTOM_PADDING, MeasureSpec.EXACTLY);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 画出前端
        leftFill.setBounds(0, DEFAULT_TOP_PADDING, DEFAULT_LEFT_RIGHT_WIDTH, DEFAULT_TOTAL_HEIGHT + DEFAULT_TOP_PADDING);
        leftFill.draw(canvas);

        // 画已填充部分
        int progressWidth = (int) ((mWidth - DEFAULT_LEFT_RIGHT_WIDTH - DEFAULT_LEFT_RIGHT_WIDTH - DEFAULT_BLOCK_WIDTH) * percent);
        middleFill.setBounds(DEFAULT_LEFT_RIGHT_WIDTH, DEFAULT_TOP_PADDING, DEFAULT_LEFT_RIGHT_WIDTH + progressWidth, DEFAULT_TOTAL_HEIGHT + DEFAULT_TOP_PADDING);
        middleFill.draw(canvas);

        // 画未填充部分
        middleUnfill.setBounds(DEFAULT_LEFT_RIGHT_WIDTH + progressWidth + DEFAULT_BLOCK_WIDTH,
                DEFAULT_TOP_PADDING, mWidth - DEFAULT_LEFT_RIGHT_WIDTH, DEFAULT_TOTAL_HEIGHT + DEFAULT_TOP_PADDING);
        middleUnfill.draw(canvas);

        // 画末端
        rightUnfill.setBounds(mWidth - DEFAULT_LEFT_RIGHT_WIDTH, DEFAULT_TOP_PADDING, mWidth, DEFAULT_TOTAL_HEIGHT + DEFAULT_TOP_PADDING);
        rightUnfill.draw(canvas);

        // 画阴影
        shadow.setBounds(0, DEFAULT_TOP_PADDING, mWidth, DEFAULT_TOTAL_HEIGHT + DEFAULT_TOP_PADDING);
        shadow.draw(canvas);

        // 画bar必须放到最后画，不然阴影会被遮盖
        block.setBounds(DEFAULT_LEFT_RIGHT_WIDTH + progressWidth, DEFAULT_TOP_PADDING, DEFAULT_LEFT_RIGHT_WIDTH + progressWidth + DEFAULT_BLOCK_WIDTH, DEFAULT_TOTAL_HEIGHT + DEFAULT_TOP_PADDING);
        block.draw(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDisable) {
            return super.onTouchEvent(event);
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    oldX = event.getX();
                    onScroll = false;
                    onSeekListener.onControl();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float curX = event.getX();
                    float dX = curX - oldX;
                    if (!onScroll && Math.abs(dX) > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                        percent += dX / (mWidth - DEFAULT_LEFT_RIGHT_WIDTH - DEFAULT_LEFT_RIGHT_WIDTH - DEFAULT_BLOCK_WIDTH);
                        if (percent > 1f) percent = 1;
                        if (percent < 0f) percent = 0;
                        if (onSeekListener != null) {
                            onSeekListener.onSyncSeek((int) (mMax * percent), true);
                        }
                        postInvalidate();
                        oldX = curX;
                        onScroll = true;
                        return true;
                    } else if (onScroll) {
                        percent += dX / (mWidth - DEFAULT_LEFT_RIGHT_WIDTH - DEFAULT_LEFT_RIGHT_WIDTH - DEFAULT_BLOCK_WIDTH);
                        if (percent > 1f) percent = 1;
                        if (percent < 0f) percent = 0;
                        if (onSeekListener != null) {
                            onSeekListener.onSyncSeek((int) (mMax * percent), true);
                        }
                        postInvalidate();
                        oldX = curX;
                        onScroll = true;
                        return true;
                    } else {
                        onScroll = false;
                        return false;
                    }
                case MotionEvent.ACTION_UP:
                    if (onSeekListener != null && onScroll) {
                        onSeekListener.onOneSeekDone((int) (mMax * percent), true);
                        onScroll = false;
                        return true;
                    } else {
                        onScroll = false;
                        return false;
                    }
                default:
                    return false;
            }
        }
    }
}
