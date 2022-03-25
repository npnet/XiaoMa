package com.xiaoma.vr.recorder;

/**
 * Created by LKF
 * 2017/9/20 0020.
 */

class RecorderFactory {
    static BaseRecorder createRecorder(RecorderType type) {
        if (type == null)
            return null;
        BaseRecorder r = null;
        switch (type) {
            case IVW:
                r = new IvwRecorder();
                break;
            case IAT:
                r = new IatRecorder();
                break;
        }
        return r;
    }
}
