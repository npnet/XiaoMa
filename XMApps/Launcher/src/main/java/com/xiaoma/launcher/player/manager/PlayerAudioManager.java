package com.xiaoma.launcher.player.manager;

import android.content.Context;
import android.util.Log;

import com.xiaoma.center.logic.ErrorCode;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.player.AudioConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by kaka
 * on 19-1-22 下午7:34
 * <p>
 * desc: 用于维护Launcher Audio的manager
 * </p>
 */
public class PlayerAudioManager {
    private static final String TAG = PlayerAudioManager.class.getSimpleName();

    private static PlayerAudioManager instance;
    private PlayerConnectHelper mConnectHelper;
    private Map<Integer, SourceInfo> clientInSourceMap;
    private List<SourceInfo> sourceInfos;
    private CopyOnWriteArraySet<ClientOutListener> clientOutListeners = new CopyOnWriteArraySet<>();

    private List<SourceInfo> connectSourceList = new ArrayList<>();
    private int connectCode = ErrorCode.CODE_EXCEPTION;

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
        SourceInfo xting = new SourceInfo(LauncherConstants.LauncherApp.LAUNCHER_XTING_PACKAGE, AudioConstants.AudioTypes.XTING);
        sourceInfos.add(xting);

        //酷我在线音乐
        SourceInfo kwMusic = new SourceInfo(LauncherConstants.LauncherApp.LAUNCHER_MUSIC_PACKAGE, AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
        sourceInfos.add(kwMusic);
        //usb音乐
        SourceInfo usbMusic = new SourceInfo(LauncherConstants.LauncherApp.LAUNCHER_MUSIC_PACKAGE, AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
        sourceInfos.add(usbMusic);
        //蓝牙音乐
        SourceInfo btMusic = new SourceInfo(LauncherConstants.LauncherApp.LAUNCHER_MUSIC_PACKAGE, AudioConstants.AudioTypes.MUSIC_LOCAL_BT);
        sourceInfos.add(btMusic);
    }

    public void connectAudio(final Context context) {
        for (int i = 0; i < sourceInfos.size(); i++) {
            if (Center.getInstance().isClientAlive(sourceInfos.get(i))) {
                if (connectSourceList.contains(sourceInfos.get(i))) {
                    return;
                }
                connectCode = mConnectHelper.playerConnect(context, sourceInfos.get(i));
                clientInSourceMap.put(sourceInfos.get(i).getPort(), sourceInfos.get(i));
                if (connectCode == ErrorCode.CODE_SUCCESS) {
                    connectSourceList.add(sourceInfos.get(i));
                }
                Log.d(TAG, "isClientAlive playerConnect:" + sourceInfos.get(i).toString());
            }
        }
        StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
            @Override
            public void onClientIn(SourceInfo source) {
                if (connectSourceList.contains(source)) {
                    return;
                }
                if (LauncherConstants.LauncherApp.LAUNCHER_XTING_PACKAGE.equals(source.getLocation())) {
                    if (source.getPort() == AudioConstants.AudioTypes.XTING) {
                        //连接想听
                        connectCode = mConnectHelper.playerConnect(context, source);
                        clientInSourceMap.put(source.getPort(), source);
                    }

                } else if (LauncherConstants.LauncherApp.LAUNCHER_MUSIC_PACKAGE.equals(source.getLocation())) {
                    if (source.getPort() == AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO ||
                            source.getPort() == AudioConstants.AudioTypes.MUSIC_LOCAL_BT ||
                            source.getPort() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
                        //连接音乐
                        connectCode = mConnectHelper.playerConnect(context, source);
                        clientInSourceMap.put(source.getPort(), source);
                    }
                }

                if (connectCode == ErrorCode.CODE_SUCCESS) {
                    connectSourceList.add(source);
                }
                Log.d(TAG, "onClientIn playerConnect:" + source.toString());
            }

            @Override
            public void onClientOut(SourceInfo source) {
                super.onClientOut(source);
                clientInSourceMap.remove(source.getPort());
                connectSourceList.remove(source);
                for (ClientOutListener clientOutListener : clientOutListeners) {
                    try {
                        clientOutListener.onClientOut(source);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public SourceInfo getSourceInfo(int port) {
        return clientInSourceMap.get(port);
    }

    public void addClientOutListener(ClientOutListener clientOutListener) {
        clientOutListeners.add(clientOutListener);
    }

    public void removeClientOutListener(ClientOutListener clientOutListener) {
        clientOutListeners.remove(clientOutListener);
    }

    public interface ClientOutListener {
        void onClientOut(SourceInfo sourceInfo);
    }
}
