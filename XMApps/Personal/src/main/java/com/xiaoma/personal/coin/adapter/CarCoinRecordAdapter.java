package com.xiaoma.personal.coin.adapter;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.personal.R;
import com.xiaoma.personal.coin.model.CoinRecord;
import com.xiaoma.personal.common.NotFullShowTipsAdapter;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Gillben
 * date: 2018/12/07
 * <p>
 * 车币记录列表适配器
 */
public class CarCoinRecordAdapter extends NotFullShowTipsAdapter<CoinRecord, BaseViewHolder> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());

    public CarCoinRecordAdapter() {
        super(R.layout.item_car_coin_record);
    }

    @Override
    protected void convert(BaseViewHolder helper, CoinRecord item) {
        helper.setText(R.id.tv_car_coin_record_desc, item.getAction());
        helper.setText(R.id.tv_car_coin_record_date, TimeUtils.millis2String(item.getActionTime(), dateFormat));
        helper.setText(R.id.tv_car_coin_number, StringUtil.getSignedNumber(item.getCreditsChanged()));
    }
}
