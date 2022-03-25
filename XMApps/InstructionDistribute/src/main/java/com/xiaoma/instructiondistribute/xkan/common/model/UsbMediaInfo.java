package com.xiaoma.instructiondistribute.xkan.common.model;

import java.io.Serializable;

/**
 * Created by Thomas on 2018/11/14 0014
 */

public class UsbMediaInfo implements Serializable {

    private String mediaName;
    private String path;
    private long size;
    private long date;
    private int fileType;

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMediaName() {
        return mediaName;
    }

    public String getPath() {
        return path;
    }

}
