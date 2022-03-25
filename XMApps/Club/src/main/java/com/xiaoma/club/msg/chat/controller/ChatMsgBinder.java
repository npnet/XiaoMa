package com.xiaoma.club.msg.chat.controller;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.club.R;
import com.xiaoma.club.common.hyphenate.IMUtils;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.msg.chat.constant.ChatMsgViewType;
import com.xiaoma.club.msg.chat.constant.MessageKey;
import com.xiaoma.club.msg.chat.controller.viewholder.BaseMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.ImageMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.LocationMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.MsgTimeHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.RedPacketMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.ShareMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.SystemNotifyMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.TxtMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.UnknownMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.VoiceMsgHolder;
import com.xiaoma.club.msg.chat.model.IsTeamMember;
import com.xiaoma.club.msg.chat.ui.LocationDetailActivity;
import com.xiaoma.club.msg.redpacket.constant.RPOpenStatus;
import com.xiaoma.club.msg.voiceplayer.IVoiceMsgPlayerManager;
import com.xiaoma.club.share.ui.JoinConfirmActivity;
import com.xiaoma.image.transformation.CircleTransform;
import com.xiaoma.model.User;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ShareUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.share.ShareClubBean;
import com.xiaoma.utils.share.ShareConstants;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.xiaoma.club.common.model.ClubEventConstants.NormalClick.positionMsg;
import static com.xiaoma.club.common.model.ClubEventConstants.PageDescribe.chatActivity;
import static com.xiaoma.club.msg.chat.constant.MessageConfig.MAX_VOICE_SECONDS;
import static com.xiaoma.club.msg.chat.constant.MessageConfig.MIN_VOICE_SECONDS;

class ChatMsgBinder {
    private static final long MAX_MSG_RECALL_TIME = TimeUnit.MINUTES.toMillis(2);// 最长可撤回的消息间隔
    private static final String TAG = "ChatMsgBinder";
    private static final int NO_SUCH_TEAM_RESULTCODE = 40038;

    private RequestManager mGlideRequestManager;
    private IVoiceMsgPlayerManager mPlayer;
    private Callback mCallback;

    ChatMsgBinder(RequestManager glideRequestManager, IVoiceMsgPlayerManager player, Callback callback) {
        mGlideRequestManager = glideRequestManager;
        mPlayer = player;
        mCallback = callback;
    }

    void bind(@NonNull RecyclerView.ViewHolder holder, final EMMessage message, int viewType) {
        switch (viewType) {
            case ChatMsgViewType.VIEW_TYPE_NONE:
            case ChatMsgViewType.VIEW_TYPE_UNKNOWN_MSG:
            default:
                bindUnknownMsg((UnknownMsgHolder) holder, message);
                break;
            case ChatMsgViewType.VIEW_TYPE_TXT_SEND:
            case ChatMsgViewType.VIEW_TYPE_TXT_RECEIVE:
                bindTextMsg((TxtMsgHolder) holder, message);
                break;
            case ChatMsgViewType.VIEW_TYPE_VOICE_SEND:
            case ChatMsgViewType.VIEW_TYPE_VOICE_RECEIVE:
                bindVoiceMsg((VoiceMsgHolder) holder, message);
                break;
            case ChatMsgViewType.VIEW_TYPE_FACE_SEND:
            case ChatMsgViewType.VIEW_TYPE_FACE_RECEIVE:
                bindFaceMsg((ImageMsgHolder) holder, message);
                break;
            case ChatMsgViewType.VIEW_TYPE_LOCATION_SEND:
            case ChatMsgViewType.VIEW_TYPE_LOCATION_RECEIVE:
                bindLocationMsg((LocationMsgHolder) holder, message);
                break;
            case ChatMsgViewType.VIEW_TYPE_RED_PACKET_RECEIVE:
            case ChatMsgViewType.VIEW_TYPE_RED_PACKET_SEND:
                bindRedPacketMsg((RedPacketMsgHolder) holder, message);
                break;
            case ChatMsgViewType.VIEW_TYPE_SHARE_SEND:
            case ChatMsgViewType.VIEW_TYPE_SHARE_RECEIVE:
                bindShareMsg((ShareMsgHolder) holder, message);
                break;
            case ChatMsgViewType.VIEW_TYPE_SYSTEM_NOTIFICATION:
                bindSystemNotifyMsg((SystemNotifyMsgHolder) holder, message);
                break;
            case ChatMsgViewType.VIEW_TYPE_MSG_TIME:
                bindMsgTime((MsgTimeHolder) holder, message);
                break;
        }
    }

    /**
     * @param recallView 响应撤回的View
     */
    private void bindRecall(final BaseMsgHolder holder, View recallView, final EMMessage message) {
        if (EMMessage.Direct.SEND == message.direct()) {
            final Context context = holder.itemView.getContext();
            final Resources res = context.getResources();
            recallView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    LogUtil.logI(TAG, "bindVoiceMsg onLongClick");
                    v = holder.itemView.findViewById(R.id.msg_container);
                    final View recallView = View.inflate(context, R.layout.popup_message_recall, null);
                    final int recallW = res.getDimensionPixelSize(R.dimen.recall_popup_width);
                    final int recallH = res.getDimensionPixelSize(R.dimen.recall_popup_height);
                    final PopupWindow recallWin = new PopupWindow(recallView, recallW, recallH);
                    recallWin.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    recallWin.setOutsideTouchable(true);
                    recallWin.setFocusable(true);
                    recallWin.setClippingEnabled(false);

                    int yOff = -v.getMeasuredHeight() - recallH - 10;

                    final int[] locMsg = new int[2];
                    v.getLocationInWindow(locMsg);

                    final int[] locRv = new int[2];
                    ((View) holder.itemView.getParent()).getLocationInWindow(locRv);

                    LogUtil.logI(TAG, "bindVoiceMsg locMsg: %s, locRv: %s", Arrays.toString(locMsg), Arrays.toString(locRv));
                    final int overTopHeight = recallH + (locRv[1] - locMsg[1]);
                    if (overTopHeight > 0) {
                        yOff += overTopHeight;
                    }
                    recallWin.showAsDropDown(v, (v.getMeasuredWidth() - recallW) / 2, yOff);
                    recallWin.getContentView().findViewById(R.id.tv_recall).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!NetworkUtils.isConnected(v.getContext())) {
                                XMToast.toastException(v.getContext(), R.string.net_work_error);
                                return;
                            }
                            final long currTime = System.currentTimeMillis();
                            final long msgTime = message.getMsgTime();
                            if ((currTime - msgTime) <= MAX_MSG_RECALL_TIME) {
                                try {
                                    IMUtils.recallMessage(message);
                                    IMUtils.saveRecallMessageTips(message, context.getString(R.string.recall_msg_notify));
                                } catch (HyphenateException e) {
                                    e.printStackTrace();
                                    XMToast.toastException(context, R.string.recall_msg_error_tips);
                                }
                            } else {
                                XMToast.showToast(context, R.string.chat_msg_cannot_recall);
                            }
                            recallWin.dismiss();
                        }
                    });
                    return true;
                }
            });
        } else {
            recallView.setOnLongClickListener(null);
        }
    }

    private void bindSenderInfo(BaseMsgHolder holder, final EMMessage message) {
        final View.OnClickListener userClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onUserHeadClick(message);
                }
            }
        };
        if (holder.msgUserName != null) {
            String uName = null;
            final User u = ClubRepo.getInstance().getUserRepo().getByKey(message.getFrom());
            if (u != null) {
                uName = u.getName();
            } else {
                // 拉取用户信息
                if (mCallback != null) {
                    mCallback.onFetchUserInfo(message, message.getFrom());
                }
            }
            holder.msgUserName.setText(StringUtil.optString(uName));
            holder.msgUserName.setOnClickListener(userClick);
        }
        if (holder.msgUserImg != null) {
            Object model = null;
            if (EMMessage.Direct.SEND == message.direct()) {
                final User u = UserUtil.getCurrentUser();
                if (u != null) {
                    model = u.getPicPath();
                }
            } else {
                final User u = ClubRepo.getInstance().getUserRepo().getByKey(message.getFrom());
                if (u != null) {
                    model = u.getPicPath();
                }
            }
            try {
                mGlideRequestManager.load(model)
                        .dontAnimate()
                        .transform(new CircleTransform(holder.itemView.getContext()))
                        .placeholder(R.drawable.default_head_icon)
                        .error(R.drawable.default_head_icon)
                        .into(holder.msgUserImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.msgUserImg.setOnClickListener(userClick);
        }

        final TextView tvOccupy = holder.tvOccupy;
        if (tvOccupy != null) {
            @StringRes int occupyRes = 0;
            if (mCallback != null && mCallback.isGroupChat()) {
                final String from = message.getFrom();
                final String to = message.getTo();
                final EMGroup group = EMClient.getInstance().groupManager().getGroup(to);
                if (group != null) {
                    if (Objects.equals(from, group.getOwner())) {
                        occupyRes = R.string.group_owner_occupy;
                    } else if (group.getAdminList().contains(from)) {
                        occupyRes = R.string.group_admin_occupy;
                    }
                }
            }
            if (occupyRes != 0) {
                tvOccupy.setVisibility(View.VISIBLE);
                tvOccupy.setText(occupyRes);
            } else {
                tvOccupy.setVisibility(View.INVISIBLE);
            }
        }

        final ImageView ivStatus = holder.ivStatus;
        if (ivStatus != null) {
            LogUtil.logE(TAG, "message.status() = %s", message.status());
            switch (message.status()) {
                case SUCCESS:
                    ivStatus.clearAnimation();
                    ivStatus.setVisibility(View.GONE);
                    break;
                case INPROGRESS:
                    ivStatus.setVisibility(View.VISIBLE);
                    ivStatus.setImageResource(R.drawable.chat_msg_state_sending);
                    if (ivStatus.getAnimation() != null) {
                        ivStatus.getAnimation().start();
                    } else {
                        final Animation anim = AnimationUtils.loadAnimation(ivStatus.getContext(), R.anim.rotate);
                        anim.setRepeatCount(Animation.INFINITE);
                        anim.setRepeatMode(Animation.RESTART);
                        anim.setInterpolator(new LinearInterpolator());
                        ivStatus.startAnimation(anim);
                    }
                    ivStatus.setOnClickListener(null);
                    break;
                case CREATE:
                case FAIL:
                default:
                    ivStatus.clearAnimation();
                    ivStatus.setVisibility(View.VISIBLE);
                    ivStatus.setImageResource(R.drawable.chat_msg_state_send_failed);
                    ivStatus.clearAnimation();
                    ivStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCallback != null) {
                                mCallback.onReSendMsg(message);
                            }
                        }
                    });
                    break;
            }
        }
    }

    private void bindTextMsg(TxtMsgHolder holder, EMMessage message) {
        bindSenderInfo(holder, message);
        bindRecall(holder, holder.messageVoiceText, message);
        final EMTextMessageBody voiceBody = (EMTextMessageBody) message.getBody();
        holder.messageVoiceText.setText(voiceBody.getMessage());
    }

    private void bindVoiceMsg(VoiceMsgHolder holder, final EMMessage message) {
        bindSenderInfo(holder, message);
        bindRecall(holder, holder.msgContainer, message);
        final Context context = holder.itemView.getContext();
        final EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();
        // 语音时长
        holder.tvVoiceDuration.setText(getVoiceLenText(voiceBody.getLength()));
        final String astRlt = message.getStringAttribute(MessageKey.Voice.ASR_RESULT, "");
        holder.messageVoiceText.setText(astRlt);
        // 是否在播放中
        final ImageView ivAnim = holder.messageVoiceWaveImage;
        if (mPlayer.isPlaying() && Objects.equals(mPlayer.getCurrMessage(), message)) {
            final Drawable dr = ivAnim.getDrawable();
            if (dr instanceof AnimationDrawable) {
                ((AnimationDrawable) dr).start();
            } else {
                if (EMMessage.Direct.SEND == message.direct()) {
                    ivAnim.setImageResource(R.drawable.chat_msg_voice_playing_anim_send);
                } else {
                    ivAnim.setImageResource(R.drawable.chat_msg_voice_playing_anim_recv);
                }
                ((AnimationDrawable) ivAnim.getDrawable()).start();
            }
        } else {
            ivAnim.setImageResource(EMMessage.Direct.SEND == message.direct() ?
                    R.drawable.chat_msg_voice_playing_send_2 : R.drawable.chat_msg_voice_playing_recv_2);
        }
        // 是否未听
        final View dot = holder.ivDot;
        if (dot != null) {
            if (EMMessage.Direct.RECEIVE == message.direct() && !message.isListened()) {
                dot.setVisibility(View.VISIBLE);
            } else {
                dot.setVisibility(View.GONE);
            }
        }
        holder.msgContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.logI(TAG, "bindVoiceMsg onClick");
                if (mCallback != null) {
                    mCallback.onVoiceClick(message);
                }
            }
        });
        try {
            final LinearLayout.LayoutParams lpStatus = (LinearLayout.LayoutParams) holder.ivStatus.getLayoutParams();
            if (lpStatus != null) {
                lpStatus.bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.chat_msg_voice_txt_bottom_padding) / 2;
            }
        } catch (Exception ignored) {
        }
    }

    private void bindFaceMsg(ImageMsgHolder holder, final EMMessage message) {
        bindSenderInfo(holder, message);
        bindRecall(holder, holder.messageImageIcon, message);
        final String uri = message.getStringAttribute(MessageKey.Face.FACE_URI, "");
        try {
            mGlideRequestManager.load(uri)
                    .dontAnimate()
                    .placeholder(R.drawable.default_placeholder)
                    .error(R.drawable.default_placeholder)
                    .into(holder.messageImageIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.messageImageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onFaceClick(message, uri);
                }
            }
        });
    }

    private void bindLocationMsg(LocationMsgHolder holder, final EMMessage message) {
        bindSenderInfo(holder, message);
        bindRecall(holder, holder.locationItemContainer, message);
        final String poiName = message.getStringAttribute(MessageKey.Location.POI_NAME, "");
        final String poiAddress = ((EMLocationMessageBody) message.getBody()).getAddress();

        holder.tvPoiName.setText(poiName);
        holder.tvPoiAddress.setText(poiAddress);
        holder.locationItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EMLocationMessageBody locationBody = (EMLocationMessageBody) message.getBody();
                LocationDetailActivity.start(v.getContext(), locationBody.getLatitude(), locationBody.getLongitude(), poiName, poiAddress);
                XmAutoTracker.getInstance().onEvent(positionMsg, positionMsg, "ChatActivity", chatActivity);
            }
        });
    }

    private void bindRedPacketMsg(RedPacketMsgHolder holder, final EMMessage message) {
        bindSenderInfo(holder, message);
        bindRecall(holder, holder.containerPacket, message);
        holder.tvGreeting.setText(message.getStringAttribute(MessageKey.RedPacket.GREETING, ""));
        boolean isLocation = message.getBooleanAttribute(MessageKey.RedPacket.IS_LOCATION, false);
        if (isLocation) {
            holder.tvRpType.setText(R.string.rp_type_location);
            holder.tvPoiDisplay.setVisibility(View.VISIBLE);
            holder.tvPoiDisplay.setText(message.getStringAttribute(MessageKey.RedPacket.POI_ADDRESS, ""));
        } else {
            holder.tvRpType.setText(R.string.rp_type_normal);
            holder.tvPoiDisplay.setVisibility(View.INVISIBLE);
        }
        float packetAlpha;
        int openStatus = message.getIntAttribute(MessageKey.RedPacket.OPEN_STATUS, -1);
        switch (openStatus) {
            case RPOpenStatus.NOT_OPEN:
            default:
                packetAlpha = 1f;
                break;
            case RPOpenStatus.HAS_OPEN:
            case RPOpenStatus.EXPIRED:
            case RPOpenStatus.EMPTY:
                packetAlpha = 0.4f;
                break;
        }
        holder.containerPacket.setAlpha(packetAlpha);
        holder.containerPacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onOpenRedPacket(message);
                }
            }
        });
    }

    private void bindShareMsg(final ShareMsgHolder holder, final EMMessage message) {
        bindSenderInfo(holder, message);
        bindRecall(holder, holder.msgContainer, message);
        holder.tvShareTitle.setText(message.getStringAttribute(MessageKey.Share.TITLE, ""));
        holder.tvShareContent.setText(message.getStringAttribute(MessageKey.Share.CONTENT, ""));
        final String uri = message.getStringAttribute(MessageKey.Share.URL, "");
        try {
            mGlideRequestManager.load(uri)
                    .dontAnimate()
                    .placeholder(R.drawable.motorcade_icon)
                    .error(R.drawable.motorcade_icon)
                    .into(holder.ivShareIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.msgContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = holder.itemView.getContext();
                if (context == null) {
                    return;
                }
                ShareClubBean shareClubBean = new ShareClubBean();
                shareClubBean.setCoreKey(message.getStringAttribute(MessageKey.Share.CORE, ""));
                shareClubBean.setFromPackage(message.getStringAttribute(MessageKey.Share.FROM_PACK, ""));
                shareClubBean.setBackAction(message.getStringAttribute(MessageKey.Share.BACK, ""));
                shareClubBean.setShareImage(message.getStringAttribute(MessageKey.Share.URL, ""));
                shareClubBean.setCarTeamDetail(message.getStringAttribute(MessageKey.Share.FROM_DETAIL, ""));
                shareClubBean.setCarTeamId(message.getLongAttribute(MessageKey.Share.SHARE_CAR_ID, 0));
                handleJump(context, shareClubBean);
                XmAutoTracker.getInstance().onEvent("分享", GsonHelper.toJson(shareClubBean),
                        "ChatActivity", chatActivity);
                KLog.d("click share msg" + message.getFrom());
            }
        });
    }

    private void handleJump(final Context context, final ShareClubBean shareClubBean) {
        if (ShareConstants.MOTORCADE_HANDLE_SHARE_ACTION.equals(shareClubBean.getBackAction())) {
            //车队的分享消息
            ClubRequestManager.isCarTeamMember(UserUtil.getCurrentUid(), shareClubBean.getCarTeamId(),
                    new CallbackWrapper<IsTeamMember>() {

                        @Override
                        public IsTeamMember parse(String data) throws Exception {
                            return GsonHelper.fromJson(data, IsTeamMember.class);
                        }

                        @Override
                        public void onSuccess(IsTeamMember model) {
                            super.onSuccess(model);
                            if (model == null) {
                                XMToast.toastException(context, context.getString(R.string.net_work_error));
                                return;
                            } else {
                                if (model.getResultCode() == NO_SUCH_TEAM_RESULTCODE) {
                                    showTeamDismissDialog(context);
                                    return;
                                } else if (model.getResultCode() != 1) {
                                    XMToast.toastException(context, model.getResultMessage());
                                    return;
                                }
                            }
                            if (model.getData().isInCarTeam()) {
                                if (!AppUtils.isAppInstalled(context, shareClubBean.getFromPackage())) {
                                    XMToast.toastException(context, context.getString(R.string.is_not_installed));
                                    return;
                                }
                                Intent intent = new Intent();
                                intent.setAction(shareClubBean.getBackAction());
                                intent.putExtra(ShareUtils.SHARE_KEY, shareClubBean.getCoreKey());
                                intent.putExtra(ShareUtils.HAS_BEEN_MEMBER, true);
                                intent.putExtra(ShareUtils.CAR_TEAM_ID, shareClubBean.getCarTeamId());
                                context.startActivity(intent);
                            } else {
                                Intent intent = new Intent(context, JoinConfirmActivity.class);
                                intent.putExtra(ShareUtils.SHARE_KEY, shareClubBean);
                                context.startActivity(intent);
                            }
                        }

                        @Override
                        public void onError(int code, String msg) {
                            super.onError(code, msg);
                            XMToast.toastException(context, context.getString(R.string.net_work_error));
                        }
                    });
        } else if (ShareConstants.CAR_PARK_HANDLE_SHARE_ACTION.equals(shareClubBean.getBackAction())) {
            //车乐园的分享消息
            if (!AppUtils.isAppInstalled(context, shareClubBean.getFromPackage())) {
                XMToast.toastException(context, context.getString(R.string.is_not_installed));
                return;
            }
            Intent intent = new Intent();
            intent.setAction(shareClubBean.getBackAction());
            intent.putExtra(ShareUtils.SHARE_KEY, shareClubBean.getCoreKey());
            intent.putExtra(ShareUtils.HAS_BEEN_MEMBER, true);
            intent.putExtra(ShareUtils.CAR_TEAM_ID, shareClubBean.getCarTeamId());
            context.startActivity(intent);
        }
    }

    private void showTeamDismissDialog(Context context) {
        FragmentActivity activity = (FragmentActivity) context;
        final ConfirmDialog dialog = new ConfirmDialog(activity);
        dialog.setTitle(activity.getString(R.string.group_tips_title))
                .setContent(context.getString(R.string.team_dismissed))
                .setPositiveButton(context.getString(R.string.share_yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButtonVisibility(false)
                .show();
    }

    private void bindUnknownMsg(UnknownMsgHolder holder, EMMessage message) {
        // do nothing:
    }

    private void bindSystemNotifyMsg(SystemNotifyMsgHolder holder, EMMessage message) {
        final EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        holder.tvNotifyContent.setText(body.getMessage());
    }

    private void bindMsgTime(MsgTimeHolder holder, EMMessage message) {
        final EMTextMessageBody textBody = (EMTextMessageBody) message.getBody();
        holder.tvMsgTime.setText(textBody.getMessage());
    }

    private static String getImageUri(EMImageMessageBody messageBody) {
        String uri = null;
        if (messageBody != null) {
            uri = messageBody.getLocalUrl();
            if (TextUtils.isEmpty(uri) || !new File(uri).exists()) {
                uri = messageBody.getRemoteUrl();
            }
        }
        return uri;
    }

    /**
     * @param len 时长单位:秒
     */
    private static String getVoiceLenText(int len) {
        // 至少1秒,最多60s
        len = Math.min(Math.max(MIN_VOICE_SECONDS, len), MAX_VOICE_SECONDS);
        // 特殊处理:录音那里有时候录不满60秒,这里强行显示60
        if (len == MAX_VOICE_SECONDS - 1) {
            len = MAX_VOICE_SECONDS;
        }
        return String.format("%s″", len);
    }

    interface Callback {
        void onUserHeadClick(EMMessage message);

        void onFaceClick(EMMessage message, String uri);

        void onVoiceClick(EMMessage message);

        void onFetchUserInfo(EMMessage message, String hxAccount);

        void onReSendMsg(EMMessage message);

        void onOpenRedPacket(EMMessage message);

        boolean isGroupChat();
    }
}
