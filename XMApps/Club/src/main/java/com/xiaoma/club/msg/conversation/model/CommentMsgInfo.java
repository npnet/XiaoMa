package com.xiaoma.club.msg.conversation.model;


import java.io.Serializable;

/**
 * Author: loren
 * Date: 2018/10/10 0010
 */

public class CommentMsgInfo implements Serializable {

    String commentFriendIconUrl;
    String commentFriendName;
    String commentMsgDate;
    String commentNewMsg;
    String shareIconUrl;
    String shareTitle;
    String shareContent;
    String shareDate;

    public String getCommentFriendIconUrl() {
        return commentFriendIconUrl;
    }

    public void setCommentFriendIconUrl(String commentFriendIconUrl) {
        this.commentFriendIconUrl = commentFriendIconUrl;
    }

    public String getCommentFriendName() {
        return commentFriendName;
    }

    public void setCommentFriendName(String commentFriendName) {
        this.commentFriendName = commentFriendName;
    }

    public String getCommentMsgDate() {
        return commentMsgDate;
    }

    public void setCommentMsgDate(String commentMsgDate) {
        this.commentMsgDate = commentMsgDate;
    }

    public String getCommentNewMsg() {
        return commentNewMsg;
    }

    public void setCommentNewMsg(String commentNewMsg) {
        this.commentNewMsg = commentNewMsg;
    }

    public String getShareIconUrl() {
        return shareIconUrl;
    }

    public void setShareIconUrl(String shareIconUrl) {
        this.shareIconUrl = shareIconUrl;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public String getShareDate() {
        return shareDate;
    }

    public void setShareDate(String shareDate) {
        this.shareDate = shareDate;
    }
}
