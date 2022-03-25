package com.xiaoma.music.export.model;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/8/1
 */
public class SearchByTypeMode {

    /**
     * tag : 流行
     * songList : [{"singerName":"陈雪凝","name":"绿色","singerCoverUrl":"http://www.carbuyin.net/by_spider/spider/kw_music/35331b8c-f364-424a-a79c-8ff2e67d1b9b.png","id":4673316,"songId":63803414},{"singerName":"98k&袁乐乐","name":"我曾","singerCoverUrl":"http://www.carbuyin.net/by_spider/spider/kw_music/20f1d506-b814-4e84-bcfc-ac31d8d856da.png","id":7250597,"songId":66404472},{"singerName":"锦零","name":"水星记","singerCoverUrl":"http://www.carbuyin.net/by_spider/spider/kw_music/9a3bab57-22c4-45e3-aafa-c989d340a82b.png","id":4630515,"songId":57694074},{"singerName":"李天平","name":"心如止水","singerCoverUrl":"","id":2108013,"songId":41256728},{"singerName":"Her","name":"晚安","singerCoverUrl":"http://www.carbuyin.net/by_spider/spider/kw_music/58adc078-3e5f-4c6c-bac9-acb793809b3e.png","id":2966380,"songId":41190434},{"singerName":"猗琦","name":"有一种悲伤","id":7349474,"songId":66040200},{"singerName":"李荣浩","name":"年少有为","singerCoverUrl":"http://www.carbuyin.net/by_spider/spider/kw_music/4d2049cb-7e5b-4b3e-9597-be566f1cb9df.png","id":4484817,"songId":55724544},{"singerName":"韩媛儿","name":"云烟成雨","singerCoverUrl":"http://www.carbuyin.net/by_spider/spider/kw_music/1bae7ed2-2ee2-480b-bdc9-3c4fe33d0c8b.png","id":481705,"songId":47485514},{"singerName":"李铭顺","name":"我的名字","singerCoverUrl":"http://www.carbuyin.net/by_spider/spider/kw_music/3ef07e91-d19a-4221-9be1-b1add875c755.png","id":3235393,"songId":41268715},{"singerName":"阿涵&Ayo97","name":"感谢你曾来过","singerCoverUrl":"http://www.carbuyin.net/by_spider/spider/kw_music/3bb3c3dd-778b-4f64-bc98-638b4a76d969.png","id":4536316,"songId":56628196},{"singerName":"沈木","name":"你的酒馆对我打了烊","id":7310314,"songId":66859629},{"singerName":"裂天&小魂","name":"起风了","singerCoverUrl":"http://www.carbuyin.net/by_spider/spider/kw_music/5f7d5f83-1916-4b30-9299-287829a30f9b.png","id":455018,"songId":46463943},{"singerName":"Tablo","name":"bad","singerCoverUrl":"http://www.carbuyin.net/by_spider/spider/kw_music/a984e1f2-60cd-4a0d-bd76-c07f391bc755.png","id":4991173,"songId":11295564},{"singerName":"二珂&韦懿&戚薇&张翰","name":"慢慢喜欢你","singerCoverUrl":"http://www.carbuyin.net/by_spider/spider/kw_music/f520a6fc-aa07-4995-93fd-88ed3a37af09.png","id":378992,"songId":41397743},{"singerName":"Dolly Parton","name":"假装","singerCoverUrl":"http://www.carbuyin.net/by_spider/spider/kw_music/77ca7ac6-c408-44cc-ae4a-60b398927425.png","id":1350793,"songId":18169848},{"singerName":"电音老傅&精彩轩迪&精彩苏刚","name":"出山","singerCoverUrl":"http://www.carbuyin.net/by_spider/spider/kw_music/f2be20f8-cf42-470f-b3e3-f5efc007bb16.png","id":4655283,"songId":64184654}]
     */

    private String tag;
    private List<SongListBean> songList;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<SongListBean> getSongList() {
        return songList;
    }

    public void setSongList(List<SongListBean> songList) {
        this.songList = songList;
    }

    public static class SongListBean {
        /**
         * singerName : 陈雪凝
         * name : 绿色
         * singerCoverUrl : http://www.carbuyin.net/by_spider/spider/kw_music/35331b8c-f364-424a-a79c-8ff2e67d1b9b.png
         * id : 4673316
         * songId : 63803414
         */

        private String singerName;
        private String name;
        private String singerCoverUrl;
        private long id;
        private long songId;

        public String getSingerName() {
            return singerName;
        }

        public void setSingerName(String singerName) {
            this.singerName = singerName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSingerCoverUrl() {
            return singerCoverUrl;
        }

        public void setSingerCoverUrl(String singerCoverUrl) {
            this.singerCoverUrl = singerCoverUrl;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getSongId() {
            return songId;
        }

        public void setSongId(long songId) {
            this.songId = songId;
        }
    }

}
