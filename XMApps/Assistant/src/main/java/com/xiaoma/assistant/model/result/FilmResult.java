package com.xiaoma.assistant.model.result;

import com.xiaoma.assistant.model.FilmBean;

import java.util.List;

/**
 * Created by qiuboxiang on 2019/3/13 11:43
 * Desc:
 */
public class FilmResult extends SpecialResult<List<FilmBean>> {

    public FilmResult.Data data;

    @Override
    public List<FilmBean> getData() {
        return data.films;
    }

    public boolean isEmptyData() {
        return data == null || data.films == null;
    }

    public class Data {
        List<FilmBean> films;
        PageInfoBean pageInfo;
    }

}
