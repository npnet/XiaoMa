package com.xiaoma.xting.player.model;

import com.google.gson.annotations.SerializedName;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/1/2
 */
public class FMTrack {
    @SerializedName("id")
    private String albumId;
    @SerializedName("value")
    private String albumName;
    @SerializedName("h")
    private String programId;
    @SerializedName("j")
    private String programName;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }
}
