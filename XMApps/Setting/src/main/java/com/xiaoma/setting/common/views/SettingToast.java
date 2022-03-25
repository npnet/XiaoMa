package com.xiaoma.setting.common.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoma.setting.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: iSun
 * @date: 2018/10/29 0029
 */
public class SettingToast extends Toast {
    private static final long PERIOD = 3000;
    private Context context;
    private String msg = "";
    private TextView tvMsg;
    private ImageView ivImage;
    private Drawable image;

    public SettingToast(Context context) {
        super(context);
        initView(context);
    }

    public static SettingToast build(Context context) {
        return new SettingToast(context);
    }

    private void initView(Context context) {
        this.context = context;
        View toast = LayoutInflater.from(context).inflate(R.layout.view_setting_toast, null);
        tvMsg = toast.findViewById(R.id.message);
        ivImage = toast.findViewById(R.id.image);
        if (!TextUtils.isEmpty(msg)) {
            tvMsg.setText(msg);
        }
        if (image != null) {
            ivImage.setImageDrawable(image);
            ivImage.setVisibility(View.VISIBLE);
        }
        setDuration(Toast.LENGTH_LONG);
        setView(toast);
        setGravity(Gravity.CENTER, 0, 0);
    }


    @Override
    public void setView(View view) {
        super.setView(view);
    }

    public SettingToast setMessage(String msg) {
        this.msg = msg;
        if (tvMsg != null && !TextUtils.isEmpty(msg)) {
            tvMsg.setText(msg);
        }
        return this;
    }

    public SettingToast setMessage(int msgId) {
        this.msg = context.getResources().getString(msgId);
        if (tvMsg != null && !TextUtils.isEmpty(msg)) {
            tvMsg.setText(msg);
        }
        return this;
    }


    public SettingToast setImage(Drawable drawable) {
        image = drawable;
        if (image != null) {
            ivImage.setImageDrawable(image);
            ivImage.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public SettingToast setImage(int drawable) {
        image = context.getResources().getDrawable(drawable);
        if (image != null) {
            ivImage.setImageDrawable(image);
            ivImage.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public void show(int cnt) {
        final Toast toast = this;
        final Timer timer = new Timer();
        toast.setDuration(Toast.LENGTH_LONG);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, PERIOD);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt);
    }

}
