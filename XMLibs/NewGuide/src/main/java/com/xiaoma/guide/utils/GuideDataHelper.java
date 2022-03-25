package com.xiaoma.guide.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.guide.bean.BaseGuideStatusBean;
import com.xiaoma.guide.bean.GuideStatusJsonBean;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.utils.tputils.TPUtils;

import java.io.File;

import static com.xiaoma.guide.utils.GuideConstants.APPSTORE_SHOWED;
import static com.xiaoma.guide.utils.GuideConstants.CAR_PARK_SHOWED;
import static com.xiaoma.guide.utils.GuideConstants.CLUB_SHOWED;
import static com.xiaoma.guide.utils.GuideConstants.LAUNCHER_SHOWED;
import static com.xiaoma.guide.utils.GuideConstants.MUSIC_SHOWED;
import static com.xiaoma.guide.utils.GuideConstants.PERSONAL_SHOWED;
import static com.xiaoma.guide.utils.GuideConstants.PET_SHOWED;
import static com.xiaoma.guide.utils.GuideConstants.SERVICE_SHOWED;
import static com.xiaoma.guide.utils.GuideConstants.SHOP_SHOWED;
import static com.xiaoma.guide.utils.GuideConstants.XTING_SHOWED;


/**
 * 在个人中心二次打开新手引导的文件操作类
 */
public class GuideDataHelper {
    private static final String TAG = "GuideDataHelper";
    // 新手引导是否是从头开始的 避免其他应用 直接启动中间的页面 导致新手引导显示异常（不从第一张开始 也不显示最后一张）
    public static boolean isGuideStartFromHead;

    /**
     * 是否应该显示新手引导
     *
     * @param label        app的标识
     * @param isFirstGuide 是不是当前应用内的第一个引导
     * @return true 显示新手引导 false 不显示
     */
    public static boolean shouldShowGuide(String label, boolean isFirstGuide) {
        if(!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.BEGINNER_S_GUIDE)) return false;
        BaseGuideStatusBean guideStatusBean = getGuideDataByLabel(label);
        if (guideStatusBean == null) {
            Log.d(TAG, "shouldShowGuide: please confirm the label is right");
            return false;
        }
        if (isFirstGuide) {
            boolean firstGuide = guideStatusBean.isFirstGuide();
            boolean guideShow = guideStatusBean.isGuideShow();
            // 避免引导中途退出 所以设置isFirstGuide标识
            // 如果guideShow为false 但 isFirstGuide为true 则为显示了新手引导 但中途退出 此情况 不再显示引导
            if (!guideShow && !firstGuide) {
                finishGuideData(label);
                return false;
            }
            return firstGuide && !guideShow;
        } else {
            Log.d(TAG, "shouldShowGuide: isGuideStartFromHead=" + isGuideStartFromHead);
            return !guideStatusBean.isGuideShow();
        }
    }

    /**
     * 是否应该显示新手引导
     * 第一个新手引导查询文件 保存结果至TP
     * 后面的查询TP
     *
     * @param labelShow    guideShow
     * @param labelFirst   guideFirst
     * @param isFirstGuide 是不是当前应用内的第一个引导
     * @return true 显示新手引导 false 不显示
     */
    public static boolean shouldShowGuide(Context context, String labelShow, String labelFirst, boolean isFirstGuide) {
        if(!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.BEGINNER_S_GUIDE)) return false;
        if (isFirstGuide) {
            isGuideStartFromHead = true;
            BaseGuideStatusBean guideStatusBean = getGuideDataByLabel(labelShow);
            if (guideStatusBean == null) {
                Log.d(TAG, "shouldShowGuide: please confirm the label is right");
                return false;
            }
            boolean guideFirst = guideStatusBean.isFirstGuide();
            boolean guideShow = guideStatusBean.isGuideShow();
            // 避免引导中途退出 所以设置isFirstGuide标识
            // 如果guideShow为false 但 isFirstGuide为true 则为显示了新手引导 但中途退出 此情况 不再显示引导
            if (!guideShow && !guideFirst) {
                finishGuideData(labelShow);
                TPUtils.put(context, labelShow, true);
                TPUtils.put(context, labelFirst, false);
                return false;
            }
            TPUtils.put(context, labelShow, guideShow);
            TPUtils.put(context, labelFirst, guideFirst);
            return guideFirst && !guideShow;
        } else {
            Boolean guideShow = TPUtils.get(context, labelShow, false);
            Log.d(TAG, "shouldShowGuide: isGuideStartFromHead=" + isGuideStartFromHead);
            return isGuideStartFromHead && !guideShow;
        }
    }

    /**
     * 重置引导标识
     *
     * @param type
     */
    public static void resetGuideData(String type) {
        GuideStatusJsonBean guideStatusJsonBean = getGuideData();
        BaseGuideStatusBean statusBean = getStatusBean(type, guideStatusJsonBean);
        if (statusBean != null) {
            if (!statusBean.isGuideShow() && statusBean.isFirstGuide()) return;
            statusBean.setGuideShow(false);
            statusBean.setFirstGuide(true);
            saveGuideData(guideStatusJsonBean);
        }
    }

    /**
     * 设置引导结束标识
     *
     * @param label
     */
    public static void finishGuideData(String label) {
        GuideStatusJsonBean guideStatusJsonBean = getGuideData();
        BaseGuideStatusBean statusBean = getStatusBean(label, guideStatusJsonBean);
        if (statusBean != null) {
            statusBean.setFirstGuide(false);
            statusBean.setGuideShow(true);
            saveGuideData(guideStatusJsonBean);
        }
    }

    public static GuideStatusJsonBean getGuideData() {
        File guideFile = ConfigManager.FileConfig.getGuideFile();
        String json = FileUtils.read(guideFile);
        // 第一次为空 写入
        if (TextUtils.isEmpty(json)) {
            GuideStatusJsonBean guideStatusJsonBean = new GuideStatusJsonBean();
            guideStatusJsonBean.setXting(new GuideStatusJsonBean.Xting());
            guideStatusJsonBean.setMusic(new GuideStatusJsonBean.Music());
            guideStatusJsonBean.setPersonal(new GuideStatusJsonBean.Personal());
            guideStatusJsonBean.setAppStore(new GuideStatusJsonBean.AppStore());
            guideStatusJsonBean.setService(new GuideStatusJsonBean.Service());
            guideStatusJsonBean.setLauncher(new GuideStatusJsonBean.Launcher());
            guideStatusJsonBean.setClub(new GuideStatusJsonBean.Club());
            guideStatusJsonBean.setCarPark(new GuideStatusJsonBean.CarPark());
            guideStatusJsonBean.setShop(new GuideStatusJsonBean.Shop());
            guideStatusJsonBean.setPet(new GuideStatusJsonBean.Pet());
            saveGuideData(guideStatusJsonBean);
            return guideStatusJsonBean;
        }
        GuideStatusJsonBean guideStatusJsonBean = GsonHelper.fromJson(json, GuideStatusJsonBean.class);
        return guideStatusJsonBean;
    }

    public static BaseGuideStatusBean getGuideDataByLabel(String type) {
        GuideStatusJsonBean guideData = getGuideData();
        BaseGuideStatusBean statusBean = getStatusBean(type, guideData);
        return statusBean;
    }

    /**
     * 设置firstGuide 为false
     *
     * @param label
     */
    public static void setFirstGuideFalse(String label) {
        GuideStatusJsonBean guideData = getGuideData();
        BaseGuideStatusBean statusBean = getStatusBean(label, guideData);
        statusBean.setFirstGuide(false);
        saveGuideData(guideData);
    }

    /**
     * 保存引导标识
     *
     * @param guideStatusJsonBean
     * @return
     */
    public static Boolean saveGuideData(GuideStatusJsonBean guideStatusJsonBean) {
        File guideFile = ConfigManager.FileConfig.getGuideFile();
        String result = GsonHelper.toJson(guideStatusJsonBean);
        Log.d(TAG, "saveGuideData: result\n" + result);
        return FileUtils.write(result, guideFile, false);
    }

    private static BaseGuideStatusBean getStatusBean(String type, GuideStatusJsonBean guideStatusJsonBean) {
        BaseGuideStatusBean statusBean = null;
        switch (type) {
            case XTING_SHOWED:
                statusBean = guideStatusJsonBean.getXting();
                break;
            case MUSIC_SHOWED:
                statusBean = guideStatusJsonBean.getMusic();
                break;
            case PERSONAL_SHOWED:
                statusBean = guideStatusJsonBean.getPersonal();
                break;
            case SERVICE_SHOWED:
                statusBean = guideStatusJsonBean.getService();
                break;
            case APPSTORE_SHOWED:
                statusBean = guideStatusJsonBean.getAppStore();
                break;
            case CLUB_SHOWED:
                statusBean = guideStatusJsonBean.getClub();
                break;
            case SHOP_SHOWED:
                statusBean = guideStatusJsonBean.getShop();
                break;
            case LAUNCHER_SHOWED:
                statusBean = guideStatusJsonBean.getLauncher();
                break;
            case CAR_PARK_SHOWED:
                statusBean = guideStatusJsonBean.getCarPark();
                break;
            case PET_SHOWED:
                statusBean = guideStatusJsonBean.getPet();
        }
        return statusBean;
    }
}
