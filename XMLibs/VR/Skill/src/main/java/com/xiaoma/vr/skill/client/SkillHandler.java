package com.xiaoma.vr.skill.client;

import com.xiaoma.vr.skill.model.Skill;

/**
 * @author youthyJ
 * @date 2019/6/18
 */
public interface SkillHandler {
    boolean onSkill(String command, Skill skill, SkillCallback callback);
}
