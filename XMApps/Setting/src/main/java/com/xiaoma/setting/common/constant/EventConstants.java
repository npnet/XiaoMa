package com.xiaoma.setting.common.constant;

public interface EventConstants {

    //page 路径映射
    interface PageDescribe {
        String mainActivityPagePathDesc = "首页";
        String blueToothSettingFragmentPagePathDesc = "蓝牙设置";
        String blueToothControlFragmentPagePathDesc = "蓝牙列表";
        String wifiSettingFragmentPagePathDesc = "wifi设置";
        String wifiConnectionFragmentPagePathDesc = "wifi连接";
        String wifiHotSpotFragmentPagePathDesc = "wifi热点";
        String soundEffectsSettingFragmentPagePathDesc = "声音设置";
        String volumeSettingFragmentPagePathDesc = "音量设置";
        String effectsSettingFragmentPagePathDesc = "音效设置";
        String otherSettingFragmentPagePathDesc = "其他设置";
        String displaySettingFragmentPagePathDesc = "屏幕设置";
        String themeSettingFragmentPagePathDesc = "主题设置";
        String languageSettingFragmentPagePathDesc = "语言设置";
        String versionSettingFragmentPagePathDesc = "版本设置";
        String resetSettingFragmentPagePathDesc = "出厂设置";
        String carSettingFragmentPagePathDesc = "车辆设置";
        String safetySettingFragmentPagePathDesc = "安全设置";
        String comfortSettingFragmentPagePathDesc = "舒适设置";
        String lamplightSettingFragmentPagePathDesc = "灯光设置";
        String editDialogPagePathDesc = "编辑输入弹窗";
        String assistantSettingFragmentPagePathDesc = "语音设置";
    }

    //click 名称映射
    interface NormalClick {
        String editBltName = "编辑蓝牙名称";
        String addDevices = "添加设备";
        String editHotspotName = "编辑wifi热点名称";
        String editHotspotPsw = "编辑wifi热点密码";
        String themeOne = "主题一";
        String themeTwo = "主题二";
        String themeThree = "主题三";
        String showDate = "设置日期";
        String showTime = "设置时间";
        String showZone = "设置时区";
        String versionUpdateSearch = "版本更新查询询问";
        String versionUpdate = "版本更新询问";
        String resetSetting = "恢复出厂设置询问弹窗";
        String cancelEditBlueName = "编辑蓝牙名称-取消";
        String confirmEditBlueName = "编辑蓝牙名称-确定";
        String cancelEditWifiHotSpotName = "编辑wifi热点名称-取消";
        String confirmEditWifiHotSpotName = "编辑wifi热点名称-确定";
        String cancelEditWifiHotSpotPwd = "编辑wifi热点密码-取消";
        String confirmEditWifiHotSpotPwd = "编辑wifi热点密码-确定";
        String cancelEditWifiPwd = "连接wifi密码-取消";
        String confirmEditWifiPwd = "连接wifi密码-确定";
        String cancelReset = "恢复出厂设置-取消";
        String confirmReset = "恢复出厂设置-确定";
        String cancelVersionSearch = "版本更新查询-取消";
        String confirmVersionSearch = "版本更新查询-确定";
        String cancelVersionUpdate = "版本更新-取消";
        String confirmVersionUpdate = "版本更新-确定";
        String bluetoothSetting = "蓝牙开关";
        String netWorkSetting = "WIFI网络开关";
        String hotSpotSetting = "WIFI热点开关";
        String SoundEffetsSetting = "3D音效设置";
        String SubwooferSetting = "虚拟低音炮开关";
        String onOffMusicSetting = "开关机音乐开关";
        String watchVideoSetting = "行车过程中观看视频";
        String syncTimeSetting = "同步网络时间";
        String autoTimeZoneSetting = "同步网络时间";
    }

    interface slideEvent {
        String screenBrightnessSetting = "屏幕亮度";
        String keyBrightnessSetting = "按键亮度";
        String mediaVolumeSetting = "媒体音量";
        String bluetoothMusicSetting = "蓝牙音乐";
        String callVolumeSetting = "通话音量";
        String speechVolumeSetting = "语音音量";
        String soundEffect50Setting = "按键亮度";
        String soundEffect200Setting = "媒体音量";
        String soundEffect800Setting = "蓝牙音乐";
        String soundEffect3200Setting = "通话音量";
        String soundEffect10000Setting = "语音音量";
    }


}
