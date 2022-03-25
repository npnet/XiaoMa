package com.xiaoma.music.common.model;

/**
 * Author: loren
 * Date: 2018/11/12 0012
 */

public class BaseCacheInfo {
    private String cacheStream;

    public BaseCacheInfo newInstance() {
        return new BaseCacheInfo();
    }

    public String getCacheStream() {
        return cacheStream;
    }

    public void setCacheStream(String info) {
        this.cacheStream = info;
    }
}
