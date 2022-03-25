package com.xiaoma.club.msg.chat.controller.viewholder;

import android.view.View;
import android.widget.TextView;

import com.xiaoma.club.R;

/**
 * Author: loren
 * Date: 2018/10/17 0017
 */

public class TxtMsgHolder extends BaseMsgHolder {
    public TextView messageVoiceText;

    public TxtMsgHolder(View itemView, int childId) {
        super(itemView, childId);
        //R.layout.item_receive_message_txt
        messageVoiceText = itemView.findViewById(R.id.message_voice_text);
    }
}
