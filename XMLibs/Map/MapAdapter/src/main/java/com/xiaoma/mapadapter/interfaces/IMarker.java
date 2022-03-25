package com.xiaoma.mapadapter.interfaces;

import com.xiaoma.mapadapter.model.LatLng;


/**
 * 对覆盖物的行为抽象， 可以根据业务扩展
 * Created by minxiwen on 2017/12/12 0012.
 */

public interface IMarker {
    void remove();

    void destroy();

    void setObject(Object object);

    Object getObject();

    void setPosition(LatLng latLng);

    LatLng getPosition();

    void setAnimationListener(MarkAnimationListener markAnimationListener);
    boolean startAnimation();
    void setAnimation(Object obj);

    interface MarkAnimationListener{
        void onAnimationStart();
        void onAnimationEnd();
    }
}
