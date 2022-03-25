package com.xiaoma.login.business.ui.infoview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xiaoma.login.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gillben
 * date: 218/12/04
 */
public class WheelView extends ScrollView {

    public static final String TAG = WheelView.class.getSimpleName();
    private Context context;

    //由偏移量决定容器views高度，以及当前偏移item所在高度位置
    public static final int OFF_SET_DEFAULT = 2;
    private int offset = OFF_SET_DEFAULT; // 偏移量（需要在最前面和最后面补全）
    private int displayItemCount; // 每页显示的数量
    private int selectedIndex = OFF_SET_DEFAULT;

    private LinearLayout views;
    private List<String> items;
    private int itemHeight = 0;

    private int initialY;
    private Runnable scrollerTask;
    private int newCheck = 50;
    private OnWheelViewListener onWheelViewListener;

    /**
     * 获取选中区域的边界
     */
    private int[] selectedAreaBorder;
    private Paint textPaint;
    private Paint paint;
    private int viewWidth;
    private String unitText = "年"; //单位

    public WheelView(Context context) {
        this(context, null);

    }

    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        unitText = typedArray.getString(R.styleable.WheelView_unit_text);
        typedArray.recycle();

        init(context);
    }


    private List<String> getItems() {
        return items;
    }

    public void setItems(List<String> list) {
        if (null == items) {
            items = new ArrayList<>();
        }
        views.removeAllViews();
        items.clear();
        items.addAll(list);

        // 前面和后面补全
        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }
        initData();
    }


    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }


    private void init(Context context) {
        this.context = context;
        this.setVerticalScrollBarEnabled(false);

        views = new LinearLayout(context);
        views.setOrientation(LinearLayout.VERTICAL);
        this.addView(views);

        scrollerTask = new Runnable() {
            public void run() {
                int newY = getScrollY();
                if (initialY - newY == 0) { // stopped
                    final int remainder = initialY % itemHeight;
                    final int divided = initialY / itemHeight;

                    if (remainder == 0) {
                        selectedIndex = divided + offset;

                        onSelectedCallBack();
                    } else {
                        if (remainder > itemHeight / 2) {
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    WheelView.this.smoothScrollTo(0, initialY - remainder + itemHeight);
                                    selectedIndex = divided + offset + 1;
                                    onSelectedCallBack();
                                }
                            });
                        } else {
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    WheelView.this.smoothScrollTo(0, initialY - remainder);
                                    selectedIndex = divided + offset;
                                    onSelectedCallBack();
                                }
                            });
                        }
                    }
                } else {
                    initialY = getScrollY();
                    WheelView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    public void startScrollerTask() {
        initialY = getScrollY();
        this.postDelayed(scrollerTask, newCheck);
    }

    private void initData() {
        displayItemCount = offset * 2 + 1;

        for (String item : items) {
            views.addView(createView(item));
        }

        refreshItemView(0);
    }


    private TextView createView(String item) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 63));
        tv.setSingleLine(true);
        tv.setTextSize(32);
        tv.setText(item);
        tv.setGravity(Gravity.CENTER);
        int padding = dip2px(10);
        tv.setPadding(padding, padding, padding, padding);
        if (0 == itemHeight) {
            itemHeight = getViewMeasuredHeight(tv);
            views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
            this.setLayoutParams(new LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount));
        }
        return tv;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshItemView(t);
    }

    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;
        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }
        }

        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }
            if (position == i) {
                itemView.setTextColor(Color.parseColor("#ffffff"));
                itemView.setTextSize(32);
            } else if (position == i + 1
                    || position == i - 1) {
                itemView.setTextColor(Color.parseColor("#99ffffff"));
                itemView.setTextSize(28);
            } else {
                itemView.setTextColor(Color.parseColor("#33ffffff"));
                itemView.setTextSize(24);
            }
        }
    }


    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = itemHeight * offset;
            selectedAreaBorder[1] = itemHeight * (offset + 1);
        }
        return selectedAreaBorder;
    }


    @Override
    public void setBackgroundDrawable(Drawable background) {

        if (viewWidth == 0) {
            viewWidth = context.getResources().getDisplayMetrics().widthPixels;
            Log.d(TAG, "viewWidth: " + viewWidth);
        }

        if (null == paint) {
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
        }

        if (null == textPaint) {
            textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setAntiAlias(true);
            textPaint.setStyle(Paint.Style.STROKE);
            textPaint.setTextSize(32);
        }

        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
//                canvas.drawRect(0, obtainSelectedAreaBorder()[0], viewWidth, obtainSelectedAreaBorder()[1], paint);
                drawText(canvas);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }
        };
        super.setBackgroundDrawable(background);
    }


    private void drawText(Canvas canvas) {
        Rect bounds = new Rect();
        textPaint.getTextBounds(unitText, 0, unitText.length(), bounds);
        float x = viewWidth * 2 / 3 + 20;
        float y = (obtainSelectedAreaBorder()[0] + (itemHeight - bounds.height()) / 2 + bounds.height());
        canvas.drawText(unitText, x, y, textPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "w: " + w + ", h: " + h + ", oldw: " + oldw + ", oldh: " + oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }

    /**
     * 选中回调
     */
    private void onSelectedCallBack() {
        if (null != onWheelViewListener) {
            onWheelViewListener.onSelected(selectedIndex, items.get(selectedIndex));
        }

    }


    public void setSelection(int position) {
        final int p = position - offset;
        selectedIndex = position;
        this.post(new Runnable() {
            @Override
            public void run() {
                WheelView.this.scrollTo(0, p * itemHeight);
                onSelectedCallBack();
            }
        });
        refreshItemView(p * itemHeight);
    }


    public void setSelectionAndRefresh(int position) {
        final int p = position - offset;
        selectedIndex = position;
        this.post(new Runnable() {
            @Override
            public void run() {
                WheelView.this.smoothScrollTo(0, p * itemHeight);
                onSelectedCallBack();
            }
        });
        refreshItemView(p * itemHeight);
    }

    public String getSeletedItem() {
        return items.get(selectedIndex);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }


    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {

            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }


    private int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int getViewMeasuredHeight(View view) {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }


    public OnWheelViewListener getOnWheelViewListener() {
        return onWheelViewListener;
    }

    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
    }


    public interface OnWheelViewListener {
        void onSelected(int selectedIndex, String item);
    }

}
