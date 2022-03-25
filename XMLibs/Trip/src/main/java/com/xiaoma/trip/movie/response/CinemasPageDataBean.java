package com.xiaoma.trip.movie.response;

import com.xiaoma.trip.common.PageInfoBean;

import java.io.Serializable;
import java.util.List;

/**
 * 影院列表
 * Created by zhushi.
 * Date: 2018/12/5
 */
public class CinemasPageDataBean implements Serializable{

    /**
     * cinemas : [{"cinemaId":"10194","cinemaName":"中影国际影城（欢乐海岸店）","cinemaCode":"20000606","spCode":"sp015","countyCode":"440305","countyName":"南山区","address":"南山区 白石路东8号欢乐海岸曲水湾2栋A区","longitude":"113.992531","latitude":"22.52189","imgUrl":"http://image.189mv.cn/images/cinema/cinema_39062.jpg","mobile":"","facilitys":"[]","terminal":"1"},{"cinemaId":"28044","cinemaName":"中影星美明星国际影城(福永康之宝店)","cinemaCode":"24021","spCode":"sp1006","countyCode":"440306","countyName":"宝安区","address":"福永街道康之宝超级广场三楼","longitude":"113.82063","latitude":"22.671852","imgUrl":"","mobile":"","facilitys":"[]","terminal":"21"}]
     * pageInfo : {"pageNum":1,"pageSize":2,"totalRecord":114,"totalPage":57}
     */

    private PageInfoBean pageInfo;
    private List<CinemaBean> cinemas;

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<CinemaBean> getCinemas() {
        return cinemas;
    }

    public void setCinemas(List<CinemaBean> cinemas) {
        this.cinemas = cinemas;
    }
}
