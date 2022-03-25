package com.xiaoma.launcher.common.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xiaoma.launcher.R;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *     author : wutao
 *     time   : 2019/03/26
 *     desc   :
 * </pre>
 */
public class WidthProgressBar extends View {

    private static final String TAG = "WidthProgressBar";
    private final int VERTICAL = 1;
    private final int HORIZONTAL = 2;

    private final int defaultBgColor = 0xFf000000;
    private final int defaultProgressColor = 0xFFFF4081;

    private int bgColor = defaultBgColor;
    private int progressColor = defaultProgressColor;

    /*圆角弧度*/
    private float rectRadius = 30f;
    /*画边框使用的Rect*/
    private RectF borderRect = new RectF();
    /*画背景使用的Rect*/
    private RectF bgRect = new RectF();
    /*画进度使用的Rect*/
    private RectF progressRect = new RectF();
    /*边框画笔*/
    private Paint borderPaint;
    /*背景画笔*/
    private Paint bgPaint;
    /*进度画笔*/
    private Paint progressPaint;
    /*进度方向*/
    private int orientation = VERTICAL;
    private int max = 100;
    private int min = 0;
    private int progress = 15;
    private Bitmap bitmap;
    /*icon显示区域Rect*/
    private Rect srcRect;
    /*icon显示位置Rect*/
    private Rect dstRect;
    private float iconPadding;
    /*进度百分比*/
    private int percent = 0;

    public WidthProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public WidthProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WidthProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public WidthProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //关闭硬件加速，不然setXfermode()可能会不生效
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WidthProgressBar);
            bgColor = typedArray.getColor(R.styleable.WidthProgressBar_bgColor, defaultBgColor);
            progressColor = typedArray.getColor(R.styleable.WidthProgressBar_progressColor, defaultProgressColor);
            progress = typedArray.getInteger(R.styleable.WidthProgressBar_progressValue, progress);
            max = typedArray.getInteger(R.styleable.WidthProgressBar_progressMax, max);
            if (max <= 0)
                throw new RuntimeException("Max 必须大于 0");
            orientation = typedArray.getInteger(R.styleable.WidthProgressBar_progressOrientation, VERTICAL);
            int imgSrc = typedArray.getResourceId(R.styleable.WidthProgressBar_iconSrc, 0);
            iconPadding = typedArray.getDimensionPixelSize(R.styleable.WidthProgressBar_iconPadding, 10);
            rectRadius = typedArray.getDimensionPixelSize(R.styleable.WidthProgressBar_rectRadius, 20);
            if (max < progress) {
                progress = max;
            }

            min = typedArray.getInteger(R.styleable.WidthProgressBar_progressMin, min);

            if (min <= 0) {
                min = 0;
            }

            typedArray.recycle();

            if (imgSrc != 0) {
                bitmap = ((BitmapDrawable) getResources().getDrawable(imgSrc)).getBitmap();
            }
        }

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(progressColor);
        borderPaint.setStrokeWidth(2);
        borderPaint.setAntiAlias(true);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgColor);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        progressPaint.setColor(progressColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bgRect.set(getPaddingLeft()
                , getPaddingTop()
                , getWidth() - getPaddingRight()
                , getHeight() - getPaddingBottom());
        computeProgressRect();

        borderRect.set(bgRect);

        if (bitmap != null) {
            srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            int iconSideLength;
            if (orientation == VERTICAL) {
                iconSideLength = (int) (bgRect.width() - iconPadding * 2);
                dstRect = new Rect((int) bgRect.left + (int) iconPadding
                        , (int) (bgRect.bottom - iconSideLength - iconPadding)
                        , (int) bgRect.right - (int) iconPadding
                        , (int) bgRect.bottom - (int) iconPadding);
            } else {
                iconSideLength = (int) (bgRect.height() - iconPadding * 2);
                dstRect = new Rect((int) bgRect.left + (int) iconPadding
                        , (int) (bgRect.bottom - iconPadding - iconSideLength)
                        , (int) (bgRect.left + iconPadding + iconSideLength)
                        , (int) (bgRect.bottom - iconPadding));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
        {
            bgPaint.setColor(bgColor);
            // draw the background of progress
            canvas.drawRoundRect(bgRect, rectRadius, rectRadius, bgPaint);

            canvas.drawRoundRect(borderRect, rectRadius, rectRadius, borderPaint);
            // draw progress
            canvas.drawRect(progressRect, progressPaint);
            bgPaint.setXfermode(null);
            if (bitmap != null) {
                //draw icon
                canvas.drawBitmap(bitmap, srcRect, dstRect, bgPaint);
            }
        }
        canvas.restoreToCount(layerId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //在家进度条内才执行操作
        KLog.d(TAG, event.getAction() + "(" + event.getX() + "," + event.getY() + ")");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (bgRect.contains(event.getX(), event.getY())) {
                    //按下时,在进度内才执行操作
                    handleTouch(event);
                    setPressed(true);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                handleTouch(event);
                setPressed(true);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                handleTouch(event);
                if (changedListener != null) {
                    changedListener.onProgressCancel();
                }
                setPressed(false);
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (changedListener != null) {
                    changedListener.onProgressCancel();
                }
                setPressed(false);
                invalidate();
                break;

        }
        return true;
    }

    private void handleTouch(MotionEvent event) {
        final int x = Math.round(event.getX());
        final int y = Math.round(event.getY());
        if (orientation == VERTICAL) {
            if (y < bgRect.top) {
                //触点超出Progress顶部
                progressRect.top = bgRect.top;
            } else if (y > bgRect.bottom) {
                //触点超过Progress底部
                progressRect.top = bgRect.bottom;
            } else {
                progressRect.top = event.getY();
            }
            int tmp = (int) ((progressRect.height() / bgRect.height()) * 100);
            if (percent != tmp) {
                percent = tmp;
                progress = percent * max / 100;
                if (progress <= min) {
                    progress = min;
                }
                if (changedListener != null)
                    changedListener.onProgressChanged(progress, percent);
            }
        } else {
            if (x > bgRect.right) {
                //触点超出Progress右端
                progressRect.right = bgRect.right;
            } else if (x < bgRect.left) {
                //触点超出Progress左端
                progressRect.right = bgRect.left;
            } else {
                progressRect.right = event.getX();
            }
            int tmp = (int) ((progressRect.width() / bgRect.width()) * 100);
            if (percent != tmp) {
                percent = tmp;
                progress = percent * max / 100;
                if (changedListener != null)
                    changedListener.onProgressChanged(progress, percent);
            }
        }
        computeProgressRect();
    }


    private OnProgressChangedListener changedListener;

    public void setChangedListener(OnProgressChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    public interface OnProgressChangedListener {
        void onProgressChanged(int currentValue, int percent);

        void onProgressCancel();
    }

    public void setMax(int m) {
        if (max <= 0)
            throw new RuntimeException("Max 必须大于 0");
        max = m;
    }

    public void setProgress(int p) {
        int oldProgress = progress;
        progress = p;
        if (max < progress) {
            progress = max;
        } else if (progress < 0)
            progress = 0;
        computeProgressRect();
        invalidate();
//        startProgressAnim(oldProgress);
    }

    private ValueAnimator valueAnimator;

    /**/
    private void startProgressAnim(int oldProgress) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator = ValueAnimator.ofInt(oldProgress, progress);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (int) animation.getAnimatedValue();
                computeProgressRect();
                invalidate();
            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.start();
    }

    /**
     * 计算进度Progress
     */
    private void computeProgressRect() {
        if (orientation == VERTICAL) {
            progressRect.set(bgRect.left
                    , bgRect.bottom - progress * bgRect.height() / max
                    , bgRect.right
                    , bgRect.bottom);
        } else {
            progressRect.set(bgRect.left
                    , bgRect.top
                    , bgRect.left + progress * bgRect.width() / max
                    , bgRect.bottom);
        }
    }

}
