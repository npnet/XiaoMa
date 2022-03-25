package com.xiaoma.club.share.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.club.common.view.UserHeadView;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ShareUtils;
import com.xiaoma.utils.share.ShareCallBack;
import com.xiaoma.utils.share.ShareClubBean;

/**
 * 简介:确定加入车队选择页面
 *
 * @author lingyan
 */
public class JoinConfirmActivity extends BaseActivity {
    private static final int MAX_MEMBER_COUNT = 30;
    private int memberCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_join_confirm);
        initView();
        initEvent();
    }

    private void initView() {
        TextView name = findViewById(R.id.motorcade_name);
        TextView command = findViewById(R.id.command);
        TextView count = findViewById(R.id.members_count);
        UserHeadView headView = findViewById(R.id.motorcade_head);
        ShareClubBean bean = getIntent().getParcelableExtra(ShareUtils.SHARE_KEY);
        headView.setOnlyHead(bean.getShareImage());
        String[] teamDetail = bean.getCarTeamDetail().split(",");
        if (teamDetail.length == 3) {
            command.setText(String.format(getString(R.string.command), teamDetail[2]));
            name.setText(teamDetail[1]);
            count.setText(String.format(getString(R.string.members_count), teamDetail[0]));
            memberCount = Integer.parseInt(teamDetail[0]);
        }
    }

    private void initEvent() {
        ShareClubBean bean = getIntent().getParcelableExtra(ShareUtils.SHARE_KEY);
        final String pck = bean.getFromPackage();
        final String action = bean.getBackAction();
        final String core = bean.getCoreKey();
        findViewById(R.id.join_right_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.isConnected(JoinConfirmActivity.this)) {
                    showToastException(R.string.net_work_error);
                    return;
                }
                if (memberCount >= MAX_MEMBER_COUNT) {
                    showToastException(R.string.beyond_maximum);
                    return;
                }
                ShareUtils.handleShare(JoinConfirmActivity.this, pck, action, core, new ShareCallBack() {
                    @Override
                    public void shareError(String errorMsg) {
                        XMToast.toastException(JoinConfirmActivity.this, errorMsg);
                        finish();
                    }

                    @Override
                    public void shareSuccess() {
                        finish();
                    }
                });
            }
        });
    }

}
