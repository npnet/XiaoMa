package com.xiaoma.music.player.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.music.R;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceListener;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.utils.FragmentUtils;

import java.util.List;

import skin.support.widget.SkinCompatSupportable;

import static com.xiaoma.music.common.audiosource.AudioSource.BLUETOOTH_MUSIC;
import static com.xiaoma.music.common.audiosource.AudioSource.ONLINE_MUSIC;
import static com.xiaoma.music.common.audiosource.AudioSource.USB_MUSIC;

/**
 * @author zs
 * @date 2018/10/11 0011.
 */
@PageDescComponent(EventConstants.PageDescribe.playerActivity)
public class PlayerActivity extends BaseActivity implements SkinCompatSupportable {

    public final String FRAGMENT_ONLINE_PLAY_TAG = "OnlinePlayFragment";
    public final String FRAGMENT_BT_PLAY_TAG = "BtPlayFragment";
    public final String FRAGMENT_USB_PLAY_TAG = "UsbPlayFragment";
    public final String FRAGMENT_ALBUM_SWITCH_TAG = "AlbumSwitchFragment";
    private final AudioSourceManager mAudioSourceManager = AudioSourceManager.getInstance();
    private AudioSourceListener mAudioSourceListener;

    public static void launch(Context context) {
        Intent intent = new Intent(context, PlayerActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initArgs();
        initView();
        showLastGuide();
    }

    private void initArgs() {
        @AudioSource int currAudioSource = AudioSourceManager.getInstance().getCurrAudioSource();
        switch (currAudioSource) {
            case AudioSource.BLUETOOTH_MUSIC:
                switchFragment(BtPlayFragment.newInstance(), FRAGMENT_BT_PLAY_TAG);
                break;
            case AudioSource.ONLINE_MUSIC:
                switchFragment(OnlinePlayFragment.newInstance(), FRAGMENT_ONLINE_PLAY_TAG);
                break;
            case AudioSource.USB_MUSIC:
                switchFragment(UsbPlayFragment.newInstance(), FRAGMENT_USB_PLAY_TAG);
                break;
            case AudioSource.NONE:
                finish();
                break;
            default:
                break;
        }
        mAudioSourceManager.addAudioSourceListener(mAudioSourceListener = new AudioSourceListener() {
            @Override
            public void onAudioSourceSwitch(@AudioSource int preAudioSource, @AudioSource int currAudioSource) {
                if (AudioSource.NONE != preAudioSource) {
                    switch (currAudioSource) {
                        case ONLINE_MUSIC:
                            switchFragment(OnlinePlayFragment.newInstance(), FRAGMENT_ONLINE_PLAY_TAG);
                            break;
                        case BLUETOOTH_MUSIC:
                            switchFragment(BtPlayFragment.newInstance(), FRAGMENT_BT_PLAY_TAG);
                            break;
                        case USB_MUSIC:
                            switchFragment(UsbPlayFragment.newInstance(), FRAGMENT_USB_PLAY_TAG);
                            break;
                        case AudioSource.NONE:
                            finish();
                            break;
                    }
                }
            }
        });
    }

    private void initView() {
        getNaviBar().hideNavi();
        findViewById(R.id.activity_player_back).setOnClickListener(new View.OnClickListener() {

            @NormalOnClick(EventConstants.NormalClick.exitPlayer)
            @ResId(R.id.activity_player_back)
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void switchFragment(BaseFragment fragment, String tag) {
        try {
            List<Fragment> fragments = FragmentUtils.getFragments(getSupportFragmentManager());
            Fragment fragmentByTag = FragmentUtils.findFragment(getSupportFragmentManager(), tag);
            if (fragmentByTag != null) {
                fragments.remove(fragmentByTag);
                FragmentUtils.showHide(fragmentByTag, fragments);
            } else {
                for (Fragment fg : fragments) {
                    FragmentUtils.hide(fg);
                }
                FragmentUtils.add(getSupportFragmentManager(), fragment, R.id.activity_player_content, tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jumpToAlbumSwitchFragment(BaseFragment fragment, String tag) {
        try {
            List<Fragment> fragments = FragmentUtils.getFragments(getSupportFragmentManager());
            Fragment fragmentByTag = FragmentUtils.findFragment(getSupportFragmentManager(), tag);
            if (fragmentByTag != null) {
                fragments.remove(fragmentByTag);
                FragmentUtils.remove(fragmentByTag);
                for (Fragment fg : fragments) {
                    FragmentUtils.hide(fg);
                }
                FragmentUtils.add(getSupportFragmentManager(), fragment, R.id.activity_player_content, tag);
            } else {
                for (Fragment fg : fragments) {
                    FragmentUtils.hide(fg);
                }
                FragmentUtils.add(getSupportFragmentManager(), fragment, R.id.activity_player_content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.MUSIC.PLAYER_FRAGMENT:
                @AudioSource int currAudioSource = AudioSourceManager.getInstance().getCurrAudioSource();
                if (currAudioSource == AudioSource.ONLINE_MUSIC) {
                    switchFragment(OnlinePlayFragment.newInstance(), FRAGMENT_ONLINE_PLAY_TAG);
                    return true;
                } else if (currAudioSource == AudioSource.USB_MUSIC) {
                    switchFragment(UsbPlayFragment.newInstance(), FRAGMENT_USB_PLAY_TAG);
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.MUSIC.PLAYER_ACTIVITY;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAudioSourceManager.removeAudioSourceListener(mAudioSourceListener);
    }

    private void showLastGuide() {
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.MUSIC_SHOWED, GuideConstants.MUSIC_GUIDE_FIRST, false))
            return;
        NewGuide.with(this)
                .setLebal(GuideConstants.MUSIC_SHOWED)
                .build()
                .showLastGuide();
    }

    @Override
    public void applySkin() {
        OnlinePlayFragment fragment = (OnlinePlayFragment) FragmentUtils.findFragment(getSupportFragmentManager(), FRAGMENT_ONLINE_PLAY_TAG);
        if(fragment != null){
            fragment.updatePlaying();
        }
    }
}
