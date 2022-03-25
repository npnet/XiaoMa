package com.xiaoma.ad;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;

import com.xiaoma.ad.dialog.AdDialog;
import com.xiaoma.ad.models.Ad;
import com.xiaoma.ad.provider.SharedPreferenceProxy;
import com.xiaoma.ad.utils.AdSpUtil;
import com.xiaoma.ad.utils.GsonUtil;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.FileCallback;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.MD5Utils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * 本 module 有两种用法(单应用和多应用)：<br/>
 * 1. 每个应用单独的同步和显示自己的广告，只需在application中初始化{@link AdManager#init(Context, boolean)}时传入false即可，
 *    无需其他配置<br/>
 * 2. 只有一个应用(一般为launcher)去同步广告，其他应用通过跨进程通信的方式去获取广告。在application中
 *    初始化{@link AdManager#init(Context, boolean)}时传入true，需要在launcher的manifest中配置一个provider，其exported
 *    属性设为true。其他App就可以调用 {@link AdManager#getCachedAd()}获取launcher中保存的数据。<br/>
 * </pre>
 *
 * @author KY
 * @date 2018/9/13
 */

public class AdManager {

    /**
     * 将图片的缓存目录放到sd卡根目录中以共享缓存的广告图片
     */
    private static final File CACHE_DIR = ConfigManager.FileConfig.getAdFolder();

    private static Context context;

    /**
     * 传入Context
     *
     * @param context  Context
     */
    public static void init(Context context) {
        AdManager.context = context.getApplicationContext();
        SharedPreferenceProxy.setMultipleFlag(true);
    }

    /**
     * 同步广告
     */
    public static void syncAd() {
        if (!needSyncAd()) {
            return;
        }
        XmHttp.getDefault().getString(AdConstants.getAdUrl, Collections.<String, Object>emptyMap(), "", new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    List<Ad> ads = GsonUtil.fromJsonToList(jsonArray.toString(), Ad[].class);
                    // 与本地缓存的广告进行合并更新且过滤掉过期的广告
                    List<Ad> validAd = mergeCachedAds(ads);
                    // 将没有下载的广告图片进行下载
                    downloadImage(validAd);
                    // 重新将有效的广告保存至本地
                    AdSpUtil.saveAd(validAd);
                    // 标记同步广告的时间
                    AdSpUtil.markSync();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 将从网络获取的广告 与 本地缓存的广告进行合并，id一样时以网络获取的为准
     *
     * @param ads Ad
     * @return ads
     */
    private static List<Ad> mergeCachedAds(List<Ad> ads) {
        List<Ad> adList = new ArrayList<>(ads);
        List<Ad> cachedAds = getCachedAd();
        List<Long> ids = new ArrayList<>();
        for (Ad ad : adList) {
            ids.add(ad.getId());
        }

        for (Ad cachedAd : cachedAds) {
            if (!ids.contains(cachedAd.getId())) {
                adList.add(cachedAd);
            }
        }
        return filterExpired(adList);
    }

    /**
     * 过滤掉已过期的广告
     *
     * @param ads 入参
     * @return 过滤后的列表
     */
    private static List<Ad> filterExpired(List<Ad> ads) {
        long now = System.currentTimeMillis();
        List<Ad> validList = new ArrayList<>();
        List<Ad> invalidAds = new ArrayList<>();
        if (ads != null && !ads.isEmpty()) {
            for (Ad ad : ads) {
                if (now < ad.getEndDate()) {
                    validList.add(ad);
                } else {
                    invalidAds.add(ad);
                }
            }
        }

        if (!invalidAds.isEmpty()) {
            asyncClearInvalidAd(invalidAds);
        }

        return validList;
    }

    /**
     * 异步的清除无效的广告图片
     *
     * @param invalidAds 无效的广告
     */
    private static void asyncClearInvalidAd(final List<Ad> invalidAds) {
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                for (Ad ad : invalidAds) {
                    getAdFile(ad).deleteOnExit();
                }
            }
        });
    }

    /**
     * 获取本地存储的有效广告列表
     *
     * @return valid ads
     */
    public static List<Ad> getCachedAd() {
        return filterExpired(AdSpUtil.getCachedAds());
    }

    /**
     * 随机获取本地存储的有效广告
     *
     * @return valid ad
     */
    public static Ad getRandomAd() {
        List<Ad> validAd = getCachedAd();
        return validAd.get((int) (Math.random() * validAd.size()));
    }

    /**
     * 根据上一次同步广告的时间和当前时间比较，判断是否需要同步
     *
     * @return 是否需要同步
     */
    private static boolean needSyncAd() {
        return !AdSpUtil.getSyncMark().equals(StringUtil.getDateByYMD());
    }

    /**
     * 判断是否需要显示广告
     *
     * @return 是否需要同步
     */
    private static boolean needShowAd() {
        return !AdSpUtil.getAdShowSyncMark().equals(StringUtil.getDateByYMD());
    }

    /**
     * 下载广告展示所需的图片
     *
     * @param ads 要下载图片的广告列表
     */
    private static void downloadImage(final List<Ad> ads) {
        final List<Ad> needDownloadAds = new ArrayList<>();
        for (final Ad ad : ads) {
            // assets 中的文件不能用 file.exists()判断是否存在，需要过滤掉
            if (!ad.getImgPath().startsWith("file:") && !getAdFile(ad).exists()) {
                needDownloadAds.add(ad);
            }
        }


        for (final Ad ad : needDownloadAds) {
            XmHttp.getDefault().getFile(ad.getImgPath(), null,
                    String.valueOf(ad.getId()), new FileCallback(getAdFile(ad).getParent(), getAdFile(ad).getName()) {
                        @Override
                        public void onSuccess(Response<File> response) {
                            KLog.i(String.format("ID:%s 的广告图片缓存成功", ad.getId()));
                        }

                        @Override
                        public void onError(Response<File> response) {
                            KLog.e(String.format("ID:%s 的广告图片缓存失败", ad.getId()));
                            ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
                                @Override
                                public void run() {
                                    getAdFile(ad).deleteOnExit();
                                }
                            });
                        }
                    });
        }

    }

    /**
     * 获取 将id和url拼接并MD5加密后的字符串作为文件名
     *
     * @param ad AD
     * @return 广告图片文件
     */
    public static File getAdFile(Ad ad) {
        if (ad.getImgPath().startsWith("file:")) {
            return new File(ad.getImgPath());
        } else {
            return new File(CACHE_DIR, MD5Utils.getStringMD5(ad.getId() + ad.getImgPath()));
        }
    }

    public static Context getContext() {
        return context;
    }

    /**
     * 显示广告，传入的context
     *
     * @param context
     */
    public static Ad showAd(final Context context, @Nullable final DialogInterface.OnDismissListener dismissCallback,
                            final AdDialog.AdDialogClickListener dialogClickListener) {
        if (!needShowAd()) {
            return null;
        }
        final Ad randomAd = getRandomAd();
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                try {
                    final AdDialog mAdDialog = new AdDialog(context, randomAd);
                    mAdDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (dismissCallback != null) dismissCallback.onDismiss(dialog);
                        }
                    });
                    mAdDialog.setDialogClickListener(new AdDialog.AdDialogClickListener() {
                        @Override
                        public void onDialogClick(String link) {
                            if (dialogClickListener != null) dialogClickListener.onDialogClick(link);
                        }
                    });
                    mAdDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return randomAd;
    }
}
