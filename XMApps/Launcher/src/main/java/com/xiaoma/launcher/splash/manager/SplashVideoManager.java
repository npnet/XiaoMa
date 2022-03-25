package com.xiaoma.launcher.splash.manager;

import android.content.Context;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.launcher.common.LauncherUtils;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.splash.model.ListBean;
import com.xiaoma.launcher.splash.model.VideoBean;
import com.xiaoma.launcher.splash.model.VideoDlownBean;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.FileCallback;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.MD5Utils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SplashVideoManager {
    public static final String VIDEO_URL = ConfigManager.EnvConfig.getEnv().getBusiness() + "report/openReportV3.action";
    /**
     * 缓存目录放到sd卡根目录中
     */
    private static final File CACHE_DIR = ConfigManager.FileConfig.getSplashVideoFolder();

    private static Context context;

    /**
     * 传入Context
     *
     * @param context Context
     */
    public static void init(Context context) {
        SplashVideoManager.context = context.getApplicationContext();

    }

    /**
     * 同步开机视频
     */
    public static void syncVideo() {
        XmHttp.getDefault().getString(VIDEO_URL, null, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                VideoBean videoBean = GsonHelper.fromJson(response.body(), VideoBean.class);
                if (videoBean.getData() != null) {
                    List<ListBean> video = videoBean.getData().getList();
                    if (!ListUtils.isEmpty(video)) {
                        // 与本地缓存的视频进行合并更新且过滤掉过期的视频
                        List<ListBean> validVideo = mergeCachedVideos(video);
                        // 将没有下载的视频进行下载
                        downloadVideo(validVideo);
                        // 重新将有效的视频保存至本地
                        TPUtils.put(context, LauncherConstants.VIDEO_JSON, response.body());
                    }
                }
            }
        });
    }

    /**
     * 下载未下载的视频
     *
     * @param validVideo
     */
    private static void downloadVideo(List<ListBean> validVideo) {
        for (final ListBean videoBean : validVideo) {
            if (StringUtil.isNotEmpty(videoBean.getVideo())) {
                XmHttp.getDefault().getFile(videoBean.getVideo(), null,
                        new FileCallback(getVideoFile(videoBean).getParent(), getVideoFile(videoBean).getName()) {
                            @Override
                            public void onSuccess(Response<File> response) {
                                KLog.i(String.format(" Festival:%s video download success", videoBean.getFestival()));
                                VideoDlownBean videoDlownBean = new VideoDlownBean();
                                videoDlownBean.setVideoMd5(videoBean.getMd5String());

                                LauncherUtils.getDBManager().save(videoDlownBean);
                            }

                            @Override
                            public void onError(Response<File> response) {
                                KLog.e(String.format(" Festival:%s video download failure", videoBean.getFestival()));
                                ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
                                    @Override
                                    public void run() {
                                        getVideoFile(videoBean).deleteOnExit();
                                    }
                                });
                            }
                        });

            }
        }
    }


    public static File getVideoFile(ListBean videoBean) {
        return new File(CACHE_DIR, videoBean.getMd5String() + ".mp4");
    }

    /**
     * 判断视频是否存在,存在返回地址，不存在返回null
     *
     * @param videoBean
     * @return
     */
    public static String getVideoExists(ListBean videoBean) {
        try {
            File videoFile = getVideoFile(videoBean);
            if (videoFile.exists()) {
                String fileMD5String = MD5Utils.getFileMD5String(videoFile);
                if (fileMD5String.equals(videoBean.getMd5String())) {

                    return videoFile.getPath();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 清除过期的视频
     *
     * @param video
     * @return
     */
    private static List<ListBean> mergeCachedVideos(List<ListBean> video) {
        List<ListBean> netWorkVideo = new ArrayList<>();
        List<String> md5String = new ArrayList<>();
        // List<String> netWorkMd5String = new ArrayList<>();
        List<VideoDlownBean> videoDlownBeans = LauncherUtils.getDBManager().queryAll(VideoDlownBean.class);

        if (!ListUtils.isEmpty(videoDlownBeans)) {
            for (VideoDlownBean local : videoDlownBeans) {
                if (StringUtil.isNotEmpty(local.getVideoMd5())) {
                    md5String.add(local.getVideoMd5());
                }
            }
        } else {
            return video;
        }
        //获取网络json有新视频数据
        for (ListBean item : video) {
            if (StringUtil.isNotEmpty(item.getMd5String())) {
                //netWorkMd5String.add(item.getMd5string());
                if (!md5String.contains(item.getMd5String())) {
                    netWorkVideo.add(item);
                }
            }
        }

      /*  //判断本地，md5是否包含在网络中，如果没有表示过期则删除
        if (!ListUtils.isEmpty(localVideo)) {
            for (ListBean local : localVideo) {
                if (StringUtil.isNotEmpty(local.getMd5string())) {
                    if (!netWorkMd5String.contains(local.getMd5string())) {
                        filterExpired(local);
                    }
                }
            }
        }*/
        return netWorkVideo;
    }

    private static void filterExpired(ListBean local) {
        KLog.d("Delete expired video filter:::", CACHE_DIR + local.getMd5String() + ".mp4");
        FileUtils.delete(CACHE_DIR + local.getMd5String() + ".mp4");
    }

    /**
     * 获取本地保存的videolist
     *
     * @return
     */
    public static List<ListBean> getLocalVideo() {
        String videoJson = TPUtils.get(context, LauncherConstants.VIDEO_JSON, "");
        if (StringUtil.isNotEmpty(videoJson)) {
            try {
                VideoBean videoBean = GsonHelper.fromJson(videoJson, VideoBean.class);
                if (videoBean!=null){
                    if (videoBean.getData()!=null){
                        List<ListBean> localVideo = videoBean.getData().getList();
                        return localVideo;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return null;
        }
        return null;
    }


}

