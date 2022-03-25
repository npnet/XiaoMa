package com.xiaoma.systemui.bussiness.barstatus;

import android.car.Car;
import android.car.media.CarAudioManager;
import android.car.media.ICarVolumeCallback;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.systemui.R;
import com.xiaoma.systemui.bussiness.BarUtil;
import com.xiaoma.systemui.common.controller.CarConnector;
import com.xiaoma.systemui.common.controller.OnConnectStateListener;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.topbar.controller.TopBarController;

/**
 * Created by LKF on 2019-3-6 0006.
 */
public class VolumeBarStatus implements BarStatus {
    private static final String TAG = "VolumeBarStatus";
    private int mVolume = -1;
    private Boolean mIsMute = null;
    private int mVolumeGroupId = -1;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mUpdateVolTask;
    private CarAudioManager mAudioManager;
    private final ICarVolumeCallback.Stub mVolumeCallback = new ICarVolumeCallback.Stub() {
        @Override
        public void onGroupVolumeChanged(int groupId, int flags) {
            LogUtil.logI(TAG, "onGroupVolumeChanged ( groupId: %s, flags: %s )", groupId, flags);
            updateVolume();
        }

        @Override
        public void onMasterMuteChanged(int flags) {
            LogUtil.logI(TAG, "onMasterMuteChanged ( flags: %s )", flags);
            updateVolume();
        }

        @Override
        public void onCarVolumeChanged(int volume, int groupId, int flags) {
            LogUtil.logI(TAG, "onCarVolumeChanged ( volume: %s, groupId: %s, flags: %s )",
                    volume, groupId, flags);
            updateVolume();
        }
    };

    @Override
    public void startup(final Context context, final int iconLevel) {
        mUpdateVolTask = new Runnable() {
            @Override
            public void run() {
                update(context, iconLevel);
            }
        };
        CarConnector.getInstance().registerConnectListener(new OnConnectStateListener<Car>() {
            @Override
            public void onConnected(@NonNull Car car) {
                try {
                    mAudioManager = (CarAudioManager) car.getCarManager(Car.AUDIO_SERVICE);
                    mAudioManager.registerVolumeCallback(mVolumeCallback);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                updateVolume();
            }

            @Override
            public void onDisconnected() {
                if (mAudioManager != null) {
                    try {
                        mAudioManager.unregisterVolumeCallback(mVolumeCallback);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    mAudioManager = null;
                }
            }
        });
    }

    private void postUpdate() {
        mHandler.removeCallbacks(mUpdateVolTask);
        mHandler.post(mUpdateVolTask);
    }

    private void updateVolume() {
        CarAudioManager mgr = mAudioManager;
        if (mgr == null)
            return;
        try {
            mIsMute = mgr.isMasterMute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            mVolume = mgr.getGroupVolume(mVolumeGroupId = mgr.getCarVolumeGroupId());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        postUpdate();
    }

    private void update(Context context, int iconLevel) {
        if (mVolume < 0 || mVolumeGroupId < 0 || mIsMute == null) {
            try {
                TopBarController.getInstance()
                        .getStatusBar()
                        .removeIcon(TAG);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return;
        }
        final @DrawableRes int res;
        final boolean drawVolume;
        if (mVolume == 0) {
            drawVolume = false;
            res = R.drawable.status_icon_volume_quiet;
        } else if (mIsMute) {
            // 只有媒体才能静音(媒体+蓝牙音乐)
            if (SDKConstants.MEDIA_VOLUME == mVolumeGroupId || SDKConstants.BT_MEDIA_VOLUME == mVolumeGroupId) {
                drawVolume = false;
                res = R.drawable.status_icon_volume_quiet;
            } else {
                drawVolume = true;
                res = R.drawable.status_icon_volume_normal;
            }
        } else {
            drawVolume = true;
            res = R.drawable.status_icon_volume_normal;
        }
        final Drawable dr = context.getDrawable(res);
        if (dr == null)
            return;
        final int exWidth = 10;
        final Bitmap bmp = Bitmap.createBitmap(dr.getIntrinsicWidth() + exWidth, dr.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
        dr.draw(canvas);

        if (drawVolume) {
            final Paint paint = BarUtil.getTextPaint(context);
            paint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.status_bar_volume_text));
            final Paint.FontMetrics fm = paint.getFontMetrics();
            String text = String.valueOf(mVolume);
            if (text.length() == 1) {
                // 如果音量值只有1位,加一个空格来占位
                text += " ";
            }
            final float x = bmp.getWidth() - paint.measureText(text);
            final float y = (bmp.getHeight() - (fm.top + fm.bottom)) / 2;
            canvas.drawText(text, x, y, paint);
        }
        try {
            TopBarController.getInstance().getStatusBar().setIcon(TAG, BarUtil.makeIcon(context, bmp, iconLevel));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
