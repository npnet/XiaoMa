package com.xiaoma.trip.movie.response;

import com.xiaoma.trip.common.PageInfoBean;

import java.io.Serializable;
import java.util.List;

/**
 * 影片列表
 * Created by zhushi.
 * Date: 2018/12/5
 */
public class FilmsPageDataBean implements Serializable {


    /**
     * films : [{"filmId":"7175","imgUrl":"http://image.189mv.cn/images/filmlib/82bbd6a63d8361ffef4449f7d3e621a7_HB1_64785.jpg","duration":"117","dataType":"2","title":"22年后的自白","filmType":"剧情,悬疑,犯罪","actor":"藤原龙也,伊藤英明,夏帆","director":"入江悠","language":"日语","showDate":"2019-01-11","filmArea":"cn","showCount":"0","cinemaCount":"0","description":"1995年，东京发生了连环绞杀案，五人被害，凶手无影无踪，该案成为悬案。2017年，就在该案早已过了诉讼有效期之时，一位名叫曾根崎雅人（藤原龙也饰）的男子出版了一本《我是杀人犯》的告白书，声称自己是22年前连环杀人案的凶手，并嘲笑警方无能。此举震惊了整个日本社会，面对受害者家属的愤怒，警方却束手无策。作为当年调查案件的刑警，牧村航（伊藤英明饰）多年来从未放弃追捕真凶，对于曾根崎雅人的话他将信将疑，一场激烈的猫鼠游戏在两人之间展开。","oneWord":"全民追凶","filmScore":"8.2","userCount":"0","viewPic":"http://image.189mv.cn/images/vedio/82bbd6a63d8361ffef4449f7d3e621a7_PIC_25785.jpg","filmDetailForm":{"id":"2080","filmId":"7175","filmName":"22年后的自白","dbScore":"","iMDBScore":"","filmScore":"8.2","oneWord":"全民追凶","adminId":"wangn","addTime":"2019-01-02 11:28:22","trailersUrl":"","userCount":"12685","filmLabel":"","filmMark":"","orderImg":"","jumpTypeId":"0","webLink":"","link":""},"filmPics":[{"picUrl":"http://image.189mv.cn/images/filmlib/82bbd6a63d8361ffef4449f7d3e621a7_JZV_64204.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/82bbd6a63d8361ffef4449f7d3e621a7_JZV_64205.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/82bbd6a63d8361ffef4449f7d3e621a7_JZV_64206.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/82bbd6a63d8361ffef4449f7d3e621a7_JZV_64207.jpg"}],"viewFiles":[]},{"filmId":"6024","imgUrl":"http://image.189mv.cn/images/filmlib/64577f734145c3778f933e22e519db90_HB1_64208.jpg","duration":"93","dataType":"2","title":"\u201c大\u201d人物","filmType":"动作,剧情","actor":"王千源,包贝尔,王迅","director":"五百","language":"汉语普通话","showDate":"2019-01-11","filmArea":"cn","showCount":"0","cinemaCount":"0","description":"无力维权的修车工遭遇非法强拆后，选择跳楼自杀；随着小刑警孙大圣（王千源饰）调查的深入，发现这场看似简单的民事纠纷背后其实另有隐情；随着嫌疑目标的锁定，赵泰（包贝尔饰）和崔京民（王迅饰）为代表的反派集团被盯上后，公然藐视法律挑衅警察。面对反派集团金钱诱惑、顶头上司的警告劝阻、家人性命遭受威胁，这场力量悬殊的正邪较量将会如何收场\u2026\u2026","oneWord":"中国版《老手》","filmScore":"8.4","userCount":"0","viewPic":"http://image.189mv.cn/images/vedio/64577f734145c3778f933e22e519db90_PIC_25786.jpg","filmDetailForm":{"id":"1996","filmId":"6024","filmName":"大人物","dbScore":"","iMDBScore":"","filmScore":"8.4","oneWord":"中国版《老手》","adminId":"chenzl","addTime":"2018-11-22 15:45:02","trailersUrl":"http://preview.189mv.cn:8970/2018103110_b8b22727-df58-4ea9-83c6-2e43ec342f8c_720.mp4","userCount":"20941","filmLabel":"","filmMark":"","orderImg":"","jumpTypeId":"0","webLink":"","link":""},"filmPics":[{"picUrl":"http://image.189mv.cn/images/filmlib/64577f734145c3778f933e22e519db90_JZV_64213.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/64577f734145c3778f933e22e519db90_JZV_64214.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/64577f734145c3778f933e22e519db90_JZV_64215.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/64577f734145c3778f933e22e519db90_JZV_64216.jpg"}],"viewFiles":[]}]
     * pageInfo : {"pageNum":1,"pageSize":2,"totalRecord":2,"totalPage":170}
     */

    private PageInfoBean pageInfo;
    private List<FilmsBean> films;

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<FilmsBean> getFilms() {
        return films;
    }

    public void setFilms(List<FilmsBean> films) {
        this.films = films;
    }

}
