package com.xiaoma.setting.sound.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.StringDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.setting.R;
import com.xiaoma.skin.views.XmSkinRelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by qiuboxiang on 2018/11/19 20:02
 */
public class SoundFieldLayout extends XmSkinRelativeLayout implements View.OnClickListener {

    public static final String TAG = "SoundFieldLayout";
    private ImageView mSlider;
    private ImageView mIvFrontMaster;
    private ImageView mIvFrontSlave;
    private ImageView mIvRearMaster;
    private ImageView mIvRearSlave;
    private ImageView mIvChecked;
    private TextView mTvFrontMaster;
    private TextView mTvFrontSlave;
    private TextView mTvRearMaster;
    private TextView mTvRearSlave;
    private TextView mTvChecked;
    private int mDownX;
    private int mDownY;
    private int mSliderLastX;
    private int mSliderLastY;
    private float mCellWidth;
    private float mCellHeight;
    private int mMaxX;
    private int mMaxY;
    private int minTranslationX;
    private int maxTranslationX;
    private int minTranslationY;
    private int maxTranslationY;
    private int mSliderWidth;
    private int mSliderHeight;
    private int mSeatWidth;
    private int mSeatHeight;
    private int mRows = 15;//网格行数
    private int mCols = 15;//网格列数
    private Map<RectF, Point> mRectMap;
    private Point mCheckedPoint;
    private @Position String mCheckedPosition;
    private OnPositionChangedListener mListener;
    private boolean mInited;
    private Map<String, Point> positionMap = new HashMap<>();
    private boolean mDrawFinished;

    public SoundFieldLayout(Context context) {
        this(context, null);
    }

    public SoundFieldLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoundFieldLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        setWillNotDraw(false);
        initPositionMap();
        initView();
        initBitmapDimension();
    }

    private void initPositionMap() {
        positionMap.put(Position.TOP_LEFT, new Point(4, 12));
        positionMap.put(Position.TOP_RIGHT, new Point(12, 12));
        positionMap.put(Position.BOTTOM_LEFT, new Point(4, 4));
        positionMap.put(Position.BOTTOM_RIGHT, new Point(12, 4));
        positionMap.put(Position.CENTER, new Point(8, 8));
    }

    private void initView() {
        inflate(getContext(), R.layout.view_sound_field, this);
        mSlider = findViewById(R.id.center_position);
        mIvFrontMaster = findViewById(R.id.front_master_seat);
        mIvFrontSlave = findViewById(R.id.front_slave_seat);
        mIvRearMaster = findViewById(R.id.rear_master_seat);
        mIvRearSlave = findViewById(R.id.rear_slave_seat);
        mTvFrontMaster = findViewById(R.id.tv_front_master);
        mTvFrontSlave = findViewById(R.id.tv_front_slave);
        mTvRearMaster = findViewById(R.id.tv_rear_master);
        mTvRearSlave = findViewById(R.id.tv_rear_slave);

        mIvFrontMaster.setOnClickListener(this);
        mIvFrontSlave.setOnClickListener(this);
        mIvRearMaster.setOnClickListener(this);
        mIvRearSlave.setOnClickListener(this);
        mTvRearMaster.setOnClickListener(this);
        mTvFrontMaster.setOnClickListener(this);
        mTvFrontSlave.setOnClickListener(this);
        mTvRearSlave.setOnClickListener(this);

        initSlider();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initSlider() {
        mSlider.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mSlider.setImageDrawable(getResources().getDrawable(R.drawable.cross_slider_pressed));
                        updateUI(null, null);

                        mDownX = x;
                        mDownY = y;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int deltaX = x - mSliderLastX;
                        int deltaY = y - mSliderLastY;
                        int translationX = (int) (mSlider.getTranslationX() + deltaX);
                        int translationY = (int) (mSlider.getTranslationY() + deltaY);

                        translationX = translationX < minTranslationX ? minTranslationX : translationX;
                        translationX = translationX > maxTranslationX ? maxTranslationX : translationX;
                        translationY = translationY < minTranslationY ? minTranslationY : translationY;
                        translationY = translationY > maxTranslationY ? maxTranslationY : translationY;

                        mSlider.setTranslationX(translationX);
                        mSlider.setTranslationY(translationY);
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mSlider.setImageDrawable(getResources().getDrawable(R.drawable.cross_slider));
                        if (x == mDownX && y == mDownY) {
                            if (mCheckedPosition != null) {
                                setPosition(mCheckedPosition);
                            }
                        } else {
                            float sliderCenterX = mSlider.getLeft() + mSlider.getTranslationX() + mSliderWidth / 2;
                            float sliderCenterY = mSlider.getTop() + mSlider.getTranslationY() + mSliderHeight / 2;
                            getCheckedPoint(sliderCenterX, sliderCenterY);
                        }
                        break;
                }
                mSliderLastX = x;
                mSliderLastY = y;
                return true;
            }
        });
    }

    private void initBitmapDimension() {
        Bitmap slider = BitmapFactory.decodeResource(getResources(), R.drawable.cross_slider);
        mSliderWidth = slider.getWidth();
        mSliderHeight = slider.getHeight();

        Bitmap seat = BitmapFactory.decodeResource(getResources(), R.drawable.seat_unchecked);
        mSeatWidth = seat.getWidth();
        mSeatHeight = seat.getHeight();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        initDimension();
    }

    private void initDimension() {
        //初始化网格矩阵
/*        int rectLeft = mTvFrontMaster.getRight();
        int rectRight = mTvFrontSlave.getLeft();
        int rectTop = mIvFrontMaster.getTop();
        int rectBottom = mIvRearMaster.getBottom();*/
        int rectLeft = mTvFrontMaster.getRight() + mSliderWidth / 2;
        int rectRight = mTvFrontSlave.getLeft() - mSliderWidth / 2;
        int rectTop = mIvFrontMaster.getTop() + mSliderHeight / 2;
        int rectBottom = mIvRearMaster.getBottom() - mSliderHeight / 2;

        mCellWidth = (float) (rectRight - rectLeft) / mCols;
        mCellHeight = (float) (rectBottom - rectTop) / mRows;

      /*  mMinX = rectLeft + mSliderWidth / 2;
        mMaxX = rectRight - mSliderWidth / 2;
        mMinY = rectTop + mSliderHeight / 2;
        mMaxY = rectBottom - mSliderHeight / 2;*/

        int mMinX = rectLeft;
        int mMinY = rectTop;
        mMaxX = rectRight;
        mMaxY = rectBottom;

        minTranslationX = mMinX - mSliderWidth / 2 - mSlider.getLeft();
        maxTranslationX = mMaxX - mSliderWidth / 2 - mSlider.getLeft();
        minTranslationY = mMinY - mSliderHeight / 2 - mSlider.getTop();
        maxTranslationY = mMaxY - mSliderHeight / 2 - mSlider.getTop();

        mRectMap = new HashMap<>();
        for (int j = 0; j < mRows; j++) {
            for (int i = 0; i < mCols; i++) {
                float left = rectLeft + mCellWidth * i;
                float top = rectTop + mCellHeight * j;
//                float right = left + mCellWidth;
                float right = rectLeft + mCellWidth * (i + 1);
//                float bottom = top + mCellHeight;
                float bottom = rectTop + mCellHeight * (j + 1);
                mRectMap.put(new RectF(left, top, right, bottom), new Point(i, j));
            }
        }

        if (mCheckedPoint != null) {
            handleCheckedPoint();
        }
    }

 /*   @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);

        for (RectF rectF : mRectMap.keySet()) {
            canvas.drawRect(rectF, paint);
        }
    }*/

    private void getCheckedPoint(float x, float y) {
        Point point = null;
        for (RectF rectF : mRectMap.keySet()) {
            if (rectF.contains(x, y) || ((x == mMaxX || y == mMaxY) && contains(rectF, x, y))) {
                point = mRectMap.get(rectF);
                break;
            }
        }

        /*if (point.x == mCols / 2 && point.y == mRows / 2) {
            setPosition(Position.CENTER);
            return;
        }*/
        Log.d(TAG, "getCheckedPoint: " + point.x + " " + point.y);
        notifyPointChange(point, true);
    }

    private void notifyPointChange(Point point, boolean transformPoint) {
        if (point == null) return;
        if (mCheckedPoint != null && point.x == mCheckedPoint.x && point.y == mCheckedPoint.y) {
            return;
        }
        mCheckedPoint = point;
        mCheckedPosition = null;
        notifyListener(transformPoint);
    }

    public boolean contains(RectF rectF, float x, float y) {
        float left = rectF.left;
        float top = rectF.top;
        float right = rectF.right;
        float bottom = rectF.bottom;
        return left < right && top < bottom && x >= left && x <= right && y >= top && y <= bottom;
    }

    private void notifyListener(boolean transformPoint) {
        if (mListener != null) {
            Point point = mCheckedPosition == null ? mCheckedPoint : null;
            if (transformPoint && mCheckedPoint != null) {
                point = getTransformedPoint(mCheckedPoint);
            }
            mListener.onPositionChanged(getId(), point, mCheckedPosition);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.front_master_seat:
            case R.id.tv_front_master:
                setPosition(Position.TOP_LEFT);
                break;
            case R.id.front_slave_seat:
            case R.id.tv_front_slave:
                setPosition(Position.TOP_RIGHT);
                break;
            case R.id.rear_master_seat:
            case R.id.tv_rear_master:
                setPosition(Position.BOTTOM_LEFT);
                break;
            case R.id.rear_slave_seat:
            case R.id.tv_rear_slave:
                setPosition(Position.BOTTOM_RIGHT);
                break;
        }
    }

    public void updateUI(TextView textView, ImageView imageView) {
        if (mTvChecked != null) {
            mTvChecked.setSelected(false);
        }
        if (textView != null) {
            textView.setSelected(true);
        }
        mTvChecked = textView;

        if (mIvChecked != null) {
            mIvChecked.setSelected(false);
        }
        if (imageView != null) {
            imageView.setSelected(true);
        }
        mIvChecked = imageView;
    }

    /**
     * 设置听音位
     *
     * @param position
     */
    public void setPosition(@Position String position) {
        mCheckedPoint = null;
        if (!mInited) {
            mInited = true;
        } else {
            notifyPointChange(positionMap.get(position), false);
        }

        ImageView seat = null;
        switch (position) {
            case Position.TOP_LEFT:
                updateUI(mTvFrontMaster, mIvFrontMaster);
                seat = mIvFrontMaster;
                break;

            case Position.TOP_RIGHT:
                updateUI(mTvFrontSlave, mIvFrontSlave);
                seat = mIvFrontSlave;
                break;

            case Position.BOTTOM_LEFT:
                updateUI(mTvRearMaster, mIvRearMaster);
                seat = mIvRearMaster;
                break;

            case Position.BOTTOM_RIGHT:
                updateUI(mTvRearSlave, mIvRearSlave);
                seat = mIvRearSlave;
                break;

            case Position.CENTER:
                updateUI(null, null);
                mSlider.setTranslationX(0);
                mSlider.setTranslationY(0);
                return;
        }

        PointF point = getSeatLocation(seat);
        setSliderLocation(point.x, point.y);
    }

    public void setCheckedPoint(Point checkedPoint) {
        Iterator<Map.Entry<String, Point>> iter = positionMap.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<String, Point> item = iter.next();
            final Point point = item.getValue();
            if (checkedPoint.x == point.x && checkedPoint.y == point.y) {
                if (!mDrawFinished) {
                    getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                        @Override
                        public void onDraw() {
                            mDrawFinished = true;
                            getViewTreeObserver().removeOnDrawListener(this);
                            setPosition(item.getKey());
                        }
                    });
                } else {
                    setPosition(item.getKey());
                }
                return;
            }
        }
        mInited = true;
        mCheckedPosition = null;
        mCheckedPoint = getDisplayPoint(checkedPoint);
        if (mRectMap != null) {
            handleCheckedPoint();
        }
    }

    private Point getDisplayPoint(Point point) {
        return new Point(point.x - 1, mRows - point.y);
    }

    private Point getTransformedPoint(Point point) {
        return new Point(point.x + 1, (mRows + 1) - (point.y + 1));
    }

    public void handleCheckedPoint() {
        RectF checkedRectF = null;
        for (RectF rectF : mRectMap.keySet()) {
            Point point = mRectMap.get(rectF);
            if (point.x == mCheckedPoint.x && point.y == mCheckedPoint.y) {
                checkedRectF = rectF;
                break;
            }
        }
        if (checkedRectF == null) return;
        float sliderCenterX = checkedRectF.left + checkedRectF.width() / 2;
        float sliderCenterY = checkedRectF.top + checkedRectF.height() / 2;
        setSliderLocation(sliderCenterX, sliderCenterY);
    }

    private PointF getSeatLocation(ImageView view) {
        float x = view.getLeft() + mSeatWidth / 2;
        float y = view.getTop() + mSeatHeight / 2;
        return new PointF(x, y);
    }

    private void setSliderLocation(float sliderCenterX, float sliderCenterY) {
        int translationX = (int) (sliderCenterX - mSliderWidth / 2 - mSlider.getLeft());
        int translationY = (int) (sliderCenterY - mSliderWidth / 2 - mSlider.getTop());
        mSlider.setTranslationX(translationX);
        mSlider.setTranslationY(translationY);
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef(value = {Position.TOP_LEFT, Position.TOP_RIGHT, Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT, Position.CENTER})
    public @interface Position {
        String TOP_LEFT = "TOP_LEFT";
        String TOP_RIGHT = "TOP_RIGHT";
        String BOTTOM_LEFT = "BOTTOM_LEFT";
        String BOTTOM_RIGHT = "BOTTOM_RIGHT";
        String CENTER = "CENTER";
    }

    public interface OnPositionChangedListener {
        void onPositionChanged(int viewId, Point point, @Position String position);
    }

    public void setOnPointChangedListener(OnPositionChangedListener listener) {
        this.mListener = listener;
    }

    public static @Position
    String caseEffectsPositionToLayoutPosition(int effectPosition) {
        @Position String position;
        switch (effectPosition) {
            case SDKConstants.VALUE.FRONT_MASTER:
                position = SoundFieldLayout.Position.TOP_LEFT;
                break;
            case SDKConstants.VALUE.FRONT_SLAVE:
                position = SoundFieldLayout.Position.TOP_RIGHT;
                break;
            case SDKConstants.VALUE.REAR_MASTER:
                position = SoundFieldLayout.Position.BOTTOM_LEFT;
                break;
            case SDKConstants.VALUE.REAR_SLAVE:
                position = SoundFieldLayout.Position.BOTTOM_RIGHT;
                break;
            case SDKConstants.VALUE.CENTER:
            default:
                position = SoundFieldLayout.Position.CENTER;
                break;
        }
        return position;
    }

    public static int caseLayoutPositionToEffectsPosition(@Position String layoutPosition) {
        int position = 0;
        switch (layoutPosition) {
            case SoundFieldLayout.Position.TOP_LEFT:
                position = SDKConstants.VALUE.FRONT_MASTER;
                break;
            case SoundFieldLayout.Position.TOP_RIGHT:
                position = SDKConstants.VALUE.FRONT_SLAVE;
                break;
            case SoundFieldLayout.Position.BOTTOM_LEFT:
                position = SDKConstants.VALUE.REAR_MASTER;
                break;
            case SoundFieldLayout.Position.BOTTOM_RIGHT:
                position = SDKConstants.VALUE.REAR_SLAVE;
                break;
            case SoundFieldLayout.Position.CENTER:
                position = SDKConstants.VALUE.CENTER;
                break;
        }
        return position;
    }
}
