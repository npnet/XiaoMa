package com.xiaoma.mapadapter.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.xiaoma.mapadapter.control.MapViewControl;
import com.xiaoma.mapadapter.interfaces.IMapView;


/**
 * 对上层提供的地图View
 * Created by minxiwen on 2017/12/11 0011.
 */

public class TextrueMapView extends FrameLayout implements IMapView {
    private IMapView realMapView;
    private static MapViewControl controlView;

    public TextrueMapView(Context context) {
        super(context);
        initView(context);
    }

    public TextrueMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TextrueMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        realMapView = controlView.getTextView(context);
        this.addView(realMapView.getMapView());
    }

    @Override
    public Map getMap() {
        return realMapView.getMap();
    }

    @Override
    public void onCreate(Bundle bundle) {
        realMapView.onCreate(bundle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        realMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        realMapView.onResume();
    }

    @Override
    public void onPause() {
        realMapView.onPause();
    }

    @Override
    public void onDestroy() {
        realMapView.onDestroy();
    }

    @Override
    public View getMapView() {
        return realMapView.getMapView();
    }

    public static void registerMapView(MapViewControl controlViews) {
        controlView = controlViews;
    }
}
