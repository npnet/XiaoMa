package com.xiaoma.smarthome.scene.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.constants.EventConstants;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.common.manager.CMSceneDataManager;
import com.xiaoma.smarthome.common.model.HomeBean;
import com.xiaoma.smarthome.common.view.CMProgressDialog;
import com.xiaoma.smarthome.common.view.NumberPickerView;
import com.xiaoma.smarthome.scene.adapter.ActionAdapter;
import com.xiaoma.smarthome.scene.model.SceneBean;
import com.xiaoma.smarthome.scene.vm.SettingVM;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;

@PageDescComponent(EventConstants.PageDescribe.SelectSceneActivity)
public class CMSettingFragment extends BaseFragment {
    private static final String SCENE_NAME = "scene_name";
    private static final String SCENE_ID = "scene_id";
    private static final String SCENE_ACTIONS = "scene_actions";
    private static final String SCENE_CONDITION = "scene_condition";
    private Button mBtnCondition;
    private TextView mTvSceneName;
    private Button mBtnExcute;
    private PopupWindow mConditionPop;
    private View mContainer;
    private TextView mTvTitle;
    private Switch mSwitchAuto;
    private EditText mEtCondition;
    private TextView mTvTip;
    private TextView mTvSure;
    private TextView mTvCancel;

    private String[] startArray;
    private static final String[] endArray = {"500米", "1000米", "2000米"};
    private static final int[] distances = {500, 1000, 2000};
    private Dialog mConDialog;
    private NumberPickerView mPickerStart;
    private NumberPickerView mPickerEnd;

    private boolean isBeyond;
    private int distance = distances[0];
    private boolean isAuto;
    private SettingVM mSettingVM;

    private String pickLeftStr;
    private String pickRightStr = endArray[0];
    private RecyclerView mRvActions;
    private ActionAdapter mActionAdapter;
    private String mSceneName;
    private String mSceneId;
    private ArrayList<SceneBean.ActionsBean> mActionsBeans;
    private SceneBean.AutoConditionBean mAutoConditionBean;
    private CMProgressDialog mCmProgressDialog;
    private ConfirmDialog mAdressSettingDialog;

    public static CMSettingFragment newInstance(String sceneName, String sceneId, ArrayList<SceneBean.ActionsBean> actionsBeans,
                                                SceneBean.AutoConditionBean conditionBean) {
        CMSettingFragment cmSettingFragment = new CMSettingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SCENE_NAME, sceneName);
        bundle.putString(SCENE_ID, sceneId);
        bundle.putParcelableList(SCENE_ACTIONS, actionsBeans);
        bundle.putParcelable(SCENE_CONDITION, conditionBean);
        cmSettingFragment.setArguments(bundle);

        return cmSettingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_cm_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initData();
    }

    private void initView(View view) {
        mContainer = view.findViewById(R.id.container);
        mBtnCondition = view.findViewById(R.id.btn_condition);
        mTvSceneName = view.findViewById(R.id.tv_scene_name);
        mBtnExcute = view.findViewById(R.id.btn_excute);
        mRvActions = view.findViewById(R.id.rv_actions);
        mRvActions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mActionAdapter = new ActionAdapter(new ArrayList<SceneBean.ActionsBean>());
        mRvActions.setAdapter(mActionAdapter);
        mBtnCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConditionPop();
            }
        });

        mBtnExcute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.isConnected(getContext())) {
                    XMToast.toastException(getContext(), R.string.no_net_work);
                    return;
                }
                showExcuteProgress();
                mSettingVM.excuteScene(mSceneId);
            }
        });
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mSceneName = bundle.getString(SCENE_NAME);
        mSceneId = bundle.getString(SCENE_ID);
        mActionsBeans = bundle.getParcelableArrayList(SCENE_ACTIONS);
        mAutoConditionBean = bundle.getParcelable(SCENE_CONDITION);
        if (mActionsBeans != null) {
            mActionAdapter.setNewData(mActionsBeans);
        }
        mTvSceneName.setText(mSceneName);
        if (mSceneName.contains("回家")) {
            startArray = new String[]{"小于"};
        } else if (mSceneName.contains("离家")) {
            startArray = new String[]{"大于"};
        } else {
            startArray = new String[]{"小于", "大于"};
        }
        pickLeftStr = startArray[0];
        mSettingVM = ViewModelProviders.of(this).get(SettingVM.class);
        mSettingVM.getXmExcuteScene().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> stringXmResource) {
                stringXmResource.handle(new OnCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        dismissExcuteProgress();
                        showToast(String.format(getString(R.string.already_execute_mode), mSceneName));
                    }

                    @Override
                    public void onError(int code, String message) {
                        dismissExcuteProgress();
                        showToast(R.string.excute_fail);
                    }
                });
            }
        });
        mSettingVM.getXmSettingData().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> stringXmResource) {
                stringXmResource.handle(new OnCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        KLog.d("updatecon success");
                        //通知场景列表页面重新拉取数据
                        EventBus.getDefault().post("", SmartConstants.REFRESH_SCENE_LIST);
                    }

                    @Override
                    public void onError(int code, String message) {
                        KLog.d("updatecon onError: " + message);
                    }
                });
            }
        });
    }

    private void showConditionPop() {
        if (mAutoConditionBean == null) {
            return;
        }
        if (mConditionPop == null) {
            initConditionPop();
        }
        if (mConditionPop.isShowing()) {
            mConditionPop.dismiss();
        } else {
            mConditionPop.showAtLocation(getNaviBar(), Gravity.START, 165, 0);
        }
    }

    @SuppressLint("StringFormatMatches")
    private void initConditionPop() {
        View view = getLayoutInflater().inflate(R.layout.view_scene_condition, null);
        mConditionPop = new PopupWindow(view, 1000, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mConditionPop.setOutsideTouchable(true);
        mConditionPop.setAnimationStyle(R.style.PopAnimTranslate);
        mConditionPop.setClippingEnabled(false);
        mConditionPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mAutoConditionBean.setAutoFlag(isAuto ? SmartConstants.AUTO_EXCUTE_ON : SmartConstants.AUTO_EXCUTE_OFF);
                mAutoConditionBean.setRadius(distance);
                mAutoConditionBean.setRuleFlag(isBeyond ? SmartConstants.BEYOND : SmartConstants.INNER);
                mSettingVM.updateSceneCondition(mAutoConditionBean);
            }
        });
        mTvTitle = view.findViewById(R.id.tv_title);
        mSwitchAuto = view.findViewById(R.id.iv_auto);
        mEtCondition = view.findViewById(R.id.et_condition);
        mTvTip = view.findViewById(R.id.tv_tip);
        mTvTitle.setText(getString(R.string.scene_con_setting, mSceneName));

        isAuto = mAutoConditionBean.getAutoFlag() == SmartConstants.AUTO_EXCUTE_ON;
        if (mSceneName.contains("回家")) {
            isBeyond = false;
        } else if (mSceneName.contains("离家")) {
            isBeyond = true;
        } else {
            isBeyond = mAutoConditionBean.getRuleFlag() == SmartConstants.BEYOND;
        }
        mSwitchAuto.setChecked(isAuto && CMSceneDataManager.getInstance().getHomeBean() != null);

        if (mAutoConditionBean.getRadius() != 0) {
            mTvTip.setTextColor(Color.WHITE);
            mTvTip.setText(String.format(getString(R.string.away_from_home_distance), isBeyond ? "大于" : "小于", mAutoConditionBean.getRadius()));
        } else {
            mTvTip.setTextColor(Color.parseColor("#8a919d"));
            mTvTip.setText(R.string.please_select);
        }
        mSwitchAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                HomeBean homeBean = CMSceneDataManager.getInstance().getHomeBean();
                if (homeBean == null) {
                    mSwitchAuto.setChecked(false);
                    showAddressSettingDialog();
                    return;
                }
                isAuto = isChecked;
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.autoExecuteSwitch,
                        mSceneName, "CMSettingFragment", EventConstants.PageDescribe.SelectSceneActivity);

            }
        });

        mEtCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeBean homeBean = CMSceneDataManager.getInstance().getHomeBean();
                if (homeBean == null) {
                    showAddressSettingDialog();
                    return;
                }
                showDistanceDialog();
            }
        });
    }

    private void showAddressSettingDialog() {
        mAdressSettingDialog = new ConfirmDialog(getActivity());
        mAdressSettingDialog.setContent(getString(R.string.not_home_address_to_set))
                .setPositiveButton(getString(R.string.to_set), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdressSettingDialog.dismiss();
                        mConditionPop.dismiss();
                        //返回场景列表同时打开地址设置弹框
                        if (getActivity() != null) {
                            EventBus.getDefault().post("", SmartConstants.SHOW_SETTING_ADRESS_DIALOG);
                            FragmentUtils.pop(getActivity().getSupportFragmentManager());
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdressSettingDialog.dismiss();
                    }
                })
                .show();
    }

    private void showDistanceDialog() {
        if (mConDialog == null) {
            initConDialog();
        }
        mConDialog.show();
    }

    private void initConDialog() {
        mConDialog = new Dialog(getActivity());
        View view = View.inflate(getContext(), R.layout.view_dialog_distance, null);
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
                if (startArray.length != 1) {
                    isBeyond = newVal == 1;
                }
                pickLeftStr = startArray[newVal];
            }
        });

        mPickerEnd.setDisplayedValues(endArray);
        mPickerEnd.setMinValue(0);
        mPickerEnd.setMaxValue(endArray.length - 1);
        mPickerEnd.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal, int clickDay) {
                distance = distances[newVal];
                pickRightStr = endArray[newVal];
            }
        });

        mTvSure.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                mConDialog.dismiss();
                mTvTip.setText(String.format(getString(R.string.away_from_home_distance), pickLeftStr, distance));
            }
        });
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConDialog.dismiss();
            }
        });
        mConDialog.setContentView(view);
        WindowManager.LayoutParams lp = mConDialog.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = 800;
        lp.height = 500;
        mConDialog.getWindow().setAttributes(lp);
        mConDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    protected void showExcuteProgress() {
        if (mCmProgressDialog == null) {
            mCmProgressDialog = new CMProgressDialog(getActivity());
        }
        mCmProgressDialog.show();
    }

    private void dismissExcuteProgress() {
        if (mCmProgressDialog == null) {
            return;
        }
        mCmProgressDialog.dismiss();
    }
}
