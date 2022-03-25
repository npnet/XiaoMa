package com.xiaoma.club.msg.chat.ui;

import android.app.Dialog;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.carlib.wheelcontrol.OnWheelKeyListener;
import com.xiaoma.carlib.wheelcontrol.WheelKeyEvent;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.club.R;
import com.xiaoma.club.common.constant.PackageName;
import com.xiaoma.club.common.hyphenate.IMUtils;
import com.xiaoma.club.common.model.ClubBaseResult;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.model.Friendship;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.RepoObserver;
import com.xiaoma.club.common.repo.impl.FriendshipRepo;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.common.util.TextUtil;
import com.xiaoma.club.common.util.UploadGroupScoreManager;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.common.viewmodel.BooleanState;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.msg.chat.constant.MessageKey;
import com.xiaoma.club.msg.chat.constant.MessageType;
import com.xiaoma.club.msg.chat.model.HxChatParam;
import com.xiaoma.club.msg.chat.vm.ChatVM;
import com.xiaoma.club.msg.hyphenate.SimpleGroupListener;
import com.xiaoma.club.msg.redpacket.model.RedPacketInfo;
import com.xiaoma.club.msg.redpacket.ui.RPLocationActivity;
import com.xiaoma.club.msg.redpacket.ui.RPSendActivity;
import com.xiaoma.club.msg.redpacket.ui.RPTypeSelectFragment;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.model.User;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.xiaoma.club.common.model.ClubEventConstants.PageDescribe.chatActivity;
import static com.xiaoma.club.common.util.LogUtil.logE;
import static com.xiaoma.club.common.util.LogUtil.logI;

/**
 * Created by LKF on 2018/10/10 0010.
 * 聊天页面
 */
@PageDescComponent(chatActivity)
public class ChatActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();
    public static final String EXTRA_HX_CHAT_ID = "hx_chat_id";
    public static final String EXTRA_IS_GROUP_CHAT = "is_group_chat";
    private static final int REQ_CODE_POI_SELECT = 10;
    private static final int REQ_CODE_SEND_RED_PACKET = 11;
    private NotificationManager notificationManager;

    public static void start(Context context, String chatId, boolean isGroupChat) {
        context.startActivity(new Intent(context, ChatActivity.class)
                .putExtra(EXTRA_HX_CHAT_ID, chatId)
                .putExtra(EXTRA_IS_GROUP_CHAT, isGroupChat)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private ChatVM mChatVM;
    private BroadcastReceiver mReceiver;
    private EMGroupChangeListener mGroupChangeListener;

    @Override
    protected boolean isAppNeedShowNaviBar() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chat);
        final Intent intent = getIntent();
        final String chatId = intent.getStringExtra(EXTRA_HX_CHAT_ID);
        final boolean isGroupChat = intent.getBooleanExtra(EXTRA_IS_GROUP_CHAT, false);
        //打开当前聊天页面，移除聊天对象通知栏
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        removeCurrentChatNoti(chatId);
        final ChatOptFragment chatOptFragment;
        if (isGroupChat) {
            EMClient.getInstance().groupManager().addGroupChangeListener(mGroupChangeListener = new SimpleGroupListener() {
                private boolean isNotCurrGroup(String groupId) {
                    return !Objects.equals(groupId, chatId);
                }

                @Override
                public void onMemberExited(String groupId, String member) {
                    logI(TAG, "onMemberExited ( groupId: %s, member: %s )", groupId, member);
                }

                @Override
                public void onUserRemoved(final String groupId, String groupName) {
                    logI(TAG, "onUserRemoved ( groupId: %s, groupName: %s )", groupId, groupName);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isDestroy() || isNotCurrGroup(groupId))
                                return;
                            final ConfirmDialog dialog = new ConfirmDialog(ChatActivity.this);
                            dialog.setContent(getString(R.string.group_tips_removed))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.group_tips_chat_to_admin), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                dialog.dismiss();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            final GroupCardInfo groupInfo = ClubRepo.getInstance().getGroupRepo().get(groupId);
                                            if (groupInfo != null) {
                                                final String adminHxId = groupInfo.getAdminHxId();
                                                start(ChatActivity.this, adminHxId, false);
                                                logI(TAG, "Chat to admin: %s", adminHxId);
                                            } else {
                                                logE(TAG, "Chat to admin, groupInfo is null");
                                            }
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.group_tips_quit), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                dialog.dismiss();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            finish();
                                        }
                                    });
                            try {
                                dialog.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onGroupDestroyed(final String groupId, String groupName) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isDestroy() || isNotCurrGroup(groupId))
                                return;
                            final ConfirmDialog dialog = new ConfirmDialog(ChatActivity.this);
                            dialog.setContent(getString(R.string.group_tips_destroyed))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.group_tips_confirm), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                dialog.dismiss();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            finish();
                                        }
                                    })
                                    .setNegativeButtonVisibility(false);
                            try {
                                dialog.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onMuteListAdded(String groupId, List<String> mutes, long muteExpire) {
                    LogUtil.logI(TAG, "onMuteListAdded( groupId: %s, mutes: %s, muteExpire: %s)", groupId, mutes, muteExpire);
                    if (isDestroy() || isNotCurrGroup(groupId))
                        return;
                    if (mutes != null && mutes.contains(EMClient.getInstance().getCurrentUser())) {
                        // 当前用户被禁言
                        saveMuteMsg(groupId, R.string.msg_tips_user_was_mute);
                        mChatVM.getMuteState().postValue(BooleanState.TRUE);
                    }
                }

                @Override
                public void onMuteListRemoved(String groupId, List<String> mutes) {
                    LogUtil.logI(TAG, "onMuteListRemoved( groupId: %s, mutes: %s )", groupId, mutes);
                    if (isDestroy() || isNotCurrGroup(groupId))
                        return;
                    if (mutes != null && mutes.contains(EMClient.getInstance().getCurrentUser())) {
                        // 当前用户被解除禁言
                        saveMuteMsg(groupId, R.string.msg_tips_user_was_not_mute);
                        mChatVM.getMuteState().postValue(BooleanState.FALSE);
                    }
                }

                private void saveMuteMsg(String groupId, @StringRes int msgRes) {
                    final EMMessage message = EMMessage.createTxtSendMessage(getString(msgRes), groupId);
                    message.setAttribute(MessageKey.MSG_TYPE, MessageType.SYSTEM_NOTIFY);
                    IMUtils.saveMessage(message);
                }
            });
            chatOptFragment = new GroupChatOptFragment();
        } else {
            chatOptFragment = new SingleChatOptFragment();
        }
        chatOptFragment.setCallback(new ChatOptFragment.Callback() {
            @Override
            public void onBackClick() {
                logI(TAG, "onBackClick");
                finish();
                if (GuideDataHelper.shouldShowGuide(ChatActivity.this, GuideConstants.CLUB_SHOWED, GuideConstants.CLUB_GUIDE_FIRST, false))
                    EventBus.getDefault().post("", "show_main_guide_message");
            }

            @Override
            public void onSpeakClick() {
                logI(TAG, "onSpeakClick");
                startVoiceRecordVR();
            }

            @Override
            public void onSendFaceClick() {
                logI(TAG, "onSendFaceClick");
                if (checkNetworkDisable()) {
                    return;
                }
                // 发送表情
                final SendFaceFragment fragment = new SendFaceFragment();
                fragment.setCallback(new SendFaceFragment.Callback() {
                    @Override
                    public void onImageSelect(String uri) {
                        if (checkNetworkDisable()) {
                            return;
                        }
                        sendFaceMsg(uri);
                        onBackPressed();
                    }
                });
                showOptFragment(fragment);
            }

            @Override
            public void onLocationClick(View v) {
                logI(TAG, "onLocationClick");
                if (checkNetworkDisable()) {
                    return;
                }
                // 位置共享不做了,变成车队管理,这里直接跳转到发送位置
                startActivityForResult(new Intent(ChatActivity.this, SendLocationActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), REQ_CODE_POI_SELECT);
            }

            @Override
            public void onSendRedPacketClick(View v) {
                // TODO:
                RPTypeSelectFragment fmt = new RPTypeSelectFragment();
                fmt.setCallback(new RPTypeSelectFragment.Callback() {
                    @Override
                    public void onTypeSelect(RPTypeSelectFragment.RPType type) {
                        onBackPressed();
                        skipByRPType(type, chatId, isGroupChat);
                    }
                });
                showOptFragment(fmt, false);
            }

            @Override
            public void onTitleClick() {
                logI(TAG, "onTitleClick");
                showOptFragment(getIntent().getBooleanExtra(EXTRA_IS_GROUP_CHAT, false) ?
                        GroupDetailsFragment.newInstance(chatId) : FriendDetailsFragment.newInstance(chatId, new FriendDetailsFragment.Callback() {
                    @Override
                    public void onChatToUser(String hxAccount) {
                        if (!Objects.equals(chatId, hxAccount)) {
                            start(ChatActivity.this, hxAccount, false);
                        } else {
                            onBackPressed();
                        }
                    }
                }));// 打开部落/车友资料
            }

            @Override
            public String getChatId() {
                return chatId;
            }
        });

        // ViewModel初始化
        mChatVM = ViewModelProviders.of(this).get(ChatVM.class);
        mChatVM.getHxChatParam().observe(this, new Observer<HxChatParam>() {
            @Override
            public void onChanged(@Nullable HxChatParam hxChatParam) {
                if (hxChatParam == null) {
                    logE(TAG, "onChanged -> HxChatParam is null !");
                    return;
                }
                if (TextUtils.isEmpty(hxChatParam.getHxChatId())) {
                    logE(TAG, "onChanged -> HxChatId is null !");
                    XMToast.toastException(ChatActivity.this, R.string.hx_empty_error);
                    finish();
                    return;
                }
                logI(TAG, StringUtil.format("onChanged -> [ HxChatParam: %s ]", hxChatParam));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fmt_left, chatOptFragment)
                        .replace(R.id.fmt_right, ChatMsgFragment.create(hxChatParam.getHxChatId(), hxChatParam.isGroupChat(), new ChatMsgFragment.Callback() {
                            @Override
                            public void onAddFriend() {
                                if (!NetworkUtils.isConnected(ChatActivity.this)) {
                                    XMToast.toastException(ChatActivity.this, R.string.net_work_error);
                                    return;
                                }
                                final User other = ClubRepo.getInstance().getUserRepo().getByKey(chatId);
                                if (other != null) {
                                    reqAddFriend(other);
                                } else {
                                    showProgressDialog("");
                                    ClubRequestManager.getUserByHxAccount(chatId, new SimpleCallback<User>() {
                                        @Override
                                        public void onSuccess(User other) {
                                            dismissProgress();
                                            if (other != null) {
                                                reqAddFriend(other);
                                            } else {
                                                XMToast.toastException(ChatActivity.this, R.string.request_add_friend_no_user_failed);
                                            }
                                        }

                                        @Override
                                        public void onError(int code, String msg) {
                                            dismissProgress();
                                            XMToast.toastException(ChatActivity.this, R.string.request_add_friend_no_user_failed);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onUserHeadClick(EMMessage message) {
                                if (EMMessage.Direct.SEND != message.direct()) {
                                    showOptFragment(FriendDetailsFragment.newInstance(message.getFrom(), new FriendDetailsFragment.Callback() {
                                        @Override
                                        public void onChatToUser(String hxAccount) {
                                            if (!Objects.equals(chatId, hxAccount)) {
                                                start(ChatActivity.this, hxAccount, false);
                                            } else {
                                                onBackPressed();
                                            }
                                        }
                                    }));// 打开部落/车友资料
                                }
                            }
                        }))
                        .commitAllowingStateLoss();
            }
        });
        mChatVM.getHxChatParam().setValue(new HxChatParam(chatId, isGroupChat));

        mChatVM.getNetworkEnable().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean enable) {
                findViewById(R.id.tv_network_disconnect).setVisibility(enable != null && enable ? View.GONE : View.VISIBLE);
            }
        });
        mChatVM.getNetworkEnable().setValue(NetworkUtils.isConnected(this));
        // 监听网络连接状态
        registerReceiver(mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                    final boolean enable = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                    LogUtil.logI(TAG, "onReceive() Action: %s, networkEnable: %s", action, enable);
                    mChatVM.getNetworkEnable().setValue(enable);
                }
            }
        }, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        // 拉取信息
        if (isGroupChat) {
            ClubRequestManager.getGroupByHxId(chatId, null);
        } else {
            ClubRequestManager.getUserByHxAccount(chatId, null);
            // 监听好友变化
            mFriendshipRepo.addObserver(mRepoObserver = new RepoObserver() {
                @Override
                public void onChanged(String table) {
                    Friendship friendship = mFriendshipRepo.getFriendship(chatId);
                    mChatVM.getIsFriend().postValue(friendship != null ? BooleanState.TRUE : BooleanState.FALSE);
                }
            });
            mRepoObserver.onChanged("");
            mFriendshipRepo.fetchFriendship();
        }
    }

    private final FriendshipRepo mFriendshipRepo = ClubRepo.getInstance().getFriendshipRepo();
    private RepoObserver mRepoObserver;
    private OnWheelKeyListener mOnWheelKeyListener;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if (mGroupChangeListener != null) {
            EMClient.getInstance().groupManager().removeGroupChangeListener(mGroupChangeListener);
        }
        if (mRepoObserver != null) {
            mFriendshipRepo.removeObserver(mRepoObserver);
        }
    }

    private WeakReference<VrRecordDialog> mVoiceRecordDlgRef;

    @Override
    protected void onStop() {
        super.onStop();
        XmWheelManager.getInstance().unregister(mOnWheelKeyListener);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String chatId = intent.getStringExtra(EXTRA_HX_CHAT_ID);
        boolean isGroupChat = intent.getBooleanExtra(EXTRA_IS_GROUP_CHAT, false);
        start(this, chatId, isGroupChat);
    }

    private void removeCurrentChatNoti(String chatId) {
        notificationManager.cancel(chatId, 0);
    }

    private void reqAddFriend(final User otherUser) {
        final User curr = UserUtil.getCurrentUser();
        if (curr == null) {
            XMToast.showToast(this, R.string.null_user_id);
            return;
        }
        if (otherUser == null) {
            XMToast.toastException(this, R.string.request_add_friend_null_failed);
            return;
        }
        showProgressDialog("");
        ClubRequestManager.requestAddFriend(String.valueOf(curr.getId()), String.valueOf(otherUser.getId()), new CallbackWrapper<ClubBaseResult>() {
            @Override
            public ClubBaseResult parse(String data) {
                return GsonHelper.fromJson(data, ClubBaseResult.class);
            }

            @Override
            public void onSuccess(ClubBaseResult model) {
                super.onSuccess(model);
                if (isDestroy())
                    return;
                if (model != null) {
                    if (model.isSuccess()) {
                        showToast(R.string.request_add_friend);
                    } else if (model.isFrequently()) {
                        showToast(model.getResultMessage());
                    } else if (model.isFriend()) {
                        showToast(R.string.request_is_friend);
                    } else {
                        XMToast.toastException(ChatActivity.this, R.string.request_add_friend_failed);
                    }
                } else {
                    XMToast.toastException(ChatActivity.this, R.string.request_add_friend_failed);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (isDestroy())
                    return;
                if (TextUtil.hasChinese(msg)) {
                    showToast(msg);
                } else {
                    XMToast.toastException(ChatActivity.this, R.string.request_add_friend_failed);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissProgress();
            }
        });
    }

    private boolean checkNetworkDisable() {
        if (!NetworkUtils.isConnected(this)) {
            XMToast.toastException(this, R.string.net_work_error);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && RESULT_OK == resultCode) {
            if (REQ_CODE_POI_SELECT == requestCode) {
                PoiItem poiItem = data.getParcelableExtra(SendLocationActivity.RESULT_POI_ITEM);
                if (poiItem != null) {
                    sendLocationMsg(poiItem);
                }
            } else if (REQ_CODE_SEND_RED_PACKET == requestCode) {
                RedPacketInfo redPacketInfo = data.getParcelableExtra(RPSendActivity.RESULT_RED_PACKET_INFO);
                if (redPacketInfo != null) {
                    sendRedPacketMsg(redPacketInfo);
                }
            }
        }
    }

    // 开启录音
    /*private void startVoiceRecord() {
        final HxChatParam chatParam = ensureHxChatParam("startVoiceRecord");
        if (chatParam == null)
            return;
        final int minSeconds = 2;// 最短时间
        final int maxSeconds = 60;// 最长时间
        final int countdownSeconds = 10;// 开始倒计时剩余时间

        final MediaRecorder recorder = new MediaRecorder();
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setMaxDuration(maxSeconds * 1000);
            recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    logI(TAG, "onInfo( what: %s, extra: %s )", what, extra);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            XMToast.showToast(ChatActivity.this, R.string.recorder_init_fail);
            return;
        }
        final Context context = ChatActivity.this;
        try {
            final File outputDir = new File(Environment.getExternalStorageDirectory(), "ClubVoiceMsg");
            if (!outputDir.isDirectory()) {
                outputDir.delete();
            }
            outputDir.mkdirs();
            final File outputFile = new File(outputDir, String.valueOf(System.currentTimeMillis()));
            recorder.setOutputFile(outputFile.getPath());

            recorder.prepare();
            recorder.start();

            final Handler handler = new Handler(Looper.getMainLooper());
            final Dialog dialog = new AppCompatDialog(context, R.style.custom_dialog2);
            UIUtils.hideNavigationBar(dialog.getWindow());
            dialog.setCancelable(false);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    try {
                        recorder.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 取消录音删除掉录音文件,避免浪费空间
                    outputFile.delete();
                    LogUtil.logI(TAG, "RecordDlg cancel");
                }
            });
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    handler.removeCallbacksAndMessages(null);
                    try {
                        recorder.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    LogUtil.logI(TAG, "RecordDlg onDismiss");
                }
            });
            dialog.setContentView(R.layout.fmt_voice_msg_speak);
            final TextView tvTitle = dialog.findViewById(R.id.tv_title);
            final View btnSend = dialog.findViewById(R.id.btn_send);
            final View btnCancel = dialog.findViewById(R.id.btn_cancel);
            final long recordStartTime = System.currentTimeMillis();
            final View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.btn_send:
                            if ((System.currentTimeMillis() - recordStartTime) <= (minSeconds * 1000)) {
                                tvTitle.setText(R.string.speak_time_too_short);
                                tvTitle.setTextColor(getColor(R.color.chat_send_voice_recorder_duration_too_short));
                                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.chat_send_voice_recorder_duration_too_short));
                                tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.chat_send_voice_recorder_alert, 0, 0, 0);
                                return;
                            }
                            // 要先停止录音,才能读取到完整的音频长度
                            recorder.stop();
                            final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(outputFile.getPath());
                            final String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            long seconds = 0;
                            try {
                                seconds = TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(duration));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            logI(TAG, "RecordDlg voice len: %s s", seconds);
                            sendEMMessage(EMMessage.createVoiceSendMessage(outputFile.getPath(), (int) seconds, chatParam.getHxChatId()), chatParam.isGroupChat());
                            dialog.dismiss();
                            break;
                        case R.id.btn_cancel:
                            dialog.cancel();
                            break;
                    }
                }
            };
            btnSend.setOnClickListener(clickListener);
            btnCancel.setOnClickListener(clickListener);

            // 录音动画
            final ImageView ivWvStart = dialog.findViewById(R.id.iv_wave_start);
            ((AnimationDrawable) ivWvStart.getDrawable()).start();
            final ImageView ivWvEnd = dialog.findViewById(R.id.iv_wave_end);
            ((AnimationDrawable) ivWvEnd.getDrawable()).start();

            handler.postDelayed(new Runnable() {
                private long mSeconds = countdownSeconds;

                @Override
                public void run() {
                    if (!dialog.isShowing())
                        return;
                    if (mSeconds >= 0) {
                        if (tvTitle != null) {
                            tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            tvTitle.setText(String.valueOf(mSeconds--));
                            tvTitle.setTextColor(getColor(R.color.chat_send_voice_recorder_countdown));
                            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.chat_send_voice_recorder_countdown));
                        }
                        handler.postDelayed(this, 1000);
                    } else {
                        btnSend.performClick();
                    }
                }
            }, (maxSeconds - countdownSeconds) * 1000);

            WindowManager.LayoutParams lp;
            if (dialog.getWindow() != null && (lp = dialog.getWindow().getAttributes()) != null) {
                lp.gravity = Gravity.START;
                lp.width = lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            }
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            showToast(getString(R.string.record_fail_format, StringUtil.optString(e.getMessage(), "Unknown reason")));
        }
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        // 监听方控发送语音按键
        XmWheelManager.getInstance().register(mOnWheelKeyListener = new OnWheelKeyListener.Stub() {
            @Override
            public String getPackageName() {
                return ChatActivity.this.getPackageName();
            }

            @Override
            public boolean onKeyEvent(int keyAction, int keyCode) {
                if (WheelKeyEvent.KEYCODE_WHEEL_VOICE == keyCode) {
                    switch (keyAction) {
                        case WheelKeyEvent.ACTION_PRESS:
                            startVoiceRecordVR();
                            break;
                        case WheelKeyEvent.ACTION_RELEASE:
                            sendVoiceRecord();
                            break;
                    }
                }
                return true;
            }
        }, new int[]{WheelKeyEvent.KEYCODE_WHEEL_VOICE});
    }

    // 开启录音,通过VR语音助手来录音+语音听写
    private void startVoiceRecordVR() {
        if (XmCarVendorExtensionManager.getInstance().getCameraStatus()) {
            LogUtil.logE(TAG, "CameraStatus is true, cannot show VR !!!");
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Context context = ChatActivity.this;
                final HxChatParam chatParam = ensureHxChatParam("startVoiceRecordVR");
                if (chatParam == null)
                    return;
                if (checkNetworkDisable())
                    return;
                if (!AppUtils.isAppInstalled(context, PackageName.VR)) {
                    XMToast.showToast(context, R.string.vr_uninstall_tips);
                    return;
                }
                if (isVoiceRecordShowing())
                    return;
                VrRecordDialog dlg = new VrRecordDialog(context, new VrRecordDialog.Callback() {
                    @Override
                    public void onSend(VrRecordDialog dlg, File wavFile, long duration, String translation) {
                        final int seconds = (int) TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS);
                        final EMMessage message = EMMessage.createVoiceSendMessage(wavFile.getPath(), seconds, chatParam.getHxChatId());
                        if (!TextUtils.isEmpty(translation)) {
                            message.setAttribute(MessageKey.Voice.ASR_RESULT, translation);
                        }
                        sendEMMessage(message, chatParam.isGroupChat());
                        if (chatParam.isGroupChat()) {
                            XmAutoTracker.getInstance().onEventSpeechInfo(ClubEventConstants.NormalClick.voiceChat,
                                    duration, message.conversationId(), translation, TAG, chatActivity);
                        } else {
                            XmAutoTracker.getInstance().onEventSpeechInfo(ClubEventConstants.NormalClick.voiceChat,
                                    duration, message.getTo(), translation, TAG, chatActivity);
                        }
                    }

                    @Override
                    public void onError(VrRecordDialog dlg, int errorCode) {
                        showToast(R.string.recorder_failed);
                    }
                });
                dlg.show();
                mVoiceRecordDlgRef = new WeakReference<>(dlg);
            }
        });
    }

    private void sendVoiceRecord() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isVoiceRecordShowing())
                    mVoiceRecordDlgRef.get().sendVoice();
            }
        });
    }

    private boolean isVoiceRecordShowing() {
        if (mVoiceRecordDlgRef == null)
            return false;
        Dialog lastDlg = mVoiceRecordDlgRef.get();
        return lastDlg != null && lastDlg.isShowing();
    }

    private HxChatParam ensureHxChatParam(String method) {
        final HxChatParam chatParam = mChatVM.getHxChatParam().getValue();
        if (chatParam == null) {
            logE(TAG, "%s -> HxChatParam is null !", method);
            return null;
        }
        final String chatId = chatParam.getHxChatId();
        if (TextUtils.isEmpty(chatId)) {
            logE(TAG, "%s -> Invalid HxChatId < %s > !", method, chatId);
            return null;
        }
        return chatParam;
    }

    private void sendFaceMsg(String uri) {
        if (TextUtils.isEmpty(uri)) {
            logE(TAG, StringUtil.format("sendImageMsg -> < Uri >is empty !"));
            return;
        }
        final HxChatParam chatParam = ensureHxChatParam(String.format("sendImageMsg( uri: %s )", uri));
        if (chatParam == null) {
            return;
        }
        final String chatId = chatParam.getHxChatId();
        final boolean isGroupChat = chatParam.isGroupChat();
        logI(TAG, StringUtil.format("sendImageMsg -> [ hxChatId: %s, isGroupChat: %b, uri: %s ]", chatId, isGroupChat, uri));
        final EMMessage message = EMMessage.createTxtSendMessage(uri, chatId);// 发送图片地址，为图片消息
        message.setAttribute(MessageKey.MSG_TYPE, MessageType.FACE);
        message.setAttribute(MessageKey.Face.FACE_URI, uri);
        sendEMMessage(message, isGroupChat);
        XmAutoTracker.getInstance().onBusinessInfoEvent(ClubEventConstants.NormalClick.sendFace,
                uri, chatId, "ChatActivity", chatActivity);
    }

    private void sendLocationMsg(@NonNull PoiItem poiItem) {
        final LatLonPoint latLonPoint = poiItem.getLatLonPoint();
        if (latLonPoint == null) {
            XMToast.showToast(this, R.string.tips_no_lat_lon);
            return;
        }
        final HxChatParam chatParam = ensureHxChatParam(String.format("sendLocationMsg( poiItem: %s )", poiItem));
        if (chatParam == null) {
            return;
        }
        final String chatId = chatParam.getHxChatId();
        final boolean isGroupChat = chatParam.isGroupChat();
        final EMMessage message = EMMessage.createLocationSendMessage(
                latLonPoint.getLatitude(), latLonPoint.getLongitude(),
                poiItem.getSnippet(), chatId);
        message.setAttribute(MessageKey.Location.POI_NAME, poiItem.getTitle());
        sendEMMessage(message, isGroupChat);
    }

    private void sendRedPacketMsg(@NonNull RedPacketInfo redPacketInfo) {
        String dump = GsonHelper.toJson(redPacketInfo);
        final HxChatParam chatParam = ensureHxChatParam(String.format("sendImageMsg( redPacketInfo: %s )", dump));
        if (chatParam == null) {
            return;
        }
        final String chatId = chatParam.getHxChatId();
        final boolean isGroupChat = chatParam.isGroupChat();
        EMMessage message = RedPacketInfo.toMessage(redPacketInfo, chatId, isGroupChat);
        sendEMMessage(message, isGroupChat);
    }

    private void sendEMMessage(EMMessage message, boolean isGroupChat) {
        logI(TAG, String.format("sendEMMessage( message: %s, isGroupChat: %s )", message, isGroupChat));
        message.setChatType(isGroupChat ? EMMessage.ChatType.GroupChat : EMMessage.ChatType.Chat);
        IMUtils.sendMessage(message);
        if (isGroupChat) {
            //上报发送部落消息事件
            UploadGroupScoreManager.getInstance().uploadGroupMessage(message.getTo(), message.getType().toString());
        }
        if (NetworkUtils.isConnected(this)) {
            XmTracker.getInstance().uploadEvent(-1, TrackerCountType.SENDMESSAGE.getType());
            try {
                if (message.getStringAttribute(MessageKey.MSG_TYPE).equals(MessageType.RED_PACKET)) {
                    XmTracker.getInstance().uploadEvent(-1, TrackerCountType.REDPACKETMESSAGE.getType());
                }
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    private void showOptFragment(Fragment fragment) {
        showOptFragment(fragment, true);
    }

    private void showOptFragment(Fragment fragment, boolean slideIn) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (slideIn) {
            transaction.setCustomAnimations(
                    R.anim.slide_in_left, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_left);
        }
        transaction.add(R.id.view_content, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void skipByRPType(final RPTypeSelectFragment.RPType type, final String chatId, final boolean isGroupChat) {
        if (isDestroy())
            return;
        long toId = 0;
        if (isGroupChat) {
            GroupCardInfo group = ClubRepo.getInstance().getGroupRepo().get(chatId);
            if (group != null) {
                toId = group.getId();
            } else {
                showProgressDialog(R.string.base_loading);
                ClubRequestManager.getGroupByHxId(chatId, new SimpleCallback<GroupCardInfo>() {
                    @Override
                    public void onSuccess(GroupCardInfo group) {
                        if (isDestroy())
                            return;
                        dismissProgress();
                        skipByRPType(type, chatId, isGroupChat);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        if (isDestroy())
                            return;
                        dismissProgress();
                        showToastException(R.string.get_group_msg_failed);
                    }
                });
            }
        } else {
            User user = ClubRepo.getInstance().getUserRepo().getByKey(chatId);
            if (user != null) {
                toId = user.getId();
            } else {
                ClubRequestManager.getUserByHxAccount(chatId, new SimpleCallback<User>() {
                    @Override
                    public void onSuccess(User user) {
                        if (isDestroy())
                            return;
                        dismissProgress();
                        skipByRPType(type, chatId, isGroupChat);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        if (isDestroy())
                            return;
                        dismissProgress();
                        showToastException(R.string.get_user_msg_failed);
                    }
                });
            }
        }
        switch (type) {
            case NORMAL:
                startActivityForResult(new Intent(this, RPSendActivity.class)
                                .putExtra(RPSendActivity.EXTRA_TO_ID, toId)
                                .putExtra(RPSendActivity.EXTRA_IS_LOCATION, false)
                                .putExtra(RPSendActivity.EXTRA_IS_GROUP, isGroupChat),
                        REQ_CODE_SEND_RED_PACKET);
                break;
            case LOCATION:
                startActivityForResult(new Intent(this, RPSendActivity.class)
                                .putExtra(RPSendActivity.EXTRA_TO_ID, toId)
                                .putExtra(RPSendActivity.EXTRA_IS_LOCATION, true)
                                .putExtra(RPSendActivity.EXTRA_IS_GROUP, isGroupChat),
                        REQ_CODE_SEND_RED_PACKET);
                break;
            case RP_MAP:
                HxChatParam chatParam = mChatVM.getHxChatParam().getValue();
                if (chatParam != null) {
                    if (chatParam.isGroupChat()) {
                        GroupCardInfo groupCardInfo = ClubRepo.getInstance().getGroupRepo().get(chatParam.getHxChatId());
                        RPLocationActivity.launch(ChatActivity.this, groupCardInfo.getId(), true);
                    } else {
                        User user = ClubRepo.getInstance().getUserRepo().getByKey(chatParam.getHxChatId());
                        RPLocationActivity.launch(ChatActivity.this, user.getId(), false);
                    }
                }
                break;
        }
    }
}
