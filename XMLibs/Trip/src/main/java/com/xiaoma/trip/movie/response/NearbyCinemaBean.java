package com.xiaoma.trip.movie.response;

import com.xiaoma.trip.common.PageInfoBean;

import java.io.Serializable;
import java.util.List;

public class NearbyCinemaBean implements Serializable {

    /**
     * cinemas : [{"cinemaId":"8471","cinemaName":"太平洋影城·深圳京基百纳店","cinemaCode":"20002845","spCode":"sp015","countyCode":"440305","countyName":"南山区","address":"深圳市南山区白石路与石洲中路交汇处京基百纳广场3楼（中信红树湾旁）","longitude":"113.970477","latitude":"22.530615","imgUrl":"http://image.189mv.cn/images/cinema/cinema_31453.jpg","mobile":"0755-86286000","facilitys":"[{\"name\":\"地铁\",\"value\":\"蛇口线红树湾站D出口步行920米\",\"type\":\"0\"},{\"name\":\"公交\",\"value\":\"610、109到石洲中路站下步行400米；45、49到百纳广场站下步行320米\",\"type\":\"1\"},{\"name\":\"停车\",\"value\":\"京基百纳广场地下停车场，免费停车3小时\",\"type\":\"2\"},{\"name\":\"卖品\",\"value\":\"爆米花、可乐等\",\"type\":\"3\"},{\"name\":\"取票机\",\"value\":\"影院自助取票机\",\"type\":\"4\"},{\"name\":\"3D眼镜\",\"value\":\"影院2016年9月1日起不再提供免费3D眼镜，眼镜价格以影城公告为准\",\"type\":\"5\"},{\"name\":\"儿童票\",\"value\":\"1.3米（含）以下儿童可免费观看2D电影（无座），1位家长最多可带1名免票儿童；儿童观看3D电影均需购票入场\",\"type\":\"6\"},{\"name\":\"休息区\",\"value\":\"大厅中心区\",\"type\":\"7\"},{\"name\":\"餐饮\",\"value\":\"醉忆江南、黄记煌、一点味餐厅、肯德基\",\"type\":\"9\"},{\"name\":\"购物\",\"value\":\"天虹超市、世界之窗\",\"type\":\"10\"}]","terminal":"8","distance":"1.0","buyTimeLimit":0,"newBuyTimeLimit":0},{"cinemaId":"23459","cinemaName":"麦希中影南方影城(深大店)","cinemaCode":"15785","spCode":"sp1006","countyCode":"440305","countyName":"南山区","address":"深南大道深大地铁站A3出口科技园文化广场3楼","longitude":"113.94326","latitude":"22.54062","imgUrl":"","mobile":"0755-86718487","facilitys":"[{\"name\":\"地铁\",\"value\":\"罗宝线 深大 A3号口 \",\"type\":\"0\"},{\"name\":\"公交\",\"value\":\"M200路204路223路234路369路113路245路21路245区间车路101路19路36路42路79路81路70路B839路高峰专线8233路M388路N1路M453路N8路N4路欧洲城假日专线高峰专线69到达科技园文化广场附近\",\"type\":\"1\"},{\"name\":\"停车\",\"value\":\"地下停车场需要自费\",\"type\":\"2\"},{\"name\":\"3D眼镜\",\"value\":\"5元/副\",\"type\":\"5\"},{\"name\":\"儿童票\",\"value\":\"1.3米以下儿童免费无座观影\",\"type\":\"6\"},{\"name\":\"休息区\",\"value\":\"健身中心、身心道场\",\"type\":\"7\"}]","terminal":"20","distance":"1.0","buyTimeLimit":0,"newBuyTimeLimit":0}]
     * pageInfo : {"pageNum":1,"pageSize":2,"totalRecord":259,"totalPage":130}
     */

    private PageInfoBean pageInfo;
    private List<NearbyCinemasDetailsBean> cinemas;

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<NearbyCinemasDetailsBean> getCinemas() {
        return cinemas;
    }

    public void setCinemas(List<NearbyCinemasDetailsBean> cinemas) {
        this.cinemas = cinemas;
    }

}
