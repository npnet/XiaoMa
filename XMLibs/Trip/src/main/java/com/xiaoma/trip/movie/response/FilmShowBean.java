package com.xiaoma.trip.movie.response;

import com.xiaoma.trip.common.PageInfoBean;

import java.io.Serializable;
import java.util.List;

public class FilmShowBean implements Serializable {

    private List<CinemasBean> cinemas;
    private PageInfoBean pageInfo;

    public List<CinemasBean> getCinemas() {
        return cinemas;
    }

    public void setCinemas(List<CinemasBean> cinemas) {
        this.cinemas = cinemas;
    }

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }
}
