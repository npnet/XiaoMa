package com.xiaoma.setting.assistant.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.component.base.VisibleFragment;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.views.SettingItemView;
import com.xiaoma.ui.dialog.InputDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.constant.VrConstants;

import static com.xiaoma.vr.VoiceConfigManager.CONFIG_CHANGE_ACTION;
import static com.xiaoma.vr.VoiceConfigManager.CONFIG_KEYWORD;
import static com.xiaoma.vr.VoiceConfigManager.CONFIG_REPLY;
import static com.xiaoma.vr.VoiceConfigManager.CONFIG_SEOPT;
import static com.xiaoma.vr.VoiceConfigManager.CONFIG_SWITCH;
import static com.xiaoma.vr.VoiceConfigManager.CONFIG_WITHOUT_WAKE;
import static com.xiaoma.vr.VoiceConfigManager.IS_VOICE_WAKEUP_ON;
import static com.xiaoma.vr.VoiceConfigManager.IS_VOICE_WITHOUT_WAKE;
import static com.xiaoma.vr.VoiceConfigManager.KEY_CONFIG_TYPE;
import static com.xiaoma.vr.VoiceConfigManager.KEY_DATA;
import static com.xiaoma.vr.VoiceConfigManager.KEY_SEOPT_TYPE;
import static com.xiaoma.vr.VoiceConfigManager.SEOPT_AUTO;
import static com.xiaoma.vr.VoiceConfigManager.SEOPT_CLOSE;
import static com.xiaoma.vr.VoiceConfigManager.SEOPT_LEFT;
import static com.xiaoma.vr.VoiceConfigManager.SEOPT_RIGHT;
import static com.xiaoma.vr.VoiceConfigManager.VOICE_WAKEUP_WORD;

/**
 * @Author ZiXu Huang
 * @Data 2019/1/11
 */
@PageDescComponent(EventConstants.PageDescribe.assistantSettingFragmentPagePathDesc)
public class AssistantSettingFragment extends VisibleFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final int MAX_WAKEUP_LENGTH = 6;
    private static final int MIN_WAKEUP_LENGTH = 2;
    private static final int MAX_REPLY_LENGTH = 6;
    private CheckBox wakeupSwitch;
    private CheckBox notWakeSwitch;
    private TextView wakeUpWord;
    private SettingItemView mSivReplyType, mSivRecognitionType;
    private LinearLayout mChooseReplyType;
    private TextView tvDefault;
    private RelativeLayout replyContainer;
    private TextView tvReply;
    private ImageView ivEditReplyWord;
    private String uid;
    private static final String REPLY_TYPE = "reply_type";
    private BroadcastReceiver wakeupReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (wakeUpWord != null) {
                String wakeupWordString = getWakeUpKey(VOICE_WAKEUP_WORD, getString(R.string.default_wake_up_word));
                wakeUpWord.setText(wakeupWordString);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assistant_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        view.findViewById(R.id.change_wakeup_word).setOnClickListener(this);
        wakeUpWord = view.findViewById(R.id.wakeup_word);
        String wakeupWordString = getWakeUpKey(VOICE_WAKEUP_WORD, getString(R.string.default_wake_up_word));
        wakeUpWord.setText(wakeupWordString);
        wakeupSwitch = view.findViewById(R.id.wake_up_switch);
        uid = LoginManager.getInstance().getLoginUserId();
        boolean isWakeUpOn = XmProperties.build(uid).get(IS_VOICE_WAKEUP_ON, true);
        wakeupSwitch.setChecked(isWakeUpOn);
        wakeupSwitch.setOnCheckedChangeListener(this);
        notWakeSwitch = view.findViewById(R.id.without_wake_switch);
        notWakeSwitch.setOnCheckedChangeListener(this);
        notWakeSwitch.setChecked(XmProperties.build(uid).get(IS_VOICE_WITHOUT_WAKE, true));
        tvReply = (TextView) view.findViewById(R.id.tv_reply);
        mChooseReplyType = view.findViewById(R.id.ll_choose_reply_type);
        mSivReplyType = view.findViewById(R.id.siv_reply_type);
        String s = XmProperties.build(uid).get(VrConstants.WELCOME_TYPE_KEY, VrConstants.WELCOME_TYPE_MEDIA);
        if (VrConstants.WELCOME_TYPE_MEDIA.equals(s)) {
            mSivReplyType.setCheck(0);
            mChooseReplyType.setVisibility(View.GONE);
        } else {
            mSivReplyType.setCheck(1);
            mChooseReplyType.setVisibility(View.VISIBLE);
        }
        mSivReplyType.setListener(new SettingItemView.StateListener() {
            @Override
            public void onSelect(int viewId, int index) {
                if (index == 0) {
                    XmProperties.build(uid).put(VrConstants.WELCOME_TYPE_KEY, VrConstants.WELCOME_TYPE_MEDIA);
                    mChooseReplyType.setVisibility(View.GONE);
                } else {
                    XmProperties.build(uid).put(VrConstants.WELCOME_TYPE_KEY, VrConstants.WELCOME_TYPE_TEXT);
                    mChooseReplyType.setVisibility(View.VISIBLE);
                }
            }
        });

        mSivRecognitionType = view.findViewById(R.id.siv_recognition_type);
        mSivRecognitionType.setListener(new SettingItemView.StateListener() {
            @Override
            public void onSelect(int viewId, int index) {
                int seoptType = SEOPT_CLOSE;
                switch (index) {
                    case 0:
                        seoptType = SEOPT_CLOSE;
                        break;
                    case 1:
                        seoptType = SEOPT_LEFT;
                        break;
                    case 2:
                        seoptType = SEOPT_RIGHT;
                        break;
                    case 3:
                        seoptType = SEOPT_AUTO;
                        break;
                }
                Log.d("LxWakeupMultipleHelper", "BuildSerEnv:" + ConfigManager.ApkConfig.getBuildSerEnv() + "   uid:" + uid + "  set config is：" + seoptType);
                XmProperties.build(uid).put(KEY_SEOPT_TYPE, seoptType);
                //发送到语音助手
                sendBroadcastReceiver(CONFIG_SEOPT, seoptType);
            }
        });
        mSivRecognitionType.setCheck(XmProperties.build(uid).get(KEY_SEOPT_TYPE, SEOPT_CLOSE) - 1);

        tvDefault = (TextView) view.findViewById(R.id.tv_default);
        tvDefault.setOnClickListener(this);
        replyContainer = (RelativeLayout) view.findViewById(R.id.rl_define_reply);
        replyContainer.setOnClickListener(this);

        tvReply.setOnClickListener(this);
        String replyWordString = XmProperties.build(uid).get(VrConstants.WELCOME_TYPE_TEXT_KEY, getString(R.string.default_reply_word));

        tvReply.setText(replyWordString);
        ivEditReplyWord = view.findViewById(R.id.iv_edit_reply_word);
        ivEditReplyWord.setOnClickListener(this);
        if (getContext() != null) {
            getContext().registerReceiver(wakeupReceiver, new IntentFilter(VrConstants.Actions.WAKEUP_CHANGE));
        }
        String welcomeWord = "";

        if (TextUtils.isEmpty(XmProperties.build(uid).get(VrConstants.WELCOME_TYPE_TEXT_KEY, ""))) {
            setDefaultReplyStyle();
        } else {
            setDefinedReplyStyle();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.change_wakeup_word) {
            changeWakeupWord();
        } else if (v.getId() == R.id.tv_default) {
            setDefaultReplyStyle();
            XmProperties.build(uid).put(VrConstants.WELCOME_TYPE_TEXT_KEY, "");
            XmProperties.build(uid).put(VrConstants.WELCOME_TYPE_KEY, VrConstants.WELCOME_TYPE_TEXT);
            //发送到语音助手
            sendBroadcastReceiver(CONFIG_REPLY, "-1");
        } else if (v.getId() == R.id.rl_define_reply || v.getId() == R.id.tv_reply) {
            setDefinedReplyStyle();
            if (tvReply.length() == 0) {
                changeReplyWord();
            } else {
                String word = tvReply.getText().toString();
                XmProperties.build(uid).put(VrConstants.WELCOME_TYPE_KEY, VrConstants.WELCOME_TYPE_TEXT);
                XmProperties.build(uid).put(VrConstants.WELCOME_TYPE_TEXT_KEY, word);
                //发送到语音助手
                sendBroadcastReceiver(CONFIG_REPLY, word);
            }
        } else if (v.getId() == R.id.iv_edit_reply_word) {
            changeReplyWord();
        }
    }

    private void setDefaultReplyStyle() {
        tvDefault.setBackgroundResource(R.drawable.bg_default_on);
//        tvDefault.setTextColor(Color.parseColor("#ffc640"));
        tvDefault.setTextAppearance(R.style.tv_color_p);
        replyContainer.setBackgroundResource(R.drawable.bg_define_edittext_off);
        tvReply.setTextColor(Color.parseColor("#72889b"));
        tvReply.setHintTextColor(Color.parseColor("#72889b"));
        XmProperties.build(uid).put(VrConstants.WELCOME_TYPE_TEXT_KEY, "");
    }

    private void setDefinedReplyStyle() {
        tvDefault.setBackgroundResource(R.drawable.bg_default_off);
        tvDefault.setTextColor(Color.parseColor("#72889b"));
        replyContainer.setBackgroundResource(R.drawable.bg_define_edittext_on);
//      tvReply.setTextColor(Color.parseColor("#ffc640"));
        tvReply.setTextAppearance(R.style.tv_color_p);
        tvReply.setHintTextColor(Color.parseColor("#ffc640"));
        XmProperties.build(uid).put(VrConstants.WELCOME_TYPE_TEXT_KEY, tvReply.getText().toString());
    }

    private void changeWakeupWord() {
        final InputDialog dialog = new InputDialog(getActivity());
        final EditText etWakeup = dialog.getEditText();
        final String saveWorld = getWakeUpKey(VOICE_WAKEUP_WORD, getString(R.string.default_wake_up_word));
        etWakeup.setText(saveWorld);
        etWakeup.setSelection(etWakeup.getText().toString().length());
        etWakeup.setMaxEms(4);
        etWakeup.requestFocus();
        dialog.setTitle(getString(R.string.please_input_wakeup_word))
                .setPositiveButton(getString(R.string.confirm), new InputDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(String editext) {
                        String inputText = etWakeup.getText().toString().trim();
                        if (saveWorld.equals(inputText)) {
                            dialog.dismiss();
                            return;
                        }
                        if (inputText.length() > MAX_WAKEUP_LENGTH || inputText.length() < MIN_WAKEUP_LENGTH) {
                            XMToast.showToast(AssistantSettingFragment.this.getContext(), getString(R.string.up_max_length));
                            return;
                        }
                        if (!isChinese(inputText)) {
                            XMToast.showToast(AssistantSettingFragment.this.getContext(), getString(R.string.not_chinest));
                            return;
                        }
                        wakeUpWord.setText(inputText);
                        XmProperties.build(uid).put(VOICE_WAKEUP_WORD, inputText);
                        XMToast.toastSuccess(getContext(), R.string.edit_success, false);
                        dialog.dismiss();
                        //发送到语音助手
                        sendBroadcastReceiver(CONFIG_KEYWORD, inputText);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new InputDialog.OnCancelClickListener() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private String getWakeUpKey(String voiceWakeupWord, String def) {
        String voiceString = XmProperties.build(uid).get(voiceWakeupWord, def);
        Log.d("AssistantSettingFragment","voiceString: " + voiceString);
        if ("-1".equals(voiceString) || StringUtil.isEmpty(voiceString)) {
            return def;
        } else {
            return voiceString;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uid = LoginManager.getInstance().getLoginUserId();
        String wakeUpKey = getWakeUpKey(VOICE_WAKEUP_WORD, getString(R.string.default_wake_up_word));
        wakeUpWord.setText(wakeUpKey);
    }

    @Override
    public void onVisibleChange(boolean realVisible) {
        super.onVisibleChange(realVisible);
        if (realVisible) {
            uid = LoginManager.getInstance().getLoginUserId();
            String wakeUpKey = getWakeUpKey(VOICE_WAKEUP_WORD, getString(R.string.default_wake_up_word));
            wakeUpWord.setText(wakeUpKey);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getContext() != null) {
            getContext().unregisterReceiver(wakeupReceiver);
        }
    }

    private void changeReplyWord() {
        final InputDialog dialog = new InputDialog(getActivity());
        final EditText etWakeup = dialog.getEditText();
        String saveWorld = XmProperties.build(uid).get(VrConstants.WELCOME_TYPE_TEXT_KEY, "");
        etWakeup.requestFocus();
        etWakeup.setText(saveWorld);
        etWakeup.setSelection(etWakeup.getText().toString().length());
        etWakeup.setMaxEms(6);
        dialog.setTitle(getString(R.string.please_input_reply_word))
                .setPositiveButton(getString(R.string.confirm), new InputDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(String editext) {
                        String inputText = etWakeup.getText().toString().trim();
                        if (inputText.length() > MAX_REPLY_LENGTH) {
                            XMToast.showToast(AssistantSettingFragment.this.getContext(), getString(R.string.reply_max_length));
                            return;
                        }
                        tvReply.setText(inputText);
                        XmProperties.build(uid).put(VrConstants.WELCOME_TYPE_KEY, VrConstants.WELCOME_TYPE_TEXT);
                        XmProperties.build(uid).put(VrConstants.WELCOME_TYPE_TEXT_KEY, tvReply.getText().toString());
                        //发送到语音助手
                        sendBroadcastReceiver(CONFIG_REPLY, inputText);
                        setDefinedReplyStyle();
                        XMToast.toastSuccess(getContext(), R.string.edit_success, false);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new InputDialog.OnCancelClickListener() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.without_wake_switch://语音免唤醒
                XmProperties.build(uid).put(IS_VOICE_WITHOUT_WAKE, isChecked);
                sendBroadcastReceiver(CONFIG_WITHOUT_WAKE, isChecked);
                break;
            case R.id.wake_up_switch:
                XmProperties.build(uid).put(IS_VOICE_WAKEUP_ON, isChecked);
                //发送到语音助手
                sendBroadcastReceiver(CONFIG_SWITCH, isChecked);
                break;
        }
    }

    private void sendBroadcastReceiver(int configType, String word) {
        Intent intent = new Intent();
        intent.setAction(CONFIG_CHANGE_ACTION);
        intent.putExtra(KEY_CONFIG_TYPE, configType);
        intent.putExtra(KEY_DATA, word);
        mContext.sendBroadcast(intent);
    }

    private void sendBroadcastReceiver(int configType, boolean isChecked) {
        Intent intent = new Intent();
        intent.setAction(CONFIG_CHANGE_ACTION);
        intent.putExtra(KEY_CONFIG_TYPE, configType);
        intent.putExtra(KEY_DATA, isChecked);
        mContext.sendBroadcast(intent);
    }

    private void sendBroadcastReceiver(int configType, int seoptType) {
        Intent intent = new Intent();
        intent.setAction(CONFIG_CHANGE_ACTION);
        intent.putExtra(KEY_CONFIG_TYPE, configType);
        intent.putExtra(KEY_DATA, seoptType);
        mContext.sendBroadcast(intent);
    }

    private boolean isChinese(String string) {
        String reg = "[\\u4e00-\\u9fa5]+";
        return string.matches(reg);
    }

}
