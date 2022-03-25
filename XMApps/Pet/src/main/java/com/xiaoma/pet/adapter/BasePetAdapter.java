package com.xiaoma.pet.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.pet.R;

/**
 * Created by Gillben on 2018/12/24 0024
 * <p>
 * desc:
 */
public abstract class BasePetAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {


    public BasePetAdapter() {
        super(R.layout.item_store_repository_rv);
    }


}
