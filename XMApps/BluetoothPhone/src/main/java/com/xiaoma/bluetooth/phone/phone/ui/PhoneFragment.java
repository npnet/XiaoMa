package com.xiaoma.bluetooth.phone.phone.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.constants.EventBusTags;
import com.xiaoma.bluetooth.phone.common.constants.EventConstants;
import com.xiaoma.bluetooth.phone.common.constants.ViewState;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.manager.WheelOperatePhoneManager;
import com.xiaoma.bluetooth.phone.common.model.BluePhoneState;
import com.xiaoma.bluetooth.phone.common.utils.ContactNameUtils;
import com.xiaoma.bluetooth.phone.common.utils.LastTimeFormatUtils;
import com.xiaoma.bluetooth.phone.common.utils.OperateUtils;
import com.xiaoma.bluetooth.phone.common.utils.RouteUtils;
import com.xiaoma.bluetooth.phone.common.views.CircleCharAvatarView;
import com.xiaoma.bluetooth.phone.main.ui.BaseBluetoothFragment;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import org.simple.eventbus.Subscriber;
import java.util.List;

/**
 * 通话页(通话中|挂断中等）
 *
 * @author: iSun
 * @date: 2018/11/16 0016
 */
@PageDescComponent(EventConstants.PageDescribe.phoneFragmentPagePathDesc)
public class PhoneFragment extends BaseBluetoothFragment implements View.OnClickListener, WheelOperatePhoneManager.OnWheelOperatePhoneListener {

    private TextView currentStatus;
    private CircleCharAvatarView iconContact;
    private TextView name;
    private TextView phoneNum;
    private TextView callLastTime;
    private LinearLayout mLayoutSwitch;
    private RelativeLayout mLayoutOneTarget;
    private LinearLayout mLayoutTwoTarget;
    private TextView mTvFirstTargetName;
    private TextView mTvFirstTargetState;
    private TextView mTvSecondTargetName;
    private TextView mTvSecondTargetState;
    private ImageView mIvMute;
    private ImageView mIvAnswer;
    private TextView mTvAnswerType;
    private RelativeLayout mMuteLayout;
    private RelativeLayout mKeepLayout;
    private ImageView mIconKeep;
    private TextView mTvKeep;
    private AudioManager audioManager;
    private ImageView hangUpBg;
    private ViewState currentShowStatus;
    private TextView mute;
    private ImageView iconContactBook;
    private TextView contactBook;
    private ImageView iconDialing;
    private TextView dialing;
    private TextView hangupText;
    private boolean mIsCallState;
    private boolean isMicrophoneMute;
    private boolean isAnswerOnPhone = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        bindView(view);
        WheelOperatePhoneManager.getInstance().setOnWheelOperatePhoneListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showViewByState() {

        List<ContactBean> beanList = PhoneStateManager.getInstance(getActivity()).getPhoneStates().getBeanList();

        int grayColor = mContext.getColor(R.color.dark_gray_text);
        mTvFirstTargetName.setTextColor(Color.WHITE);
        mTvSecondTargetName.setTextColor(Color.WHITE);
        mTvFirstTargetState.setTextColor(grayColor);
        mTvSecondTargetState.setTextColor(grayColor);
        hangUpBg.setVisibility(View.GONE);

        BluePhoneState states = PhoneStateManager.getInstance(getActivity()).getPhoneStates();
        State firstTargetState = states.getState(0);
        State secondTargetState = states.getState(1);
       /* //电话接听的时候默认设置为车机接听,防止在ICall,BCall后会默认手机接听问题
        BlueToothPhoneManagerFactory.getInstance().connectAudio();*/
        if (PhoneStateManager.getInstance(getActivity()).isBothCallBusy()) {

            mLayoutOneTarget.setVisibility(View.GONE);
            mLayoutTwoTarget.setVisibility(View.VISIBLE);
            mLayoutSwitch.setVisibility(View.VISIBLE);

            ContactBean firstBean = beanList.get(0);
            if (firstBean != null) {
                mTvFirstTargetName.setText(getString(R.string.unknown_contact).equals(firstBean.getName()) ? firstBean.getPhoneNum() : ContactNameUtils.getLimitedContactName(firstBean.getName()));
            }

            ContactBean secondBean = beanList.get(1);
            if (secondBean != null) {
                mTvSecondTargetName.setText(getString(R.string.unknown_contact).equals(secondBean.getName()) ? secondBean.getPhoneNum() : ContactNameUtils.getLimitedContactName(secondBean.getName()));
            }

            if (firstTargetState == State.KEEP) {
                mTvFirstTargetName.setTextColor(grayColor);
            } else {
                mTvSecondTargetName.setTextColor(grayColor);
            }

            switch (secondTargetState) {
                case ACTIVE:
                    mTvFirstTargetState.setText(getResources().getText(R.string.keep));
                    mTvSecondTargetState.setText(LastTimeFormatUtils.getCallDuration(secondBean));
                    break;
                case KEEP:
                    mTvFirstTargetState.setText(LastTimeFormatUtils.getCallDuration(firstBean));
                    mTvSecondTargetState.setText(getResources().getText(R.string.keep));
                    break;
                case CALL:
                    mTvSecondTargetState.setText(getResources().getText(R.string.dialing));
                    mTvFirstTargetState.setText(getResources().getText(R.string.keep));
                    break;
            }

        } else {
            mLayoutOneTarget.setVisibility(View.VISIBLE);
            mLayoutTwoTarget.setVisibility(View.GONE);
            mLayoutSwitch.setVisibility(View.GONE);

            if (beanList.size() != 0) {
                ContactBean bean = beanList.get(0);
                if (bean != null) {
                    if (!TextUtils.isEmpty(bean.getPhoneNum()))
                        phoneNum.setText(ContactNameUtils.getLimitedPhoneNumber(bean.getPhoneNum()));
                    if (!TextUtils.isEmpty(bean.getName()))
                        name.setText(ContactNameUtils.getLimitedContactName(bean.getName()));
                    OperateUtils.setHeadImage(iconContact, bean);

                    callLastTime.setText(LastTimeFormatUtils.getCallDuration(bean));
                }
            }

//                case ACTIVE:
            hangUpBg.setVisibility(View.GONE);
            currentStatus.setText("");

            boolean isKeep = PhoneStateManager.getInstance(mContext).isKeep();
            mMuteLayout.setVisibility(isKeep ? View.GONE : View.VISIBLE);
            mKeepLayout.setVisibility(isKeep ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    protected void onPhoneDataChang(ContactBean bean, State state) {
        boolean isCallState = PhoneStateManager.getInstance(mContext).isCallState();
        if (isCallState && !mIsCallState) {
            mIsCallState = true;
            isMicrophoneMute = audioManager.isMicrophoneMute();
        }
        if (!isCallState && mIsCallState) {
            mIsCallState = false;
            mutePhone(isMicrophoneMute, false);
            isContactBookSelected(false);
            isDialingSelected(false);
        }

        if (!PhoneStateManager.getInstance(mContext).isActivePageState()) return;
        ContactBean currentActiveBean = PhoneStateManager.getInstance(mContext).getPhoneStates().getCurrentActiveBean();
        if (currentActiveBean != null) {
            mutePhone(currentActiveBean.isMute(), false);
        }
        mMuteLayout.setOnClickListener(isAnswerOnPhone ? null : this);
        if (!PhoneStateManager.getInstance(mContext).isHangUp()) {
            showViewByState();
        }
    }

    public void bindView(View view) {
        view.findViewById(R.id.minimum).setOnClickListener(this);
        currentStatus = view.findViewById(R.id.current_status);
        iconContact = view.findViewById(R.id.icon_contact);
        name = view.findViewById(R.id.name);
        phoneNum = view.findViewById(R.id.phone_num);
        hangUpBg = view.findViewById(R.id.hang_up_bg);
        callLastTime = view.findViewById(R.id.call_last_time);
        mLayoutSwitch = view.findViewById(R.id.layout_icon_switch);
        hangupText = view.findViewById(R.id.hang_up_text);
        mLayoutOneTarget = view.findViewById(R.id.layout_one_target);
        mLayoutTwoTarget = view.findViewById(R.id.layout_two_targets);
        mTvFirstTargetName = view.findViewById(R.id.first_target_name);
        mTvFirstTargetState = view.findViewById(R.id.first_target_state);
        mTvSecondTargetName = view.findViewById(R.id.second_target_name);
        mTvSecondTargetState = view.findViewById(R.id.second_target_state);
        mIvMute = view.findViewById(R.id.icon_mute);
        mute = view.findViewById(R.id.mute);
        iconContactBook = view.findViewById(R.id.icon_contact_book);
        contactBook = view.findViewById(R.id.contact_book);
        mIvAnswer = view.findViewById(R.id.icon_phone);
        mTvAnswerType = view.findViewById(R.id.phone);
        iconDialing = view.findViewById(R.id.icon_dialing);
        dialing = view.findViewById(R.id.dialing);
        mMuteLayout = view.findViewById(R.id.layout_mute);
        mKeepLayout = view.findViewById(R.id.layout_keep);
        mIconKeep = view.findViewById(R.id.icon_keep);
        mTvKeep = view.findViewById(R.id.tv_keep);
        mMuteLayout.setOnClickListener(this);
        mKeepLayout.setOnClickListener(this);
        view.findViewById(R.id.layout_mute).setOnClickListener(this);
        view.findViewById(R.id.layout_contact_book).setOnClickListener(this);
        view.findViewById(R.id.layout_answer_type).setOnClickListener(this);
        view.findViewById(R.id.layout_dialpad).setOnClickListener(this);
        view.findViewById(R.id.hang_up).setOnClickListener(this);
        view.findViewById(R.id.layout_icon_switch).setOnClickListener(this);
        view.findViewById(R.id.layout_first_target).setOnClickListener(this);
        view.findViewById(R.id.layout_second_target).setOnClickListener(this);
    }

    private void isMuteSelected(boolean isSelected) {
        if (isAnswerOnPhone) {
            isSelected = false;
        }
        mIvMute.setSelected(isSelected);
//        mute.setTextColor(isSelected ? getResources().getColor(R.color.call_selected_text_color)
//                : getResources().getColor(R.color.dark_gray_text));
        mute.setTextAppearance(isSelected ? R.style.phoneTextColor_Select
                : R.style.phoneTextColor_noSelect);
    }

    private void isContactBookSelected(boolean isSelected) {
        iconContactBook.setSelected(isSelected);
//        contactBook.setTextColor(isSelected ? getResources().getColor(R.color.call_selected_text_color)
//                : getResources().getColor(R.color.dark_gray_text));
        contactBook.setTextAppearance(isSelected ? R.style.phoneTextColor_Select
                : R.style.phoneTextColor_noSelect);
    }

    private void isPhoneAnswerSelected(boolean isSelected) {
        mIvAnswer.setSelected(isSelected);
//        mTvAnswerType.setTextColor(isSelected ? getResources().getColor(R.color.call_selected_text_color) : getResources().getColor(R.color.dark_gray_text));
        mTvAnswerType.setTextAppearance(isSelected ? R.style.phoneTextColor_Select
                : R.style.phoneTextColor_noSelect);
    }

    private void isDialingSelected(boolean isSelected) {
        iconDialing.setSelected(isSelected);
//        dialing.setTextColor(isSelected ? getResources().getColor(R.color.call_selected_text_color) : getResources().getColor(R.color.dark_gray_text));
        dialing.setTextAppearance(isSelected ? R.style.phoneTextColor_Select
                : R.style.phoneTextColor_noSelect);
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.mute, EventConstants.NormalClick.contactook, EventConstants.NormalClick.answerType, EventConstants.NormalClick.dialpad, EventConstants.NormalClick.hangup, EventConstants.NormalClick.minimum, EventConstants.NormalClick.switchCall, EventConstants.NormalClick.switchCall, EventConstants.NormalClick.switchCall, EventConstants.NormalClick.cancelKeep})
    @ResId({R.id.layout_mute, R.id.layout_contact_book, R.id.layout_answer_type, R.id.layout_dialpad, R.id.hang_up, R.id.minimum, R.id.layout_icon_switch, R.id.layout_first_target, R.id.layout_second_target, R.id.layout_keep})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_mute:
                mutePhone(!audioManager.isMicrophoneMute(), true);
                break;
            case R.id.layout_contact_book:
                openContactBook();
                break;
            case R.id.layout_answer_type:
                answerOnPhone();
                break;
            case R.id.layout_dialpad:
                dialPhone();
                break;
            case R.id.hang_up:
                hangUpPhone();
                break;
            case R.id.minimum:
                minimum();
                break;
            case R.id.layout_icon_switch:
                switchCall();
                break;
            case R.id.layout_first_target:
                if (needSwitch(0)) {
                    switchCall();
                }
                break;
            case R.id.layout_second_target:
                if (needSwitch(1)) {
                    switchCall();
                }
                break;
            case R.id.layout_keep:
                BlueToothPhoneManagerFactory.getInstance().acceptCall();
                break;
        }
    }

    private boolean needSwitch(int index) {
        BluePhoneState states = PhoneStateManager.getInstance(getActivity()).getPhoneStates();
        return states.getState(index) == State.KEEP;
    }

    private void switchCall() {
        BluePhoneState states = PhoneStateManager.getInstance(getActivity()).getPhoneStates();
        State firstTargetState = states.getState(0);
        State secondTargetState = states.getState(1);
        if (!((firstTargetState == State.ACTIVE && secondTargetState == State.KEEP) || (firstTargetState == State.KEEP && secondTargetState == State.ACTIVE))) {
            XMToast.showToast(mContext, mContext.getString(R.string.can_not_switch));
            return;
        }
//        BlueToothPhoneManagerFactory.getInstance().holdCall();
        BlueToothPhoneManagerFactory.getInstance().acceptCall();
        activity.clearDialPad();
    }

    /**
     * 最小化
     */
    private void minimum() {
        RouteUtils.startWindowService(this);
    }

    private void hangUpPhone() {
        if (!PhoneStateManager.getInstance(getActivity()).isBothCallBusy()) {
            ContactBean contactBean = PhoneStateManager.getInstance(mContext).getCurrentActiveBean();
            if (contactBean != null) {
                contactBean.setIsAnswerOnPhone(isAnswerOnPhone);
                sendHangUpBroadcast(contactBean);
            }
            handleHangUpPage();
            hangUpBg.setVisibility(View.VISIBLE);
            currentStatus.setText(R.string.calling_is_out);
        }

        if (PhoneStateManager.getInstance(mContext).isKeep()) {
            BlueToothPhoneManagerFactory.getInstance().rejectCall();
        } else {
            BlueToothPhoneManagerFactory.getInstance().terminateCall();
        }

    }

    /**
     * 拨号键盘
     */
    private void dialPhone() {
        if (currentShowStatus == ViewState.KeyPad) {
            currentShowStatus = null;
            activity.setSecondViewState(null, true);
            isDialingSelected(false);
            isContactBookSelected(false);
            return;
        }
        currentShowStatus = ViewState.KeyPad;
        activity.setSecondViewState(ViewState.KeyPad, true);
        isDialingSelected(true);
        isContactBookSelected(false);
    }

    /**
     * 切换 手机/车机 接听
     */
    private void answerOnPhone() {
        if (BlueToothPhoneManagerFactory.getInstance().connectAudio()) {
//            displayAnswerOnPhoneLayout(true);
            isAnswerOnPhone = false;
        } else {
            BlueToothPhoneManagerFactory.getInstance().disconnectAudio();
//            displayAnswerOnPhoneLayout(false);
            isAnswerOnPhone = true;
        }
    }

    private void displayAnswerOnPhoneLayout(final boolean isAnswerOnCar) {
        isAnswerOnPhone = !isAnswerOnCar;
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                if (!PhoneStateManager.getInstance(mContext).isCallState()) {
                    return;
                }
                if (isAnswerOnCar) {
                    mTvAnswerType.setText(getString(R.string.answer_by_phone));
                    isPhoneAnswerSelected(false);
                    //恢复为车机接听后，静音按钮恢复可用
                    mMuteLayout.setClickable(true);
                    mMuteLayout.setAlpha(1);
                } else {
                    mTvAnswerType.setText(getString(R.string.answer_by_car));
                    isPhoneAnswerSelected(true);
                    //转为手机后，静音按钮不可操作，并且呈现给用户视觉上的效果
                    mMuteLayout.setClickable(false);
                    mMuteLayout.setAlpha((float) 0.4);
                }
            }
        }, 200);
    }

    private void displayAnswerOnPhoneLayout() {
        displayAnswerOnPhoneLayout(BlueToothPhoneManagerFactory.getInstance().getAudioState() == BluetoothHeadsetClient.STATE_AUDIO_CONNECTED);
    }

    /**
     * 通讯录
     */
    private void openContactBook() {
        if (currentShowStatus == ViewState.Contacts) {
            currentShowStatus = null;
            activity.setSecondViewState(null, true);
            isContactBookSelected(false);
            isDialingSelected(false);
            return;
        }
        currentShowStatus = ViewState.Contacts;
        activity.setSecondViewState(ViewState.Contacts, true);
        isContactBookSelected(true);
        isDialingSelected(false);
    }

    /**
     * 静音/取消静音
     */
    private void mutePhone(boolean mute, boolean updateBean) {
        if (audioManager.isMicrophoneMute() == mute) {
            return;
        }
        boolean isMute;
        if (isAnswerOnPhone) {
            isMute = BlueToothPhoneManagerFactory.getInstance().mutePhone();
        } else {
            isMute = mute;
            if (audioManager != null) {
                audioManager.setMicrophoneMute(isMute);
            }
        }
        isMuteSelected(isMute);
        if (updateBean) {
            setBeanMute(mute);
        }
    }

    private void setBeanMute(boolean mute) {
        ContactBean currentActiveBean = PhoneStateManager.getInstance(mContext).getPhoneStates().getCurrentActiveBean();
        if (currentActiveBean == null) return;
        currentActiveBean.setMute(mute);
    }

    @Subscriber(tag = EventBusTags.AUDIO_STATE_CHANGED)
    private void onAudioStateChaned(boolean isConnected) {
        displayAnswerOnPhoneLayout(isConnected);
    }

    @Override
    public void onHfpConnected(BluetoothDevice device) {
        super.onHfpConnected(device);
        displayAnswerOnPhoneLayout();
    }

    @Override
    public void answerPhone(String phoneNum) {

    }

    @Override
    public void hangupPhone(String phoneNum) {
        BluePhoneState phoneStates = PhoneStateManager.getInstance(getContext()).getPhoneStates();
        State firstState = phoneStates.getState(0);
        State secondState = phoneStates.getState(1);
        if (firstState != null) {
            if (firstState == State.INCOMING) {
                return ;
            }
        }

        if (secondState != null) {
            if (secondState == State.INCOMING) {
                return;
            }
        }
        hangUpPhone();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WheelOperatePhoneManager.getInstance().unregisterPhoneListener(this);
    }
}
