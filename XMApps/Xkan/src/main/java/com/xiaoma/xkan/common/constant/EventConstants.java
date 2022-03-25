package com.xiaoma.xkan.common.constant;

public interface EventConstants {

    //page 路径映射
    interface PageDescribe {
        String MAINACTIVITYPAGEPATHDESC = "首页";
        String XMPHOTOACTIVITYPAGEPATHDESC = "图片详情页";
        String VIDEOPLAYACTIVITYPAGEPATHDESC = "视频详情页";
        String MAINFRAGMENTPAGEPATHDESC = "全部";
        String USBCONNSTATEFRAGMENTPAGEPATHDESC = "USB";
        String PICTUREFRAGMENTPAGEPATHDESC = "图片列表";
        String VIDEOFRAGMENTPAGEPATHDESC = "视频列表";
    }

    //click 名称映射
    interface NormalClick {
        String ALL = "全部";
        String VIDEO = "视频";
        String PICTURE = "图片";
        String NAME = "名称";
        String DATE = "日期";
        String SIZE = "大小";
        String BACK = "返回";
        String RESTARTORPAUSE = "播放或暂停";
        String RETRY = "重试";
        String REPLAY = "重新播放";
        String SPEED = "切换倍速";
        String VIDEOPREVIOUS = "视频-上一曲";
        String VIDEONEXT = "视频-下一曲";
        String INCREASE = "放大";
        String ROTATE = "旋转";
        String PICTUREPREVIOUS = "图片-上一张";
        String PICTURENEXT = "图片-下一张";
        String CLOSE = "关闭";
        String CONTINUE_PLAY = "视频页面-超速弹窗-继续播放";
        String STOP_CLOSE = "视频页面-超速弹窗-停止并关闭";
    }

    //滑动 名称映射
    interface SlideEvent {
        String VIDEO_TRACK = "视频快进/快退";
        String PICTURE_TRACK = "图片查看滑动";
    }

}
