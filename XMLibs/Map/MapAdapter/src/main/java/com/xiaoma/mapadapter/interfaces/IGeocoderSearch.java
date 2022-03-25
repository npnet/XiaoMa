package com.xiaoma.mapadapter.interfaces;


import com.xiaoma.mapadapter.listener.OnGeocodeSearchListener;
import com.xiaoma.mapadapter.model.RegeocodeQueryOption;

/**
 * 地理编码反地理编码相关接口抽象, 可根据业务进行扩展
 * Created by minxiwen on 2017/12/12 0012.
 */

public interface IGeocoderSearch {

    void setOnGeocodeSearchListener(OnGeocodeSearchListener listener);

    void getFromLocationAsyn(RegeocodeQueryOption option);

    void destroy();

}
