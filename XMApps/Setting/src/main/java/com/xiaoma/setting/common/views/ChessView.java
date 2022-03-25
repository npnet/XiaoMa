package com.xiaoma.setting.common.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.StringDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.xiaoma.setting.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by qiuboxiang on 2018/10/10 14:30
 * description:
 */
public class ChessView extends View {

    public static final String TAG = "ChessView";
    private int mRows;//行数
    private int mCols;//列数
    private float mCellSide;
    private int mDotRadius;
    private int mDotBgStroke;
    private boolean mIsEnabled = true;
    private boolean mIsEdgeEnabled;  // 是否能将圆点置于边界
    private Paint mDotPaint;
    private Paint mDotBgPaint;
    private Paint mChessBoardPaint;
    private Paint mBackgroundPaint;
    private Point mCheckedPoint;
    private PointF mDragPoint;
    private int mBoardWidth;
    private int mBoardHeight;
    private float mMaxX;
    private float mMaxY;
    private float mMinX;
    private float mMinY;
    private int mDotShadowRadius;
    private int mDotShadowOffsetX;
    private int mDotShadowOffsetY;
    private boolean mTouchHited;
    private OnPointChangedListener mListener;

    private int DEFAULT_ROWS_COUNT = 5;
    private int DEFAULT_COLS_COUNT = 5;
    private int DEFAULT_CELL_SIZE;

    public ChessView(Context context) {
        this(context, null);
    }

    public ChessView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        applyConfig(context, attrs);
        initDimension();
        initPaint();
    }

    private void initDimension() {
        Resources resources = getResources();
        DEFAULT_CELL_SIZE = resources.getDimensionPixelSize(R.dimen.chessview_default_cell_side);
        mDotRadius = resources.getDimensionPixelSize(R.dimen.chessview_default_dot_radius);
        mDotBgStroke = resources.getDimensionPixelSize(R.dimen.chessview_default_dot_background_stroke);
        mDotShadowRadius = resources.getDimensionPixelSize(R.dimen.chessview_default_shadow_radius);
        mDotShadowOffsetX = resources.getDimensionPixelSize(R.dimen.chessview_default_shadow_offset);
        mDotShadowOffsetY = mDotShadowOffsetX;
    }

    private void applyConfig(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ChessView);
        mRows = ta.getInteger(R.styleable.ChessView_rows, DEFAULT_ROWS_COUNT);
        mCols = ta.getInteger(R.styleable.ChessView_cols, DEFAULT_COLS_COUNT);
        mIsEdgeEnabled = ta.getBoolean(R.styleable.ChessView_edgeEnabled, false);
        ta.recycle();
    }

    private void initPaint() {
        mDotPaint = getPaint();
        mDotPaint.setColor(Color.BLACK);

        mChessBoardPaint = getPaint();
        mChessBoardPaint.setColor(Color.BLACK);

        mBackgroundPaint = getPaint();
        mBackgroundPaint.setColor(Color.WHITE);

        mDotBgPaint = getPaint();
        mDotBgPaint.setColor(Color.WHITE);
        mDotBgPaint.setShadowLayer(mDotShadowRadius, mDotShadowOffsetX, mDotShadowOffsetY, Color.BLACK);
    }

    private Paint getPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int defaultWidth = DEFAULT_CELL_SIZE * mCols;
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                mBoardWidth = widthSize;
                mCellSide = (float) (widthSize) / mCols;
                break;

            case MeasureSpec.AT_MOST:
                if (defaultWidth > widthSize) {
                    mBoardWidth = widthSize;
                    mCellSide = (float) (widthSize) / mCols;
                } else {
                    mBoardWidth = defaultWidth;
                    mCellSide = DEFAULT_CELL_SIZE;
                }
                break;

            case MeasureSpec.UNSPECIFIED:
                mBoardWidth = defaultWidth;
                mCellSide = DEFAULT_CELL_SIZE;
                break;
        }

        float height = mCellSide * mRows;
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                if (height > heightSize) {
                    mBoardHeight = heightSize;
                    mCellSide = (float) (heightSize) / mRows;
                    mBoardWidth = (int) (mCellSide * mCols);
                } else {
                    mBoardHeight = (int) height;
                }
                break;

            case MeasureSpec.UNSPECIFIED:
                mBoardHeight = (int) (height);
                break;
        }

        setMeasuredDimension(mBoardWidth, mBoardHeight);
        mMaxX = mCellSide * (mCols - 1);
        mMaxY = mCellSide * (mRows - 1);
        mMinX = mCellSide;
        mMinY = mCellSide;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, mBoardWidth, mBoardHeight, mBackgroundPaint);
        for (int i = 0; i <= mRows; i++) {
            canvas.drawLine(0, mCellSide * i, mBoardWidth, mCellSide * i, mChessBoardPaint); // 画横线
        }
        for (int i = 0; i <= mCols; i++) {
            canvas.drawLine(mCellSide * i, 0, mCellSide * i, mBoardHeight, mChessBoardPaint);// 画竖线
        }

        if (mCheckedPoint != null) {
            float x;
            float y;
            if (mTouchHited) {
                x = mDragPoint.x;
                y = mDragPoint.y;
            } else {
                x = mCellSide * mCheckedPoint.x;
                y = mCellSide * mCheckedPoint.y;
            }
            canvas.drawCircle(x, y, mDotRadius + mDotBgStroke, mDotBgPaint);
            canvas.drawCircle(x, y, mDotRadius, mDotPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsEnabled) {
            if (getParent() != null && mTouchHited) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float x = event.getX();
                    float y = event.getY();
                    Rect rect = getLittleRect(x, y);
                    if (mCheckedPoint != null && rect.contains((int) (mCellSide * mCheckedPoint.x), (int) (mCellSide * mCheckedPoint.y))) {
                        mTouchHited = true;
                        break;
                    }
                    getCheckedPoint(event.getX(), event.getY());
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mTouchHited) {
                        getDragPoint(event);
                        invalidate();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (mTouchHited) {
                        mTouchHited = false;
                        getDragPoint(event);
                        getCheckedPoint(mDragPoint.x, mDragPoint.y);
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void getCheckedPoint(float x, float y) {
        Rect rect = getLittleRect(x, y);
        Point point = getContainPoint(rect);
        x = point.x;
        y = point.y;
        if (point == null) return;
        if (mCheckedPoint != null && x == mCheckedPoint.x && y == mCheckedPoint.y) return;
        if (!mIsEdgeEnabled && (x == 0 || x == mCols || y == 0 || y == mRows)) return;
        mCheckedPoint = point;
        invalidate();
        Log.d(TAG, "getCheckedPoint: " + x + "  " + y);
        if (mListener != null) {
            mListener.onPointChanged(getId(), mCheckedPoint);
        }
    }

    private void getDragPoint(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        x = x < mMinX ? mMinX : x;
        x = x > mMaxX ? mMaxX : x;
        y = y < mMinY ? mMinY : y;
        y = y > mMaxY ? mMaxY : y;
        mDragPoint = new PointF(x, y);
    }

    private Rect getLittleRect(float x, float y) {
        int left = (int) (x - mCellSide / 2);
        int top = (int) (y - mCellSide / 2);
        int right = (int) (x + mCellSide / 2);
        int bottom = (int) (y + mCellSide / 2);
        return new Rect(left, top, right, bottom);
    }

    private Point getContainPoint(Rect rect) {
        for (int i = 0; i <= mCols; i++) {
            for (int j = 0; j <= mRows; j++) {
                if (rect.contains((int) (mCellSide * i), (int) (mCellSide * j))) {
                    Point point = new Point(i, j);
                    return point;
                }
            }
        }
        return null;
    }

    public void setPoint(Point point) {
        mCheckedPoint = point;
        invalidate();
    }

    public Point setPosition(@Position String position) {
        int leftX = (mCols - 1) / 4;
        int rightX = mCols - leftX;
        int topY = (mRows - 1) / 4;
        int bottomY = mRows - topY;
        int x = 0;
        int y = 0;
        switch (position) {
            case Position.TOP_LEFT:
                x = leftX;
                y = topY;
                break;
            case Position.TOP_RIGHT:
                x = rightX;
                y = topY;
                break;
            case Position.BOTTOM_LEFT:
                x = leftX;
                y = bottomY;
                break;
            case Position.BOTTOM_RIGHT:
                x = rightX;
                y = bottomY;
                break;
            case Position.CENTER:
                x = mCols / 2;
                y = mRows / 2;
                break;
        }
        mCheckedPoint = new Point(x, y);
        invalidate();
        return mCheckedPoint;
    }

    @StringDef(value = {Position.TOP_LEFT, Position.TOP_RIGHT, Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT, Position.CENTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Position {
        String TOP_LEFT = "TOP_LEFT";
        String TOP_RIGHT = "TOP_RIGHT";
        String BOTTOM_LEFT = "BOTTOM_LEFT";
        String BOTTOM_RIGHT = "BOTTOM_RIGHT";
        String CENTER = "CENTER";
    }

    public void setEnabled(boolean enabled) {
        mIsEnabled = enabled;
    }

    public interface OnPointChangedListener {
        void onPointChanged(int viewId, Point point);
    }

    public void setOnPointChangedListener(OnPointChangedListener listener) {
        this.mListener = listener;
    }
}
