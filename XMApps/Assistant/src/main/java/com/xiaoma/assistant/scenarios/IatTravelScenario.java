package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.widget.Toast;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.model.parser.TravelInfo;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.ui.adapter.TravelListAdapter;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：景点场景
 */
public class IatTravelScenario extends IatScenario implements BaseMultiPageAdapter.OnMultiPageItemClickListener {


    public IatTravelScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        String list = "";
        if (StringUtil.isNotEmpty(parseResult.getSlots())) {
            try {
                JSONObject jsonObject = new JSONObject(parseResult.getSemantic().getSlots());
                if (jsonObject.has("allList")) {
                    try {
                        list = jsonObject.getString("allList");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (list == null) {
            speakUnderstand();
        } else {
            ArrayList<TravelInfo> travelInfos = null;
            try {
                travelInfos = (ArrayList<TravelInfo>) GsonHelper.fromJsonToList(list, TravelInfo[].class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ListUtils.isEmpty(travelInfos)) {
                XmTtsManager.getInstance().stopSpeaking();
                speakContent(getString(R.string.not_search_data));
                return;
            }
            TravelListAdapter adapter = new TravelListAdapter(context, travelInfos);
            adapter.setOnMultiPageItemClickListener(IatTravelScenario.this);
            showMultiPageData(adapter,context.getString(R.string.search_result_tips));
        }
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

    @Override
    public void onItemClick(int position) {
        // TODO: 2019/1/22 0022
        Toast.makeText(context, "导航去景点", Toast.LENGTH_SHORT).show();
        closeVoicePopup();
    }
}
