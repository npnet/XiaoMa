package com.xiaoma.mapadapter.listener;


import com.xiaoma.mapadapter.model.RegeocodeResultInfo;

/**
 * 地理/反地理编码回调接口
 * Created by minxiwen on 2017/12/12 0012.
 */

public interface OnGeocodeSearchListener {
    void onRegeocodeSearched(RegeocodeResultInfo result, int resultCode);
}
