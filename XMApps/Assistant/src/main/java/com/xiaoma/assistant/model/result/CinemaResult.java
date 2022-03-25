package com.xiaoma.assistant.model.result;

import com.xiaoma.assistant.model.CinemaBean;

import java.util.List;

/**
 * Created by qiuboxiang on 2019/3/8 19:47
 * Desc:
 */
public class CinemaResult extends SpecialResult<List<CinemaBean>> {

    public Data data;

    @Override
    public List<CinemaBean> getData() {
        return data.cinemas;
    }

    public boolean isEmptyData() {
        return data == null || data.cinemas == null;
    }

    public class Data {
        List<CinemaBean> cinemas;
        PageInfoBean pageInfo;
    }

}
