package com.xiaoma.music.kuwo.impl;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.utils.log.KLog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.kuwo.base.bean.MusicFormat;
import cn.kuwo.base.bean.MusicQuality;
import cn.kuwo.open.ImageSize;
import cn.kuwo.open.base.ArtistType;
import cn.kuwo.open.base.MusicChargeType;
import cn.kuwo.open.base.SearchType;
import cn.kuwo.service.PlayProxy;

/**
 * <pre>
 *  author : Jir
 *  date : 2018/7/30
 *  description :
 * </pre>
 */
public class IKuwoConstant {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IAudioStatus.INIT, IAudioStatus.PLAYING,
            IAudioStatus.BUFFERING, IAudioStatus.PAUSE,
            IAudioStatus.STOP, IAudioStatus.FAILED})
    public @interface IAudioStatus {
        int INIT = 0;
        int PLAYING = 1;
        int BUFFERING = 2;
        int PAUSE = 3;
        int STOP = 4;
        int FAILED = 5;
    }

    public static int getPlayStatus(PlayProxy.Status status) {
        int audioState = -1;
        switch (status) {
            case INIT:
                audioState = PlayStatus.INIT;
                break;
            case PLAYING:
                audioState = PlayStatus.PLAYING;
                break;
            case BUFFERING:
                audioState = PlayStatus.BUFFERING;
                break;
            case PAUSE:
                audioState = PlayStatus.PAUSE;
                break;
            case STOP:
                audioState = PlayStatus.STOP;
                break;
            default:
                KLog.d("Error audio status " + status);
                break;
        }
        return audioState;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ISearchStatus.SUCCESS, ISearchStatus.FAILURE,
            ISearchStatus.LOADING, ISearchStatus.NET_UNAVALIABLE,
            ISearchStatus.SEARCH_NULL})
    public @interface ISearchStatus {
        int SUCCESS = 0;
        int FAILURE = 1;
        int LOADING = 2;
        int NET_UNAVALIABLE = 3;
        int SEARCH_NULL = 4;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IArtistType.CHINESE_ALL, IArtistType.CHINESE_MALE, IArtistType.CHINESE_FEMALE, IArtistType.CHINESE_GROUP,
            IArtistType.ASIAN_All, IArtistType.ASIAN_MALE, IArtistType.ASIAN_FEMALE, IArtistType.ASIAN_GROUP,
            IArtistType.WESTERN_All, IArtistType.WESTERN_MALE, IArtistType.WESTERN_FEMALE, IArtistType.WESTERN_GROUP})
    public @interface IArtistType {
        int CHINESE_ALL = 0;
        int CHINESE_MALE = 1;
        int CHINESE_FEMALE = 2;
        int CHINESE_GROUP = 3;
        int ASIAN_All = 4;
        int ASIAN_MALE = 5;
        int ASIAN_FEMALE = 6;
        int ASIAN_GROUP = 7;
        int WESTERN_All = 8;
        int WESTERN_MALE = 9;
        int WESTERN_FEMALE = 10;
        int WESTERN_GROUP = 11;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ISearchType.MUSIC, ISearchType.ARTIST,
            ISearchType.ALBUM, ISearchType.SONGLIST})
    public @interface ISearchType {
        int MUSIC = 0;
        int ARTIST = 1;
        int ALBUM = 2;
        int SONGLIST = 3;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IMusicFormat.NONE, IMusicFormat.AAC, IMusicFormat.MP3,
            IMusicFormat.MP4, IMusicFormat.WMA, IMusicFormat.APE,
            IMusicFormat.FLAC})
    public @interface IMusicFormat {
        int NONE = 0;
        int AAC = 1;
        int MP3 = 2;
        int MP4 = 3;
        int WMA = 4;
        int APE = 5;
        int FLAC = 6;
    }

    public static MusicFormat getMusicFormat(@IMusicFormat int format) {
        MusicFormat musicFormat = null;
        switch (format) {
            case IMusicFormat.NONE:
                musicFormat = MusicFormat.NONE;
                break;
            case IMusicFormat.AAC:
                musicFormat = MusicFormat.AAC;
                break;
            case IMusicFormat.APE:
                musicFormat = MusicFormat.APE;
                break;
            case IMusicFormat.FLAC:
                musicFormat = MusicFormat.FLAC;
                break;
            case IMusicFormat.MP3:
                musicFormat = MusicFormat.MP3;
                break;
            case IMusicFormat.MP4:
                musicFormat = MusicFormat.MP4;
                break;
            case IMusicFormat.WMA:
                musicFormat = MusicFormat.WMA;
                break;
        }
        return musicFormat;
    }

    public static SearchType getSearchType(@ISearchType int type) {
        SearchType searchType = null;
        switch (type) {
            case ISearchType.ALBUM:
                searchType = SearchType.ALBUM;
                break;
            case ISearchType.ARTIST:
                searchType = SearchType.ARTIST;
                break;
            case ISearchType.MUSIC:
                searchType = SearchType.MUSIC;
                break;
            case ISearchType.SONGLIST:
                searchType = SearchType.SONGLIST;
                break;
        }
        return searchType;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IMusicQuality.AUTO, IMusicQuality.FLUENT,
            IMusicQuality.HIGH_QUALITY, IMusicQuality.PERFECT, IMusicQuality.LOSS_LESS})
    public @interface IMusicQuality {
        int AUTO = 0;
        int FLUENT = 1;
        int HIGH_QUALITY = 2;
        int PERFECT = 3;
        int LOSS_LESS = 4;
    }

    public static @IMusicQuality int convertIMusicQuality(MusicQuality quality){
        int musicQuality = IMusicQuality.AUTO;
        switch (quality){
            case AUTO:
                musicQuality = IMusicQuality.AUTO;
                break;
            case FLUENT:
                musicQuality = IMusicQuality.FLUENT;
                break;
            case HIGHQUALITY:
                musicQuality = IMusicQuality.HIGH_QUALITY;
                break;
            case PERFECT:
                musicQuality = IMusicQuality.PERFECT;
                break;
            case LOSSLESS:
                musicQuality = IMusicQuality.LOSS_LESS;
                break;
        }
        return musicQuality;
    }

    public static MusicQuality getMusicQuality(@IMusicQuality int size) {
        MusicQuality musicQuality = null;
        switch (size) {
            case IMusicQuality.AUTO:
                musicQuality = MusicQuality.AUTO;
                break;
            case IMusicQuality.FLUENT:
                musicQuality = MusicQuality.FLUENT;
                break;
            case IMusicQuality.HIGH_QUALITY:
                musicQuality = MusicQuality.HIGHQUALITY;
                break;
            case IMusicQuality.PERFECT:
                musicQuality = MusicQuality.PERFECT;
                break;
            case IMusicQuality.LOSS_LESS:
                musicQuality = MusicQuality.LOSSLESS;
                break;

        }
        return musicQuality;
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IImageSize.SIZE_70, IImageSize.SIZE_120,
            IImageSize.SIZE_240, IImageSize.SIZE_320})
    public @interface IImageSize {
        int SIZE_70 = 0;
        int SIZE_120 = 1;
        int SIZE_240 = 2;
        int SIZE_320 = 3;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({XMMusicChargeType.FREE, XMMusicChargeType.NEED_VIP,
            XMMusicChargeType.NEED_SONG, XMMusicChargeType.NEED_ALBUM, XMMusicChargeType.NEED_VIP_SONG,
            XMMusicChargeType.NEED_VIP_ALBUM, XMMusicChargeType.NO_COPYRIGHT})
    public @interface XMMusicChargeType {
        int FREE = 0;
        int NEED_VIP = 1;
        int NEED_SONG = 2;
        int NEED_ALBUM = 3;
        int NEED_VIP_SONG = 4;
        int NEED_VIP_ALBUM = 5;
        int NO_COPYRIGHT = 6;
    }

    public static int convertChargeType(MusicChargeType type) {
        @XMMusicChargeType int chargeType;
        switch (type) {
            case FREE:
                chargeType = XMMusicChargeType.FREE;
                break;
            case NEED_VIP:
                chargeType = XMMusicChargeType.NEED_VIP;
                break;
            case NEED_SONG:
                chargeType = XMMusicChargeType.NEED_SONG;
                break;
            case NEED_ALBUM:
                chargeType = XMMusicChargeType.NEED_ALBUM;
                break;
            case NEED_VIP_SONG:
                chargeType = XMMusicChargeType.NEED_VIP_SONG;
                break;
            case NEED_VIP_ALBUM:
                chargeType = XMMusicChargeType.NEED_VIP_ALBUM;
                break;
            case NO_COPYRIGHT:
                chargeType = XMMusicChargeType.NO_COPYRIGHT;
                break;
            default:
                chargeType = XMMusicChargeType.FREE;
                break;
        }
        return chargeType;
    }

    public static ImageSize getImageSize(@IImageSize int size) {
        ImageSize imageSize = null;
        switch (size) {
            case IImageSize.SIZE_70:
                imageSize = ImageSize.SIZE_70;
                break;
            case IImageSize.SIZE_120:
                imageSize = ImageSize.SIZE_120;
                break;
            case IImageSize.SIZE_240:
                imageSize = ImageSize.SIZE_240;
                break;
            case IImageSize.SIZE_320:
                imageSize = ImageSize.SIZE_320;
                break;
        }
        return imageSize;
    }

    @Nullable
    public static ArtistType getArtistType(@IKuwoConstant.IArtistType int type) {
        ArtistType artistType = null;
        switch (type) {
            case IKuwoConstant.IArtistType.ASIAN_All:
                artistType = ArtistType.AsianAll;
                break;
            case IKuwoConstant.IArtistType.ASIAN_FEMALE:
                artistType = ArtistType.AsianFemale;
                break;
            case IKuwoConstant.IArtistType.ASIAN_GROUP:
                artistType = ArtistType.AsianGroup;
                break;
            case IKuwoConstant.IArtistType.ASIAN_MALE:
                artistType = ArtistType.AsianMale;
                break;
            case IKuwoConstant.IArtistType.CHINESE_ALL:
                artistType = ArtistType.ChineseAll;
                break;
            case IKuwoConstant.IArtistType.CHINESE_FEMALE:
                artistType = ArtistType.ChineseFemale;
                break;
            case IKuwoConstant.IArtistType.CHINESE_GROUP:
                artistType = ArtistType.ChineseGroup;
                break;
            case IKuwoConstant.IArtistType.CHINESE_MALE:
                artistType = ArtistType.ChineseMale;
                break;
            case IKuwoConstant.IArtistType.WESTERN_All:
                artistType = ArtistType.WesternAll;
                break;
            case IKuwoConstant.IArtistType.WESTERN_FEMALE:
                artistType = ArtistType.WesternFemale;
                break;
            case IKuwoConstant.IArtistType.WESTERN_GROUP:
                artistType = ArtistType.WesternGroup;
                break;
            case IKuwoConstant.IArtistType.WESTERN_MALE:
                artistType = ArtistType.WesternMale;
                break;
            default:
                break;
        }
        return artistType;
    }


    public static class AudioHelper {

        public static boolean isPlayOrBuffering(@IAudioStatus int status) {
            return status == IAudioStatus.PLAYING
                    || status == IAudioStatus.BUFFERING;
        }
    }

}
