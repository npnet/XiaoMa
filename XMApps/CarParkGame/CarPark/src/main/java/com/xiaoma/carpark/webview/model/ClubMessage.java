package com.xiaoma.carpark.webview.model;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/07/03
 *     desc   :
 * </pre>
 */
public class ClubMessage {
    String key;
    String currentPath;
    String avatarUrl;
    String name;
    String uid;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "ClubMessage{" +
                "key='" + key + '\'' +
                ", currentPath='" + currentPath + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
