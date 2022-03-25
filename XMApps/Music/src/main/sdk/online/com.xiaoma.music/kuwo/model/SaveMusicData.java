package com.xiaoma.music.kuwo.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringDef;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.kuwo.base.bean.Music;

/**
 * Created by ZYao.
 * Date ：2018/11/16 0016
 */
@Table("xiaoma_kw_music_save")
public class SaveMusicData implements Parcelable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long id;
    private long rid;
    @SaveMusicType
    private String saveMusicType;
    private Music music;

    public long getRid() {
        return rid;
    }

    public void setRid(long rid) {
        this.rid = rid;
    }

    public String getSaveMusicType() {
        return saveMusicType;
    }

    public void setSaveMusicType(String saveMusicType) {
        this.saveMusicType = saveMusicType;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({SaveMusicType.HISTORY, SaveMusicType.COLLECTION})
    public @interface SaveMusicType {
        String HISTORY = "history";
        String COLLECTION = "collection";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.rid);
        dest.writeString(this.saveMusicType);
        dest.writeParcelable(this.music, 0);
    }

    public SaveMusicData() {
    }

    protected SaveMusicData(Parcel in) {
        this.rid = in.readLong();
        this.saveMusicType = in.readString();
        this.music = in.readParcelable(Music.class.getClassLoader());
    }

    public static final Parcelable.Creator<SaveMusicData> CREATOR = new Parcelable.Creator<SaveMusicData>() {
        public SaveMusicData createFromParcel(Parcel source) {
            return new SaveMusicData(source);
        }

        public SaveMusicData[] newArray(int size) {
            return new SaveMusicData[size];
        }
    };
}
