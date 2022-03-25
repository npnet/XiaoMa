package com.xiaoma.music.local.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.music.BTMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.callback.OnBTConnectStateChangeListener;
import com.xiaoma.music.callback.OnBTMusicChangeListener;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceListener;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.manager.BTControlManager;
import com.xiaoma.music.model.BTMusic;
import com.xiaoma.music.model.XMBluetoothDevice;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.view.MusicWaveView;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/10 0010
 */
@PageDescComponent(EventConstants.PageDescribe.btFragment)
public class BtFragment extends BaseFragment implements View.OnClickListener {
    private MusicWaveView imgBtDes;
    private LinearLayout btNotConnectView;
    private Button goToSetting;
    private TextView btStatus;
    private TextView btMediaStatus;
    private Button btnStartBtMusic;
    private boolean isBtDisconnect;

    public static BtFragment newInstance() {
        return new BtFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initEvent();
    }

    private void initEvent() {
        List<XMBluetoothDevice> btConnectDevice = BTMusicFactory.getBTMusicControl().getBTConnectDevice();
        if (!ListUtils.isEmpty(btConnectDevice)) {
            showConnectView();
        } else {
            if (!MusicConstants.SHOW_LOCAL_TEST) {
                showNotConnectView();
            } else {
                showConnectView();
            }
        }
        BTMusicFactory.getBTMusicControl().addBtStateChangeListener(new OnBTConnectStateChangeListener() {
            @Override
            public void onBTConnect() {
                showConnectView();
                isBtDisconnect = false;
            }

            @Override
            public void onBTDisconnect() {
                isBtDisconnect = true;
                showNotConnectView();
            }

            @Override
            public void onBTSinkConnected() {
                showConnectView();
                isBtDisconnect = false;
            }

            @Override
            public void onBTSinkDisconnected() {
                if (!isBtDisconnect) {
                    showNotMediaView();
                }
                AudioSourceManager.getInstance().removeAudioSourceListener(focusUpdateListener);
                BTControlManager.getInstance().removeBtMusicInfoChangeListener(btListener);
            }
        });
    }

    private void showNotConnectView() {
        AudioSourceManager.getInstance().removeAudioSourceListener(focusUpdateListener);
        BTControlManager.getInstance().removeBtMusicInfoChangeListener(btListener);
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                imgBtDes.setVisibility(View.GONE);
                btNotConnectView.setVisibility(View.VISIBLE);
                goToSetting.setVisibility(View.VISIBLE);
                btStatus.setText(mContext.getString(R.string.not_connect_bluetooth));
                btMediaStatus.setVisibility(View.GONE);
                btnStartBtMusic.setVisibility(View.GONE);
            }
        });
    }

    private void showNotMediaView() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                imgBtDes.setVisibility(View.GONE);
                btNotConnectView.setVisibility(View.VISIBLE);
                goToSetting.setVisibility(View.GONE);
                btStatus.setText(mContext.getString(R.string.connect_bluetooth));
                btMediaStatus.setVisibility(View.VISIBLE);
                btnStartBtMusic.setVisibility(View.GONE);
            }
        });
    }

    private void showConnectView() {
        AudioSourceManager.getInstance().addAudioSourceListener(focusUpdateListener);
        BTControlManager.getInstance().addBtMusicInfoChangeListener(btListener);
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (AudioSource.BLUETOOTH_MUSIC != AudioSourceManager.getInstance().getCurrAudioSource()) {
                    btnStartBtMusic.setVisibility(View.VISIBLE);
                    imgBtDes.setVisibility(View.VISIBLE);
                    btNotConnectView.setVisibility(View.GONE);
                    imgBtDes.controlWave(false);
                } else {
                    if (BTMusicFactory.getBTMusicControl().isPlaying()) {
                        imgBtDes.setVisibility(View.VISIBLE);
                        btnStartBtMusic.setVisibility(View.GONE);
                        btNotConnectView.setVisibility(View.GONE);
                        imgBtDes.controlWave(true);
                    } else {
                        imgBtDes.setVisibility(View.VISIBLE);
                        btnStartBtMusic.setVisibility(View.VISIBLE);
                        btNotConnectView.setVisibility(View.GONE);
                        imgBtDes.controlWave(false);
                    }
                }
            }
        });
    }

    private void bindView(View view) {
        imgBtDes = view.findViewById(R.id.img_bt_des);
        imgBtDes.controlWave(false);
        btNotConnectView = view.findViewById(R.id.bt_not_connect_view);
        goToSetting = view.findViewById(R.id.btn_go_to_setting);
        btStatus = view.findViewById(R.id.connect_bluetooth_status);
        btMediaStatus = view.findViewById(R.id.open_bt_media_tv);
        btnStartBtMusic = view.findViewById(R.id.btn_start_bt_music);
        goToSetting.setOnClickListener(this);
        btnStartBtMusic.setOnClickListener(this);
    }

    private final AudioSourceListener focusUpdateListener = new AudioSourceListener() {
        @Override
        public void onAudioSourceSwitch(int preAudioSource, int currAudioSource) {
            ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                @Override
                public void run() {
                    if (AudioSource.BLUETOOTH_MUSIC == currAudioSource) {
                        if (BTMusicFactory.getBTMusicControl().isPlaying()) {
                            imgBtDes.setVisibility(View.VISIBLE);
                            btnStartBtMusic.setVisibility(View.GONE);
                            btNotConnectView.setVisibility(View.GONE);
                            imgBtDes.controlWave(true);
                        } else {
                            imgBtDes.setVisibility(View.VISIBLE);
                            btnStartBtMusic.setVisibility(View.VISIBLE);
                            btNotConnectView.setVisibility(View.GONE);
                            imgBtDes.controlWave(false);
                        }
                    } else {
                        btnStartBtMusic.setVisibility(View.VISIBLE);
                        imgBtDes.setVisibility(View.VISIBLE);
                        btNotConnectView.setVisibility(View.GONE);
                        imgBtDes.controlWave(false);
                    }
                }
            });
        }
    };

    private OnBTMusicChangeListener btListener = new OnBTMusicChangeListener() {
        @Override
        public void currentBtMusic(BTMusic btMusic) {
            if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.BLUETOOTH_MUSIC) {
                btnStartBtMusic.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPlay() {
            if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.BLUETOOTH_MUSIC) {
                btnStartBtMusic.setVisibility(View.GONE);
                imgBtDes.controlWave(true);
            }
        }

        @Override
        public void onPause() {
            imgBtDes.controlWave(false);
        }

        @Override
        public void onProgressChange(long progressInMs, long totalInMs) {
        }

        @Override
        public void onPlayFailed(int errorCode) {

        }

        @Override
        public void onPlayStop() {
            imgBtDes.controlWave(false);
        }
    };


    @NormalOnClick({EventConstants.NormalClick.btGoToSetting, EventConstants.NormalClick.playOrPause})
    @ResId({R.id.btn_go_to_setting, R.id.btn_start_bt_music})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_go_to_setting:
//                jumpToSetting();
                NodeUtils.jumpTo(
                        mContext,
                        CenterConstants.SETTING,
                        "com.xiaoma.setting.main.ui.MainActivity",
                        NodeConst.Setting.ASSISTANT_ACTIVITY + "/" + NodeConst.Setting.BLUETOOTH_SETTINGS);
                break;
            case R.id.btn_start_bt_music:
                if (BTControlManager.getInstance().getCurrBTMusic() != null) {
                    String title = BTControlManager.getInstance().getCurrBTMusic().getTitle();
                    if (title != null && title.equals(mContext.getString(R.string.have_no_song))) {
                        showToastException(R.string.play_btmusic_failed_empty);
                    }
                }
                if (AudioSourceManager.getInstance().getCurrAudioSource() != AudioSource.BLUETOOTH_MUSIC) {
                    AudioSourceManager.getInstance().switchAudioSource(AudioSource.BLUETOOTH_MUSIC, new AudioSourceManager.PlayerProxy() {
                        @Override
                        public void continuePlay() {
                            BTControlManager.getInstance().switchPlay(true);
                        }

                        @Override
                        public void pause() {
                            BTControlManager.getInstance().switchPlay(false);
                        }
                    });
                }
                BTMusicFactory.getBTMusicControl().switchPlay(true);
                break;
        }
    }

    private void jumpToSetting() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = new ComponentName(mContext.getString(R.string.setting_pkg), mContext.getString(R.string.setting_main_cls));
            intent.setComponent(cn);
            startActivity(intent);
        } catch (Exception e) {
            KLog.e("start setting mainactivity error");
            e.printStackTrace();
        }
    }

    private static boolean isAppInstalled(Context context, String packageName) {
        try {
            return context != null
                    && !TextUtils.isEmpty(packageName)
                    && context.getPackageManager().getApplicationInfo(packageName, 0) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean launchApp(Context context, String packageName, String className, Bundle bundle, boolean newTask) {
        if (context == null || TextUtils.isEmpty(packageName) || packageName.trim().isEmpty()) {
            // 关键参数缺失
            return false;
        }
        if (!isAppInstalled(context, packageName)) {
            // 目标App未安装
            return false;
        }
        Intent intent;
        if (TextUtils.isEmpty(className) || className.trim().isEmpty()) {
            PackageManager pm = context.getPackageManager();
            intent = pm.getLaunchIntentForPackage(packageName);
            if (intent == null) {
                // 无法获取有效Intent
                return false;
            }
        } else {
            intent = new Intent();
            ComponentName componentName = new ComponentName(packageName, className);
            intent.setComponent(componentName);
        }
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            context.startActivity(intent, bundle);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // 跳转时发生异常
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BTMusicFactory.getBTMusicControl().switchPlay(false);
    }
}
