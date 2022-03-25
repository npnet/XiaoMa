package com.xiaoma.launcher.mark.cluster.bean;


import com.xiaoma.launcher.mark.cluster.ClusterItem;
import com.xiaoma.launcher.mark.model.MarkPhotoBean;
import com.xiaoma.mapadapter.model.LatLng;

/**
 * Created by yiyi.qi on 16/10/10.
 */

public class RegionItem implements ClusterItem {
    private LatLng mLatLng;
    private MarkPhotoBean mMarkPhotoBean;
    public RegionItem(LatLng latLng,MarkPhotoBean markPhotoBean) {
        mLatLng=latLng;
        mMarkPhotoBean = markPhotoBean;
    }

    @Override
    public LatLng getPosition() {
        // TODO Auto-generated method stub
        return mLatLng;
    }

    @Override
    public MarkPhotoBean getMarkPhoto() {
        return mMarkPhotoBean;
    }


}
