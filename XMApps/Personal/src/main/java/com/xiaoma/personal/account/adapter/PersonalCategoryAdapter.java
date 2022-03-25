package com.xiaoma.personal.account.adapter;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.guide.listener.ViewLayoutCallBack;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.account.model.CategoryInfo;
import com.xiaoma.personal.account.vm.ItemFlag;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

/**
 * @author Gillben
 * date 2018/12/03
 * <p>
 * 个人中心分类标签适配器
 */
public class PersonalCategoryAdapter extends XMBaseAbstractBQAdapter<CategoryInfo, BaseViewHolder> {
    private boolean hasTriggered = false;
    private ViewLayoutCallBack callBack;

    public PersonalCategoryAdapter() {
        super(R.layout.item_persoanl_center_category);
    }

    @Override
    protected void convert(BaseViewHolder helper, CategoryInfo item) {
        helper.setText(R.id.tv_category_title, item.getTitle());
        helper.setImageResource(R.id.iv_category_icon, item.getImageResId());
        if (!hasTriggered && callBack != null) {
//            if (helper.getAdapterPosition() == 1){
            if (item.getItemFlag() == ItemFlag.TASK_CENTER){
                callBack.onViewLayouted(helper.itemView);
                hasTriggered = true;
            }
        }
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(mData.get(position).getTitle(), position + "");
    }

    public void setCallBack(ViewLayoutCallBack callBack) {
        this.callBack = callBack;
    }
}
