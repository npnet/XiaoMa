package com.xiaoma.shop.business.repository;

import com.xiaoma.shop.common.constant.LoadMoreState;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/6
 */
public interface ILoadDataListener {

    void loadStateChanged(@LoadMoreState int loadState);
}
