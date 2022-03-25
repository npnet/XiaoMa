package com.xiaoma.vr;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * VoiceType，getVoiceListName，voiceToName，setVoiceType
 * 所支持语言有更改时 以上方法需要做相应增减
 * Created by iSun on 2017/4/28 0028.
 */

public class VoiceTypeManager {
    public static final String VOICE_KEY = "voicer";
    private static Set<OnVoiceParamChangedListener> sVoiceParamChangedListenerSet = new HashSet<>();

    /**
     * 当前所支持的语言列表type
     */
    public static final class VoiceTypeNumber {
        //青年女声 中英文（普通话）
        public final static int VOICE_TYPE_WOMEN = 0;
        //青年男声 中英文（普通话）
        public final static int VOICE_TYPE_MAN = 1;
        //小梅 青年女声(粤语)
        public final static int VOICE_TYPE_GUANGDONG = 2;
        //汉语（四川话）
        public final static int VOICE_TYPE_SICHUAN = 3;
        //汉语（东北话）
        public final static int VOICE_TYPE_DONGBEI = 4;
        //汉语（河南话）
        public final static int VOICE_TYPE_HENAN = 5;
        //汉语（湖南话）
        public final static int VOICE_TYPE_HUNAN = 6;
        //汉语 (陕西话)
        public final static int VOICE_TYPE_SHANXI = 7;
    }

    /**
     * 当前所支持的语言列表
     */
    public static final class VoiceType {
        //青年女声 中英文（普通话）
        public static final String XIAOQI = "xiaoqi";
        //青年男声 中英文（普通话）
        public static final String XIAOFENG = "vixf";
        //小梅 青年女声(粤语)
        public static final String XIAOMEI = "xiaomei";
        //汉语（四川话）
        public static final String XIAORONG = "xiaorong";
        //汉语（东北话）
        public static final String XIAOYUN = "xiaoqian";
        //汉语（河南话）
        public static final String XIAOKUN = "xiaokun";
        //汉语（湖南话）
        public static final String XIAOQIANG = "xiaoqiang";
        //汉语 (陕西话)
        public static final String XIAOYING = "vixying";
    }

    /**
     * 获取当前所支持的语言名
     *
     * @return
     */
    public static List<String> getVoiceListName(Context context) {
        List<String> voiceList;
        String[] voiceName = context.getResources().getStringArray(R.array.voice_name);
        voiceList = Arrays.asList(voiceName);
        if (voiceList == null) {
            voiceList = new ArrayList<>();
        }
        return voiceList;
    }


    /**
     * @param voice
     * @return
     * @date 根据VoiceType类型 获取对应的语言名
     */
    public static String voiceToName(Context context, String voice) {
        String name;
        switch (voice) {
            case VoiceType.XIAOFENG: //男普通话
                name = context.getString(R.string.voice_name_m_mandarin);
                break;
            case VoiceType.XIAOMEI: //女粤语
                name = context.getString(R.string.voice_name_cantonese);
                break;
            case VoiceType.XIAORONG: //四川话
                name = context.getString(R.string.voice_name_sichuan);
                break;
            case VoiceType.XIAOYUN: //东北话
                name = context.getString(R.string.voice_name_northeast);
                break;
            case VoiceType.XIAOKUN: //河南话
                name = context.getString(R.string.voice_name_henan);
                break;
            case VoiceType.XIAOQIANG: //湖南话
                name = context.getString(R.string.voice_name_hunan);
                break;
            case VoiceType.XIAOQI: //普通话//
                name = context.getString(R.string.voice_name_w_mandarin);
                break;
            case VoiceType.XIAOYING://陕西话
                name = context.getString(R.string.voice_name_shaanxi);
                break;
            default:
                name = context.getString(R.string.voice_name_w_mandarin);
                break;
        }
        return name;
    }

    /**
     * 根据VoiceType设置Voice
     *
     * @param type
     * @return
     */
    public static String setVoiceType(Context context, int type) {
        String voicer;
        switch (type) {
            case VoiceTypeNumber.VOICE_TYPE_WOMEN: //女普通话
                voicer = VoiceType.XIAOQI;
                break;
            case VoiceTypeNumber.VOICE_TYPE_MAN: //男普通话
                voicer = VoiceType.XIAOFENG;
                break;
            case VoiceTypeNumber.VOICE_TYPE_GUANGDONG: //女粤语
                voicer = VoiceType.XIAOMEI;
                break;
            case VoiceTypeNumber.VOICE_TYPE_SICHUAN: //四川话
                voicer = VoiceType.XIAORONG;
                break;
            case VoiceTypeNumber.VOICE_TYPE_DONGBEI: //东北话
                voicer = VoiceType.XIAOYUN;
                break;
            case VoiceTypeNumber.VOICE_TYPE_HENAN: //河南话
                voicer = VoiceType.XIAOKUN;
                break;
            case VoiceTypeNumber.VOICE_TYPE_HUNAN: //湖南话
                voicer = VoiceType.XIAOQIANG;
                break;
            case VoiceTypeNumber.VOICE_TYPE_SHANXI://陕西话
                voicer = VoiceType.XIAOYING;
                break;
            default:
                voicer = VoiceType.XIAOQI;
                break;
        }
        //EventAgent.getInstance().onEvent(com.xiaoma.base.constant.Constants.XMEventKey.ThemeMall.SWITCH_VOICE,voiceToName(voicer));
        return setVoiceType(context, voicer, voiceToName(context, voicer));
    }

    public static String setVoiceType(Context context, String voiceParam, String voiceName) {
        if (!TextUtils.isEmpty(voiceParam)) {
            TPUtils.put(context, VOICE_KEY, voiceParam);
            for (OnVoiceParamChangedListener l : sVoiceParamChangedListenerSet) {
                l.onVoiceParamChanged(voiceParam, voiceName);
            }
            KLog.i(StringUtil.format("dispatch onVoiceParamChanged, voiceParam = %s", voiceParam));
        }
        return voiceParam;
    }

    /**
     * 获取当前语言的类型
     *
     * @return
     */
    public static String getVoice(Context context) {
        return TPUtils.get(context,VOICE_KEY, VoiceType.XIAOQI);
    }

    /**
     * 获取当前语言的类型名称
     *
     * @return
     */
    public static String getVoiceName(Context context) {
        return voiceToName(context, getVoice(context));
    }

    public static void addOnVoiceParamChangedListener(OnVoiceParamChangedListener listener) {
        if (listener != null)
            sVoiceParamChangedListenerSet.add(listener);
    }

    public static void removeOnVoiceParamChangedListener(OnVoiceParamChangedListener listener) {
        if (listener != null)
            sVoiceParamChangedListenerSet.remove(listener);
    }

    public interface OnVoiceParamChangedListener {
        void onVoiceParamChanged(String voiceParam, String voiceName);
    }

}
