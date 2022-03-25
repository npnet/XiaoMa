package com.xiaoma.xting.assistant.model;

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
 * @date 2019/3/22
 */
public class AssistantAlbum {


    /**
     * pageInfo : {"pageNum":1,"pageSize":2,"totalRecord":500,"totalPage":250}
     * list : [{"coverUrlSmall":"http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfbRLMioAACCF_27DgE863_web_meduim.jpg","orderNum":1558,"source":1,"coverUrlMiddle":"http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfbRLMioAACCF_27DgE863_web_large.jpg","trackTitle":"主播茶茶：感情好不好，看走路姿势就知道","duration":848,"createdAt":1509243928000,"albumOriginId":6234075,"playSize64M4a":6868119,"intro":"如果你喜欢我们的音频，","ignore":2433159,"playSize24M4a":2626443,"id":56168829,"createDate":1544149485000,"updatedAt":1510815229000,"coverUrlLarge":"http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfbRLMioAACCF_27DgE863_mobile_large.jpg","modifyDate":1544149485000,"album":{"coverUrlLarge":"http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfPy1EReAACCF_27DgE668_mobile_large.jpg","coverUrlSmall":"http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfPy1EReAACCF_27DgE668_mobile_small.jpg","albumTitle":"一星期一本书","id":6234075,"coverUrlMiddle":"http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfPy1EReAACCF_27DgE668_mobile_meduim.jpg"},"kind":"track","commentCount":0,"playSizeAmr":0,"tags":"","playCount":1363,"extendCategoryOriginId":39,"categoryId":39,"downloadCount":0,"favoriteCount":1,"status":0}]
     */

    private PageInfoBean pageInfo;
    private List<ListBean> list;

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
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
        for (AssistantAlbum.ListBean listBean : list) {
            xmTrack = new XMTrack(new Track());

            xmSubordinatedAlbum = new XMSubordinatedAlbum(new SubordinatedAlbum());
            AssistantAlbum.ListBean.AlbumBean album = listBean.getAlbum();

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

            trackLists.add(xmTrack);
        }
        return trackLists;
    }

    public static class PageInfoBean {
        /**
         * pageNum : 1
         * pageSize : 2
         * totalRecord : 500
         * totalPage : 250
         */

        private int pageNum;
        private int pageSize;
        private int totalRecord;
        private int totalPage;

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalRecord() {
            return totalRecord;
        }

        public void setTotalRecord(int totalRecord) {
            this.totalRecord = totalRecord;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }
    }

    public static class ListBean {
        /**
         * coverUrlSmall : http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfbRLMioAACCF_27DgE863_web_meduim.jpg
         * orderNum : 1558
         * source : 1
         * coverUrlMiddle : http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfbRLMioAACCF_27DgE863_web_large.jpg
         * trackTitle : 主播茶茶：感情好不好，看走路姿势就知道
         * duration : 848
         * createdAt : 1509243928000
         * albumOriginId : 6234075
         * playSize64M4a : 6868119
         * intro : 如果你喜欢我们的音频，
         * ignore : 2433159
         * playSize24M4a : 2626443
         * id : 56168829
         * createDate : 1544149485000
         * updatedAt : 1510815229000
         * coverUrlLarge : http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfbRLMioAACCF_27DgE863_mobile_large.jpg
         * modifyDate : 1544149485000
         * album : {"coverUrlLarge":"http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfPy1EReAACCF_27DgE668_mobile_large.jpg","coverUrlSmall":"http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfPy1EReAACCF_27DgE668_mobile_small.jpg","albumTitle":"一星期一本书","id":6234075,"coverUrlMiddle":"http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfPy1EReAACCF_27DgE668_mobile_meduim.jpg"}
         * kind : track
         * commentCount : 0
         * playSizeAmr : 0
         * tags :
         * playCount : 1363
         * extendCategoryOriginId : 39
         * categoryId : 39
         * downloadCount : 0
         * favoriteCount : 1
         * status : 0
         */

        private String coverUrlSmall;
        private int orderNum;
        private int source;
        private String coverUrlMiddle;
        private String trackTitle;
        private int duration;
        private long createdAt;
        private int albumOriginId;
        private int playSize64M4a;
        private String intro;
        private int ignore;
        private int playSize24M4a;
        private int id;
        private long createDate;
        private long updatedAt;
        private String coverUrlLarge;
        private long modifyDate;
        private AlbumBean album;
        private String kind;
        private int commentCount;
        private int playSizeAmr;
        private String tags;
        private int playCount;
        private int extendCategoryOriginId;
        private int categoryId;
        private int downloadCount;
        private int favoriteCount;
        private int status;

        public String getCoverUrlSmall() {
            return coverUrlSmall;
        }

        public void setCoverUrlSmall(String coverUrlSmall) {
            this.coverUrlSmall = coverUrlSmall;
        }

        public int getOrderNum() {
            return orderNum;
        }

        public void setOrderNum(int orderNum) {
            this.orderNum = orderNum;
        }

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }

        public String getCoverUrlMiddle() {
            return coverUrlMiddle;
        }

        public void setCoverUrlMiddle(String coverUrlMiddle) {
            this.coverUrlMiddle = coverUrlMiddle;
        }

        public String getTrackTitle() {
            return trackTitle;
        }

        public void setTrackTitle(String trackTitle) {
            this.trackTitle = trackTitle;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        public int getAlbumOriginId() {
            return albumOriginId;
        }

        public void setAlbumOriginId(int albumOriginId) {
            this.albumOriginId = albumOriginId;
        }

        public int getPlaySize64M4a() {
            return playSize64M4a;
        }

        public void setPlaySize64M4a(int playSize64M4a) {
            this.playSize64M4a = playSize64M4a;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public int getIgnore() {
            return ignore;
        }

        public void setIgnore(int ignore) {
            this.ignore = ignore;
        }

        public int getPlaySize24M4a() {
            return playSize24M4a;
        }

        public void setPlaySize24M4a(int playSize24M4a) {
            this.playSize24M4a = playSize24M4a;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public long getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(long updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getCoverUrlLarge() {
            return coverUrlLarge;
        }

        public void setCoverUrlLarge(String coverUrlLarge) {
            this.coverUrlLarge = coverUrlLarge;
        }

        public long getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(long modifyDate) {
            this.modifyDate = modifyDate;
        }

        public AlbumBean getAlbum() {
            return album;
        }

        public void setAlbum(AlbumBean album) {
            this.album = album;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        public int getPlaySizeAmr() {
            return playSizeAmr;
        }

        public void setPlaySizeAmr(int playSizeAmr) {
            this.playSizeAmr = playSizeAmr;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public int getPlayCount() {
            return playCount;
        }

        public void setPlayCount(int playCount) {
            this.playCount = playCount;
        }

        public int getExtendCategoryOriginId() {
            return extendCategoryOriginId;
        }

        public void setExtendCategoryOriginId(int extendCategoryOriginId) {
            this.extendCategoryOriginId = extendCategoryOriginId;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public int getDownloadCount() {
            return downloadCount;
        }

        public void setDownloadCount(int downloadCount) {
            this.downloadCount = downloadCount;
        }

        public int getFavoriteCount() {
            return favoriteCount;
        }

        public void setFavoriteCount(int favoriteCount) {
            this.favoriteCount = favoriteCount;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public static class AlbumBean {
            /**
             * coverUrlLarge : http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfPy1EReAACCF_27DgE668_mobile_large.jpg
             * coverUrlSmall : http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfPy1EReAACCF_27DgE668_mobile_small.jpg
             * albumTitle : 一星期一本书
             * id : 6234075
             * coverUrlMiddle : http: //fdfs.xmcdn.com/group28/M0A/8B/0C/wKgJSFlbMfPy1EReAACCF_27DgE668_mobile_meduim.jpg
             */

            private String coverUrlLarge;
            private String coverUrlSmall;
            private String albumTitle;
            private int id;
            private String coverUrlMiddle;

            public String getCoverUrlLarge() {
                return coverUrlLarge;
            }

            public void setCoverUrlLarge(String coverUrlLarge) {
                this.coverUrlLarge = coverUrlLarge;
            }

            public String getCoverUrlSmall() {
                return coverUrlSmall;
            }

            public void setCoverUrlSmall(String coverUrlSmall) {
                this.coverUrlSmall = coverUrlSmall;
            }

            public String getAlbumTitle() {
                return albumTitle;
            }

            public void setAlbumTitle(String albumTitle) {
                this.albumTitle = albumTitle;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getCoverUrlMiddle() {
                return coverUrlMiddle;
            }

            public void setCoverUrlMiddle(String coverUrlMiddle) {
                this.coverUrlMiddle = coverUrlMiddle;
            }
        }
    }
}
