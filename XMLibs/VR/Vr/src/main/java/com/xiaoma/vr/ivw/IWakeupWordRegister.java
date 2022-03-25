package com.xiaoma.vr.ivw;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/9/18
 * Desc:唤醒词注册接口
 */
public interface IWakeupWordRegister {


    /**
     * 注册one shot唤醒词，是否唤醒由决策者决定,唤醒词阈值使用默认的统一阈值
     *
     * @param wakeupWord 唤醒词 key:唤醒词的拼音,value:唤醒词对应的中文
     */
    boolean registerOneShotWakeupWord(List<String> wakeupWord);


    /**
     * 通过映射关系注销唤醒词，映射关系与注册时的映射关系一样，则注销，否则不注销
     *
     * @param wakeupWord 需要注销的唤醒词, key:唤醒词的拼音,value:唤醒词映射的中文
     */
    boolean unregisterOneShotWakeupWord(List<String> wakeupWord);

}
