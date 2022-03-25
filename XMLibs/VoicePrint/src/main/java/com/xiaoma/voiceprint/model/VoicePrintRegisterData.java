package com.xiaoma.voiceprint.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author KY
 * @date 11/23/2018
 */
public class VoicePrintRegisterData implements Serializable {
    private String userName;
    private String clientId;
    private List<String> voiceFileIds;

    public VoicePrintRegisterData(String userName, List<String> voiceFileIds) {
        this.userName = userName;
        this.voiceFileIds = voiceFileIds;
    }

    public String getUserName() {
        return userName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<String> getVoiceFileIds() {
        return voiceFileIds;
    }

    public static VoicePrintRegisterData obtain(String userName, List<String> voiceFileIds) {
        return new VoicePrintRegisterData(userName, voiceFileIds);
    }
}
