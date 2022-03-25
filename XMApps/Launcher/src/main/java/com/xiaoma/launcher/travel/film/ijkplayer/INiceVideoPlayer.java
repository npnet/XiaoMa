package com.xiaoma.launcher.travel.film.ijkplayer;

public interface INiceVideoPlayer {

    //设置视频Url
    void setUrl(String url);

    //开始播放
    void start();
    //开始播放
    void stop();

    //从指定的位置开始播放
    void start(long position);

    //重新播放，播放器被暂停、播放错误、播放完成后，需要调用此方法重新播放
    void restart();

    //暂停播放
    void pause();

    //seek到制定的位置继续播放
    void seekTo(long position);

    //设置音量
    void setVolume(int volume);

    //设置播放速度，目前只有IjkPlayer有效果，原生MediaPlayer暂不支持
    void setSpeed(float speed);

    //以下9个方法是播放器在当前的播放状态
    boolean isIdle();
    boolean isPreparing();
    boolean isPrepared();
    boolean isBufferingPlaying();
    boolean isBufferingPaused();
    boolean isPlaying();
    boolean isPaused();
    boolean isError();
    boolean isCompleted();

    //获取最大音量
    int getMaxVolume();

    //获取当前音量
    int getVolume();

    //获取办法给总时长，毫秒
    long getDuration();

    //获取当前播放的位置，毫秒
    long getCurrentPosition();

    //获取视频缓冲百分比
    int getBufferPercentage();

    //获取播放速度
    float getSpeed(float speed);

    //此处只释放播放器（如果要释放播放器并恢复控制器状态需要调用{@link #release()}方法）
    //不管是全屏、小窗口还是Normal状态下控制器的UI都不恢复初始状态
    //这样以便在当前播放器状态下可以方便的切换不同的清晰度的视频地址
    void releasePlayer();

    //释放INiceVideoPlayer，释放后，内部的播放器被释放掉，同时如果在全屏、小窗口模式下都会退出
    //并且控制器的UI也应该恢复到最初始的状态.
    void release();

    void requestAudioFocus();

    void abandonAudioFocus();

}
