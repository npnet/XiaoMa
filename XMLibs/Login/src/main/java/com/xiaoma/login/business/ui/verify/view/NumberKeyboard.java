package com.xiaoma.login.business.ui.verify.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.xiaoma.login.R;
import com.xiaoma.model.annotation.SingleClick;


/**
 * @author KY
 * @date 11/20/2018
 */
public class NumberKeyboard extends ConstraintLayout implements View.OnClickListener {

    private Button number1;
    private Button number2;
    private Button number3;
    private Button number4;
    private Button number5;
    private Button number6;
    private Button number7;
    private Button number8;
    private Button number9;
    private Button number0;
    private onKeyEventListener onKeyEventListener;

    public interface onKeyEventListener {
        void onKey(int number);

        void onBackspace();
    }

    public NumberKeyboard(Context context) {
        this(context, null);
    }

    public NumberKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_number_keyboard, this, true);
        number1 = findViewById(R.id.number_1);
        number2 = findViewById(R.id.number_2);
        number3 = findViewById(R.id.number_3);
        number4 = findViewById(R.id.number_4);
        number5 = findViewById(R.id.number_5);
        number6 = findViewById(R.id.number_6);
        number7 = findViewById(R.id.number_7);
        number8 = findViewById(R.id.number_8);
        number9 = findViewById(R.id.number_9);
        number0 = findViewById(R.id.number_0);

        number1.setOnClickListener(this);
        number2.setOnClickListener(this);
        number3.setOnClickListener(this);
        number4.setOnClickListener(this);
        number5.setOnClickListener(this);
        number6.setOnClickListener(this);
        number7.setOnClickListener(this);
        number8.setOnClickListener(this);
        number9.setOnClickListener(this);
        number0.setOnClickListener(this);
    }


    public void setOnKeyEventListener(onKeyEventListener listener) {
        this.onKeyEventListener = listener;
    }

    @Override
    @SingleClick(0)
    public void onClick(View v) {
        int i = v.getId();
        if (onKeyEventListener == null){
            //不允许回调
            return;
        }
        if (i == R.id.number_1) {
            onKeyEventListener.onKey(1);
        } else if (i == R.id.number_2) {
            onKeyEventListener.onKey(2);
        } else if (i == R.id.number_3) {
            onKeyEventListener.onKey(3);
        } else if (i == R.id.number_4) {
            onKeyEventListener.onKey(4);
        } else if (i == R.id.number_5) {
            onKeyEventListener.onKey(5);
        } else if (i == R.id.number_6) {
            onKeyEventListener.onKey(6);
        } else if (i == R.id.number_7) {
            onKeyEventListener.onKey(7);
        } else if (i == R.id.number_8) {
            onKeyEventListener.onKey(8);
        } else if (i == R.id.number_9) {
            onKeyEventListener.onKey(9);
        } else if (i == R.id.number_0) {
            onKeyEventListener.onKey(0);
        }  else {
            //Nothing to do
        }
    }
}
