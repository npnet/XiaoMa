package com.xiaoma.vrpractice.common.manager;

/**
 * Created by Thomas on 2019/6/1 0001
 */

public class VrPracticeManager {

    private VrPracticeManager() {
    }

    private static class InstanceHolder {
        static final VrPracticeManager sInstance = new VrPracticeManager();
    }

    public static VrPracticeManager getInstance() {
        return InstanceHolder.sInstance;
    }

}
