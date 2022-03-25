//package com.xiaoma.systemui.topbar.controller;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.drawable.Icon;
//import android.os.RemoteException;
//import android.os.UserHandle;
//import android.support.annotation.DrawableRes;
//import android.text.TextUtils;
//
//import com.android.internal.statusbar.IStatusBar;
//import com.android.internal.statusbar.StatusBarIcon;
//import com.xiaoma.systemui.R;
//
///**
// * Created by LKF on 2018/11/16 0016.
// */
//public class Test {
//    /**
//     * TODO 测试:设置通知栏图标
//     */
//    public static void setDebugIcons(Context context) {
//        final IStatusBar statusBar = TopBarController.getInstance().getStatusBar();
//        // 信号
//        try {
//            statusBar.setIcon("signal", makeIcon(context, R.drawable.icon_signal_car, context.getString(R.string.china_telecom)));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        // USB
//        try {
//            statusBar.setIcon("usb", makeIcon(context, R.drawable.icon_usb, null));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        // 蓝牙
//        try {
//            statusBar.setIcon("bluetooth", makeIcon(context, R.drawable.icon_bluetooth, null));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        // 天气
//        try {
//            statusBar.setIcon("weather", makeIcon(context, R.drawable.icon_weather, context.getString(R.string.test_temperature)));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static StatusBarIcon makeIcon(Context context, @DrawableRes int drawableRes, String text) {
//        final Bitmap bmp;
//        if (TextUtils.isEmpty(text)) {
//            bmp = BitmapFactory.decodeResource(context.getResources(), drawableRes);
//        } else {
//            final Paint paint = new Paint();
//            paint.setAntiAlias(true);
//            paint.setColor(Color.parseColor("#b5ccd4"));
//            paint.setTextSize(22);
//            bmp = drawWithTextBehind(context, drawableRes, text, paint);
//        }
//        final Icon icon = Icon.createWithBitmap(bmp);
//        return new StatusBarIcon(UserHandle.ALL, context.getPackageName(), icon, Integer.MAX_VALUE, 0, "");
//    }
//
//    private static Bitmap drawWithTextBehind(Context context, @DrawableRes int drawableRes, String text, Paint paint) {
//        final Bitmap bmpSource = BitmapFactory.decodeResource(context.getResources(), drawableRes);
//        final int textPaddingStart = 10;
//
//        final Bitmap bmpRet = Bitmap.createBitmap((int) (bmpSource.getWidth() + textPaddingStart + paint.measureText(text)), bmpSource.getHeight(), bmpSource.getConfig());
//        final Canvas canvas = new Canvas(bmpRet);
//        // 绘制Icon
//        canvas.drawBitmap(bmpSource, 0, 0, paint);
//        // 绘制文字
//        final Paint.FontMetrics fm = paint.getFontMetrics();
//        final float x = bmpSource.getWidth() + textPaddingStart;
//        final float y = (bmpRet.getHeight() - (fm.top + fm.bottom)) / 2;
//        canvas.drawText(text, x, y, paint);
//
//        bmpSource.recycle();
//
//        return bmpRet;
//    }
//}