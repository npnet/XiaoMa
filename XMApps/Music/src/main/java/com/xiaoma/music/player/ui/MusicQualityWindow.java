package com.xiaoma.music.player.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.player.adapter.QualityListAdapter;
import com.xiaoma.music.player.model.MusicQualityModel;
import com.xiaoma.utils.PopWindowUtils;

import java.util.List;

/**
 * Author: loren
 * Date: 2019/6/29 0029
 */
public class MusicQualityWindow extends PopupWindow implements View.OnClickListener {

    private Context context;
    private RecyclerView qualityRv;
    private QualityListAdapter adapter;
    private Window mWindow;
    private View view;

    public MusicQualityWindow(Activity activity) {
        super(activity);
        this.context = activity.getApplicationContext();
        init(activity);
    }

    private void init(Activity activity) {
        if (context == null) {
            return;
        }
        view = LayoutInflater.from(context).inflate(R.layout.view_quality_window, null);
        setContentView(view);
        view.findViewById(R.id.view_quality_window_back).setOnClickListener(this);
        qualityRv = view.findViewById(R.id.quality_window_rv);

        setWidth(context.getResources().getDimensionPixelSize(R.dimen.width_view_play_list_window));
        setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        PopWindowUtils.fitPopupWindowOverStatusBar(this, true);
        setAnimationStyle(R.style.popup_window_anim_style);
        mWindow = activity.getWindow();

        initRv();
    }

    private void initRv() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        qualityRv.setLayoutManager(mLinearLayoutManager);
        adapter = new QualityListAdapter();
        adapter.setCurrentQuality(OnlineMusicFactory.getKWPlayer().getDownloadWhenPlayQuality());
        adapter.setCurrentIsVip(OnlineMusicFactory.getKWLogin().isCarVipUser());
        qualityRv.setAdapter(adapter);
    }

    public void setOnItemChildClickListener(QualityListAdapter.OnItemChildClickListener listener) {
        if (adapter != null) {
            adapter.setOnItemChildClickListener(listener);
        }
    }

    public void bgAlpha(float alphaValue) {
        WindowManager.LayoutParams attributes = mWindow.getAttributes();
        attributes.alpha = alphaValue;
        mWindow.setAttributes(attributes);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        bgAlpha(1.0f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_quality_window_back:
                show(false);
                break;
        }
    }

    public void refreshQualityList(List<MusicQualityModel> models) {
        if (adapter != null && models != null) {
            adapter.setNewData(models);
        }
    }

    public void show(boolean b) {
        if (b && !isShowing()) {
            bgAlpha(0.3f);
            if (adapter != null) {
                adapter.setCurrentQuality(OnlineMusicFactory.getKWPlayer().getDownloadWhenPlayQuality());
                adapter.setCurrentIsVip(OnlineMusicFactory.getKWLogin().isCarVipUser());
            }
            showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        } else {
            dismiss();
        }
    }

    public void setLoading() {

    }

    public void setFailure() {

    }
}
