package com.xiaoma.xting.launcher;

import android.content.Context;
import android.util.Log;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.xting.assistant.AssistantClient;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.RecordInfo;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.instructions.EOLClient;
import com.xiaoma.xting.local.model.BaseChannelBean;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/1/22
 */
public class AudioShareManager {

    private Context mContext;
    private AssistantClient mAssistantClient;
    private XtingAudioClient mAudioClient;

    private IRegisterStateListener mRegisterStateListener;

    private AudioShareManager() {
    }

    interface Holder {
        AudioShareManager sINSTANCE = new AudioShareManager();
    }

    public static AudioShareManager newSingleton() {
        return Holder.sINSTANCE;
    }

    public void init(final Context context) {
        Log.d("Jir", "init: ShareManage OK");
        this.mContext = context;
        initCenterService(context);
        StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
            @Override
            public void onPrepare(String depend) {
                Log.d("Jir", "onPrepare: initCenterService " + Center.getInstance().isConnected());
                if (!Center.getInstance().isConnected()) {
                    initCenterService(mContext);
                }
            }

            @Override
            public void onConnected() {
                super.onConnected();
                Log.d("Jir", "onConnected: showHistoryInfo");
                showHistoryInfo();
            }
        });
    }

    private void initCenterService(Context context) {
        Center.getInstance().init(context);
        Center.getInstance().runAfterConnected(new Runnable() {
            @Override
            public void run() {
                registerClient(mContext);
            }
        });
    }

    private void showHistoryInfo() {
        PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
        if (playerInfo == null) {
            PlayerInfo cachedPlayerInfo = SharedPrefUtils.getCachedPlayerInfo(mContext);
            if (cachedPlayerInfo != null) {
                int type = cachedPlayerInfo.getType();
                PlayerSourceFacade.newSingleton().setSourceType(type);
                XtingAudioClient.newSingleton(mContext).setLauncherCategoryId(cachedPlayerInfo.getCategoryId());
                if (type == PlayerSourceType.RADIO_YQ) {
                    PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.PLAYING);
                    PlayerInfoImpl.newSingleton().onPlayerInfoChanged(cachedPlayerInfo);
                    BaseChannelBean channelBean = XtingUtils.getBaseChannelByValue((int) cachedPlayerInfo.getProgramId());
                    Log.e("kaka", "showHistoryInfo: " + channelBean);
                    PlayerSourceFacade.newSingleton().getPlayerControl().playWithModel(channelBean);
                } else {
                    RecordInfo recordInfo = XtingUtils.getRecordDao().selectBy(cachedPlayerInfo.getType(), cachedPlayerInfo.getProgramId());
                    if (recordInfo != null) {
                        cachedPlayerInfo.setProgress(recordInfo.getProgress());
                        cachedPlayerInfo.setDuration(recordInfo.getDuration());
                    }
                    if (PlayerSourceFacade.newSingleton().getPlayerControl().isPlaying()) {
                        PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.PLAYING);
                    } else {
                        PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.PAUSE);
                    }
                }
                cachedPlayerInfo.setFromRecordF(true);
                PlayerInfoImpl.newSingleton().onPlayerInfoChanged(cachedPlayerInfo);
            } else {
                XtingAudioClient.newSingleton(mContext).restoreLauncherCategoryId();
                PlayerInfoImpl.newSingleton().onPlayerInfoChanged(null);
            }
        } else {
            playerInfo.setFromRecordF(true);
            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(playerInfo);
        }
    }

    private void registerClient(Context context) {
        Center.getInstance().register(mAssistantClient = AssistantClient.newSingleton(context));
        Center.getInstance().register(mAudioClient = XtingAudioClient.newSingleton(context));
        //如果支持EOL
        if (CenterConstants.SUPPORT_EOL) {
            Center.getInstance().register(EOLClient.newSingleton(context));
        }
        if (mRegisterStateListener != null) {
            mRegisterStateListener.onRegisterOk();
            mRegisterStateListener = null;
        }
    }

    public void shareAudioTypeChanged(int audioType) {
        if (mAudioClient != null) {
            mAudioClient.shareAudioTypeToLauncher(audioType);
        }
    }

    public void setRegisterStateListener(IRegisterStateListener listener) {
        this.mRegisterStateListener = listener;
    }

    public interface IRegisterStateListener {

        void onRegisterOk();
    }
}
