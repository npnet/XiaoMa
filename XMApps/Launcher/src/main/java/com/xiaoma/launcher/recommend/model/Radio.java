package com.xiaoma.launcher.recommend.model;

import com.xiaoma.mqtt.model.DatasBean;

/**
 * @Auther: huojie
 * @Date: 2019/1/3 0003 13:12
 * @Description:喜马拉雅电台
 */
public class Radio extends DatasBean {

    /**
     * album : {"albumTitle":"美女老总的全能保镖","coverUrlLarge":"http://fdfs.xmcdn.com/group10/M05/6B/D9/wKgDaVc67AbjYLqaAAJKOdZyb30977_mobile_large.png","coverUrlMiddle":"http://fdfs.xmcdn.com/group10/M05/6B/D9/wKgDaVc67AbjYLqaAAJKOdZyb30977_mobile_meduim.png","coverUrlSmall":"http://fdfs.xmcdn.com/group10/M05/6B/D9/wKgDaVc67AbjYLqaAAJKOdZyb30977_mobile_small.png","id":4291784}
     * albumOriginId : 4291784
     * categoryId : 3
     * commentCount : 5
     * coverUrlLarge : http://fdfs.xmcdn.com/group10/M05/6B/D9/wKgDaVc67AuwHzSUAAJKOdZyb30149_mobile_large.jpg
     * coverUrlMiddle : http://fdfs.xmcdn.com/group10/M05/6B/D9/wKgDaVc67AuwHzSUAAJKOdZyb30149_web_large.jpg
     * coverUrlSmall : http://fdfs.xmcdn.com/group10/M05/6B/D9/wKgDaVc67AuwHzSUAAJKOdZyb30149_web_meduim.jpg
     * createDate : 1544086118000
     * createdAt : 1463584649000
     * downloadCount : 0
     * duration : 1226
     * extendCategoryOriginId : 3
     * favoriteCount : 34
     * id : 15910139
     * ignore : 561610
     * intro : 退役后的唐林军不想去洗盘子，所以为了生活，他只能开了一家私家侦探事务所。婚外情调查，子女课外监护、安保、讨债样样精通。迷情寡妇，出轨熟女，还有那大学校园的校花陆续出现，都市驰骋，美艳无边，加上修真！走上人生的巅峰。 本有声小说`情节丰富！ 俗世，修真世界，地仙界，仙界。让我们一起聆听
     * kind : track
     * modifyDate : 1544086118000
     * orderNum : 47
     * playCount : 39915
     * playSize24M4a : 3756211
     * playSize64M4a : 9886977
     * playSizeAmr : 1103442
     * source : 1
     * status : 0
     * tags :
     * trackTitle : 美女老总的全能保镖048（欢迎打赏、点赞、评论）
     * updatedAt : 1483912578000
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

    @Override
    public String toString() {
        return "Radio{" +
                "album=" + album +
                ", albumOriginId=" + albumOriginId +
                ", categoryId=" + categoryId +
                ", commentCount=" + commentCount +
                ", coverUrlLarge='" + coverUrlLarge + '\'' +
                ", coverUrlMiddle='" + coverUrlMiddle + '\'' +
                ", coverUrlSmall='" + coverUrlSmall + '\'' +
                ", createDate=" + createDate +
                ", createdAt=" + createdAt +
                ", downloadCount=" + downloadCount +
                ", duration=" + duration +
                ", extendCategoryOriginId=" + extendCategoryOriginId +
                ", favoriteCount=" + favoriteCount +
                ", id=" + id +
                ", ignore=" + ignore +
                ", intro='" + intro + '\'' +
                ", kind='" + kind + '\'' +
                ", modifyDate=" + modifyDate +
                ", orderNum=" + orderNum +
                ", playCount=" + playCount +
                ", playSize24M4a=" + playSize24M4a +
                ", playSize64M4a=" + playSize64M4a +
                ", playSizeAmr=" + playSizeAmr +
                ", source=" + source +
                ", status=" + status +
                ", tags='" + tags + '\'' +
                ", trackTitle='" + trackTitle + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public static class AlbumBean {
        /**
         * albumTitle : 美女老总的全能保镖
         * coverUrlLarge : http://fdfs.xmcdn.com/group10/M05/6B/D9/wKgDaVc67AbjYLqaAAJKOdZyb30977_mobile_large.png
         * coverUrlMiddle : http://fdfs.xmcdn.com/group10/M05/6B/D9/wKgDaVc67AbjYLqaAAJKOdZyb30977_mobile_meduim.png
         * coverUrlSmall : http://fdfs.xmcdn.com/group10/M05/6B/D9/wKgDaVc67AbjYLqaAAJKOdZyb30977_mobile_small.png
         * id : 4291784
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

        @Override
        public String toString() {
            return "AlbumBean{" +
                    "albumTitle='" + albumTitle + '\'' +
                    ", coverUrlLarge='" + coverUrlLarge + '\'' +
                    ", coverUrlMiddle='" + coverUrlMiddle + '\'' +
                    ", coverUrlSmall='" + coverUrlSmall + '\'' +
                    ", id=" + id +
                    '}';
        }
    }
}
