package com.xiaoma.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.ui.R;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author KY
 * @date 11/23/2018
 */
public class UnderLineTextView extends LinearLayout {
    private static final int DEF_LENGTH = 6;
    private static final int DEF_TEXT_SIZE = 28;
    private static final int DEF_LINE_WIDTH = 90;
    private static final int DEF_LINES_MARGIN = 16;
    private static final int DEF_LINE_TEXT_MARGIN = 10;
    private static final int DEF_INPUT_TYPE_NORMAL = 0;
    private static final int DEF_INPUT_TYPE_PASSWD = 1;

    private enum RefreshFrom {
        SET,
        INPUT,
        BACKSPACE,
    }

    private int mLength;
    private int mTextSize;
    private int mTextColor;
    private int mLineWidth;
    private int mLineColor;
    private int mLinesMargin;
    private int mLineTextMargin;
    private HideTask mCurrentHideTask;
    private PasswdStack mCharStack;
    /**
     * 0 普通显示模式， 1-密码输入模式
     */
    private int mInputType;
    private TextView[] textViews;


    private OnContentLengthChangeCallback onContentLengthChangeCallback;

    public UnderLineTextView(Context context) {
        this(context, null);
    }

    public UnderLineTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnderLineTextView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.UnderLineTextView, 0, 0);
        mLength = attributes.getInt(R.styleable.UnderLineTextView_length, DEF_LENGTH);
        mTextSize = attributes.getDimensionPixelSize(R.styleable.UnderLineTextView_text_size_ui, DEF_TEXT_SIZE);
        mInputType = attributes.getInt(R.styleable.UnderLineTextView_input_type, DEF_INPUT_TYPE_NORMAL);
        mTextColor = attributes.getColor(R.styleable.UnderLineTextView_text_color, Color.WHITE);
        mLineWidth = attributes.getDimensionPixelSize(R.styleable.UnderLineTextView_line_width, DEF_LINE_WIDTH);
        mLineColor = attributes.getColor(R.styleable.UnderLineTextView_line_color, Color.LTGRAY);
        mLinesMargin = attributes.getDimensionPixelSize(R.styleable.UnderLineTextView_lines_margin, DEF_LINES_MARGIN);
        mLineTextMargin = attributes.getDimensionPixelSize(R.styleable.UnderLineTextView_line_text_margin, DEF_LINE_TEXT_MARGIN);
        attributes.recycle();
        setOrientation(HORIZONTAL);
        initView();
        initData();
    }

    private void initView() {
        textViews = new TextView[mLength];

        for (int i = 0; i < mLength; i++) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_unserline_text, this, false);
            TextView tvText = view.findViewById(R.id.tvText);
            View underline = view.findViewById(R.id.underline);

            tvText.setTextColor(mTextColor);
            tvText.setTextSize(mTextSize);
            underline.setBackgroundColor(mLineColor);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) underline.getLayoutParams();
            layoutParams.width = mLineWidth;
            layoutParams.setMargins(mLinesMargin / 2, mLineTextMargin, mLinesMargin / 2, 0);
            textViews[i] = tvText;
            addView(view);
        }
    }

    private void initData() {
        mCharStack = new PasswdStack(mLength);
        refreshTexts(DEF_INPUT_TYPE_NORMAL, RefreshFrom.SET);
    }

    private void refreshTexts(int inputType, RefreshFrom from) {
        for (int i = 0; i < mLength; i++) {
            if (mInputType == DEF_INPUT_TYPE_PASSWD) {
                int top = mCharStack.getTop();
                if (i == top) {
                    if (from == RefreshFrom.INPUT) {
                        textViews[i].setText(mCharStack.get(i).toString());
                    } else if (from == RefreshFrom.BACKSPACE
                            || from == RefreshFrom.SET) {
                        textViews[i].setText("*");
                    }
                } else if (i > top) {
                    textViews[i].setText(" ");
                } else {
                    textViews[i].setText("*");
                }
            } else {
                textViews[i].setText(mCharStack.get(i).toString());
            }
        }

        if (inputType == DEF_INPUT_TYPE_PASSWD
                && from == RefreshFrom.INPUT) {
            if (mCurrentHideTask != null && !mCurrentHideTask.getDone()) {
                removeCallbacks(mCurrentHideTask);
                mCurrentHideTask.run();
            }
            mCurrentHideTask = new HideTask(mCharStack.getTop());
            postDelayed(mCurrentHideTask, 300);
        } else if (from == RefreshFrom.BACKSPACE) {
            if (mCurrentHideTask != null && !mCurrentHideTask.getDone()) {
                removeCallbacks(mCurrentHideTask);
                mCurrentHideTask = null;
            }
        }
    }

    public void setString(String str) {
        if (TextUtils.isEmpty(str)) {
            mCharStack.empty();
        } else if (str.length() <= mLength) {
            mCharStack.valueOf(str);
        } else {
            throw new RuntimeException("输入字符串长度大于设定长度");
        }
        refreshTexts(DEF_INPUT_TYPE_NORMAL, RefreshFrom.SET);
    }

    public String getString() {
        return mCharStack.getPasswd();
    }

    public void put(Character character) {
        if (!mCharStack.isFull()) {
            mCharStack.push(character);
            refreshTexts(mInputType, RefreshFrom.INPUT);
        }
    }

    public void backspace() {
        if (!mCharStack.isEmpty()) {
            mCharStack.backspace();
            refreshTexts(mInputType, RefreshFrom.BACKSPACE);
        }
    }

    public void empty() {
        mCharStack.empty();
        removeCallbacks(mCurrentHideTask);
        mCurrentHideTask = null;
        refreshTexts(DEF_INPUT_TYPE_NORMAL, RefreshFrom.SET);
    }


    private void recordContentLength(int length) {
        if (onContentLengthChangeCallback != null) {
            onContentLengthChangeCallback.contentLengthChange(length);
        }
    }


    public void addContentLengthChangeCallback(OnContentLengthChangeCallback callback) {
        this.onContentLengthChangeCallback = callback;
    }


    public interface OnContentLengthChangeCallback {
        void contentLengthChange(int length);
    }


    class PasswdStack implements Serializable {

        private final int capacity;
        private final Character[] array;
        private int top;

        public PasswdStack(int capacity) {
            this.capacity = capacity;
            array = new Character[capacity];
            for (int i = 0; i < capacity; i++) {
                array[i] = ' ';
            }
            top = -1;
        }

        /**
         * 判断是否为空栈
         *
         * @return 是否为空栈
         */
        public boolean isEmpty() {
            return top == -1;
        }

        public int getTop() {
            return top;
        }

        /**
         * 判断是否为满栈
         *
         * @return 是否为满栈
         */
        public boolean isFull() {
            return (top + 1) == capacity;
        }

        public Character get(int index) {
            return array[index];
        }

        /**
         * 压栈操作，模拟输入
         *
         * @param passwdChar 密码字符
         */
        public void push(Character passwdChar) {
            if (!isFull()) {
                top++;
                this.array[top] = passwdChar;
                recordContentLength(top + 1);
            }
        }


        /**
         * 出栈操作，模拟回退
         */
        public void backspace() {
            if (!isEmpty()) {
                this.array[top] = ' ';
                top--;
                recordContentLength(top + 1);
            }
        }

        /**
         * 设置为String内容
         *
         * @param str str
         */
        public void valueOf(String str) {
            for (int i = 0; i < capacity; i++) {
                if (i < str.length()) {
                    array[i] = str.charAt(i);
                } else {
                    array[i] = ' ';
                }
            }
            top = str.length() - 1;
        }

        /**
         * 清空
         */
        public void empty() {
            for (int i = 0; i < capacity; i++) {
                array[i] = ' ';
            }
            top = -1;
            recordContentLength(0);
        }

        /**
         * 返回Character集合
         *
         * @return
         */
        public List<Character> getCharList() {
            return Arrays.asList(array);
        }

        /**
         * 获取密码字符串
         *
         * @return 密码字符串
         */
        public String getPasswd() {
            StringBuilder builder = new StringBuilder();
            for (Character s : array) {
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    builder.append(s);
                }
            }
            return builder.toString();
        }
    }

    class HideTask implements Runnable {
        private boolean done;
        private int top;

        public HideTask(int top) {
            this.top = top;
        }

        boolean getDone() {
            return done;
        }

        @Override
        public void run() {
            textViews[top].setText("*");
            done = true;
        }
    }
}
