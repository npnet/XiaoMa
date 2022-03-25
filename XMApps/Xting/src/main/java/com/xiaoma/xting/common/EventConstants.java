package com.xiaoma.xting.common;

public interface EventConstants {
    String ACTIVITY_RECORD_COMPLETED_EVENT = "语音输入完成-电台";
    String  searchHistoryEvent = "搜索历史";
    String  searchHotEvent = "大家都在搜";

    //page 路径映射
    interface PageDescribe {
        /**
         * 界面 -> Activity
         * 页面 -> Fragment
         */
        String ACTIVITY_MAIN = "主界面";

        String ACTIVITY_SPLASH = "欢迎界面";
        String FRAGMENT_PREFERENCE_SELECT = "偏好选择页";

        String FRAGMENT_HOME = "首页";

        String ACTIVITY_SEARCH = "搜索界面-电台";
        String ACTIVITY_SEARCH_RESULT = "搜索结果界面";
//        String FRAGMENT_SEARCH_RESULT_ALBUM = "搜索结果-专辑页";
//        String FRAGMENT_SEARCH_RESULT_PROGRAM = "搜索结果-节目页";
//        String FRAGMENT_SEARCH_RESULT_RADIO = "搜索结果-电台页";

        String FRAGMENT_FM_NET = "网络电台";
        String FRAGMENT_FM_NET_RECOMMEND = "推荐页";
        String FRAGMENT_FM_NET_RANK = "排行榜页";
        //        String FRAGMENT_FM_NET_CATEGORY = "分类页";
        String FRAGMENT_CATEGORY_SUB_ALBUM = "分类子页-专辑页";
        String FRAGMENT_CATEGORY_SUB_RADIO = "分类子页-电台页";
        String FRAGMENT_CATEGORY_SUB_RADIO_MULTI = "多级电台分类页";
        String FRAGMENT_CATEGORY_SUB_RADIO_SINGLE = "子网络电台列表页";
        String FRAGMENT_CATEGORY_SUB_ALBUM_MULTI = "多级节目分类页";
        String FRAGMENT_RANK_LIST = "排行榜页";

        String FRAGMENT_FM_LOCAL = "本地电台";
        String FRAGMENT_LOCAL_FM = "本地电台FM页";
        String FRAGMENT_LOCAL_AM = "本地电台AM页";
        String FRAGMENT_LOCAL_RADIO_MANUAL_SEARCH = "手动调台页";

        String FRAGMENT_MINE = "我的";
        String FRAGMENT_COLLECT = "我的收藏";
        String FRAGMENT_HISTORY = "播放历史";

        String FRAGMENT_PLAYER = "播放详情页";

        String FRAGMENT_ONLINE_PLAYER = "在线电台播放页面";

        String FRAGMENT_FM_ALBUM = "播放器相关页";
        String FRAGMENT_ONLINE_SIMILAR = "相似推荐页";
        String FRAGMENT_PLAYER_STORE = "我的收藏";

        String FRAGMENT_CATEGORY_ALBUM_CHILD = "专辑分类详情";
        String FRAGMENT_CATEGORY_RADIO_CHILD = "电台分类详情";

        String FRAGMENT_LOCAL_PLAYER = "本地电台播放器页面";
        String FRAGMENT_LOCAL_PLAYER_SWITCH = "本地电台切换";
        String FRAGMENT_LOCAL_PLAYER_STORE = "本地电台收藏";

        String VIEW_NET_POP_LIST = "网络播放列表展示页面";
        String VIEW_LOCAL_POP_LIST = "本地播放列表";

        String TAG_SEARCH_HOT = "热搜标签";
        String TAG_SEARCH_HISTORY = "历史标签";
        String TAG_SEARCH_INPUT = "输入框";
        String TAG_PLAY_AREA = "播放框";
        String TAG_RECOMMEND = "单个专辑";
        String TAG_RANK_LIST = "排行榜标签";
        String TAG_ALBUM = "专辑标签";
        String TAG_RADIO = "电台标签";

        String VIEW_MINI_PLAYER = "迷你播放器";
        String TAG_SEARCH_CONTENT = "搜索内容";

        String albumSearchResult = "搜索结果专辑页面";
        String programSearchResult = "搜索结果节目页面";
        String radioSearchResult = "搜索结果电台页面";
    }

    //click 名称映射
    interface NormalClick {
        //网络相关
        String ACTION_NET_NONE_RETRY = "无网络重试";
        String ACTION_NET_ERROR_RETRY = "网络异常重试";

        //SplashFragment 偏好选择
        String ACTION_START_APP = "开启想听之旅";
        String ACTION_SKIP_SET = "跳过偏好设置";
        String ACTION_PREF_SELECT = "偏好选择";
        String ACTION_CONFIRM_SKIP_SET = "确定跳过偏好设置";
        String ACTION_CANCEL_SKIP_SET = "取消跳过偏好设置";

        //HomeFragment  首页
        String ACTION_MENU_SEARCH = "去搜索";
        String ACTION_MENU_NET_RADIO = "首页-网络电台";
        String ACTION_MENU_LOCAL_RADIO = "首页-本地电台";
        String ACTION_MENU_MINE = "首页-我的电台";

        //SearchActivity 搜索
        String ACTION_START_VOICE_SEARCH = "启动语音搜索";
        String ACTION_CLEAR_SEARCH_HISTORY = "清除搜索历史";
        String ACTION_CANCEL_VOICE_SEARCH = "取消语音搜索";
        String ACTION_SEARCh_NOW = "立即搜索";

        //SearchResultActivity 搜索结果
        String ACTION_BACK_TO_SEARCH = "返回搜索界面";

        //LocalFMFragment 本地电台
        String ACTION_AUTO_SEARCH_PROGRAM = "预设";
        String ACTION_MANUAL_SELECT_PROGRAM = "手动调台";
        String ACTION_LOCAL_RADIO_POWER = "开关按钮";

        String ACTION_PRE_CHANNEL = "上一频道";
        String ACTION_NEXT_CHANNEL = "下一频道";

        //ManualFragment 手动调台
        String ACTION_AUTO_SEARCH_NEXT_CHANNEL = "滑动加";
        String ACTION_AUTO_SEARCH_PREVIOUS_CHANNEL = "滑动减";
        String ACTION_SAVE_SEARCH_RESULT = "保存搜到的电台";
        String ACTION_MANUAL_SEARCH_CHANNEL_ADD = "手动调频+";
        String ACTION_MANUAL_SEARCH_CHANNEL_REDUCE = "手动调频-";

        //HistoryFragment 播放历史
        String ACTION_CLEAR_PLAY_HISTORY = "清除播放历史";
        String ACTION_CONFIRM_CLEAR_HISTORY = "确定清除播放历史";
        String ACTION_CANCEL_CLEAR_HISTORY = "取消清除播放历史";

        // MiniPlayerView 迷你播放器
        String ACTION_PLAYER_CONTROL_PLAY_OR_PAUSE = "播放按钮";
        String ACTION_TO_PLAY_DETAILS = "进入播放详情";

        //OnlinePlayDetailsFragment 播放详情
        String ACTION_EXIT_PLAYER_DETAILS = "最小化";
        String ACTION_EXIT_PLAYER_LIST = "列表菜单";
        String ACTION_SAVE_OR_NOT = "收藏按钮";
        String ACTION_LISTEN_TO_RECOGNIZE = "听歌识曲";

        String ACTION_PLAYER_CONTROL_PLAY_PRE = "上一首";
        String ACTION_PLAYER_CONTROL_PLAY_NEXT = "下一首";
        String ACTION_CHANGE_PLAY_MODE = "切换播放模式";

        String ACTION_BACK_PLAYER_DETAILS = "返回播放器";

        String ACTION_RADIO_POWER_OFF = "关闭本地电台";

        String ACTION_CLOSE_PLAY_LIST = "关闭播放列表";

        String ACTION_CLOSE_MANUAL_SEARCH_CHANNEL = "退出手动搜台";

        String TAG_PROGRESS = "进度条";

        // 播放
        String ACTION_SLIDE_UP = "向上滑动";

        String albumItem = "搜索结果专辑item";
        String programItem = "搜索结果节目item";
        String radioItem = "搜索结果电台item";
    }

}
