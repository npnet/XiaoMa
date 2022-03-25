package com.xiaoma.club.contact.repo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.ModelRepo;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.contact.model.ContactGroup;
import com.xiaoma.club.contact.model.GroupCardInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LKF on 2019-2-27 0027.
 * 通讯录群组数据仓库
 */
@Dao
public abstract class ContactGroupRepo extends ModelRepo<ContactGroup> {
    @Query("DELETE FROM ContactGroup WHERE uid=:uid")
    abstract protected void clearContactGroups(long uid);

    @Query("SELECT * FROM  ContactGroup contactGroup " +
            "LEFT JOIN GroupCardInfo groupCardInfo " +
            "ON contactGroup.uid=:uid AND contactGroup.groupId==groupCardInfo.id " +
            "WHERE groupCardInfo.id>0")
    abstract protected List<GroupCardInfo> getContactGroups(long uid);

    public List<GroupCardInfo> getContactGroups() {
        return getContactGroups(UserUtil.getCurrentUid());
    }

    public void saveContactGroups(List<GroupCardInfo> groups) {
        long myUid = UserUtil.getCurrentUid();
        if (myUid == 0)
            return;
        if (groups == null || groups.isEmpty()) {
            clearContactGroups(myUid);
            return;
        }
        ClubRepo.getInstance().getGroupRepo().insertAll(groups);
        final List<ContactGroup> contactGroups = new ArrayList<>();
        for (final GroupCardInfo group : groups) {
            contactGroups.add(new ContactGroup(myUid, group.getId()));
        }
        clearContactGroups(myUid);
        insertAll(contactGroups);
    }

    @Override
    protected String getTableName() {
        return ContactGroup.class.getSimpleName();
    }
}
