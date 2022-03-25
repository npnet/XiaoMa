package com.xiaoma.carpark.main.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.carpark.R;
import com.xiaoma.carpark.common.constant.CarParkConstants;
import com.xiaoma.carpark.main.vm.MainMenuVM;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.apptool.AppObserver;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import skin.support.annotation.Skinable;

/**
 * Created by Thomas on 2018/11/5 0005
 */
@Skinable
public class MainActivity extends BaseActivity {

    //推荐
    public static final int TYPE_RECOMMEND = 1;
    //游戏
    public static final int TYPE_GAME = 2;
    //比赛
    public static final int TYPE_COMPETITION = 3;

    private List<Fragment> fragments;
    private MainMenuVM mMainMenuVM;
    private TextView tvRecommend;
    private TextView tvGame;
    private TextView tvCompetition;
    private NewGuide newGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        showFirstGuideWindow();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdateCheck.getInstance().checkAppUpdate(getPackageName(), getApplication());
    }

    private void initView() {
        tvRecommend = findViewById(R.id.tab_recommend);
        tvGame = findViewById(R.id.tab_game);
        tvCompetition = findViewById(R.id.tab_activity);
        fragments = new ArrayList<>();
        fragments.add(MainTypeFragment.newTypeFragmentInstance(TYPE_RECOMMEND));
        fragments.add(MainTypeFragment.newTypeFragmentInstance(TYPE_GAME));
        fragments.add(MainTypeFragment.newTypeFragmentInstance(TYPE_COMPETITION));
    }

    private void initData() {
        mMainMenuVM = ViewModelProviders.of(this).get(MainMenuVM.class);
        mMainMenuVM.getMenuIndexMap().observe(this, new Observer<TreeMap<Integer, String>>() {
            @Override
            public void onChanged(@Nullable TreeMap<Integer, String> integerStringTreeMap) {
                if (integerStringTreeMap == null) {
                    return;
                }
                if (integerStringTreeMap.firstKey() == CarParkConstants.MAIN_TAB_INDEX_ACTIVITY) {
                    if (!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.CARPARK_ACTIVITY
                            , new OnBlockCallback() {
                                @Override
                                public boolean onShowToast(LoginType loginType) {
                                    XMToast.showToast(MainActivity.this, loginType.getPrompt(MainActivity.this));
                                    return true;
                                }
                            })) return;
                }
                setTabSelected(integerStringTreeMap.firstKey());
                switchFragment(fragments.get(integerStringTreeMap.firstKey()), integerStringTreeMap.get(integerStringTreeMap.firstKey()));
            }
        });
        //默认选中推荐
        recommend(tvRecommend);
    }

    public void recommend(View view) {
        mMainMenuVM.setMenuData(CarParkConstants.MAIN_TAB_INDEX_RECOMMEND, CarParkConstants.MAIN_TAB_INDEX_RECOMMEND_FRAGMENT_TAG);
    }

    public void game(View view) {
        mMainMenuVM.setMenuData(CarParkConstants.MAIN_TAB_INDEX_GAME, CarParkConstants.MAIN_TAB_INDEX_GAME_FRAGMENT_TAG);
        dismissGuideWindow();
        showSecondGuideWindow();
    }

    public void activity(View view) {
        mMainMenuVM.setMenuData(CarParkConstants.MAIN_TAB_INDEX_ACTIVITY, CarParkConstants.MAIN_TAB_INDEX_ACTIVITY_FRAGMENT_TAG);
        dismissGuideWindow();
        showLastWindow();
    }

    private void setTabSelected(int index) {
        tvRecommend.setSelected(false);
        tvGame.setSelected(false);
        tvCompetition.setSelected(false);
        tvRecommend.setBackground(null);
        tvGame.setBackground(null);
        tvCompetition.setBackground(null);

        switch (index) {
            case CarParkConstants.MAIN_TAB_INDEX_RECOMMEND:
                tvRecommend.setSelected(true);
                //                tvRecommend.setBackground(getDrawable(R.drawable.bg_item_select));
                tvRecommend.setBackgroundResource(R.drawable.bg_item_select);
                tvGame.setBackgroundResource(0);
                tvCompetition.setBackgroundResource(0);
                break;

            case CarParkConstants.MAIN_TAB_INDEX_GAME:
                tvGame.setSelected(true);
                //                tvGame.setBackground(getDrawable(R.drawable.bg_item_select));
                tvGame.setBackgroundResource(R.drawable.bg_item_select);
                tvCompetition.setBackgroundResource(0);
                tvRecommend.setBackgroundResource(0);
                break;

            case CarParkConstants.MAIN_TAB_INDEX_ACTIVITY:
                tvCompetition.setSelected(true);
                //                tvCompetition.setBackground(getDrawable(R.drawable.bg_item_select));
                tvCompetition.setBackgroundResource(R.drawable.bg_item_select);
                tvGame.setBackgroundResource(0);
                tvRecommend.setBackgroundResource(0);
                break;
        }
    }

    public void switchFragment(Fragment fragment, String tag) {
        List<Fragment> fragments = FragmentUtils.getFragments(getSupportFragmentManager());
        Fragment fragmentByTag = FragmentUtils.findFragment(getSupportFragmentManager(), tag);
        if (fragmentByTag != null) {
            fragments.remove(fragmentByTag);
            FragmentUtils.showHide(fragmentByTag, fragments);

        } else {
            for (Fragment fg : fragments) {
                FragmentUtils.hide(fg);
            }
            FragmentUtils.add(getSupportFragmentManager(), fragment, R.id.fl_container, tag);
        }
    }

    private void showFirstGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.CAR_PARK_SHOWED, GuideConstants.CAR_PARK_GUIDE_FIRST, true))
            return;
        tvGame.post(new Runnable() {
            @Override
            public void run() {
                Rect viewRect = NewGuide.getViewRect(tvGame);
                Rect targetRect = new Rect(viewRect.left + 30, viewRect.top - 40, viewRect.right - 30, viewRect.bottom - 40);
                newGuide = NewGuide.with(MainActivity.this)
                        .setLebal(GuideConstants.CAR_PARK_SHOWED)
                        .setTargetView(tvGame)
                        .setTargetRect(targetRect)
                        .setGuideLayoutId(R.layout.guide_view_game)
                        .setNeedShake(true)
                        .setNeedHande(true)
                        .needMoveUpALittle(true)
                        .setViewHandId(R.id.iv_gesture)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .build();
                newGuide.showGuide();
                GuideDataHelper.setFirstGuideFalse(GuideConstants.CAR_PARK_SHOWED);
            }
        });
    }

    private void showSecondGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.CAR_PARK_SHOWED, GuideConstants.CAR_PARK_GUIDE_FIRST, false))
            return;
        Rect viewRect = NewGuide.getViewRect(tvCompetition);
        Rect targetRect = new Rect(viewRect.left + 30, viewRect.top - 40, viewRect.right - 30, viewRect.bottom - 40);
        newGuide = NewGuide.with(MainActivity.this)
                .setLebal(GuideConstants.CAR_PARK_SHOWED)
                .setGuideLayoutId(R.layout.guide_view_competition)
                .setTargetView(tvCompetition)
                .setTargetRect(targetRect)
                .setNeedShake(true)
                .setNeedHande(true)
                .needMoveUpALittle(true)
                .setViewHandId(R.id.iv_gesture)
                .setViewSkipId(R.id.tv_guide_skip)
                .setViewWaveIdOne(R.id.iv_wave_one)
                .setViewWaveIdTwo(R.id.iv_wave_two)
                .setViewWaveIdThree(R.id.iv_wave_three)
                .build();
        newGuide.showGuide();
    }

    private void showLastWindow() {
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.CAR_PARK_SHOWED, GuideConstants.CAR_PARK_GUIDE_FIRST, false))
            return;
        NewGuide.with(MainActivity.this)
                .setLebal(GuideConstants.CAR_PARK_SHOWED)
                .build()
                .showLastGuide();
    }

    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
//        AppObserver.getInstance().closeAllActivitiesAndExit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppObserver.getInstance().closeAllActivitiesAndExit();
    }
}
