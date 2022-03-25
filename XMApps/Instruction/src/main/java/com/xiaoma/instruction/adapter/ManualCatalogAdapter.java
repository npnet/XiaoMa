package com.xiaoma.instruction.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.instruction.R;
import com.xiaoma.instruction.mode.ManualBean;
import com.xiaoma.instruction.utils.LanguageUtils;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.StringUtil;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/06/01
 *     desc   :
 * </pre>
 */
public class ManualCatalogAdapter extends XMBaseAbstractBQAdapter<ManualBean, BaseViewHolder> {

    public ManualCatalogAdapter(){
        super(R.layout.item_manual_catalog);
    }

    @Override
    protected void convert(BaseViewHolder helper, ManualBean item) {
        initData(helper,item);
    }

    public void initData(BaseViewHolder helper, ManualBean item){
        String menuName = LanguageUtils.isChinese(mContext)?item.getMenuName():item.getMenuNameUs();
        if (StringUtil.isNotEmpty(menuName))
            helper.setText(R.id.content,menuName);
        if (StringUtil.isNotEmpty(item.getIcon()))
            ImageLoader.with(mContext).load(item.getIcon()).into((ImageView) helper.getView(R.id.icon));

    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}
