package com.xiaoma.mapadapter.listener;

import com.xiaoma.mapadapter.model.PoiResult;

/**
 * Poi搜索接口回调， 根据业务需要可以进行接口方法扩展
 * Created by minxiwen on 2017/12/12 0012.
 */

public interface OnPoiSearchListener {
    void onPoiSearched(PoiResult result, int resultCode);
}
