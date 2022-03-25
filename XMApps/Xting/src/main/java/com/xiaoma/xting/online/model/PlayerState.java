package com.xiaoma.xting.online.model;

/**
 * @author youthyJ
 * @date 2018/10/17
 */
public enum PlayerState {
    IDLE,           //播放器IDLE状态
    INITIALIZED,    //播放器已经初始化了
    PREPARING,      //播放器准备中
    PREPARED,       //播放器准备完成
    STARTED,        //播放器开始播放
    STOPPED,        //播放器停止
    PAUSED,         //播放器暂停
    COMPLETED,      //播放器单曲播放完成
    ERROR,          //播放器错误
    END,            //播放器被释放
}
