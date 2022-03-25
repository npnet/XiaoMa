package com.xiaoma.motorcade.setting.repo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.xiaoma.motorcade.common.manager.ModelRepo;
import com.xiaoma.motorcade.common.manager.MotorcadeRepo;
import com.xiaoma.motorcade.common.model.GroupMemberInfo;
import com.xiaoma.motorcade.setting.model.MemberInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 简介: 主页面车队列表缓存处理类
 *
 * @author lingyan
 */
@Dao
public abstract class CarTeamMembersRepo extends ModelRepo<MemberInfo> {
    @Query("DELETE FROM MemberInfo")
    abstract protected void clearAll();

    @Query(" SELECT * FROM MemberInfo motorcade LEFT JOIN GroupMemberInfo info ON  motorcade.memberId=info.id")
    abstract public List<GroupMemberInfo> getcarTeamList();

    public void saveMotorcadeLists(Collection<GroupMemberInfo> memberInfos) {
        if (memberInfos == null)
            return;
        MotorcadeRepo.getInstance().getMemberRepo().putAll(memberInfos);
        final List<MemberInfo> carTeamsArr = new ArrayList<>(memberInfos.size());
        for (final GroupMemberInfo memberInfo : memberInfos) {
            final MemberInfo info = new MemberInfo();
            info.setMemberId(memberInfo.getId());
            carTeamsArr.add(info);
        }
        clearAll();
        putAll(carTeamsArr);
    }

    @Override
    protected String getTableName() {
        return MemberInfo.class.getSimpleName();
    }
}
