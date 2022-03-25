package com.xiaoma.club.msg.chat.controller;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.hyphenate.chat.EMMessage;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.club.msg.chat.constant.ChatMsgViewType;
import com.xiaoma.club.msg.chat.constant.MessageKey;
import com.xiaoma.club.msg.chat.constant.MessageType;
import com.xiaoma.club.msg.voiceplayer.IVoiceMsgPlayerManager;

import java.util.Objects;

import static com.xiaoma.club.common.model.ClubEventConstants.NormalClick.faceMsg;
import static com.xiaoma.club.common.model.ClubEventConstants.NormalClick.friendIcon;
import static com.xiaoma.club.common.model.ClubEventConstants.NormalClick.redPacketMsg;
import static com.xiaoma.club.common.model.ClubEventConstants.NormalClick.voiceMsg;
import static com.xiaoma.club.common.model.ClubEventConstants.PageDescribe.chatActivity;

/**
 * Created by LKF on 2018/10/15 0015.
 */
public class ChatMsgListAdapter extends PagedListAdapter<EMMessage, RecyclerView.ViewHolder> {
    private static final String TAG = "ChatMsgListAdapter";
    private ChatMsgBinder mMsgBinder;

    public ChatMsgListAdapter(RequestManager glideRequestManager, IVoiceMsgPlayerManager player, final ChatMsgListAdapter.Callback callback) {
        super(new DiffUtil.ItemCallback<EMMessage>() {
            @Override
            public boolean areItemsTheSame(@NonNull EMMessage oldItem, @NonNull EMMessage newItem) {
                return oldItem == newItem ||
                        Objects.equals(oldItem.getMsgId(), newItem.getMsgId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull EMMessage oldItem, @NonNull EMMessage newItem) {
                return false;
                /*return Objects.equals(oldItem.getBody(), newItem.getBody()) &&
                        oldItem.direct() == newItem.direct() &&
                        oldItem.isUnread() == newItem.isUnread() &&
                        oldItem.isListened() == newItem.isListened() &&
                        oldItem.status() == newItem.status() &&
                        oldItem.progress() == newItem.progress() &&
                        Objects.equals(
                                oldItem.getStringAttribute(MessageKey.MSG_TYPE, null),
                                newItem.getStringAttribute(MessageKey.MSG_TYPE, null));*/
            }
        });
        mMsgBinder = new ChatMsgBinder(glideRequestManager, player, new ChatMsgBinder.Callback() {
            @Override
            public void onUserHeadClick(EMMessage message) {
                if (callback != null) {
                    callback.onUserHeadClick(message);
                    XmAutoTracker.getInstance().onEvent(friendIcon, friendIcon, "ChatActivity", chatActivity);
                }
            }

            @Override
            public void onFaceClick(EMMessage message, String uri) {
                if (callback != null) {
                    callback.onFaceClick(message, uri);
                    XmAutoTracker.getInstance().onEvent(faceMsg, faceMsg, "ChatActivity", chatActivity);
                }
            }

            @Override
            public void onVoiceClick(EMMessage message) {
                if (callback != null) {
                    callback.onVoiceClick(message);
                    XmAutoTracker.getInstance().onEvent(voiceMsg, voiceMsg, "ChatActivity", chatActivity);
                }
            }

            @Override
            public void onFetchUserInfo(EMMessage message, String hxAccount) {
                if (callback != null) {
                    callback.onFetchUserInfo(message, hxAccount);
                }
            }

            @Override
            public void onReSendMsg(EMMessage message) {
                if (callback != null) {
                    callback.onReSendMsg(message);
                }
            }

            @Override
            public void onOpenRedPacket(EMMessage message) {
                if (callback != null) {
                    callback.onOpenRedPacket(message);
                    XmAutoTracker.getInstance().onEvent(redPacketMsg, redPacketMsg, "ChatActivity", chatActivity);
                }
            }

            @Override
            public boolean isGroupChat() {
                return callback != null && callback.isGroupChat();
            }
        });
    }

    /*@Override
    public void onCurrentListChanged(@Nullable PagedList<EMMessage> currentList) {
        super.onCurrentListChanged(currentList);
        LogUtil.logI(TAG, String.format("onCurrentListChanged( currentList: %s ) Size: %s", currentList, currentList != null ? currentList.size() : "null"));
    }*/

    @Override
    public int getItemViewType(int position) {
        int type = ChatMsgViewType.VIEW_TYPE_NONE;
        final EMMessage message = getItem(position);
        if (message != null) {
            final boolean isSend = EMMessage.Direct.SEND == message.direct();
            final EMMessage.Type emType = message.getType();
            switch (emType) {
                case TXT:
                    final String msgType = message.getStringAttribute(MessageKey.MSG_TYPE, "");
                    if (MessageType.SYSTEM_NOTIFY.equalsIgnoreCase(msgType)) {
                        type = ChatMsgViewType.VIEW_TYPE_SYSTEM_NOTIFICATION;
                    } else if (MessageType.MSG_TIME.equalsIgnoreCase(msgType)) {
                        type = ChatMsgViewType.VIEW_TYPE_MSG_TIME;
                    } else if (MessageType.FACE.equalsIgnoreCase(msgType)) {
                        type = isSend ? ChatMsgViewType.VIEW_TYPE_FACE_SEND : ChatMsgViewType.VIEW_TYPE_FACE_RECEIVE;
                    } else if (MessageType.RED_PACKET.equalsIgnoreCase(msgType)) {
                        type = isSend ? ChatMsgViewType.VIEW_TYPE_RED_PACKET_SEND : ChatMsgViewType.VIEW_TYPE_RED_PACKET_RECEIVE;
                    } else if (MessageType.SHARE.equalsIgnoreCase(msgType)) {
                        type = isSend ? ChatMsgViewType.VIEW_TYPE_SHARE_SEND : ChatMsgViewType.VIEW_TYPE_SHARE_RECEIVE;
                    } else {
                        type = isSend ? ChatMsgViewType.VIEW_TYPE_TXT_SEND : ChatMsgViewType.VIEW_TYPE_TXT_RECEIVE;
                    }
                    break;
                case IMAGE:
                    break;
                case VIDEO:
                    break;
                case LOCATION:
                    type = isSend ? ChatMsgViewType.VIEW_TYPE_LOCATION_SEND : ChatMsgViewType.VIEW_TYPE_LOCATION_RECEIVE;
                    break;
                case VOICE:
                    type = isSend ? ChatMsgViewType.VIEW_TYPE_VOICE_SEND : ChatMsgViewType.VIEW_TYPE_VOICE_RECEIVE;
                    break;
                case FILE:
                    break;
                case CMD:
                    break;
            }
        }
        return type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ChatMsgHolderFactory.create(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        mMsgBinder.bind(holder, getItem(position), getItemViewType(position));
    }

    public interface Callback extends ChatMsgBinder.Callback {

    }
}
