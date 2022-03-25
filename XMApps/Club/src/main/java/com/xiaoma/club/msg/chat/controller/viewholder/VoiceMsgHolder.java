package com.xiaoma.club.msg.chat.controller.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.club.R;

/**
 * Author: loren
 * Date: 2018/10/17 0017
 */

public class VoiceMsgHolder extends BaseMsgHolder {
    public ImageView messageVoiceWaveImage;
    public TextView messageVoiceText;
    public TextView tvVoiceDuration;
    public View ivDot;
    public View textContainer;

    public VoiceMsgHolder(View itemView, int childId) {
        super(itemView, childId);
        //R.layout.item_receive_message_txt
        messageVoiceWaveImage = itemView.findViewById(R.id.message_voice_wave_image);
        messageVoiceText = itemView.findViewById(R.id.message_voice_text);
        tvVoiceDuration = itemView.findViewById(R.id.tv_voice_duration);
        ivDot = itemView.findViewById(R.id.iv_dot);
        textContainer = itemView.findViewById(R.id.text_container);
    }
}
