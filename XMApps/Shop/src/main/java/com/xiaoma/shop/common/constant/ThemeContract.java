package com.xiaoma.shop.common.constant;

import android.support.annotation.StringDef;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/4/10
 */
public interface ThemeContract {

    @StringDef({SortRule.DEFAULT, SortRule.INTEGRATED, SortRule.SALES_COUNT, SortRule.LATEST,
            SortRule.SCORE_ASC, SortRule.SCORE_DESC, SortRule.RMB_ASC, SortRule.RMB_DESC
    ,SortRule.VEHICLE_SOUND_SALES_COUNT})
    @interface SortRule {
        String DEFAULT = "default";
        String INTEGRATED = ""; //综合排序 不用传递
        String SALES_COUNT = "usedNum,desc";//销量
        String LATEST = "createDate,desc"; //最近上架

        String SCORE_ASC = "discountScorePrice,asc"; //积分正序
        String SCORE_DESC = "discountScorePrice,desc";

        String RMB_ASC = "discountPrice,asc";//现金正序
        String RMB_DESC = "discountPrice,desc";


        String VEHICLE_SOUND_SALES_COUNT="downloadNum,desc";//整车音效 销量
    }

}
