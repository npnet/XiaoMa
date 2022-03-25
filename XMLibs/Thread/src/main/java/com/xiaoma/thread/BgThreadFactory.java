package com.xiaoma.thread;

import android.os.Process;

/**
 * Created by LKF on 2018/1/12 0012.
 * 后台线程优先级的ThreadFactory
 */
public class BgThreadFactory extends PriorityThreadFactory {
    @Override
    protected int getThreadPriority() {
        return Process.THREAD_PRIORITY_BACKGROUND;
    }
}