package com.xiaoma.assistant.Interceptor.interceptor;

import android.util.Log;

import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.assistant.Interceptor.matcher.ConfirmOrCancelTextMatcher;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.model.parser.LxParseResult;

/**
 * Created by qiuboxiang on 2019/7/9 19:22
 * Desc:
 */
public class NavigationInterceptor extends BaseInterceptor<ConfirmOrCancelTextMatcher> {

    private String name;
    private String address;
    private double longitude;
    private double latitude;

    public NavigationInterceptor(String name, String address, double longitude, double latitude) {
        this.name = name;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    protected ConfirmOrCancelTextMatcher getTextMatcher() {
        ConfirmOrCancelTextMatcher confirmMatcher = new ConfirmOrCancelTextMatcher();
        confirmMatcher.addStartWithKeyword("开始", true)
                .addStartWithKeyword("导航", true);
        return confirmMatcher;
    }

    @Override
    protected void action(LxParseResult parserResult, String text) {
        if (textMatcher.isConfirm()) {
            XmMapNaviManager.getInstance().startNaviToPoi(name, address, longitude, latitude);
            closeAssistant();
        } else {
            cancelOperation();
        }
    }

    public void cancelOperation() {
        AssistantManager.getInstance().closeAfterSpeak(context.getString(R.string.stand_down_first));
    }

}
