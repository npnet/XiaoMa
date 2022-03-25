package com.xiaoma.launcher.mark.service;

import android.content.Context;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.google.android.cameraview.CameraView;


/**
 * 隐藏的全局窗口，用于后台拍照
 *
 * @author WuRS
 */
public class CameraWindow {

    private static final String TAG = CameraWindow.class.getSimpleName();

    private static WindowManager windowManager;

    private static Context applicationContext;

    private static CameraView mCamera;

    /**
     * 显示全局窗口
     *
     * @param context
     */
    public static void show(Context context) {
        if (applicationContext == null) {
            applicationContext = context.getApplicationContext();
            windowManager = (WindowManager) applicationContext
                    .getSystemService(Context.WINDOW_SERVICE);
            mCamera = new CameraView(applicationContext);
            LayoutParams params = new LayoutParams();
            params.width = 1;
            params.height = 1;
            params.alpha = 0;
            params.type = LayoutParams.TYPE_SYSTEM_ALERT;
            // 屏蔽点击事件
            params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE
                    | LayoutParams.FLAG_NOT_TOUCHABLE;
            windowManager.addView(mCamera, params);
        }
    }

    /**
     * @return 获取窗口视图
     */
    public static CameraView getDummyCameraView() {
        return mCamera;
    }

    /**
     * 隐藏窗口
     */
    public static void dismiss() {
        try {
            if (windowManager != null && mCamera != null) {
                applicationContext = null;
                windowManager.removeView(mCamera);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
