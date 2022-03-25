package com.xiaoma.shop.business.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.business.ui.ChooseUserActivity;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.PayInfo;
import com.xiaoma.shop.business.ui.bought.BoughtActivity;
import com.xiaoma.shop.business.ui.flow.MainFlowFragment2;
import com.xiaoma.shop.business.ui.hologram.MainHologramFragment;
import com.xiaoma.shop.business.ui.sound.VehicleSoundFragment;
import com.xiaoma.shop.business.ui.theme.MainThemeFragment;
import com.xiaoma.shop.business.ui.theme.ThemeDetailsFragment;
import com.xiaoma.shop.business.ui.view.TabMenuGroup;
import com.xiaoma.shop.common.callback.OnPayFromPersonalCallback;
import com.xiaoma.shop.common.constant.PaySourceType;
import com.xiaoma.shop.common.constant.ShopOrderConstants;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.util.UnitConverUtils;
import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.tts.EventTtsManager;

import java.util.List;

@PageDescComponent(EventConstant.PageDesc.ACTIVITY_APP)
public class MainActivity extends BaseActivity implements TabMenuGroup.OnCheckedChangeListener {

    public static final String FRAGMENT_TAG_RB_THEME = "FRAGMENT_TAG_RB_THEME";
    public static final String FRAGMENT_TAG_RB_FLOW = "FRAGMENT_TAG_RB_FLOW";
    public static final String FRAGMENT_TAG_RB_HOLOGRAM = "FRAGMENT_TAG_RB_HOLOGRAM";
    public static final String FRAGMENT_TAG_RB_VEHICLE_SOUND = "FRAGMENT_TAG_RB_VEHICLE_SOUND";

    private TabMenuGroup mTabMenuGroup;
    private MainThemeFragment mMainThemeFragment = MainThemeFragment.newInstance();
    private MainFlowFragment2 mMainFlowFragment2 = MainFlowFragment2.newInstance();
    private MainHologramFragment mMainHologramFragment = MainHologramFragment.newInstance();
    private VehicleSoundFragment mVehicleSoundFragment = VehicleSoundFragment.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setBackgroundDrawableResource(R.drawable.bg_common);
        setContentView(R.layout.activity_main);
        initView();
        EventTtsManager.getInstance().init(this);
        if (!LoginManager.getInstance().isUserLogin()) {
            ChooseUserActivity.start(this, true);
            finish();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        directOpenPage();
    }


    private void initView() {
        mTabMenuGroup = findViewById(R.id.rg_menu);
        mTabMenuGroup.setOnCheckedChangeListener(this);
        directOpenPage();
    }


    /**
     * 定向开启界面
     */
    private void directOpenPage() {
        getUIHandler().getLooper().getQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                if (!isDestroy()) {
                    boolean directFlag = false;
                    Intent intent = getIntent();
                    if (intent != null) {
                        Uri uri = intent.getData();
                        if (uri != null) {
                            String uriStr = uri.toSafeString();
                            if (ShopOrderConstants.SHOP_URI.equals(uriStr)) {
                                directFlag = true;
                            }
                        }
                    }

                    if (directFlag) {
                        openPageToPay();
                    } else {
                        mTabMenuGroup.check(R.id.rb_theme);
                    }
                }
                return false;
            }
        });
    }

    private void openPageToPay() {
        PayInfo payInfo = buildPayInfo();
        if(TextUtils.isEmpty(payInfo.getProductType())) return;
        switch (payInfo.getProductType()) {
            case PaySourceType.FLOW:// 联通流量现金购买
            case PaySourceType.TRAFFIC:
                mTabMenuGroup.check(R.id.rb_flow);
                if (mMainFlowFragment2 instanceof OnPayFromPersonalCallback) {
                    mMainFlowFragment2.handlePay(payInfo, false);
                }
                break;
            case PaySourceType.SKIN://皮肤
            case PaySourceType.SKU:// 音色
                mTabMenuGroup.check(R.id.rb_theme);
                if (mMainThemeFragment instanceof OnPayFromPersonalCallback) {
                    mMainThemeFragment.handlePay(payInfo, false);
                }
                break;
            case PaySourceType.VEHICLE://音响
            case PaySourceType.INSTRUMENT:// 仪表
                mTabMenuGroup.check(R.id.rb_vehicle_sound);
                if (mVehicleSoundFragment instanceof OnPayFromPersonalCallback) {
                    mVehicleSoundFragment.handlePay(payInfo, false);
                }
                break;
            case PaySourceType.HOLO://全息
                mTabMenuGroup.check(R.id.rb_hologram);
                if (mMainHologramFragment instanceof OnPayFromPersonalCallback) {
                    mMainHologramFragment.handlePay(payInfo, false);
                }
                break;

        }
    }

    @NonNull
    private PayInfo buildPayInfo() {
        String  productId = getIntent().getExtras().getString(ShopOrderConstants.ORDER_ID);
        String type = getIntent().getExtras().getString(ShopOrderConstants.PAY_TYPE);
        String paySource = getIntent().getExtras().getString(ShopOrderConstants.PAY_SOURCE);
        String price = getIntent().getExtras().getString(ShopOrderConstants.PRODUCT_PRICE);
        String number = getIntent().getExtras().getString(ShopOrderConstants.ORDER_NUMBER);
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNum(number);
       long id = UnitConverUtils.string2Long(productId);
        payInfo.setProductId(id);
        payInfo.setPrice(price);
        payInfo.setProductType(type);
        payInfo.setPaySource(paySource);
        return payInfo;
    }




    /**
     * 我的购买
     *
     * @param view
     */
    public void mineBuy(View view) {
        manualUpdateTrack(EventConstant.PageDesc.ACTIVITY_MY_BUY);
        startActivity(BoughtActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdateCheck.getInstance().checkAppUpdate(getPackageName(), getApplication());
    }

    public void replaceContent(BaseFragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        boolean foundAndShow = false;
        if (allFragments != null && !allFragments.isEmpty()) {
            for (final Fragment fmt : allFragments) {
                if (fmt.getClass() == fragment.getClass()) {
                    transaction.show(fmt);
                    foundAndShow = true;
                } else {
                    transaction.hide(fmt);
                }
            }
        }
        if (!foundAndShow) {
            transaction.add(R.id.fragment, fragment, tag);
        }
        transaction.commitAllowingStateLoss();

        /*List<Fragment> fragments = FragmentUtils.getFragments(getSupportFragmentManager());
        Fragment fragmentByTag = FragmentUtils.findFragment(getSupportFragmentManager(), tag);
        if (fragmentByTag != null) {
            fragments.remove(fragmentByTag);
            FragmentUtils.showHide(fragmentByTag, fragments);
        } else {
            for (Fragment fg : fragments) {
                FragmentUtils.hide(fg);
            }
            FragmentUtils.add(getSupportFragmentManager(), fragment, R.id.fragment, tag);
        }*/
    }

    public void addHoleContainer(BaseFragment fragment) {
        FragmentUtils.add(getSupportFragmentManager(), fragment, R.id.whole_container, fragment.getClass().getSimpleName()
                , false, true);
    }

    @Override
    public void onCheckedChanged(TabMenuGroup group, int checkedId) {
        switch (checkedId) {
            default:
            case R.id.rb_theme:
                manualUpdateTrack(EventConstant.PageDesc.FRAGMENT_PERSONAL_THEME);
                replaceContent(mMainThemeFragment, FRAGMENT_TAG_RB_THEME);
                break;
            case R.id.rb_flow:
                manualUpdateTrack(EventConstant.PageDesc.FRAGMENT_GROW_STORE);
                replaceContent(mMainFlowFragment2, FRAGMENT_TAG_RB_FLOW);
                break;
            case R.id.rb_hologram:
                manualUpdateTrack(EventConstant.PageDesc.FRAGMENT_HOLOGRAM);
                replaceContent(mMainHologramFragment, FRAGMENT_TAG_RB_HOLOGRAM);
                break;
            case R.id.rb_vehicle_sound:
                replaceContent(mVehicleSoundFragment, FRAGMENT_TAG_RB_VEHICLE_SOUND);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventTtsManager.getInstance().destroy();
    }

    private void manualUpdateTrack(String eventAction) {
        XmAutoTracker.getInstance().onEvent(eventAction,
                MainActivity.this.getClass().getName(),
                EventConstant.PageDesc.ACTIVITY_APP);
    }

    public void showLastGuide() {
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.SHOP_SHOWED, GuideConstants.SHOP_GUIDE_FIRST, false))
            return;
        NewGuide.with(this)
                .setLebal(GuideConstants.SHOP_SHOWED)
                .build()
                .showLastGuide();
    }

    @Override
    public void onBackPressed() {
        Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(ThemeDetailsFragment.class.getSimpleName());
        boolean exit = fragmentByTag == null;//当ThemeDetailsFragment还在时，点击back键，不应该退出应用
        super.onBackPressed();
        if (exit) {
            finish();
        }
    }

    @Override
    public boolean handleJump(String nextNode) {
        KLog.i("filOut| " + "[handleJump]->nextNode : " + nextNode);
        if (!TextUtils.isEmpty(nextNode)) {
            if (nextNode.equals(NodeConst.SHOP.BUY_FLOW)) {
                if (mTabMenuGroup != null) {
                    mTabMenuGroup.check(R.id.rb_flow);
                }
                return true;
            }
        }
        return super.handleJump(nextNode);
    }

    @Override
    public String getThisNode() {
        return NodeConst.SHOP.ASSISTANT_ACTIVITY;
    }
}
