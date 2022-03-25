package com.xiaoma.pet.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoma.pet.R;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/21 0021 14:05
 *   desc:   带描边、渐变的TextView
 * </pre>
 */
@SuppressLint("AppCompatCustomView")
public class DrawStrokeTextView extends View {

    private static final int DEFAULT_STROKE_WIDTH = 3;
    private static final int DEFAULT_TEXT_SIZE = 12;
    private static final int DEFAULT_BADGE_SIZE = 11;
    private boolean isDrawStrokeFlag = true;
    private Paint strokePaint;
    private Paint gradientPaint;
    private Paint normalPaint;
    private Paint badgePaint;

    private float strokeWidth;      //描边宽度
    private int textSize;           //字体大小
    private String content;         //文本内容
    private float letterSpacing;    //字间隔
    private boolean isGradient;     //是否渐变
    private int normalColor;        //普通状态颜色
    private int badgeColor;         //标识点颜色
    private int badgeSize;          //标识点大小
    private boolean isBadge;        //是否绘制标识点

    public DrawStrokeTextView(Context context) {
        this(context, null);
    }

    public DrawStrokeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawStrokeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawStrokeTextView);
        strokeWidth = typedArray.getFloat(R.styleable.DrawStrokeTextView_stroke_width, DEFAULT_STROKE_WIDTH);
        textSize = typedArray.getDimensionPixelSize(R.styleable.DrawStrokeTextView_stroke_text_size, DEFAULT_TEXT_SIZE);
        content = typedArray.getString(R.styleable.DrawStrokeTextView_text_content);
        letterSpacing = typedArray.getFloat(R.styleable.DrawStrokeTextView_letter_spacing, 0);
        isGradient = typedArray.getBoolean(R.styleable.DrawStrokeTextView_gradient, false);
        normalColor = typedArray.getColor(R.styleable.DrawStrokeTextView_text_normal_color, Color.GRAY);
        badgeColor = typedArray.getColor(R.styleable.DrawStrokeTextView_badge_color, Color.RED);
        badgeSize = typedArray.getDimensionPixelSize(R.styleable.DrawStrokeTextView_badge_size, DEFAULT_BADGE_SIZE);
        typedArray.recycle();

        //描边背景
        strokePaint = new Paint();
        strokePaint.setColor(context.getColor(R.color.text_stroke_color));
        strokePaint.setAntiAlias(true);
        strokePaint.setFakeBoldText(true);
        strokePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setTextSize(textSize);
        strokePaint.setLetterSpacing(letterSpacing);

        //渐变
        gradientPaint = new Paint();
        gradientPaint.setAntiAlias(true);
        gradientPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        gradientPaint.setTextSize(textSize);
        gradientPaint.setLetterSpacing(letterSpacing);
        Shader shader = new LinearGradient(0, 0, 0, 50,
                new int[]{
                        context.getColor(R.color.text_start_color),
                        context.getColor(R.color.text_middle_color),
                        context.getColor(R.color.text_end_color)},
                new float[]{0, 0.3f, 0.7f},
                Shader.TileMode.REPEAT);
        gradientPaint.setShader(shader);


        //默认
        normalPaint = new Paint();
        normalPaint.setColor(normalColor);
        normalPaint.setAntiAlias(true);
        normalPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        normalPaint.setTextSize(textSize);
        normalPaint.setLetterSpacing(letterSpacing);

        //标识点
        badgePaint = new Paint();
        badgePaint.setColor(badgeColor);
        badgePaint.setAntiAlias(true);
        badgePaint.setDither(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float contentWidth = strokePaint.measureText(content, 0, content.length());
        Paint.FontMetrics fontMetrics = strokePaint.getFontMetrics();
        int startX = (int) ((getMeasuredWidth() - contentWidth) / 2);
        int startY = (int) ((getMeasuredHeight() - fontMetrics.bottom - fontMetrics.top) / 2);

        if (isDrawStrokeFlag) {
            canvas.drawText(content, startX, startY, strokePaint);

            if (isGradient) {
                canvas.drawText(content, startX, startY, gradientPaint);
            }
        } else {
            canvas.drawText(content, startX, startY, normalPaint);
        }

        if (isBadge) {
            int circleX = (int) (contentWidth + startX);
            canvas.drawCircle(circleX, Math.abs(fontMetrics.top) + fontMetrics.bottom, badgeSize, badgePaint);
        }
    }


    public void updateStyle(boolean isDrawStrokeFlag) {
        //TODO 更新样式
        this.isDrawStrokeFlag = isDrawStrokeFlag;
        invalidate();
    }


    public void showBadgeView(boolean visible) {
        this.isBadge = visible;
        invalidate();
    }

    public String getText() {
        return content;
    }

    public void setText(String text) {
        content = text;
        invalidate();
    }

}
