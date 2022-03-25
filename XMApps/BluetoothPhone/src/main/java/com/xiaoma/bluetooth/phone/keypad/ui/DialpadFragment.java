package com.xiaoma.bluetooth.phone.keypad.ui;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.constants.EventConstants;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.utils.BluetoothUtils;
import com.xiaoma.bluetooth.phone.common.utils.EditTextUtils;
import com.xiaoma.bluetooth.phone.common.utils.OperateUtils;
import com.xiaoma.bluetooth.phone.main.ui.BaseBluetoothFragment;
import com.xiaoma.model.annotation.Ignore;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.thread.ThreadDispatcher;

import java.lang.reflect.Method;
import java.util.HashMap;

import static com.xiaoma.bluetooth.phone.common.constants.EventConstants.PageDescribe.dialPadFragmentPagePathDesc;

/**
 * Created by qiuboxiang on 2018/12/3 19:02
 */
@PageDescComponent(EventConstants.PageDescribe.dialPadFragmentPagePathDesc)
public class DialpadFragment extends BaseBluetoothFragment implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "DialpadFragment";
    private static final SparseArray<Character> mDialpadButtonMap = new SparseArray<>();
    private static final HashMap<Character, Integer> mToneMap = new HashMap();
    private static final int DTMF_DURATION_MS = 120; // 按键音的播放时长

    static {
        mDialpadButtonMap.put(R.id.one, '1');
        mDialpadButtonMap.put(R.id.two, '2');
        mDialpadButtonMap.put(R.id.three, '3');
        mDialpadButtonMap.put(R.id.four, '4');
        mDialpadButtonMap.put(R.id.five, '5');
        mDialpadButtonMap.put(R.id.six, '6');
        mDialpadButtonMap.put(R.id.seven, '7');
        mDialpadButtonMap.put(R.id.eight, '8');
        mDialpadButtonMap.put(R.id.nine, '9');
        mDialpadButtonMap.put(R.id.zero, '0');
        mDialpadButtonMap.put(R.id.star, '*');
        mDialpadButtonMap.put(R.id.pound, '#');

        mToneMap.put('1', ToneGenerator.TONE_DTMF_1);
        mToneMap.put('2', ToneGenerator.TONE_DTMF_2);
        mToneMap.put('3', ToneGenerator.TONE_DTMF_3);
        mToneMap.put('4', ToneGenerator.TONE_DTMF_4);
        mToneMap.put('5', ToneGenerator.TONE_DTMF_5);
        mToneMap.put('6', ToneGenerator.TONE_DTMF_6);
        mToneMap.put('7', ToneGenerator.TONE_DTMF_7);
        mToneMap.put('8', ToneGenerator.TONE_DTMF_8);
        mToneMap.put('9', ToneGenerator.TONE_DTMF_9);
        mToneMap.put('0', ToneGenerator.TONE_DTMF_0);
        mToneMap.put('*', ToneGenerator.TONE_DTMF_S);
        mToneMap.put('#', ToneGenerator.TONE_DTMF_P);
    }

    private final Object mToneGeneratorLock = new Object();
    private EditText mEtNumber;
    private LinearLayout mDisconnectBluetoothLayout;
    private LinearLayout mDialPadLayout;
    private ToneGenerator mToneGenerator;
    private boolean mDTMFToneEnabled;
    private boolean mIsDTMFMode;
    private ContactBean currentCallObject;
    private AudioManager audioManager;
    private long action_down_time = 0;
    private static final long LONG_PRESS_DURATION = 1000;
    private final View.OnTouchListener mDialpadTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Character digit = mDialpadButtonMap.get(v.getId());
            final int tone = mToneMap.get(digit);
            if (digit == null) {
                return false;
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                action_down_time = System.currentTimeMillis();
                ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
                    @Override
                    public void run() {
                        playTone(tone);
                    }
                });
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                long action_up_time = System.currentTimeMillis();
                int index = mEtNumber.getSelectionStart();
                Editable edit = mEtNumber.getEditableText();
                if (action_up_time - action_down_time >= LONG_PRESS_DURATION
                        && digit == mDialpadButtonMap.get(R.id.zero)) {
                    edit.insert(index, "+");
                    action_down_time = 0;
                    return true;
                }
                edit.insert(index, String.valueOf(digit));
                action_down_time = 0;
                if (mIsDTMFMode) {
                    BlueToothPhoneManagerFactory.getInstance().sendDTMF(digit);
                    currentCallObject = PhoneStateManager.getInstance(mContext).getCurrentActiveBean();
                }
                return true;
            }

            return false;
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        initToneGenerator();
    }

    public void bindView(View view) {
        mEtNumber = view.findViewById(R.id.tv_number);
        mDisconnectBluetoothLayout = view.findViewById(R.id.layout_disconnect_bluetooth);
        mDialPadLayout = view.findViewById(R.id.layout_dialpad);
        handleEditText(mEtNumber);
        EditTextUtils.disableCopyAndPaste(mEtNumber);

        for (int i = 0; i < mDialpadButtonMap.size(); i++) {
            int id = mDialpadButtonMap.keyAt(i);
            View button = view.findViewById(id);
            button.setOnTouchListener(mDialpadTouchListener);
        }

        view.findViewById(R.id.delete).setOnClickListener(this);
        view.findViewById(R.id.to_connect_bluetooth).setOnClickListener(this);
        view.findViewById(R.id.delete).setOnLongClickListener(this);
        view.findViewById(R.id.call).setOnClickListener(this);
        view.findViewById(R.id.call).setEnabled(!mIsDTMFMode);

        displayDisconnectLayout(!BluetoothUtils.isBTConnectDevice());
    }

    private void displayDisconnectLayout(boolean show) {
        mDisconnectBluetoothLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mDialPadLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onHfpConnected(BluetoothDevice device) {
        super.onHfpConnected(device);
        if (isDeviceConnected(device)) {
            displayDisconnectLayout(false);
        }
    }

    @Override
    public void onHfpDisConnected(BluetoothDevice device) {
        super.onHfpDisConnected(device);
//        if (isDeviceDisconnected(device)) {
        displayDisconnectLayout(true);
//        }
    }

    @Override
    public void onA2dpConnected(BluetoothDevice device) {
        super.onA2dpConnected(device);
        /*if (isDeviceConnected(device)) {
            displayDisconnectLayout(false);
        }*/
    }

    @Override
    public void onA2dpDisconnected(BluetoothDevice device) {
        super.onA2dpDisconnected(device);
       /* if (isDeviceDisconnected(device)) {
            displayDisconnectLayout(true);
        }*/
    }

    private void initToneGenerator() {
        //按键声音播放设置及初始化
        try {
            // 获取系统参数“按键操作音”是否开启
            mDTMFToneEnabled = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1;
            synchronized (mToneGeneratorLock) {
                if (mDTMFToneEnabled && mToneGenerator == null) {
                    mToneGenerator = new ToneGenerator(AudioManager.STREAM_DTMF, 80); // 设置声音的大小
//                    int volumeControlStream = activity.getVolumeControlStream();
                    getActivity().setVolumeControlStream(AudioManager.STREAM_DTMF);//TODO
                }
            }
        } catch (Exception e) {
            mDTMFToneEnabled = false;
            mToneGenerator = null;
        }
    }

    @Override
    protected void onPhoneDataChang(ContactBean bean, State state) {
        if (mIsDTMFMode && state == State.IDLE && currentCallObject != null && bean.getPhoneNum().equals(currentCallObject.getPhoneNum())) {
            currentCallObject = null;
            clearDialPad();
        }
    }


    /**
     * 点击出现光标但不弹出软键盘
     *
     * @param editText
     */
    public void handleEditText(EditText editText) {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            editText.setInputType(InputType.TYPE_NULL);
        } else {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setSoftInputShownOnFocus;
                setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setSoftInputShownOnFocus.setAccessible(true);
                setSoftInputShownOnFocus.invoke(editText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 播放按键声音
     */
    private void playTone(int tone) {
        if (!mDTMFToneEnabled) {
            return;
        }
        int ringerMode = audioManager.getRingerMode();
        if (ringerMode == AudioManager.RINGER_MODE_SILENT || ringerMode == AudioManager.RINGER_MODE_VIBRATE) {  // 静音或者震动时不发出声音
            return;
        }
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                return;
            }
            mToneGenerator.startTone(tone, DTMF_DURATION_MS);
        }
    }

    @Override
    @Ignore
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.delete:
                int index = mEtNumber.getSelectionStart();
                Editable editable = mEtNumber.getText();
                if (index > 0) {
                    editable.delete(index - 1, index);
                }
                break;

            case R.id.call:
                call();
                break;

            case R.id.to_connect_bluetooth:
                connectBluetooth();
                break;
        }
    }

    private void call() {
        String phoneNum = mEtNumber.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            phoneNum = BlueToothPhoneManagerFactory.getInstance().getRedialNumber();
            if (!TextUtils.isEmpty(phoneNum)) {
                mEtNumber.setText(phoneNum);
                mEtNumber.requestFocus();
                mEtNumber.setSelection(phoneNum.length());
                return;
            }
        }
        if (TextUtils.isEmpty(phoneNum)) {
            showToast(getString(R.string.phone_number_empty));

        } else if (phoneNum.contains("*") || phoneNum.contains("#")) {
            showToast(getString(R.string.not_phone_num));
        } else {
            XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.call, phoneNum,
                    "DialpadFragment", dialPadFragmentPagePathDesc);
            if (OperateUtils.dial(phoneNum)) {
                ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearDialPad();
                    }
                }, 1000);
            }
        }
    }

    public void clearDialPad() {
        mEtNumber.setText("");
    }

    @Override
    public boolean onLongClick(View v) {
        clearDialPad();
        return true;
    }

    public void setIsDTMFMode(boolean mIsDTMFMode) {
        this.mIsDTMFMode = mIsDTMFMode;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
