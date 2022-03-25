package com.xiaoma.assistant.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.xiaoma.assistant.R;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.receiver.UsbDetector;

import java.util.List;

/**
 * @author taojin
 * @date 2019/5/6
 */
public class ScreenDialog extends Dialog {
    private final String TAG = this.getClass().getSimpleName();
    private GestureDetector mGestureDetector;
    private Context mContext;

    public ScreenDialog(@NonNull Context context) {
        this(context, R.style.screen_dialog);
    }

    public ScreenDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenDialogSize();
        setContentView(R.layout.dialog_screen);

        findViewById(R.id.screen_view).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

        UsbDetector.getInstance().init(mContext);
        UsbDetector.getInstance().addUsbDetectListener(mUsbDetectListener);

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            /**
             * 发生确定的单击时执行
             * @param e
             * @return
             */
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {//单击事件
                return super.onSingleTapConfirmed(e);
            }

            /**
             * 双击发生时的通知
             * @param e
             * @return
             */
            @Override
            public boolean onDoubleTap(MotionEvent e) {//双击事件
                KLog.e(TAG, "double click");
                if (isShowing()) {
                    dismiss();
                }
                return super.onDoubleTap(e);
            }
        });
    }

    @Override
    public void show() {
        super.show();
        KLog.e(TAG, "closeScreen");
        XmCarVendorExtensionManager.getInstance().closeScreen();
//        if (XmCarVendorExtensionManager.getInstance().getScreenStatus()) {
//            KLog.d(TAG, "closeScreen");
//            XmCarVendorExtensionManager.getInstance().closeScreen();
//        }
    }

    private void setFullScreenDialogSize() {

        Window window = getWindow();
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ConfigManager.ApkConfig.isCarPlatform()) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        //window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        KLog.e(TAG, "turnOnScreen");
        XmCarVendorExtensionManager.getInstance().turnOnScreen();
//        if (!XmCarVendorExtensionManager.getInstance().getScreenStatus()) {
//            KLog.d(TAG, "turnOnScreen");
//            XmCarVendorExtensionManager.getInstance().turnOnScreen();
//        }
    }

    private UsbDetector.UsbDetectListener mUsbDetectListener = new UsbDetector.UsbDetectListener() {
        @Override
        public void noUsbMounted() {

        }

        @Override
        public void inserted() {
//            XMToast.showToast(AppHolder.getInstance().getAppContext(),"USB CONNECTED");
            KLog.e(TAG, "USB CONNECTED");
            if (isShowing()) {
                dismiss();
            } else if (!XmCarVendorExtensionManager.getInstance().getScreenStatus()) {
                KLog.e(TAG, "turnOnScreen");
                XmCarVendorExtensionManager.getInstance().turnOnScreen();

            }
        }

        @Override
        public void mounted(List<String> mountPaths) {

        }

        @Override
        public void mountError() {

        }

        @Override
        public void removed() {

        }
    };
}
