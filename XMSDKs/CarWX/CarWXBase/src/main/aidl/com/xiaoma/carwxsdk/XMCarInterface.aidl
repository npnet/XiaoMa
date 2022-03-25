// XMCarInterface.aidl
package com.xiaoma.carwxsdk;

import com.xiaoma.carwxsdk.TtsListener;
import com.xiaoma.carwxsdk.AsrListener;
import com.xiaoma.carwxsdk.ClientListener;
import com.xiaoma.carwxsdk.SpeedChangeListener;

interface XMCarInterface {

    void startNaviByPoi(double lat,double lon,String name);

    void startNaviByKey(String keyWords);

    boolean hasConnectedBluetoothDevice();

    void callPhone(String phoneNumber);

    void startTTS(String id,String speakContent);

    void stopTTS();

    void setTTSListener(TtsListener listener);

    void startRecord(/*int vadTime,*/boolean needPunctuation);

    void finishRecord();

    void cancelRecord();

    void setASRListener(AsrListener listener);

    void uploadContact(in List<String> contacts,ClientListener callback);

    void setSpeedChangeListener(SpeedChangeListener listener);

    String getCarVin();

    String getSerialNumber();

    int getCurrentTheme();
}
