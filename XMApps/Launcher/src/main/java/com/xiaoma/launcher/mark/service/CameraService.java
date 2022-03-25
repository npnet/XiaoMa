package com.xiaoma.launcher.mark.service;


import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;

import com.google.android.cameraview.CameraView;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.component.AppHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.LauncherUtils;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.model.Weather;
import com.xiaoma.launcher.mark.model.MarkPhotoBean;
import com.xiaoma.launcher.schedule.utils.DateUtil;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeUtils;
import com.xiaoma.utils.tputils.TPUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 后台拍照服务，配合全局窗口使用
 *
 * @author WuRS
 */
public class CameraService extends Service {

    private static final String TAG = CameraService.class.getSimpleName();

    private Camera mCamera;

    private boolean isRunning; // 是否已在监控拍照

    private String commandId; // 指令ID

    private CameraView mCameraView;
    private final static int CMAERA_OCCUPY = 3;
    private static boolean INITIALIZATION_STATE = false;
    private Weather mWeather;
    private LocationInfo mLocationInfo;

    public static final String CITY_WEATHER_DETAIL = "city_weather";

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    private void initData() {
        mLocationInfo = LocationManager.getInstance().getCurrentLocation();
        mWeather = TPUtils.getObject(AppHolder.getInstance().getAppContext(), CITY_WEATHER_DETAIL, Weather.class);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initCamera();
        try {
            if (mCameraView != null && INITIALIZATION_STATE) {

                mCameraView.addCallback(mCallback);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    mCameraView.start();
                }
                TPUtils.put(this, LauncherConstants.CAMERA_STYLE, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    private void initCamera() {
        CameraWindow.show(getApplicationContext());
        if (XmMapNaviManager.getInstance().getNaviShowState() == CMAERA_OCCUPY) {
            stopSelf();
        } else {
            mCameraView = CameraWindow.getDummyCameraView();
            INITIALIZATION_STATE = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //相机监听
    private CameraView.Callback mCallback
            = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCameraView.takePicture();
                }
            }, 1000);
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {

        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {

            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (mCameraView != null && INITIALIZATION_STATE) {
                        TPUtils.put(getApplication(), LauncherConstants.CAMERA_STYLE, true);
                    }
                    saveToSDCard(data);
                    mCameraView.stop();
                    CameraWindow.dismiss();
                }
            });
        }
    };
    private Handler mBackgroundHandler;

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    /**
     * 将拍下来的照片存放在SD卡中
     *
     * @param data
     * @return imagePath 图片路径
     */
    Bundle bundle = null; // 声明一个Bundle对象，用来存储数据

    public boolean saveToSDCard(byte[] data) {
        bundle = new Bundle();
        bundle.putByteArray("bytes", data);    //将图片字节数据保存在bundle当中，实现数据交换

        //ADD 生成保存图片的路径
        String mImagePath = absolutePath(data);

        //保存到SD卡
        if (StringUtil.isEmpty(mImagePath)) {
            return false;
        }
        try {
            FileOutputStream outputStream = null; // 文件输出流
            outputStream = new FileOutputStream(mImagePath);
            outputStream.write(data); // 写入sd卡中
            outputStream.close(); // 关闭输出流
            savePhoto(mImagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 保存图片
     */
    private void savePhoto(String imagePath) {

        MarkPhotoBean markPhotoBean = new MarkPhotoBean();
        if (mLocationInfo != null) {

            markPhotoBean.setPhotoAddress(mLocationInfo.getAddress());
            if (mLocationInfo.getCity().endsWith("市")) {
                String city = mLocationInfo.getCity();
                markPhotoBean.setLocation(mLocationInfo.getProvince() + " " + city.substring(0, city.length() - 1));
            } else {
                markPhotoBean.setLocation(mLocationInfo.getProvince() + " " + mLocationInfo.getCity());
            }

            markPhotoBean.setLatitude(mLocationInfo.getLatitude());
            markPhotoBean.setLongitude(mLocationInfo.getLongitude());

            markPhotoBean.setSaveType(false);
        } else {
            markPhotoBean.setSaveType(false);
            markPhotoBean.setPhotoAddress(getString(R.string.not_location_information_was_obtained));
        }

        if (StringUtil.isNotEmpty(imagePath)) {
            markPhotoBean.setPhotoPath(imagePath);
        }
        markPhotoBean.setFacialType(0);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm "); // 格式化时间
        String mFileTime = format.format(date) + TimeUtils.getChineseWeek(date);
        if (StringUtil.isNotEmpty(mFileTime))
            markPhotoBean.setPhotoTime(mFileTime);

        int year = DateUtil.getCurrentYear();
        int month = DateUtil.getCurrentMonth();
        int day = DateUtil.getCurrentDay();
        markPhotoBean.setYear(String.valueOf(year));
        markPhotoBean.setMonth(String.valueOf(month));
        markPhotoBean.setDay(String.valueOf(day));
        markPhotoBean.setSaveType(false);

        if (mWeather != null) {
            if (StringUtil.isNotEmpty(mWeather.getWeather())) {
                markPhotoBean.setWeather(mWeather.getWeather());
            }
        }
        LauncherUtils.getDBManager().save(markPhotoBean);
        stopSelf();
    }


    //存储路径
    public String absolutePath(byte[] data) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
        String filename = format.format(date) + ".jpg";
        File fileFolder = new File(Environment.getExternalStorageDirectory() + "/mark/");
        if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
            fileFolder.mkdir();
        }
        File jpgFile = new File(fileFolder, filename);
        return jpgFile.getAbsolutePath();
    }
}