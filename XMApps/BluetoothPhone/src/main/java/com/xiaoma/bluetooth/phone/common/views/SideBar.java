package com.xiaoma.bluetooth.phone.common.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.bluetooth.phone.R;

import java.util.ArrayList;
import java.util.List;


/**
 * wutao
 */
public class SideBar extends View {

    // 触摸事件
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    // 26个字母
    public static String[] letters = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    public static final int[] colors = {
            0xff1abc9c, 0xffa94136
    };

    private List<Float> heightList = new ArrayList<>();
    private int choose = -1;// 选中
    private Paint paint = new Paint();

    private TextView mTextDialog;

    private Context mContext;
    private float singleHeight;
    private int specialTextColor;
    private int[] coloredIndexs;

    /**
     * 为SideBar设置显示字母的TextView
     */
    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public SideBar(Context context) {
        super(context);
        mContext = context;
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取焦点改变背景颜色.
        int height = getHeight();// 获取对应高度
        int width = getWidth(); // 获取对应宽度
        // 获取每一个字母的高度
        singleHeight = height / (letters.length + 0f);
        heightList.clear();
        for (int i = 0; i < letters.length; i++) {
            if (coloredIndexs != null && coloredIndexs[i] == 0) {
                paint.setColor(specialTextColor);
            } else {
                paint.setColor(getResources().getColor(R.color.side_bar_text));
            }
            paint.setTypeface(Typeface.DEFAULT);
            paint.setAntiAlias(true);
            paint.setTextSize(14);
            // 选中的状态
           /* if (i == choose) {
                paint.setColor(getResources().getColor(R.color.side_bar_select_color));
                paint.setFakeBoldText(true);
            }*/
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - paint.measureText(letters[i]) / 2;
            float centerY = singleHeight * i + singleHeight / 2;
            float yPos = getBaseLine(centerY, paint);
            heightList.add(centerY);
//            Paint paint = new Paint();
//            paint.setColor(colors[i % 2 == 0 ? 0 : 1]);
//            canvas.drawRect(new Rect(0, (int) (singleHeight * i), width, (int) (singleHeight * i + singleHeight)), paint);
            canvas.drawText(letters[i], xPos, yPos, paint);
            this.paint.reset();// 重置画笔
        }

    }

    private float getBaseLine(float centerLine, Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return centerLine + (fontMetrics.bottom - fontMetrics.top) / 2.0F - fontMetrics.bottom;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / singleHeight);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

        switch (action) {
            case MotionEvent.ACTION_UP:
//                setBackground(new ColorDrawable(0x00000000));
                choose = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;

            default:
//                setBackgroundResource(R.drawable.sidebar_background);
                if (oldChoose != c) {
                    if (c >= 0 && c < letters.length) {
                        if (listener != null) {
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
                            int topMargin = layoutParams.topMargin;
                            listener.onTouchingLetterChanged(letters[c], heightList.get(c) + topMargin);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(letters[c]);

                            mTextDialog.setVisibility(View.VISIBLE);
                        }

                        choose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s, float y);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void changeTextColorByIndexs(int color, int[] indexs){
        specialTextColor = color;
        coloredIndexs = indexs;
        invalidate();
    }
}
