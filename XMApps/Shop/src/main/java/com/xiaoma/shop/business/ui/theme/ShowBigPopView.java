package com.xiaoma.shop.business.ui.theme;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.BigThemePicAdapter;
import com.xiaoma.shop.common.track.EventConstant;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/6
 */
public class ShowBigPopView extends PopupWindow implements PopupWindow.OnDismissListener {

    private Activity mActivity;
    private BigThemePicAdapter mAdapter;
    private RecyclerView mDetailsRV;
    private Window mWindow;

    public ShowBigPopView(Activity activity) {
        mActivity = activity;
        init();
    }

    private void init() {
        mWindow = mActivity.getWindow();
        View inflate = LayoutInflater.from(mActivity).inflate(R.layout.pop_big_pic, null);
        setContentView(inflate);

        setWidth(mActivity.getResources().getDimensionPixelOffset(R.dimen.width_skin_thumbnail));
        setHeight(mActivity.getResources().getDimensionPixelOffset(R.dimen.height_skin_thumbnail));
        this.setFocusable(true);
        this.setTouchable(true);
        setOnDismissListener(this);

        mDetailsRV = inflate.findViewById(R.id.rvDetails);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mDetailsRV.setLayoutManager(linearLayoutManager);

        mAdapter = new BigThemePicAdapter();
        mDetailsRV.setAdapter(mAdapter);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mDetailsRV);

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                dismiss();
            }
        });

    }

    public void setBigPics(List<String> picUrls, int index) {
        mAdapter.setNewData(picUrls);
        mDetailsRV.scrollToPosition(index);
    }


    public void showPopView() {
        if (!isShowing()) {
            bgAlpha(0.5f);
            showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public void onDismiss() {
        XmAutoTracker.getInstance().onEvent(EventConstant.NormalClick.ACTION_CLOSE_THEME_DETAILS_POP_VIEW,
                ThemeDetailsFragment.class.getName(),
                EventConstant.PageDesc.FRAGMENT_SKIN_DETAILS);
        bgAlpha(1.0f);
    }

    private void bgAlpha(float alphaValue) {
        WindowManager.LayoutParams attributes = mWindow.getAttributes();
        attributes.alpha = alphaValue;
        mWindow.setAttributes(attributes);
    }

    public RecyclerView getBigPicRv(){
        return mDetailsRV;
    }
}
