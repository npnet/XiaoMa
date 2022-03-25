package com.xiaoma.xting.online.model;

import android.content.Context;
import android.text.Html;

import com.xiaoma.utils.StringUtil;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.adapter.IGalleryData;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.sdk.bean.XMRadio;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KY
 * @date 2018/11/1
 */
public class RadioBean extends XMRadio implements Serializable, IGalleryData {

    public RadioBean(Radio radio) {
        super(radio);
    }

    public static List<RadioBean> convertFromRadio(List<XMRadio> xmRadios) {
        ArrayList<RadioBean> radioBeans = new ArrayList<>(xmRadios.size());
        for (XMRadio xmRadio : xmRadios) {
            radioBeans.add(new RadioBean(xmRadio.getSDKBean()));
        }
        return radioBeans;
    }

    @Override
    public String getCoverUrl() {
        return getValidCover();
    }

    @Override
    public CharSequence getTitleText(Context context) {
        return getRadioName();
    }

    @Override
    public CharSequence getFooterText(Context context) {
        return null;
    }

    @Override
    public CharSequence getBottomText(Context context) {
        return Html.fromHtml(context.getString(R.string.str_count_listener, "<font color=\"#fbd3a4\">" + StringUtil.convertBigDecimal(getRadioPlayCount()) + "</font>"));
//        return SpanUtils.with(context)
//                .append(StringUtil.convertBigDecimal(getRadioPlayCount()))
//                .setForegroundColorRes(R.color.gallery_hint_text_color)
//                .appendRes(R.string.channel_listener_num).create();
    }

    @Override
    public long getUUID() {
        return getDataId();
    }

    @Override
    public int getSourceType() {
        return PlayerSourceType.HIMALAYAN;
    }
}
