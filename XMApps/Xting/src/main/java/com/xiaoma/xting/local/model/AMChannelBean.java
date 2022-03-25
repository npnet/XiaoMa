package com.xiaoma.xting.local.model;

import android.content.Context;

import com.litesuits.orm.db.annotation.Table;
import com.xiaoma.xting.R;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * @author KY
 * @date 2018/10/23
 */
@Table("AM_Channel")
public class AMChannelBean extends BaseChannelBean implements Serializable {

    public AMChannelBean(int channelValue) {
        super(channelValue);
    }

    public AMChannelBean(String channelName, String channelCover, int channelValue) {
        super(channelName, channelCover, channelValue);
    }

    @Override
    public CharSequence getTitleText(Context context) {
        return context.getResources().getString(R.string.am_unit, new DecimalFormat("#.#").format(getChannelValue()));
    }
}
