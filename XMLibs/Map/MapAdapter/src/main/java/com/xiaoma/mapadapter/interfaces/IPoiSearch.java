package com.xiaoma.mapadapter.interfaces;


import com.xiaoma.mapadapter.listener.OnPoiSearchListener;
import com.xiaoma.mapadapter.model.QueryBound;
import com.xiaoma.mapadapter.model.QueryOption;

/**
 * 根据PoiSearch的行为抽象出的接口
 * Created by minxiwen on 2017/12/12 0012.
 */

public interface IPoiSearch {
    void setOnPoiSearchListener(OnPoiSearchListener listener);

    void setBound(QueryBound queryBound);

    void doPoiSearch();

    void setQueryOption(QueryOption option);

    void destroy();
}
