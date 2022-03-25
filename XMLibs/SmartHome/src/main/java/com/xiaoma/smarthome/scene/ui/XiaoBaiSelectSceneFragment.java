package com.xiaoma.smarthome.scene.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.mapadapter.ui.MapActivity;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.constants.EventConstants;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.common.manager.ISmartHomeManager;
import com.xiaoma.smarthome.common.manager.SmartHomeManager;
import com.xiaoma.smarthome.common.model.AccountRecord;
import com.xiaoma.smarthome.common.model.ExecuteCondition;
import com.xiaoma.smarthome.common.model.LocalDeviceInfo;
import com.xiaoma.smarthome.common.view.NumberPickerView;
import com.xiaoma.smarthome.scene.adapter.XiaoBaiSceneAdapter;
import com.xiaoma.smarthome.scene.service.XiaoBaiSceneAutoExecuteService;
import com.xiaoma.smarthome.scene.view.ClickButtonView;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.tputils.TPUtils;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 公子小白场景列表页面
 */
@PageDescComponent(EventConstants.PageDescribe.SelectSceneActivity)
public class XiaoBaiSelectSceneFragment extends BaseFragment implements View.OnClickListener, ISmartHomeManager.OnGetAllDevicesCallback {
    private TextView tvUser;
    private Button btnLoginOut;
    private RecyclerView rvScene;
    private XmScrollBar scrollBar;
    private XiaoBaiSceneAdapter mAdapter;
    //退出登录弹框
    private ConfirmDialog mLogoutDialog;
    //时间执行条件弹框
    private Dialog mTimeConDialog;
    //距离执行条件弹框
    private Dialog mDistanceDialog;
    //场景条件设置pop window
    private PopupWindow mSceneSettingPop;

    private EditText mEtAddress;
    private TextView mSettingPopTitle;
    public static final int REQUEST_CODE = 200;
    //当前场景
    private LocalDeviceInfo mLocalDeviceInfo;

    private Switch mSwitchAuto;

    //是否自动执行
    private boolean isAuto;
    private EditText mEtExecuteCondition;

    private NumberPickerView mPickerStart;
    private NumberPickerView mPickerEnd;
    private TextView mTvSure;
    private TextView mTvCancel;
    private String[] startArray = new String[24];
    private String[] endArray = new String[25];
    private String pickLeftStr;
    private String pickRightStr;

    private ClickButtonView viewDistance1;
    private ClickButtonView viewDistance2;
    private ClickButtonView viewDistance3;
    private ClickButtonView viewDistance4;
    private ClickButtonView viewDistance5;
    private ClickButtonView viewDistance6;
    private ExecuteCondition mExecuteCondition;
    //选择的距离
    private int mSelectDistance;
    private int[] distants = {500, 1000, 2000, 3000, 4000, 5000};

    public static XiaoBaiSelectSceneFragment newInstance() {
        return new XiaoBaiSelectSceneFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_xiaobai_select_scene, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initData();
    }

    private void initView(View view) {
        tvUser = view.findViewById(R.id.tv_user);
        btnLoginOut = view.findViewById(R.id.btn_login_out);
        rvScene = view.findViewById(R.id.rv_scene);
        scrollBar = view.findViewById(R.id.scroll_bar);

        btnLoginOut.setOnClickListener(this);

        AccountRecord accountRecord = TPUtils.getObject(getContext(), SmartConstants.KEY_LOGIN_ACCOUNT, AccountRecord.class);
        if (accountRecord != null) {
            tvUser.setText(accountRecord.account);
        }
        rvScene.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new XiaoBaiSceneAdapter(new ArrayList<LocalDeviceInfo>());
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!NetworkUtils.isConnected(getContext())) {
                    XMToast.toastException(getContext(), mContext.getString(R.string.no_net_work));
                    return;
                }
                LocalDeviceInfo item = mAdapter.getData().get(position);
                SmartHomeManager.getInstance().sceneControl(item.getDeviceName());
                XMToast.showToast(getContext(), getString(R.string.scene_do_success, item.getDeviceName()));
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.selectScene, item.getDeviceName(),
                        "XiaoBaiSelectSceneFragment", EventConstants.PageDescribe.SelectSceneActivity);
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.btn_excute) {
                    LocalDeviceInfo item = mAdapter.getData().get(position);
                    mLocalDeviceInfo = item;
                    //设置执行条件
                    showSceneSettingPop(item);
                }
            }
        });
        rvScene.setAdapter(mAdapter);
        scrollBar.setRecyclerView(rvScene);
    }

    private void initData() {
        SmartHomeManager.getInstance().registerGetAllDevicesCallback(this);
        showProgressDialog(com.xiaoma.component.R.string.base_loading);
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                SmartHomeManager.getInstance().getAllDeviceList();
            }
        }, 1000);
        //打开自动执行服务
        if (getContext() != null) {
            getContext().startService(new Intent(getContext(), XiaoBaiSceneAutoExecuteService.class));
        }
    }

    /**
     * 显示场景条件设置pop window
     *
     * @param item
     */
    private void showSceneSettingPop(LocalDeviceInfo item) {
        SearchAddressInfo addressInfo;
        addressInfo = TPUtils.getObject(getContext(), SmartConstants.KEY_ARRIVE_ADDRESS_INFO, SearchAddressInfo.class);
        if (isArriveMode()) {
            isAuto = TPUtils.get(getContext(), SmartConstants.KEY_ARRIVE_XIAOBAI_IS_AUTO, false);
            mExecuteCondition = TPUtils.getObject(getContext(), SmartConstants.KEY_ARRIVE_EXECUTE_CONDITION, ExecuteCondition.class);
        } else {
            isAuto = TPUtils.get(getContext(), SmartConstants.KEY_LEAVE_XIAOBAI_IS_AUTO, false);
            mExecuteCondition = TPUtils.getObject(getContext(), SmartConstants.KEY_LEAVE_EXECUTE_CONDITION, ExecuteCondition.class);
        }

        String sceneType = item.getDeviceName().substring(0, 4);
        initSceneSettingPop(sceneType);
        mSettingPopTitle.setText(getString(R.string.scene_setting, item.getDeviceName()));

        if (addressInfo != null) {
            mEtAddress.setText(addressInfo.provinceCity + addressInfo.title);
        }

        if (mExecuteCondition == null) {
            mExecuteCondition = new ExecuteCondition("0:00", "24:00", distants[0]);
        }
        pickLeftStr = mExecuteCondition.getStartTime();
        pickRightStr = mExecuteCondition.getEndTime();
        mSelectDistance = mExecuteCondition.getDistance();
        if (isArriveMode()) {
            mEtExecuteCondition.setText(getString(R.string.arrive_condition, mExecuteCondition.getDistance(), mExecuteCondition.getStartTime(),
                    mExecuteCondition.getEndTime()));

        } else {
            mEtExecuteCondition.setText(getString(R.string.leave_condition, mExecuteCondition.getDistance(), mExecuteCondition.getStartTime(),
                    mExecuteCondition.getEndTime()));
        }

        if (mSceneSettingPop.isShowing()) {
            mSceneSettingPop.dismiss();

        } else {
            mSceneSettingPop.showAtLocation(getNaviBar(), Gravity.START, 165, 0);
        }
    }

    /**
     * 初始化场景条件设置pop window
     */
    private void initSceneSettingPop(final String sceneType) {
        View view = getLayoutInflater().inflate(R.layout.view_xiaobai_scene_setting, null);
        mSceneSettingPop = new PopupWindow(view, 1000, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mSceneSettingPop.setOutsideTouchable(true);
        mSceneSettingPop.setAnimationStyle(R.style.PopAnimTranslate);
        mSceneSettingPop.setClippingEnabled(false);
        mEtAddress = view.findViewById(R.id.et_address);
        mEtExecuteCondition = view.findViewById(R.id.et_execute_condition);
        mSettingPopTitle = view.findViewById(R.id.tv_setting_title);
        mSwitchAuto = view.findViewById(R.id.auto_switch);
        mSwitchAuto.setChecked(isAuto);

        mSwitchAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //打开自动执行时,判断地址是否存在
                if (isChecked && StringUtil.isEmpty(mEtAddress.getText().toString())) {
                    mEtAddress.setBackground(getContext().getDrawable(R.drawable.inputbox_dis));
                    mEtAddress.setHintTextColor(Color.RED);
                    mSwitchAuto.setChecked(false);
                    return;
                }
                //重新设置自动执行后，通知围栏服务更新
                EventBus.getDefault().post("", SmartConstants.REFRESH_XIAO_BAI_SCENE);

                isAuto = isChecked;
                mLocalDeviceInfo.setAuto(isAuto);
                mAdapter.notifyDataSetChanged();
                if (isArriveMode()) {
                    TPUtils.put(getContext(), SmartConstants.KEY_ARRIVE_XIAOBAI_IS_AUTO, isAuto);
                } else {
                    TPUtils.put(getContext(), SmartConstants.KEY_LEAVE_XIAOBAI_IS_AUTO, isAuto);
                }
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.autoExecuteSwitch,
                        mLocalDeviceInfo.getDeviceName(), "XiaoBaiSelectSceneFragment", EventConstants.PageDescribe.SelectSceneActivity);
            }
        });
        mEtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳到地图页面选择地址
                startActivityForResult(new Intent(getActivity(), MapActivity.class), REQUEST_CODE);
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.inputAddressEt,
                        "XiaoBaiSelectSceneFragment", EventConstants.PageDescribe.SelectSceneActivity);
            }
        });
        mEtExecuteCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDistanceDialog();
            }
        });
    }

    /**
     * 距离设置弹框
     */
    private void showDistanceDialog() {
        if (mDistanceDialog == null) {
            initDistanceDialog();
        }
        setBtnSelected();
        mDistanceDialog.show();
    }

    /**
     * 距离条件设置
     */
    private void initDistanceDialog() {
        mDistanceDialog = new Dialog(getActivity());
        View view = View.inflate(getContext(), R.layout.layout_conditional_distance_setting, null);
        viewDistance1 = view.findViewById(R.id.btn_distant1);
        viewDistance2 = view.findViewById(R.id.btn_distant2);
        viewDistance3 = view.findViewById(R.id.btn_distant3);
        viewDistance4 = view.findViewById(R.id.btn_distant4);
        viewDistance5 = view.findViewById(R.id.btn_distant5);
        viewDistance6 = view.findViewById(R.id.btn_distant6);
        viewDistance1.setOnClickListener(this);
        viewDistance2.setOnClickListener(this);
        viewDistance3.setOnClickListener(this);
        viewDistance4.setOnClickListener(this);
        viewDistance5.setOnClickListener(this);
        viewDistance6.setOnClickListener(this);

        mDistanceDialog.setContentView(view);
        WindowManager.LayoutParams lp = mDistanceDialog.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = 684;
        lp.height = 430;
        mDistanceDialog.getWindow().setAttributes(lp);
        mDistanceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * 设置选中的距离
     */
    private void setBtnSelected() {
        int distance;
        if (mExecuteCondition == null) {
            distance = distants[0];

        } else {
            distance = mSelectDistance;
        }
        for (int i = 0; i < distants.length; i++) {
            if (distance == distants[i]) {
                switch (i) {
                    case 0:
                        viewDistance1.setSelected(true);
                        viewDistance2.setSelected(false);
                        viewDistance3.setSelected(false);
                        viewDistance4.setSelected(false);
                        viewDistance5.setSelected(false);
                        viewDistance6.setSelected(false);
                        break;

                    case 1:
                        viewDistance2.setSelected(true);
                        viewDistance1.setSelected(false);
                        viewDistance3.setSelected(false);
                        viewDistance4.setSelected(false);
                        viewDistance5.setSelected(false);
                        viewDistance6.setSelected(false);
                        break;

                    case 2:
                        viewDistance3.setSelected(true);
                        viewDistance1.setSelected(false);
                        viewDistance2.setSelected(false);
                        viewDistance4.setSelected(false);
                        viewDistance5.setSelected(false);
                        viewDistance6.setSelected(false);
                        break;

                    case 3:
                        viewDistance4.setSelected(true);
                        viewDistance1.setSelected(false);
                        viewDistance2.setSelected(false);
                        viewDistance3.setSelected(false);
                        viewDistance5.setSelected(false);
                        viewDistance6.setSelected(false);
                        break;

                    case 4:
                        viewDistance5.setSelected(true);
                        viewDistance1.setSelected(false);
                        viewDistance2.setSelected(false);
                        viewDistance3.setSelected(false);
                        viewDistance4.setSelected(false);
                        viewDistance6.setSelected(false);
                        break;

                    case 5:
                        viewDistance6.setSelected(true);
                        viewDistance1.setSelected(false);
                        viewDistance2.setSelected(false);
                        viewDistance3.setSelected(false);
                        viewDistance4.setSelected(false);
                        viewDistance5.setSelected(false);
                        break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login_out) {
            showLogoutDialog();

        } else if (v.getId() == R.id.btn_distant1) {
            viewDistance1.setSelected(true);
            mDistanceDialog.dismiss();
            putExecuteConditionShared(distants[0]);

        } else if (v.getId() == R.id.btn_distant2) {
            viewDistance2.setSelected(true);
            mDistanceDialog.dismiss();
            putExecuteConditionShared(distants[1]);

        } else if (v.getId() == R.id.btn_distant3) {
            viewDistance3.setSelected(true);
            mDistanceDialog.dismiss();
            putExecuteConditionShared(distants[2]);

        } else if (v.getId() == R.id.btn_distant4) {
            viewDistance4.setSelected(true);
            mDistanceDialog.dismiss();
            putExecuteConditionShared(distants[3]);

        } else if (v.getId() == R.id.btn_distant5) {
            viewDistance5.setSelected(true);
            mDistanceDialog.dismiss();
            putExecuteConditionShared(distants[4]);

        } else if (v.getId() == R.id.btn_distant6) {
            viewDistance6.setSelected(true);
            mDistanceDialog.dismiss();
            putExecuteConditionShared(distants[5]);
        }
    }

    /**
     * 退出登录弹框
     */
    private void showLogoutDialog() {
        mLogoutDialog = new ConfirmDialog(getActivity());
        mLogoutDialog.setContent(getString(R.string.logout_accout))
                .setPositiveButton(getString(R.string.sure), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLogoutDialog.dismiss();
                        SmartHomeManager.getInstance().loginOut();
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLogoutDialog.dismiss();
                    }
                });
        mLogoutDialog.show();
    }

    /**
     * 保存条件设置
     *
     * @param distance
     */
    private void putExecuteConditionShared(int distance) {
        mSelectDistance = distance;
        mExecuteCondition.setDistance(distance);
        if (isArriveMode()) {
            TPUtils.putObject(getContext(), SmartConstants.KEY_ARRIVE_EXECUTE_CONDITION, mExecuteCondition);
            mEtExecuteCondition.setText(getString(R.string.arrive_condition, distance, mExecuteCondition.getStartTime(), mExecuteCondition.getEndTime()));

        } else {
            TPUtils.putObject(getContext(), SmartConstants.KEY_LEAVE_EXECUTE_CONDITION, mExecuteCondition);
            mEtExecuteCondition.setText(getString(R.string.leave_condition, distance, mExecuteCondition.getStartTime(), mExecuteCondition.getEndTime()));
        }
        //重新设置条件后，通知围栏服务更新
        EventBus.getDefault().post("", SmartConstants.REFRESH_XIAO_BAI_SCENE);
        //显示时间弹框
        showTimeConditionDialog();
    }

    private void showTimeConditionDialog() {
        if (mTimeConDialog == null) {
            initTimeConDialog();
        }
        String startTime = mExecuteCondition.getStartTime();
        String endTime = mExecuteCondition.getEndTime();
        mPickerStart.setValue(Integer.parseInt(startTime.split(":")[0]));
        mPickerEnd.setValue(Integer.parseInt(endTime.split(":")[0]));
        mTimeConDialog.show();
    }

    /**
     * 时间设置弹框
     */
    private void initTimeConDialog() {
        for (int i = 0; i < 24; i++) {
            startArray[i] = String.valueOf(i);
            endArray[i] = String.valueOf(i);
        }
        endArray[24] = String.valueOf(24);

        mTimeConDialog = new Dialog(getActivity());
        View view = View.inflate(getContext(), R.layout.view_dialog_time_condition, null);
        mPickerStart = view.findViewById(R.id.wheel_start);
        mPickerEnd = view.findViewById(R.id.wheel_end);
        mTvSure = view.findViewById(R.id.tv_sure);
        mTvCancel = view.findViewById(R.id.tv_cancel);

        mPickerStart.setDisplayedValues(startArray);
        mPickerStart.setMinValue(0);
        mPickerStart.setMaxValue(startArray.length - 1);
        mPickerStart.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal, int clickDay) {
                pickLeftStr = startArray[newVal] + ":00";
            }
        });

        mPickerEnd.setDisplayedValues(endArray);
        mPickerEnd.setMinValue(0);
        mPickerEnd.setMaxValue(endArray.length - 1);
        mPickerEnd.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal, int clickDay) {
                pickRightStr = endArray[newVal] + ":00";
            }
        });

        mTvSure.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                int left = Integer.parseInt(pickLeftStr.split(":")[0]);
                int right = Integer.parseInt(pickRightStr.split(":")[0]);
                if (left > right || left == right) {
                    XMToast.showToast(getContext(), getString(R.string.selecte_time_error));
                    return;
                }
                //重新设置条件后，通知围栏服务更新
                EventBus.getDefault().post("", SmartConstants.REFRESH_XIAO_BAI_SCENE);

                mTimeConDialog.dismiss();
                mExecuteCondition.setStartTime(pickLeftStr);
                mExecuteCondition.setEndTime(pickRightStr);
                if (isArriveMode()) {
                    TPUtils.putObject(getContext(), SmartConstants.KEY_ARRIVE_EXECUTE_CONDITION, mExecuteCondition);
                    mEtExecuteCondition.setText(getString(R.string.arrive_condition, mSelectDistance, pickLeftStr, pickRightStr));

                } else {
                    TPUtils.putObject(getContext(), SmartConstants.KEY_LEAVE_EXECUTE_CONDITION, mExecuteCondition);
                    mEtExecuteCondition.setText(getString(R.string.leave_condition, mSelectDistance, pickLeftStr, pickRightStr));
                }
            }
        });
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimeConDialog.dismiss();
            }
        });
        mTimeConDialog.setContentView(view);
        WindowManager.LayoutParams lp = mTimeConDialog.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = 654;
        lp.height = 440;
        mTimeConDialog.getWindow().setAttributes(lp);
        mTimeConDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onSuccess(List<LocalDeviceInfo> deviceInfos) {
        dismissProgress();
        //过滤掉名字为空的item
        if (!ListUtils.isEmpty(deviceInfos)){
            Iterator<LocalDeviceInfo> iterator = deviceInfos.iterator();
            while (iterator.hasNext()) {
                LocalDeviceInfo next = iterator.next();
                if (StringUtil.isEmpty(next.getDeviceName())) {
                    iterator.remove();
                }
            }
        }
        mAdapter.setNewData(deviceInfos);
    }

    @Override
    public void onFail(int errorCode, String errorMsg) {
        dismissProgress();
        XMToast.toastException(getContext(), "onFail:" + errorMsg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SmartHomeManager.getInstance().unregisterGetAllDevicesCallback();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            SearchAddressInfo searchAddressInfo = data.getParcelableExtra(MapActivity.EXTRA_LOCATION_DATA);
            //保存地址
            TPUtils.putObject(getContext(), SmartConstants.KEY_ARRIVE_ADDRESS_INFO, searchAddressInfo);
            String address = searchAddressInfo.provinceCity + searchAddressInfo.title;
            mEtAddress.setText(address);
            mEtAddress.setBackground(getContext().getDrawable(R.drawable.bg_inputbox));
            mEtAddress.setHintTextColor(Color.WHITE);
            //重新设置家庭地址后，通知围栏服务更新
            EventBus.getDefault().post("", SmartConstants.REFRESH_XIAO_BAI_SCENE);
            XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.setAddress, address,
                    "XiaoBaiSelectSceneFragment", EventConstants.PageDescribe.SelectSceneActivity);
        }
    }

    /**
     * 是否回家模式
     */
    private boolean isArriveMode() {
        if (mLocalDeviceInfo == null || mLocalDeviceInfo.getDeviceName() == null) {
            return false;
        }

        return mLocalDeviceInfo.getDeviceName().contains(SmartConstants.SCENE_ARRIVE_HOME);
    }
}
