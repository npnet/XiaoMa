package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.api.RadioApiManager;
import com.xiaoma.assistant.model.parser.FmBean;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.utils.GsonHelper;

import org.w3c.dom.Text;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：本地电台场景
 */
public class IatFmScenario extends IatScenario {


    public IatFmScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        this.parseResult = parseResult;
        String slots = parseResult.getSlots();
        if (TextUtils.isEmpty(slots) || slots.equals("{}")) {
            speakUnderstand();
            return;
        }
        setRobAction(AssistantConstants.RobActionKey.SWITCH_RADIO_STATION);
        FmBean fm = GsonHelper.fromJson(slots, FmBean.class);
        if (fm != null
                || TextUtils.isEmpty(fm.waveband)
                || (!fm.waveband.equalsIgnoreCase("fm")
                && !fm.waveband.equalsIgnoreCase("am"))) {
            if (TextUtils.isEmpty(fm.code)) {
                if (fm.waveband.equalsIgnoreCase("fm")) {
                    RadioApiManager.getInstance().playFM();
                } else if (fm.waveband.equalsIgnoreCase("am")) {
                    RadioApiManager.getInstance().playAM();
                }
            } else {
                double freq = Double.valueOf(fm.code);
                if (fm.waveband.equalsIgnoreCase("fm")) {
                    if (freq<87.5||freq>108){
                        speakThenListening(getString(R.string.fm_am_freq_error));
                        return;
                    }
                }else if(fm.waveband.equalsIgnoreCase("am")){
                    if (freq<531||freq>1602){
                        speakThenListening(getString(R.string.fm_am_freq_error));
                        return;
                    }
                }
                if (fm.waveband.equalsIgnoreCase("fm")) {
                    freq *= 1000;
                }
                RadioApiManager.getInstance().playRadioStationByFrequency(fm.waveband, (int) freq);
            }
        } else {
            speakUnderstand();
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
}
