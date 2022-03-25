package com.xiaoma.launcher.common.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.launcher.R;

/**
 * @author taojin
 * @date 2019/1/2
 */
public class HotelView extends FrameLayout {

    private ImageView ivHotel;
    private TextView tvHotel;
    private Button btnOperationLeft;
    private Button btnOperationRight;

    public HotelView(@NonNull Context context) {
        super(context);
        initView();
    }

    public HotelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HotelView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        inflate(getContext(), R.layout.view_hotel, this);
        ivHotel = findViewById(R.id.iv_hotel);
        tvHotel = findViewById(R.id.tv_hotel);
        btnOperationLeft = findViewById(R.id.btn_hotel_left);
        btnOperationRight = findViewById(R.id.btn_hotel_right);
    }

    public void setBtnText(String leftText, String rightText) {
        btnOperationLeft.setText(leftText);
        btnOperationRight.setText(rightText);
    }

    public void setTextView(String text) {
        tvHotel.setText(text);
    }
}
