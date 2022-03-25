package com.xiaoma.club.msg.chat.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.club.Club;
import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;

/**
 * Created by LKF on 2018/10/10 0010.
 */
public abstract class ChatOptFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "ChatOptFragment";
    protected static final float ALPHA_ENABLE = 1.0f;
    protected static final float ALPHA_DISABLE = .3f;
    protected Button mBtnBack;
    protected TextView mTvTitle;
    protected Button mBtnSpeak;
    protected Button mBtnSendFace;
    protected Button mBtnSendLocation;
    protected Button mBtnSendRedPacket;

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnBack = view.findViewById(R.id.btn_back);
        mTvTitle = view.findViewById(R.id.tv_title);
        mBtnSpeak = view.findViewById(R.id.btn_speak);
        mBtnSendFace = view.findViewById(R.id.btn_send_face);
        mBtnSendLocation = view.findViewById(R.id.btn_send_location);
        mBtnSendRedPacket = view.findViewById(R.id.btn_send_red_packet);

        mBtnBack.setOnClickListener(this);
        mBtnSpeak.setOnClickListener(this);
        mBtnSendFace.setOnClickListener(this);
        mBtnSendLocation.setOnClickListener(this);
        mBtnSendRedPacket.setOnClickListener(this);

        mTvTitle.setOnClickListener(this);
        mTvTitle.setText("");
    }

    protected void setTitle(CharSequence text) {
        mTvTitle.setText(text);
    }

    protected String getChatId() {
        String chatId = null;
        if (mCallback != null) {
            chatId = mCallback.getChatId();
        }
        LogUtil.logI(TAG, "getChatId() rlt: %s", chatId);
        return chatId;
    }

    @Override
    @NormalOnClick({ClubEventConstants.NormalClick.returnButton, ClubEventConstants.NormalClick.groupDetail, ClubEventConstants.NormalClick.chatVoiceButton, ClubEventConstants.NormalClick.faceButton, ClubEventConstants.NormalClick.positionShareButton, ClubEventConstants.NormalClick.redPacketButton,})
//按钮对应的名称
    @ResId({R.id.btn_back, R.id.tv_title, R.id.btn_speak, R.id.btn_send_face, R.id.btn_send_location, R.id.btn_send_red_packet})//按钮对应的R文件id
    public void onClick(View v) {
        if (mCallback != null) {
            switch (v.getId()) {
                case R.id.btn_back:
                    mCallback.onBackClick();
                    onGuideTargetClick();
                    break;
                case R.id.btn_speak:
                    mCallback.onSpeakClick();
                    break;
                case R.id.btn_send_face:
                    mCallback.onSendFaceClick();
                    onGuideTargetClick();
                    break;
                case R.id.btn_send_location:
                    mCallback.onLocationClick(v);
                    break;
                case R.id.btn_send_red_packet:
                    mCallback.onSendRedPacketClick(v);
                    break;
                case R.id.tv_title:
                    mCallback.onTitleClick();
                    onGuideTargetClick();
                    break;
            }
        }
    }

    public interface Callback {
        void onBackClick();

        void onSpeakClick();

        void onSendFaceClick();

        void onLocationClick(View v);

        void onSendRedPacketClick(View v);

        void onTitleClick();

        String getChatId();
    }

    protected void onGuideTargetClick() {
    }

    public void showFirstWindow() {
    }
}
