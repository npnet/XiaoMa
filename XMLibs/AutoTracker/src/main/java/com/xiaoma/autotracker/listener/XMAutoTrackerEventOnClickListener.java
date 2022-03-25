package com.xiaoma.autotracker.listener;

import android.view.View;
import com.xiaoma.model.ItemEvent;

/**
 * Created by Thomas on 2018/12/6 0006
 * 基于AOP需代理onclick实现返回item position event msg
 */

public abstract class XMAutoTrackerEventOnClickListener implements View.OnClickListener {

    public abstract ItemEvent returnPositionEventMsg(View view);

}
