package com.xiaoma.club.msg.chat.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.PopupWindow;

import com.xiaoma.club.R;

/**
 * Created by LKF on 2018/10/11 0011.
 */
class SendLocationPopup extends PopupWindow implements View.OnClickListener {
    private Callback mCallback;

    SendLocationPopup(Context context) {
        super(context);
        final View v = View.inflate(context, R.layout.popup_send_location, null);
        setContentView(v);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setFocusable(true);
        setOutsideTouchable(false);

        v.findViewById(R.id.btn_popup_send_location).setOnClickListener(this);
        v.findViewById(R.id.btn_popup_share_location).setOnClickListener(this);

    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void onClick(View v) {
        if (mCallback != null) {
            switch (v.getId()) {
                case R.id.btn_popup_send_location:
                    mCallback.onSendLocationClick(this, v);
                    break;
                case R.id.btn_popup_share_location:
                    mCallback.onShareLocationClick(this, v);
                    break;
            }
        }
    }

    public interface Callback {
        void onSendLocationClick(PopupWindow win, View v);

        void onShareLocationClick(PopupWindow win, View v);
    }
}
