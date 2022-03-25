package com.xiaoma.music.kuwo.model;

import android.graphics.Bitmap;

import com.xiaoma.adapter.base.XMBean;

import cn.kuwo.base.bean.quku.AlbumInfo;
import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.base.bean.quku.BaseQukuItemList;
import cn.kuwo.base.bean.quku.CategoryListInfo;
import cn.kuwo.base.bean.quku.RadioInfo;
import cn.kuwo.base.bean.quku.SongListInfo;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class XMBaseQukuItem extends XMBean<BaseQukuItem> {
    public static final String TYPE_CATEGORY_SONGLIST = "category_songlist";
    public static final String TYPE_SONGLIST = "Songlist";

    public XMBaseQukuItem(BaseQukuItem baseQukuItem) {
        super(baseQukuItem);
    }

    public int getSpecialFlag() {
        return getSDKBean().getSpecialFlag();
    }

    public void setSpecialFlag(int flag) {
        getSDKBean().setSpecialFlag(flag);
    }

    public boolean isLocalSetted() {
        return getSDKBean().isLocalSetted();
    }

    public void setLocalSetted(boolean localSetted) {
        getSDKBean().setLocalSetted(localSetted);
    }

    public String getUpdateTime() {
        return getSDKBean().getUpdateTime();
    }

    public void setUpdateTime(String time) {
        getSDKBean().setUpdateTime(time);
    }

    public void setQukuItemType(String type) {
        getSDKBean().setQukuItemType(type);
    }

    public String getQukuItemType() {
        return getSDKBean().getQukuItemType();
    }

    public long getId() {
        return getSDKBean().getId();
    }

    public void setId(String id) {
        getSDKBean().setId(id);
    }

    public String getName() {
        return getSDKBean().getName();
    }

    public void setName(String name) {
        getSDKBean().setName(name);
    }

    public String getImageUrl() {
        return getSDKBean().getImageUrl();
    }

    public void setImageUrl(String url) {
        getSDKBean().setImageUrl(url);
    }

    public String getSmallImageUrl() {
        return getSDKBean().getSmallImageUrl();
    }

    public void setSmallImageUrl(String url) {
        getSDKBean().setSmallImageUrl(url);
    }

    public String getImagePath() {
        return getSDKBean().getImagePath();
    }

    public void setImagePath(String path) {
        getSDKBean().setImagePath(path);
    }

    public String getSmallImagePath() {
        return getSDKBean().getSmallImagePath();
    }

    public void setSmallImagePath(String path) {
        getSDKBean().setSmallImagePath(path);
    }

    public void setParent(BaseQukuItemList parent) {
        getSDKBean().setParent(parent);
    }

    public XMBaseQukuItemList getParent() {
        return new XMBaseQukuItemList(getSDKBean().getParent());
    }

    public void setExtend(String extend) {
        getSDKBean().setExtend(extend);
    }

    public String getExtend() {
        return getSDKBean().getExtend();
    }

    public String getDescription() {
        return getSDKBean().getDescription();
    }

    public void setDescription(String description) {
        getSDKBean().setDescription(description);
    }

    public String getUrl() {
        return getSDKBean().getUrl();
    }

    public void setUrl(String url) {
        getSDKBean().setUrl(url);
    }

    public String getInfo() {
        return getSDKBean().getInfo();
    }

    public String getPublish() {
        return getSDKBean().getPublish();
    }

    public void setPublish(String publish) {
        getSDKBean().setPublish(publish);
    }

    public String getIsNew() {
        return getSDKBean().getIsNew();
    }

    public void setIsNew(String isNew) {
        getSDKBean().setIsNew(isNew);
    }

    public boolean getIsHot() {
        return getSDKBean().getIsHot();
    }

    public Bitmap getBitmap() {
        return getSDKBean().getBitmap();
    }

    public void setBitmap(Bitmap bitmap) {
        getSDKBean().setBitmap(bitmap);
    }

    public void setResId(int resId) {
        getSDKBean().setResId(resId);
    }

    public int getResId() {
        return getSDKBean().getResId();
    }

    public boolean isLastItem() {
        return getSDKBean().isLastItem();
    }

    public void setLastItem(boolean lastItem) {
        getSDKBean().setLastItem(lastItem);
    }

    public void getRealQukuItem(XMBaseQukuItem qukuItem, OnCheckQukuListener listener) {
        if (qukuItem == null || qukuItem.getSDKBean() == null) {
            return;
        }
        BaseQukuItem item = qukuItem.getSDKBean();
        if (item instanceof CategoryListInfo) {
            CategoryListInfo categoryListInfo = (CategoryListInfo) item;
            listener.onCategoryList(new XMCategoryListInfo(categoryListInfo));
        } else if (item instanceof SongListInfo) {
            SongListInfo songListInfo = (SongListInfo) item;
            listener.onSongList(new XMSongListInfo(songListInfo));
        }
    }

    public static XMSongListInfo convertSongListInfo(XMBaseQukuItem qukuItem) {
        if (qukuItem == null || qukuItem.getSDKBean() == null) {
            return null;
        }
        BaseQukuItem sdkBean = qukuItem.getSDKBean();
        SongListInfo songList = (SongListInfo) sdkBean;
        return new XMSongListInfo(songList);
    }

    public static XMRadioInfo convertRadioInfo(XMBaseQukuItem qukuItem) {
        if (qukuItem == null || qukuItem.getSDKBean() == null) {
            return null;
        }
        BaseQukuItem sdkBean = qukuItem.getSDKBean();
        RadioInfo radioInfo = (RadioInfo) sdkBean;
        return new XMRadioInfo(radioInfo);
    }

    public static XMAlbumInfo convertAlbumInfo(XMBaseQukuItem qukuItem) {
        if (qukuItem == null || qukuItem.getSDKBean() == null) {
            return null;
        }
        BaseQukuItem sdkBean = qukuItem.getSDKBean();
        AlbumInfo albumInfo = (AlbumInfo) sdkBean;
        return new XMAlbumInfo(albumInfo);
    }


    public interface OnCheckQukuListener {
        void onSongList(XMSongListInfo songListInfo);

        void onCategoryList(XMCategoryListInfo categoryListInfo);
    }

}
