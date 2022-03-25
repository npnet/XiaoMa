package com.xiaoma.instructiondistribute.contract;

import android.media.AudioManager;

/**
 * <des>
 * [WARN] 目前采用的是下发到各个APP进行使用,所以这里目前只使用SerialNo即可 !!!
 *
 * @author YangGang
 * @date 2019/7/9
 */
public enum AudioSourceType {

    DEFAULT(0x00, 0),
    UNKNOWN_MODE(0xFF, 0),
    AM(0x01, AudioManager.STREAM_RADIO),
    FM(0x02, AudioManager.STREAM_RADIO),
    USB_AUDIO(0x03, AudioManager.STREAM_MUSIC),
    BT_Audio(0x04, AudioManager.STREAM_BLUETOOTH_MUSIC),
    // TODO: 2019/7/10  stream 暂时用这个,后面再变动
    IDEVICE(0x05, AudioManager.STREAM_BLUETOOTH_MUSIC),//废弃,暂不使用
    USB_VIDEO(0x06, AudioManager.STREAM_MUSIC),
    TBOX(0x07, AudioManager.STREAM_BLUETOOTH_MUSIC), //不涉及到焦点
    RESERVED(0x08, AudioManager.STREAM_BLUETOOTH_MUSIC);

    private int mSerialNo;
    private int mStream;

    AudioSourceType(int serialNo, int stream) {
        mSerialNo = serialNo;
        mStream = stream;
    }

    public int getSerialNo() {
        return mSerialNo;
    }

    public int getStream() {
        return mStream;
    }

    public static String getAudioSource(int serialNo) {
        AudioSourceType[] sourceTypeList = AudioSourceType.values();
        for (AudioSourceType source : sourceTypeList) {
            if (source.mSerialNo == serialNo) {
                return source.name();
            }
        }
        return "UN_VALID";
    }

    public static AudioSourceType convert2AudioSourceType(int audioType) {
        AudioSourceType[] sourceTypeList = AudioSourceType.values();
        for (AudioSourceType source : sourceTypeList) {
            if (source.mSerialNo == audioType) {
                return source;
            }
        }
        return AudioSourceType.UNKNOWN_MODE;
    }
}
