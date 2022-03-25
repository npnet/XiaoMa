package com.xiaoma.club.contact.repo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.ModelRepo;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.contact.model.ContactUser;
import com.xiaoma.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LKF on 2019-2-27 0027.
 * 通讯录用户数据仓库
 */
@Dao
public abstract class ContactUserRepo extends ModelRepo<ContactUser> {
    @Query("DELETE FROM ContactUser WHERE uid=:uid")
    abstract void clearContactUsers(long uid);

    @Query("SELECT * FROM  ContactUser contactUser " +
            "LEFT JOIN User user " +
            "ON contactUser.uid=:uid AND contactUser.contactUid=user.id " +
            "WHERE user.id > 0")
    abstract protected List<User> getContactUsers(long uid);

    public List<User> getContactUsers() {
        return getContactUsers(UserUtil.getCurrentUid());
    }

    public void saveContactUsers(List<User> users) {
        if (users == null)
            return;
        long myUid = UserUtil.getCurrentUid();
        if (myUid != 0) {
            ClubRepo.getInstance().getUserRepo().insertAll(users);
            final List<ContactUser> contactGroups = new ArrayList<>();
            for (final User group : users) {
                contactGroups.add(new ContactUser(myUid, group.getId()));
            }
            clearContactUsers(myUid);
            insertAll(contactGroups);
        }
    }

    @Override
    protected String getTableName() {
        return ContactUser.class.getSimpleName();
    }
}
