package com.xiaoma.club.msg.chat.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.RepoObserver;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.common.viewmodel.BooleanState;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.msg.chat.model.GroupMuteUser;
import com.xiaoma.club.msg.chat.vm.ChatVM;
import com.xiaoma.club.msg.chat.vm.GroupChatOptVM;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.StringUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

/**
 * Created by LKF on 2019-1-14 0014.
 */
@PageDescComponent(ClubEventConstants.PageDescribe.groupChatOptFragment)
public class GroupChatOptFragment extends ChatOptFragment {
    private static final String TAG = "GroupChatOptFragment";
    private RepoObserver mMuteObserver;
    //private ImageView mIvGroupIcon;
    //private MemberHeadsView mIvGroupHeads;
    private View mReplayContainer;
    private TextView mTvMuteTips;
    private Button mBtnSpeak;
    private Button mBtnSendFace;
    private Button mBtnSendLocation;
    private ChatVM mChatVM;
    private NewGuide newGuide;
    private boolean guideShowed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fmt_group_chat_opt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mIvGroupIcon = view.findViewById(R.id.iv_group_icon);
        //mIvGroupHeads = view.findViewById(R.id.iv_group_heads);
        mReplayContainer = view.findViewById(R.id.replay_container);
        mTvMuteTips = view.findViewById(R.id.tv_mute_tips);
        mBtnSpeak = view.findViewById(R.id.btn_speak);
        mBtnSendFace = view.findViewById(R.id.btn_send_face);
        mBtnSendLocation = view.findViewById(R.id.btn_send_location);
        initGroupChatOptVM();
        initChatVM();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ClubRepo.getInstance().getGroupMuteUserRepo().removeObserver(mMuteObserver);
    }

    private void initGroupChatOptVM() {
        // 监听群组数据更新消息
        final GroupChatOptVM groupChatOptVM = ViewModelProviders.of(this).get(GroupChatOptVM.class);
        final MutableLiveData<GroupCardInfo> groupInfo = groupChatOptVM.getGroupInfo();
        groupInfo.observe(this, new Observer<GroupCardInfo>() {
            @Override
            public void onChanged(@Nullable GroupCardInfo info) {
                if (info == null
                        || TextUtils.isEmpty(info.getNick())
                        || isDestroy()) {
                    return;
                }
                setTitle(StringUtil.optString(info.getNick()));
                showFirstWindow();
                /*try {
                    Glide.with(GroupChatOptFragment.this)
                            .load(info.getPicPath())
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .dontAnimate()
                            .into(mIvGroupIcon);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                //mIvGroupHeads.setHeadListWithUrl(info.getShowQunPics());
            }
        });

        final LiveData<GroupCardInfo> group = ClubRepo.getInstance().getGroupRepo().getLiveData(getChatId());
        group.observe(this, new Observer<GroupCardInfo>() {
            @Override
            public void onChanged(@Nullable GroupCardInfo groupCardInfo) {
                groupInfo.setValue(groupCardInfo);
            }
        });
        // 监听禁言状态变化
        LiveData<GroupMuteUser> isMute = ClubRepo.getInstance().getGroupMuteUserRepo().isMuteLiveData(getChatId());
        isMute.observe(this, new Observer<GroupMuteUser>() {
            @Override
            public void onChanged(@Nullable GroupMuteUser user) {
                mChatVM.getMuteState().postValue(user != null && user.isMute() ?
                        BooleanState.TRUE : BooleanState.FALSE);
            }
        });
    }

    private void initChatVM() {
        mChatVM = ViewModelProviders.of(getActivity()).get(ChatVM.class);
        final MutableLiveData<BooleanState> muteStateVM = mChatVM.getMuteState();
        muteStateVM.observe(this, new Observer<BooleanState>() {
            @Override
            public void onChanged(@Nullable BooleanState state) {
                LogUtil.logI(TAG, "onChanged( muteState: %s )", state);
                if (state != null) {
                    switch (state) {
                        case FETCHING:
                            mTvMuteTips.setVisibility(View.GONE);
                            mReplayContainer.setAlpha(ALPHA_DISABLE);
                            mBtnSpeak.setEnabled(false);
                            mBtnSendFace.setEnabled(false);
                            mBtnSendRedPacket.setEnabled(false);
                            mBtnSendLocation.setEnabled(false);
                            break;
                        case TRUE:
                            mTvMuteTips.setVisibility(View.VISIBLE);
                            mReplayContainer.setAlpha(ALPHA_DISABLE);
                            mBtnSpeak.setEnabled(false);
                            mBtnSendFace.setEnabled(false);
                            mBtnSendRedPacket.setEnabled(false);
                            mBtnSendLocation.setEnabled(false);
                            break;
                        case FALSE:
                            mTvMuteTips.setVisibility(View.GONE);
                            mReplayContainer.setAlpha(ALPHA_ENABLE);
                            mBtnSpeak.setEnabled(true);
                            mBtnSendFace.setEnabled(true);
                            mBtnSendRedPacket.setEnabled(true);
                            mBtnSendLocation.setEnabled(true);
                            break;
                    }
                }
            }
        });
        muteStateVM.setValue(isMute() ? BooleanState.TRUE : BooleanState.FALSE);
        // 获取禁言状态
        ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
            @Override
            public void run() {
                ClubRequestManager.getGroupMuteListByHxId(getChatId(), null);
            }
        });
    }

    private boolean isMute() {
        return ClubRepo.getInstance().getGroupMuteUserRepo().isMute(getChatId());
    }

    @Subscriber(tag = "left_frg_show_third_guide")
    public void showThirdGuideWindow(String s) {
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.CLUB_SHOWED)
                .setTargetViewAndRect(mBtnBack)
                .setGuideLayoutId(R.layout.guide_view_back_group)
                .setViewSkipId(R.id.tv_guide_skip)
                .setNeedShake(true)
                .setNeedHande(true)
                .setViewHandId(R.id.iv_gesture)
                .setViewWaveIdOne(R.id.iv_wave_one)
                .setViewWaveIdTwo(R.id.iv_wave_two)
                .setViewWaveIdThree(R.id.iv_wave_three)
                .setTargetViewTranslationX(0.05f)
                .build();
        newGuide.showGuide();
    }

    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }

    @Override
    protected void onGuideTargetClick() {
        super.onGuideTargetClick();
        dismissGuideWindow();
    }

    public void showFirstWindow() {
        if (guideShowed) return;
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.CLUB_SHOWED, GuideConstants.CLUB_GUIDE_FIRST, false))
            return;
        EventBus.getDefault().register(this);
        mBtnSendFace.post(new Runnable() {
            @Override
            public void run() {
                Rect viewRect = NewGuide.getViewRect(mBtnSendFace);
                Rect targetRect = new Rect(viewRect.left,viewRect.top,viewRect.right,viewRect.bottom);
                GroupChatOptFragment.this.guideShowed = true;
                newGuide = NewGuide.with(getActivity())
                        .setLebal(GuideConstants.CLUB_SHOWED)
                        .setTargetViewAndRect(mBtnSendFace)
                        .setGuideLayoutId(R.layout.guide_view_send_face)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .setNeedShake(true)
                        .setNeedHande(true)
                        .setViewHandId(R.id.iv_gesture)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setHandLocation(NewGuide.RIGHT_AND_BOTTOM_TOP)
                        .setTargetViewTranslationX(0.03f)
                        .build();
                newGuide.showGuide();
            }
        });
    }

    @Subscriber(tag = "show_guide_group_name")
    public void showSecondGuideWindow(String s) {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.CLUB_SHOWED, GuideConstants.CLUB_GUIDE_FIRST, false))
            return;
        mTvTitle.post(new Runnable() {
            @Override
            public void run() {
                GroupChatOptFragment.this.guideShowed = true;
                Rect rect = NewGuide.getViewRect(mTvTitle);
                Rect targetRect = new Rect(rect.left - 42, rect.top, rect.right, rect.bottom);
                newGuide = NewGuide.with(getActivity())
                        .setLebal(GuideConstants.CLUB_SHOWED)
                        .setTargetView(mTvTitle)
                        .setTargetRect(targetRect)
                        .setGuideLayoutId(R.layout.guide_view_group_name)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .setNeedShake(true)
                        .setNeedHande(true)
                        .setViewHandId(R.id.iv_gesture)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .build();
                newGuide.showGuide();
            }
        });
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}