package com.xiaoma.gdmap;

import com.amap.api.maps.model.animation.Animation;
import com.xiaoma.mapadapter.convert.MapConverter;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.view.Marker;


/**
 * Created by minxiwen on 2017/12/12 0012.
 */

public class GDMarker extends Marker {
    private com.amap.api.maps.model.Marker marker;

    public GDMarker(com.amap.api.maps.model.Marker marker) {
        this.marker = marker;
    }

    @Override
    public void remove() {
        marker.remove();
    }

    @Override
    public void destroy() {
        this.marker.destroy();
    }

    @Override
    public void setObject(Object object) {
        marker.setObject(object);
    }

    @Override
    public Object getObject() {
        return marker.getObject();
    }

    @Override
    public void setPosition(LatLng latLng) {
        com.amap.api.maps.model.LatLng latLng1 = new com.amap.api.maps.model.LatLng(latLng.getLatitude(), latLng.getLongitude());
        marker.setPosition(latLng1);
    }

    @Override
    public LatLng getPosition() {
        return MapConverter.getInstance().convertLatLng(marker.getPosition());
    }

    @Override
    public void setAnimationListener(final MarkAnimationListener markAnimationListener) {
            marker.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart() {
                    markAnimationListener.onAnimationStart();
                }

                @Override
                public void onAnimationEnd() {
                    markAnimationListener.onAnimationEnd();
                }
            });
    }

    @Override
    public boolean startAnimation() {
        return marker.startAnimation();
    }

    @Override
    public void setAnimation(Object obj) {
        marker.setAnimation((Animation) obj);
    }
}
