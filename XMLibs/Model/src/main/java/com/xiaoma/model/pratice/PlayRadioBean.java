package com.xiaoma.model.pratice;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/06/05
 *     desc   :
 * </pre>
 */
public class PlayRadioBean implements MultiItemEntity {


    private int itemType;

    private int categoryOriginId;
    private String coverUrlSmall;
    private String updateAt;
    private int source;

    private String title;
    private String coverUrlMiddle;
    private int duration;
    private String createdAt;

    private long albumOriginId;
    private long originId;
    private long playSize64M4a;
    private long playSize24M4a;

    private long id;
    private String createDate;
    private String coverUrlLarge;
    private int orderNo;

    private String modifyDate;
    private String kind;
    private String introduce;
    private String tags;

    private String commentCount;
    private String playSizeAmr;
    private String playCount;
    private String extendCategoryOriginId;

    private String downloadCount;
    private String status;
    private String favoriteCount;

    private boolean isSelected;
    private String key;
    private boolean online;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public int getCategoryOriginId() {
        return categoryOriginId;
    }

    public void setCategoryOriginId(int categoryOriginId) {
        this.categoryOriginId = categoryOriginId;
    }

    public String getCoverUrlSmall() {
        return coverUrlSmall;
    }

    public void setCoverUrlSmall(String coverUrlSmall) {
        this.coverUrlSmall = coverUrlSmall;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrlMiddle() {
        return coverUrlMiddle;
    }

    public void setCoverUrlMiddle(String coverUrlMiddle) {
        this.coverUrlMiddle = coverUrlMiddle;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public long getAlbumOriginId() {
        return albumOriginId;
    }

    public void setAlbumOriginId(long albumOriginId) {
        albumOriginId = albumOriginId;
    }

    public long getOriginId() {
        return originId;
    }

    public void setOriginId(long originId) {
        this.originId = originId;
    }

    public long getPlaySize64M4a() {
        return playSize64M4a;
    }

    public void setPlaySize64M4a(long playSize64M4a) {
        this.playSize64M4a = playSize64M4a;
    }

    public long getPlaySize24M4a() {
        return playSize24M4a;
    }

    public void setPlaySize24M4a(long playSize24M4a) {
        this.playSize24M4a = playSize24M4a;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCoverUrlLarge() {
        return coverUrlLarge;
    }

    public void setCoverUrlLarge(String coverUrlLarge) {
        this.coverUrlLarge = coverUrlLarge;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getPlaySizeAmr() {
        return playSizeAmr;
    }

    public void setPlaySizeAmr(String playSizeAmr) {
        this.playSizeAmr = playSizeAmr;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public String getExtendCategoryOriginId() {
        return extendCategoryOriginId;
    }

    public void setExtendCategoryOriginId(String extendCategoryOriginId) {
        this.extendCategoryOriginId = extendCategoryOriginId;
    }

    public String getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(String downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(String favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

}
