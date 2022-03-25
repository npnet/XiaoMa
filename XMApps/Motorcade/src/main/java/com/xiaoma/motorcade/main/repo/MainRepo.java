package com.xiaoma.motorcade.main.repo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.xiaoma.motorcade.common.manager.ModelRepo;
import com.xiaoma.motorcade.common.manager.MotorcadeRepo;
import com.xiaoma.motorcade.common.model.GroupCardInfo;
import com.xiaoma.motorcade.main.model.MototrcadeGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 简介: 主页面车队列表缓存处理类
 *
 * @author lingyan
 */
@Dao
public abstract class MainRepo extends ModelRepo<MototrcadeGroup> {
    @Query("DELETE FROM MototrcadeGroup")
    abstract protected void clearAll();

    @Query(" SELECT * FROM MototrcadeGroup motorcade LEFT JOIN GroupCardInfo info ON  motorcade.carTeamId=info.id")
    abstract public List<GroupCardInfo> getcarTeamList();

    public void saveMotorcadeLists(Collection<GroupCardInfo> motorcadeLists) {
        if (motorcadeLists == null)
            return;
        MotorcadeRepo.getInstance().getGroupRepo().putAll(motorcadeLists);
        final List<MototrcadeGroup> carTeamsArr = new ArrayList<>(motorcadeLists.size());
        for (final GroupCardInfo group : motorcadeLists) {
            final MototrcadeGroup mototrcadeGroup = new MototrcadeGroup();
            mototrcadeGroup.setCarTeamId(group.getId());
            carTeamsArr.add(mototrcadeGroup);
        }
        clearAll();
        putAll(carTeamsArr);
    }

    @Override
    protected String getTableName() {
        return MototrcadeGroup.class.getSimpleName();
    }
}
