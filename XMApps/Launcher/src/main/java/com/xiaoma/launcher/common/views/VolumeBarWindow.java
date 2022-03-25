package com.xiaoma.launcher.common.views;

import android.annotation.SuppressLint;
import android.car.media.ICarVolumeCallback;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.wheel.WheelManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

public class VolumeBarWindow {
    private static final String TAG = "[VolumeBarWindow]";
    public static boolean isShow = false;
    private static VolumeBarWindow naviBarWindow = new VolumeBarWindow();
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private View volumeView;
    private TextTackSeekBar volumeSeekBar;
    private TextView volumeText;
    private TextView volumeMinText;
    private TextView tvVolumeType;
    private ImageView ivMute;
    private Context context;
    private boolean isInit;
    private int volumeGroupId;
    private Runnable dismissRunnable = new Runnable() {
        @Override
        public void run() {
            hideContent();
        }
    };

    private final IBinder volumeBinder = new ICarVolumeCallback.Stub() {
        @Override
        public void onGroupVolumeChanged(int i, int i1) throws RemoteException {
            KLog.i(TAG, "onGroupVolumeChanged() i=" + i + " , i1=" + i1);
            //update();
        }

        @Override
        public void onMasterMuteChanged(int i) throws RemoteException {
            KLog.i(TAG, "onGroupVolumeChanged() i=" + i);
            update();
        }

        /**
         *用于负一屏展示当前音频通道的音量
         * 发生音频通道切换时系统发出通知,需要通知切换后的音频通道类型
         *
         * 用于负一屏展示当前音频通道的音量
         * 发生音量变化时系统发出通知,需要通知音量发生变化的音频通道类型与变化后的音量
         * @param i     音量类型
         * @param i1    音量大小
         * @param i2    暂时不用
         * @throws RemoteException
         */
        @Override
        public void onCarVolumeChanged(int i, int i1, int i2) throws RemoteException {
            KLog.i(TAG, "onGroupVolumeChanged() i=" + i + " , i1=" + i1 + " , i2=" + i2);
            update();
        }
    };

    private VolumeBarWindow() {

    }

    public static VolumeBarWindow getVolumeWindow() {
        return naviBarWindow;
    }

    public synchronized void init(Context context) {
        if (context == null || this.context != null) {
            return;
        }
        if (isInit) {
            return;
        }
        Log.i(TAG, "VolumeBarWindow init()");
        this.context = context;
        createNaviBarWindow();
        isInit = true;
    }

    private boolean update() {
        try {
            if (null != WheelManager.getInstance().getCarAudioManager()) {
                boolean isMute = WheelManager.getInstance().getCarAudioManager().isMasterMute();
                int volume = WheelManager.getInstance().getGroupVolume(volumeGroupId);
                ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        setVolume(volumeGroupId, volume, isMute);
                    }
                });
                return true;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    @SuppressLint("InflateParams")
    private void createNaviBarWindow() {
        Log.i(TAG, "VolumeBarWindow createNaviBarWindow()");
        windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ConfigManager.ApkConfig.isCarPlatform()) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_KEYGUARD;
        }
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 682;
        params.y = 268;
        params.width = (int) dp2px(557);
        params.height = (int) dp2px(213);
        volumeView = LayoutInflater.from(this.context).inflate(R.layout.layout_volumebar_window, null);
        volumeSeekBar = volumeView.findViewById(R.id.media_volume_seekbar);
        volumeText = volumeView.findViewById(R.id.media_volume_value);
        volumeMinText = volumeView.findViewById(R.id.media_volume_min_value);
        tvVolumeType = volumeView.findViewById(R.id.tv_volume_type);
        ivMute = volumeView.findViewById(R.id.iv_mute);
        volumeSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        volumeView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    KLog.i(TAG, "outside");
                    if (isShow) {
                        //如果添加了window，就移除window
                        ThreadDispatcher.getDispatcher().postOnMainDelayed(dismissRunnable, 3000);
                    }
                }
                KLog.i(TAG, "x:" + arg1.getX());
                KLog.i(TAG, "y:" + arg1.getY());
                return false;
            }
        });
        volumeView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    }

    public void showVolumeWindow() {
        KLog.d(TAG, "showVolumeWindow");
        ThreadDispatcher.getDispatcher().removeOnMain(dismissRunnable);
        ThreadDispatcher.getDispatcher().postOnMainDelayed(dismissRunnable, 3000);
        if (isShow) {
            return;
        }
        isShow = true;
        try {
            if (null != WheelManager.getInstance().getCarAudioManager()) {
                WheelManager.getInstance().getCarAudioManager().registerVolumeCallback(volumeBinder);
                volumeGroupId = WheelManager.getInstance().getCarVolumeGroupId();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (update()) {
            if (volumeView.isAttachedToWindow()) {
                KLog.d(TAG, "updateViewLayout");
                windowManager.updateViewLayout(volumeView, params);
            } else {
                KLog.d(TAG, "addView");
                windowManager.addView(volumeView, params);
            }
        } else {
            hideContent();
        }
    }

    //隐藏
    private synchronized void hideContent() {
        Log.i(TAG, "VolumeBarWindow startAnimationLeftOut()");
        try {
            if (null != WheelManager.getInstance().getCarAudioManager()) {
                WheelManager.getInstance().getCarAudioManager().unregisterVolumeCallback(volumeBinder);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (volumeView.isAttachedToWindow()) {
            windowManager.removeView(volumeView);
        }
        isShow = false;
    }

    private void setVolume(int type, int volume, boolean mute) {

        if (type == LauncherConstants.PHONE_VOLUME) {
            tvVolumeType.setText(R.string.phone_volume);
            ivMute.setVisibility(View.INVISIBLE);
        } else if (type == LauncherConstants.TTS_VOLUME) {
            tvVolumeType.setText(R.string.tts_volume);
            ivMute.setVisibility(View.INVISIBLE);
        } else if (type == LauncherConstants.BT_MEDIA_VOLUME) {
            tvVolumeType.setText(R.string.bl_volume);
            ivMute.setVisibility(mute ? View.VISIBLE : View.INVISIBLE);
        } else if (type == LauncherConstants.MEDIA_VOLUME) {
            tvVolumeType.setText(R.string.media_volume);
            ivMute.setVisibility(mute ? View.VISIBLE : View.INVISIBLE);
        }


        if (type == LauncherConstants.PHONE_VOLUME) {
            volumeSeekBar.setMin(1);
            volumeMinText.setText("1");
        } else {
            volumeSeekBar.setMin(0);
            volumeMinText.setText("0");
        }
        if (type == LauncherConstants.TTS_VOLUME) {
            volumeSeekBar.setMax(8);
            volumeText.setText("8");
        } else {
            volumeSeekBar.setMax(40);
            volumeText.setText("40");
        }
        volumeSeekBar.setProgress(volume);
    }

    public float dp2px(float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }
}
