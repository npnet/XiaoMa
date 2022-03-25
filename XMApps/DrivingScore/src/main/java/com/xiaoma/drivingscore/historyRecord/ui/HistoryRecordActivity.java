package com.xiaoma.drivingscore.historyRecord.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.drivingscore.R;
import com.xiaoma.utils.FragmentUtils;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/1/7
 */
public class HistoryRecordActivity extends BaseActivity {

    public static void launch(Context context) {
        context.startActivity(new Intent(context, HistoryRecordActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().showBackNavi();
        replaceContent(HistoryRecordFragment.newInstance());
    }

    public void replaceContent(BaseFragment fragment) {
        FragmentUtils.replace(getSupportFragmentManager(), fragment, R.id.view_content, fragment.getClass().getName(), false);
    }
}
