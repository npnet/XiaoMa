package com.xiaoma.vrpractice.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.xiaoma.vrpractice.R;


public class TextTackSeekBar extends android.support.v7.widget.AppCompatSeekBar {
    private static final float DEF_IMG_WIDTH = 40;
    private static final float DEF_IMG_HEIGHT = 40;
    private static final float DEF_SPACE_HEIGHT = 40;
    public static final int DEF_MIN = -1;
    float modulus = 0.16f;//字体越大，系数越小;
    /**
     * 文本的颜色
     */
    private int mTitleTextColor;
    /**
     * 文本的大小
     */
    private float mTitleTextSize;
    private String mTitleText;//文字的内容

    /**
     * 背景图片
     */
    private int img;
    private Bitmap map;
    //bitmap对应的宽高
    private float img_width, img_height;
    Paint paint;

    private float numTextWidth;
    //测量seekbar的规格
    private Rect rect_seek;
    private Paint.FontMetrics fm;
    /**
     * 最小值
     */
    private int mMin = DEF_MIN;

    public static final int TEXT_ALIGN_LEFT = 0x00000001;
    public static final int TEXT_ALIGN_RIGHT = 0x00000010;
    public static final int TEXT_ALIGN_CENTER_VERTICAL = 0x00000100;
    public static final int TEXT_ALIGN_CENTER_HORIZONTAL = 0x00001000;
    public static final int TEXT_ALIGN_TOP = 0x00010000;
    public static final int TEXT_ALIGN_BOTTOM = 0x00100000;

    /**
     * 文本中轴线X坐标
     */
    private float textCenterX;
    /**
     * 文本baseline线Y坐标
     */
    private float textBaselineY;
    /**
     * 文字的方位
     */
    private int textAlign;

    public TextTackSeekBar(Context context) {
        this(context, null);
    }

    public TextTackSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextTackSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TextTackSeekBar, defStyleAttr, 0);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.TextTackSeekBar_textsize) {
                mTitleTextSize = array.getDimension(attr, 15f);
            } else if (attr == R.styleable.TextTackSeekBar_textcolor) {
                mTitleTextColor = array.getColor(attr, Color.WHITE);
            } else if (attr == R.styleable.TextTackSeekBar_img) {
                img = array.getResourceId(attr, 0);
            }
        }
        array.recycle();
        getImgWH();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(mTitleTextSize);
        paint.setColor(mTitleTextColor);
        //设置控件的padding 给提示文字留出位置
//        textAlign = TEXT_ALIGN_CENTER_HORIZONTAL | TEXT_ALIGN_CENTER_VERTICAL;
        textAlign = TEXT_ALIGN_TOP | TEXT_ALIGN_CENTER_HORIZONTAL;
        int top = 0, botton = 0;
        if (textAlign == (TEXT_ALIGN_TOP | TEXT_ALIGN_CENTER_HORIZONTAL)) {
            top = (int) Math.ceil(img_height);
        } else {
            botton = (int) Math.ceil(img_height) + 10;
        }
        setPadding((int) Math.ceil(img_width) / 2, top, (int) Math.ceil(img_height) / 2, botton);
//        setThumb(getResources().getDrawable(R.drawable.text_tack_seekbar_slider));
//        setProgressDrawable(getResources().getDrawable(R.drawable.text_tack_seekbar));
    }

    /**
     * 获取图片的宽高
     */
    private void getImgWH() {
        if (img == 0) {
            img_width = DEF_IMG_WIDTH;
            img_height = DEF_IMG_HEIGHT;
            return;
        }

        map = BitmapFactory.decodeResource(getResources(), img);
        img_width = map.getWidth();
        img_height = map.getHeight();


    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setTextLocation();//定位文本绘制的位置
        rect_seek = this.getProgressDrawable().getBounds();
        //定位文字背景图片的位置
        float bm_x = rect_seek.width() * getProgress() / getMax();
        float bm_y, text_y;
        if (textAlign == (TEXT_ALIGN_CENTER_HORIZONTAL | TEXT_ALIGN_CENTER_VERTICAL)) {
            bm_y = rect_seek.height() + DEF_SPACE_HEIGHT;
            text_y = (float) (textBaselineY + bm_y + (modulus * img_height / 2));
        } else {
            bm_y = rect_seek.height();
            text_y = (float) (textBaselineY + bm_y + (img_height) - ((modulus * img_height / 2) + DEF_SPACE_HEIGHT));
        }
//        //计算文字的中心位置在bitmap
        float text_x = rect_seek.width() * getProgress() / getMax() + (img_width - numTextWidth) / 2;
        if (map != null) {
            canvas.drawBitmap(map, bm_x, bm_y, paint);
        }
        // canvas.drawRoundRect();
        canvas.drawText(mTitleText, text_x, text_y, paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        return super.onTouchEvent(event);
    }

    /**
     * 定位文本绘制的位置
     */
    private void setTextLocation() {

        fm = paint.getFontMetrics();
        //文本的宽度
        if (mMin == DEF_MIN) {
            mTitleText = String.valueOf(getProgress());
        } else {
            mTitleText = String.valueOf(getProgress() < mMin ? mMin : getProgress());
        }

        numTextWidth = paint.measureText(mTitleText);

        float textCenterVerticalBaselineY = img_height / 2 - fm.descent + (fm.descent - fm.ascent) / 2;
        switch (textAlign) {
            case TEXT_ALIGN_CENTER_HORIZONTAL | TEXT_ALIGN_CENTER_VERTICAL:
                textCenterX = img_width / 2;
                textBaselineY = textCenterVerticalBaselineY;
                break;
            case TEXT_ALIGN_LEFT | TEXT_ALIGN_CENTER_VERTICAL:
                textCenterX = numTextWidth / 2;
                textBaselineY = textCenterVerticalBaselineY;
                break;
            case TEXT_ALIGN_RIGHT | TEXT_ALIGN_CENTER_VERTICAL:
                textCenterX = img_width - numTextWidth / 2;
                textBaselineY = textCenterVerticalBaselineY;
                break;
            case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_CENTER_HORIZONTAL:
                textCenterX = img_width / 2;
                textBaselineY = img_height - fm.bottom;
                break;
            case TEXT_ALIGN_TOP | TEXT_ALIGN_CENTER_HORIZONTAL:
                textCenterX = img_width / 2;
                textBaselineY = -fm.ascent;
                break;
            case TEXT_ALIGN_TOP | TEXT_ALIGN_LEFT:
                textCenterX = numTextWidth / 2;
                textBaselineY = -fm.ascent;
                break;
            case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_LEFT:
                textCenterX = numTextWidth / 2;
                textBaselineY = img_height - fm.bottom;
                break;
            case TEXT_ALIGN_TOP | TEXT_ALIGN_RIGHT:
                textCenterX = img_width - numTextWidth / 2;
                textBaselineY = -fm.ascent;
                break;
            case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_RIGHT:
                textCenterX = img_width - numTextWidth / 2;
                textBaselineY = img_height - fm.bottom;
                break;
        }
    }

    public void setMinValue(int min) {
        mMin = min;
    }
}