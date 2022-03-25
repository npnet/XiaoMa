package com.xiaoma.club.common.util;

import android.text.TextUtils;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.utils.GsonHelper;

/**
 * Author: loren
 * Date: 2019/2/18 0018
 * 向后台上报部落热度计分
 */

public class UploadGroupScoreManager {

    public static UploadGroupScoreManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final UploadGroupScoreManager instance = new UploadGroupScoreManager();
    }

    /**
     * 上报用户点击查看部落活动
     *
     * @param groupId
     * @param activity 活动
     */
    public void uploadGroupActivity(String groupId, String activity) {
        if (TextUtils.isEmpty(activity) || TextUtils.isEmpty(groupId)) {
            return;
        }
        UploadGroupScoreModel model = new UploadGroupScoreModel();
        model.setType(ClubEventConstants.ClubScoreType.CHECK_GROUP_ACTIVITY);
        model.setGroupId(groupId);
        model.setContent(activity);
        XmAutoTracker.getInstance().onEventClubScore(GsonHelper.toJson(model));
    }

    /**
     * 上报用户发送一条部落消息
     *
     * @param groupId
     * @param msgType 消息内容
     */
    public void uploadGroupMessage(String groupId, String msgType) {
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(msgType)) {
            return;
        }
        GroupCardInfo info = ClubRepo.getInstance().getGroupRepo().get(groupId);
        if (info != null) {
            String qunId = String.valueOf(info.getId());
            UploadGroupScoreModel model = new UploadGroupScoreModel();
            model.setType(ClubEventConstants.ClubScoreType.SEND_GROUP_MESSAGE);
            model.setGroupId(qunId);
            model.setContent(msgType);
            XmAutoTracker.getInstance().onEventClubScore(GsonHelper.toJson(model));
        }
    }

    class UploadGroupScoreModel {
        @ClubEventConstants.ClubScoreType
        int type;

        String groupId;

        String content;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
