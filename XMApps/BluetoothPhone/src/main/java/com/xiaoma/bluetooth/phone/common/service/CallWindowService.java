package com.xiaoma.bluetooth.phone.common.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.constants.BluetoothPhoneConstants;
import com.xiaoma.bluetooth.phone.common.listener.BluetoothStateListener;
import com.xiaoma.bluetooth.phone.common.listener.PhoneStateChangeListener;
import com.xiaoma.bluetooth.phone.common.manager.BluetoothStateManager;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.model.BluePhoneState;
import com.xiaoma.bluetooth.phone.common.utils.ContactNameUtils;
import com.xiaoma.bluetooth.phone.common.utils.LastTimeFormatUtils;
import com.xiaoma.bluetooth.phone.common.utils.OperateUtils;
import com.xiaoma.bluetooth.phone.common.views.CircleCharAvatarView;
import com.xiaoma.bluetooth.phone.main.ui.MainActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by qiuboxiang on 2018/12/3 20:16
 */
public class CallWindowService extends Service implements PhoneStateChangeListener, BluetoothStateListener {

    private static final String TAG = "CallWindowService";
    private RelativeLayout toucherLayout;
    private WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private Context mContext;
    private boolean viewAdded;
    private Timer mTimer;
    private TimerTask dismissWindowTask = new TimerTask() {
        public void run() {
            dismissWindow();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mContext = this;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PhoneStateManager.getInstance(mContext).addPhoneListener(CallWindowService.this);
        BluetoothStateManager.getInstance(mContext).addListener(this);
        mTimer = new Timer();
        createFloatWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createFloatWindow() {
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;

        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 0;
        params.y = 0;

        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        toucherLayout = (RelativeLayout) inflater.inflate(R.layout.layout_call_state_window, null);
        toucherLayout.setSystemUiVisibility(View.VISIBLE);
        windowManager.addView(toucherLayout, params);
        viewAdded = true;

        toucherLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        toucherLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*params.x = (int) event.getRawX() - toucherLayout.getMeasuredWidth();
                params.y = (int) event.getRawY() - toucherLayout.getMeasuredHeight();
                windowManager.updateViewLayout(toucherLayout,params);*/
                dismissWindow();
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                return true;
            }
        });

        showViewByState(true);
    }

    private void dismissWindow() {
        PhoneStateManager.getInstance(mContext).setWindowMode(false);
        stopSelf();
    }

    private void showViewByState(boolean firstEnter) {

        TextView mTvName = toucherLayout.findViewById(R.id.tv_name);
        TextView mTvNumber = toucherLayout.findViewById(R.id.tv_number);
        TextView mTvState = toucherLayout.findViewById(R.id.tv_state);
        CircleCharAvatarView mIvHead = toucherLayout.findViewById(R.id.head);

        BluePhoneState mBluePhoneState = PhoneStateManager.getInstance(mContext).getPhoneStates();
        List<ContactBean> beanList = mBluePhoneState.getBeanList();

        State state;
        ContactBean bean;
        State firstState = mBluePhoneState.getState(0);
        State secondState = mBluePhoneState.getState(1);
        if (secondState == State.ACTIVE || secondState == State.INCOMING || secondState == State.CALL) {
            state = secondState;
            bean = beanList.get(1);
        } else {
            state = firstState;
            bean = beanList.get(0);
        }

        if (bean != null) {
            mTvName.setText(ContactNameUtils.getLimitedContactName(bean.getName()));
            mTvNumber.setText(ContactNameUtils.getLimitedPhoneNumber(bean.getPhoneNum()));
        }
        OperateUtils.setHeadImage(mIvHead, bean);
        mTvName.setTextColor(Color.WHITE);
        mIvHead.setColorFilter(null);

        switch (state) {
            case IDLE:
                mTvName.setTextColor(Color.parseColor("#6b7f98"));
                mIvHead.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#4D03121A"), PorterDuff.Mode.SRC_ATOP)); //头像置灰
                try {
                    mTimer.schedule(dismissWindowTask, BluetoothPhoneConstants.HANGUP_PAGE_DISMISS_DURATION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case INCOMING:
                mTvState.setText(getResources().getString(R.string.incoming_window));
                break;
            case CALL:
                mTvState.setText(getResources().getString(R.string.dialing_window));
                break;
            case KEEP:
                mTvState.setText(getResources().getString(R.string.keeping));
                break;
            case ACTIVE:
                mTvState.setText(LastTimeFormatUtils.getCallDuration(bean));
                // 如果一开始就是通话中状态,需要开始通话计时
                if (firstEnter) {
                    long time = 0;
                    String num = "";
                    ContactBean currentActiveBean = PhoneStateManager.getInstance(mContext).getPhoneStates().getCurrentActiveBean();
                    if (currentActiveBean != null) {
                        time = currentActiveBean.getElapsedTime();
                        num = currentActiveBean.getPhoneNum();
                    }
                    Log.i(TAG, String.format("getFragmentByPhoneState -> time: %s, num: %s, currActBean: %s",
                            time, num, currentActiveBean != null ? currentActiveBean.getName() : "null"));
                    PhoneStateManager.getInstance(mContext).getPhoneStates().beginTimeKeeping(time, num);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (viewAdded) {
            windowManager.removeView(toucherLayout);
            viewAdded = false;
        }
        PhoneStateManager.getInstance(this).removePhoneListener(this);
        BluetoothStateManager.getInstance(mContext).removeListener(this);
        super.onDestroy();
    }

    @Override
    public void onPhoneStateChange(ContactBean bean, State state) {
        showViewByState(false);
    }

    @Override
    public void onBlueToothConnected() {

    }

    @Override
    public void onHfpConnected(BluetoothDevice device) {

    }

    @Override
    public void onBlueToothDisConnected(BluetoothDevice device) {

    }

    @Override
    public void onBlueToothDisabled() {

    }

    @Override
    public void onHfpDisConnected(BluetoothDevice device) {

    }

    @Override
    public void onPbapConnected() {

    }

    @Override
    public void onPbapDisconnected() {

    }

    @Override
    public void onA2dpConnected(BluetoothDevice device) {

    }

    @Override
    public void onA2dpDisconnected(BluetoothDevice device) {

    }
}
