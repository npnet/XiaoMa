package com.xiaoma.component.dispatch;

/**
 * @author youthyJ
 * @date 2019-07-20
 */
public class SimulateWheelDispatch {
    private static SimulateWheelDispatch instance;
    private OnWheelEvent impl;

    private SimulateWheelDispatch() {
    }

    public static SimulateWheelDispatch getInstance() {
        if (instance == null) {
            synchronized (SimulateWheelDispatch.class) {
                if (instance == null) {
                    instance = new SimulateWheelDispatch();
                }
            }
        }
        return instance;
    }

    public void setEventListener(OnWheelEvent impl) {
        this.impl = impl;
    }

    public void removeEventListener() {
        impl = null;
    }

    public boolean notifyNextDownEvent() {
        return impl.onNextDown();
    }

    public boolean notifyNextUpEvent() {
        return impl.onNextUp();
    }

    public boolean notifyPreviousDownEvent() {
        return impl.onPreviousDown();
    }

    public boolean notifyPreviousUpEvent() {
        return impl.onPreviousUp();
    }

    public interface OnWheelEvent {
        boolean onNextDown();

        boolean onNextUp();

        boolean onPreviousDown();

        boolean onPreviousUp();
    }
}
