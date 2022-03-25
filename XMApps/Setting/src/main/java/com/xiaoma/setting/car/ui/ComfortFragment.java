package com.xiaoma.setting.car.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.setting.R;
import com.xiaoma.setting.car.vm.ComfortVM;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.constant.SettingConstants;
import com.xiaoma.setting.common.views.SettingItemView;
import com.xiaoma.setting.common.views.SwitchAnimation;
import com.xiaoma.utils.log.KLog;

@PageDescComponent(EventConstants.PageDescribe.comfortSettingFragmentPagePathDesc)
public class ComfortFragment extends BaseFragment implements SettingItemView.StateListener, SwitchAnimation.SwitchListener, ComfortVM.OnValueChange {
    private static final String TAG = ComfortFragment.class.getSimpleName();
    private static final String CAR_CONTROL_REQ = "car_control_req";
    private static final String CAR_CONTROL = "car_control";
    private SettingItemView sivDoorUnlock;
    private SwitchAnimation sivSpeedLock;
    private SwitchAnimation sivLeaveCarLock;
    private SwitchAnimation sivCarWindow;
    private SwitchAnimation sivRearview;
    private SwitchAnimation sivSeat;
    private SwitchAnimation scvTrunk;
    private ComfortVM convenientVM;
    private SwitchAnimation farLock;
    private SwitchAnimation approachUnlock;
    private int currentSetting;
    private ImageView mIvSettingPic;
    private int[] telecontrolUnlockImagesArr = {R.drawable.comfort_telecontrol_unlock_all_door, R.drawable.comfort_telecontrol_unlock_driver_door};
    private boolean isHiddenChanged = false; // 是否执行了onHiddenChanged
//    private String wjw = "Wjw";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_car_comfort, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        convenientVM.setOnValueChange(this);
    }

    private void bindView(View view) {
        sivDoorUnlock = view.findViewById(R.id.siv_door_unlock);
        sivDoorUnlock.setDelayItemSelector(true);
        sivSpeedLock = view.findViewById(R.id.speed_lock_control);
        sivLeaveCarLock = view.findViewById(R.id.leave_car_lock_control);
        sivCarWindow = view.findViewById(R.id.car_windows);
        sivRearview = view.findViewById(R.id.rearview);
        /*if (!XmCarConfigManager.hasBackMirrorAutoFold()) {
            sivRearview.setVisibility(View.GONE);
            view.findViewById(R.id.seat_line).setVisibility(View.GONE);
        }*/
        sivSeat = view.findViewById(R.id.seat);
        if (!XmCarConfigManager.hasAutoSeat()) {
            sivSeat.setVisibility(View.GONE);
            view.findViewById(R.id.seat_line).setVisibility(View.GONE);
        }
        scvTrunk = view.findViewById(R.id.trunk);
        if (!XmCarConfigManager.hasElectricTrunk()) {
            scvTrunk.setVisibility(View.GONE);
            view.findViewById(R.id.trunk_line).setVisibility(View.GONE);
        }
        farLock = view.findViewById(R.id.far_lock_view);
        approachUnlock = view.findViewById(R.id.approach_unlock_view);
        mIvSettingPic = view.findViewById(R.id.iv_setting_pic);

        farLock.setListener(this);
        approachUnlock.setListener(this);

        sivDoorUnlock.setListener(this);
        sivSpeedLock.setListener(this);
        sivLeaveCarLock.setListener(this);
        sivCarWindow.setListener(this);
        sivRearview.setListener(this);
        scvTrunk.setListener(this);
        sivSeat.setListener(this);

        initData();

    }

    private void initData() {
        convenientVM = ViewModelProviders.of(this).get(ComfortVM.class).initData();
        //初始化所有设置数据
        sivDoorUnlock.setCheck(getVM().getRemoteControlMode());
        sivSpeedLock.check(getVM().getSpeedLockControl());
        sivLeaveCarLock.check(getVM().getLeaveAutomaticLock());
        sivCarWindow.check(getVM().getSelfClosingWindow());
        sivRearview.check(getVM().getRearviewMirror());
        scvTrunk.check(getVM().getTrunk());
        sivSeat.check(getVM().getWelcomeSeat());

        farLock.check(getVM().getManager().getFarAutoLock() == SDKConstants.VALUE.LEAVE_AUTO_LOCK_ON);
        approachUnlock.check(getVM().getManager().getApproachAutoUnlock() == SDKConstants.VALUE.APPROACH_AUTO_UNLOCK_ON);
        if (!isHiddenChanged)
            mIvSettingPic.setImageResource(R.drawable.car_setting_default);// 默认图
    }


    private ComfortVM getVM() {
        if (convenientVM != null) {
            return convenientVM;
        } else {
            return convenientVM = ViewModelProviders.of(this).get(ComfortVM.class).initData();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onSelect(int viewId, int index) {
        KLog.e(TAG, "select index:" + viewId);
        if (viewId == R.id.siv_door_unlock) {//遥控解锁模式
            getVM().setRemoteControlMode(index);
        }
    }

    @Override
    public void onSwitch(int viewId, boolean state) {
        KLog.e(TAG, "onSwitch : " + viewId);
        switch (viewId) {
            case R.id.speed_lock_control://随速闭锁
                getVM().setSpeedLockControl(state);
                break;
            case R.id.leave_car_lock_control://离车自动落锁
                getVM().setLeaveAutomaticLock(state);
                break;
            case R.id.car_windows://锁车自动关窗
                getVM().setSelfClosingWindow(state);
                break;
            case R.id.rearview://后视镜自动折叠
                getVM().setRearviewMirror(state);
                break;
            case R.id.seat://迎宾座椅退让
                getVM().setWelcomeSeat(state);
                break;
            case R.id.trunk://行李箱智能开启
                getVM().setTrunk(state);
                break;
            case R.id.far_lock_view:
                getVM().getManager().setFarAutoLock(state);
                KLog.d(CAR_CONTROL_REQ, "走远自动闭锁: " + state);
                break;
            case R.id.approach_unlock_view:
                getVM().getManager().setApproachAutoUnlock(state);
                KLog.d(CAR_CONTROL_REQ, "走进自动解锁: " + state);
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        KLog.d("hzx", "ComfortFragment 显示情况: " + !hidden);
        if (!hidden) {
            isHiddenChanged = true;
            initData();
        }
    }

    @Override
    public void onChange(int id, Object value) {
        if (id == SDKConstants.REMOTE_CONTROL_UNLOCK_MODE) {
            int i = (int) value == SDKConstants.VALUE.LOCK_ALL_DOOR ? 0 : 1;
            if (i == sivDoorUnlock.getIndex()) {
                return;
            }
            sivDoorUnlock.setCheck(i);
            KLog.d(CAR_CONTROL, "遥控解锁模式: " + i);
            currentSetting = SettingConstants.COMFORT_TELECONTROL_UNLOCK_MODE;
            switchSettingPic(i);
        } else if (id == SDKConstants.SPEED_AUTOMATIC_LOCK) {
            boolean b = (int) value == SDKConstants.VALUE.MIRROR_AUTOMATIC_FOLDING_ON;
            if (b == sivSpeedLock.isCheck())
                return;
            sivSpeedLock.check(b);
            KLog.d(CAR_CONTROL, "随速闭锁: " + b);
            currentSetting = SettingConstants.COMFORT_SPEED_LOCK_CONTROL;
            switchSettingPic(b);
        } else if (id == SDKConstants.LEAVE_CAR_AUTOMATIC_LOCK) {
            boolean b = (int) value == SDKConstants.VALUE.LEAVE_CAR_AUTOMATIC_LOCK_ON;
            if (b == sivLeaveCarLock.isCheck())
                return;
            sivLeaveCarLock.check(b);
            KLog.d(CAR_CONTROL, "离车自动解锁: " + b);
            currentSetting = SettingConstants.COMFORT_EXIT_AUTOMATIC_LOCK;
            switchSettingPic(b);
        } else if (id == SDKConstants.ID_NEAR_DOOR_AUTO_UNLOCK_MODE) {
            boolean b = (int) value == SDKConstants.VALUE.APPROACH_AUTO_UNLOCK_ON;
            if (b == approachUnlock.isCheck())
                return;
            approachUnlock.check(b);
            KLog.d(CAR_CONTROL, "走进自动解锁: " + b);
            currentSetting = SettingConstants.COMFORT_APPROACH_AUTO_UNLOCK;
            switchSettingPic(b);
        } else if (id == SDKConstants.ID_FAR_DOOR_AUTO_LOCK_MODE) {
            boolean b = (int) value == SDKConstants.VALUE.LEAVE_AUTO_LOCK_ON;
            if (b == farLock.isCheck())
                return;
            farLock.check(b);
            KLog.d(CAR_CONTROL_REQ, "走远自动闭锁: " + b);
            currentSetting = SettingConstants.COMFORT_GO_FAR_AUTO_LOCK;
            switchSettingPic(b);
        } else if (id == SDKConstants.SELF_CLOSING_WINDOW) {
            boolean b = (int) value == SDKConstants.VALUE.SELF_CLOSING_WINDOW_ON;
            if (b == sivCarWindow.isCheck())
                return;
            sivCarWindow.check(b);
            KLog.d(CAR_CONTROL, "锁车自动关窗: " + b);
            currentSetting = SettingConstants.COMFORT_LOCK_CAR_WITH_CLOSE_WINDOW;
            switchSettingPic(b);
        } else if (id == SDKConstants.AUTOMATIC_TRUNK) {
            boolean b = (int) value == SDKConstants.VALUE.AUTOMATIC_TRUNK_ON;
            if (b == scvTrunk.isCheck())
                return;
            scvTrunk.check(b);
            KLog.d(CAR_CONTROL, "行李箱智能开启: " + b);
            currentSetting = SettingConstants.COMFORT_TRUNK_AUTO_OPEN;
            switchSettingPic(b);
        } else if (id == SDKConstants.MIRROR_AUTOMATIC_FOLDING) {
            boolean b = (int) value == SDKConstants.VALUE.MIRROR_AUTOMATIC_FOLDING_ON;
            if (b == sivRearview.isCheck())
                return;
            sivRearview.check(b);
            KLog.d(CAR_CONTROL, "锁车外后视镜自动折叠: " + b);
            currentSetting = SettingConstants.COMFORT_DOOR_LOCK_REARVIEW_MIRROR_AUTO_FOLD;
            switchSettingPic(b);
        } else if (id == SDKConstants.WELCOME_SEAT) {
            boolean b = (int) value == SDKConstants.VALUE.WELCOME_SEAT_TURN_ON;
            if (b == sivSeat.isCheck())
                return;
            sivSeat.check(b);
            KLog.d(CAR_CONTROL, "座椅迎宾退让: " + b);
            currentSetting = SettingConstants.COMFORT_SEAT_GIVE_AWAY;
            switchSettingPic(b);
        }
    }

    private void switchSettingPic(boolean open) {
//        KLog.e(wjw, "switchSettingPic");
        switch (currentSetting) {
            case SettingConstants.COMFORT_EXIT_AUTOMATIC_LOCK: // 熄火自动解锁
//                KLog.e(wjw, "熄火自动解锁");
//                KLog.e(wjw, "flag :" + open);
                mIvSettingPic.setImageResource(open ? R.drawable.comfort_exit_automatic_lock_open : R.drawable.comfort_exit_automatic_lock_close);
                break;
            case SettingConstants.COMFORT_DOOR_LOCK_REARVIEW_MIRROR_AUTO_FOLD: // 锁车外后视镜自动折叠
//                KLog.e(wjw, "锁车外后视镜自动折叠");
//                KLog.e(wjw, "flag :" + open);
                mIvSettingPic.setImageResource(open ? R.drawable.comfort_rearview_mirror_fold_open : R.drawable.comfort_rearview_mirror_fold_close);
                break;
            case SettingConstants.COMFORT_SEAT_GIVE_AWAY: // 座椅迎宾退让
//                KLog.e(wjw, "座椅迎宾退让");
//                KLog.e(wjw, "flag :" + open);
                mIvSettingPic.setImageResource(open ? R.drawable.comfort_seat_give_away_open : R.drawable.comfort_seat_give_away_close);
                break;
            case SettingConstants.COMFORT_TRUNK_AUTO_OPEN: // 行李箱智能开启
//                KLog.e(wjw, "行李箱智能开启");
//                KLog.e(wjw, "flag :" + open);
                mIvSettingPic.setImageResource(open ? R.drawable.comfort_trunk_smart_open : R.drawable.comfort_trunk_smart_close);
                break;
            case SettingConstants.COMFORT_GO_FAR_AUTO_LOCK: // 走远自动闭锁
//                KLog.e(wjw, "走远自动闭锁");
//                KLog.e(wjw, "flag :" + open);
                mIvSettingPic.setImageResource(open ? R.drawable.comfort_go_far_auto_lock_open : R.drawable.comfort_go_far_auto_lock_close);
                break;
            case SettingConstants.COMFORT_APPROACH_AUTO_UNLOCK: // 走进自动解锁
//                KLog.e(wjw, "走进自动解锁");
//                KLog.e(wjw, "flag :" + open);
                mIvSettingPic.setImageResource(open ? R.drawable.comfort_approach_auto_unlock_open : R.drawable.comfort_approach_auto_unlock_close);
                break;
            case SettingConstants.COMFORT_LOCK_CAR_WITH_CLOSE_WINDOW: // 锁车自动关窗
//                KLog.e(wjw, "锁车自动关窗");
//                KLog.e(wjw, "flag :" + open);
                mIvSettingPic.setImageResource(open ? R.drawable.comfort_remote_car_window_open : R.drawable.comfort_remote_car_window_close);
                break;
            case SettingConstants.COMFORT_SPEED_LOCK_CONTROL: // 随速闭锁
//                KLog.e(wjw, "随速闭锁");
//                KLog.e(wjw, "flag :" + open);
                mIvSettingPic.setImageResource(open ? R.drawable.comfort_speed_lock_control_open : R.drawable.comfort_speed_lock_control_close);
                break;

        }
    }

    private void switchSettingPic(int index) {
//        KLog.e(wjw, "switchSettingPic");
        switch (currentSetting) {
            case SettingConstants.COMFORT_TELECONTROL_UNLOCK_MODE: // 遥控解锁模式
//                KLog.e(wjw, "遥控解锁模式");
//                KLog.e(wjw, "index :" + index);
                if (index < 0 || index >= telecontrolUnlockImagesArr.length) return;
//                KLog.e(wjw, "设置图片 :" + index);
                mIvSettingPic.setImageResource(telecontrolUnlockImagesArr[index]);
                break;
        }
    }

}
