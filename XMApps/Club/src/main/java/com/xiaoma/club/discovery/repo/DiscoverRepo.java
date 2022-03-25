package com.xiaoma.club.discovery.repo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.ModelRepo;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.discovery.model.DiscoverGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by LKF on 2019-2-26 0026.
 */
@Dao
public abstract class DiscoverRepo extends ModelRepo<DiscoverGroup> {
    @Query("DELETE FROM DiscoverGroup")
    abstract protected void clearAll();

    @Query(" SELECT * FROM DiscoverGroup discover " +
            "LEFT JOIN GroupCardInfo info " +
            "ON  discover.discoverGroupId=info.id " +
            "WHERE info.id > 0")
    abstract public List<GroupCardInfo> getDiscoverGroups();

    public void saveDiscoverGroups(Collection<GroupCardInfo> discoverGroups) {
        if (discoverGroups == null)
            return;
        ClubRepo.getInstance().getGroupRepo().insertAll(discoverGroups);
        final List<DiscoverGroup> discoverGroupsArr = new ArrayList<>(discoverGroups.size());
        for (final GroupCardInfo group : discoverGroups) {
            final DiscoverGroup discoverGroup = new DiscoverGroup();
            discoverGroup.setDiscoverGroupId(group.getId());
            discoverGroupsArr.add(discoverGroup);
        }
        clearAll();
        insertAll(discoverGroupsArr);
    }

    @Override
    protected String getTableName() {
        return DiscoverGroup.class.getSimpleName();
    }
}
