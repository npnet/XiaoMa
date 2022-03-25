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
@Table("FM_Channel")
public class FMChannelBean extends BaseChannelBean implements Serializable {

    public FMChannelBean(int channelValue) {
        super(channelValue);
    }

    public FMChannelBean(String channelName, String channelCover, int channelValue) {
        super(channelName, channelCover, channelValue);
    }

    @Override
    public CharSequence getTitleText(Context context) {
        return context.getResources().getString(R.string.fm_unit, new DecimalFormat("0.0").format(getChannelValue() / 1000f));
    }
}
