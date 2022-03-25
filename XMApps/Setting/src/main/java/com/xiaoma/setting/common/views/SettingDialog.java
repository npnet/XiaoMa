package com.xiaoma.setting.common.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoma.setting.R;
import com.xiaoma.ui.dialog.OnViewClickListener;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.progress.loading.ProgressDrawable;
import com.xiaoma.utils.log.KLog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: iSun
 * @date: 2018/10/31 0031
 */
public class SettingDialog {

    public static XmDialog createProgressDialog(FragmentManager fragmentManager, Context context, String msg, int width, int height) {
        View view = View.inflate(context, R.layout.dialog_progress, null);
        TextView message = view.findViewById(R.id.message);
        message.setText(msg);
        XmDialog dialog = new XmDialog.Builder(fragmentManager).setDimAmount(0.3f).setView(view).setGravity(Gravity.CENTER).setHeight(height).setWidth(width).create();
        return dialog;
    }

    public static XmDialog createProgressDialog(FragmentManager fragmentManager, Context context, String msg) {
        int width = context.getResources().getDimensionPixelOffset(R.dimen.progress_dialog_width);
        int height = context.getResources().getDimensionPixelOffset(R.dimen.progress_dialog_height);
        return createProgressDialog(fragmentManager, context, msg, width, height);
    }

    public static XmDialog createConfirmDialog(FragmentManager fragmentManager, Context context, final View.OnClickListener leftOnClickListener, final View.OnClickListener rightOnClickListener) {
        View view = View.inflate(context, R.layout.dialog_confirm, null);
        XmDialog dialog = new XmDialog.Builder(fragmentManager).setView(view).setGravity(Gravity.CENTER).setHeight(240).setWidth(560).addOnClickListener(R.id.tv_left_button, R.id.tv_right_button)   //添加进行点击控件的id
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(View v, XmDialog tDialog) {
                        switch (v.getId()) {
                            case R.id.tv_left_button:
                                leftOnClickListener.onClick(v);
                                tDialog.dismiss();
                                break;
                            case R.id.tv_right_button:
                                rightOnClickListener.onClick(v);
                                tDialog.dismiss();
                                break;
                        }
                    }
                }).create();
        return dialog;
    }

//    public static XmDialog createTipsDialog(FragmentManager fragmentManager, Context context, String msg, int width, int height, Drawable image, final int time) {
//        View view = View.inflate(context, R.layout.dialog_tips, null);
//        DialogInterface.OnShowListener onShowListener = new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                dismiss(dialog, time);
//            }
//        };
//        TextView message = view.findViewById(R.id.message);
//        ImageView imageView = view.findViewById(R.id.image);
//        imageView.setBackground(image);
//        message.setText(msg);
//        XmDialog dialog = new XmDialog.Builder(fragmentManager).setView(view).setGravity(Gravity.CENTER).setOnShowListener(onShowListener).setHeight(height).setWidth(width).create();
//        return dialog;
//    }
//
//    public static XmDialog createTipsDialog(FragmentManager fragmentManager, Context context, String msg,int time) {
//        int width = context.getResources().getDimensionPixelOffset(R.dimen.progress_dialog_width);
//        int height = context.getResources().getDimensionPixelOffset(R.dimen.progress_dialog_height);
//        Drawable image = context.getResources().getDrawable(R.mipmap.seekbar_indicator);
//        return createTipsDialog(fragmentManager, context, msg, width, height, image,time);
//    }

    private static void dismiss(final DialogInterface dialog, int time) {
        if (dialog != null && time > 0) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, time);
        }
    }
}
