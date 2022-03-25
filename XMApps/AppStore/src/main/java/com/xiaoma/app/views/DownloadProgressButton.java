package com.xiaoma.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.xiaoma.app.R;

/**
 * Created by zhushi on 2018/8/25.
 */

public class DownloadProgressButton extends AppCompatTextView {

    //只有点击类型 eg:去下载、去打开、去更新
    public static final int TYPE_NORMAL = 1;
    //带进度类型   eg:正在下载
    public static final int TYPE_PROGRESS = 2;
    //不可点击类型 eg:安装中
    public static final int TYPE_UN_CLICK = 3;
    //暂停
    public static final int TYPE_PROGRESS_PAUSE = 4;
    //等待中
    public static final int TYPE_PROGRESS_WAIT = 5;

    //当前按钮状态
    private int mState = TYPE_NORMAL;

    //下载已走进度背景颜色
    private int mBackgroundColor;
    //下载中后半部分后面背景颜色
    private int mBackgroundSecondColor;
    //暂停已走进度背景颜色
    private int mPauseBackgroundColor;
    //文字颜色
    private int mTextColor;
    //点击颜色
    private int mPressColor;

    private float mProgress = -1;
    private int mMaxProgress;
    private int mMinProgress;

    //背景画笔
    private volatile Paint mBackgroundPaint;
    //按钮文字画笔
    private volatile Paint mTextPaint;
    //点击画笔
    private Paint mPressPaint;

    private CharSequence mCurrentText;
    //待下载bitmap
    private Bitmap mDownloadBitmap;
    //下载中bitmap
    private Bitmap mDownloadingBitmap;
    //六边形
    private Path mPath;
    //是否点击
    boolean mPressed;

    private static final int MIN_VOLUME_CHANGE_TIME = 32;
    private long lastTime;

    public String getmCurrentText() {
        return mCurrentText.toString();
    }

    public DownloadProgressButton(Context context) {
        this(context, null);
    }

    public DownloadProgressButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            initAttrs(context, attrs);
            init();
        }
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DownloadProgressButton);
        mBackgroundColor = a.getColor(R.styleable.DownloadProgressButton_backgroud_color, getResources().getColor(R.color.download_bg_color));
        mBackgroundSecondColor = Color.TRANSPARENT;
        mTextColor = a.getColor(R.styleable.DownloadProgressButton_text_color, Color.WHITE);
        mPressColor = a.getColor(R.styleable.DownloadProgressButton_backgroud_press_color, getResources().getColor(R.color.download_bg_press_color));
        mPauseBackgroundColor = a.getColor(R.styleable.DownloadProgressButton_backgroud_download_pause_color, getResources().getColor(R.color.download_pause_bg_colorr));
        a.recycle();
    }

    private void init() {
        mMaxProgress = 100;
        mMinProgress = 0;
        mProgress = 0;

        //下载背景画笔
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setStrokeWidth(1f);

        //点击背景
        mPressPaint = new Paint();
        mPressPaint.setAntiAlias(true);
        mPressPaint.setStyle(Paint.Style.FILL);
        mPressPaint.setStrokeWidth(1f);
        mPressPaint.setColor(mPressColor);

        //设置文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //解决文字有时候画不出问题
            setLayerType(LAYER_TYPE_SOFTWARE, mTextPaint);
        }

        //下载背景
        mDownloadBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.btn_download_n);
        //下载中背景
        mDownloadingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.btn_downloading_n);

        //绘制六边形
        mPath = new Path();
        mPath.moveTo(2.0f, 26);
        mPath.lineTo(21.0f, 1.5f);
        mPath.lineTo(188.5f, 1.5f);
        mPath.lineTo(207.5f, 26);
        mPath.lineTo(188.5f, 49.5f);
        mPath.lineTo(21.5f, 49.5f);
        mPath.close();

        //初始化状态设为TYPE_NORMAL
        mState = TYPE_NORMAL;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode()) {
            drawBitmap(canvas);
            drawing(canvas);
        }
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        super.dispatchSetPressed(pressed);
        this.mPressed = pressed;
        invalidate();
    }

    /**
     * draw图片背景
     *
     * @param canvas
     */
    private void drawBitmap(Canvas canvas) {
        if (mState == TYPE_PROGRESS) {
            canvas.drawBitmap(mDownloadingBitmap, 0, 0, mTextPaint);
        } else {
            canvas.drawBitmap(mDownloadBitmap, 0, 0, mTextPaint);
        }
    }

    private void drawing(Canvas canvas) {
        drawBackground(canvas);
        drawTextAbove(canvas);
    }

    private void drawBackground(Canvas canvas) {
        switch (mState) {
            case TYPE_UN_CLICK:
                mBackgroundPaint.setColor(mBackgroundSecondColor);
                canvas.drawPath(mPath, mBackgroundPaint);
                break;
            case TYPE_PROGRESS_PAUSE:
                mBackgroundPaint.setColor(mBackgroundColor);
                drawProgressGradient(canvas, mPauseBackgroundColor);
                break;
            case TYPE_PROGRESS:
            case TYPE_PROGRESS_WAIT:
                mBackgroundPaint.setColor(mBackgroundColor);
                drawProgressGradient(canvas, mBackgroundColor);
                break;
            default:
                mBackgroundPaint.setColor(Color.TRANSPARENT);
                canvas.drawPath(mPath, mBackgroundPaint);
                //点击
                if (mPressed) {
                    canvas.drawPath(mPath, mPressPaint);
                }
                break;
        }
        mBackgroundPaint.setShader(null);
    }

    private void drawProgressGradient(Canvas canvas, int color) {
        float mProgressPercent = mProgress / (mMaxProgress + 0f);
        LinearGradient mProgressBgGradient = new LinearGradient(0, 0, getMeasuredWidth(), 0,
                new int[]{color, mBackgroundSecondColor},
                new float[]{mProgressPercent, mProgressPercent}, Shader.TileMode.CLAMP);
        mBackgroundPaint.setShader(mProgressBgGradient);
        canvas.drawPath(mPath, mBackgroundPaint);
        //点击
        if (mPressed && mState != TYPE_PROGRESS_WAIT) {
            canvas.drawPath(mPath, mPressPaint);
        }
    }

    private void drawTextAbove(Canvas canvas) {
        mTextPaint.setTextSize(24);
        if (mCurrentText == null) {
            mCurrentText = "";
        }
        final float textWidth = mTextPaint.measureText(mCurrentText.toString());
        if (mState == TYPE_PROGRESS_PAUSE) {
            mTextColor = getResources().getColor(R.color.download_pause_text_color);
        } else if (mState == TYPE_UN_CLICK || mState == TYPE_PROGRESS_WAIT) {
            mTextColor = getResources().getColor(R.color.download_un_click_color);
        } else {
            mTextColor = Color.WHITE;
        }
        mTextPaint.setColor(mTextColor);
        canvas.drawText(mCurrentText.toString(), (getMeasuredWidth() - textWidth) / 2, getMeasuredHeight() / 2, mTextPaint);
    }

    public void setState(int state) {
        //状态确实有改变
        this.mState = state;
        invalidate();
    }

    public void setState(int state, CharSequence charSequence) {
        //状态确实有改变
        this.mState = state;
        mCurrentText = charSequence;
        invalidate();
    }

    public void setProgress(float progress) {
        if (progress >= mMinProgress && progress <= mMaxProgress) {
            mProgress = progress;
            if (!isFastUpdate()) {
                return;
            }
            invalidate();
        } else if (progress < mMinProgress) {
            mProgress = 0;

        } else if (progress > mMaxProgress) {
            mProgress = 100;
        }
    }

    private boolean isFastUpdate() {
        boolean flag = false;
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastTime >= MIN_VOLUME_CHANGE_TIME) {
            flag = true;
            lastTime = currentTimeMillis;
        }
        //防止车机更新can时间时出现时间异常导致刷新动画不出现的问题
        if (lastTime > currentTimeMillis) {
            lastTime = currentTimeMillis;
        }
        return flag;
    }

}
