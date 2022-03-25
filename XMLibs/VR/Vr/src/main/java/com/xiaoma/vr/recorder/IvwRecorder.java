package com.xiaoma.vr.recorder;

import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.os.Process;

/**
 * Created by LKF
 * 2017/9/19 0019.
 * 唤醒录音器
 */
public class IvwRecorder extends BaseRecorder {
    @Override
    public int getRecordThreadPriority() {
        //优先级太低容易导致录音掉帧,唤醒困难
        //但是优先级过高可能导致CPU占用过高,抢占资源导致系统运行速度慢
        //因此将此值设置成介于默认优先级和后台优先级之间,平衡以上两种情况;当然,最终仍应根据实测的效果来调整
        return (Process.THREAD_PRIORITY_DEFAULT + Process.THREAD_PRIORITY_BACKGROUND) / 2;
    }

    @Override
    public void onStart() {
        super.onStart();
        /*AudioRecord recorder = getRecorder();
        if (isAECAvailable()) {
            AcousticEchoCanceler acousticEchoCanceler = AcousticEchoCanceler.create(recorder.getAudioSessionId());
            if (acousticEchoCanceler != null) {
                int resultCode = acousticEchoCanceler.setEnabled(true);
                if (AudioEffect.SUCCESS != resultCode) {
                    KLog.w("Do NOT support AcousticEchoCanceler");
                }
            }
        }
        if (isNSAvailable()) {
            NoiseSuppressor noiseSuppressor = NoiseSuppressor.create(recorder.getAudioSessionId());
            int resultCode = noiseSuppressor.setEnabled(true);
            if (AudioEffect.SUCCESS != resultCode) {
                KLog.w("Do NOT support NoiseSuppressor");
            }
        }*/
    }

    private static boolean isAECAvailable() {
        return runningOnJellyBeanOrHigher() && AcousticEchoCanceler.isAvailable();
    }

    private static boolean runningOnJellyBeanOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    private static boolean isNSAvailable() {
        return runningOnJellyBeanOrHigher() && NoiseSuppressor.isAvailable();
    }
}
