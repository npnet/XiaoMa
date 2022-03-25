package com.xiaoma.vrpractice.common.util;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.common.manager
 *  @file_name:      ActionsUtils
 *  @author:         Rookie
 *  @create_time:    2019/6/3 19:24
 *  @description：   TODO             */

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.model.pratice.NewsChannelBean;
import com.xiaoma.model.pratice.PlayMusicBean;
import com.xiaoma.model.pratice.PlayRadioBean;
import com.xiaoma.model.pratice.UserSkillItemsBean;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.StringUtil;

public class ActionsUtils {


    private static final int[] SKILL_REQUEST_CODE = {VrPracticeConstants.YOMI_TTS_SOME_WORDS, VrPracticeConstants.YOMI_PLAY_SOME_MUSIC, VrPracticeConstants.YOMI_TTS_WEATHER,
            VrPracticeConstants.YOMI_GUIDE, VrPracticeConstants.YOMI_PLAY_RADIO, VrPracticeConstants.YOMI_CAR_CONTROL, VrPracticeConstants.YOMI_MEDIA_VOLUME,
            VrPracticeConstants.YOMI_RECORD, VrPracticeConstants.YOMI_NEWS};


    public static int getAddSkillRequestCode(int index) {
        return SKILL_REQUEST_CODE[index];
    }

    /**
     * 注：+10区别于添加技能的resultcode
     *
     * @param index
     * @return
     */
    public static int getEditSkillRequestCode(int index) {
        return SKILL_REQUEST_CODE[index] + 10;
    }

    /**
     * 根据对应技能获取信息提示语
     * @param userSkillItemsBean
     * @return
     */
    public static String getSkillTip(UserSkillItemsBean userSkillItemsBean) {
        String tip = "";
        String content = userSkillItemsBean.getContent();
        switch (userSkillItemsBean.getItemId()) {
            case VrPracticeConstants.YOMI_TTS_SOME_WORDS:
                tip = content;
                break;
            case VrPracticeConstants.YOMI_PLAY_SOME_MUSIC:
                PlayMusicBean playMusicBean = GsonHelper.fromJson(content, PlayMusicBean.class);
                if (playMusicBean!=null&& !StringUtil.isEmpty(playMusicBean.getName())){
                    tip = playMusicBean.getName();
                }
                break;
            case VrPracticeConstants.YOMI_TTS_WEATHER:
                tip = content;
                break;
            case VrPracticeConstants.YOMI_GUIDE:
                SearchAddressInfo searchAddressInfo = GsonHelper.fromJson(content, SearchAddressInfo.class);
                if (searchAddressInfo!=null&& !StringUtil.isEmpty(searchAddressInfo.title)){
                    tip = searchAddressInfo.title;
                }
                break;
            case VrPracticeConstants.YOMI_PLAY_RADIO:
                PlayRadioBean playRadioBean = GsonHelper.fromJson(content, PlayRadioBean.class);
                if (playRadioBean!=null&& !StringUtil.isEmpty(playRadioBean.getTitle())){
                    tip = playRadioBean.getTitle();
                }
                break;
            case VrPracticeConstants.YOMI_CAR_CONTROL:
                tip = "温度、车窗、氛围...";
                break;
            case VrPracticeConstants.YOMI_MEDIA_VOLUME:
                tip = content;
                break;
            case VrPracticeConstants.YOMI_RECORD:
                tip = "开启";
                break;
            case VrPracticeConstants.YOMI_NEWS:
                NewsChannelBean newsChannelBean = GsonHelper.fromJson(content, NewsChannelBean.class);
                if (newsChannelBean!=null&& !StringUtil.isEmpty(newsChannelBean.getName())){
                    tip = newsChannelBean.getName();
                }
                break;
            default:
                break;
        }
        return tip;
    }


    /**
     * 禁止EditText输入空格
     *
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        editText.setFilters(new InputFilter[]{filter});

    }

    /**
     * 禁止EditText输入空格(maxlength会无效,需要重新设置长度限制)
     *
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText,int maxLength) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength), filter});

    }

}
