package com.xiaoma.xting.practice.util;


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
import com.xiaoma.mapadapter.ui.MapActivity;
import com.xiaoma.model.pratice.NewsChannelBean;
import com.xiaoma.model.pratice.PlayMusicBean;
import com.xiaoma.model.pratice.PlayRadioBean;
import com.xiaoma.model.pratice.UserSkillItemsBean;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.xting.practice.ui.PlayRadioActivity;

public class ActionsUtils {
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

}
