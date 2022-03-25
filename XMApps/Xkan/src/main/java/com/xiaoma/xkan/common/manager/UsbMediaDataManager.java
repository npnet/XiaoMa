package com.xiaoma.xkan.common.manager;

import android.content.Context;

import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.receiver.UsbDetector;
import com.xiaoma.xkan.common.comparator.NameComparator;
import com.xiaoma.xkan.common.constant.XkanConstants;
import com.xiaoma.xkan.common.listener.UsbConnectStateListener;
import com.xiaoma.xkan.common.listener.UsbMediaSearchListener;
import com.xiaoma.xkan.common.model.UsbMediaInfo;
import com.xiaoma.xkan.common.model.UsbStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Thomas on 2018/11/14 0014
 * usb 数据 状态 管理
 */

public class UsbMediaDataManager {

    private static final String TAG = "UsbMediaDataManager";
    private List<UsbMediaInfo> videoList = new ArrayList<>();
    private List<UsbMediaInfo> pictureList = new ArrayList<>();
    private List<UsbMediaInfo> currPathFileList = new ArrayList<>();

    private List<UsbMediaInfo> currDecList = new ArrayList<>();
    private List<UsbMediaInfo> currVideoList = new ArrayList<>();
    private List<UsbMediaInfo> currPictList = new ArrayList<>();


    private static UsbMediaDataManager sUsbMediaDataManager;
    private UsbStatus mCurrentUsbStatus = UsbStatus.NO_USB_MOUNTED;

    //当前文件夹路径
    private String currPath;
    //根据文件层次 保存对应数据
    private TreeMap<String, List<UsbMediaInfo>> fileListMap = new TreeMap<>();
    //根据文件层次的 路径集合
    private List<String> pathList = new ArrayList<>();
    //usb根路径
    private String rootPath;

    private UsbMediaDataManager() {
    }

    public static UsbMediaDataManager getInstance() {
        if (sUsbMediaDataManager == null) {
            synchronized (AudioFocusManager.class) {
                if (sUsbMediaDataManager == null) {
                    sUsbMediaDataManager = new UsbMediaDataManager();
                }
            }
        }
        return sUsbMediaDataManager;
    }

    public void init(Context context) {
        UsbDetector.getInstance().init(context);
    }

    public void setCurrentUsbStatus(UsbStatus currentUsbStatus) {
        mCurrentUsbStatus = currentUsbStatus;
    }

    public UsbStatus getCurrentUsbStatus() {
        return mCurrentUsbStatus;
    }

//    private void initUsbReceiver() {
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.setPriority(Integer.MAX_VALUE);
//        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        intentFilter.addAction(ConfigConstants.STORAGE_ACTION_VOLUME_STATE_CHANGED);
//        context.registerReceiver(UsbDetector.getInstance(), intentFilter);
//    }

    private UsbConnectStateListener listener;

    private UsbDetector.UsbDetectListener mUsbDetectListener = new UsbDetector.UsbDetectListener() {
        @Override
        public void noUsbMounted() {
            mCurrentUsbStatus = UsbStatus.NO_USB_MOUNTED;
            if (listener != null) {
                listener.onConnection(UsbStatus.NO_USB_MOUNTED, null);
            }

        }

        @Override
        public void inserted() {
            mCurrentUsbStatus = UsbStatus.INSERTED;
            if (listener != null) {
                listener.onConnection(UsbStatus.INSERTED, null);
            }

        }

        @Override
        public void mounted(List<String> mountPaths) {
            mCurrentUsbStatus = UsbStatus.MOUNTED;
            if (listener != null) {
                listener.onConnection(UsbStatus.MOUNTED, mountPaths);
            }

        }

        @Override
        public void mountError() {
            mCurrentUsbStatus = UsbStatus.MOUNT_ERROR;
            if (listener != null) {
                listener.onConnection(UsbStatus.MOUNT_ERROR, null);
            }

        }

        @Override
        public void removed() {
            mCurrentUsbStatus = UsbStatus.REMOVED;
            resetMediaList();
            if (listener != null) {
                listener.onConnection(UsbStatus.REMOVED, null);
            }

        }
    };

    public void syncUsbConnectState(Context context, UsbConnectStateListener listener) {
        this.listener = listener;
        UsbDetector.getInstance().syncUsbConnectState(context, mUsbDetectListener);
    }

    public void removeConnectListener() {
        this.listener = null;
        UsbDetector.getInstance().removeUsbDetectListener(mUsbDetectListener);
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public List<UsbMediaInfo> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<UsbMediaInfo> videoList) {
        this.videoList = videoList;
    }

    public List<UsbMediaInfo> getPictureList() {
        return pictureList;
    }

    public void setPictureList(List<UsbMediaInfo> pictureList) {
        this.pictureList = pictureList;
    }


    public TreeMap<String, List<UsbMediaInfo>> getFileListMap() {
        return fileListMap;
    }

    public List<UsbMediaInfo> getCurrPathFileList() {
        return currPathFileList;
    }

    public void setCurrPathFileList(List<UsbMediaInfo> currPathFileList) {
        this.currPathFileList = currPathFileList;
    }

    public String getCurrPath() {
        return currPath;
    }

    public void setCurrPath(String currPath) {
        this.currPath = currPath;
    }

    public void setFileListMap(TreeMap<String, List<UsbMediaInfo>> fileListMap) {
        this.fileListMap = fileListMap;
    }

    public List<String> getPathList() {
        return pathList;
    }

    public void setPathList(List<String> pathList) {
        this.pathList = pathList;
    }

    public List<UsbMediaInfo> getCurrVideoList() {
        return currVideoList;
    }

    public void setCurrVideoList(List<UsbMediaInfo> currVideoList) {
        this.currVideoList = currVideoList;
    }

    public List<UsbMediaInfo> getCurrPictList() {
        return currPictList;
    }

    public void setCurrPictList(List<UsbMediaInfo> currPictList) {
        this.currPictList = currPictList;
    }

    public List<UsbMediaInfo> getCurrDecList() {
        return currDecList;
    }

    public void setCurrDecList(List<UsbMediaInfo> currDecList) {
        this.currDecList = currDecList;
    }

    //获取到所有图片和视频文件
    public void fetchUsbMediaData(final String rootPath, final UsbMediaSearchListener listener) {
        this.rootPath = rootPath + "/";
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                getUsbMediaData(rootPath, listener);
            }
        });
    }

    /**
     * 获取当前路径下图片、视频、文件夹
     *
     * @param currPath
     * @param listener
     */
    public void fetchCurrPathMediaData(final String currPath, final UsbMediaSearchListener listener) {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                requestCurrentPathList(currPath, listener);
            }
        });
    }

    private void getUsbMediaData(String rootPath, final UsbMediaSearchListener listener) {
        if (listener == null) {
            return;
        }
        resetMediaList();
        KLog.d(TAG, "start scan usb file");
        List<File> fileList = new ArrayList<>();
        FileUtils.refreshUsbMediaFileList(rootPath, fileList);
        //获取当前路径下的文件
        List<File> currPathFiles = FileUtils.getCurrPathFileList(rootPath);
        KLog.d(TAG, "end scan usb file");
        for (int i = 0; i < fileList.size(); i++) {
            File file = fileList.get(i);
            String endStr = file.getName().toLowerCase();
            UsbMediaInfo usbMediaInfo = new UsbMediaInfo();
            usbMediaInfo.setDate(file.lastModified());
            usbMediaInfo.setSize(file.length());
            usbMediaInfo.setMediaName(file.getName());
            usbMediaInfo.setPath(file.getAbsolutePath());
            usbMediaInfo.setFirstLetter(StringUtil.getSortLetter2(file.getName()));
            if (FileUtils.isVideo(endStr)) {
                videoList.add(usbMediaInfo);
            } else if (FileUtils.isPicture(endStr)) {
                pictureList.add(usbMediaInfo);
            }
        }
        initCurrFileList(currPathFiles);

        Collections.sort(videoList, new NameComparator(false));
        Collections.sort(pictureList, new NameComparator(false));

        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                listener.onUsbMediaSearchFinished();
            }
        });
    }

    private void requestCurrentPathList(String path, final UsbMediaSearchListener listener) {
        currPathFileList = new ArrayList<>();
        currDecList.clear();
        currVideoList.clear();
        currPictList.clear();
        //获取当前路径下的文件
        List<File> currPathFiles = FileUtils.getCurrPathFileList(path);

        initCurrFileList(currPathFiles);

        currPath = path;
        addFileListByPath(path);

        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                listener.onUsbMediaSearchFinished();
            }
        });
    }

    private void initCurrFileList(List<File> currPathFiles) {
        if (!ListUtils.isEmpty(currPathFiles)) {
            for (int i = 0; i < currPathFiles.size(); i++) {
                File file = currPathFiles.get(i);
                UsbMediaInfo usbMediaInfo = new UsbMediaInfo();

                usbMediaInfo.setDate(file.lastModified());
                usbMediaInfo.setSize(file.length());
                usbMediaInfo.setMediaName(file.getName());
                usbMediaInfo.setPath(file.getAbsolutePath());
                usbMediaInfo.setFirstLetter(StringUtil.getSortLetter2(file.getName()));
                String endStr = file.getName().toLowerCase();
                if (file.isDirectory()) {
                    usbMediaInfo.setFileType(XkanConstants.FILE_TYPE_DEC);
                    currDecList.add(usbMediaInfo);
                } else if (FileUtils.isVideo(endStr)) {
                    usbMediaInfo.setFileType(XkanConstants.FILE_TYPE_VIDEO);
                    currVideoList.add(usbMediaInfo);
                } else if (FileUtils.isPicture(endStr)) {
                    usbMediaInfo.setFileType(XkanConstants.FILE_TYPE_PIC);
                    currPictList.add(usbMediaInfo);
                }

                currPathFileList.add(usbMediaInfo);
            }

            //进行名称排序
            Collections.sort(currPathFileList, new NameComparator(false));
            Collections.sort(currPictList, new NameComparator(false));
            Collections.sort(currVideoList, new NameComparator(false));
        }
    }

    public void resetCurrPicAndVideoList(List<UsbMediaInfo> currData) {
        currDecList.clear();
        currVideoList.clear();
        currPictList.clear();
        if (!ListUtils.isEmpty(currData)) {
            for (int i = 0; i < currData.size(); i++) {
                UsbMediaInfo usbMediaInfo = currData.get(i);
                int fileType = usbMediaInfo.getFileType();
                if (fileType == XkanConstants.FILE_TYPE_DEC) {
                    currDecList.add(usbMediaInfo);
                } else if (fileType == XkanConstants.FILE_TYPE_VIDEO) {
                    currVideoList.add(usbMediaInfo);
                } else if (fileType == XkanConstants.FILE_TYPE_PIC) {
                    currPictList.add(usbMediaInfo);
                }
            }
            //进行名称排序
//            Collections.sort(currPictList, new NameComparator(false));
//            Collections.sort(currVideoList, new NameComparator(false));
        }
    }

    /**
     * 添加path 到list
     */
    public void addPathToList(String path) {
        pathList.add(path);
    }

    /**
     * 返回上一层 文件夹
     */
    public void backUpDec() {
        pathList.remove(pathList.size() - 1);
        currPath = pathList.get(pathList.size() - 1);
    }

    /**
     * 根据文件路径 获取当前filelist
     *
     * @return
     */
    public List<UsbMediaInfo> getFileListByPath() {
        return fileListMap.get(currPath);
    }

    /**
     * 根据文件路径 获取当前filelist
     *
     * @return
     */
    public List<UsbMediaInfo> getFileListByPath(String path) {
        return fileListMap.get(path);
    }


    /**
     * 根据文件路径 添加当前filelist
     *
     * @return
     */
    public void addFileListByPath(String path) {
        fileListMap.put(path, currPathFileList);
    }

    public void resetMediaList() {
        videoList.clear();
        pictureList.clear();
        pathList.clear();
        fileListMap.clear();
        currPathFileList = new ArrayList<>();
        currDecList.clear();
        currPictList.clear();
        currVideoList.clear();

    }

}
