package com.xiaoma.assistant.model.result;

import com.xiaoma.assistant.model.parser.HotelBean;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/3/8 17:30
 * Desc:
 */

public class HotelResult extends SpecialResult<List<HotelBean>> {

    public Data data;

    @Override
    public List<HotelBean> getData() {
        return data.hotelList;
    }

    public boolean isEmptyData() {
        return data == null || data.hotelList == null;
    }

    public class Data {
        List<HotelBean> hotelList;
        PageInfoBean pageInfo;
    }

}
