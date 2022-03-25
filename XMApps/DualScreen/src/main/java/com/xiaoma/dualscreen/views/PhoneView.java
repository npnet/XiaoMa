package com.xiaoma.dualscreen.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.dualscreen.R;
import com.xiaoma.dualscreen.manager.DualApiManager;
import com.xiaoma.dualscreen.manager.DualBluetoothPhoneApiManager;
import com.xiaoma.dualscreen.manager.DualBluetoothPhoneManager;
import com.xiaoma.dualscreen.manager.DualViewManager;
import com.xiaoma.dualscreen.model.ContactModel;
import com.xiaoma.dualscreen.utils.LastTimeFormatUtils;
import com.xiaoma.dualscreen.views.adapter.CallingListAdapter;
import com.xiaoma.dualscreen.views.adapter.ContactListAdapter;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.xiaoma.center.logic.CenterConstants.BLUETOOTH_PHONE_DIED;
import static com.xiaoma.center.logic.ErrorCode.CODE_REMOTE_CLIENT_NOT_FOUND;

/**
 * @author: iSun
 * @date: 2019/3/7 0007
 */
public class PhoneView extends BaseView implements DualBluetoothPhoneManager.PhoneStateUIListener {

    private Context mContext;
    private RelativeLayout mUnHungContainer;
    private LinearLayout mHungContainer, rcContactLL;
    private RelativeLayout mNoContactContainer, mNoBlueContainer;
    private TextView mTvContactTime, mTvContactName, mTvPhoneNumber, recentTitleTv, noContactTv, noBlueTv;
    private RecyclerView mRcContact, mRcOtherContact;
    private ImageView mIvNoContact, mIvNoBlue, mBgPhoneStateImg;
    private ImageView handUpTwo, handUpOne, answerOne;
    private ContactListAdapter mContactListAdapter;
    private CallingListAdapter mCallingListAdapter;
    private PhoneViewListener mPhoneViewListener;
    private int mSelectedCallingUser = 0;
    private static int mPhoneState = 0;  //0最近通话,1来电，2呼叫，3通话,4通话中有第三方呼入
    private ContactBean mSimpleTextContactBean;
    private SyncHistoryRunnable syncHistoryRunnable;
    private List<ContactBean> mContactBeanList = new ArrayList<>();
    private List<ContactModel> mCallingList = new ArrayList<>();
    private int lastInComingIndex = -1, lastKeepIndex = -1, lastActiveIndex = -1, lastCallIndex = -1;


    public PhoneView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PhoneView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public PhoneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public PhoneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public int contentViewId() {
        boolean isHigh = DualViewManager.getInstance().isHigh();
        if (isHigh) {
            return R.layout.view_phone;
        } else {
            return R.layout.view_phone_low;
        }
    }

    @Override
    public void onViewCreated() {
        init();
    }

    private void init() {
        mUnHungContainer = findViewById(R.id.rl_un_hung_container);
        mHungContainer = findViewById(R.id.ll_hung_container);
        mTvContactTime = findViewById(R.id.tv_contact_time);
        mTvContactName = findViewById(R.id.tv_contact_name);
        mTvPhoneNumber = findViewById(R.id.tv_phone_number);
        mRcContact = findViewById(R.id.rc_contact);
        rcContactLL =  findViewById(R.id.contact_list_ll);
        recentTitleTv = findViewById(R.id.recent_calls_tv);
        mNoContactContainer = findViewById(R.id.rl_no_contact);
        mNoBlueContainer = findViewById(R.id.rl_no_blue);
        mRcOtherContact = findViewById(R.id.rc_other_contact);
        mIvNoContact = findViewById(R.id.iv_no_contact);
        mIvNoBlue = findViewById(R.id.iv_no_blue);
        mBgPhoneStateImg = findViewById(R.id.bg_low_wisdom);
        noContactTv = findViewById(R.id.tv_no_contact);
        noBlueTv = findViewById(R.id.tv_no_blue);
        handUpTwo = findViewById(R.id.iv_hung_up_2);
        handUpOne = findViewById(R.id.iv_hung_up);
        answerOne = findViewById(R.id.iv_answer);
        resetAdapter(R.layout.item_contact);

        mCallingListAdapter = new CallingListAdapter(mContext, mCallingList, R.layout.item_calling);
        mRcOtherContact.setLayoutManager(new LinearLayoutManager(mContext));
        mRcOtherContact.setAdapter(mCallingListAdapter);

        DualBluetoothPhoneManager.getInstance().setPhoneStateUIListener(this);
        try{
            getContext().registerReceiver(phoneReceiver, new IntentFilter(BLUETOOTH_PHONE_DIED));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    BroadcastReceiver phoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BLUETOOTH_PHONE_DIED.equals(intent.getAction())){
               if(getPhoneState() != 0){
                   setPhoneState(0);
                   onResume();
               }
            }
        }
    };

    public void onResume() {
        if (mPhoneState == 0) {
            int result = DualBluetoothPhoneApiManager.getInstance().registerPhoneHistoryCallback();
            DualBluetoothPhoneApiManager.getInstance().registerPhoneStateCallback();
            getBluetoothConnectedAndContactList();
            if(result == CODE_REMOTE_CLIENT_NOT_FOUND){
                ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onResume();
                    }
                }, 2000);
            }
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        DualBluetoothPhoneManager.getInstance().setPhoneStateUIListener(null);
        try {
            getContext().unregisterReceiver(phoneReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
        this.mPhoneViewListener = null;
    }

    private void getBluetoothConnectedAndContactList() {
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                DualBluetoothPhoneApiManager.getInstance().isBluetoothConnected(new DualApiManager.onGetBooleanResultListener() {
                    @Override
                    public void onTrue() {
                        KLog.d("isBluetoothConnected onTrue");
                        getContactList();
                }

                    @Override
                    public void onFalse() {
                        KLog.d("isBluetoothConnected onFalse");
                        showNoBlue();
                    }
                });
            }
        }, 1000);  //加延迟避免中心服务还没连上
    }

    private void setPhoneState(int state) {
        this.mPhoneState = state;
    }

    public int getPhoneState() {
        return this.mPhoneState;
    }

    public ContactBean getSimpleTextContactBean() {
        return mSimpleTextContactBean;
    }

    public void setPhoneViewListener(PhoneViewListener phoneViewListener) {
        this.mPhoneViewListener = phoneViewListener;
    }

    public void showLowWisdom() {
        if (!DualViewManager.getInstance().isHigh()
                && DualViewManager.getInstance().getCurSkin() == 0)
            mBgPhoneStateImg.setVisibility(VISIBLE);
    }

    public void dismissLowWisdom() {
        mBgPhoneStateImg.setVisibility(GONE);
    }

    public void showInComing(ContactBean bean) {
        KLog.d("showInComing");
        mSimpleTextContactBean = bean;
        String contactName = bean.getName();
        String phoneNumber = bean.getPhoneNum();
        mTvContactName.setText(contactName);
        mTvPhoneNumber.setText(phoneNumber);
        setPhoneState(1);
        mTvContactName.setVisibility(View.VISIBLE);
        mTvPhoneNumber.setVisibility(View.VISIBLE);
        mUnHungContainer.setVisibility(View.VISIBLE);
        mHungContainer.setVisibility(View.GONE);
        mRcContact.setVisibility(View.GONE);
        if(rcContactLL != null) rcContactLL.setVisibility(GONE);
        mRcOtherContact.setVisibility(View.GONE);
        mNoContactContainer.setVisibility(View.GONE);
        mNoBlueContainer.setVisibility(View.GONE);
        String name = contactName;
        if (name.equalsIgnoreCase(getContext().getString(R.string.unknow_caller))) {
            name = phoneNumber;
        }
        if(mPhoneViewListener != null) mPhoneViewListener.onImComing(name);
        showLowWisdom();
    }

    public void showComeOut(ContactBean contactBean) {
        KLog.d("showComeOut");
        mSimpleTextContactBean = contactBean;
        mTvContactName.setText(contactBean.getName());
        mTvPhoneNumber.setText(contactBean.getPhoneNum());
        setPhoneState(2);
        mTvContactName.setVisibility(View.VISIBLE);
        mTvPhoneNumber.setVisibility(View.VISIBLE);
        mUnHungContainer.setVisibility(View.GONE);
        mHungContainer.setVisibility(View.VISIBLE);
        mTvContactTime.setText(getContext().getString(R.string.calling_now));
        mRcContact.setVisibility(View.GONE);
        if(rcContactLL != null) rcContactLL.setVisibility(GONE);
        mRcOtherContact.setVisibility(View.GONE);
        mNoContactContainer.setVisibility(View.GONE);
        mNoBlueContainer.setVisibility(View.GONE);
        String name = contactBean.getName();
        if (name.equalsIgnoreCase(getContext().getString(R.string.unknow_caller))) {
            name = contactBean.getPhoneNum();
        }
        if(mPhoneViewListener != null) mPhoneViewListener.onComeOut(name);
        showLowWisdom();
    }

    public void showContacting(ContactBean contactBean) {
        KLog.d("showContacting");
        if (contactBean == null) {
            return;
        }
        mSimpleTextContactBean = contactBean;
        setPhoneState(3);
        mTvContactName.setText(contactBean.getName());
        mTvPhoneNumber.setText(contactBean.getPhoneNum());
        mTvContactTime.setText(LastTimeFormatUtils.getCallDuration(contactBean));
        mTvContactName.setVisibility(View.VISIBLE);
        mTvPhoneNumber.setVisibility(View.VISIBLE);
        mUnHungContainer.setVisibility(View.GONE);
        mHungContainer.setVisibility(View.VISIBLE);
        mRcContact.setVisibility(View.GONE);
        if(rcContactLL != null) rcContactLL.setVisibility(GONE);
        mRcOtherContact.setVisibility(View.GONE);
        mNoContactContainer.setVisibility(View.GONE);
        mNoBlueContainer.setVisibility(View.GONE);
        String name = contactBean.getName();
        if (name.equalsIgnoreCase(getContext().getString(R.string.unknow_caller))) {
            name = contactBean.getPhoneNum();
        }
        if(mPhoneViewListener != null) mPhoneViewListener.onCalling(name, LastTimeFormatUtils.getCallDuration(contactBean));
        showLowWisdom();
    }

    public void showOtherComing(List<ContactModel> contactModelList) {
        KLog.d("showOtherComing");
        if (ListUtils.isEmpty(contactModelList)) {
            return;
        }
        setPhoneState(4);
        Collections.reverse(contactModelList);
        mCallingList.clear();
        if(contactModelList != null) mCallingList.addAll(contactModelList);
        resetCallSelect();
        mCallingListAdapter.notifyDataSetChanged();
        boolean isInComing = false;
        ContactBean inComingBean = null;
        ContactBean activeBean = null;

        mTvContactName.setVisibility(View.GONE);
        mTvPhoneNumber.setVisibility(View.GONE);
        mUnHungContainer.setVisibility(View.GONE);
        mHungContainer.setVisibility(View.GONE);
        mRcContact.setVisibility(View.GONE);
        if(rcContactLL != null) rcContactLL.setVisibility(GONE);
        mRcOtherContact.setVisibility(View.VISIBLE);
        mNoContactContainer.setVisibility(View.GONE);
        mNoBlueContainer.setVisibility(View.GONE);

        for (int i = 0; i < contactModelList.size(); i++) {
            if (contactModelList.get(i).getState() == State.INCOMING) {
                isInComing = true;
                inComingBean = contactModelList.get(i).getContactBean();
            } else if (contactModelList.get(i).getState() == State.ACTIVE) {
                activeBean = contactModelList.get(i).getContactBean();
            }
        }
        if (isInComing) {
            if (inComingBean == null) {
                return;
            }
            String name = inComingBean.getName();
            if (name.equalsIgnoreCase(getContext().getString(R.string.unknow_caller))) {
                name = inComingBean.getPhoneNum();
            }
            mSimpleTextContactBean = inComingBean;
            if(mPhoneViewListener != null) mPhoneViewListener.onOtherInComing(name);
        } else {
            if (activeBean == null) {
                return;
            }
            String name = activeBean.getName();
            if (name.equalsIgnoreCase(getContext().getString(R.string.unknow_caller))) {
                name = activeBean.getPhoneNum();
            }
            mSimpleTextContactBean = activeBean;
            if(mPhoneViewListener != null) mPhoneViewListener.onOtherCalling(name, LastTimeFormatUtils.getCallDuration(activeBean));
        }
        showLowWisdom();
    }

    private void resetCallSelect(){
        int inCommingIndex = -1;
        int inActiveIndex = -1;
        int inKeepIndex = -1;
        int inCallIndex = -1;
        for (int i = 0; i < mCallingList.size(); i++) {
            if (mCallingList.get(i).getState() == State.INCOMING) {
                inCommingIndex = i;
            }else if(mCallingList.get(i).getState() == State.ACTIVE){
                inActiveIndex = i;
            }else if(mCallingList.get(i).getState() == State.KEEP){
                inKeepIndex = i;
            }else if(mCallingList.get(i).getState() == State.CALL){
                inCallIndex = i;
            }
        }
        boolean hasChange = false;
        if(lastActiveIndex != inActiveIndex || lastInComingIndex != inCommingIndex
                || lastKeepIndex != inKeepIndex || lastCallIndex != inCallIndex){
            hasChange = true;
        }
        resetCallIndex(inCommingIndex, inActiveIndex, inKeepIndex, inCallIndex);
        if(hasChange){
            if(inKeepIndex >= 0){
                if(inActiveIndex >= 0){
                    mSelectedCallingUser = inActiveIndex;
                }else if(inCommingIndex >= 0){
                    mSelectedCallingUser = inCommingIndex;
                }
            }else{
                if(inCommingIndex >= 0){
                    mSelectedCallingUser = inCommingIndex;
                }else  if(inActiveIndex >= 0){
                    mSelectedCallingUser = inActiveIndex;
                }
            }
            mCallingListAdapter.setSelectedItem(mSelectedCallingUser);
        }
    }

    private void resetCallIndex(int inCommingIndex, int inActiveIndex, int inKeepIndex, int inCallIndex){
        lastInComingIndex = inCommingIndex;
        lastActiveIndex = inActiveIndex;
        lastKeepIndex = inKeepIndex;
        lastCallIndex = inCallIndex;
    }

    public void getContactList() {
        DualBluetoothPhoneApiManager.getInstance().isContactBookSynchronized(new DualApiManager.OnTrueListener() {
            @Override
            public void onTrue() {
                KLog.d("getContactList onTrue");
                DualBluetoothPhoneApiManager.getInstance().getCallHistory(new IClientCallback.Stub() {
                    @Override
                    public void callback(Response response) throws RemoteException {
                        KLog.d("getCallHistory callback");
                        Bundle extra = response.getExtra();
                        List<ContactBean> list = null;
                        try {
                            list = extra.getParcelableArrayList(CenterConstants.BluetoothPhoneThirdBundleKey.GET_CALL_HISTORY);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final List<ContactBean> finalList = list;
                        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                            @Override
                            public void run() {
                                mContactBeanList.clear();
                                if(finalList != null) mContactBeanList.addAll(finalList);
                                if(mContactBeanList.size() > 0){
                                    mContactListAdapter.resetSelect();
                                    mContactListAdapter.next();
                                }else{
                                    mContactListAdapter.notifyDataSetChanged();
                                }
                                if(mPhoneState != 0) return;
                                if (!ListUtils.isEmpty(mContactBeanList)) {
                                    showContactList();
                                } else {
                                    showNoContact();
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onFalse() {
                KLog.d("getContactList onFalse");
                ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if(mPhoneState != 0) return;
                        if (!ListUtils.isEmpty(mContactBeanList)) {
                            showContactList();
                        } else {
                            showNoContact();
                        }
                    }
                });
            }
        });
    }

    public void showContactList() {
        KLog.d("showContactList");
        setPhoneState(0);
        mTvContactName.setVisibility(View.GONE);
        mTvPhoneNumber.setVisibility(View.GONE);
        mUnHungContainer.setVisibility(View.GONE);
        mHungContainer.setVisibility(View.GONE);
        mRcOtherContact.setVisibility(View.GONE);
        mNoContactContainer.setVisibility(View.GONE);
        mNoBlueContainer.setVisibility(View.GONE);
        mRcContact.setVisibility(View.VISIBLE);
        if(rcContactLL != null) rcContactLL.setVisibility(VISIBLE);
        if(mPhoneViewListener != null) mPhoneViewListener.onContactList();
        if(DualViewManager.getInstance().getCurSkin() == 0){
            showLowWisdom();
        }else{
            dismissLowWisdom();
        }
        resetCallIndex(-1, -1, -1, -1);
    }

    public void showNoContact() {
        KLog.d("showNoContact");
        setPhoneState(0);
        mTvContactName.setVisibility(View.GONE);
        mTvPhoneNumber.setVisibility(View.GONE);
        mUnHungContainer.setVisibility(View.GONE);
        mHungContainer.setVisibility(View.GONE);
        mRcContact.setVisibility(View.GONE);
        if(rcContactLL != null) rcContactLL.setVisibility(GONE);
        mRcOtherContact.setVisibility(View.GONE);
        mNoContactContainer.setVisibility(View.VISIBLE);
        mNoBlueContainer.setVisibility(View.GONE);
        if(mPhoneViewListener != null) mPhoneViewListener.onNoContact();
        dismissLowWisdom();
        resetCallIndex(-1, -1, -1, -1);
    }

    public void showNoBlue() {
        KLog.d("showNoBlue");
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                mContactBeanList.clear();
                mContactListAdapter.notifyDataSetChanged();
                mCallingList.clear();
                setPhoneState(0);
                mTvContactName.setVisibility(View.GONE);
                mTvPhoneNumber.setVisibility(View.GONE);
                mUnHungContainer.setVisibility(View.GONE);
                mHungContainer.setVisibility(View.GONE);
                mRcContact.setVisibility(View.GONE);
                if(rcContactLL != null) rcContactLL.setVisibility(GONE);
                mRcOtherContact.setVisibility(View.GONE);
                mNoContactContainer.setVisibility(View.GONE);
                mNoBlueContainer.setVisibility(View.VISIBLE);
                if(mPhoneViewListener != null) mPhoneViewListener.onNoBlue();
                dismissLowWisdom();
                resetCallIndex(-1, -1, -1, -1);
            }
        });
    }

    public void setPhoneSelectUp() {
        if (mPhoneState == 0) {
            if (mContactListAdapter != null) {
                mContactListAdapter.pre();
                mRcContact.smoothScrollToPosition(mContactListAdapter.mSelectedItemPosition);
                mRcContact.setHasFixedSize(true);
            }
        } else if (mPhoneState == 4) {
            mSelectedCallingUser--;
            if (mSelectedCallingUser < 0) {
                mSelectedCallingUser = 0;
            }
            mCallingListAdapter.setSelectedItem(mSelectedCallingUser);
        }
    }

    public void setPhoneSelectDown() {
        if (mPhoneState == 0) {
            if (mContactListAdapter != null) {
                mContactListAdapter.next();
                mRcContact.smoothScrollToPosition(mContactListAdapter.mSelectedItemPosition);
                mRcContact.setHasFixedSize(true);
            }
        } else if (mPhoneState == 4) {
            mSelectedCallingUser++;
            if (mSelectedCallingUser > 1) {
                mSelectedCallingUser = 1;
            }
            mCallingListAdapter.setSelectedItem(mSelectedCallingUser);
        }
    }

    //方控确定处理
    public void setPhoneOk() {
        KLog.d("setPhoneOk, mPhoneState=" + mPhoneState);
        if (mPhoneState == 0) {
            ContactBean contactBean = mContactListAdapter.getSelectedContactBean();
            if (contactBean == null) {
                return;
            }
            DualBluetoothPhoneApiManager.getInstance().dial(contactBean.getPhoneNum());
        }
//        else if (mPhoneState == 1) {
//            DualBluetoothPhoneApiManager.getInstance().acceptCall();
//        } else if (mPhoneState == 4) {
//            KLog.d("setPhoneOk" + mCallingListAdapter.getSelectedState());
//            if (mCallingListAdapter.getSelectedState() == State.KEEP) {
//                DualBluetoothPhoneApiManager.getInstance().holdCall();
//            } else if (mCallingListAdapter.getSelectedState() == State.INCOMING) {
//                DualBluetoothPhoneApiManager.getInstance().acceptCall();
//            }
//        }
    }

    //方控取消处理
    public void setPhoneCancel() {
        if (mPhoneState == 1 || mPhoneState == 2 || mPhoneState == 3 || mPhoneState == 4) {
            if (mPhoneState == 1 || mPhoneState == 4) { //来电,第三方呼入
                DualBluetoothPhoneApiManager.getInstance().rejectCall();
            } else {   //拨打，通话中
                DualBluetoothPhoneApiManager.getInstance().terminateCall();
            }
        }
    }

    @Override
    public void onConferIncomingPhone(final ContactBean bean) {
        KLog.d("新的来电:" + bean.getPhoneNum() + ";" + bean.getName());
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                showInComing(bean);
            }
        });
    }

    @Override
    public void onConferClearPhoneState(ContactBean contactBean) {
        KLog.e("onConferClearPhoneState");
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                getBluetoothConnectedAndContactList();
                if (!ListUtils.isEmpty(mContactBeanList)) {
                    showContactList();
                } else {
                    showNoContact();
                }
                if(mPhoneViewListener != null) mPhoneViewListener.clearPhoneState();
                dismissLowWisdom();
            }
        });

    }

    @Override
    public void onConferCallingPhone(final ContactBean bean) {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                showContacting(bean);
            }
        });
    }

    @Override
    public void onConferCallOutPhone(final ContactBean bean) {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                showComeOut(bean);
            }
        });
    }

    @Override
    public void onConferOtherPhoneIncoming(final List<ContactModel> contactModelList) {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                showOtherComing(contactModelList);
            }
        });
    }

    @Override
    public void onBlueToothConnected() {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                showNoContact();
            }
        });
    }

    @Override
    public void onHfpConnected() {

    }

    @Override
    public void onBlueToothDisconnected() {
        showNoBlue();
    }

    @Override
    public void onBlueToothDisabled() {
        showNoBlue();
    }

    @Override
    public void onHfpDisconnected() {

    }

    @Override
    public void onPbapConnected() {

    }

    @Override
    public void onPbapDisconnected() {

    }

    @Override
    public void onCallHistorySync(final List<ContactBean> contactBeanList) {
        KLog.d("onCallHistorySync: "+contactBeanList.size());
        if(syncHistoryRunnable != null) ThreadDispatcher.getDispatcher().removeOnMain(syncHistoryRunnable);
        ThreadDispatcher.getDispatcher().postOnMainDelayed(syncHistoryRunnable = new SyncHistoryRunnable(contactBeanList), 1000);
    }


    class SyncHistoryRunnable  implements Runnable {

        List<ContactBean> contactBeanList;

        SyncHistoryRunnable(List<ContactBean> contactBeanList){
            this.contactBeanList = contactBeanList;
        }

        @Override
        public void run() {
            if (!ListUtils.isEmpty(contactBeanList) && mCallingListAdapter != null) {
                mContactBeanList.clear();
                if(contactBeanList != null) mContactBeanList.addAll(contactBeanList);
                mContactListAdapter.notifyDataSetChanged();
                if(mPhoneState != 0) return;
                showContactList();
            } else {
                showNoContact();
            }
        }
    }


    public interface PhoneViewListener {
        void onCalling(String name, String time);

        void onContactList();

        void onImComing(String name);

        void onComeOut(String name);

        void onOtherInComing(String name);

        void clearPhoneState();

        void onOtherCalling(String name, String time);

        void onNoContact();

        void onNoBlue();
    }

    //skin
    public void changeSkin(boolean isHigh, int skinType) {
        if (isHigh) {
            switch (skinType) {
                case 1:
                    mTvPhoneNumber.setTextColor(getContext().getColor(R.color.simple_tv_color_blue));
                    mIvNoContact.setImageResource(R.drawable.icon_no_contact_high_luxury);
                    mIvNoBlue.setImageResource(R.drawable.icon_no_blue_high_luxury);
                    resetAdapter(R.layout.item_contact_high_two);
                    resetNoBlueView(380, 356, 35, 45);
                    resetNoContactView(380, 356,35, 55);
                    resetUserNameView(90);
                    resetRCContactView(70);
                    mCallingListAdapter.setSelectedColor(R.color.simple_tv_color_blue, R.drawable.btn_hung_up,R.drawable.btn_answer);
                    mContactListAdapter.setSelectedColor(R.color.simple_tv_color_blue);
                    dismissLowWisdom();
                    break;
                case 2:
                    mTvPhoneNumber.setTextColor(getContext().getColor(R.color.simple_tv_color_yellow));
                    mIvNoContact.setImageResource(R.drawable.icon_no_contact_high_dream);
                    mIvNoBlue.setImageResource(R.drawable.icon_no_blue_high_dream);
                    resetAdapter(R.layout.item_contact);
                    resetNoBlueView(380, 356, 55, 65);
                    resetNoContactView(380, 356, 55, 75);
                    resetUserNameView(90);
                    resetRCContactView(70);
                    mCallingListAdapter.setSelectedColor(R.color.simple_tv_color_yellow, R.drawable.btn_hung_up,R.drawable.btn_answer);
                    mContactListAdapter.setSelectedColor(R.color.simple_tv_color_yellow);
                    dismissLowWisdom();
                    break;
                case 0:default:
                    mTvPhoneNumber.setTextColor(getContext().getColor(R.color.simple_tv_color_yellow));
                    mIvNoContact.setImageResource(R.drawable.icon_no_contact_high_wisdom);
                    mIvNoBlue.setImageResource(R.drawable.icon_no_blue_high_wisdom);
                    resetAdapter(R.layout.item_contact);
                    resetNoBlueView(380, 356, 40, 50);
                    resetNoContactView(380, 356, 40, 60);
                    resetUserNameView(100);
                    resetRCContactView(70);
                    mCallingListAdapter.setSelectedColor(R.color.simple_tv_color_yellow, R.drawable.btn_hung_up,R.drawable.btn_answer);
                    mContactListAdapter.setSelectedColor(R.color.simple_tv_color_yellow);
                    dismissLowWisdom();
                    break;
            }
        } else {
            switch (skinType) {
                case 1:
                    mTvPhoneNumber.setTextColor(getContext().getColor(R.color.simple_tv_color_blue));
                    if(recentTitleTv != null) recentTitleTv.setTextColor(getContext().getColor(R.color.simple_tv_color_blue));
                    mIvNoContact.setImageResource(R.drawable.icon_no_contact_low_luxury);
                    mIvNoBlue.setImageResource(R.drawable.icon_no_blue_low_luxury);
                    handUpTwo.setImageResource(R.drawable.btn_hung_up_low);
                    handUpOne.setImageResource(R.drawable.btn_hung_up_low);
                    answerOne.setImageResource(R.drawable.btn_answer_low);
                    resetLayout(404, 404, 250, 40);
                    resetNoBlueView(174, 172, 155, 85);
                    resetNoContactView(174, 172, 155, 85);
                    resetUserNameView(70);
                    resetRCContactView(-30);
                    resetAdapter(R.layout.item_contact_low_two);
                    resetCallingAdapter(R.layout.item_calling_low_two);
                    mCallingListAdapter.setSelectedColor(R.color.simple_tv_color_blue, R.drawable.btn_hung_up_low,R.drawable.btn_answer_low);
                    mContactListAdapter.setSelectedColor(R.color.simple_tv_color_blue);
                    dismissLowWisdom();
                    break;
                case 2:
                    mTvPhoneNumber.setTextColor(getContext().getColor(R.color.simple_tv_color_yellow));
                    if(recentTitleTv != null) recentTitleTv.setTextColor(getContext().getColor(R.color.simple_tv_color_yellow));
                    mIvNoContact.setImageResource(R.drawable.icon_no_contact_low_dream);
                    mIvNoBlue.setImageResource(R.drawable.icon_no_blue_low_dream);
                    handUpTwo.setImageResource(R.drawable.btn_hung_up);
                    handUpOne.setImageResource(R.drawable.btn_hung_up);
                    answerOne.setImageResource(R.drawable.btn_answer);
                    resetLayout(450, 453, 300, 20);
                    resetNoBlueView(380, 356, 75, 90);
                    resetNoContactView(380, 356, 75, 90);
                    resetUserNameView(30);
                    resetRCContactView(-40);
                    resetAdapter(R.layout.item_contact_low_three);
                    resetCallingAdapter(R.layout.item_calling_low_dream);
                    mCallingListAdapter.setSelectedColor(R.color.simple_tv_color_yellow, R.drawable.btn_hung_up,R.drawable.btn_answer );
                    mContactListAdapter.setSelectedColor(R.color.simple_tv_color_yellow);
                    dismissLowWisdom();
                    break;
                case 0:default:
                    mTvPhoneNumber.setTextColor(getContext().getColor(R.color.simple_tv_color_yellow));
                    if(recentTitleTv != null) recentTitleTv.setTextColor(getContext().getColor(R.color.simple_tv_color_yellow));
                    mIvNoContact.setImageResource(R.drawable.icon_no_contact_high_wisdom);
                    mIvNoBlue.setImageResource(R.drawable.icon_no_blue_high_wisdom);
                    handUpTwo.setImageResource(R.drawable.btn_hung_up);
                    handUpOne.setImageResource(R.drawable.btn_hung_up);
                    answerOne.setImageResource(R.drawable.btn_answer);
                    resetLayout(532, 542, 300, 50);
                    resetNoBlueView(380, 356, 135, 155);
                    resetNoContactView(380, 356, 135, 160);
                    resetUserNameView(40);
                    resetRCContactView(-40);
                    resetAdapter(R.layout.item_contact_low_one);
                    resetCallingAdapter(R.layout.item_calling);
                    mCallingListAdapter.setSelectedColor(R.color.simple_tv_color_yellow, R.drawable.btn_hung_up,R.drawable.btn_answer );
                    mContactListAdapter.setSelectedColor(R.color.simple_tv_color_yellow);
                    if (mPhoneState == 0) {
                        dismissLowWisdom();
                    } else {
                        showLowWisdom();
                    }
                    break;
            }
        }
    }

    private void resetRCContactView(int marginTop){
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mRcOtherContact.getLayoutParams();
        linearParams.topMargin = marginTop;
        mRcOtherContact.setLayoutParams(linearParams);
    }

    private void resetUserNameView(int txtMarginTop){
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mTvContactName.getLayoutParams();
        linearParams.topMargin = txtMarginTop;
        mTvContactName.setLayoutParams(linearParams);
    }


    private void resetNoContactView(int width, int height, int ivMarginTop, int txtMarginTop){
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) mIvNoContact.getLayoutParams();
        linearParams.width = width;
        linearParams.height = height;
        linearParams.topMargin = ivMarginTop;
        mIvNoContact.setLayoutParams(linearParams);

        RelativeLayout.LayoutParams desLinearParams = (RelativeLayout.LayoutParams) noContactTv.getLayoutParams();
        desLinearParams.topMargin = txtMarginTop;
        noContactTv.setLayoutParams(desLinearParams);
    }


    private void resetNoBlueView(int width, int height, int ivMarginTop, int txtMarginTop){
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) mIvNoBlue.getLayoutParams();
        linearParams.width = width;
        linearParams.height = height;
        linearParams.topMargin = ivMarginTop;
        mIvNoBlue.setLayoutParams(linearParams);

        RelativeLayout.LayoutParams desLinearParams = (RelativeLayout.LayoutParams) noBlueTv.getLayoutParams();
        desLinearParams.topMargin = txtMarginTop;
        noBlueTv.setLayoutParams(desLinearParams);
    }

    private void resetLayout(int width, int height, int listHeight, int titlePadingTop) {
        LinearLayout.LayoutParams parentLinearParams = (LinearLayout.LayoutParams) rcContactLL.getLayoutParams();
        parentLinearParams.width = width;
        parentLinearParams.height = height;
        rcContactLL.setLayoutParams(parentLinearParams);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mRcContact.getLayoutParams();
        linearParams.width = width;
        linearParams.height = listHeight;
        mRcContact.setLayoutParams(linearParams);

        if(recentTitleTv != null) recentTitleTv.setPadding(0, titlePadingTop, 0, 0);
    }

    private void resetAdapter(int p) {
        mContactListAdapter = new ContactListAdapter(mContext, mContactBeanList, p);
        mRcContact.setLayoutManager(new LinearLayoutManager(mContext));
        mRcContact.setAdapter(mContactListAdapter);
    }

    private void resetCallingAdapter(int p) {
        mCallingListAdapter =  new CallingListAdapter(mContext, mCallingList, p);
        mRcOtherContact.setLayoutManager(new LinearLayoutManager(mContext));
        mRcOtherContact.setAdapter(mCallingListAdapter);
    }

}
