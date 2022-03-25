package com.xiaoma.app.common.constant;

/**
 * Created by Thomas on 2018/12/28 0028
 * 打点描述
 */

public interface EventConstants {

    //page 路径映射
    interface PageDescribe {
        String mainActivityPagePathDesc = "首页";
        String downloadListActivityPagePathDesc = "下载列表";
        String appDetailActivityPagePathDesc = "应用详情";
        String appListFragmentPagePathDesc = "应用市场";
        String appManagerFragmentPagePathDesc = "应用管理";
        String appDetailFragmentPagePathDesc = "应用详情信息介绍";
        String detailFragmentPagePathDesc = "应用详情信息介绍";
        String appUpdateLogFragmentFragmentPagePathDesc = "应用详情升级描述";
        String updateAllDialogFragmentFragmentPagePathDesc = "应用全部升级弹窗";
    }

    //click 名称映射
    interface NormalClick {
        String appMarket = "应用市场";
        String appManager = "应用管理";
        String downloadList = "下载中心";
        String allAppUpdate = "全部升级";
        String updateAllSure = "全部升级-确认";
        String updateAllCancel = "全部升级-取消";
        String detailImage = "详情页图片";
        String detailImageDismiss = "详情页图片disMiss";
        String updateLog = "更新日志";
        String updateLog2Detail = "更新日志2应用详情";
        String installError = "安装异常";
        String uninstallSure = "静默卸载-确定";
        String uninstallCancel = "静默卸载-取消";
        String uninstall = "静默卸载弹框";
    }

    //滑动 名称映射
    interface SlideEvent {
        String appMarketLog = "应用市场app更新日志";
        String appManagerLog = "应用管理app更新日志";
    }

}
