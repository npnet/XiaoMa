package com.xiaoma.login.sdk;

import android.car.hardware.CarVendorExtensionManager;
import android.car.hardware.vendor.CanOnOff2;
import android.car.hardware.vendor.DmsGuideIndicate;
import android.car.hardware.vendor.RecognizeState;
import android.car.hardware.vendor.UserIdAction;
import android.car.hardware.vendor.UserIdRecognize;
import android.content.Context;
import android.util.Log;

import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.utils.log.KLog;

import java.util.concurrent.CopyOnWriteArrayList;

import static com.xiaoma.login.sdk.FaceId.FaceId1;
import static com.xiaoma.login.sdk.FaceId.FaceId2;
import static com.xiaoma.login.sdk.FaceId.FaceId3;
import static com.xiaoma.login.sdk.FaceId.FaceId4;
import static com.xiaoma.login.sdk.FaceId.FaceId5;


/**
 * Created by kaka
 * on 19-5-22 下午7:44
 * <p>
 * desc: #a
 * </p>
 */
public class FaceSDK implements IFace {
    private static final String TAG = FaceSDK.class.getSimpleName();
    private static FaceSDK mFaceSDK;
    private static Status mStatus = Status.idle;
    private static boolean isWaitCallback;
    private CopyOnWriteArrayList<IdentifyListener> identifyListeners = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<IRecordListener> recordListeners = new CopyOnWriteArrayList<>();
    private boolean mHasInit;

    public static FaceSDK getInstance() {
        if (mFaceSDK == null) {
            synchronized (FaceSDK.class) {
                if (mFaceSDK == null) {
                    mFaceSDK = new FaceSDK();
                }
            }
        }

        return mFaceSDK;
    }


    @Override
    public synchronized void init(Context context) {
        if (mHasInit)
            return;
        XmCarVendorExtensionManager.getInstance().init(context);
        XmCarVendorExtensionManager.getInstance().addValueChangeListener(new XmCarVendorExtensionManager.ValueChangeListener() {
            @Override
            public void onChange(int id, Object value) {
                if (id == CarVendorExtensionManager.ID_DMS_GUIDE_INDICATE) {
                    Log.e(TAG, "onChange: " + DmsGuideIndicate.toString((Integer) value) + "; status:" + mStatus);
                    if (mStatus == Status.recording) {
                        handleRecordStatus((int) value);
                    }
                } else if (id == CarVendorExtensionManager.ID_RECOGNIZE_STATE) {
                    Log.e(TAG, "onChange: " + RecognizeState.toString((Integer) value) + "; status:" + mStatus);
                    if (mStatus == Status.identifying) {
                        handleRecognizeStatus((int) value);
                    }
                } else if (id == CarVendorExtensionManager.ID_DMS_KEY_NUM) {
                    Log.e(TAG, "onChange: userid-" + value);
                    if (isWaitCallback) {
                        isWaitCallback = false;
                        if (mStatus == Status.waitIdentifyResult) {
                            mStatus = Status.idle;
                            KLog.d(TAG, "face sdk identifying onSuccess:" + value);
                            for (IdentifyListener identifyListener : identifyListeners) {
                                if (identifyListener != null) {
                                    identifyListener.onSuccess((int) value);
                                    identifyListener.onEnd();
                                }
                            }
                        } else if (mStatus == Status.waitRecordResult) {
                            mStatus = Status.idle;
                            Log.e(TAG, "face sdk recording onSuccess:" + value);
                            for (IRecordListener recordListener : recordListeners) {
                                if (recordListener != null) {
                                    recordListener.onSuccess((int) value);
                                    recordListener.onEnd();
                                }
                            }
                        }
                    }
                }
            }
        });
        mHasInit = true;
    }

    private void handleRecognizeStatus(int value) {
        Log.e(TAG, "handleRecognizeStatus: " + RecognizeState.toString(value));
        switch (value) {
            case RecognizeState.FAIL:
                mStatus = Status.idle;
                for (IdentifyListener identifyListener : identifyListeners) {
                    if (identifyListener != null) {
                        identifyListener.onFailure(LoginConstants.FaceRecRes.FAIL, "识别失败");
                        identifyListener.onEnd();
                    }
                }
                break;
            case RecognizeState.INVALID_ID:
                mStatus = Status.idle;
                for (IdentifyListener identifyListener : identifyListeners) {
                    if (identifyListener != null) {
                        identifyListener.onFailure(LoginConstants.FaceRecRes.INVALID, "识别到无效人脸");
                    }
                }
                break;
            case RecognizeState.RECOGNIZING:
                //do nothing
                break;
            case RecognizeState.SUCCESS:
                isWaitCallback = true;
                mStatus = Status.waitIdentifyResult;
                break;
            case RecognizeState.INACTIVE:
                //do nothing
                break;
        }
    }

    private void handleRecordStatus(int value) {
        Log.e(TAG, "handleRecordStatus: " + DmsGuideIndicate.toString(value));
        switch (value) {
            case DmsGuideIndicate.FRONT:
                for (IRecordListener recordListener : recordListeners) {
                    if (recordListener != null) {
                        recordListener.onGuidTip(RecordGuid.front);
                    }
                }
                break;
            case DmsGuideIndicate.TURN_LEFT:
                for (IRecordListener recordListener : recordListeners) {
                    if (recordListener != null) {
                        recordListener.onGuidTip(RecordGuid.TurnLeft);
                    }
                }
                break;
            case DmsGuideIndicate.TURN_RIGHT:
                for (IRecordListener recordListener : recordListeners) {
                    if (recordListener != null) {
                        recordListener.onGuidTip(RecordGuid.TurnRight);
                    }
                }
                break;
            case DmsGuideIndicate.LOOK_DOWN:
                for (IRecordListener recordListener : recordListeners) {
                    if (recordListener != null) {
                        recordListener.onGuidTip(RecordGuid.LookDown);
                    }
                }
                break;
            case DmsGuideIndicate.LOOK_UP:
                for (IRecordListener recordListener : recordListeners) {
                    if (recordListener != null) {
                        recordListener.onGuidTip(RecordGuid.LookUp);
                    }
                }
                break;
            case DmsGuideIndicate.IDENTIFY_FAIL:
                mStatus = Status.idle;
                for (IRecordListener recordListener : recordListeners) {
                    if (recordListener != null) {
                        recordListener.onFailure(-1, "录入失败");
                        recordListener.onEnd();
                    }
                }
                break;
            case DmsGuideIndicate.IDENTIFY_SUCC:
                isWaitCallback = true;
                mStatus = Status.waitRecordResult;
                break;
            case DmsGuideIndicate.ALREADY_IN_USR_1:
            case DmsGuideIndicate.ALREADY_IN_USR_2:
            case DmsGuideIndicate.ALREADY_IN_USR_3:
            case DmsGuideIndicate.ALREADY_IN_USR_4:
            case DmsGuideIndicate.ALREADY_IN_USR_5:
                mStatus = Status.idle;
                int bindFaceId = getBindFaceId(value);
                for (IRecordListener recordListener : recordListeners) {
                    if (recordListener != null) {
                        recordListener.onFaceAlreadyBind(bindFaceId);
                        recordListener.onEnd();
                    }
                }
                break;
            case DmsGuideIndicate.NO_DISPLAY:
                // do nothing
        }
    }

    private int getBindFaceId(int dsmGuid) {
        switch (dsmGuid) {
            case DmsGuideIndicate.ALREADY_IN_USR_1:
                return FaceId.FaceId1.getValue();
            case DmsGuideIndicate.ALREADY_IN_USR_2:
                return FaceId.FaceId2.getValue();
            case DmsGuideIndicate.ALREADY_IN_USR_3:
                return FaceId.FaceId3.getValue();
            case DmsGuideIndicate.ALREADY_IN_USR_4:
                return FaceId.FaceId4.getValue();
            case DmsGuideIndicate.ALREADY_IN_USR_5:
                return FaceId.FaceId5.getValue();
            default:
                return 0;
        }
    }

    @Override
    public boolean isFaceALive() {
        return XmCarVendorExtensionManager.getInstance().getRecognizeAvailable() == CanOnOff2.ON;
    }

    @Override
    public boolean isIdentifying() {
        return XmCarVendorExtensionManager.getInstance().getRecognizeState() == RecognizeState.RECOGNIZING;
    }

    @Override
    public void startIdentify() {
        Log.e(TAG, "face sdk startIdentify");
        mStatus = Status.identifying;
        XmCarVendorExtensionManager.getInstance().setRecognize(UserIdRecognize.ACTIVE_REQ);
        for (IdentifyListener identifyListener : identifyListeners) {
            if (identifyListener != null) {
                identifyListener.onStart();
            }
        }
    }


    //打开dms一级开关
    public void openDms() {
        XmCarVendorExtensionManager.getInstance().openDms(CanOnOff2.ON_REQ);
    }

    //打开注意力分散二级开关
    public void openDistraction() {
        XmCarVendorExtensionManager.getInstance().openDistraction(CanOnOff2.ON_REQ);
    }

    //打开疲劳检测二级开关
    public void openTired() {
        XmCarVendorExtensionManager.getInstance().openTired(CanOnOff2.ON_REQ);
    }

    //打开不良行为提醒二级开关
    public void openBadDrive() {
        XmCarVendorExtensionManager.getInstance().openBadDrive(CanOnOff2.ON_REQ);
    }

    //打开身份识别一级开关
    public void openUserId() {
        XmCarVendorExtensionManager.getInstance().openUserId(CanOnOff2.ON_REQ);
    }

    //打开用户情绪感知一级开关
    public void openUserFeeling() {
        XmCarVendorExtensionManager.getInstance().openUserFeeling(CanOnOff2.ON_REQ);
    }

    @Override
    public void cancelIdentify() {
        mStatus = Status.idle;
        //杨工后来确定，取消人脸识别直接复用取消注册的接口
        XmCarVendorExtensionManager.getInstance().cancelFaceRecord();
        for (IdentifyListener identifyListener : identifyListeners) {
            if (identifyListener != null) {
                identifyListener.onFailure(LoginConstants.FaceRecRes.FAIL, "cancel");
                identifyListener.onEnd();
            }
        }
    }

    @Override
    public void addIdentifyListener(IdentifyListener identifyListener) {
        identifyListeners.add(identifyListener);
    }

    @Override
    public void removeIdentifyListener(IdentifyListener identifyListener) {
        identifyListeners.remove(identifyListener);
    }

    @Override
    public boolean isRecording() {
        return XmCarVendorExtensionManager.getInstance().getRecognizeState() == RecognizeState.RECOGNIZING;
    }

    @Override
    public void startRecord(int userId) {
        mStatus = Status.recording;
        KLog.d(TAG, "face sdk startRecord： " + userId);
        XmCarVendorExtensionManager.getInstance().startFaceRecord(userId);
        for (IRecordListener IRecordListener : recordListeners) {
            if (IRecordListener != null) {
                IRecordListener.onStart();
            }
        }
    }

    @Override
    public void cancelRecord() {
        mStatus = Status.idle;
        XmCarVendorExtensionManager.getInstance().cancelFaceRecord();
        for (IRecordListener IRecordListener : recordListeners) {
            if (IRecordListener != null) {
                IRecordListener.onFailure(-1, "cancel");
                IRecordListener.onEnd();
            }
        }
    }

    @Override
    public void addRecordListener(IRecordListener iRecordListener) {
        recordListeners.add(iRecordListener);
    }

    @Override
    public void removeRecordListener(IRecordListener iRecordListener) {
        recordListeners.remove(iRecordListener);
    }

    @Override
    public void deleteRecord(int faceId) {
        int carLibFaceId = 0;
        if (faceId == FaceId1.getValue()) {
            carLibFaceId = UserIdAction.USER_ID_1_REQ;
        } else if (faceId == FaceId2.getValue()) {
            carLibFaceId = UserIdAction.USER_ID_2_REQ;
        } else if (faceId == FaceId3.getValue()) {
            carLibFaceId = UserIdAction.USER_ID_3_REQ;
        } else if (faceId == FaceId4.getValue()) {
            carLibFaceId = UserIdAction.USER_ID_4_REQ;
        } else if (faceId == FaceId5.getValue()) {
            carLibFaceId = UserIdAction.USER_ID_5_REQ;
        }
        XmCarVendorExtensionManager.getInstance().delFaceRecord(carLibFaceId);
    }

    @Override
    public void mockFace() {
        //do nothing
    }

    @Override
    public void setMockFace(int faceId) {
        //do nothing
    }

    @Override
    public int getMockFace() {
        //do nothing
        return 0;
    }

    private enum Status {
        idle,
        identifying,
        recording,
        waitRecordResult,
        waitIdentifyResult;
    }
}
