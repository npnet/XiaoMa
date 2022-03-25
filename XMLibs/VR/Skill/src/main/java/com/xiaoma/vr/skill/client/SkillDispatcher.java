package com.xiaoma.vr.skill.client;

import com.xiaoma.vr.skill.model.Skill;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2019/6/18
 */
public class SkillDispatcher {
    private static SkillDispatcher instance;
    private List<SkillHandler> handlerList = new ArrayList<>();

    public static SkillDispatcher getInstance() {
        if (instance == null) {
            synchronized (SkillDispatcher.class) {
                if (instance == null) {
                    instance = new SkillDispatcher();
                }
            }
        }
        return instance;
    }

    public boolean notifySkill(String command, Skill skill, SkillCallback callback) {
        if (handlerList.size() <= 0) {
            return false;
        }
        for (int index = handlerList.size() - 1; index >= 0; index--) {
            SkillHandler handler = handlerList.get(index);
            if (handler == null) {
                continue;
            }
            boolean handled = handler.onSkill(command, skill, callback);
            if (handled) {
                return true;
            }
        }
        return false;
    }

    public boolean addSkillHandler(SkillHandler handler) {
        if (handler == null) {
            return false;
        }
        if (handlerList.contains(handler)) {
            return true;
        }
        return handlerList.add(handler);
    }

    public boolean removeSkillHandler(SkillHandler handler) {
        return handlerList.remove(handler);
    }
}
