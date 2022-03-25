package com.xiaoma.autotracker.listener;

import android.support.design.widget.TabLayout;
import android.view.View;

import com.xiaoma.model.ItemEvent;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/12/11
 *     desc   : TabLayout的独有OnTabSelectedListener
 * </pre>
 */
public abstract class XmTrackerOnTabSelectedListener implements TabLayout.OnTabSelectedListener {


    public void onTabUnselected(TabLayout.Tab tab) {
    }

    public void onTabReselected(TabLayout.Tab tab) {
    }

    public abstract ItemEvent returnPositionEventMsg(View view);


}
