package com.xiaoma.club.msg.chat.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.xiaoma.club.R;
import com.xiaoma.club.common.util.LogUtil;

/**
 * Created by LKF on 2019-1-4 0004.
 */
public class ImageDisplayPopup extends PopupWindow {
    private static final String TAG = "ImageDisplayPopup";

    public ImageDisplayPopup(Context context, int width, int height, String imgUri) {
        super(width, height);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        setFocusable(true);
        setClippingEnabled(false);

        final View view = View.inflate(context, R.layout.fmt_img_display, null);
        setContentView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        try {
            final ImageView imgDisplay = view.findViewById(R.id.img_display);
            Glide.with(context)
                    .load(imgUri)
                    .into(imgDisplay);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.logI(TAG, "ImageDisplayDlg(imgUri: %s)", imgUri);
    }
}
