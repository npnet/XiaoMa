package com.xiaoma.bluetooth.phone.common.model;

import com.xiaoma.bluetooth.phone.BlueToothPhone;
import com.xiaoma.aidl.model.State;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.aidl.model.ContactBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: iSun
 * @date: 2018/11/30 0030
 */
public class BluePhoneState implements Serializable {
    private State[] mStates = {State.IDLE, State.IDLE};
    private boolean mIsHangUp;
    private List<ContactBean> mBeanList = new ArrayList<>();
    private Boolean[] mIsStartTimeKeepingList = {false, false};

    public BluePhoneState() {
        mBeanList.add(null);
        mBeanList.add(null);
    }

    public void setState(int index, State state) {
        if (index >= 0 && index < 2) {
            mStates[index] = state;
        }
    }

    public State getState(int index) {
        if (index >= 0 && index < 2) {
            return mStates[index];
        }
        return mStates[0];
    }

    public boolean isCallState() {
        return (mStates[0] != State.IDLE) || (mStates[1] != State.IDLE);
    }

    public boolean isActiveState() {
        return (mStates[0] == State.ACTIVE) || (mStates[1] == State.ACTIVE);
    }

    public boolean isBothCallBusy() {
        return mStates[0] != State.IDLE && mStates[1] != State.IDLE;
    }

    public List<ContactBean> getBeanList() {
        List<ContactBean> tempList = new ArrayList<>();
        for (int i = 0; i < mBeanList.size(); i++) {
            tempList.add(mBeanList.get(i) != null ? (ContactBean) mBeanList.get(i).clone() : null);
        }
        return tempList;
    }

    public boolean updateState(ContactBean bean, State state) {
        String phoneNum = bean.getPhoneNum();
        for (int i = 0; i < mBeanList.size(); i++) {
            if (mBeanList.get(i) == null) continue;
            if (mBeanList.get(i).getPhoneNum().equals(phoneNum)) {
                if (i == 0 && state == State.IDLE && mBeanList.get(1) != null) {
                    updateStateAtIndex(0, mStates[1], mBeanList.get(1), mIsStartTimeKeepingList[1]);
                    clearStateAtIndex(1);
                    bean = mBeanList.get(0);
                } else {
                    if (state == State.IDLE) {
                        bean = mBeanList.get(i);
                        clearStateAtIndex(i);
                    } else {
                        updateStateAtIndex(i, state, bean, mIsStartTimeKeepingList[i]); //不修改mIsStartTimeKeepingList[i]
                        bean = mBeanList.get(i);
                    }
                }
                return true;
            }
        }
        if (state == State.IDLE) {
            return false;
        }
        if (mBeanList.get(0) == null) {
            updateStateAtIndex(0, state, bean, mIsStartTimeKeepingList[0]);
            bean = mBeanList.get(0);
        } else if (mBeanList.get(0) != null && mBeanList.get(1) == null) {
            updateStateAtIndex(1, state, bean, mIsStartTimeKeepingList[1]);
            bean = mBeanList.get(1);
        }
        return true;
    }

    private void updateStateAtIndex(int index, State state, ContactBean bean, boolean isStartTimeKeeping) {
        if (state == State.IDLE) {
            if (mBeanList.get(index) != null) {
                PhoneStateManager.getInstance(BlueToothPhone.getContext()).setBeforeState(State.getState(mBeanList.get(index).getBeforeState()));
            }
        }
        mStates[index] = state;
        setBean(index, bean);
        mIsStartTimeKeepingList[index] = isStartTimeKeeping;
        if (state == State.CALL || state == State.INCOMING) {
            mBeanList.get(index).setBeforeState(state.getValue());
        }
    }

    public void clearState() {
        clearStateAtIndex(0);
        clearStateAtIndex(1);
        PhoneStateManager.getInstance(BlueToothPhone.getContext()).notifyCallState();
        PhoneStateManager.getInstance(BlueToothPhone.getContext()).notifyStateChange(mBeanList.get(0), mStates[0]);
        PhoneStateManager.getInstance(BlueToothPhone.getContext()).notifyStateChange(mBeanList.get(1), mStates[1]);
    }

    private void clearStateAtIndex(int index) {
        updateStateAtIndex(index, State.IDLE, null, false);
    }

    /**
     * 通话时长计时
     *
     * @param elapsedTime
     * @param phoneNum
     */
    public void beginTimeKeeping(final long elapsedTime, final String phoneNum) {
        final int index = getIndex(phoneNum);
        if (index == -1 || mIsStartTimeKeepingList[index]) {
            return;
        }
        mIsStartTimeKeepingList[index] = true;
        mStates[index] = State.ACTIVE;
        BlueToothPhone.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int index = getIndex(phoneNum);
                if (index == -1) return;
                if (mStates[index] != State.ACTIVE && mStates[index] != State.KEEP) {
                    mIsStartTimeKeepingList[index] = false;
                    return;
                }
                BlueToothPhone.getHandler().postDelayed(this, 1000);
                ContactBean bean = mBeanList.get(index);
                if (bean.getElapsedTime() == 0) {
                    bean.setElapsedTime(elapsedTime + 1000);
                } else {
                    bean.setElapsedTime(bean.getElapsedTime() + 1000);
                }
                PhoneStateManager.getInstance(BlueToothPhone.getContext()).notifyStateChange(bean, mStates[index]);
            }
        }, 1000);
    }

    public int getIndex(String phoneNum) {
        for (int i = 0; i < mBeanList.size(); i++) {
            if (mBeanList.get(i) == null) continue;
            if (phoneNum.equals(mBeanList.get(i).getPhoneNum())) {
                return i;
            }
        }
        return -1;
    }

    private void setBean(int index, ContactBean bean) {
        if (mBeanList.get(index) != null && bean != null) {
            if (bean.getCallStartTime() == 0 && mBeanList.get(index).getCallStartTime() != 0) {
                bean.setCallStartTime(mBeanList.get(index).getCallStartTime());
            }
            if (mBeanList.get(index).isMute()) {
                bean.setMute(true);
            }
            if (mBeanList.get(index).getBeforeState() != 0) {
                bean.setBeforeState(mBeanList.get(index).getBeforeState());
            }
            if (mBeanList.get(index).getIsAnswerOnPhone()) {
                bean.setIsAnswerOnPhone(mBeanList.get(index).getIsAnswerOnPhone());
            }
        }
        mBeanList.set(index, bean);
    }

    public boolean isHangUp() {
        return mIsHangUp;
    }

    public void setIsHangUp(boolean mIsHangUp) {
        this.mIsHangUp = mIsHangUp;
    }

    public ContactBean getCurrentActiveBean() {
        if (!isCallState()) {
            return null;
        }
        if (!isBothCallBusy()) {
            return mBeanList.get(0);
        } else {
            return mStates[0] == State.ACTIVE ? mBeanList.get(0) : mBeanList.get(1);
        }
    }

    public int[] getStates() {
        int[] states = new int[2];
        for (int i = 0; i < mStates.length; i++) {
            states[i] = mStates[i].getValue();
        }
        return states;
    }

    public boolean isKeep() {
        return mStates[0] == State.KEEP && mStates[1] == State.IDLE;
    }

}
