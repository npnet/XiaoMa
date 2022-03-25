package com.xiaoma.assistant.scenarios;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.model.FlowBean;
import com.xiaoma.assistant.model.FlowMarginBean;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.utils.AssistantUtils;
import com.xiaoma.assistant.utils.Constants;
import com.xiaoma.assistant.utils.UnitConverUtils;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.apptool.AndroidProcess;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import org.json.JSONException;

import java.util.List;

/**
 * @author: wuzongwei
 * @date: 2019/5/21 1458
 * 智能家居场景
 */
public class IatFlowScenario extends IatScenario {

    public IatFlowScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        try {
            if (StringUtil.isNotEmpty(parseResult.getOperation())) {
                String operation = parseResult.getOperation();
                if (!LoginTypeManager.getInstance().canUse(operation, new OnBlockCallback() {
                    @Override
                    public void handle(LoginType loginType) {
                        XMToast.showToast(context, LoginTypeManager.getPrompt(context));
                    }
                }))
                    return;
                instructionDispatcher(operation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void instructionDispatcher(String operation) throws JSONException {
        String speakContent = "";
        switch (operation) {
            case "BUY"://购买流量
                speakContent = startGetFlowApp();
                break;
            case "EXCHANGE"://兑换流量
                speakContent = startGetFlowApp();
                break;
            case "QUERY"://查询流量
                queryFlow();
                return;
        }
        if (TextUtils.isEmpty(speakContent)) {
          closeVoicePopup();
        } else {
            closeAfterSpeak(speakContent);
        }
    }

    public void queryFlow() {
        setRobAction(AssistantConstants.FlowType.QUERY_FLOW);
        RequestManager.newSingleton().queryFlow(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String result = response.body();
                //1060:服务不可用，1059:暂不支持该语言
                FlowMarginBean flowMarginBean = GsonHelper.fromJson(result, FlowMarginBean.class);
                if (flowMarginBean == null) {
                    return;
                }
                FlowBean flowBean = handleFlowMarginData(flowMarginBean);

                if (StringUtil.isNotEmpty(flowBean.getBalance()) && StringUtil.isNotEmpty(flowBean.getTotal())) {
                    closeAfterSpeak(String.format(getString(R.string.total_flow_this_month), flowBean.getTotal(), flowBean.getBalance()));
                }

            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                closeAfterSpeak(R.string.no_traffic_information_obtained);
            }
        });

    }

    /**
     * 处理流量余量数据
     */
    private FlowBean handleFlowMarginData(FlowMarginBean flowMarginBean) {
        String total = "0.00M", balance = "0.00M", usage = "0.00M";
        FlowBean flowBean = new FlowBean();
        List<FlowMarginBean.LeftInfoBean> leftInfo = flowMarginBean.getLeftInfo();
        if (leftInfo != null && leftInfo.size() > 0) {
            FlowMarginBean.LeftInfoBean leftInfoBean = leftInfo.get(0);
            String unit = TextUtils.isEmpty(leftInfoBean.getUnit()) ? "" : leftInfoBean.getUnit();
            UnitConverUtils.Unit u = null;
            switch (unit) {
                case "Kb":
                    u = UnitConverUtils.Unit.KB;
                    break;
                case "G":
                    u = UnitConverUtils.Unit.G;
                    break;
                case "M":
                    u = UnitConverUtils.Unit.M;
                    break;
            }
            if (u != null) {
                total = UnitConverUtils.toNear(u, leftInfoBean.getQuota());
                balance = UnitConverUtils.toNear(u, leftInfoBean.getQuotaBalance());
                usage = UnitConverUtils.toNear(u, leftInfoBean.getQuotaUsage());

            }
        }
        flowBean.setTotal(total);
        flowBean.setBalance(balance);
        flowBean.setUsage(usage);
        return flowBean;
    }

    private String startGetFlowApp() {
        setRobAction(AssistantConstants.FlowType.BUY_FLOW);
        if (!AppUtils.isAppInstalled(context, CenterConstants.SHOP)) {
            return getString(R.string.please_install_shop);
        }
        if (!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.SHOP)) {
            return LoginTypeManager.getInstance().getLoginType().getPrompt(context);
        }

        //        Intent intent = new Intent();
        //        intent.setAction(Intent.ACTION_VIEW);
        //        intent.addCategory(Intent.CATEGORY_DEFAULT);
        //        intent.setAction(AssistantConstants.ASSIATANT_JUP_SHOP_ACTION);
        //        intent.setData(Uri.parse(AssistantConstants.ASSIATANT_JUP_SHOP_URI));
        //        context.startActivity(intent);
        NodeUtils.jumpTo(context, CenterConstants.SHOP, "com.xiaoma.shop.business.ui.main.MainActivity", NodeConst.SHOP.ASSISTANT_ACTIVITY + "/" + NodeConst.SHOP.BUY_FLOW);

        return getString(R.string.result_ok);
    }

    @Override
    public void onChoose(String voiceText) {

    }

    @Override
    public boolean isIntercept() {
        return false;
    }

    @Override
    public void onEnd() {

    }
}
