package com.xiaoma.launcher.common.constant;

/**
 * Created by Thomas on 2018/12/28 0028
 * 打点描述
 */

public interface EventConstants {

    //page 路径映射
    interface PagePath {
        String MainActivityPath = "MainActivity";
        String deliciousActivityPath = "DeliciousActivity";
        String deliciousSortActivityPath = "DeliciousSortActivity";
        String deliciousCollectActivityPath = "DeliciousCollectActivity";
        String attractionsActivityPath = "AttractionsActivity";
        String attractionsSortActivityPath = "AttractionsSortActivity";
        String attractionsCollectActivityPath = "AttractionsCollectActivity";
        String nearbyCinemaListActivityPath = "NearbyCinemaListActivity";
        String filmActivityPath = "FilmActivity";
        String filmCollectActivityPath = "FilmCollectActivity";
        String recomHotelActivityPath = "RecomHotelActivity";
        String hotelCollectActivity = "HotelCollectActivity";
        String MovieOrdersActivity = "MovieOrdersActivity";
        String HotelOrdersActivity = "HotelOrdersActivity";
        String ServiceFragment = "ServiceFragment";
        String AudioMainFragment = "AudioMainFragment";
        String ControlPanelFragment = "ControlPanelFragment";
        String AudioListActivityPath = "AudioListActivity";
        String XtingFavoriteActivity = "XtingFavoriteActivity";
    }

    //page 路径映射
    interface PageDescribe {
        String sisclaimerActivityPagePathDesc = "免责详情";
        String SplashActivityPagePathDesc = "启动屏";
        String WelcomeSplashFragmentPagePathDesc = "欢迎页";
        String mainActivityPagePathDesc = "首页";
        String deliciousActivityPagePathDesc = "附近美食";
        String deliciousActivitySort = "美食分类";
        String deliciousActivityCollect = "美食收藏";
        String attractionsActivityPagePathDesc = "附近景点";
        String attractionsActivitySortPagePathDesc = "景点分类";
        String attractionsActivityCollectPagePathDesc = "景点收藏";
        String nearbyCinemaListActivityPagePathDesc = "附近影院";
        String FilmActivityPagePathDesc = "热门影片";
        String SelectCinemaActivityPagePathDesc = "选择影院";
        String FilmTrailerActivityPagePathDesc = "电影预告";
        String FilmCollectActivityPagePathDesc = "电影收藏";
        String FilmOrderCancelActicityPagePathDesc = "支付超时";
        String FilmOrderPayActicityPagePathDesc = "支付页面";
        String FilmOrderSuccessActicityPagePathDesc = "支付成功";
        String RecomHotelActivityPagePathDesc = "附近酒店";
        String hotelCollectActivityPagePathDesc = "酒店收藏";
        String BookUserMsgActivityPagePathDesc = "预定房间数量";
        String SelectDateActivityPagePathDesc = "日期选择";
        String BookHotelTwoActivityPagePathDesc = "填写预定信息";
        String BookHotelThreeActivityPagePathDesc = "扫码支付";
        String BookHotelOneActivityPagePathDesc = "选择房间类型";
        String BookHotelPolicyActivityPagePathDesc = "酒店政策";
        String MovieOrdersActivityPagePathDesc = "电影我的订单";
        String HotelOrdersActivityPagePathDesc = "酒店我的订单";
        String AudioMainFragmentPagePathDesc = "音源";
        String ControlPanelFragmentPagePathDesc = "快捷设置";
        String AppFragmentPagePathDesc = "应用";
        String ServiceFragmentPagePathDesc = "服务";
        String AudioListActivityPagePathDesc = "音乐分类";
        String MusicRecDialogActivityPagePathDesc = "听歌识曲";
        String CalendarActivityPagePathDesc = "日历";
        String ScheduleDetailActivityPagePathDesc = "日程列表";
        String MarkMainActivityPagePathDesc = "mark首页";
        String RecordTripMomentActivityPagePathDesc = "照片预览";
        String TripAlbumActivityPagePathDesc = "路途相册";
        String TripDateilsActivityPagePathDesc = "照片详情";
        String TripFootPrintActivityPagePathDesc = "路途足迹";
        String XtingFavoriteActivityPagePathDesc = "电台收藏";
        String CinemaShowActivityPagePathDesc = "选择电影";
        String SelectSessionActivityPagePathDesc = "选择场次";
        String SeatTableActivityPagePathDesc = "选择座位";
    }

    //click 名称映射
    interface NormalClick {
        String DISCLAIMERTEXT = "查看免责详情";
        String DISCLAIMERBUT = "同意免责";
        String BACK = "返回";
        String RESTARTORPAUSE = "播放或暂停";
        String REPLAY = "重新播放";
        String SPEED = "切换倍速";
        String VIDEOPREVIOUS = "视频-上一曲";
        String VIDEONEXT = "视频-下一曲";
        String DELICIOUS_NAVIGATION = "附近美食-导航";
        String DELICIOUS_PHONE = "附近美食-电话";
        String DELICIOUS_COLLECT_NAVIGATION = "美食收藏-导航";
        String DELICIOUS_COLLECT_PHONE = "美食收藏-电话";
        String DELICIOUS_SORT = "美食分类";
        String ATTRACTION_NAVIGATION = "附近景点-导航";
        String ATTRACTION_PHONE = "附近景点-电话";
        String ATTRACTION_COLLECT_NAVIGATION = "景点收藏-导航";
        String ATTRACTION_COLLECT_PHONE = "景点收藏-电话";
        String NEARBYCINEMA_BUY_TICKETS_NOW = "附近影院-立即购票";
        String NEARBYCINEMA_RENEW_POSITION = "附近影院定位";
        String CINEMA_RENEW_POSITION = "影院定位";
        String NEARBYCINEMA_NAVI = "附近影院-导航";
        String FILM_BUY_TICKETS = "热门电影-立即购票";
        String FILM_LOOK_TRAILER = "附近影院-查看预告片";
        String NEARBYCINEMA_NAVI_SURE = "附近影院-导航-确定";
        String NEARBYCINEMA_NAVI_CANCE = "附近影院-导航-取消";
        String OIL_NAVI_SURE="附近加油站-导航-确定";
        String OIL_NAVI_CANCE="附近加油站-导航-取消";
        String PARK_NAVI_SURE="附近停车场-导航-确定";
        String PARK_NAVI_CANCE="附近停车场-导航-取消";
        String SELECT_CINEMA = "选择影院item";
        String SELECT_SESSION = "选择场次item";
        String CINEMA_SELECT_FILM = "选择某个电影";
        String CINEMA_SHOW_NETWORK_RECONNECTION = "网络重连";
        String CINEMA_SHOW_ITEM = "选择电影item";
        String COLLECT = "收藏";
        String CANCELCOLLECT = "取消收藏";
        String SORTITEM = "分类item";
        String LISTITEM = "列表item";
        String AUDIO_PLAY_LISTITEM = "列表item-播放";
        String AUDIO_STOP_LISTITEM = "列表item-暂停";
        String XTING_AUDIO_PLAY_LISTITEM = "列表item-暂停";
        String XTING_AUDIO_STOP_LISTITEM = "列表item-暂停";
        String DELICIOUS_NAVI_SURE = "附近美食-导航-确定";
        String DELICIOUS_RENEW_POSITION = "附近美食定位";

        String CONTINUE_PLAY = "热门影片-预告片-超速弹窗-继续播放";
        String STOP_CLOSE = "热门影片-预告片-超速弹窗-停止并关闭";
        String DELICIOUS_NAVI_CANCE = "附近美食-导航-取消";
        String DELICIOUS_PHONE_SURE = "附近美食-电话-确定";
        String DELICIOUS_PHONE_CANCE = "附近美食-电话-取消";
        String DELICIOUS_COLLECT_NAVI_SURE = "美食收藏-导航-确定";
        String DELICIOUS_COLLECT_NAVI_CANCE = "美食收藏-导航-取消";
        String DELICIOUS_COLLECT_PHONE_SURE = "美食收藏-电话-确定";
        String DELICIOUS_COLLECT_PHONE_CANCE = "美食收藏-电话-取消";
        String ATTRACTION_NAVI_SURE = "附近景点-导航-确定";
        String ATTRACTION_NAVI_CANCE = "附近景点-导航-取消";
        String ATTRACTION_PHONE_SURE = "附近景点-电话-确定";
        String ATTRACTION_PHONE_CANCE = "附近景点-电话-取消";
        String ATTRACTION_RENEW_POSITION = "附近美食定位";
        String ATTRACTION_COLLECT_NAVI_SURE = "景点收藏-导航-确定";
        String ATTRACTION_COLLECT_NAVI_CANCE = "景点收藏-导航-取消";
        String ATTRACTION_COLLECT_PHONE_SURE = "景点收藏-电话-确定";
        String ATTRACTION_COLLECT_PHONE_CANCE = "景点收藏-电话-取消";
        String SEAT_TABLE_PHONE = "选择座位修改电话";
        String SEAT_TABLE_SUBMIT = "确认选座";
        String SEAT_TABLE_SUBMIT_SURE = "确认选座_确定";
        String SEAT_TABLE_SUBMIT_CANCEL = "确认选座_修改";
        String SEAT_TABLE_SUBMIT_CLOSE = "确认选座_关闭";
        String FILM_TRAILER_RETRY = "电影预告-重试";
        String FILM_TRAILER_CANCEL = "电影预告-取消";
        String FILM_COLLECT_BUY_TICKETS = "影院收藏-立即购票";
        String FILM_COLLECT_PHONE = "影院收藏-电话";
        String FILM_COLLECT_PHONE_SURE = "影院收藏-电话-立即拨打";
        String FILM_COLLECT_PHONE_CANCE = "影院收藏-电话-算了";
        String FILM_ORDER_SUCCESS_NAVI = "支付成功—导航";
        String FILM_ORDER_SUCCESS_PHONE = "支付成功-电话";
        String FILM_ORDER_CANCEL_NAVI = "支付超时-导航";
        String FILM_ORDER_CANCEL_PHONE = "支付超时-电话";
        String FILM_ORDER_CANCEL_RESCHEDULE = "支付超时-重新预定";
        String FILM_ORDER_PAY_SURE = "支付页面-确认支付";

        String HOTEL_CHECKIN_TIME = "修改入住时间";
        String HOTEL_COLLECT_NAVI = "酒店导航";
        String HOTEL_RENEW_POSITION = "酒店定位";
        String HOTEL_NAVI = "酒店导航";
        String HOTEL_NAVI_SURE = "酒店导航-立即导航";
        String HOTEL_NAVI_CANCEL = "酒店导航-算了";
        String HOTEL_CLLECT_NAVI_SURE = "酒店收藏导航-立即导航";
        String HOTEL_CLLECT_NAVI_CANCEL = "酒店收藏导航-算了";
        String HOTEL_PHONE = "酒店电话";
        String HOTEL_COLLECT_PHONE = "酒店电话";
        String HOTEL_PHONE_SURE = "酒店电话-立即拨打";
        String HOTEL_PHONE_CANCE = "酒店电话-算了";
        String HOTEL_CLLECT_PHONE_SURE = "酒店收藏电话-立即拨打";
        String HOTEL_CLLECT_PHONE_CANCE = "酒店收藏电话-算了";
        String HOTEL_BOOK = "酒店预定";
        String HOTEL_CLLECT_BOOK = "酒店收藏预定";
        String HOTEL_DATA_CANCE = "入住日期-清除";
        String HOTEL_DATA_SURE = "入住日期-确定";
        String HOTEL_DATA_PRE = "入住日期-上个月";
        String HOTEL_DATA_NEXT = "入住日期-下个月";
        String HOTEL_SELECT_HOME = "选择房间item";
        String HOTEL_HOME_NUMBER = "预定房间数";
        String HOTEL_HOME_PHONE = "联系方式";
        String HOTEL_HOME_GOTO_PAY = "去支付";
        String HOTEL_HOME_INCREASE = "增加房间";
        String EDITMSG_DIALOG_SURE = "编辑窗-确定";
        String EDITMSG_DIALOG_CANCEL = "编辑窗-取消";
        String HOTEL_HOME_EDIT = "编辑房间";
        String HOTEL_HOME_DELETE = "删除房间";
        String HOTEL_HOME_PAY_CANCE = "取消支付";
        String HOTEL_HOME_PAY_RETRY = "重新预定";
        String HOTEL_HOME_PAY_STATUS = "支付状态";
        String HOTEL_HOME_PAY_PHONE = "支付-电话";
        String HOTEL_HOME_PAY_NAVI = "支付-导航";
        String HOTEL_PAY_SURE = "支付页面-取消预定";
        String HOTEL_PAY_CANCE = "支付页面-取消预定-算了";
        String HOTEL_PAY_BACK_SURE = "支付页面-离开-确定";
        String HOTEL_PAY_BACK_CANCE = "支付页面-离开-算了";
        String HOTEL_ORDER_ITEM_NAVI = "酒店订单-导航";
        String HOTEL_ORDER_ITEM_NAVI_SURE = "酒店订单-导航_立即导航";
        String HOTEL_ORDER_ITEM_NAVI_CANCE = "酒店订单-导航_算了";
        String MOVE_ORDER_ITEM_NAVI = "电影订单-导航";
        String MOVE_ORDER_ITEM_NAVI_SURE = "电影订单-导航_立即导航";
        String MOVE_ORDER_ITEM_NAVI_CANCE = "电影订单-导航_算了";
        //-MainActivity相关
        String MAINACTIVITY_RADIO_BUTTON_NAVI = "地图";
        String MAINACTIVITY_RADIO_BUTTON_MUSIC = "音源";
        String MAINACTIVITY_RADIO_BUTTON_SERVICE = "服务";
        String MAINACTIVITY_RADIO_BUTTON_APP = "应用";
        String MAINACTIVITY_BETTON_PET = "首页宠物";
        String AUDIOMAIN_MUSIC_FAVORITE = "音乐收藏";
        String AUDIOMAIN_XTING_FAVORITE = "电台收藏";
        String CONTROL_BLUETOOTH = "快捷设置-蓝牙";
        String CONTROL_INTERNET = "快捷设置-网络";
        String CONTROL_HOTSPOT = "快捷设置-热点";
        String CONTROL_WIFI = "快捷设置-WiFi";
        String CONTROL_POWER = "快捷设置-息屏";
        String CONTROL_VOLUME = "快捷设置-音量";
        String CONTROL_BRIGHTNESS = "快捷设置-亮度";
        String APP_USER_HEAD = "应用-头像";
        String SERVICE_ITEM_GAS = "附近加油站";
        String SERVICE_ITEM_PARKING = "附近停车场";
        String SERVICE_ITEM_CAR_MAINTENANCE = "汽车保养";
        String SERVICE_ITEM_DELICIOUS = "附近美食";
        String SERVICE_ITEM_DELICIOUS_SORT = "美食分类";
        String SERVICE_ITEM_DELICIOUS_COLLECT = "美食收藏";
        String SERVICE_ITEM_ATTRACTIONS = "附近景点";
        String SERVICE_ITEM_ATTRACTIONS_SORT = "景点分类";
        String SERVICE_ITEM_ATTRACTIONS_COLLECT = "景点收藏";
        String SERVICE_ITEM_FILM = "热门电影";
        String SERVICE_ITEM_CINEMA = "附近影院";
        String SERVICE_ITEM_FILM_ORDER = "影院订单";
        String SERVICE_ITEM_FILM_COOLECT = "影院收藏";
        String SERVICE_ITEM_HOTEL = "附近酒店";
        String SERVICE_ITEM_HOTEL_ORDER = "酒店订单";
        String SERVICE_ITEM_HOTEL_COOLECT = "酒店收藏";
        String APP_NAME = "应用";
        String CONTROL_APP_PLUGIN = "应用插件";
        String XTING_FAVORITE_CLOSE = "关闭";
        String XTING_FAVORITE_ITEM = "电台收藏item";

        //AudioMainActivity
        String AUDIO_ITEM_BT_MUSIC = "音源-蓝牙音乐";
        String AUDIO_ITEM_USB_MUSIC = "音源-USB音乐";
        String AUDIO_ITEM_TYPE = "音源类型";
        String AUDIO_PLAYER = "播放暂停";
        String AUDIO_PREVIOUS = "上一首";
        String AUDIO_NEXT = "下一首";
        String AUDIO_COLLECT = "收藏";
        String AUDIO_LIST = "列表";
        String AUDIO_LIST_CLOSE = "关闭页面";
        String AUDIO_LIST_ITEM_PLAY_TYPE = "播放状态";
        String AUDIO_LIST_ITEM_PLAY = "开始播放";
        String AUDIO_LIST_ITEM_STOP = "停止播放";

        //听歌识曲相关
        String MUSIC_REC_HISTORY_LIST = "识别记录item";
        String MUSIC_REC_TYPE = "识别状态";
        String MUSIC_REC_START = "开始";
        String MUSIC_REC_STOP = "关闭";
        String MUSIC_REC_RETRY = "重试";
        String MUSIC_REC_HISTORY = "识别记录";
        String MUSIC_REC_CANCEL_HISTORY = "清空全部记录";
        String MUSIC_REC_CANCEL_HISTORY_SURE = "清空全部记录-确定";
        String MUSIC_REC_CANCEL_HISTORY_CANCEL = "清空全部记录-取消";
        String MUSIC_REC_CLOSE = "关闭";
        String MUSIC_REC_SUCESS = "识别成功";
        String MUSIC_REC_HISTORY_NETWORK_RETRY = "识别记录网络重连";


        //日历相关
        String CALENDAR_ADD_SCHEDULE = "添加日程";
        String CALENDAR_ADD_SCHEDULE_SURE = "确认创建";
        String CALENDAR_ADD_SCHEDULE_INFO = "点击添加位置信息";
        String CALENDAR_ADD_SCHEDULE_CANCEL = "添加日程-取消";
        String CALENDAR_DAY = "日期";
        String SCHEDULE_DETAIL_EDIT = "日程编辑";
        String SCHEDULE_DETAIL_NETWORK_RECONNECTION = "刷新网络";
        String SCHEDULE_DETAIL_SELECT_ADDRESS = "选择地址";
        String SCHEDULE_DETAIL_DELETE = "删除日程";

        //mark相关
        String MARK_ABLUM = "旅途相册";
        String MARK_FOOT = "旅途足迹";
        String MARK_TAKE_PHOTO = "拍照";
        String MARK_RECORD_SAVE = "保存";
        String MARK_RECORD_CLEAN = "取消";
        String MARK_RECORD_HAPPY = "开心";
        String MARK_RECORD_DULL = "平常";
        String MARK_RECORD_LOSE = "沮丧";
        String MARK_RECORD_EDIT = "编辑";
        String MARK_RECORD_PHOTO_ITEM = "相册item";
        String MARK_RECORD_PHOTO_ITEM_DELETE = "相册item删除";
        String MARK_RECORD_PHOTO_ITEM_DELETE_SURE = "相册item删除-确定";
        String MARK_RECORD_PHOTO_ITEM_DELETE_CANCEL = "相册item删除-取消";
        String MARK_RECORD_PHOTO_DATEILS_DELETE = "删除";
        String MARK_RECORD_PHOTO_DATEILS_DELETE_SURE = "删除-确定";
        String MARK_RECORD_PHOTO_DATEILS_DELETE_CANCEL = "删除-取消";

        //推送Dialog
        String RECOMMEND_DIALOG_SURE = "推送Dialog确定";
        String RECOMMEND_DIALOG_CANCEL = "推送Dialog取消";

        //模式Dialog
        String SELECT_MODE_SURE = "模式-确定";
        String SELECT_MODE_CANCEL = "模式-取消";
        String SELECT_MODE_LIFE = "生活模式";
        String SELECT_MODE_WORK = "工作模式";
        String SELECT_MODE_TRAVEL = "旅游模式";
        String SELECT_MODE_QUIET = "我想静静";

        //电影票，酒店
        String BOOK_MOVIE = "预订电影票";
        String BOOK_HOTEL = "预订酒店";
    }

}
