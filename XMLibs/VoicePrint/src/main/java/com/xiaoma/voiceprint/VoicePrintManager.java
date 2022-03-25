package com.xiaoma.voiceprint;

import android.content.Context;

import com.xiaoma.voiceprint.model.ICallback;
import com.xiaoma.voiceprint.model.VoicePrintRegisterData;

import java.io.File;
import java.util.List;

/**
 * @author KY
 * @date 11/22/2018
 */
public class VoicePrintManager {
    private static VoicePrintManager instance;
    private IVoicePrint voicePrintSDK;

    public static VoicePrintManager getInstance() {
        if (instance == null) {
            synchronized (VoicePrintManager.class) {
                if (instance == null) {
                    instance = new VoicePrintManager();
                }
            }
        }
        return instance;
    }

    private VoicePrintManager() {
        voicePrintSDK = new VoicePrintSDK();
    }

    public void init(Context context) {
        voicePrintSDK.init(context);
    }

    public void getTrainingText(ICallback.GetTextCallback callback) {
        voicePrintSDK.getTrainingText(VoicePrintConstant.VoicePrintConfig.MODE_TYPE,
                VoicePrintConstant.VoicePrintConfig.VOICE_RECORD_COUNT, callback);
    }

    public void uploadVoicePrintFile(File file, final ICallback.UploadCallback callback) {
        voicePrintSDK.uploadVoicePrint(file, callback);
    }

    public void registerVoicePrint(String name, List<String> fileIds, ICallback.AddRegisterCallback registerCallback) {
        VoicePrintRegisterData registerData = VoicePrintRegisterData.obtain(name, fileIds);
        addUser(registerData, registerCallback);
    }

    public void verifyVoicePrint(String userId, List<String> fileIds, ICallback.VoicePrintVerifyCallback verifyCallback) {
        voicePrintSDK.verifyVoicePrint(userId, VoicePrintConstant.VoicePrintConfig.SRC_SAMPLE_RATE,
                VoicePrintConstant.VoicePrintConfig.MODE_TYPE, fileIds,
                VoicePrintConstant.VoicePrintConfig.LIMIT_COUNT, verifyCallback);
    }

    private void addUser(final VoicePrintRegisterData registerData, final ICallback.AddRegisterCallback registerCallback) {
        voicePrintSDK.addUser(registerData.getUserName(),
                VoicePrintConstant.VoicePrintConfig.USER_DESC, new ICallback.AddUserCallback() {
                    @Override
                    public void onUserAddSuccess(String clientId) {
                        registerData.setClientId(clientId);
                        RealRegisterVoicePrint(registerData, registerCallback);
                    }

                    @Override
                    public void onFailure(String tag, String message) {
                        registerCallback.onFailure(tag, message);
                    }
                });
    }

    private void RealRegisterVoicePrint(final VoicePrintRegisterData registerData, final ICallback.RegisterVoicePrintCallback registerCallback) {
        voicePrintSDK.registerVoicePrint(registerData.getClientId(),
                VoicePrintConstant.VoicePrintConfig.SRC_SAMPLE_RATE,
                VoicePrintConstant.VoicePrintConfig.MODE_TYPE,
                registerData.getVoiceFileIds(), new ICallback.RegisterVoicePrintCallback() {
                    @Override
                    public void onRegisterSuccess(String clientId) {
                        registerCallback.onRegisterSuccess(clientId);
                    }

                    @Override
                    public void onFailure(String tag, String message) {
                        registerCallback.onFailure(tag, message);
                    }
                });
    }
}
