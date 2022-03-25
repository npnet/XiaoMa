package com.xiaoma.club.msg.conversation.model;


import java.io.Serializable;

/**
 * Author: loren
 * Date: 2018/10/10 0010
 */

public class LikeMsgInfo implements Serializable {

    String likeFriendIconUrl;
    String likeFriendName;
    String likeMsgDate;
    String likeShareIconUrl;
    String likeShareTitle;
    String likeShareContent;
    String likeShareDate;

    public String getLikeFriendIconUrl() {
        return likeFriendIconUrl;
    }

    public void setLikeFriendIconUrl(String likeFriendIconUrl) {
        this.likeFriendIconUrl = likeFriendIconUrl;
    }

    public String getLikeFriendName() {
        return likeFriendName;
    }

    public void setLikeFriendName(String likeFriendName) {
        this.likeFriendName = likeFriendName;
    }

    public String getLikeMsgDate() {
        return likeMsgDate;
    }

    public void setLikeMsgDate(String likeMsgDate) {
        this.likeMsgDate = likeMsgDate;
    }

    public String getLikeShareIconUrl() {
        return likeShareIconUrl;
    }

    public void setLikeShareIconUrl(String likeShareIconUrl) {
        this.likeShareIconUrl = likeShareIconUrl;
    }

    public String getLikeShareTitle() {
        return likeShareTitle;
    }

    public void setLikeShareTitle(String likeShareTitle) {
        this.likeShareTitle = likeShareTitle;
    }

    public String getLikeShareContent() {
        return likeShareContent;
    }

    public void setLikeShareContent(String likeShareContent) {
        this.likeShareContent = likeShareContent;
    }

    public String getLikeShareDate() {
        return likeShareDate;
    }

    public void setLikeShareDate(String likeShareDate) {
        this.likeShareDate = likeShareDate;
    }
}
