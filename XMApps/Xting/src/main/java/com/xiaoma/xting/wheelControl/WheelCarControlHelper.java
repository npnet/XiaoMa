package com.xiaoma.xting.wheelControl;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.xiaoma.carlib.wheelcontrol.OnWheelKeyListener;
import com.xiaoma.carlib.wheelcontrol.WheelKeyEvent;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.component.dispatch.SimulateWheelDispatch;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerOperate;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.sdk.LocalFMFactory;
import com.xiaoma.xting.sdk.model.XMRadioStation;
import com.xiaoma.xting.utils.XtingFastPlayController;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/22
 */
public class WheelCarControlHelper {
    private static final int[] KEYCODE = new int[]{
            WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB,
            WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD
    };

    private Context mContext;
    private CopyOnWriteArraySet<OnWheelHandle> wheelHandles = new CopyOnWriteArraySet<>();
    private OnWheelKeyListener mOnWheelKeyListener = new OnWheelKeyListener.Stub() {
        @Override
        public boolean onKeyEvent(int keyAction, int keyCode) {
            if (keyAction == WheelKeyEvent.ACTION_CLICK) {
                handleClick(keyCode);
            } else if (keyAction == WheelKeyEvent.ACTION_LONG_PRESS
                    || keyAction == WheelKeyEvent.ACTION_RELEASE) {
                handleOtherKeyEvent(keyAction, keyCode);
            }
            //????????????seek??????,???????????????seek?????????seek????????????????????????????????????????????????
            return keyCode == WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB
                    || keyCode == WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD;
        }

        @Override
        public String getPackageName() {
            return mContext.getPackageName();
        }
    };

    private WheelCarControlHelper() {
    }

    public static WheelCarControlHelper newSingleton() {
        return Holder.sINSTANCE;
    }

    public void register() {
        Log.d("WheelControl", "{registerWheelControl}-[mOnWheelKeyListener] : " + mOnWheelKeyListener);
        XmWheelManager.getInstance().register(mOnWheelKeyListener, KEYCODE);
    }

    public void unRegister() {
        Log.d("WheelControl", "{unRegisterWheelControl}-[mOnWheelKeyListener] : " + mOnWheelKeyListener);
        if (mOnWheelKeyListener != null) {
            XmWheelManager.getInstance().unregister(mOnWheelKeyListener);
        }
    }

    public void addWheelHandle(OnWheelHandle wheelHandle) {
        wheelHandles.add(wheelHandle);
    }

    public void removeWheelHandle(OnWheelHandle wheelHandle) {
        wheelHandles.remove(wheelHandle);
    }

    private void dispatchWheelClick(boolean isNext) {
        if (!CollectionUtil.isListEmpty(wheelHandles)) {
            for (OnWheelHandle wheelHandle : wheelHandles) {
                try {
                    if (wheelHandle.onHandle(isNext)) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        boolean localFmOpen = LocalFMFactory.getSDK().isRadioOpen();
        if (localFmOpen) {
            if (LocalFMFactory.getSDK().isScanning()) {
                //??????????????????????????????
                LocalFMFactory.getSDK().cancel();
                return;
            }
            // ???????????????????????????
            XMRadioStation currentStation = LocalFMFactory.getSDK().getCurrentStation();
            if (currentStation == null) {
                XMToast.showToast(mContext, R.string.no_content_to_play);
                return;
            }
            boolean b = LocalFMOperateManager.newSingleton().playClosestChannel(isNext);
            if (b) {
                XMToast.showToast(mContext, isNext ? R.string.have_to_next : R.string.have_to_pre);
            } else {
                XMToast.showToast(mContext, isNext ? R.string.no_next_sound : R.string.no_pre_sound);

            }
        } else {
            // ????????????????????????
            int sourceType = PlayerSourceFacade.newSingleton().getSourceType();
            if (sourceType == PlayerSourceType.DEFAULT
                    || !PlayerSourceFacade.newSingleton().getPlayerControl().isCurPlayerInfoAlive()) {
                //??????bug???????????????????????????????????????
                //XMToast.showToast(mContext, R.string.no_content_to_play);
                return;
            }
            PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
            if ((sourceType == PlayerSourceType.HIMALAYAN
                    && playerInfo.getSourceSubType() == PlayerSourceSubType.RADIO)) {
                XMToast.showToast(mContext, R.string.hint_operate_unsupport);
                return;
            }
            if (!isNext) {
                int result = PlayerSourceFacade.newSingleton().getPlayerControl().playPre();
                if (result == PlayerOperate.FAIL) {
                    XMToast.showToast(mContext, R.string.no_pre_sound);
                } else {
                    //?????????????????? ??? ?????????????????????????????????????????????toast
                    XMToast.showToast(mContext, R.string.have_to_pre);
                }
            } else {
                int result = PlayerSourceFacade.newSingleton().getPlayerControl().playNext();
                if (result != PlayerOperate.SUCCESS) {
                    XMToast.showToast(mContext, R.string.no_next_sound);
                } else {
                    //?????????????????? ??? ?????????????????????????????????????????????toast
                    XMToast.showToast(mContext, R.string.have_to_next);
                }
            }
        }
    }

    private int curKey = -1;

    public void init(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        mContext = context.getApplicationContext();

        if (!ConfigManager.ApkConfig.isCarPlatform() && ConfigManager.ApkConfig.isDebug()) {
            initSimulateWheel();
        }
    }

    private void handleClick(int keyCode) {
        switch (keyCode) {
            case WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD:
                //?????????
                dispatchWheelClick(false);
                break;

            case WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB:
                //?????????
                dispatchWheelClick(true);
                break;
        }
    }

    private Handler handler;
    private Runnable nextLongPressTask = () -> {
        try {
            mOnWheelKeyListener.onKeyEvent(WheelKeyEvent.ACTION_LONG_PRESS, WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    };
    private Runnable previousLongPressTask = () -> {
        try {
            mOnWheelKeyListener.onKeyEvent(WheelKeyEvent.ACTION_LONG_PRESS, WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    };

    private boolean handleOtherKeyEvent(int keyAction, int keyCode) {
        boolean localFmOpen = LocalFMFactory.getSDK().isRadioOpen();
        if (localFmOpen) {
            // ?????????????????????
            // TODO: 2019-07-20 ??????????????????????????????????????????
            return false;
        }
        switch (keyAction) {
            case WheelKeyEvent.ACTION_LONG_PRESS:
                int sourceType = PlayerSourceFacade.newSingleton().getSourceType();
                if (sourceType == PlayerSourceType.DEFAULT
                        || !PlayerSourceFacade.newSingleton().getPlayerControl().isCurPlayerInfoAlive()) {
                    // ???????????????????????????
                    // TODO: 2019-07-20 ??????????????????????????????????????????
                    return false;
                }
                PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
                if ((sourceType == PlayerSourceType.HIMALAYAN
                        && playerInfo.getSourceSubType() == PlayerSourceSubType.RADIO)) {
                    XMToast.showToast(mContext, R.string.hint_operate_unsupport);
                    // ??????????????????????????????????????????
                    // TODO: 2019-07-20 ??????????????????????????????????????????
                    return false;
                }
                PlayerSourceFacade.newSingleton().getPlayerControl().pause();
                if (keyCode == WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD) {
                    // ????????????
                    XtingFastPlayController.getInstance().starter(false);
                } else if (keyCode == WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB) {
                    // ????????????
                    XtingFastPlayController.getInstance().starter(true);
                }
                break;
            case WheelKeyEvent.ACTION_RELEASE:
                XtingFastPlayController.getInstance().closeStarter();
                break;
        }
        return false;
    }

    private void initSimulateWheel() {
        if (!ConfigManager.ApkConfig.isDebug()) {
            return;
        }
        if (ConfigManager.ApkConfig.isCarPlatform()) {
            return;
        }
        SimulateWheelDispatch.getInstance().setEventListener(new SimulateWheelDispatch.OnWheelEvent() { // ??????
            @Override
            public boolean onNextDown() {
                try {
                    synchronized (WheelCarControlHelper.class) {
                        if (curKey != -1) {
                            return false;
                        }
                        curKey = WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB;
                        getHandler().postDelayed(nextLongPressTask, 1500);
                        return mOnWheelKeyListener.onKeyEvent(WheelKeyEvent.ACTION_PRESS, WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onNextUp() {
                try {
                    if (curKey != WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB) {
                        return false;
                    }
                    curKey = -1;
                    getHandler().removeCallbacks(nextLongPressTask);
                    return mOnWheelKeyListener.onKeyEvent(WheelKeyEvent.ACTION_RELEASE, WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onPreviousDown() {
                try {
                    if (curKey != -1) {
                        return false;
                    }
                    curKey = WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD;
                    getHandler().postDelayed(previousLongPressTask, 1500);
                    return mOnWheelKeyListener.onKeyEvent(WheelKeyEvent.ACTION_PRESS, WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onPreviousUp() {
                try {
                    if (curKey != WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD) {
                        return false;
                    }
                    curKey = -1;
                    getHandler().removeCallbacks(previousLongPressTask);
                    return mOnWheelKeyListener.onKeyEvent(WheelKeyEvent.ACTION_RELEASE, WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    private Handler getHandler() {
        return handler == null ? handler = new Handler() : handler;
    }

    private interface Holder {
        WheelCarControlHelper sINSTANCE = new WheelCarControlHelper();
    }

    public interface OnWheelHandle {
        /**
         * ????????????????????????
         *
         * @return ??????????????????????????????
         */
        boolean onHandle(boolean isNext);
    }
}
