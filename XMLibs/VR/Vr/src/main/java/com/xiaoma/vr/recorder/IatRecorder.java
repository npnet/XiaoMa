package com.xiaoma.vr.recorder;

import android.os.Process;

/**
 * Created by LKF
 * 2017/9/19 0019.
 * 语义识别录音器
 */

public class IatRecorder extends BaseRecorder {
    @Override
    public int getRecordThreadPriority() {
        //标准Audio优先级
        return Process.THREAD_PRIORITY_AUDIO;
    }
}
