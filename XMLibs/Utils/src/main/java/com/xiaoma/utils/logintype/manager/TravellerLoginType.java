package com.xiaoma.utils.logintype.manager;

import android.text.TextUtils;

import com.xiaoma.utils.logintype.bean.AccountType;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.constant.LoginTypeModel;

import java.util.HashMap;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/06
 * @Describe: 游客模式--登录
 */

public class TravellerLoginType extends LoginType {
    protected String[] config_arr = {
            LoginCfgConstant.APP,//车应用
            LoginCfgConstant.CLUB,//车信
            LoginCfgConstant.PERSONAL,//个人中心
            LoginCfgConstant.SHOP,//奔腾商城
            LoginCfgConstant.PET,//宠物互动
            LoginCfgConstant.MOTORCADE,//车队

//            LoginCfgConstant.MARK,
            LoginCfgConstant.MOVIE_TICKETS,//电影票购买
            LoginCfgConstant.ORDER_HOTEL,//预定酒店
            LoginCfgConstant.SERVICE_INIT_ORDER,//车服务 发起订单
            LoginCfgConstant.SERVICE_CHECK_ORDER,//车服务 查看订单
            LoginCfgConstant.SERVICE_CHECK_MORE,//车服务 查看更多
            LoginCfgConstant.SERVICE_CAR_MAINTENANCE_PLAN,//车服务 养车计划
            LoginCfgConstant.CARPARK_ACTIVITY,//车乐园 活动
//            LoginCfgConstant.SETTING_SOUND_SETTINGS,//设置 音效设置
//            LoginCfgConstant.SETTING_VEHICLE_SETTING,// 设置 车辆设置
//            LoginCfgConstant.SETTING_GENERAL_SETTINGS,// 设置 通用设置
            LoginCfgConstant.BEGINNER_S_GUIDE,//新手引导
//            LoginCfgConstant.CAR_WECHAT,//车载微信
//            LoginCfgConstant.XIAOMA_SMART_HOME,//智能家居
            LoginCfgConstant.VRPRACTICE,//语音训练
            // 语音助理
            //声音控制
//            LoginCfgConstant.VOLUME_MINUS,//声音小点
//            LoginCfgConstant.VOLUME_PLUS,//声音大点
//            LoginCfgConstant.VOLUME_MUTE,//静音
//            LoginCfgConstant.VOLUME_UNMUTE,//取消静音
//            LoginCfgConstant.VOLUME_MAX,//音量最大
//            LoginCfgConstant.VOLUME_MIN,//音量最小
//            LoginCfgConstant.VOLUME_ADJUST,//具体音量
//            LoginCfgConstant.MODE_SSE,//音效模式设置成 标准
//            LoginCfgConstant.MODE_PSE,//音效模式设置成 流行
//            LoginCfgConstant.MODE_CLSE,//音效模式设置成 古典
//            LoginCfgConstant.MODE_JSE,//音效模式设置成 爵士
//            LoginCfgConstant.MODE_CUSE,//音效模式设置成 自定义
//            LoginCfgConstant.OPEN_STSE,//开启Arkamys3D音效
//            LoginCfgConstant.CLOSE_STSE,//关闭Arkamys3D音效
//            LoginCfgConstant.VITS_MAX,//车辆信息提示音设置为“大”
//            LoginCfgConstant.VITS_MIN,//车辆信息提示音设置为“小”
//
//            LoginCfgConstant.MODE_SSF,//声场模式设置成 标准
//            LoginCfgConstant.MODE_OHSF,//声场模式设置成 歌剧院
//            LoginCfgConstant.MODE_CHSF,//声场模式设置成 音乐厅

            // 屏幕亮度
//            LoginCfgConstant.BRIGHTNESS_MINUS,//降低屏幕亮度
//            LoginCfgConstant.BRIGHTNESS_PLUS,//调亮
//            LoginCfgConstant.BRIGHTNESS_MAX,//屏幕最亮
//            LoginCfgConstant.BRIGHTNESS_MIN,//屏幕最暗
//            LoginCfgConstant.KEYPADLIGHT_PLUS,//调高按键亮度
//            LoginCfgConstant.KEYPADLIGHT_MINUS,//调低按键亮度
//            LoginCfgConstant.MODE_DAY,//屏幕白天模式
//            LoginCfgConstant.MODE_NIGHT,//屏幕黑夜模式
            // 主题设置
//            LoginCfgConstant.CHANGE_THEME,//切换主题
//            LoginCfgConstant.SET_THEME,//设置指定主题
//            LoginCfgConstant.MODE_AUTO,//屏幕自动模式
//            LoginCfgConstant.AMBIENT_LIGHT,//氛围灯
            //  舒服设置
//            LoginCfgConstant.OPEN_RMAFOLDING,//开启后视镜自动折叠
//            LoginCfgConstant.CLOSE_RMAFOLDING,//关闭后视镜自动折叠
//            LoginCfgConstant.OPEN_BRLBW,//打开后排安全带未系提醒开关
//            LoginCfgConstant.CLOSE_BRLBW,//关闭后排安全带未系提醒开关
//            LoginCfgConstant.BOARDING_LIGHTING,//登车照明
//            LoginCfgConstant.OUT_OF_CAR_LIGHTING,//离车照明
            //   时间设置
//            LoginCfgConstant.OPEN_NETSYN,//开启网络同步时间开关
//            LoginCfgConstant.CLOSE_NETSYN,//关闭网络同步时间开关
            // 安全设置
//            LoginCfgConstant.OPEN_COLWARNING,//开启前碰撞预警
//            LoginCfgConstant.CLOSE_COLWARNING,//开启前碰撞预警

    };

    //一般用于登录后的初始化
    public TravellerLoginType(AccountType accountType) {
        this(accountType, "");
    }

    //一般用于登录时的初始化
    public TravellerLoginType(AccountType accountType, String userId) {
        super(accountType, userId);
        initPurviewConfig();
    }

    @Override
    public AccountType buildAccount() {
        AccountType accountType = new AccountType();
        accountType.setLoginType(LoginTypeModel.LOGIN_TRAVELLER_MODEL);
        writeToLocal(accountType);
        return accountType;
    }

    @Override
    public void initPurviewConfig() {
        mConfigs = new HashMap<>();
        if (config_arr == null || config_arr.length <= 0) return;
        for (int i = 0; i < config_arr.length; i++) {
            String config = config_arr[i];
            mConfigs.put(config, config);
        }
    }

    /**
     * @param condition 条件
     * @param callback  回调
     * @return 返回true，说明功能能使用，返回false，说明功能不能使用
     */
    @Override
    public boolean judgeUse(String condition, OnBlockCallback callback) {
        if (TextUtils.isEmpty(condition)) return true;
        String result = mConfigs.get(condition);
        // 为 null ,说明没有功能限制，不为null，说明有功能限制
        if (TextUtils.isEmpty(result)) return true;
        if (callback == null) return false;
        if (LoginCfgConstant.PERSONAL.equals(result)) {//游客模式下的个人中心进行子账号注册流程
            if (callback.onChooseAccount(this)) return false;
        }
        //吐司,onShowToast 返回true，说明逻辑已经自己处理，逻辑不在往后
        if (callback.onShowToast(this)) return false;
        callback.handle(this);
        return false;
    }
}
