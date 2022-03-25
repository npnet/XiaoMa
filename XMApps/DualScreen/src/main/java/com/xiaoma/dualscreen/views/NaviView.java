package com.xiaoma.dualscreen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.dualscreen.R;
import com.xiaoma.dualscreen.manager.DualScreenMapManager;
import com.xiaoma.dualscreen.manager.DualViewManager;
import com.xiaoma.dualscreen.manager.MediaViewManager;
import com.xiaoma.dualscreen.manager.NaviDisplayManager;
import com.xiaoma.dualscreen.manager.WheelOperateDualManager;
import com.xiaoma.dualscreen.model.NaviMenu;
import com.xiaoma.thread.ThreadDispatcher;

/**
 * @author: iSun
 * @date: 2019/3/7 0007
 */
public class NaviView extends BaseView implements WheelOperateDualManager.OnWheelOperatePhoneListener {
    private SurfaceView surfaceView;
    private LinearLayout mNaviMenu;
    private NaviMenu naviMenu = NaviMenu.NAVI_2D;
    private TextView tv2D;
    private TextView tv3D;
    private int count = 0;
    private int mNaviMode = 0;
    private int tempNaviMode = 0;


    public NaviView(Context context) {
        super(context);
    }

    public NaviView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NaviView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NaviView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init() {
        NaviDisplayManager.getInstance().init(getContext(), surfaceView);
//        SimulateTest.getInstance().initNaviView(this);
        WheelOperateDualManager.getInstance(getContext()).setWheelOperatePhoneListener(this);
        mNaviMenu.setBackground(getContext().getResources().getDrawable(getMenuBgDrawable()));
        if (surfaceView != null) {
            surfaceView.setZOrderOnTop(false);
//            surfaceView.setZOrderMediaOverlay(true);
        }
        upNaviMenuUI();
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public int contentViewId() {
        return R.layout.view_navi;
    }

    @Override
    public void onViewCreated() {
        surfaceView = findViewById(R.id.video);
        mNaviMenu = findViewById(R.id.ll_navi_menu);
        tv2D = findViewById(R.id.tv_2d);
        tv3D = findViewById(R.id.tv_3d);
        init();
    }

    public void resetText(){
        if(tv2D != null) tv2D.setText(getContext().getString(R.string.major_navi));
        if(tv3D != null) tv3D.setText(getContext().getString(R.string.ar_navi));
    }

    public void onOK() {
        if (mNaviMenu.getVisibility() == VISIBLE) {
            cancelMenu();
            naviSwitch();
        } else if (isNaviing()) {
            if (surfaceView != null) {
                surfaceView.setZOrderOnTop(false);
//                surfaceView.setZOrderMediaOverlay(true);
            }
            mNaviMenu.bringToFront();
            mNaviMenu.setVisibility(VISIBLE);
            XmCarVendorExtensionManager.getInstance().setMediaMenuLevel(MediaViewManager.IViewStep.MEDIA_DETAIL);
        }
        upNaviMenuUI();
    }

    /**
     * 切换地图模式
     */
    private void naviSwitch() {
        if (isNaviing()) {
            if (naviMenu == NaviMenu.NAVI_2D) {
                DualScreenMapManager.getInstance().setICARVisible(false);
            } else {
                tempNaviMode = DualScreenMapManager.getInstance().getNaviShowState();
                DualScreenMapManager.getInstance().setICARVisible(true);
            }
        }
    }


    public void onKeyDown() {
        if (mNaviMenu.getVisibility() == VISIBLE) {
            naviMenu = NaviMenu.values()[count++ % 2];
            upNaviMenuUI();
        } else if (this.isShown()) {
            if (mNaviMode == 0) {
                mNaviMode = 2;
            } else if (mNaviMode == 1) {
                mNaviMode = 0;
            } else if (mNaviMode == 2) {
                return;
            }
            DualScreenMapManager.getInstance().setZoomMode(mNaviMode);
        }

    }

    public boolean isNaviing() {
        //正在导航
        return DualScreenMapManager.getInstance().isNaviingV2();
    }

    public void onKeyUp() {
        if (mNaviMenu.getVisibility() == VISIBLE) {
            naviMenu = NaviMenu.values()[count++ % 2];
            upNaviMenuUI();
        } else if (this.isShown()) {
            if (mNaviMode == 0) {
                mNaviMode = 1;
            } else if (mNaviMode == 1) {
                return;
            } else if (mNaviMode == 2) {
                mNaviMode = 0;
            }
            DualScreenMapManager.getInstance().setZoomMode(mNaviMode);
        }
    }

    public void upNaviMenuUI() {
        if (naviMenu == NaviMenu.NAVI_2D) {
            tv2D.setSelected(true);
            tv2D.setBackground(getResources().getDrawable(getMenuSelectDrawable()));
            tv3D.setBackground(null);
            tv3D.setSelected(false);
        } else if (naviMenu == NaviMenu.NAVI_3D) {
            tv3D.setSelected(true);
            tv3D.setBackground(getResources().getDrawable(getMenuSelectDrawable()));
            tv2D.setBackground(null);
            tv2D.setSelected(false);
        }
    }

    public int getMenuSelectDrawable() {
        int curSkin = DualViewManager.getInstance().getCurSkin();
        int res = R.drawable.bg_navi_menu_select_wisdom;
        switch (curSkin) {
            case 0:
                res = R.drawable.bg_navi_menu_select_wisdom;
                break;
            case 1:
                res = R.drawable.bg_navi_menu_select_luxurious;
                break;
            case 2:
                res = R.drawable.bg_navi_menu_select_dreams;
                break;
        }
        return res;
    }

    public int getMenuBgDrawable() {
        int curSkin = DualViewManager.getInstance().getCurSkin();
        int res = R.drawable.bg_navi_menu_ll_wisdom;
        switch (curSkin) {
            case 0:
                res = R.drawable.bg_navi_menu_ll_wisdom;
                break;
            case 1:
                res = R.drawable.bg_navi_menu_ll_luxurious;
                break;
            case 2:
                res = R.drawable.bg_navi_menu_ll_dreams;
                break;
        }
        return res;
    }


    public void cancelMenu() {
        //隐藏菜单，返回键，OK键及停止导航
        if (mNaviMenu != null) {
            mNaviMenu.setVisibility(INVISIBLE);
        }
        XmCarVendorExtensionManager.getInstance().setMediaMenuLevel(MediaViewManager.IViewStep.MEDIA_MAIN);
    }

    public int getNaviLevel(){
        if (mNaviMenu != null && mNaviMenu.getVisibility() == VISIBLE) {
            return MediaViewManager.IViewStep.MEDIA_DETAIL;
        }
        return MediaViewManager.IViewStep.MEDIA_MAIN;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void keyUp() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                onKeyUp();

            }
        });
    }

    @Override
    public void keyDown() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                onKeyDown();
            }
        });
    }

    @Override
    public void keyCancel() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                cancelMenu();
            }
        });
    }

    @Override
    public void keyOk() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if(XmCarConfigManager.hasVrNav()){
                    onOK();
                }
            }
        });
    }

    public void refreshSkin() {
        mNaviMenu.setBackground(getContext().getResources().getDrawable(getMenuBgDrawable()));
        upNaviMenuUI();
    }

}
