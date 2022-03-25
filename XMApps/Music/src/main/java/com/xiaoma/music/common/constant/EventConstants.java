package com.xiaoma.music.common.constant;

/**
 * Created by ZYao.
 * Date ：2018/12/29 0029
 */
public interface EventConstants {
    String  searchHistoryEvent = "搜索历史";
    String  searchHotEvent = "大家都在搜";
    String activityRecordCompletedEvent = "语音输入完成-音乐";
    String inputEnterClickEvent = "音乐搜索输入框Enter键";
    //page 路径映射
    interface PageDescribe {
        String mainActivityPagePathDesc = "首页";
        String leftTableFragment = "音乐主页";
        String onlineHomeFragment = "首页-在线音乐";
        String localFragment = "首页-本地音乐";
        String mineFragment = "首页-我的音乐";

        /*preference*/
        String preferenceSelectFragment = "偏好设置页";

        /*online*/
        String recommendFragment = "个性推荐页";
        String billboardFragment = "排行榜页";
        String categoryFragment = "分类歌单页";
        String categoryFragmentDetail = "分类歌单详情页";

        /*local*/
        String btFragment = "蓝牙音乐页";
        String usbFragment = "USB音乐页";

        /*mine*/
        String collectionFragment = "我的收藏页";
        String historyFragment = "播放历史页";
        String vipCenterFragment = "会员中心页";
        String purchasedMusicFragment = "已购音乐页";
        String similarFragment = "相似歌曲页";

        /*thumb*/
        String onlineThumbPlayerFragment = "迷你播放器-在线";
        String btThumbPlayerFragment = "迷你播放器-蓝牙";
        String usbThumbPlayerFragment = "迷你播放器-USB";
        String emptyThumbPlayerFragment = "迷你播放器-空";

        /*player*/
        String playerActivity = "播放详情页";
        String onlinePlayFragment = "播放详情页-在线";
        String btPlayFragment = "播放详情页-蓝牙";
        String usbPlayFragment = "播放详情页-USB";
        String albumSwitchFragment = "专辑切换页";

        /*search*/
        String searchActivity = "搜索首页-音乐";
        String searchResultActivity = "搜索结果页";
        String searchSingerActivity = "歌手首页";
        String searchResultMusicFragment = "搜索结果-单曲页";
        String searchResultSingerFragment = "搜索结果-歌手页";
        String searchResultSongListFragment = "搜索结果-歌单页";

    }

    //click 名称映射
    interface NormalClick {

        /*preference*/
        String preferenceSkip = "跳过偏好设置";

        /*online*/
        String recommendMusic = "个性推荐";

        /*local*/
        String btMusic = "蓝牙";
        String btGoToSetting = "前往设置";

        /*mine*/
        String collectionMusic = "我的收藏";
        String delete = "删除";
        String isDeleteHistory = "是否清除历史记录";
        String kwLoginout = "注销酷我账户";

        /*thumb*/
        String skipToPlayer = "音乐迷你播放框";
        String emptyMusic = "没有歌曲播放";

        /*player*/
        String exitPlayer = "退出播放详情页";
        String playlist = "播放列表";
        String collection = "收藏/取消收藏";
        String lyric = "查看/隐藏歌词";
        String quality = "查看音质";
        String preMusic = "上一首";
        String nextMusic = "下一首";
        String playlistBackToPlayer = "播放列表-返回播放器";
        String albumSwitchBackToPlayer = "专辑切换-返回播放器";

        /*common*/
        String playOrPause = "播放或暂停";
        String switchMode = "切换播放模式";

        /*search*/
        String searchStart = "搜索";
        String backToSearch = "返回搜索首页";
        String startSearchVoice = "启动语音搜索";
        String clearSearchVoice = "清除搜索历史";
        String cancelSearchVoice = "取消语音搜索";
        String retryLoading = "重新加载网络";
        String backToSearchResult = "返回搜索结果页";

        // 播放
        String ACTION_SLIDE_UP = "向上滑动";
        String seekBar = "音乐播放进度条";
        String searchNow = "立即搜索";
    }
}
