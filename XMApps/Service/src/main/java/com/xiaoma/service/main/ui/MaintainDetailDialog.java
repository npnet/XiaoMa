package com.xiaoma.service.main.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.main.model.MaintainBean;

/**
 * Created by zhushi.
 * Date: 2019/1/2
 */
@PageDescComponent(EventConstants.PageDescribe.maintaninDetailDialogPagePathDesc)
public class MaintainDetailDialog extends BaseActivity {
    public static final String MAINTAINBEAN = "maintainbean";
    private ImageView ivIcon;
    private TextView tvName;
    private TextView tvContent;
    private TextView tvMethod;
    private TextView tvPeriod;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_maintain_detail);
        getNaviBar().showBackNavi();
        getWindow().setLayout(1300, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.START);
        MaintainBean maintainBean = (MaintainBean) getIntent().getSerializableExtra(MAINTAINBEAN);
        initView();
        initData(maintainBean);
    }

    private void initView() {
        ivIcon = findViewById(R.id.iv_pop_maintain);
        tvName = findViewById(R.id.tv_name);
        tvContent = findViewById(R.id.tv_content);
        tvMethod = findViewById(R.id.tv_methods);
        tvPeriod = findViewById(R.id.tv_period);
    }

    private void initData(MaintainBean maintainBean) {
        if (maintainBean == null) {
            return;
        }
        ImageLoader.with(this).load(maintainBean.getPicUrl()).into(ivIcon);
        tvName.setText(maintainBean.getName());
        tvContent.setText(Html.fromHtml("<b><tt>"+maintainBean.getName() + "： </tt></b>"+maintainBean.getOptionContent()));
        tvMethod.setText(maintainBean.getUpkeepMethod());
        tvPeriod.setText(maintainBean.getUpkeepPeriod());
    }

    @Override
    public void finish() {
        super.finish();
        //注释掉activity本身的过渡动画
        overridePendingTransition(0, 0);
    }
}
