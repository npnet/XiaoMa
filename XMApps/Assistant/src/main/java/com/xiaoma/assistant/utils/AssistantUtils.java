package com.xiaoma.assistant.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.model.RecognitionWord;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.login.LoginManager;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;

import java.util.List;
import java.util.Random;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/9/30
 * Desc：Assistant工具类
 */
public class AssistantUtils {
    //欢迎语
    static int[] mWelcomes = new int[]{R.string.welcome_word, R.string.welcome_word1,
            R.string.welcome_word2, R.string.welcome_word3};
    //语音3.0欢迎语
    static int[] mWelcomesV2 = new int[]{R.string.ivw_v2_welcome_word1, R.string.ivw_v2_welcome_word2,
            R.string.ivw_v2_welcome_word3};
    //识别错误提示语
    public static int[] mUnStands = new int[]{R.string.can_not_understand, R.string.can_not_understand1,
            R.string.can_not_understand2};  //识别错误提示语
    public static int[] mUnStands_Speak = new int[]{R.string.can_not_understand, R.string.can_not_understand1_speak,
            R.string.can_not_understand2};

    /**
     * 随机欢迎语
     */
    public static int getWelcomeWord() {
        Random random = new Random();
        int i = random.nextInt(mWelcomes.length);
        return mWelcomes[i];
    }


    /**
     * 语音3.0随机欢迎语
     */
    public static int getWelcomeWordV2() {
        Random random = new Random();
        int i = random.nextInt(mWelcomesV2.length);
        return mWelcomesV2[i];
    }

    public static String getWelcomeWord(Context context) {
        String welcomeWord = null;
        if (context != null) {
            String type = XmProperties.build(LoginManager.getInstance().getLoginUserId()).get(VrConstants.WELCOME_TYPE_KEY, VrConstants.WELCOME_TYPE_MEDIA);
            if (VrConstants.WELCOME_TYPE_TEXT.equals(type)) {
                welcomeWord = XmProperties.build(LoginManager.getInstance().getLoginUserId()).get(VrConstants.WELCOME_TYPE_TEXT_KEY, "");
            }
        }
        //获取不到文本时为随机
        if (TextUtils.isEmpty(welcomeWord)) {
            welcomeWord = context.getString(getWelcomeWordV2());
        }
        return welcomeWord;
    }


    public static boolean isWelcomeMedia(Context context) {
        boolean result = true;
        if (context != null) {
            String type = XmProperties.build(LoginManager.getInstance().getLoginUserId()).get(VrConstants.WELCOME_TYPE_KEY, VrConstants.WELCOME_TYPE_MEDIA);
            if (VrConstants.WELCOME_TYPE_TEXT.equals(type)) {
                result = false;
            }
        }
        return result;
    }


    /**
     * 随机错误提示语
     */
    public static int getUnStandWord() {
        XmCarFactory.getCarVendorExtensionManager().setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_ERROR_SPEAK);
        Random random = new Random();
        int i = random.nextInt(mUnStands_Speak.length);
        return i;
    }

    /**
     * 数字转中文
     */
    public static String numberToChinese(int number) {
        if (number == 0) {
            return "零";
        } else if (number == 1) {
            return "一";
        } else if (number == 2) {
            return "二";
        } else if (number == 3) {
            return "三";
        } else if (number == 4) {
            return "四";
        } else if (number == 5) {
            return "五";
        } else if (number == 6) {
            return "六";
        } else if (number == 7) {
            return "七";
        } else if (number == 8) {
            return "八";
        } else if (number == 9) {
            return "九";
        } else if (number == 10) {
            return "十";
        } else if (number == 11) {
            return "十一";
        } else if (number == 12) {
            return "十二";
        } else if (number == 13) {
            return "十三";
        } else if (number == 14) {
            return "十四";
        } else if (number == 15) {
            return "十五";
        } else if (number == 16) {
            return "十六";
        } else if (number == 17) {
            return "十七";
        } else if (number == 18) {
            return "十八";
        } else if (number == 19) {
            return "十九";
        } else if (number == 20) {
            return "二十";
        }
        return "";
    }


    /**
     * open or closeRoadCondition the application
     */
    public static boolean controlApp(Context context, RecognitionWord recognitionWord) {
        if (recognitionWord == null) {
            return false;
        }

        switch (recognitionWord.getTabName()) {
            case "exit": {
//                Intent intent = new Intent();
//                intent.setAction(com.xiaoma.base.constant.Constants.Actions.ACTION_EXIT);
//                intent.putExtra(com.xiaoma.base.constant.Constants.ActionExtras.PACKAGE_NAME, recognitionWord.getPackageName(context));
//                context.sendBroadcast(intent);
                break;
            }
            case "exitMusic":
//                MusicUtils.exit(context);
                break;
            case "exitClub":
//                Intent intent = new Intent();
//                intent.setAction(com.xiaoma.base.constant.Constants.Actions.ACTION_EXIT);
//                intent.putExtra(com.xiaoma.base.constant.Constants.ActionExtras.PACKAGE_NAME, recognitionWord.getPackageName(context));
//                context.sendBroadcast(intent);
                break;
            case "exitNavi":
//                NaviUtils.exitNavi(context);
                break;
            case "exitXT": {
//                Intent intentXt = new Intent();
//                intentXt.setAction(com.xiaoma.base.constant.Constants.Actions.ACTION_EXIT);
//                intentXt.putExtra(Constants.ActionExtras.PACKAGE_NAME, recognitionWord.getPackageName(context));
//                context.sendBroadcast(intentXt);
                break;
            }
            case "Play":
//                Intent intentPlay = new Intent(Intent.ACTION_MAIN);
//                intentPlay.putExtra("Play", true);
//                intentPlay.addCategory(Intent.CATEGORY_LAUNCHER);
//                intentPlay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                ComponentName cn = new ComponentName("com.xiaoma.xting", "com.xiaoma.xting.ui.main.MainActivity");
//                intentPlay.setComponent(cn);
//                context.startActivity(intentPlay);
                break;
            case "backToLauncher":
                backToLauncher(context);
                break;
            case "openMusicRecognition":
//                Intent intentOpen = new Intent();
//                intentOpen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                ComponentName componentName = new ComponentName("com.xiaoma.launcher", "com.xiaoma.musicrec.ui.MusicRecActivity");
//                intentOpen.setComponent(componentName);
//                context.startActivity(intentOpen);
                break;
            case "Category":
//                Intent intentCategory = new Intent(Intent.ACTION_MAIN);
//                intentCategory.putExtra("Category", true);
//                intentCategory.addCategory(Intent.CATEGORY_LAUNCHER);
//                intentCategory.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                ComponentName cnCa = new ComponentName("com.xiaoma.xting", "com.xiaoma.xting.ui.main.MainActivity");
//                intentCategory.setComponent(cnCa);
//                context.startActivity(intentCategory);
                break;
            case "Favorite":
//                Intent intentFavorite = new Intent(Intent.ACTION_MAIN);
//                intentFavorite.putExtra("Favorite", true);
//                intentFavorite.addCategory(Intent.CATEGORY_LAUNCHER);
//                intentFavorite.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                ComponentName cnFa = new ComponentName("com.xiaoma.xting", "com.xiaoma.xting.ui.main.MainActivity");
//                intentFavorite.setComponent(cnFa);
//                context.startActivity(intentFavorite);
                break;
            default:
                //LaunchAppUtils.launcherApplication(context, recognitionWord.getBundle(), recognitionWord.getPackageName(context));
                LaunchUtils.launchApp(context, recognitionWord.getPackageName(context), recognitionWord.getBundle());
                uploadLauncherAppEvent(context, recognitionWord.getPackageName(context));
                break;
        }

        return true;
    }


    private static void uploadLauncherAppEvent(Context context, String packageName) {
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageName);
        List<ResolveInfo> resolveInfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveInfoList.iterator().next();
        if (resolveinfo != null) {
            PackageManager pm = context.getPackageManager();
            KLog.d("upload open app event is :" + resolveinfo.loadLabel(pm).toString());
//            EventAgent.getInstance().onEvent(com.xiaoma.base.constant.
//                    Constants.XMEventKey.Launcher.CLICK_APP, resolveinfo.loadLabel(pm).toString());
        }
    }

    public static String isEmptyString(String content) {
        if (TextUtils.isEmpty(content))
            return "";
        else
            return content;
    }


    public static void backToLauncher(Context context) {
        // todo 回到桌面
        // LaunchAppUtils.launchApp(context, context.getPackageName());
    }

}
