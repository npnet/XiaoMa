package com.xiaoma.shop.common.track;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/13
 */
public interface EventConstant {

    interface PageDesc {
        //主界面
        String ACTIVITY_APP = "主界面";

        //menu 栏
        String FRAGMENT_PERSONAL_THEME = "个性主题-系统皮肤";
        String FRAGMENT_GROW_STORE = "流量商城";
        String FRAGMENT_HOLOGRAM = "全息影像";

        //个性主题
        //--系统皮肤
        String FRAGMENT_SYSTEM_THEME = "系统皮肤页";
        String PERSONAL_SYSTEM_SKIN_BUY = "系统皮肤-购买";
        String PERSONAL_SYSTEM_SKIN_COIN_BUY_TIP = "皮肤-使用车币购买提示";
        //----皮肤详情页
        String FRAGMENT_SKIN_DETAILS = "系统皮肤详情页";

        //----语音音色
        String FRAGMENT_VOICE_TONE = "个性主题-语音音色";
        String VOICE_BUY = "语音音色-购买";
        String VOICE_COIN_BUY_TIP = "音色-使用车币购买提示";

        //流量商城
        String FLOW_SHOP_COIN_BUY_TIP = "流量-使用车币购买提示";

        //全息影像
        String HOLOGRAM_BUY = "全息影像-购买";
        String HOLOGRAM_BUY_COIN_BUY_TIP = "全息-使用车币购买提示";
        String HOLOGRAM_DETAILS = "全息影像详情";

        String VOICE_WHOLE_CAR = "整车音效";
        String VOICE_EFFECT_BUY = "音效购买";
        String VOICE_EFFECT_BUY_COIN_BUY_TIP = "音效-使用车币购买提示";

        //我的购买
        String FRAGMENT_BUYED_MAIN = "已购主页";
        String FRAGMENT_BUYED_SKIN = "已购系统主题页";
        String FRAGMENT_BUYED_VOICE = "已购语音音色页";
        String FRAGMENT_BUYED_HOLOGRAM = "已购全息影像页";

        String ACTIVITY_MY_BUY = "我的购买";
    }

    interface NormalClick {
        String ACTION_INTEGRATED = "综合";
        String ACTION_SALES = "销量";
        String ACTION_LATEST = "最近上架";
        String ACTION_COIN_DOWN = "车币价降序";
        String ACTION_COIN_UP = "车币价升序";
        String ACTION_RMB_DOWN = "现金价降序";
        String ACTION_RMB_UP = "现金价升序";

        String ACTION_BUY = "购买";
        String ACTION_BUY_CONFIRM = "确认";
        String ACTION_BUY_CANCEL = "取消";

        String ACTION_TRIAL = "试用";
        String ACTION_USE = "使用";
        String ACTION_DOWNLOAD = "下载";
        String ACTION_UNZIP = "解压";
        String ACTION_UPDATE = "更新";

        String ACTION_TO_SKIN_DETAILS = "跳转皮肤详情";
        String ACTION_SKIN_SHOW = "皮肤配图";

        String ACTION_TRY_LISTEN = "试听";

        String ACTION_PLAY_PAUSE = "播放/暂停";

        String ACTION_CLEAN_CACHE = "清理缓存";
        String ACTION_START_CLEAN = "开始清理";
        String ACTION_SELECT_ALL = "全选";
        String ACTION_CANCEL_SELECT_ALL = "取消全选";
        String ACTION_ACCOUNT_BIND = "账户绑定";

        String ACTION_CACHE_INCLUDE = "添加清除缓存";
        String ACTION_CACHE_SELECT_EXCLUDE = "去除已选待清除缓存";

        String ACTION_CLOSE_THEME_DETAILS_POP_VIEW = "关闭皮肤详情大图";

        String ACTION_BUY_WITH_COIN = "使用车币购买";

        String ACTION_HOLOGRAM_COVER = "全息影像配图";
    }

    interface Expose {
        String QR_CODE_BUY_SUCCESS = "二维码（支付成功)";
    }
}
