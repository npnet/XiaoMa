package com.xiaoma.bluetooth.phone.phone.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.constants.EventConstants;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.manager.WheelOperatePhoneManager;
import com.xiaoma.bluetooth.phone.common.model.BluePhoneState;
import com.xiaoma.bluetooth.phone.common.utils.ContactNameUtils;
import com.xiaoma.bluetooth.phone.common.utils.OperateUtils;
import com.xiaoma.bluetooth.phone.common.utils.RouteUtils;
import com.xiaoma.bluetooth.phone.common.views.CircleCharAvatarView;
import com.xiaoma.bluetooth.phone.main.ui.BaseBluetoothFragment;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;

import java.util.List;

/**
 * 来电中|拨号中
 *
 * @author: iSun
 * @date: 2018/11/16 0016
 */
@PageDescComponent(EventConstants.PageDescribe.dialingFragmentPagePathDesc)
public class DialingFragment extends BaseBluetoothFragment implements View.OnClickListener, WheelOperatePhoneManager.OnWheelOperatePhoneListener {

    private List<ContactBean> beanList;
    private CircleCharAvatarView icon;
    private TextView name;
    private TextView phoneNum;
    private ImageView hangUp;
    private ImageView minimum;
    private TextView currentStatus;
    private ImageView hangUpBg;
    private View keepAndListener;
    private View hangUpAndListener;
    private TextView listenerText;
    private ImageView keepAndListenerIcon;
   /* private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            BlueToothPhoneManagerFactory.getInstance().acceptCall();
        }
    };*/
    private static final int ANSWER = 0;
    private static final int KEEP_AND_ANSWER = 1;
    private static final int HANGUP_AND_ANSWER = 2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initEvent();
        WheelOperatePhoneManager.getInstance().setOnWheelOperatePhoneListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            showViewByState();
        }
    }

    private void setTextViewValue(ContactBean bean) {
        if (bean == null) return;
        if (!TextUtils.isEmpty(bean.getPhoneNum()))
            phoneNum.setText(ContactNameUtils.getLimitedPhoneNumber(bean.getPhoneNum()));
        if (!TextUtils.isEmpty(bean.getName()))
            name.setText(ContactNameUtils.getLimitedContactName(bean.getName()));

        OperateUtils.setHeadImage(icon, bean);
    }

    private void showViewByState() {
        beanList = PhoneStateManager.getInstance(getActivity()).getPhoneStates().getBeanList();
        BluePhoneState states = PhoneStateManager.getInstance(getActivity()).getPhoneStates();
        State state1 = states.getState(0);
        State state2 = states.getState(1);
        if (state2 != State.IDLE) { //INCOMING
            setTextViewValue(beanList.get(1));
            keepAndListener.setVisibility(View.VISIBLE);
            hangUpAndListener.setVisibility(View.VISIBLE);
            listenerText.setText(R.string.keep_and_listen);
            currentStatus.setText(R.string.incoming);
            hangUpBg.setVisibility(View.GONE);
            keepAndListenerIcon.setImageResource(R.drawable.icon_keep_and_answer_unpressed);

//        } else if (state1 == State.INCOMING && state2 == State.ACTIVE) {
//            setTextViewValue(beanList.get(0));
//            keepAndListener.setVisibility(View.VISIBLE);
//            hangUpAndListener.setVisibility(View.VISIBLE);
//            listenerText.setText(R.string.keep_and_listen);
//            currentStatus.setText(R.string.incoming);
//            hangUpBg.setVisibility(View.GONE);
//            keepAndListenerIcon.setImageResource(R.drawable.icon_keep_and_answer_unpressed);
        } else {
            setTextViewValue(beanList.get(0));

            switch (state1) {
                case CALL:
                    keepAndListener.setVisibility(View.GONE);
                    hangUpAndListener.setVisibility(View.GONE);
                    currentStatus.setText(R.string.dialing);
                    hangUpBg.setVisibility(View.GONE);
                    break;

                case INCOMING:
                    listenerText.setText(R.string.listener);
                    keepAndListenerIcon.setImageResource(R.drawable.icon_answer);
                    keepAndListener.setVisibility(View.VISIBLE);
                    hangUpAndListener.setVisibility(View.GONE);
                    currentStatus.setText(R.string.incoming);
                    hangUpBg.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void initEvent() {
        hangUp.setOnClickListener(this);
        minimum.setOnClickListener(this);
        keepAndListener.setOnClickListener(this);
        hangUpAndListener.setOnClickListener(this);
    }

    @Override
    protected void onPhoneDataChang(ContactBean bean, State state) {
        if (!PhoneStateManager.getInstance(mContext).isHangUp() && PhoneStateManager.getInstance(mContext).isDialingPageState()) {
            showViewByState();
        }
    }

    public void bindView(View view) {
        icon = view.findViewById(R.id.icon);
        name = view.findViewById(R.id.name);
        phoneNum = view.findViewById(R.id.phone_num);
        hangUp = view.findViewById(R.id.hang_up);
        minimum = view.findViewById(R.id.minimum);
        currentStatus = view.findViewById(R.id.current_status);
        hangUpBg = view.findViewById(R.id.hang_up_bg);
        keepAndListener = view.findViewById(R.id.keep_and_listen_parent);
        hangUpAndListener = view.findViewById(R.id.hang_up_and_listen_parent);
        keepAndListenerIcon = view.findViewById(R.id.keep_and_listen);
        listenerText = view.findViewById(R.id.listener_text);
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.minimum, EventConstants.NormalClick.hangup, EventConstants.NormalClick.keepAndAnswer, EventConstants.NormalClick.hangupAndAnswer})
    @ResId({R.id.minimum, R.id.hang_up, R.id.keep_and_listen_parent, R.id.hang_up_and_listen_parent})
    public void onClick(View v) {
        if (v == minimum) {
            RouteUtils.startWindowService(this);
        } else if (v == hangUp) {
            hangUpPhone();
        } else if (v == keepAndListener) {
            State firstTargetState = PhoneStateManager.getInstance(getActivity()).getPhoneStates().getState(0);
            if (firstTargetState == State.INCOMING) {
                answer();
            } else {
                keepAndListen();
            }
        } else if (v == hangUpAndListener) {
            hangUpAndListen();
        }
    }

    private void answer() {
//        BlueToothPhoneManagerFactory.getInstance().acceptCall();
        BlueToothPhoneManagerFactory.getInstance().answerCallByNForeApi(ANSWER);
    }

    /**
     * 保持并接听
     */
    private void keepAndListen() {
//        BlueToothPhoneManagerFactory.getInstance().acceptCall();
        BlueToothPhoneManagerFactory.getInstance().answerCallByNForeApi(KEEP_AND_ANSWER);
    }

    /**
     * 挂断并接听
     */
    private void hangUpAndListen() {
//        BlueToothPhoneManagerFactory.getInstance().terminateCall();
//        BlueToothPhoneManagerFactory.getInstance().acceptCall();
//        handler.postDelayed(runnable, 1000);
        BlueToothPhoneManagerFactory.getInstance().answerCallByNForeApi(HANGUP_AND_ANSWER);
    }

    private void hangUpPhone() {
        handleHangUpPage();
        hangUpBg.setVisibility(View.VISIBLE);

        BluePhoneState states = PhoneStateManager.getInstance(getActivity()).getPhoneStates();
        State firstTargetState = states.getState(0);
        State secondTargetState = states.getState(1);
        List<ContactBean> beanList = PhoneStateManager.getInstance(mContext).getPhoneStates().getBeanList();
        if (secondTargetState != State.IDLE) {  //第三方来电
            sendHangUpBroadcast(beanList.get(1));
            currentStatus.setText(R.string.reject_call);
            BlueToothPhoneManagerFactory.getInstance().rejectCall();

        } else {
            sendHangUpBroadcast(beanList.get(0));
            switch (firstTargetState) {
                case CALL:
                    currentStatus.setText(R.string.calling_is_out);
                    BlueToothPhoneManagerFactory.getInstance().terminateCall();
                    break;

                case INCOMING:
                    currentStatus.setText(R.string.reject_call);
                    BlueToothPhoneManagerFactory.getInstance().rejectCall();
                    break;
            }
        }
    }

    @Override
    public void answerPhone(String phoneNum) {
        State firstTargetState = PhoneStateManager.getInstance(getActivity()).getPhoneStates().getState(0);
        if (firstTargetState == State.INCOMING) {
            answer();
        } else {
            keepAndListen();
        }
    }

    @Override
    public void hangupPhone(String phoneNum) {
        Log.d("BluetoothReceiverManager","挂断电话");
        hangUpPhone();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WheelOperatePhoneManager.getInstance().unregisterPhoneListener(this);
    }
}
