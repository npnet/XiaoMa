package com.xiaoma.xting.launcher.model;

import com.xiaoma.xting.sdk.bean.XMSubordinatedAlbum;
import com.xiaoma.xting.sdk.bean.XMTrack;
import com.ximalaya.ting.android.opensdk.model.album.SubordinatedAlbum;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/2/15
 */
public class LauncherAlbum {


    /**
     * currentPage : 1
     * list : [{"album":{"albumTitle":"音乐听觉盛宴-3D环绕电磁波-电音-环绕-大全","coverUrlLarge":"http://fdfs.xmcdn.com/group27/M01/C4/D1/wKgJR1j0bi6ALa-FAAIGyU-NZw4040_mobile_large.jpg","coverUrlMiddle":"http://fdfs.xmcdn.com/group27/M01/C4/D1/wKgJR1j0bi6ALa-FAAIGyU-NZw4040_mobile_meduim.jpg","coverUrlSmall":"http://fdfs.xmcdn.com/group27/M01/C4/D1/wKgJR1j0bi6ALa-FAAIGyU-NZw4040_mobile_small.jpg","id":7697683},"albumOriginId":7697683,"categoryId":2,"commentCount":1,"coverUrlLarge":"http://fdfs.xmcdn.com/group26/M06/C9/67/wKgJWFj0bt6zC27eAAIGyU-NZw4584_mobile_large.jpg","coverUrlMiddle":"http://fdfs.xmcdn.com/group26/M06/C9/67/wKgJWFj0bt6zC27eAAIGyU-NZw4584_web_large.jpg","coverUrlSmall":"http://fdfs.xmcdn.com/group26/M06/C9/67/wKgJWFj0bt6zC27eAAIGyU-NZw4584_web_meduim.jpg","createDate":1544080308000,"createdAt":1492414616000,"downloadCount":0,"duration":361,"extendCategoryOriginId":29,"favoriteCount":21,"id":35625241,"ignore":1761,"intro":"音乐听觉盛宴-3D环绕电磁波-电音-环绕-大全 非一般的视听盛宴","kind":"track","modifyDate":1544080308000,"orderNum":5,"playCount":8413,"playSize24M4a":1119257,"playSize64M4a":2925852,"playSizeAmr":325104,"source":1,"status":0,"tags":"","trackTitle":"7妹-环绕浪曲 (DJ版)","updatedAt":1492422241000},{"album":{"albumTitle":"3D好声音\u2014\u2014影视金曲","coverUrlLarge":"http://fdfs.xmcdn.com/group24/M04/66/5D/wKgJMFh0jPKyEHOHAACyC7TtxbQ866_mobile_large.gif","coverUrlMiddle":"http://fdfs.xmcdn.com/group24/M04/66/5D/wKgJMFh0jPKyEHOHAACyC7TtxbQ866_mobile_meduim.gif","coverUrlSmall":"http://fdfs.xmcdn.com/group24/M04/66/5D/wKgJMFh0jPKyEHOHAACyC7TtxbQ866_mobile_small.gif","id":6408742},"albumOriginId":6408742,"categoryId":2,"commentCount":0,"coverUrlLarge":"http://fdfs.xmcdn.com/group24/M0B/66/63/wKgJMFh0jcehuu5XAACyC7TtxbQ450_mobile_large.jpg","coverUrlMiddle":"http://fdfs.xmcdn.com/group24/M0B/66/63/wKgJMFh0jcehuu5XAACyC7TtxbQ450_web_large.jpg","coverUrlSmall":"http://fdfs.xmcdn.com/group24/M0B/66/63/wKgJMFh0jcehuu5XAACyC7TtxbQ450_web_meduim.jpg","createDate":1544080330000,"createdAt":1484035721000,"downloadCount":0,"duration":640,"extendCategoryOriginId":29,"favoriteCount":1,"id":28489521,"ignore":3058,"intro":"经典影视歌曲3D版，喜欢就请关注、订阅、打赏吧，谢谢~","kind":"track","modifyDate":1544080330000,"orderNum":121,"playCount":198,"playSize24M4a":1984435,"playSize64M4a":5188461,"playSizeAmr":576636,"source":1,"status":0,"tags":"","trackTitle":"3D震撼音效空间展示 战狼","updatedAt":1484041492000},{"album":{"albumTitle":"诗经","coverUrlLarge":"http://fdfs.xmcdn.com/group7/M0B/DC/6A/wKgDWlaGbYuyh9gXAACyDBvBQx8069_mobile_large.jpg","coverUrlMiddle":"http://fdfs.xmcdn.com/group7/M0B/DC/6A/wKgDWlaGbYuyh9gXAACyDBvBQx8069_mobile_meduim.jpg","coverUrlSmall":"http://fdfs.xmcdn.com/group7/M0B/DC/6A/wKgDWlaGbYuyh9gXAACyDBvBQx8069_mobile_small.jpg","id":421250},"albumOriginId":421250,"categoryId":39,"commentCount":5,"coverUrlLarge":"http://fdfs.xmcdn.com/group7/M0B/DC/73/wKgDX1aGbY7iSEolAACyDBvBQx8691_mobile_large.jpg","coverUrlMiddle":"http://fdfs.xmcdn.com/group7/M0B/DC/73/wKgDX1aGbY7iSEolAACyDBvBQx8691_web_large.jpg","coverUrlSmall":"http://fdfs.xmcdn.com/group7/M0B/DC/73/wKgDX1aGbY7iSEolAACyDBvBQx8691_web_meduim.jpg","createDate":1544175977000,"createdAt":1457102233000,"downloadCount":0,"duration":73,"extendCategoryOriginId":34,"favoriteCount":3,"id":12836184,"ignore":3743114,"intro":"","kind":"track","modifyDate":1544175977000,"orderNum":73,"playCount":1877,"playSize24M4a":227484,"playSize64M4a":594070,"playSizeAmr":65922,"source":1,"status":0,"tags":"读书,国学","trackTitle":"73 诗经 王风 大车_李华伟吟诵","updatedAt":1486255656000},{"album":{"albumTitle":"如何实现财务自由的生活","coverUrlLarge":"http://fdfs.xmcdn.com/group21/M02/6C/7B/wKgJKFibMOTiYIDXAAVOkefNw1A463_mobile_large.jpg","coverUrlMiddle":"http://fdfs.xmcdn.com/group21/M02/6C/7B/wKgJKFibMOTiYIDXAAVOkefNw1A463_mobile_meduim.jpg","coverUrlSmall":"http://fdfs.xmcdn.com/group21/M02/6C/7B/wKgJKFibMOTiYIDXAAVOkefNw1A463_mobile_small.jpg","id":6670844},"albumOriginId":6670844,"categoryId":3,"commentCount":0,"coverUrlLarge":"http://fdfs.xmcdn.com/group21/M02/6C/63/wKgJLVibMPTw9TAqAAVOkefNw1A593_mobile_large.jpg","coverUrlMiddle":"http://fdfs.xmcdn.com/group21/M02/6C/63/wKgJLVibMPTw9TAqAAVOkefNw1A593_web_large.jpg","coverUrlSmall":"http://fdfs.xmcdn.com/group21/M02/6C/63/wKgJLVibMPTw9TAqAAVOkefNw1A593_web_meduim.jpg","createDate":1544176734000,"createdAt":1500764403000,"downloadCount":0,"duration":282,"extendCategoryOriginId":46,"favoriteCount":3,"id":44802517,"ignore":3798894,"intro":"","kind":"track","modifyDate":1544176734000,"orderNum":168,"playCount":2313,"playSize24M4a":874877,"playSize64M4a":2286414,"playSizeAmr":0,"source":1,"status":0,"tags":"","trackTitle":"163.让改变轻松起来2-《瞬变》","updatedAt":1513568408000},{"album":{"albumTitle":"诗如歌","coverUrlLarge":"http://fdfs.xmcdn.com/group49/M0A/FD/BD/wKgKl1vHSh3zFNBfAAANZ9EhhRU169_mobile_large.jpg","coverUrlMiddle":"http://fdfs.xmcdn.com/group49/M0A/FD/BD/wKgKl1vHSh3zFNBfAAANZ9EhhRU169_mobile_meduim.jpg","coverUrlSmall":"http://fdfs.xmcdn.com/group49/M0A/FD/BD/wKgKl1vHSh3zFNBfAAANZ9EhhRU169_mobile_small.jpg","id":18872445},"albumOriginId":18872445,"categoryId":39,"commentCount":0,"coverUrlLarge":"http://fdfs.xmcdn.com/group49/M0A/FD/64/wKgKmFvHSh7Au_qWAAANZ9EhhRU947_mobile_large.jpg","coverUrlMiddle":"http://fdfs.xmcdn.com/group49/M0A/FD/64/wKgKmFvHSh7Au_qWAAANZ9EhhRU947_web_large.jpg","coverUrlSmall":"http://fdfs.xmcdn.com/group49/M0A/FD/64/wKgKmFvHSh7Au_qWAAANZ9EhhRU947_web_meduim.jpg","createDate":1544176399000,"createdAt":1541820167000,"downloadCount":0,"duration":334,"extendCategoryOriginId":34,"favoriteCount":0,"id":135566310,"ignore":3783230,"intro":"","kind":"track","modifyDate":1544176399000,"orderNum":3,"playCount":55,"playSize24M4a":1036759,"playSize64M4a":2710130,"playSizeAmr":301146,"source":1,"status":0,"tags":"牛奶白,诗,歌曲","trackTitle":"03 cheers--牛奶白","updatedAt":1541822381000}]
     * pageSize : 5
     * total : 500
     * totalPages : 100
     */

    private int currentPage;
    private int pageSize;
    private int total;
    private int totalPages;
    private List<ListBean> list;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public ArrayList<XMTrack> getTrackList() {
        ArrayList<XMTrack> trackLists = new ArrayList<>();
        XMTrack xmTrack = null;
        XMSubordinatedAlbum xmSubordinatedAlbum = null;
        if (list == null) {
            return trackLists;
        }
        for (ListBean listBean : list) {
            xmTrack = new XMTrack(new Track());

            xmSubordinatedAlbum = new XMSubordinatedAlbum(new SubordinatedAlbum());
            ListBean.AlbumBean album = listBean.getAlbum();

            if (album != null) {
                xmSubordinatedAlbum.setCoverUrlLarge(album.getCoverUrlLarge());
                xmSubordinatedAlbum.setCoverUrlMiddle(album.getCoverUrlMiddle());
                xmSubordinatedAlbum.setCoverUrlSmall(album.getCoverUrlSmall());
                xmSubordinatedAlbum.setAlbumId(album.getId());
                xmSubordinatedAlbum.setAlbumTitle(album.getAlbumTitle());
            }

            xmTrack.setAlbum(xmSubordinatedAlbum);
            xmTrack.setDataId(listBean.getId());
            xmTrack.setCategoryId(listBean.getCategoryId());
            xmTrack.setCoverUrlLarge(listBean.getCoverUrlLarge());
            xmTrack.setCoverUrlMiddle(listBean.getCoverUrlMiddle());
            xmTrack.setCoverUrlSmall(listBean.getCoverUrlSmall());
            xmTrack.setCreatedAt(listBean.getCreatedAt());
            xmTrack.setDuration(listBean.getDuration());
            xmTrack.setKind(listBean.getKind());
            xmTrack.setUpdatedAt(listBean.getUpdatedAt());
            xmTrack.setTrackTitle(listBean.getTrackTitle());
            xmTrack.setPlaySize24M4a(String.valueOf(listBean.getPlaySize24M4a()));
            xmTrack.setPlaySize64m4a(String.valueOf(listBean.getPlaySize64M4a()));
            xmTrack.setTrackTags(listBean.getTags());
            xmTrack.setOrderNum(listBean.getOrderNum());

            trackLists.add(xmTrack);
        }
        return trackLists;
    }

    public static class ListBean {
        /**
         * album : {"albumTitle":"音乐听觉盛宴-3D环绕电磁波-电音-环绕-大全","coverUrlLarge":"http://fdfs.xmcdn.com/group27/M01/C4/D1/wKgJR1j0bi6ALa-FAAIGyU-NZw4040_mobile_large.jpg","coverUrlMiddle":"http://fdfs.xmcdn.com/group27/M01/C4/D1/wKgJR1j0bi6ALa-FAAIGyU-NZw4040_mobile_meduim.jpg","coverUrlSmall":"http://fdfs.xmcdn.com/group27/M01/C4/D1/wKgJR1j0bi6ALa-FAAIGyU-NZw4040_mobile_small.jpg","id":7697683}
         * albumOriginId : 7697683
         * categoryId : 2
         * commentCount : 1
         * coverUrlLarge : http://fdfs.xmcdn.com/group26/M06/C9/67/wKgJWFj0bt6zC27eAAIGyU-NZw4584_mobile_large.jpg
         * coverUrlMiddle : http://fdfs.xmcdn.com/group26/M06/C9/67/wKgJWFj0bt6zC27eAAIGyU-NZw4584_web_large.jpg
         * coverUrlSmall : http://fdfs.xmcdn.com/group26/M06/C9/67/wKgJWFj0bt6zC27eAAIGyU-NZw4584_web_meduim.jpg
         * createDate : 1544080308000
         * createdAt : 1492414616000
         * downloadCount : 0
         * duration : 361
         * extendCategoryOriginId : 29
         * favoriteCount : 21
         * id : 35625241
         * ignore : 1761
         * intro : 音乐听觉盛宴-3D环绕电磁波-电音-环绕-大全 非一般的视听盛宴
         * kind : track
         * modifyDate : 1544080308000
         * orderNum : 5
         * playCount : 8413
         * playSize24M4a : 1119257
         * playSize64M4a : 2925852
         * playSizeAmr : 325104
         * source : 1
         * status : 0
         * tags :
         * trackTitle : 7妹-环绕浪曲 (DJ版)
         * updatedAt : 1492422241000
         */

        private AlbumBean album;
        private int albumOriginId;
        private int categoryId;
        private int commentCount;
        private String coverUrlLarge;
        private String coverUrlMiddle;
        private String coverUrlSmall;
        private long createDate;
        private long createdAt;
        private int downloadCount;
        private int duration;
        private int extendCategoryOriginId;
        private int favoriteCount;
        private int id;
        private int ignore;
        private String intro;
        private String kind;
        private long modifyDate;
        private int orderNum;
        private int playCount;
        private int playSize24M4a;
        private int playSize64M4a;
        private int playSizeAmr;
        private int source;
        private int status;
        private String tags;
        private String trackTitle;
        private long updatedAt;

        public AlbumBean getAlbum() {
            return album;
        }

        public void setAlbum(AlbumBean album) {
            this.album = album;
        }

        public int getAlbumOriginId() {
            return albumOriginId;
        }

        public void setAlbumOriginId(int albumOriginId) {
            this.albumOriginId = albumOriginId;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        public String getCoverUrlLarge() {
            return coverUrlLarge;
        }

        public void setCoverUrlLarge(String coverUrlLarge) {
            this.coverUrlLarge = coverUrlLarge;
        }

        public String getCoverUrlMiddle() {
            return coverUrlMiddle;
        }

        public void setCoverUrlMiddle(String coverUrlMiddle) {
            this.coverUrlMiddle = coverUrlMiddle;
        }

        public String getCoverUrlSmall() {
            return coverUrlSmall;
        }

        public void setCoverUrlSmall(String coverUrlSmall) {
            this.coverUrlSmall = coverUrlSmall;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        public int getDownloadCount() {
            return downloadCount;
        }

        public void setDownloadCount(int downloadCount) {
            this.downloadCount = downloadCount;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getExtendCategoryOriginId() {
            return extendCategoryOriginId;
        }

        public void setExtendCategoryOriginId(int extendCategoryOriginId) {
            this.extendCategoryOriginId = extendCategoryOriginId;
        }

        public int getFavoriteCount() {
            return favoriteCount;
        }

        public void setFavoriteCount(int favoriteCount) {
            this.favoriteCount = favoriteCount;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIgnore() {
            return ignore;
        }

        public void setIgnore(int ignore) {
            this.ignore = ignore;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public long getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(long modifyDate) {
            this.modifyDate = modifyDate;
        }

        public int getOrderNum() {
            return orderNum;
        }

        public void setOrderNum(int orderNum) {
            this.orderNum = orderNum;
        }

        public int getPlayCount() {
            return playCount;
        }

        public void setPlayCount(int playCount) {
            this.playCount = playCount;
        }

        public int getPlaySize24M4a() {
            return playSize24M4a;
        }

        public void setPlaySize24M4a(int playSize24M4a) {
            this.playSize24M4a = playSize24M4a;
        }

        public int getPlaySize64M4a() {
            return playSize64M4a;
        }

        public void setPlaySize64M4a(int playSize64M4a) {
            this.playSize64M4a = playSize64M4a;
        }

        public int getPlaySizeAmr() {
            return playSizeAmr;
        }

        public void setPlaySizeAmr(int playSizeAmr) {
            this.playSizeAmr = playSizeAmr;
        }

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public String getTrackTitle() {
            return trackTitle;
        }

        public void setTrackTitle(String trackTitle) {
            this.trackTitle = trackTitle;
        }

        public long getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(long updatedAt) {
            this.updatedAt = updatedAt;
        }

        public static class AlbumBean {
            /**
             * albumTitle : 音乐听觉盛宴-3D环绕电磁波-电音-环绕-大全
             * coverUrlLarge : http://fdfs.xmcdn.com/group27/M01/C4/D1/wKgJR1j0bi6ALa-FAAIGyU-NZw4040_mobile_large.jpg
             * coverUrlMiddle : http://fdfs.xmcdn.com/group27/M01/C4/D1/wKgJR1j0bi6ALa-FAAIGyU-NZw4040_mobile_meduim.jpg
             * coverUrlSmall : http://fdfs.xmcdn.com/group27/M01/C4/D1/wKgJR1j0bi6ALa-FAAIGyU-NZw4040_mobile_small.jpg
             * id : 7697683
             */

            private String albumTitle;
            private String coverUrlLarge;
            private String coverUrlMiddle;
            private String coverUrlSmall;
            private int id;

            public String getAlbumTitle() {
                return albumTitle;
            }

            public void setAlbumTitle(String albumTitle) {
                this.albumTitle = albumTitle;
            }

            public String getCoverUrlLarge() {
                return coverUrlLarge;
            }

            public void setCoverUrlLarge(String coverUrlLarge) {
                this.coverUrlLarge = coverUrlLarge;
            }

            public String getCoverUrlMiddle() {
                return coverUrlMiddle;
            }

            public void setCoverUrlMiddle(String coverUrlMiddle) {
                this.coverUrlMiddle = coverUrlMiddle;
            }

            public String getCoverUrlSmall() {
                return coverUrlSmall;
            }

            public void setCoverUrlSmall(String coverUrlSmall) {
                this.coverUrlSmall = coverUrlSmall;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }
    }
}
