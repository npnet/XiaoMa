package com.xiaoma.setting.main.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.setting.R;
import com.xiaoma.setting.assistant.ui.AssistantSettingFragment;
import com.xiaoma.setting.bluetooth.ui.BlueToothSettingFragment;
import com.xiaoma.setting.car.ui.CarSettingFragment;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.constant.SettingConstants;
import com.xiaoma.setting.common.ui.TestActivity;
import com.xiaoma.setting.common.utils.FragmentUtils;
import com.xiaoma.setting.main.adapter.MenuAdapter;
import com.xiaoma.setting.main.vm.SettingMenuVM;
import com.xiaoma.setting.other.ui.LanguageFragment;
import com.xiaoma.setting.other.ui.OtherSettingFragment;
import com.xiaoma.setting.sound.ui.SoundEffectsSettingFragment;
import com.xiaoma.setting.wifi.ui.WifiSettingFragment;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import java.util.ArrayList;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.mainActivityPagePathDesc)
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private static final int DEF_PAGE = 0;
    long preClickTime;
    private RecyclerView menuRv;
    private FragmentManager fManager;
    private MenuAdapter.IMenuClickListener menuClickListener;
    private MenuAdapter menuAdapter;
    private SettingMenuVM settingMenuVM;
    private int prePosition = 0;
    private List<Fragment> fragments = new ArrayList<>();
    private List<Integer> darkDoorData = new ArrayList<>();
    private int mUserType = SettingConstants.COMMON_USER;
    private BlueToothSettingFragment btSettingFragment;
    private WifiSettingFragment wifiSettingFragment;
    private SoundEffectsSettingFragment soundEffectsSettingFragment;
    private CarSettingFragment carSettingFragment;
    private OtherSettingFragment otherSettingFragment;
    private AssistantSettingFragment assistantSettingFragment;
    private boolean isUserLogin = true;

    public static void switchFragment(FragmentManager fManager, int containerViewId, Fragment from, Fragment to) {
        FragmentTransaction transaction = fManager.beginTransaction();
        if (!to.isAdded()) {
            transaction.hide(from);
//            transaction.add(containerViewId, to, to.getTag());
            transaction.add(containerViewId, to, to.getClass().getName());
            transaction.show(to).commit();
        } else {
            transaction.hide(from).show(to).commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setBackgroundDrawableResource(R.drawable.bg_common);
        setContentView(R.layout.activity_main);
        /*if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            if (fManager == null) {
                fManager = getSupportFragmentManager();
            }
            List<Fragment> fragments = fManager.getFragments();
            if (fragments != null && fragments.size() != 0) {
                for (Fragment fragment : fragments) {
                    identifyFragment(fragment);
                }
            }
        }*/
        initView(savedInstanceState);
    }

    private void reCheckLogin() {
        boolean userLogin = LoginManager.getInstance().isUserLogin();
        int page = 0;
        if (userLogin != isUserLogin) {
            //登录状态check 恢复界面
            this.isUserLogin = userLogin;
            if (fragments.size() > 1 && prePosition != page) {
                switchMenu(prePosition, page);
                prePosition = page;
                menuAdapter.setCheckItem(page);
            }
        }
    }

    /*private void identifyFragment(Fragment fragment) {
        if (fragment instanceof BlueToothSettingFragment) {
            btSettingFragment = (BlueToothSettingFragment) fragment;
        } else if (fragment instanceof WifiSettingFragment) {
            wifiSettingFragment = (WifiSettingFragment) fragment;
        } else if (fragment instanceof SoundEffectsSettingFragment) {
            soundEffectsSettingFragment = (SoundEffectsSettingFragment) fragment;
        } else if (fragment instanceof CarSettingFragment) {
            carSettingFragment = (CarSettingFragment) fragment;
        } else if (fragment instanceof OtherSettingFragment) {
            otherSettingFragment = (OtherSettingFragment) fragment;
        } else if (fragment instanceof AssistantSettingFragment) {
            assistantSettingFragment = (AssistantSettingFragment) fragment;
        }
        if (fManager == null) {
            fManager = getSupportFragmentManager();
        }
        fManager.beginTransaction().hide(fragment).commit();
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        reCheckLogin();
        AppUpdateCheck.getInstance().checkAppUpdate(getPackageName(), getApplication());
    }

    public void initView(Bundle savedInstanceState) {
        isUserLogin = LoginManager.getInstance().isUserLogin();
        menuRv = findViewById(R.id.rv);
        fManager = getSupportFragmentManager();
        initFragment();
        if (savedInstanceState != null) {
            prePosition = savedInstanceState.getInt("position", DEF_PAGE);
        }
        menuAdapter = new MenuAdapter(this, mUserType, prePosition);
        menuClickListener = new MenuAdapter.IMenuClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!canUse(position)) return;
                menuAdapter.setSelectIndex(position);
                switchMenu(prePosition, position);
                prePosition = position;
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        };
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        menuRv.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        menuRv.setAdapter(menuAdapter);
        menuAdapter.setItemClickListener(menuClickListener);
        initData();
    }

    private boolean canUse(int position) {
        try {
            Resources res = getResources();
            String[] city = res.getStringArray(R.array.setting_menu);
            if (city == null || city.length <= 0) return true;
            boolean result = true;
            if (city[position].equals(getString(R.string.sound_settings))) {//音效设置
                result = LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.SETTING_SOUND_SETTINGS);
            }
            if (city[position].equals(getString(R.string.vehicle_settings))) {//车辆设置
                result = LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.SETTING_VEHICLE_SETTING);
            }
            if (city[position].equals(getString(R.string.general_settings))) {//通用设置
                result = LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.SETTING_GENERAL_SETTINGS);
            }
            if (!result) {
                XMToast.showToast(this, LoginTypeManager.getPrompt(MainActivity.this));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", prePosition);
    }

    private void initFragment() {
       /* fragments.clear();
        if (btSettingFragment == null) {
            btSettingFragment = new BlueToothSettingFragment();
        }
        fragments.add(btSettingFragment);

        if (wifiSettingFragment == null) {
            wifiSettingFragment = new WifiSettingFragment();
        }
        fragments.add(wifiSettingFragment);

        if (soundEffectsSettingFragment == null) {
            soundEffectsSettingFragment = new SoundEffectsSettingFragment();
        }
        fragments.add(soundEffectsSettingFragment);

        if (carSettingFragment == null) {
            carSettingFragment = new CarSettingFragment();
        }
        fragments.add(carSettingFragment);

        if (otherSettingFragment == null) {
            otherSettingFragment = new OtherSettingFragment();
        }
        fragments.add(otherSettingFragment);

        if (assistantSettingFragment == null) {
            assistantSettingFragment = new AssistantSettingFragment();
        }
        fragments.add(assistantSettingFragment);*/
        btSettingFragment = (BlueToothSettingFragment) FragmentUtils.newFragmentInstance(fManager, BlueToothSettingFragment.class.getName());
        fragments.add(btSettingFragment);

        if (XmCarConfigManager.isShowWifiAboutItem()) {
            wifiSettingFragment = (WifiSettingFragment) FragmentUtils.newFragmentInstance(fManager, WifiSettingFragment.class.getName());
            fragments.add(wifiSettingFragment);
        }

        soundEffectsSettingFragment = (SoundEffectsSettingFragment) FragmentUtils.newFragmentInstance(fManager, SoundEffectsSettingFragment.class.getName());
        fragments.add(soundEffectsSettingFragment);

        carSettingFragment = (CarSettingFragment) FragmentUtils.newFragmentInstance(fManager, CarSettingFragment.class.getName());
        fragments.add(carSettingFragment);

        otherSettingFragment = (OtherSettingFragment) FragmentUtils.newFragmentInstance(fManager, OtherSettingFragment.class.getName());
        fragments.add(otherSettingFragment);

        assistantSettingFragment = (AssistantSettingFragment) FragmentUtils.newFragmentInstance(fManager, AssistantSettingFragment.class.getName());
        fragments.add(assistantSettingFragment);
    }

    private void initData() {
        settingMenuVM = ViewModelProviders.of(this).get(SettingMenuVM.class).initData(this);
        List<String> value = settingMenuVM.getMenuTexts().getValue();
        final List<String> list = deleteWifiIfNeed(value);
        menuAdapter.setData(list);
        settingMenuVM.getMenuTexts().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                if (strings != null) {
                    List<String> list= listToArrayList(strings);
                    menuAdapter.setData(deleteWifiIfNeed(list));
                }
                menuAdapter.notifyDataSetChanged();
            }
        });
        menuAdapter.notifyDataSetChanged();

        switchMenu(DEF_PAGE, prePosition);

    }

    private List<String> deleteWifiIfNeed(List<String> value) {
        final List<String> list = listToArrayList(value);
        if (!XmCarConfigManager.isShowWifiAboutItem() && list.get(1).equals("WIFI设置")) {
            list.remove(list.get(1));
        }
        return list;
    }

    private List<String> listToArrayList(List<String> values) {
        List<String> list = new ArrayList<>();
        for (String value: values) {
            list.add(value);
        }
        return list;
    }

    public void switchMenu(int from, int to) {
        controlDoor(to);
        int viewSize = fragments.size();
        if (to < 0 || from < 0 || to >= viewSize || from > viewSize || fManager == null) {
            KLog.e(TAG, "switchMenu error:" + to + " fManager is null:" + (fManager == null));
            return;
        }
        KLog.e("switchMenu error:" + to + " fManager is null:" + (fManager == null));
        Fragment fromFragment = fragments.get(from);
        Fragment toFragment = fragments.get(to);
        switchFragment(fManager, R.id.fragment_container, fromFragment, toFragment);
        sendBroadcast(new Intent(LanguageFragment.ACTION_UPDATE_TIME));
    }

    private void controlDoor(int index) {
        if (makeDoor(index)) {
            startActivity(TestActivity.class);
            return;
        }
    }

    private synchronized boolean makeDoor(int to) {
        if (SettingConstants.DOOR_POWER == false) {
            return false;
        }
        try {
            long clickTime = System.currentTimeMillis();
            if (preClickTime != 0 && (clickTime - preClickTime) > SettingConstants.DOOR_INTERVALS) {
                darkDoorData = new ArrayList<>();
                preClickTime = 0;
            }
            darkDoorData.add(to);
            if (darkDoorData.size() != SettingConstants.DOOR_PASSWORD.length) {
                preClickTime = clickTime;
                return false;
            }
            for (int i = 0; i < SettingConstants.DOOR_PASSWORD.length; i++) {
                if (SettingConstants.DOOR_PASSWORD[i] != darkDoorData.get(i)) {
                    preClickTime = 0;
                    return false;
                }
            }
            preClickTime = 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean handleJump(String nextNode) {
        KLog.d("hzx", "mainactivity nextNode: " + nextNode);
        if (!TextUtils.isEmpty(nextNode)) {
            if (nextNode.equals(NodeConst.Setting.ASSISTANT_FRAGMENT)) {
                switchFragment(assistantSettingFragment);
                return true;
            } else if (nextNode.equals(NodeConst.Setting.BLUETOOTH_CONNECT_FRAGMENT)) {
                switchFragment(btSettingFragment);
                btSettingFragment.connectBluetooth();
                return true;
            } else if (nextNode.equals(NodeConst.Setting.BLUETOOTH_SETTINGS)) {
                switchFragment(btSettingFragment);
                return true;
            }else if (nextNode.equals(NodeConst.Setting.WIFI_SETTINGS)){
                switchFragment(wifiSettingFragment);
                return true;
            }
        }
        return super.handleJump(nextNode);
    }

    void switchFragment(Fragment fragment) {
        int fragmentIndex = getFragmentIndex(fragment);
        switchMenu(prePosition, fragmentIndex);
        menuAdapter.setCheckItem(fragmentIndex);
        menuRv.smoothScrollToPosition(fragmentIndex);
        prePosition = fragmentIndex;
    }

    private int getFragmentIndex(Fragment fragment) {
        if (assistantSettingFragment != null) {
            for (int i = 0; i < fragments.size(); i++) {
                if (fragments.get(i) == fragment) {
                    return i;
                }
            }
        }
        return 0;
    }

    @Override
    public String getThisNode() {
        return NodeConst.Setting.ASSISTANT_ACTIVITY;
    }
}
