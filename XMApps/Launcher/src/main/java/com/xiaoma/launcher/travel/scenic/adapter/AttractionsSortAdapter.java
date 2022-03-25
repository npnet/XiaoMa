package com.xiaoma.launcher.travel.scenic.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.LanguageUtils;
import com.xiaoma.launcher.travel.scenic.ui.AttractionsActivity;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.trip.category.response.CategoryBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;


public class AttractionsSortAdapter extends XMBaseAbstractBQAdapter<CategoryBean.SubcateBean, BaseViewHolder> {

    private TextView mSortname;

    public AttractionsSortAdapter() {
        super(R.layout.sort_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, CategoryBean.SubcateBean item) {
        initView(helper, item);
        initData(item);
    }

    private void initData(CategoryBean.SubcateBean item) {
        if(LanguageUtils.isChinese(mContext)){
            mSortname.setText(item.getName());
        }else{
            mSortname.setText(item.getNameEn());
        }

    }

    private void initView(BaseViewHolder helper, final CategoryBean.SubcateBean item) {
        mSortname = helper.getView(R.id.sort_name);

        helper.itemView.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.SORTITEM,       //按钮名称(如播放,音乐列表)
                        String.valueOf(item.getName()));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (!NetworkUtils.isConnected(mContext)) {
                    XMToast.toastException(mContext, R.string.net_work_error);
                    return;
                }
                if(LanguageUtils.isChinese(mContext)){
                    launcherAttractionsActivity(item.getName(),item.getName(), String.valueOf(item.getId()));
                }else{
                    launcherAttractionsActivity(item.getName(),item.getNameEn(), String.valueOf(item.getId()));
                }
            }
        });
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }

    private void launcherAttractionsActivity(String searchKey,String display, String cateId) {
        Intent intent = new Intent(mContext, AttractionsActivity.class);
        intent.putExtra(LauncherConstants.SEARCH_KEY, searchKey);
        intent.putExtra(LauncherConstants.DISPLAY, display);
        intent.putExtra(LauncherConstants.CATEID, cateId);
        mContext.startActivity(intent);

    }
}

