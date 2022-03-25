package com.xiaoma.setting.common.views;

import android.content.Context;
import android.view.View;

import com.xiaoma.setting.R;
import com.xiaoma.setting.practice.view.ObservableScrollView;
import com.xiaoma.setting.practice.view.VerticalScrollBar;
import com.xiaoma.ui.navi.NavigationBar;

/**
 * @author Gillben
 * date: 2018/12/03
 */
public class LeftPopupWindow extends android.widget.PopupWindow implements View.OnClickListener, android.widget.PopupWindow.OnDismissListener {

    private Context mContext;
    private NavigationBar mNavigationBar;
    private VerticalScrollBar mScrollBar;
    private ObservableScrollView mScrollView;


    public LeftPopupWindow(Context context, int with, int height) {
        this.mContext = context;
        View mContentView = View.inflate(context, R.layout.pop_window_info, null);
        setContentView(mContentView);

        initView(mContentView);
        setSize(with, height);
        setAnimationStyle(R.style.PopupWindowInfoAnimation);
    }

    private void setSize(int with, int height) {
        setWidth(with);
        setHeight(height);
    }

    private void initView(View view) {
        setFocusable(true);
        setTouchable(true);
        setClippingEnabled(false);
        setOnDismissListener(this);

        mNavigationBar = view.findViewById(R.id.personal_popup_nav);
        mNavigationBar.showBackNavi();

        mScrollBar = view.findViewById(R.id.sisclamer_scrollbar);
        mScrollView = view.findViewById(R.id.sisclamer_scrollview);
        mScrollBar.setScrollView(mScrollView);
    }


    @Override
    public void onClick(View v) {

    }



    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

    }

    @Override
    public void onDismiss() {

    }



}
