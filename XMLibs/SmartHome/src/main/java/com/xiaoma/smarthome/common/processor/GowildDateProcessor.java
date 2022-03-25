package com.xiaoma.smarthome.common.processor;


import com.xiaoma.component.AppHolder;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.common.model.DeviceInfo;
import com.xiaoma.smarthome.common.model.LocalDeviceInfo;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zy 2018/8/1 19:20
 */
public class GowildDateProcessor implements IDateProcessor<List<DeviceInfo>, List<LocalDeviceInfo>> {

    @Override
    public void process(final List<DeviceInfo> deviceInfos, final CompletedListener<List<LocalDeviceInfo>> listener) {
        SeriesAsyncWorker.create().next(new Work(Priority.NORMAL) {
            @Override
            public void doWork(Object lastResult) {
                List<LocalDeviceInfo> result = new ArrayList<>();
                if (!ListUtils.isEmpty(deviceInfos)) {
                    for (DeviceInfo deviceInfo : deviceInfos) {
                        if (deviceInfo == null) {
                            continue;
                        }
                        String deviceName = deviceInfo.getDeviceName();
                        if (deviceName == null) {
                            continue;
                        }
                        LocalDeviceInfo info;
                        if (deviceName.contains(SmartConstants.SCENE_ARRIVE_HOME)) {
                            info = new LocalDeviceInfo(deviceInfo.getDeviceName(), deviceInfo.getDeviceType(), LocalDeviceInfo.TYPE_ARRIVE_HOME);
                            info.setAuto(TPUtils.get(AppHolder.getInstance().getAppContext(), SmartConstants.KEY_ARRIVE_XIAOBAI_IS_AUTO, false));
                        } else if (deviceName.contains(SmartConstants.SCENE_LEAVE_HOME)) {
                            info = new LocalDeviceInfo(deviceInfo.getDeviceName(), deviceInfo.getDeviceType(), LocalDeviceInfo.TYPE_LEAVE_HOME);
                            info.setAuto(TPUtils.get(AppHolder.getInstance().getAppContext(), SmartConstants.KEY_LEAVE_XIAOBAI_IS_AUTO, false));
                        } else {
                            info = new LocalDeviceInfo(deviceInfo.getDeviceName(), deviceInfo.getDeviceType(), LocalDeviceInfo.TYPE_OTHERS);
                        }

                        result.add(info);
                        Collections.sort(result);
                    }
                }
                doNext(result);
            }
        }).next(new Work<List<LocalDeviceInfo>>() {
            @Override
            public void doWork(List<LocalDeviceInfo> lastResult) {
                if (listener != null) {
                    listener.completed(lastResult);
                }
            }
        }).start();
    }
}
