package com.xiaoma.bluetooth.phone.main.ui;

import android.annotation.Nullable;
import android.app.Service;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.CommonInterface.PullContactbookCallback;
import com.xiaoma.bluetooth.phone.common.constants.EventBusTags;
import com.xiaoma.bluetooth.phone.common.constants.EventConstants;
import com.xiaoma.bluetooth.phone.common.constants.ViewState;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.listener.BluetoothStateListener;
import com.xiaoma.bluetooth.phone.common.listener.HomeListener;
import com.xiaoma.bluetooth.phone.common.listener.PhoneStateChangeListener;
import com.xiaoma.bluetooth.phone.common.listener.PullPhoneBookResultCallback;
import com.xiaoma.bluetooth.phone.common.manager.BluetoothStateManager;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.model.BluePhoneState;
import com.xiaoma.bluetooth.phone.common.utils.BluetoothAdapterUtils;
import com.xiaoma.bluetooth.phone.common.utils.BluetoothUtils;
import com.xiaoma.bluetooth.phone.common.utils.OperateUtils;
import com.xiaoma.bluetooth.phone.common.utils.RouteUtils;
import com.xiaoma.bluetooth.phone.common.views.ErrorDialog;
import com.xiaoma.bluetooth.phone.contacts.ui.ContactAndCollectionFragment;
import com.xiaoma.bluetooth.phone.history.ui.HistoryFragment;
import com.xiaoma.bluetooth.phone.keypad.ui.DialpadFragment;
import com.xiaoma.bluetooth.phone.main.service_bt.PhoneBookService;
import com.xiaoma.bluetooth.phone.main.vm.RefreshContactVM;
import com.xiaoma.bluetooth.phone.phone.ui.DialingFragment;
import com.xiaoma.bluetooth.phone.phone.ui.PhoneFragment;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.dispatch.annotation.Command;
import com.xiaoma.vr.tts.EventTtsManager;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import skin.support.widget.SkinCompatSupportable;

import static com.xiaoma.bluetooth.phone.common.constants.ViewState.Collection;
import static com.xiaoma.bluetooth.phone.common.constants.ViewState.Contacts;
import static com.xiaoma.bluetooth.phone.common.constants.ViewState.History;
import static com.xiaoma.bluetooth.phone.common.constants.ViewState.KeyPad;

@PageDescComponent(EventConstants.PageDescribe.bluetoothPhonePagePathDesc)
public class MainActivity extends BaseActivity implements HomeListener.KeyFun, PhoneStateChangeListener, View.OnClickListener,
        BluetoothStateListener, RefreshContactVM.PullDataCallback, PullContactbookCallback, PhoneBookService.InPullPhoneBookCallback, SkinCompatSupportable {

    private static final String TAG = "MainActivity";
    public static boolean isFirst = false;
    public static boolean isInited = false;
    private static boolean isPause = false;
    private RecyclerView menuGroup;
    private FrameLayout left;
    private FrameLayout right;
    private ImageView mIvRefresh;
    private List<Fragment> fragments = new ArrayList<>();
    private List<Fragment> rightPageFragments = new ArrayList<>();
    private Map<ViewState, Fragment> rightPageMap = new HashMap<>();
    private ViewState[] mViewState = new ViewState[2];
    private FragmentManager fManager;
    private HomeListener homeListener;
    private Context mContext;
    private RefreshContactVM refreshContactVM;
    private DialogManager dialogManager;
    private View refreshAndCloseParent;
    private boolean mInitedWindowPermission;
    private Handler handler = new Handler();
    private boolean canRefreshClickable = true;
    private Animation rotateAnim;
    private DialpadFragment dtmfModeDialpadFragment;
    private int[] menuItems;
    private MenuAdapter menuAdapter;
    private int curSelectedPosition = 0;
    //    private PullPhoneBookResultCallback callback;
    private boolean isShowRefreshDailog = true;
    private int[] checkRecordWord = new int[]{R.string.ok, R.string.please_check, R.string.opend};
    private String hfpAddress;
    private String a2dpAddress;
    private PhoneBookService phoneBookService;

    public static boolean isPause() {
        return isPause;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            phoneBookService = ((PhoneBookService.PhoneBookServiceBinder) service).getService();
            phoneBookService.setPullResultCallback(MainActivity.this);
            phoneBookService.setInPullPhoneBookCallback(MainActivity.this);
            initData();
            Log.d("phoneBook", "service connected, shouldShow: " + phoneBookService.isInDownloading());
            if (phoneBookService.isInDownloading()) {
                dialogManager.showInRefreshDialog();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getRootLayout().setBackground(null);
        Log.d(TAG, "onCreate: ");
        isInited = true;
        mContext = this;
        setContentView(R.layout.activity_main);
//        BlueToothPhoneManagerFactory.getInstance().setPullResultCallback(this);
        dialogManager = new DialogManager(this);
        bindPhoneBookService();
        initRefreshVM();
        initViewState();
        bindView();
        initMenuAdapter();
        requestFloatWindowPermission();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //防止activity 被销毁,fragment重建导致重叠
        outState.putParcelable("android:support:fragments", null);
    }


    private void bindPhoneBookService() {
        Intent intent = new Intent(this, PhoneBookService.class);
        bindService(intent, connection, Service.BIND_AUTO_CREATE);
    }

    private void initData() {
        if (phoneBookService != null) {
            List<ContactBean> callLogs = phoneBookService.getCallLogs();
            List<ContactBean> contactBeans = phoneBookService.getContactBean();
           /* if (callLogs.isEmpty() && contactBeans.isEmpty()) {
                Log.d("phoneBook","both list is empty");
                return;
            }*/
            uploadContact(contactBeans);
            uploadCallHistory(callLogs);
        }
    }

    private void initMenuAdapter() {
        menuAdapter = new MenuAdapter(this);
        menuGroup.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        menuGroup.setAdapter(menuAdapter);
        menuAdapter.setOnItemClickedListener(new MenuAdapter.ItemClickedListener() {

            @Override
            public void onItemClickedListener(int position) {
                if (position != -1) {
                    curSelectedPosition = position;
                }
                showPageForState();
            }
        });
    }

    /*private boolean checkIfPullContact(boolean isShowRefreshDailog) {
        if (!BluetoothUtils.isBluetoothEnabled()) return false;
        BluetoothDevice connectedDevice = BluetoothUtils.getConnectedDevice();
        if (connectedDevice == null) return false;
//        refreshContactVM.refreshContact(connectedDevice);
        refreshContact(isShowRefreshDailog, null);
        return true;
    }*/

    @Override
    protected boolean showStatusBarDivider() {
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void initRefreshVM() {
        refreshContactVM = ViewModelProviders.of(this).get(RefreshContactVM.class);
       /* refreshContactVM.setOnPullDataCallback(this);
        refreshContactVM.getContact().observe(this, new Observer<List<ContactBean>>() {
            @Override
            public void onChanged(@Nullable List<ContactBean> contactBeans) {
                EventBus.getDefault().post(contactBeans, EventBusTags.CONTACT_LIST_REFRESH);
                PhoneStateManager.getInstance(MainActivity.this).setContactList(contactBeans);
                OperateUtils.upLoadContact(contactBeans);
            }
        });

        refreshContactVM.getPhoneCallHistory().observe(this, new Observer<List<ContactBean>>() {
            @Override
            public void onChanged(@Nullable List<ContactBean> callHistoryBeans) {
                EventBus.getDefault().post(callHistoryBeans, EventBusTags.CALL_HISTORY_LIST_REFRESH);
                PhoneStateManager.getInstance(MainActivity.this).setCallHistory(callHistoryBeans);
            }
        });*/

        refreshContactVM.getMenu(this).observe(this, new Observer<int[]>() {
            @Override
            public void onChanged(@Nullable int[] items) {
                menuItems = items;
                menuAdapter.setData(items);
                menuAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initViewState() {
        mViewState[0] = History;
    }

    @Override
    protected void onResume() {
        isPause = false;
        super.onResume();
        initData();
        if (phoneBookService != null) {
            Log.d("phoneBook", "onResume, should show: " + phoneBookService.isInDownloading());
        }
        if (phoneBookService != null) {
            if (phoneBookService.isInDownloading()) {
                dialogManager.showInRefreshDialog();
            } else {
                if (phoneBookService.getContactBean().isEmpty() || phoneBookService.getCallLogs().isEmpty()) {
                    Log.d("phoneBook", "Phonebook service get null , activity refresh ");
                    refreshContact(true, null);
                }
            }
        }
        AppUpdateCheck.getInstance().checkAppUpdate(getPackageName(), getApplication());
        if (PhoneStateManager.getInstance(this).isWindowMode()) {
            RouteUtils.stopWindowService(this);
        }
        if (PhoneStateManager.getInstance(this).isCallState()) {
            Log.d("MainActivity", "dismiss page");
            menuGroup.setVisibility(View.GONE);
            refreshAndCloseParent.setVisibility(View.GONE);
        } else {
            Log.d("MainActivity", "dismiss page");
            menuGroup.setVisibility(View.VISIBLE);
            right.setVisibility(View.GONE);
            refreshAndCloseParent.setVisibility(View.VISIBLE);
//            if (isPause) {
//                moveTaskToBack(false);
//            }
        }
    }

    public void setSecondViewState(ViewState viewState, boolean updateUI) {
        mViewState[1] = viewState;
        if (updateUI) {
            showSecondPageForState();
        }
    }

    public ViewState[] getViewState() {
        return mViewState;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onPause() {
        isPause = true;
        super.onPause();
        PhoneStateManager.getInstance(this).resetCallPullActivity();
    }

    private void bindView() {
        menuGroup = findViewById(R.id.group);
        menuGroup.setVisibility(View.VISIBLE);
        refreshAndCloseParent = findViewById(R.id.refresh_and_close_parent);
        left = findViewById(R.id.fl_left);
        right = findViewById(R.id.fl_right);
        mIvRefresh = findViewById(R.id.refresh);
        mIvRefresh.setOnClickListener(this);
        findViewById(R.id.close).setOnClickListener(this);

        rotateAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        rotateAnim.setInterpolator(linearInterpolator);

        fManager = getSupportFragmentManager();

        createViews();
        initEvent();
    }

    private void createViews() {
        fragments.add(new HistoryFragment());//通话记录
        fragments.add(new DialpadFragment());//拨号键盘
        fragments.add(new ContactAndCollectionFragment());//联系人及收藏
        fragments.add(new PhoneFragment());//通话页(通话中|挂断中等）
        fragments.add(new DialingFragment());//来电中|拨号中

        for (Fragment fragment : fragments) {
            FragmentUtils.add(fManager, fragment, R.id.fl_left, true);
        }
        FragmentUtils.show(getFragmentByPhoneState());

        dtmfModeDialpadFragment = new DialpadFragment();
        dtmfModeDialpadFragment.setIsDTMFMode(true);
        ContactAndCollectionFragment contactAndCollectionFragment = new ContactAndCollectionFragment();
        contactAndCollectionFragment.setRightPage(true);
        rightPageFragments.add(dtmfModeDialpadFragment);//拨号键盘
        rightPageFragments.add(contactAndCollectionFragment);//联系人及收藏夹
        rightPageMap.put(KeyPad, dtmfModeDialpadFragment);
        rightPageMap.put(Contacts, contactAndCollectionFragment);
        rightPageMap.put(Collection, contactAndCollectionFragment);
        for (Fragment fragment : rightPageFragments) {
            FragmentUtils.add(fManager, fragment, R.id.fl_right, true);
        }
    }

    public void clearDialPad() {
        dtmfModeDialpadFragment.clearDialPad();
    }

    @Override
    public void onBackPressed() {
        if (mViewState[1] != null) {
            setSecondViewState(null, true);
        } else {
            quitActivity();
        }
    }

    private void quitActivity() {
        if (PhoneStateManager.getInstance(this).isCallState()) {
            RouteUtils.startWindowService(this);
        } else {
            moveTaskToBack(false);
        }
    }

    private void initEvent() {
        homeListener = new HomeListener(this).setInterface(this).startListen();
        PhoneStateManager.getInstance(this).addPhoneListener(this);
        BluetoothStateManager.getInstance(this).addListener(this);
    }

    private void showPageForState() {

        showSecondPageForState();

        Fragment fragment;
        if (PhoneStateManager.getInstance(this).isCallState()) {
            dialogManager.dismissInRefreshDialog();
            fragment = getFragmentByPhoneState();
        } else {
            fragment = getFragmentByPosition(curSelectedPosition);
            setSecondViewState(null, false);
        }
        if (fragment != null) {
            KLog.d("Phone State", "showPageForState" + fragment.getClass().getSimpleName());
            FragmentUtils.showHide(fragment, fragments);
        }
    }

    private void showSecondPageForState() {
        if (PhoneStateManager.getInstance(this).isCallState() && mViewState[1] != null) {
            final Fragment fragment = rightPageMap.get(mViewState[1]);
            FragmentUtils.showHide(fragment, rightPageFragments);
            KLog.d("Phone State", "showSecondPageForState" + fragment.getClass().getSimpleName());
            if (right.getVisibility() == View.GONE) {
                right.setVisibility(View.VISIBLE);
            }

        } else {
            if (right.getVisibility() != View.GONE){
                right.setVisibility(View.GONE);
                Fragment topShowFragment = FragmentUtils.getTopShow(getSupportFragmentManager());
                FragmentUtils.hide(topShowFragment);
            }
        }
    }

    private Fragment getFragmentByPosition(int position) {
        switch (position) {
            case 0:
                mViewState[0] = ViewState.History;
                break;
            case 1:
                mViewState[0] = ViewState.KeyPad;
                break;
            case 2:
                mViewState[0] = ViewState.Contacts;
                break;
        }
        return fragments.get(position);
    }

    public void showPageForPhone() {
        showPageForState();
        if (PhoneStateManager.getInstance(this).isCallState()) {
            menuGroup.setVisibility(View.GONE);
            refreshAndCloseParent.setVisibility(View.GONE);
        } else {
            menuGroup.setVisibility(View.VISIBLE);
            right.setVisibility(View.GONE);
            refreshAndCloseParent.setVisibility(View.VISIBLE);
//            if (isPause) {
//                moveTaskToBack(false);
//            }
        }
    }

    private Fragment getPrePageView() {
        Fragment fragment;
        switch (mViewState[0]) {
            case History:
                fragment = fragments.get(0);
                break;
            case KeyPad:
                fragment = fragments.get(1);
                break;
            case Contacts:
                fragment = fragments.get(2);
                break;
            default:
                fragment = fragments.get(0);
        }
        menuGroup.setVisibility(View.VISIBLE);
        refreshAndCloseParent.setVisibility(View.VISIBLE);
        return fragment;
    }

    private Fragment getFragmentByPhoneState() {
        Fragment fragment = null;
        BluePhoneState bluePhoneState = PhoneStateManager.getInstance(this).getPhoneStates();
        if (bluePhoneState == null || !PhoneStateManager.getInstance(this).isCallState()) {
            return getPrePageView();
        } else {
            if (PhoneStateManager.getInstance(mContext).isDialingPageState()) {
                fragment = fragments.get(4);
            } else if (PhoneStateManager.getInstance(mContext).isActivePageState()) {
                fragment = fragments.get(3);

                BluePhoneState phoneStates = PhoneStateManager.getInstance(mContext).getPhoneStates();
                ContactBean currentActiveBean = phoneStates.getCurrentActiveBean();
                if (currentActiveBean != null) {
                    int index = phoneStates.getIndex(currentActiveBean.getPhoneNum());
                    State state = phoneStates.getState(index);
                    if (state == State.ACTIVE) {
                        long time = currentActiveBean.getElapsedTime();
                        String num = currentActiveBean.getPhoneNum();
                        Log.i(TAG, String.format("getFragmentByPhoneState -> time: %s, num: %s, currActBean: %s",
                                time, num, currentActiveBean.getName()));
                        phoneStates.beginTimeKeeping(time, num);
                    }
                }
            }
            return fragment;
        }

    }

    @Override
    public void onHome() {
        if (!isPause) {
            quitActivity();
        }
    }

    @Override
    public void onRecent() {
        //最近任务
    }

    @Override
    public void onLongHome() {
        //长按Home
    }

    @Override
    public void onPhoneStateChange(ContactBean bean, State state) {
        KLog.d("Phone State", "main activity receive phone change" + state);
        if (!PhoneStateManager.getInstance(mContext).isHangUp()) {
            KLog.d("Phone State", "start show page" + state);
            showPageForPhone();
        }
        if (state == State.IDLE && BluetoothUtils.isBTConnectDevice()) {
            /*int perStateValue = PhoneStateManager.getInstance(mContext).getBeforeState().getValue();
            // 来电点击挂断,页面消失;去电手机接听挂断,页面消失;车机接听挂断,页面才跳转.
            if (perStateValue == State.INCOMING.getValue()) {
                moveTaskToBack(false);
            } else if (perStateValue == State.CALL.getValue()) {
                if (bean != null && bean.getIsAnswerOnPhone()) {
                    moveTaskToBack(false);
                }
            }*/
            BluePhoneState phoneStates = PhoneStateManager.getInstance(this).getPhoneStates();
            if (PhoneStateManager.getInstance(this).isCallPullActivity) {
//                if (phoneStates.getState(1) == null || phoneStates.getState(1).equals(State.IDLE))
//                moveTaskToBack(false);
                if (shouldMoveTaskToBack(phoneStates)) { // state1 state2 同时为null或者空闲才会让页面消失
                    moveTaskToBack(false);
                }
            }

            // 每次电话挂断的时候,自动刷新通话记录
//            Log.d("QBX", "onPhoneStateChange: " + PhoneStateManager.getInstance(mContext).getBeforeState().getValue());
            BlueToothPhoneManagerFactory.PhoneType type = null;
           /* if (perStateValue == State.INCOMING.getValue()) {
                type = BlueToothPhoneManagerFactory.PhoneType.RECEIVER;
            } else if (perStateValue == State.CALL.getValue()) {
                type = BlueToothPhoneManagerFactory.PhoneType.DIAL;
            }*/
//            final BlueToothPhoneManagerFactory.PhoneType finalType = type;
//            if (type != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshContact(false, BlueToothPhoneManagerFactory.PhoneType.History);
                    Log.d("PhoneBook", "call finish Pull");
                }
            }, 2000);

//            }
        }
    }

    /**
     * 获取页面是否应该回到后台
     *
     * @param phoneStates
     * @return true :state one  and state two both null or idle
     */
    private boolean shouldMoveTaskToBack(BluePhoneState phoneStates) {
        boolean isStateOneIdleOrNull = phoneStates.getState(0) == null || phoneStates.getState(0).equals(State.IDLE);
        boolean isStateTwoIdleOrNull = phoneStates.getState(1) == null || phoneStates.getState(1).equals(State.IDLE);
        return isStateOneIdleOrNull && isStateTwoIdleOrNull;
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.refresh, EventConstants.NormalClick.close})
    @ResId({R.id.refresh, R.id.close})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh:
                refreshContact(true, null);
                Log.d("PhoneBook", "refresh Pull");
                break;
            case R.id.close:
                quitActivity();
                break;
        }
    }

    private void refreshContact(boolean isShowDialog, BlueToothPhoneManagerFactory.PhoneType type) {
//        if (!canRefreshClickable) return;
        canRefreshClickable = false;
        BluetoothDevice connectedDevice = BluetoothUtils.getConnectedDevice();
        if (!BlueToothPhoneManagerFactory.getInstance().isHfpDisconnected()) {
            if (isShowDialog) {
                mIvRefresh.clearAnimation();
                mIvRefresh.startAnimation(rotateAnim);
                dialogManager.showInRefreshDialog();
            }
            isShowRefreshDailog = isShowDialog;
//            refreshContactVM.refreshContact(connectedDevice);
            /*if (type != null) {
                BlueToothPhoneManagerFactory.getInstance().downloadByType(type);
            } else {
                BlueToothPhoneManagerFactory.getInstance().downloadAll();
            }*/
            if (phoneBookService != null) {
                phoneBookService.pullPhoneBookLocal(type);
            }
        } else {
            canRefreshClickable = true;
            XMToast.showToast(this, R.string.bluetooth_disconnected);
        }
    }

    private void requestFloatWindowPermission() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                RouteUtils.startWindowService(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RouteUtils.REQUEST_FLOAT_WINDOW_PERMISSION_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                XMToast.showToast(this, R.string.system_alert_window_permission_reject);
            } else {
                if (!mInitedWindowPermission) {
                    mInitedWindowPermission = true;
                } else {
                    RouteUtils.startWindowServiceDelayed(MainActivity.this, handler);
                }
            }
        }
    }

    @Override
    public void onBlueToothConnected() {
       /* BluetoothDevice connectedDevice = BluetoothUtils.getConnectedDevice();
        if (connectedDevice != null) {
            refreshContactVM.refreshContact(connectedDevice);
        }*/
    }

    @Override
    public void onHfpConnected(BluetoothDevice device) {
        if (isBothProfileConnected()) {
            isShowRefreshDailog = true;
        }
    }

    @Override
    public void onBlueToothDisConnected(BluetoothDevice device) {
        refreshContactVM.cleanData();
        PhoneStateManager.getInstance(this).setPullPhoneBookState(false);
    }

    @Override
    public void onBlueToothDisabled() {
        refreshContactVM.cleanData();
        PhoneStateManager.getInstance(this).setPullPhoneBookState(false);
    }

    @Override
    public void onHfpDisConnected(BluetoothDevice device) {
//        XMToast.showToast(mContext, R.string.bluetooth_disconnect);

        refreshContactVM.cleanData();
        PhoneStateManager.getInstance(this).setPullPhoneBookState(false);
        hfpAddress = null;
        BlueToothPhoneManagerFactory.getInstance().cleanTask();
    }

    @Override
    public void onPbapConnected() {
       /* BluetoothDevice connectedDevice = BluetoothUtils.getConnectedDevice();
        if (connectedDevice == null) return;
        mIvRefresh.startAnimation(rotateAnim);
        refreshContactVM.refreshContact(connectedDevice);
        dialogManager.showInRefreshDialog();*/
    }

    @Override
    public void onPbapDisconnected() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        refreshContactVM.getHandler().removeCallbacksAndMessages(null);
        BluetoothStateManager.getInstance(this).removeListener(this);
        homeListener.stopListen();
        unbindService(connection);
        FragmentUtils.removeAll(fManager);
    }

    @Override
    public void pullDataSuccess() {
        mIvRefresh.clearAnimation();
        dialogManager.dismissInRefreshDialog();
        if (isShowRefreshDailog) {
            dialogManager.showRefreshSuccessDialog();
        }
        canRefreshClickable = true;
        PhoneStateManager.getInstance(this).setPullPhoneBookState(true);
    }

    @Override
    public void pullDataError() {
        mIvRefresh.clearAnimation();
        dialogManager.dismissInRefreshDialog();
        dialogManager.showFreshFailedDialog();
        canRefreshClickable = true;
        refreshContactVM.cleanData();
        PhoneStateManager.getInstance(this).setPullPhoneBookState(false);
    }


    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.BluetoothPhone.CALL_RECORDS:
                EventTtsManager.getInstance().startSpeaking(mContext.getString(getCheckRecordWord()));
                menuAdapter.autoClick(menuGroup, 0);
                return true;
            case NodeConst.BluetoothPhone.MISSED_CALL:
//                if (BlueToothPhoneManagerFactory.getInstance().missCallNum() == 0) {
//                    EventTtsManager.getInstance()
//                            .startSpeaking(mContext.getString(R.string.missed_not_call));
//                } else {
//                    EventTtsManager.getInstance()
//                            .startSpeaking(StringUtil.format(mContext.getString(R.string.missed_call)
//                                    , BlueToothPhoneManagerFactory.getInstance().missCallNum()));
//                }

                menuAdapter.autoClick(menuGroup, 0);
                return true;
            case NodeConst.BluetoothPhone.CONTACT_AND_COLLECTION:
                menuAdapter.autoClick(menuGroup, 2);
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.BluetoothPhone.MAIN_ACTIVITY;
    }

    private int getCheckRecordWord() {
        int index = new Random().nextInt(checkRecordWord.length);
        return checkRecordWord[index];
    }

    @Command("(打开|查看)?(收藏夹|已收藏的电话|收藏)")
    public void checkCollection() {
        if (!BluetoothUtils.isBTConnectDevice()) {
            NodeUtils.jumpTo(mContext, CenterConstants.SETTING, "com.xiaoma.setting.main.ui.MainActivity", NodeConst.Setting.ASSISTANT_ACTIVITY + "/" + NodeConst.Setting.BLUETOOTH_CONNECT_FRAGMENT);
            return;
        }
        if (!BlueToothPhoneManagerFactory.getInstance().isContactBookSynchronized()) {
            EventTtsManager.getInstance().startSpeaking(mContext.getString(R.string.sychronize));
            BlueToothPhoneManagerFactory.getInstance().synchronizeContactBook(new PullPhoneBookResultCallback() {
                @Override
                public void onResult(boolean isSuccess) {
                    if (isSuccess) {
                        EventTtsManager.getInstance().startSpeaking(mContext.getString(R.string.synchronization_complete));
                    }
                }
            });
            return;
        }
        NodeUtils.jumpTo(mContext, CenterConstants.BLUETOOTH_PHONE, "com.xiaoma.bluetooth.phone.main.ui.MainActivity", NodeConst.BluetoothPhone.MAIN_ACTIVITY + "/" + NodeConst.BluetoothPhone.CONTACT_AND_COLLECTION + "/" + NodeConst.BluetoothPhone.COLLECTION);
    }

    @Override
    protected boolean isAppNeedShowNaviBar() {
        return false;
    }

    @Override
    public void onA2dpConnected(BluetoothDevice device) {
        if (isBothProfileConnected()) {
            isShowRefreshDailog = true;
        }
    }

    @Override
    public void onA2dpDisconnected(BluetoothDevice device) {
        a2dpAddress = null;
    }

    @Override
    public void onPullContactFinished(List<ContactBean> list) {
        canRefreshClickable = true;
        uploadContact(list);
    }

    private void uploadContact(List<ContactBean> list) {
        EventBus.getDefault().post(list, EventBusTags.CONTACT_LIST_REFRESH);
        PhoneStateManager.getInstance(MainActivity.this).setContactList(list);
        OperateUtils.upLoadContact(list);
    }

    @Override
    public void onPullMissCallLog(List<ContactBean> list) {

    }

    @Override
    public void onPullReceivedCallLog(List<ContactBean> list) {

    }

    @Override
    public void onPullDialedCallLog(List<ContactBean> list) {

    }

    @Override
    public void onPullCalllog(List<ContactBean> list) {

    }

    @Override
    public void onPullCallHistory(List<ContactBean> list) {
        canRefreshClickable = true;
        EventBus.getDefault().post(list, EventBusTags.CALL_HISTORY_LIST_REFRESH);
        PhoneStateManager.getInstance(MainActivity.this).setCallHistory(list);
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                mIvRefresh.clearAnimation();
                dialogManager.dismissInRefreshDialog();
                if (isShowRefreshDailog) {
                    dialogManager.showRefreshSuccessDialog();
                }
                canRefreshClickable = true;
            }
        });
        PhoneStateManager.getInstance(this).setPullPhoneBookState(true);
    }

    private void uploadCallHistory(List<ContactBean> list) {
        EventBus.getDefault().post(list, EventBusTags.CALL_HISTORY_LIST_REFRESH);
        PhoneStateManager.getInstance(MainActivity.this).setCallHistory(list);
        PhoneStateManager.getInstance(this).setPullPhoneBookState(true);
    }

    @Override
    public void onPullFailed() {
        canRefreshClickable = true;
        mIvRefresh.clearAnimation();
        dialogManager.dismissInRefreshDialog();
        showErrorDialogInMainThread(R.string.sync_failed);
        noticePullFailed(false);
    }

    @Override
    public void onUnauthoried() {
        canRefreshClickable = true;
        mIvRefresh.clearAnimation();
        dialogManager.dismissInRefreshDialog();
        showErrorDialogInMainThread(R.string.unauthorized);
        noticePullFailed(false);
    }

    @Override
    public void onTimeOut() {
        canRefreshClickable = true;
        mIvRefresh.clearAnimation();
        dialogManager.dismissInRefreshDialog();
        showErrorDialogInMainThread(R.string.timeout);
        noticePullFailed(false);
    }

    @Override
    public void unInitialized() {
        canRefreshClickable = true;
        mIvRefresh.clearAnimation();
        dialogManager.dismissInRefreshDialog();
        showErrorDialogInMainThread(R.string.uninitialized);
        noticePullFailed(false);
    }

    private void noticePullFailed(boolean pullState) {
        EventBus.getDefault().post(pullState, EventBusTags.PULL_STATE);
    }

    private boolean isBothProfileConnected() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapterUtils.getBluetoothAdapter();
        int a2dpState = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP_SINK);
        int hfpState = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET_CLIENT);
        return a2dpState == BluetoothProfile.STATE_CONNECTED && hfpState == BluetoothProfile.STATE_CONNECTED;
    }

    private void showErrorDialogInMainThread(final int errorMsg) {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                ErrorDialog errorDialog = new ErrorDialog(MainActivity.this);
                errorDialog.show();
                errorDialog.setErrorMsg(errorMsg);
            }
        });
    }

    @Override
    public void isInPulling() {
        if (isPause) {
            return;
        }
        if (isShowRefreshDailog) {
            dialogManager.showInRefreshDialog();
        }
    }

    @Override
    public void applySkin() {
        ViewGroup rootLayout = getRootLayout();
        if (rootLayout != null) {
            rootLayout.setBackground(null);
        }
    }
}
