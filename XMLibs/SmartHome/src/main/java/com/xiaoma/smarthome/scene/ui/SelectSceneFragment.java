package com.xiaoma.smarthome.scene.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.miot.common.abstractdevice.AbstractDevice;
import com.viomi.devicelib.manager.DeviceCentre;
import com.viomi.devicelib.manager.inter.IDeviceCallBack;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.mapadapter.ui.MapActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.constants.EventConstants;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.common.manager.CMDeviceManager;
import com.xiaoma.smarthome.common.manager.CMSceneDataManager;
import com.xiaoma.smarthome.common.model.HomeBean;
import com.xiaoma.smarthome.common.view.CMProgressDialog;
import com.xiaoma.smarthome.login.ui.MainActivity;
import com.xiaoma.smarthome.scene.adapter.DeviceAdapter;
import com.xiaoma.smarthome.scene.adapter.SceneAdapter;
import com.xiaoma.smarthome.scene.model.SceneBean;
import com.xiaoma.smarthome.scene.service.SceneAutoExecuteService;
import com.xiaoma.smarthome.scene.vm.SelectSceneVM;
import com.xiaoma.ui.constract.ScrollBarDirection;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;
@PageDescComponent(EventConstants.PageDescribe.SelectSceneActivity)
public class SelectSceneFragment extends BaseFragment implements View.OnClickListener {

    private ImageView ivUser;
    private TextView tvUser;
    private RecyclerView rvScene;
    private XmScrollBar scrollBar;
    private SceneAdapter mSceneAdapter;
    private SelectSceneVM mSelectSceneVM;

    public static final int REQUEST_CODE = 200;
    private String excuteSceneName;
    private ImageView mIvSync;
    private TextView mTvSync;

    private boolean isSyncScene;
    private PopupWindow mAddressPop;
    private View mContainer;
    private Button mBtnSetting;
    private Button mBtnDevices;
    private EditText mEtAddress;
    private PopupWindow mDevicesPop;
    private RecyclerView mRvDevices;
    private DeviceAdapter mDeviceAdapter;
    private List<AbstractDevice> mDevicesList;

    private static final String USER_HEAD = "user_head";
    private static final String USER_NICK = "user_nick";

    private String mUserNick;
    private String mUserHead;
    private String mCmToken;
    private ConfirmDialog mLogoutDialog;
    private CMProgressDialog mCmProgressDialog;

    public static SelectSceneFragment newInstance(String userHead, String userNick) {
        SelectSceneFragment selectSceneFragment = new SelectSceneFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USER_HEAD, userHead);
        bundle.putString(USER_NICK, userNick);
        selectSceneFragment.setArguments(bundle);

        return selectSceneFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_select_scene, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        initView(view);
        initData();
    }

    private void initView(View view) {
        mContainer = view.findViewById(R.id.container);
        ivUser = view.findViewById(R.id.iv_user);
        tvUser = view.findViewById(R.id.tv_user);
        rvScene = view.findViewById(R.id.rv_scene);
        scrollBar = view.findViewById(R.id.scroll_bar);
        mIvSync = view.findViewById(R.id.iv_refresh);
        mTvSync = view.findViewById(R.id.tv_sync);
        mBtnSetting = view.findViewById(R.id.btn_setting);
        mBtnDevices = view.findViewById(R.id.btn_device);

        mIvSync.setOnClickListener(this);
        mTvSync.setOnClickListener(this);
        mBtnSetting.setOnClickListener(this);
        mBtnDevices.setOnClickListener(this);

        rvScene.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mSceneAdapter = new SceneAdapter(new ArrayList<SceneBean>());
        mSceneAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SceneBean item = mSceneAdapter.getData().get(position);
                CMSettingFragment fragment = CMSettingFragment.newInstance(item.getSceneName(), item.getSceneId(), (ArrayList<SceneBean.ActionsBean>) item.getActions(), item.getAutoCondition());
                if (getActivity() != null) {
                    FragmentUtils.replace(getActivity().getSupportFragmentManager(), fragment, R.id.container_frame, MainActivity.FRAGMENT_GAT_YUN_MI_SETTING, true);
                }
            }
        });
        mSceneAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.btn_excute) {
                    //执行场景
                    if (!NetworkUtils.isConnected(getContext())) {
                        XMToast.toastException(getContext(), R.string.no_net_work);
                        return;
                    }
                    showExcuteProgress();
                    SceneBean item = mSceneAdapter.getData().get(position);
                    excuteSceneName = item.getSceneName();
                    mSelectSceneVM.excuteScene(item.getSceneId());
                    XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.selectScene, excuteSceneName,
                            "SelectSceneFragment", EventConstants.PageDescribe.SelectSceneActivity);
                }
            }
        });
        rvScene.setAdapter(mSceneAdapter);
        scrollBar.setRecyclerView(rvScene);
    }

    private void initData() {
        Bundle arg = getArguments();
        if (arg != null) {
            mUserNick = arg.getString(USER_NICK);
            mUserHead = arg.getString(USER_HEAD);
        }

        if (!TextUtils.isEmpty(mUserHead)) {
            ImageLoader.with(this).load(mUserHead).placeholder(R.drawable.icon_user).into(ivUser);
        }
        if (!TextUtils.isEmpty(mUserNick)) {
            tvUser.setText(mUserNick);
        }

        mContext.stopService(new Intent(getContext(), SceneAutoExecuteService.class));

        mSelectSceneVM = ViewModelProviders.of(this).get(SelectSceneVM.class);
        mSelectSceneVM.getXmSceneData().observe(this, new Observer<XmResource<List<SceneBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<SceneBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<SceneBean>>() {
                    @Override
                    public void onSuccess(List<SceneBean> data) {
                        mSceneAdapter.setNewData(data);
                        CMSceneDataManager.getInstance().setData(data);
                        if (isSyncScene) {
                            XMToast.showToast(getContext(), getString(R.string.sync_success), mContext.getDrawable(R.drawable.icon_ok));
                            isSyncScene = false;
                        } else {
                            mContext.startService(new Intent(getContext(), SceneAutoExecuteService.class));
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        if (isSyncScene) {
//                            XMToast.toastException(getContext(), getString(R.string.sync_fail_no_net));
                            isSyncScene = false;
                        } else {
                            showToast("网络异常," + message);
                        }
                    }
                });
            }
        });
        mSelectSceneVM.getXmExcuteScene().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> stringXmResource) {
                stringXmResource.handle(new OnCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        dismissExcuteProgress();
                        showToast(String.format(getString(R.string.already_execute_mode), excuteSceneName));
                    }

                    @Override
                    public void onError(int code, String message) {
                        dismissExcuteProgress();
                        showToast(R.string.excute_fail);
                    }
                });
            }
        });
        mSelectSceneVM.getXmLogout().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> stringXmResource) {
                stringXmResource.handle(new OnCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        //退出登录(清空地址信息，sdk退出登录)
                        dismissProgress();
                        CMDeviceManager.getInstance().logoutCM();
                        CMSceneDataManager.getInstance().setHomeData(null);
                        mContext.stopService(new Intent(getContext(), SceneAutoExecuteService.class));
                        CMSceneDataManager.getInstance().setCmToken(null);
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        dismissProgress();
                        XMToast.toastException(getContext(), "网络异常," + message);
                    }
                });
            }
        });
        mSelectSceneVM.fetchSceneData();

        //搜索设备回调
        DeviceCentre.getInstance().addDeviceCallBack(new IDeviceCallBack() {
            @Override
            public void remoteDevices(List<AbstractDevice> list) {
                KLog.d("remoteDevices list size: " + (!ListUtils.isEmpty(list) ? list.size() : 0));
                mDevicesList = list;
            }
        });
        //获取云米token
        mCmToken = CMSceneDataManager.getInstance().getCmToken();
        //搜索设备
        refreshDevices();
    }

    private void refreshDevices() {
        CMDeviceManager.getInstance().refreshRemoteDevices(mCmToken);
    }

    private void dismissExcuteProgress() {
        if (mCmProgressDialog == null) {
            return;
        }
        mCmProgressDialog.dismiss();
    }

    protected void showExcuteProgress() {
        if (mCmProgressDialog == null) {
            mCmProgressDialog = new CMProgressDialog(getActivity());
        }
        mCmProgressDialog.show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_sync || i == R.id.iv_refresh) {
            //刷新场景数据
            isSyncScene = true;
            refreshDevices();
            mSelectSceneVM.fetchSceneData();
        } else if (i == R.id.btn_setting) {
            //设置(家庭地址)
            showHomeAddressPop();
        } else if (i == R.id.btn_device) {
            //设备状态
            //搜索设备
            refreshDevices();
            showDevicesPop();
        }
    }

    private void showHomeAddressPop() {
        if (mAddressPop == null) {
            initAddressPop();
        }

        HomeBean homeBean = CMSceneDataManager.getInstance().getHomeBean();
        if (homeBean != null) {
            mEtAddress.setText(homeBean.getAddress());
        }

        if (mAddressPop.isShowing()) {
            mAddressPop.dismiss();
        } else {
            mAddressPop.showAtLocation(getNaviBar(), Gravity.START, 165, 0);
        }
    }

    private void initAddressPop() {
        View view = getLayoutInflater().inflate(R.layout.view_cloudmi_address, null);
        mAddressPop = new PopupWindow(view, 1000, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mAddressPop.setOutsideTouchable(true);
        mAddressPop.setAnimationStyle(R.style.PopAnimTranslate);
        mAddressPop.setClippingEnabled(false);
        mEtAddress = view.findViewById(R.id.et_address);
        mEtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳到地图页面选择地址
                startActivityForResult(new Intent(getActivity(), MapActivity.class), REQUEST_CODE);
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.inputAddressEt,
                        "SelectSceneFragment", EventConstants.PageDescribe.SelectSceneActivity);
            }
        });
        view.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
    }

    private void showLogoutDialog() {
        mLogoutDialog = new ConfirmDialog(getActivity());
        mLogoutDialog.setContent(getString(R.string.logout_accout))
                .setPositiveButton(getString(R.string.sure), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLogoutDialog.dismiss();
                        if (NetworkUtils.isConnected(getContext())) {
                            showProgressDialog("");
                            mSelectSceneVM.logout();
                        } else {
                            XMToast.toastException(getContext(), getString(R.string.no_net_work));
                        }
                        if (mAddressPop.isShowing()) {
                            mAddressPop.dismiss();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLogoutDialog.dismiss();
                    }
                }).show();
    }

    private void initDevicesPop() {
        View view = getLayoutInflater().inflate(R.layout.view_devices_pop, null);
        mDevicesPop = new PopupWindow(view, 1100, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mDevicesPop.setOutsideTouchable(true);
        mDevicesPop.setAnimationStyle(R.style.PopAnimTranslate);
        mDevicesPop.setClippingEnabled(false);
        view.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDevicesPop != null && mDevicesPop.isShowing()) {
                    mDevicesPop.dismiss();
                }
            }
        });
        mRvDevices = view.findViewById(R.id.rv_device);
        XmScrollBar xmScrollBar = view.findViewById(R.id.scroll_bar);
        mRvDevices.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mDeviceAdapter = new DeviceAdapter(new ArrayList<>());
        mDeviceAdapter.setEmptyView(R.layout.view_device_empty, mRvDevices);
        mRvDevices.setAdapter(mDeviceAdapter);
        xmScrollBar.setDirection(ScrollBarDirection.DIRECTION_VERTICAL);
        xmScrollBar.setRecyclerView(mRvDevices);
    }

    private void showDevicesPop() {
        if (mDevicesPop == null) {
            initDevicesPop();
        }
        mDeviceAdapter.setNewData(mDevicesList);
        if (mDevicesPop.isShowing()) {
            mDevicesPop.dismiss();
        } else {
            mDevicesPop.showAtLocation(mContainer, Gravity.START, 0, 0);
        }
    }

    @Subscriber(tag = SmartConstants.SHOW_SETTING_ADRESS_DIALOG)
    public void showAdressSettingDialog(String data) {
        //设置(家庭地址)
        showHomeAddressPop();
    }

    @Subscriber(tag = SmartConstants.REFRESH_SCENE_LIST)
    public void refreshList(String data) {
        mSelectSceneVM.fetchSceneData();
    }

    @Subscriber(tag = SmartConstants.REFRESH_SCENE_LIST_TAT)
    public void refreshListIat(List<SceneBean> data) {
        isSyncScene = true;
        mSceneAdapter.setNewData(data);
        CMSceneDataManager.getInstance().setData(data);
        XMToast.showToast(getContext(), getString(R.string.sync_success), getContext().getDrawable(R.drawable.icon_ok));
        isSyncScene = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == REQUEST_CODE) {
            SearchAddressInfo searchAddressInfo = data.getParcelableExtra(MapActivity.EXTRA_LOCATION_DATA);
            HomeBean homeBean = new HomeBean();
            homeBean.setAddress(searchAddressInfo.title);
            homeBean.setLatitude(searchAddressInfo.latLonPoint.getLatitude());
            homeBean.setLongitude(searchAddressInfo.latLonPoint.getLongitude());
            CMSceneDataManager.getInstance().setHomeData(homeBean);

            mEtAddress.setText(searchAddressInfo.title);
            XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.setAddress, searchAddressInfo.provinceCity + searchAddressInfo.title,
                    "SelectSceneFragment", EventConstants.PageDescribe.SelectSceneActivity);
        }
    }
}
