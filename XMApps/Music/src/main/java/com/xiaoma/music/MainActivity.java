package com.xiaoma.music;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.business.ui.ChooseUserActivity;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.common.constant.PlayerBroadcast;
import com.xiaoma.music.common.ui.MainFragment;
import com.xiaoma.music.welcome.model.PreferenceBean;
import com.xiaoma.music.welcome.ui.PreferenceSelectFragment;
import com.xiaoma.music.welcome.vm.PreferenceVM;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.utils.BackHandlerHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.dispatch.annotation.Command;

import java.util.List;

import cn.kuwo.mod.playcontrol.PlayMode;

@PageDescComponent(EventConstants.PageDescribe.mainActivityPagePathDesc)
public class MainActivity extends BaseActivity implements View.OnSystemUiVisibilityChangeListener {
    public static final int REQUEST_LOGIN = 0x12;
    private static final int SystemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE
            //| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            ;
    private MainFragment mainfragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setBackgroundDrawableResource(R.drawable.bg_common);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);
        getWindow().getDecorView().setSystemUiVisibility(SystemUiVisibility);
        initEvent();
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        //showToast(String.format("onSystemUiVisibilityChange( visibility: %s )", visibility));
        getWindow().getDecorView().setSystemUiVisibility(SystemUiVisibility);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdateCheck.getInstance().checkAppUpdate(getPackageName(), getApplication());
    }


    private void initEvent() {
        //设置http uid参数在Music中已设置，无需重复设置。
//        if (LoginManager.getInstance().isUserLogin()) {
//            User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
//            if (user != null) {
//                XmHttp.getDefault().addCommonParams("uid", String.valueOf(user.getId()));
//            }
//        }
        selectFragment();
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                if (firstInMusic()) {
                    OnlineMusicFactory.getKWPlayer().setPlayMode(PlayMode.MODE_ALL_ORDER);
                }
            }
        }, 2000);
    }

    private void selectFragment() {
        if (firstInMusic()) {
            getNaviBar().hideNavi();
            if (!LoginManager.getInstance().isUserLogin()) {
                if (!isDestroy()) {
                    ChooseUserActivity.start(this, true);
                    finish();
                    return;
                }
            } else {
                replaceContent(PreferenceSelectFragment.newInstance());
            }
        } else {
            getNaviBar().showBackAndHomeNavi();
            ViewCompat.postOnAnimation(getWindow().getDecorView(), new Runnable() {
                @Override
                public void run() {
                    //  在这里去处理你想延时加载的东西
                    PreferenceVM preferenceVM = ViewModelProviders.of(MainActivity.this).get(PreferenceVM.class);
                    List<PreferenceBean> selectedTags = preferenceVM.getSelectedTags();
                    if (!ListUtils.isEmpty(selectedTags)) {
                        preferenceVM.settingPreference(selectedTags);
                    }
                    replaceContent(mainfragment = MainFragment.newInstance());
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN && resultCode == MainActivity.RESULT_OK) {
            replaceContent(PreferenceSelectFragment.newInstance());
        }
    }

    private boolean firstInMusic() {
        return TPUtils.get(this, MusicConstants.TP_FIRST_START_APP, true);
    }

    public void replaceContent(BaseFragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.view_content, fragment)
                .commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        backPressed();
    }

    private void backPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.view_content);
            if (fragment instanceof MainFragment) {
                if (exitByPlayStatus()) return;
                ConfirmDialog dialog = new ConfirmDialog(this);
                dialog.setContent(getString(R.string.confim_background_play))
                        .setPositiveButton(getString(R.string.background_play), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                moveTaskToBack(true);
                            }
                        })
                        .setNegativeButton(getString(R.string.exit), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                sendBroadcast(new Intent(PlayerBroadcast.Action.PLAYER_CONTROL)
                                        .putExtra(PlayerBroadcast.Extra.CONTROL_CMD, PlayerBroadcast.Command.PAUSE));
                                moveTaskToBack(true);
                            }
                        })
                        .show();
            } else {
                MainActivity.super.onBackPressed();
            }
        }
    }

    private boolean exitByPlayStatus() {
        @AudioSource int currAudioSource = AudioSourceManager.getInstance().getCurrAudioSource();
        boolean exit = false;
        switch (currAudioSource) {
            case AudioSource.BLUETOOTH_MUSIC:
                if (!BTMusicFactory.getBTMusicControl().isPlaying()) {
                    moveTaskToBack(true);
                    exit = true;
                }
                break;
            case AudioSource.ONLINE_MUSIC:
                if (!OnlineMusicFactory.getKWPlayer().isPlaying()) {
                    moveTaskToBack(true);
                    exit = true;
                }
                break;
            case AudioSource.USB_MUSIC:
                if (!UsbMusicFactory.getUsbPlayerProxy().isPlaying()) {
                    moveTaskToBack(true);
                    exit = true;
                }
                break;
            case AudioSource.NONE:
                moveTaskToBack(true);
                exit = true;
                break;
        }
        if (exit) {
            return true;
        }
        return false;
    }

    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.MUSIC.MAIN_FRAGMENT:
                if (mainfragment != null) {
                    replaceContent(mainfragment);
                } else {
                    replaceContent(mainfragment = MainFragment.newInstance());
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.MUSIC.MAIN_ACTIVITY;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent(PlayerBroadcast.Action.PLAYER_CONTROL)
                .putExtra(PlayerBroadcast.Extra.CONTROL_CMD, PlayerBroadcast.Command.PAUSE));
        OnlineMusicFactory.getKWPlayer().saveData(true);
        int currAudioSource = AudioSourceManager.getInstance().getCurrAudioSource();
        if (currAudioSource != AudioSource.ONLINE_MUSIC) {
            AudioSourceManager.getInstance().removeCurrAudioSource();
        }
        TPUtils.put(this, AudioSourceManager.KEY_AUDIO_SOURCE, currAudioSource);
//        unRegisterClearDataListener();
    }

    @Command("打开收藏|我的收藏|打开我的收藏")
    public void openCollect() {
        NodeUtils.jumpTo(MainActivity.this, CenterConstants.MUSIC, "com.xiaoma.music.MainActivity", NodeConst.MUSIC.MAIN_ACTIVITY
                + "/" + NodeConst.MUSIC.MAIN_FRAGMENT
                + "/" + NodeConst.MUSIC.MINE_FRAGMENT
                + "/" + NodeConst.MUSIC.OPEN_COLLECTION_LIST);
    }
}