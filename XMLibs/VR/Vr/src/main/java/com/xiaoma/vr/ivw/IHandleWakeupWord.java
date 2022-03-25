package com.xiaoma.vr.ivw;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/23
 * Desc：唤醒词设置相关接口
 */

public interface IHandleWakeupWord {

    boolean setWakeupWord(String word);

    boolean resetWakeupWord();

    List<String> getWakeupWord();

}
