package com.xiaoma.music.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/12/24 0024
 */
@Table("RecentUsbMusic")
public class RecentUsbMusic implements Parcelable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;
    private UsbMusic music;
    private boolean isCurrentInfo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UsbMusic getMusic() {
        return music;
    }

    public void setMusic(UsbMusic music) {
        this.music = music;
    }

    public boolean isCurrentInfo() {
        return isCurrentInfo;
    }

    public void setCurrentInfo(boolean currentInfo) {
        isCurrentInfo = currentInfo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeParcelable(this.music, 0);
        dest.writeByte(isCurrentInfo ? (byte) 1 : (byte) 0);
    }

    public RecentUsbMusic() {
    }

    protected RecentUsbMusic(Parcel in) {
        this.id = in.readInt();
        this.music = in.readParcelable(UsbMusic.class.getClassLoader());
        this.isCurrentInfo = in.readByte() != 0;
    }

    public static final Parcelable.Creator<RecentUsbMusic> CREATOR = new Parcelable.Creator<RecentUsbMusic>() {
        public RecentUsbMusic createFromParcel(Parcel source) {
            return new RecentUsbMusic(source);
        }

        public RecentUsbMusic[] newArray(int size) {
            return new RecentUsbMusic[size];
        }
    };

    public static List<RecentUsbMusic> covertUsbMusic(UsbMusic usbMusic, List<UsbMusic> usbMusicList) {
        List<RecentUsbMusic> recentUsbMusicList = new ArrayList<>();
        if (usbMusic == null){
            return new ArrayList<>();
        }
        String path = usbMusic.getPath();
        for (UsbMusic music : usbMusicList) {
            RecentUsbMusic recentUsbMusic = new RecentUsbMusic();
            recentUsbMusic.setMusic(music);
            recentUsbMusic.setCurrentInfo(path.equals(music.getPath()));
            recentUsbMusicList.add(recentUsbMusic);
        }
        return recentUsbMusicList;
    }
}
