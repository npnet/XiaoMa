package com.xiaoma.club.msg.chat.controller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xiaoma.club.R;
import com.xiaoma.club.msg.chat.constant.ChatMsgViewType;
import com.xiaoma.club.msg.chat.controller.viewholder.ImageMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.LocationMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.MsgTimeHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.RedPacketMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.ShareMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.SystemNotifyMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.TxtMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.UnknownMsgHolder;
import com.xiaoma.club.msg.chat.controller.viewholder.VoiceMsgHolder;

class ChatMsgHolderFactory {
    static RecyclerView.ViewHolder create(@NonNull ViewGroup parent, int type) {
        RecyclerView.ViewHolder holder = null;
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (type) {
            case ChatMsgViewType.VIEW_TYPE_NONE:
            case ChatMsgViewType.VIEW_TYPE_UNKNOWN_MSG:
            default:
                holder = new UnknownMsgHolder(inflater.inflate(R.layout.item_message_unknown, parent, false));
                break;
            case ChatMsgViewType.VIEW_TYPE_TXT_SEND:
                holder = new TxtMsgHolder(inflater.inflate(
                        R.layout.item_send_container, parent, false),
                        R.layout.item_send_message_txt);
                break;
            case ChatMsgViewType.VIEW_TYPE_TXT_RECEIVE:
                holder = new TxtMsgHolder(inflater.inflate(
                        R.layout.item_receive_container, parent, false),
                        R.layout.item_receive_message_txt);
                break;
            case ChatMsgViewType.VIEW_TYPE_VOICE_SEND:
                holder = new VoiceMsgHolder(inflater.inflate(
                        R.layout.item_send_container, parent, false),
                        R.layout.item_send_message_voice);
                break;
            case ChatMsgViewType.VIEW_TYPE_VOICE_RECEIVE:
                holder = new VoiceMsgHolder(inflater.inflate(
                        R.layout.item_receive_container, parent, false),
                        R.layout.item_receive_message_voice);
                break;
            case ChatMsgViewType.VIEW_TYPE_FACE_SEND:
                holder = new ImageMsgHolder(inflater.inflate(
                        R.layout.item_send_container, parent, false),
                        R.layout.item_send_message_image);
                break;
            case ChatMsgViewType.VIEW_TYPE_FACE_RECEIVE:
                holder = new ImageMsgHolder(inflater.inflate(
                        R.layout.item_receive_container, parent, false),
                        R.layout.item_receive_message_image);
                break;
            case ChatMsgViewType.VIEW_TYPE_LOCATION_SEND:
                holder = new LocationMsgHolder(inflater.inflate(
                        R.layout.item_send_container, parent, false),
                        R.layout.item_send_message_location);
                break;
            case ChatMsgViewType.VIEW_TYPE_LOCATION_RECEIVE:
                holder = new LocationMsgHolder(inflater.inflate(
                        R.layout.item_receive_container, parent, false),
                        R.layout.item_receive_message_location);
                break;
            case ChatMsgViewType.VIEW_TYPE_RED_PACKET_RECEIVE:
                holder = new RedPacketMsgHolder(inflater.inflate(
                        R.layout.item_receive_container, parent, false),
                        R.layout.item_receive_message_rp);
                break;
            case ChatMsgViewType.VIEW_TYPE_RED_PACKET_SEND:
                holder = new RedPacketMsgHolder(inflater.inflate(
                        R.layout.item_send_container, parent, false),
                        R.layout.item_send_message_rp);
                break;
            case ChatMsgViewType.VIEW_TYPE_SHARE_RECEIVE:
                holder = new ShareMsgHolder(inflater.inflate(
                        R.layout.item_receive_container, parent, false),
                        R.layout.item_receive_message_share);
                break;
            case ChatMsgViewType.VIEW_TYPE_SHARE_SEND:
                holder = new ShareMsgHolder(inflater.inflate(
                        R.layout.item_send_container, parent, false),
                        R.layout.item_send_message_share);
                break;
            case ChatMsgViewType.VIEW_TYPE_SYSTEM_NOTIFICATION:
                holder = new SystemNotifyMsgHolder(inflater.inflate(R.layout.item_message_system_notify, parent, false));
                break;
            case ChatMsgViewType.VIEW_TYPE_MSG_TIME:
                holder = new MsgTimeHolder(inflater.inflate(R.layout.item_message_time, parent, false));
                break;
        }
        return holder;
    }

}
