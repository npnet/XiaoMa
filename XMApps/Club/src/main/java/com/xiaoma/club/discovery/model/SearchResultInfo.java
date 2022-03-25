package com.xiaoma.club.discovery.model;

import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.model.User;

import java.util.List;

/**
 * Author: loren
 * Date: 2019/1/10 0010
 */

public class SearchResultInfo {

    List<GroupCardInfo> quns;
    List<User> users;

    public List<GroupCardInfo> getQuns() {
        return quns;
    }

    public void setQuns(List<GroupCardInfo> quns) {
        this.quns = quns;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public int getGroupCount() {
        if (quns != null && !quns.isEmpty()) {
            return quns.size();
        }
        return 0;
    }

    public int getUserCount() {
        if (users != null && !users.isEmpty()) {
            return users.size();
        }
        return 0;
    }
}
