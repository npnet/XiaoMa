package com.xiaoma.oilconsumption.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.oilconsumption.R;
import com.xiaoma.oilconsumption.data.CompetitionInformation;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.List;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/05/24
 *     desc   :
 * </pre>
 */
public class GamesAdapter extends XMBaseAbstractBQAdapter<CompetitionInformation, BaseViewHolder> {

    TextView date,periods,details;

    public GamesAdapter() {
        super(R.layout.games_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, CompetitionInformation item) {
        initData(helper,item);

    }

    public void initData(BaseViewHolder helper, CompetitionInformation item){
        helper.setText(R.id.date,item.getDate());
        helper.setText(R.id.periods,item.getPeriods());
        helper.addOnClickListener(R.id.details);
    }


    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}
