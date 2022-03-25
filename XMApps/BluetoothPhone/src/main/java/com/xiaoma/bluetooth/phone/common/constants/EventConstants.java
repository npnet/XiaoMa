package com.xiaoma.bluetooth.phone.common.constants;

public interface EventConstants {

    //page 路径映射
    interface PageDescribe {
        String bluetoothPhonePagePathDesc = "蓝牙电话";
        String dialPadFragmentPagePathDesc = "拨号键盘页面";
        String callLogFragmentPagePathDesc = "通话记录页面";
        String contactFragmentPagePathDesc = "联系人页面";
        String collectionFragmentPagePathDesc = "收藏联系人页面";
        String dialingFragmentPagePathDesc = "拨号中/来电中页面";
        String phoneFragmentPagePathDesc = "通话页面";
    }

    //click 名称映射
    interface NormalClick {
        String refresh = "同步通讯录及联系人";
        String close = "退出应用";
        String toConnectBluetooth = "跳转设置应用连接蓝牙";
        String call = "拨号";
        String minimum = "缩小为最小化窗口";
        String hangup = "挂断";
        String hangupAndAnswer = "挂断并接听";
        String keepAndAnswer = "保持并接听";
        String mute = "开启/关闭静音";
        String contactook = "通讯录";
        String answerType = "切换手机/车机接听";
        String dialpad = "拨号键盘";
        String switchCall = "三方通话时切换通话对象";
        String connectBluetooth = "连接蓝牙";
        String confirmRequestWindowPermission = "申请悬浮窗权限-确定";
        String cancelRequestWindowPermission = "申请悬浮窗权限-取消";
        String cancelKeep = "取消保留";
        String collect = "收藏";
        String cancelCollect = "取消收藏";
    }

}
