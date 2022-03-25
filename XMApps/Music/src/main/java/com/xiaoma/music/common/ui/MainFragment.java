package com.xiaoma.music.common.ui;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.base.VisibleFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.music.MainActivity;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceListener;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.common.model.PayBusinessEvent;
import com.xiaoma.music.common.model.ShowHideEvent;
import com.xiaoma.music.local.ui.LocalMusicFragment;
import com.xiaoma.music.mine.ui.MineFragment;
import com.xiaoma.music.online.ui.OnlineMusicFragment;
import com.xiaoma.music.player.ui.BtThumbPlayerFragment;
import com.xiaoma.music.player.ui.EmptyThumbPlayerFragment;
import com.xiaoma.music.player.ui.MusicThumbPlayerFragment;
import com.xiaoma.music.player.ui.UsbThumbPlayerFragment;
import com.xiaoma.utils.BackHandlerHelper;
import com.xiaoma.utils.apptool.AppObserver;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.dispatch.annotation.Command;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import static com.xiaoma.music.common.audiosource.AudioSource.BLUETOOTH_MUSIC;
import static com.xiaoma.music.common.audiosource.AudioSource.NONE;
import static com.xiaoma.music.common.audiosource.AudioSource.ONLINE_MUSIC;
import static com.xiaoma.music.common.audiosource.AudioSource.USB_MUSIC;

/**
 * Created by ZYao.
 * Date ：2018/10/9 0009
 */
public class MainFragment extends VisibleFragment implements BackHandlerHelper.FragmentBackHandler {
    private Fragment fragment;
    private final AudioSourceManager mAudioSourceManager = AudioSourceManager.getInstance();
    private AudioSourceListener mAudioSourceListener;
    private View thumbPlayer;
    private MineFragment mineFragment;
    private LocalMusicFragment localMusicFragment;
    private OnlineMusicFragment onlineMusicFragment;
    private LeftTableFragment leftTableFragment;
    private PopupWindow window;
    private View vipView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null && activity.getNaviBar() != null) {
            activity.getNaviBar().showBackAndHomeNavi();
        }
        EventBus.getDefault().register(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        thumbPlayer = view.findViewById(R.id.thumb_player);
        initInsideFragment();

        EventBus.getDefault().register(this);
    }

    private int getCurrentAudioSource() {
        int currAudioSource = mAudioSourceManager.getCurrAudioSource();
        if (currAudioSource == AudioSource.NONE) {
            int audioSource = TPUtils.get(mContext, AudioSourceManager.KEY_AUDIO_SOURCE, AudioSource.NONE);
            if (audioSource == AudioSource.ONLINE_MUSIC) {
                currAudioSource = audioSource;
                OnlineMusicFactory.getKWPlayer().doBeforePlay(true);
            }
        }
        return currAudioSource;
    }

    private void initInsideFragment() {
        leftTableFragment = (LeftTableFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragment_left_table);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                //  在这里去处理你想延时加载的东西
                onlineMusicFragment = new OnlineMusicFragment();
                leftTableFragment.setCallback(new LeftTableFragment.Callback() {
                    @Override
                    public void onlineMusicClick() {
                        skipFragment(fragment, onlineMusicFragment);
                        EventBus.getDefault().post(ShowHideEvent.createOnline(), EventBusTags.SHOW_OR_HIDE_MINE);
                    }

                    @Override
                    public void localMusicClick() {
                        skipFragment(fragment, getLocalMusicFragment());
                        EventBus.getDefault().post(ShowHideEvent.createLocal(), EventBusTags.SHOW_OR_HIDE_MINE);
                    }

                    @Override
                    public void mineClick() {
                        skipFragment(fragment, getMineFragment());
                        EventBus.getDefault().post(ShowHideEvent.createMine(), EventBusTags.SHOW_OR_HIDE_MINE);
                    }
                });
                skipFragment(fragment, onlineMusicFragment);
                EventBus.getDefault().post(ShowHideEvent.createOnline(), EventBusTags.SHOW_OR_HIDE_MINE);
                updateAudioSource(getCurrentAudioSource());
                mAudioSourceManager.addAudioSourceListener(mAudioSourceListener = new AudioSourceListener() {
                    @Override
                    public void onAudioSourceSwitch(@AudioSource int preAudioSource, @AudioSource int currAudioSource) {
                        updateAudioSource(currAudioSource);
                        if (AudioSource.NONE != preAudioSource) {
                            switch (currAudioSource) {
                                case ONLINE_MUSIC:
                                    break;
                                case BLUETOOTH_MUSIC:
                                    KwPlayInfoManager.getInstance().clearCurrentPlayInfo();
                                    break;
                                case USB_MUSIC:
                                    KwPlayInfoManager.getInstance().clearCurrentPlayInfo();
                                    break;
                                case AudioSource.NONE:
                                    break;
                            }
                        }
                    }
                });
                return false;
            }
        });

        /*Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                //  在这里去处理你想延时加载的东西
                onlineMusicFragment = new OnlineMusicFragment();
                leftTableFragment.setCallback(new LeftTableFragment.Callback() {
                    @Override
                    public void onlineMusicClick() {
                        skipFragment(fragment, onlineMusicFragment);
                        EventBus.getDefault().post(ShowHideEvent.createOnline(), EventBusTags.SHOW_OR_HIDE_MINE);
                    }

                    @Override
                    public void localMusicClick() {
                        skipFragment(fragment, getLocalMusicFragment());
                        EventBus.getDefault().post(ShowHideEvent.createLocal(), EventBusTags.SHOW_OR_HIDE_MINE);
                    }

                    @Override
                    public void mineClick() {
                        skipFragment(fragment, getMineFragment());
                        EventBus.getDefault().post(ShowHideEvent.createMine(), EventBusTags.SHOW_OR_HIDE_MINE);
                    }
                });
                skipFragment(fragment, onlineMusicFragment);
                EventBus.getDefault().post(ShowHideEvent.createOnline(), EventBusTags.SHOW_OR_HIDE_MINE);
                // 最后返回false，后续不用再监听了。
                return false;
            }
        });*/

    }

    private LocalMusicFragment getLocalMusicFragment() {
        if (localMusicFragment == null) {
            localMusicFragment = new LocalMusicFragment();
        }
        return localMusicFragment;
    }

    private OnlineMusicFragment getOnlineMusicFragment() {
        if (onlineMusicFragment == null) {
            onlineMusicFragment = new OnlineMusicFragment();
        }
        return onlineMusicFragment;
    }

    private MineFragment getMineFragment() {
        if (mineFragment == null) {
            mineFragment = new MineFragment();
        }
        return mineFragment;
    }


    @Subscriber(tag = EventBusTags.PLAY_MUSIC_ON_BOUGHT_SUCCESSED)
    public void onPlayMusicBuyVipSuccessed(PayBusinessEvent payBusinessEvent) {
        leftTableFragment.skipOnline();
        skipFragment(fragment, onlineMusicFragment);
        EventBus.getDefault().post(ShowHideEvent.createLocal(), EventBusTags.SHOW_OR_HIDE_MINE);
    }


    @Command("打开收藏|我的收藏|打开我的收藏")
    public void openCollect() {
        NodeUtils.jumpTo(mContext, CenterConstants.MUSIC, "com.xiaoma.music.MainActivity", NodeConst.MUSIC.MAIN_ACTIVITY
                + "/" + NodeConst.MUSIC.MAIN_FRAGMENT
                + "/" + NodeConst.MUSIC.MINE_FRAGMENT
                + "/" + NodeConst.MUSIC.OPEN_COLLECTION_LIST);
    }

    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.MUSIC.MINE_FRAGMENT:
                leftTableFragment.skipMine();
                skipFragment(fragment, getMineFragment());
                EventBus.getDefault().post(ShowHideEvent.createMine(), EventBusTags.SHOW_OR_HIDE_MINE);
                return true;
            case NodeConst.MUSIC.MUSIC_THUMB_FRAGMENT:
                if (mAudioSourceManager.getCurrAudioSource() == AudioSource.ONLINE_MUSIC) {
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.thumb_player, new MusicThumbPlayerFragment())
                            .commitAllowingStateLoss();
                }
                return true;
            case NodeConst.MUSIC.LOCAL_FRAGMENT:
                Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                    @Override
                    public boolean queueIdle() {
                        leftTableFragment.skipLocal();
                        skipFragment(fragment, getLocalMusicFragment());
                        return false;
                    }
                });
                return true;
            case NodeConst.MUSIC.ONLINE_FRAGMENT:
                Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                    @Override
                    public boolean queueIdle() {
                        leftTableFragment.skipOnline();
                        skipFragment(fragment, getOnlineMusicFragment());
//                        OnlineMusicFactory.getKWPlayer().continuePlay();
                        return false;
                    }
                });
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.MUSIC.MAIN_FRAGMENT;
    }

    @Override
    public void onResume() {
        super.onResume();
        OnlineMusicFactory.getKWLogin().fetchVipInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAudioSourceManager.removeAudioSourceListener(mAudioSourceListener);
        EventBus.getDefault().unregister(this);
    }

    private void updateAudioSource(@AudioSource int audioSource) {
        Fragment thumb;
        switch (audioSource) {
            case ONLINE_MUSIC:
                thumb = new MusicThumbPlayerFragment();
                break;
            case USB_MUSIC:
                thumb = new UsbThumbPlayerFragment();
                break;
            case BLUETOOTH_MUSIC:
                thumb = new BtThumbPlayerFragment();
                break;
            case NONE:
            default:
                thumb = new EmptyThumbPlayerFragment();
                break;
        }

        try {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.thumb_player, thumb)
                    .commitAllowingStateLoss();
            // 快速切换Content Tab时,概率性导致此Fragment不显示
            // 具体原因不知,调试时发现此Fragment的生命周期,以及当前Activity的View状态均正常,猜测是系统bug
            // 只要让当前Activity走一次onResume,就会正常显示,因此这里强行开启一个不可见并迅速finish掉的Activity
            if (AppObserver.getInstance().isForeground()) {
                startActivity(new Intent(getContext(), FlickerActivity.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void skipFragment(Fragment fromFragment, Fragment toFragment) {
        if (!isAdded()) {
            return;
        }
        if (fromFragment != toFragment) {
            fragment = toFragment;
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (toFragment != null) {
                if (!toFragment.isAdded()) {
                    transaction.add(R.id.child_fragment_content, toFragment);
                } else {
                    transaction.show(toFragment);
                }
                if (fromFragment != null) {
                    transaction.hide(fromFragment);
                }
                transaction.commit();
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }

    public View getThumbPlayer() {
        return thumbPlayer;
    }

    @Subscriber(tag = EventBusTags.MAIN_SHOW_BUY_VIP_VIEW)
    public void showBuyVipView(boolean isShow) {
        try {
            if (window == null || vipView == null) {
                initVipView();
            }
            if (isShow) {
                if (vipView.isAttachedToWindow()) {
                    return;
                }
                int x = getActivity().getResources().getDimensionPixelOffset(R.dimen.thumb_vip_view_x);
                int y = getActivity().getResources().getDimensionPixelOffset(R.dimen.thumb_vip_view_y);
                window.showAtLocation(vipView, Gravity.START | Gravity.BOTTOM, x, y);
            } else {
                if (vipView.isAttachedToWindow()) {
                    window.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initVipView() {
        window = new PopupWindow(getContext());
        vipView = LayoutInflater.from(getActivity()).inflate(R.layout.view_buy_vip_guide, null);
        window.setContentView(vipView);
        window.setBackgroundDrawable(new BitmapDrawable());
        vipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBuyVip();
            }
        });
    }

    private void handleBuyVip() {
        //跳转到会员中心节点
        NodeUtils.jumpTo(mContext, CenterConstants.MUSIC, "com.xiaoma.music.MainActivity", NodeConst.MUSIC.MAIN_ACTIVITY
                + "/" + NodeConst.MUSIC.MAIN_FRAGMENT
                + "/" + NodeConst.MUSIC.MINE_FRAGMENT
                + "/" + NodeConst.MUSIC.OPEN_VIP_CENTER);
    }

}
