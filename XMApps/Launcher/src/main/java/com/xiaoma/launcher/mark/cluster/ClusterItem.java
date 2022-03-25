package com.xiaoma.launcher.mark.cluster;


import com.xiaoma.launcher.mark.model.MarkPhotoBean;
import com.xiaoma.mapadapter.model.LatLng;

/**
 * Created by yiyi.qi on 16/10/10.
 */

public interface ClusterItem {
    /**
     * 返回聚合元素的地理位置
     *
     * @return
     */
    LatLng getPosition();


    MarkPhotoBean getMarkPhoto();
}
