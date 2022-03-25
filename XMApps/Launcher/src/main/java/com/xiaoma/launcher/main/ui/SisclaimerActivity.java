package com.xiaoma.launcher.main.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.main.view.ObservableScrollView;
import com.xiaoma.launcher.main.view.VerticalScrollBar;
import com.xiaoma.model.annotation.PageDescComponent;

@PageDescComponent(EventConstants.PageDescribe.sisclaimerActivityPagePathDesc)
public class SisclaimerActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disclaimer_text);
        getNaviBar().showBackNavi();
        VerticalScrollBar scrollBar = findViewById(R.id.sisclamer_scrollbar);
        ObservableScrollView  scrollView  = findViewById(R.id.sisclamer_scrollview);
        scrollBar.setScrollView(scrollView);
    }
}
