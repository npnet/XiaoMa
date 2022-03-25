package com.xiaoma.launcher.mark.cluster;


import com.xiaoma.mapadapter.view.Marker;

import java.util.List;

/**
 * Created by yiyi.qi on 16/10/10.
 */

public interface ClusterClickListener{
        /**
         * 点击聚合点的回调处理函数
         *
         * @param marker
         *            点击的聚合点
         * @param clusterItems
         *            聚合点所包含的元素
         */
        void onMarkClick(Marker marker, List<ClusterItem> clusterItems);
}
