package com.xiaoma.login.sdk;

import android.content.Context;
import android.content.IntentFilter;

import com.xiaoma.login.UserBindManager;
import com.xiaoma.login.business.receive.FaceMocker;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.db.FaceStore;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FaceFactory {

    private static final String TAG = FaceFactory.class.getSimpleName();

    private FaceFactory() throws Exception {
        throw new Exception();
    }

    private static boolean hasRegister;
    private static IFace iCarKey = new IFace() {
        private int mMockFace;
        private List<FaceStore> existFaceIds = new ArrayList<>();
        private int state; //0-默认状态1-识别中2-上看3-下看4-左看5-右看6-前看7-录入完成
        private CopyOnWriteArrayList<IdentifyListener> identifyListeners = new CopyOnWriteArrayList<>();
        private CopyOnWriteArrayList<IRecordListener> IRecordListeners = new CopyOnWriteArrayList<>();
        private static final String KEY_FACE = "faces";
        private Context mContext;
        private int currentFaceId;

        @Override
        public void init(Context context) {
            mContext = context;
            mMockFace = TPUtils.get(mContext, KEY_FACE, 0);
            IntentFilter filter = new IntentFilter("com.xiaoma.FaceMocker");
            if (!hasRegister) {
                context.registerReceiver(new FaceMocker(), filter);
                hasRegister = true;
            }
//            DBManager.getInstance().getKeyBindUserDBManager().init(mContext);
            existFaceIds = UserBindManager.getInstance().getFaceDB().query();
        }

        @Override
        public void mockFace() {
            switch (state) {
                case 1:
                    state = 0;
                    int faceId = 0;
                    //模拟face标志为-1时，为模拟人脸识别失败的特殊值
                    if (mMockFace == -1) {
                        faceId = -1;
                    } else {
                        //若能匹配已绑定的人脸则返回已绑定的人脸
                        for (FaceStore existFaceId : existFaceIds) {
                            if (existFaceId.getFace() == mMockFace) {
                                faceId = existFaceId.getFaceId();
                            }
                        }
                    }

                    if (faceId == 0) {
                        for (IdentifyListener identifyListener : identifyListeners) {
                            if (identifyListener != null) {
                                identifyListener.onFailure(LoginConstants.FaceRecRes.INVALID, "no record!");
                                identifyListener.onEnd();
                            }
                        }
                    } else if (faceId == -1) {
                        for (IdentifyListener identifyListener : identifyListeners) {
                            if (identifyListener != null) {
                                identifyListener.onFailure(LoginConstants.FaceRecRes.FAIL, "error");
                                identifyListener.onEnd();
                            }
                        }
                    } else {
                        for (IdentifyListener identifyListener : identifyListeners) {
                            if (identifyListener != null) {
                                identifyListener.onSuccess(faceId);
                                identifyListener.onEnd();
                            }
                        }
                    }
                    break;
                case 2:
                    state = 3;
                    int bindFaceId = UserBindManager.getInstance().getFaceDB().queryByFace(mMockFace);
                    //模拟判断人脸是否已经绑定
                    if (UserBindManager.getInstance().getFaceBoundUser(FaceId.valueOf(bindFaceId)) != null) {
                        for (IRecordListener IRecordListener : IRecordListeners) {
                            if (IRecordListener != null) {
                                IRecordListener.onFaceAlreadyBind(bindFaceId);
                            }
                        }
                        return;
                    }
                    for (IRecordListener IRecordListener : IRecordListeners) {
                        if (IRecordListener != null) {
                            IRecordListener.onGuidTip(RecordGuid.LookUp);
                        }
                    }
                    break;
                case 3:
                    state = 4;
                    for (IRecordListener IRecordListener : IRecordListeners) {
                        if (IRecordListener != null) {
                            IRecordListener.onGuidTip(RecordGuid.LookDown);
                        }
                    }
                    break;
                case 4:
                    state = 5;
                    for (IRecordListener IRecordListener : IRecordListeners) {
                        if (IRecordListener != null) {
                            IRecordListener.onGuidTip(RecordGuid.TurnLeft);
                        }
                    }
                    break;
                case 5:
                    state = 6;
                    for (IRecordListener IRecordListener : IRecordListeners) {
                        if (IRecordListener != null) {
                            IRecordListener.onGuidTip(RecordGuid.TurnRight);
                        }
                    }
                    break;
                case 6:
                    state = 7;
                    for (IRecordListener IRecordListener : IRecordListeners) {
                        if (IRecordListener != null) {
                            IRecordListener.onGuidTip(RecordGuid.front);
                        }
                    }
                    break;
                case 7:
                    state = 0;
                    if (mMockFace == -1) {
                        for (IRecordListener IRecordListener : IRecordListeners) {
                            if (IRecordListener != null) {
                                IRecordListener.onFailure(-1, "record error");
                                IRecordListener.onEnd();
                            }
                        }
                    } else {
                        for (IRecordListener IRecordListener : IRecordListeners) {
                            if (IRecordListener != null) {
                                IRecordListener.onSuccess(currentFaceId);
                                existFaceIds.add(new FaceStore(currentFaceId, mMockFace));
                                UserBindManager.getInstance().getFaceDB().save(new FaceStore(currentFaceId, mMockFace));
//                                DBManager.getInstance().getKeyBindUserDBManager().save(new FaceStore(currentFaceId, mMockFace));
                                IRecordListener.onEnd();
                            }
                        }
                    }
                    break;

            }
        }

        @Override
        public boolean isFaceALive() {
            return true;
        }

        @Override
        public boolean isIdentifying() {
            return state == 1;
        }

        @Override
        public void startIdentify() {
            state = 1;
            existFaceIds = UserBindManager.getInstance().getFaceDB().query();
            for (IdentifyListener identifyListener : identifyListeners) {
                if (identifyListener != null) {
                    identifyListener.onStart();
                }
            }
        }

        @Override
        public void cancelIdentify() {
            state = 0;
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
            return state == 3 || state == 4 || state == 5;
        }

        @Override
        public void startRecord(int userId) {
            state = 2;
            currentFaceId = userId;
            //模拟判断人脸是否已经绑定
            if (UserBindManager.getInstance().getFaceBoundUser(FaceId.valueOf(userId)) != null) {
                for (IRecordListener IRecordListener : IRecordListeners) {
                    if (IRecordListener != null) {
                        IRecordListener.onFaceAlreadyBind(userId);
                    }
                }
                return;
            }
            existFaceIds = UserBindManager.getInstance().getFaceDB().query();
            for (IRecordListener IRecordListener : IRecordListeners) {
                if (IRecordListener != null) {
                    IRecordListener.onStart();
                }
            }
        }

        @Override
        public void cancelRecord() {
            state = 0;
            for (IRecordListener IRecordListener : IRecordListeners) {
                if (IRecordListener != null) {
                    IRecordListener.onFailure(-1, "cancel");
                    IRecordListener.onEnd();
                }
            }
        }

        @Override
        public void addRecordListener(IRecordListener IRecordListener) {
            IRecordListeners.add(IRecordListener);
        }

        @Override
        public void removeRecordListener(IRecordListener IRecordListener) {
            IRecordListeners.remove(IRecordListener);
        }

        @Override
        public void deleteRecord(int faceId) {
            UserBindManager.getInstance().getFaceDB().deleteById(faceId);
        }

        @Override
        public void setMockFace(int faceId) {
            mMockFace = faceId;
            TPUtils.put(mContext, KEY_FACE, faceId);
        }

        @Override
        public int getMockFace() {
            return mMockFace;
        }
    };

    public static IFace getSDK() {
        return iCarKey;
    }
}
