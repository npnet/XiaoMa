package com.xiaoma.pet.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoma.pet.R;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/22 0022 10:37
 *   desc:   宠物地图轨迹
 * </pre>
 */
public class MapTrackView extends View {


    private static final int DEFAULT_TRACK_WIDTH = 5;
    private static final int DEFAULT_TRACK_COLOR = Color.BLUE;
    private static final int DEFAULT_RADIUS = 40;
    private static final int CONTENT_OFFSET_X = 23;
    private static final int CONTENT_OFFSET_Y = 10;
    private String START_CONTENT = "起点";
    private String END_CONTENT = "终点";

    //坐标点
    private static final int START_OR_END_X = 266;
    private static final int START_Y = 614;
    private static final int HORIZONTAL_DISTANCE = 1276;
    private static final int SECOND_LAYER_Y = 514;
    private static final int THIRD_LAYER_Y = 414;
    private static final int FOURTH_LAYER_Y = 314;

    private Paint circlePaint;
    private Paint textPaint;
    private Paint trackPaint;
    private int trackWidth;
    private int trackColor;
    private int circleRadius;
    private int circleBg;
    private int textSize;
    private int textColor;
    private Path trackPath = new Path();


    public MapTrackView(Context context) {
        this(context, null);
    }

    public MapTrackView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapTrackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        setupPaint();
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MapTrackView);
        trackWidth = typedArray.getDimensionPixelOffset(R.styleable.MapTrackView_track_width, DEFAULT_TRACK_WIDTH);
        trackColor = typedArray.getColor(R.styleable.MapTrackView_track_color, DEFAULT_TRACK_COLOR);
        circleRadius = typedArray.getDimensionPixelOffset(R.styleable.MapTrackView_start_end_radius, DEFAULT_RADIUS);
        circleBg = typedArray.getColor(R.styleable.MapTrackView_circle_bg, Color.GRAY);
        textSize = typedArray.getDimensionPixelSize(R.styleable.MapTrackView_track_text_size, 0);
        textColor = typedArray.getColor(R.styleable.MapTrackView_track_text_color, Color.GRAY);
        typedArray.recycle();

        START_CONTENT = context.getString(R.string.map_start);
        END_CONTENT = context.getString(R.string.map_end);
    }


    private void setupPaint() {
        trackPaint = new Paint();
        trackPaint.setColor(trackColor);
        trackPaint.setStrokeWidth(trackWidth);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setAntiAlias(true);
        trackPaint.setDither(true);

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);

        circlePaint = new Paint();
        circlePaint.setColor(circleBg);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制轨迹
        trackPath.moveTo(START_OR_END_X, START_Y);
        trackPath.lineTo(HORIZONTAL_DISTANCE, START_Y);
        trackPath.lineTo(HORIZONTAL_DISTANCE, SECOND_LAYER_Y);
        trackPath.lineTo(START_OR_END_X, SECOND_LAYER_Y);
        trackPath.lineTo(START_OR_END_X, THIRD_LAYER_Y);
        trackPath.lineTo(HORIZONTAL_DISTANCE, THIRD_LAYER_Y);
        trackPath.lineTo(HORIZONTAL_DISTANCE, FOURTH_LAYER_Y);
        trackPath.lineTo(START_OR_END_X, FOURTH_LAYER_Y);
        canvas.drawPath(trackPath, trackPaint);
        canvas.save();

        //绘制起始点标志
        drawStartAndEnd(canvas);
    }


    private void drawStartAndEnd(Canvas canvas) {
        canvas.restore();
        canvas.drawCircle(START_OR_END_X, START_Y, circleRadius, circlePaint);
        canvas.drawCircle(START_OR_END_X, FOURTH_LAYER_Y, circleRadius, circlePaint);

        //绘制内部字体
        canvas.drawText(START_CONTENT, START_OR_END_X - CONTENT_OFFSET_X, START_Y + CONTENT_OFFSET_Y, textPaint);
        canvas.drawText(END_CONTENT, START_OR_END_X - CONTENT_OFFSET_X, FOURTH_LAYER_Y + CONTENT_OFFSET_Y, textPaint);
        canvas.save();
    }


}
