package com.xiaoma.pet.common.callback;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/22 0022 17:58
 *   desc:
 * </pre>
 */
public interface OnUsedGoodsCallback {

    void usedFood(boolean eating);

    void usedDecoratorNeedDownload(String goodsType, String url);
}
