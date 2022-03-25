package com.xiaoma.xting.instructions;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.xting.common.LocalPlayList;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;
import com.xiaoma.xting.common.playerSource.utils.PrintInfo;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.local.model.AMChannelBean;
import com.xiaoma.xting.local.model.FMChannelBean;
import com.xiaoma.xting.sdk.LocalFMFactory;
import com.xiaoma.xting.sdk.model.BandType;

import java.util.List;

/**
 * <des>
 * EOL测试Client
 *
 * @author YangGang
 * @date 2019/7/3
 */
public class EOLClient extends Client {

    public static final byte BAND_FM = 0x01;
    public static final byte BAND_AM = 0x02;
    public static final byte AUTO_SEEK_UP = 0x01;
    public static final byte AUTO_SEEK_DOWN = 0x02;
    public static final byte MANUAL_TURN_UP = 0x03;
    public static final byte MANUAL_TURN_DOWN = 0x04;
    public static final byte AUDIO_SEARCH_STOP = 0x01;
    public static final byte AUDIO_SEARCH_START = 0x02;


    private static EOLClient sClient;

    private EOLClient(Context context) {
        super(context, CenterConstants.INSTRUCTION_DISTRIBUTE_PORT_XTING);
    }

    public static EOLClient newSingleton(Context context) {
        if (sClient == null) {
            synchronized (EOLClient.class) {
                if (sClient == null) {
                    sClient = new EOLClient(context);
                }
            }
        }
        return sClient;
    }

    @Override
    protected void onReceive(int action, Bundle data) {

    }

    @Override
    protected void onRequest(int action, Bundle data, ClientCallback callback) {
        String connectAction = "ACTION_UNKNOWN";
        if (CenterConstants.ACTION_GET_CURRENT_TUNER_STATE == action) {
            connectAction = "ACTION_GET_CURRENT_TUNER_STATE";
        } else if (CenterConstants.ACTION_SET_TUNER_FREQUENCY == action) {
            PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
            connectAction = "ACTION_SET_TUNER_FREQUENCY";
            if (data != null) {
                int frequency = data.getInt("frequency");

                int bandByChannel = LocalFMOperateManager.newSingleton().getBandByChannel(frequency);
                if (bandByChannel == XtingConstants.FMAM.TYPE_AM) {
                    LocalFMOperateManager.newSingleton().playChannel(frequency);
                } else {
                    LocalFMOperateManager.newSingleton().playChannel(frequency * 1000);
                }
                dispatchResult(getInfoBundle(true), callback);
            }
        } else if (CenterConstants.ACTION_SET_TUNER_BAND == action) {
            PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
            connectAction = "ACTION_SET_TUNER_BAND";
            if (data != null) {
                int band = data.getInt("band");
                if ((band & BAND_AM) == BAND_AM) {
                    LocalFMOperateManager.newSingleton().switchBandAndPlayLast(BandType.AM);
                } else {
                    LocalFMOperateManager.newSingleton().switchBandAndPlayLast(BandType.FM);
                }
                dispatchResult(getInfoBundle(true), callback);
            }
        } else if (CenterConstants.ACTION_SET_TUNER_FAVORITE == action) {
            connectAction = "ACTION_GET_CURRENT_TUNER_STATE";
            PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
            int favoriteIndex = data.getInt("favorite", -1);
            if (favoriteIndex == 255) {
                PlayerSourceFacade.newSingleton().getPlayerControl().subscribe(false);
                dispatchResult(getInfoBundle(true), callback);
            } else {
                PlayerInfo curPlayerInfo = PlayerSourceFacade.newSingleton().getPlayerControl().getCurPlayerInfo();
                if (curPlayerInfo != null) {
                    int sourceSubType = curPlayerInfo.getSourceSubType();
                    if (sourceSubType == PlayerSourceSubType.YQ_RADIO_FM) {
                        List<FMChannelBean> channelList = LocalPlayList.getInstance().getFmChannelBeans();
                        if (channelList == null || channelList.isEmpty()) {
                            dispatchResult(getInfoBundle(false), callback);
                            return;
                        }

                        int size = channelList.size();
                        if (size < favoriteIndex) {
                            dispatchResult(getInfoBundle(false), callback);
                            PrintInfo.print("Client_Instruction", "onConnect",
                                    String.format("action = %1$s (%2$s),bundle = %3$s,FM favorite index=%4$s,size=%5$s",
                                            String.valueOf(action), connectAction, data == null ? "null" : data.toString(),
                                            String.valueOf(favoriteIndex), String.valueOf(size)));
                            return;
                        }

                        FMChannelBean fmChannelBean = channelList.get(favoriteIndex - 1);
                        PlayerInfo playerInfo = BeanConverter.toPlayerInfo(fmChannelBean);
                        SubscribeInfo subscribeInfo = XtingUtils.getSubscribeDao().selectBy(PlayerSourceType.RADIO_YQ, playerInfo.getProgramId());
                        if (subscribeInfo == null) {
                            XtingUtils.getSubscribeDao().insert(BeanConverter.toSubscribeInfo(playerInfo));
                        }
                    } else {
                        List<AMChannelBean> channelList = LocalPlayList.getInstance().getAmChannelBeans();
                        if (channelList == null || channelList.isEmpty()) {
                            dispatchResult(getInfoBundle(false), callback);
                            return;
                        }

                        int size = channelList.size();
                        if (size < favoriteIndex) {
                            dispatchResult(getInfoBundle(false), callback);
                            PrintInfo.print("Client_Instruction", "onConnect",
                                    String.format("action = %1$s (%2$s),bundle = %3$s,AM favorite index=%4$s,size=%5$s",
                                            String.valueOf(action), connectAction, data == null ? "null" : data.toString(),
                                            String.valueOf(favoriteIndex), String.valueOf(size)));
                            return;
                        }

                        AMChannelBean fmChannelBean = channelList.get(favoriteIndex - 1);
                        PlayerInfo playerInfo = BeanConverter.toPlayerInfo(fmChannelBean);
                        SubscribeInfo subscribeInfo = XtingUtils.getSubscribeDao().selectBy(PlayerSourceType.RADIO_YQ, playerInfo.getProgramId());
                        if (subscribeInfo == null) {
                            XtingUtils.getSubscribeDao().insert(BeanConverter.toSubscribeInfo(playerInfo));
                        }
                    }
                }
            }

            dispatchResult(getInfoBundle(true), callback);
        } else if (CenterConstants.ACTION_SET_TUNER_SEEK == action) {
            connectAction = "ACTION_SET_TUNER_SEEK";
            PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
            int seek = data.getInt("seek");
            if ((seek) == AUTO_SEEK_DOWN) {
                LocalFMFactory.getSDK().scanUp();
            } else if ((seek) == AUTO_SEEK_UP) {
                LocalFMFactory.getSDK().scanDown();
            } else if ((seek) == MANUAL_TURN_UP) {
                LocalFMFactory.getSDK().stepPrevious();
            } else if ((seek) == MANUAL_TURN_DOWN) {
                LocalFMFactory.getSDK().stepNext();
            } else {
                PrintInfo.print("Client_Instruction", "onConnect", String.format("action = %1$s (%2$s) ,bundle = %3$s", String.valueOf(action), connectAction, data == null ? "null" : data.toString()));

                dispatchResult(getInfoBundle(false), callback);

                return;
            }
            dispatchResult(getInfoBundle(true), callback);

        } else if (CenterConstants.ACTION_SET_TUNER_AUTO_STORE == action) {
            connectAction = "ACTION_SET_TUNER_AUTO_STORE";
            PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
            int autoStore = data.getInt("autoStore");
            if ((autoStore) == AUDIO_SEARCH_START) {
                BandType currentBand = LocalFMFactory.getSDK().getCurrentBand();
                if (currentBand != null) {
                    LocalFMOperateManager.newSingleton().searchLocalFM(currentBand.getBand());
                } else {
                    LocalFMOperateManager.newSingleton().searchLocalFM(XtingConstants.FMAM.TYPE_FM);
                }
            } else if ((autoStore) == AUDIO_SEARCH_STOP) {
                LocalFMFactory.getSDK().cancel();
            } else {
                PrintInfo.print("Client_Instruction", "onConnect", String.format("action = %1$s (%2$s) ,bundle = %3$s", String.valueOf(action), connectAction, data == null ? "null" : data.toString()));

                dispatchResult(getInfoBundle(false), callback);
                return;
            }

            dispatchResult(getInfoBundle(true), callback);
        }

        PrintInfo.print("Client_Instruction", "onConnect", String.format("action = %1$s (%2$s) ,bundle = %3$s", String.valueOf(action), connectAction, data == null ? "null" : data.toString()));

    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {
    }

    private void dispatchResult(Bundle data, ClientCallback callback) {
        if (callback != null) {
            callback.setData(data);
            callback.callback();
        }
    }

    private Bundle getInfoBundle(boolean isSuccess) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("result", isSuccess);
        return bundle;
    }
}
