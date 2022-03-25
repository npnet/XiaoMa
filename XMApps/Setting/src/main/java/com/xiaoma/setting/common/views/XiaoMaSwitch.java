package com.xiaoma.setting.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.setting.R;

/**
 * @Author ZiXu Huang
 * @Data 2018/11/1
 */
public class XiaoMaSwitch extends RelativeLayout implements View.OnClickListener {


    private TextView left;
    private TextView right;
    private OnCheckChangeListener listener;

    public XiaoMaSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        View inflate = LayoutInflater.from(context).inflate(R.layout.xiao_ma_switch, this, true);
        initView(inflate);
        initEvent();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XiaoMaSwitch);

        if (typedArray != null) {
            int textSize = typedArray.getResourceId(R.styleable.XiaoMaSwitch_text_size, (int) getResources().getDimension(R.dimen.default_switch_text_size));
            left.setTextSize(getResources().getDimension(textSize));
            right.setTextSize(getResources().getDimension(textSize));

            int selectedColor = typedArray.getColor(R.styleable.XiaoMaSwitch_text_selected_color, getResources().getColor(R.color.switch_button_selected_color));
            int unselectedColor = typedArray.getColor(R.styleable.XiaoMaSwitch_text_unselected_color, getResources().getColor(R.color.switch_button_unselected_color));
            if (left.isSelected()) {
                left.setTextColor(selectedColor);
                right.setTextColor(unselectedColor);
            }
            if (right.isSelected()) {
                right.setTextColor(selectedColor);
                left.setTextColor(unselectedColor);
            }

            boolean leftSelected = typedArray.getBoolean(R.styleable.XiaoMaSwitch_is_turn_on, false);
            left.setSelected(leftSelected);
            right.setSelected(!leftSelected);


            String leftText = typedArray.getString(R.styleable.XiaoMaSwitch_left_text);
            left.setText(leftText);
            String rightText = typedArray.getString(R.styleable.XiaoMaSwitch_right_text);
            right.setText(rightText);
            typedArray.recycle();
        }

    }

    private void initEvent() {
        left.setOnClickListener(this);
        right.setOnClickListener(this);
    }

    private void initView(View inflate) {
        left = inflate.findViewById(R.id.left);
        right = inflate.findViewById(R.id.right);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.left) {
            if (!left.isSelected()) {
                if (listener != null) {
                    listener.onCheckedChanged(true);
                }
                left.setSelected(true);
                right.setSelected(false);
            }
        } else if (v.getId() == R.id.right) {
            if (!right.isSelected()) {
                if (listener != null) {
                    listener.onCheckedChanged(false);
                }
                right.setSelected(true);
                left.setSelected(false);
            }
        }
    }

    public void setChecked(boolean isChecked) {
        left.setSelected(isChecked);
        right.setSelected(!isChecked);
    }

    public void setOnCheckChangedListener(OnCheckChangeListener listener) {
        this.listener = listener;
    }

    public interface OnCheckChangeListener {
        void onCheckedChanged(boolean isChecked);
    }
}
