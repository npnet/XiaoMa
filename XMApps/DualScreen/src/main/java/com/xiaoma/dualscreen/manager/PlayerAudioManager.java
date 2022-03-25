package com.xiaoma.dualscreen.manager;

import android.content.Context;

import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lailai
 * on 19-1-22 下午7:34
 * <p>
 * desc: 用于维护 Audio的manager
 * </p>
 */
public class PlayerAudioManager {
    private static PlayerAudioManager instance;
    private PlayerConnectHelper mConnectHelper;
    private Map<Integer, SourceInfo> clientInSourceMap;
    private List<SourceInfo> sourceInfos;
    public static final String XTING_PACKAGE_NAME = "com.xiaoma.xting";
    public static final String MUSIC_PACKAGE_NAME = "com.xiaoma.music";

    public static PlayerAudioManager getInstance() {
        if (instance == null) {
            synchronized (PlayerAudioManager.class) {
                if (instance == null) {
                    instance = new PlayerAudioManager();
                }
            }
        }

        return instance;
    }

    private PlayerAudioManager() {
        mConnectHelper = PlayerConnectHelper.getInstance();
        clientInSourceMap = new HashMap<>();
        sourceInfos = new ArrayList<>();

        initSourceList(sourceInfos);

    }

    private void initSourceList(List<SourceInfo> sourceInfos) {
        //想听
        SourceInfo xting = new SourceInfo(XTING_PACKAGE_NAME, AudioConstants.AudioTypes.XTING);
        sourceInfos.add(xting);
        //酷我在线音乐
        SourceInfo kwMusic = new SourceInfo(MUSIC_PACKAGE_NAME, AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
        sourceInfos.add(kwMusic);
        //usb音乐
        SourceInfo usbMusic = new SourceInfo(MUSIC_PACKAGE_NAME, AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
        sourceInfos.add(usbMusic);
        //蓝牙音乐
        SourceInfo btMusic = new SourceInfo(MUSIC_PACKAGE_NAME, AudioConstants.AudioTypes.MUSIC_LOCAL_BT);
        sourceInfos.add(btMusic);
    }

    public void connectSourceInfos(Context context) {
        for (int i = 0; i < sourceInfos.size(); i++) {
            if (Center.getInstance().isClientAlive(sourceInfos.get(i))) {
                KLog.d("isClientAlive playerConnect:" + sourceInfos.get(i).toString());
                mConnectHelper.playerConnect(context, sourceInfos.get(i));
                clientInSourceMap.put(sourceInfos.get(i).getPort(), sourceInfos.get(i));
            }
        }
    }

    public void addStateCallBack(final Context context) {
        KLog.e("addStateCallBack");
        StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
            @Override
            public void onClientIn(SourceInfo source) {
                KLog.e(source.toString());
                if (XTING_PACKAGE_NAME.equals(source.getLocation())) {
                    if (source.getPort() == AudioConstants.AudioTypes.XTING) {
                        //连接想听
                        KLog.e("onClientIn playerConnect:" + source.toString());
                        mConnectHelper.playerConnect(context, source);
                        clientInSourceMap.put(source.getPort(), source);
                    }

                } else if (MUSIC_PACKAGE_NAME.equals(source.getLocation())) {
                    if (source.getPort() == AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO ||
                            source.getPort() == AudioConstants.AudioTypes.MUSIC_LOCAL_BT ||
                            source.getPort() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
                        //连接音乐
                        KLog.e("onClientIn playerConnect:" + source.toString());
                        mConnectHelper.playerConnect(context, source);
                        clientInSourceMap.put(source.getPort(), source);
                    }
                }
            }
        });
    }

    public void setAliveSource(Context context) {
        for (int i = 0; i < sourceInfos.size(); i++) {
            SourceInfo sourceInfo = sourceInfos.get(i);
            if (Center.getInstance().isClientAlive(sourceInfo)) {
                clientInSourceMap.put(sourceInfo.getPort(), sourceInfo);
                mConnectHelper.playerConnect(context, sourceInfo);
            }
        }
    }

    public SourceInfo getSourceInfo(int port) {
        return clientInSourceMap.get(port);
    }
}
