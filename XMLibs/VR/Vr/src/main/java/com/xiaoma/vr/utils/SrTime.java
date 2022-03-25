package com.xiaoma.vr.utils;

/**
 * Created with IntelliJ IDEA.
 * User: blusehuang
 * Date: 2016-7-29
 * Desc:记录一路识别Session的时间
 */
public class SrTime {

    private static SrTime instance = null;

    public static SrTime getInstance() {
        if (instance == null) {
            instance = new SrTime();
        }
        return instance;
    }

    public long iStartSrTime = 0; // 开始识别的时间
    public long iStartRecordTime = 0; // 开始录音时间
    public long iGetFirstAudioTime = 0; // 取得第一块录音的时间
    public long iSpeechStartTime = 0; // 检测到语音开始的时间
    public long iSpeechEndTime = 0; // 检测到语音结束的时间
    public long iEndAudioDataTime = 0; // 主动结束录音的时间
    public long iRecEndTime = 0; // 识别结束时间

    public void reset() {
        iStartSrTime = 0;
        iStartRecordTime = 0;
        iGetFirstAudioTime = 0;
        iSpeechStartTime = 0;
        iSpeechEndTime = 0;
        iEndAudioDataTime = 0;
        iRecEndTime = 0;
    }


}
