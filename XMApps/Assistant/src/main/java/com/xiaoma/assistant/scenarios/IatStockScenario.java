package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.XfStockBean;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.NewStockBean;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.model.parser.StockBean;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.utils.VrConstants;

import java.util.Arrays;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：股票场景
 */
public class IatStockScenario extends IatScenario {

    public IatStockScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        String slots = parseResult.getSemantic().getSlots();
        boolean isMarketPrice = isMarketPrice(slots);
        boolean lxStock = false;
        if (!TextUtils.isEmpty(parseResult.getData())) {
            lxStock = true;
        }
        if (lxStock) {
            // 前装版查询股票
            XfStockBean stock = null;
            try {
                stock = GsonHelper.fromJson(parseResult.getData(), XfStockBean.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (stock == null || stock.result == null || stock.result.isEmpty()) {
                assistantManager.speakThenListening(getString(R.string.query_stock_error));
                return;
            }
            //EventAgent.getInstance().onEvent(Constants.XMEventKey.Assistant.CONVERSATION_LX_QUERY_STOCK, stock.getStock().getName());
//            speakContent(getString(R.string.stock_seach_result_is));
            /*XfStockBean.StockBean bean = stock.result.get(0);
            String speakContent;
            String upOrDown = Double.parseDouble(bean.riseRate.substring(0, bean.riseRate.length() - 1)) >= 0 ? "上涨" : "下跌";
            String riseRate = bean.riseRate.replace("-", "").replace("+", "").replace("%", "");
            if (isMarketPrice) {
                speakContent = String.format(context.getString(R.string.market_price_speak), bean.name, bean.currentPrice, upOrDown, riseRate);
            } else {
                speakContent = String.format(context.getString(R.string.stock_price_speak), bean.name, bean.currentPrice, upOrDown, riseRate);
            }
            speakContent(speakContent);
            //创建一个conversation并展示
            ConversationItem conversationItem = parseResult.createReceiveConversation(
                    VrConstants.ConversationType.STOCK, stock.result.get(0));
            addConversationToList(conversationItem);*/

        } else {
            // 开放平台股票
            NewStockBean stock = null;
            try {
                stock = GsonHelper.fromJson(parseResult.getSemantic().getSlots(), NewStockBean.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (stock == null || stock.getData().getInfo() == null || stock.getData().getInfo().size() == 0) {
                assistantManager.speakThenListening(getString(R.string.query_stock_error));
                return;
            }
            String upOrDown = stock.getData().getChange().contains("-") ? "下跌" : "上涨";
            String curPrice = stock.getData().getCurrent_price();
            String riseRate = stock.getData().getChange();
            if (!TextUtils.isEmpty(riseRate) && riseRate.contains("(") && riseRate.contains(")")) {
                riseRate = riseRate.split("\\(")[1].split("\\)")[0].replace("+", "").replace("-","").replace("%","");
            } else {
                assistantManager.speakThenListening(getString(R.string.query_stock_error));
                return;
            }
            String speakContent = String.format(getString(R.string.stock_price_speak),stock.getData().getName(), curPrice, upOrDown, riseRate);
            speakContent(speakContent);
            ConversationItem conversationItem = parseResult.createReceiveConversation(
                    VrConstants.ConversationType.STOCK, stock.getData());
            addConversationToList(conversationItem);
            /*JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(parseResult.getWebPage());
                stock.setUrl(jsonObject.getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
                assistantManager.speakThenListening(getString(R.string.query_stock_error));
                return;
            }
            //EventAgent.getInstance().onEvent(Constants.XMEventKey.Assistant.VOICE_SEARCH_STOCK, stock.getName());
            speakContent(getString(R.string.stock_seach_result_is));*/
        }
    }

    private boolean isMarketPrice(String slots) {
        if (TextUtils.isEmpty(slots)) {
            return false;
        }
        StockBean stockBean = GsonHelper.fromJson(slots, StockBean.class);
        if (stockBean == null) return false;
        String[] stringArray = context.getResources().getStringArray(R.array.stock_market_name);
        List<String> strings = Arrays.asList(stringArray);
        if (strings.contains(stockBean.getName())) {
            return true;
        }
        return false;
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
