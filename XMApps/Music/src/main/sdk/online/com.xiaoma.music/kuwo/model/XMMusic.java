package com.xiaoma.music.kuwo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.xiaoma.adapter.base.XMBean;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.StringUtil;

import java.io.Serializable;
import java.util.Collection;

import cn.kuwo.base.bean.Music;
import cn.kuwo.base.bean.MusicFormat;
import cn.kuwo.base.bean.MusicQuality;
import cn.kuwo.base.bean.NetResource;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
@Table("XMMusic")
public class XMMusic extends XMBean<Music> implements Serializable {
    public XMMusic(Music music) {
        super(music);
        this.music = music;
    }

    private String iconUrl;

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int recordId;
    private cn.kuwo.base.bean.Music music;

    public String getIconUrl() {
        if (!StringUtil.isNotEmpty(iconUrl)) {
            return getSDKBean().mvIconUrl;
        }
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        getSDKBean().mvIconUrl = iconUrl;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public long getRid() {
        if (getSDKBean() != null) {
            return getSDKBean().rid;
        }
        return -1;
    }

    public String getName() {
        if (getSDKBean() == null) {
            return "";
        }
        String name = getSDKBean().name;
        if (TextUtils.isEmpty(name)) {
            return "";
        }
        return name.replace("&nbsp;", " ");
    }

    public String getArtist() {
        if (getSDKBean() == null) {
            return "";
        }
        String artist = getSDKBean().artist;
        if (TextUtils.isEmpty(artist)) {
            return "";
        }
        return artist.replace("&nbsp;", " ");
    }

    public long getArtistId() {
        return getSDKBean().artistId;
    }

    public String getAlbum() {
        if (getSDKBean() == null) {
            return "";
        }
        String album = getSDKBean().album;
        if (TextUtils.isEmpty(album)) {
            return "";
        }
        return album.replace("&nbsp;", " ");
    }

    public int getDuration() {
        return getSDKBean().duration;
    }

    public String getTag() {
        return getSDKBean().tag;
    }

    public boolean isHasMv() {
        return getSDKBean().hasMv;
    }

    public String getMvQuality() {
        return getSDKBean().mvQuality;
    }

    public String getMvIconUrl() {
        return getSDKBean().mvIconUrl;
    }

    public int getHasKalaok() {
        return getSDKBean().hasKalaok;
    }

    public int getHot() {
        return getSDKBean().hot;
    }

    public String getAudioId() {
        return getSDKBean().audioId;
    }

    public String getFloatAdId() {
        return getSDKBean().floatAdId;
    }

    public String getSource() {
        return getSDKBean().source;
    }

    public String getFilePath() {
        return getSDKBean().filePath;
    }

    public String getFileFormat() {
        return getSDKBean().fileFormat;
    }

    public long getFileSize() {
        return getSDKBean().fileSize;
    }

    public long getDownSize() {
        return getSDKBean().downSize;
    }

    public void setEq(boolean var1) {
        getSDKBean().setEq(var1);
    }

    public void setFlac(boolean var1) {
        getSDKBean().setFlac(var1);
    }

    public void setLocalFileExist(boolean var1) {
        getSDKBean().setLocalFileExist(var1);
    }

    public void setChargeType(int var1) {
        getSDKBean().setChargeType(var1);
    }

    public int getChargeType() {
        return getSDKBean().getChargeType();
    }

    public void setExt(String var1) {
        getSDKBean().setExt(var1);
    }

    public String getExt() {
        return getSDKBean().getExt();
    }

    public boolean isPlayFree() {
        return getSDKBean().isPlayFree();
    }

    public boolean isDownloadFree() {
        return getSDKBean().isDownloadFree();
    }

    public boolean isLocalFile() {
        return getSDKBean().isLocalFile();
    }

    public boolean isEQ() {
        return getSDKBean().isEQ();
    }

    public boolean isFLAC() {
        return getSDKBean().isFLAC();
    }

    public boolean hasHighMv() {
        return getSDKBean().hasHighMv();
    }

    public boolean hasLowMv() {
        return getSDKBean().hasLowMv();
    }

    public boolean addResource(XMNetResource var1) {
        return getSDKBean().addResource(var1.getSDKBean());
    }

    public boolean addResource(@IKuwoConstant.IMusicQuality int var1, int var2, @IKuwoConstant.IMusicFormat int var3, int var4) {
        final MusicQuality musicQuality = IKuwoConstant.getMusicQuality(var1);
        final MusicFormat musicFormat = IKuwoConstant.getMusicFormat(var3);
        return getSDKBean().addResource(musicQuality, var2, musicFormat, var4);
    }

    public XMNetResource getResource(@IKuwoConstant.IMusicQuality int var1) {
        final NetResource resource = getSDKBean().getResource(IKuwoConstant.getMusicQuality(var1));
        return new XMNetResource(resource);
    }

    public XMNetResource getBestResource() {
        final NetResource resource = getSDKBean().getBestResource();
        return new XMNetResource(resource);
    }

    public XMNetResource getBestResource(@IKuwoConstant.IMusicQuality int var1) {
        final NetResource resource = getSDKBean().getBestResource(IKuwoConstant.getMusicQuality(var1));
        return new XMNetResource(resource);
    }


    public String getResourceStringForDatabase() {
        return getSDKBean().getResourceStringForDatabase();
    }

    public int parseResourceStringFromDatabase(String var1) {
        return getSDKBean().parseResourceStringFromDatabase(var1);
    }

    public int parseResourceStringFromQuku(String var1) {
        return getSDKBean().parseResourceStringFromQuku(var1);
    }

    public long getStorageId() {
        return getSDKBean().getStorageId();
    }

    public void setStorageId(long var1) {
        getSDKBean().setStorageId(var1);
    }

    public boolean vaild() {
        return getSDKBean().vaild();
    }

    public ContentValues getMusicContentValues(long var1) {
        return getSDKBean().getMusicContentValues(var1);
    }

    public ContentValues getMVMusicContentValues(long var1) {
        return getSDKBean().getMVMusicContentValues(var1);
    }

    public boolean getInfoFromDatabase(Cursor var1) {
        return getSDKBean().getInfoFromDatabase(var1);
    }

    public Collection<NetResource> getResourceCollection() {
        return getSDKBean().getResourceCollection();
    }

    public void setResourceCollection(Collection<NetResource> var1) {
        getSDKBean().setResourceCollection(var1);
    }

    public String toDebugString() {
        if (getSDKBean() != null) {
            return getSDKBean().toDebugString();
        }
        return "";
    }

    public static boolean isEmpty(XMMusic music) {
        return music == null || music.getSDKBean() == null;
    }

    public static String toJson(XMMusic music) {
        return GsonHelper.toJson(music);
    }

    public static XMMusic fromJson(String data) {
        return GsonHelper.fromJson(data, new TypeToken<XMMusic>() {
        }.getType());
    }
}
