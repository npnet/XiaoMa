package com.xiaoma.dualscreen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.xiaoma.dualscreen.R;
import com.xiaoma.dualscreen.manager.NaviDisplayManager;

/**
 * @author: iSun
 * @date: 2019/3/7 0007
 */
public class LeftNaviView extends BaseView {

    private SurfaceView surfaceView;

    public LeftNaviView(Context context) {
        super(context);
    }

    public LeftNaviView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LeftNaviView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LeftNaviView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(){
        NaviDisplayManager.getInstance().initLeft(getContext(), surfaceView);
//        SimulateTest.getInstance().initLeftView(this);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public int contentViewId() {
        return R.layout.view_left_navi;
    }

    @Override
    public void onViewCreated() {
        surfaceView = findViewById(R.id.left_video);
        init();
    }


}
