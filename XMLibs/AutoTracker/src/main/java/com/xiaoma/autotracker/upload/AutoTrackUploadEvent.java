package com.xiaoma.autotracker.upload;

import com.xiaoma.autotracker.model.AutoTrackInfo;
import com.xiaoma.autotracker.model.UploadEventType;

import java.util.List;

/**
 * @author taojin
 * @date 2018/12/7
 */
public interface AutoTrackUploadEvent {

    /**
     * 直接上报
     *
     * @param data
     */
    void uploadEvent(AutoTrackInfo data);


    /**
     * 批量上报
     * @param autoTrackInfos  上报数据
     * @param uploadEventType 上报类型
     */
    void batchUploadEvent(List<AutoTrackInfo> autoTrackInfos, UploadEventType uploadEventType);

}
