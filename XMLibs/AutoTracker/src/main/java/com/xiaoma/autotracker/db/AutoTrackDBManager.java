package com.xiaoma.autotracker.db;

import android.app.Activity;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.model.AutoTrackInfo;
import com.xiaoma.autotracker.model.BusinessType;
import com.xiaoma.autotracker.model.TrackerEventType;
import com.xiaoma.autotracker.model.UploadEventType;
import com.xiaoma.autotracker.upload.AutoTrackUploadEvent;
import com.xiaoma.autotracker.upload.CollectBasisInfo;
import com.xiaoma.autotracker.upload.CollectBusinessInfo;
import com.xiaoma.autotracker.upload.UploadManager;
import com.xiaoma.autotracker.util.XmEventException;
import com.xiaoma.db.DBManager;
import com.xiaoma.db.IDatabase;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/**
 * @author taojin
 * @date 2018/12/7
 */
public class AutoTrackDBManager implements AutoTrackUploadEvent, CollectBasisInfo, CollectBusinessInfo {

    private final String TAG = "com.xiaoma.autotracker.component.lifecycle";
    private IDatabase database;
    private final int cacheSize = 100;
    private final int uploadCount = 500;
    private long time = 5 * 60 * 1000;
    //操作时间(数据库查询按照操作时间排序)
    private final String OPERATIONDATE = "ot";
    private final String SUFFIX = "com.xiaoma";
    private long onlineTimeAt;
    private List<AutoTrackInfo> autoTrackCache = new ArrayList<>(cacheSize);

    private AutoTrackDBManager() {
        database = DBManager.getInstance().getDBManager();
        onlineTimeAt = currTimeMillis();
    }

    //====================构造方法=====================
    private static class AutoTrackEventHolder {
        private static final AutoTrackDBManager instance = new AutoTrackDBManager();
    }

    public static AutoTrackDBManager getInstance() {
        return AutoTrackEventHolder.instance;
    }
    //=====================构造方法end==========================


    //======================上报接口============================
    @Override
    public void uploadEvent(final AutoTrackInfo data) {
        UploadManager.uploadEvent(data, new ResultCallback<XMResult>() {
            @Override
            public void onSuccess(XMResult result) {
                KLog.d(TAG, "upload log success !");
            }

            @Override
            public void onFailure(int code, String msg) {
                database.save(data);
            }
        });
    }

    @Override
    public void batchUploadEvent(final List<AutoTrackInfo> autoTrackInfos, UploadEventType uploadEventType) {
        //上报
        UploadManager.batchUploadEvent(GsonHelper.toJson(autoTrackInfos), new ResultCallback<XMResult>() {
            @Override
            public void onSuccess(XMResult result) {
                KLog.d(TAG, "BATCH UPLOAD SUCCESS");
                //删除数据通过开启低优先级的线程去执行
                ThreadDispatcher.getDispatcher().postSerial(new Runnable() {
                    @Override
                    public void run() {
                        database.delete((autoTrackInfos));
                    }
                }, Priority.LOW);
            }

            @Override
            public void onFailure(int code, String msg) {

            }

        });
    }
    //======================上报接口end============================


    //======================收集基础信息接口============================
    @Override
    public void saveAppViewScreenEvent(Activity activity, AutoTrackInfo trackInfo) {

        if (XmAutoTracker.getInstance().getApplication() == null) {
            noInitAutoTrackerError();
            return;
        }
        setBaseInfo(trackInfo);
        if (activity.getClass().isAnnotationPresent(PageDescComponent.class)) {
            String annotationValue = activity.getClass().getAnnotation(PageDescComponent.class).value();
            if (TextUtils.isEmpty(annotationValue)) {
                implementsPageDescribeAnnotationError(activity.getClass().getCanonicalName());
            } else {
                trackInfo.setVu(activity.getClass().getSimpleName());
                trackInfo.setVn(annotationValue);
                //后台要求多传个字段，做冗余使用
                trackInfo.setE(annotationValue);
            }
        } else {
            notImplementsAnnotationError(activity.getClass().getCanonicalName());
            return;

        }
        KLog.d(TAG, "saveAppViewScreenEvent" + trackInfo.toString());
        handleData(trackInfo);

    }

    @Override
    public void saveAppEvent(int appViewType) {
        if (XmAutoTracker.getInstance().getApplication() == null) {
            noInitAutoTrackerError();
            return;
        }
        AutoTrackInfo autoTrackInfo = new AutoTrackInfo();
        setBaseInfo(autoTrackInfo);
        autoTrackInfo.setOt(TrackerEventType.LINKAROUTER.getEventValue());
        autoTrackInfo.setS(appViewType);
        KLog.d(TAG, "saveAppEvent" + autoTrackInfo.toString());
        handleData(autoTrackInfo);
    }

    @Override
    public void saveFragmentViewScreenEvent(Fragment fragment, AutoTrackInfo trackInfo) {
        if (XmAutoTracker.getInstance().getApplication() == null) {
            noInitAutoTrackerError();
            return;
        }
        setBaseInfo(trackInfo);
        if (fragment.getClass().isAnnotationPresent(PageDescComponent.class)) {
            String annotationValue = fragment.getClass().getAnnotation(PageDescComponent.class).value();
            if (TextUtils.isEmpty(annotationValue)) {
                implementsPageDescribeAnnotationError(fragment.getClass().getCanonicalName());
            } else {
                trackInfo.setVu(fragment.getClass().getSimpleName());
                trackInfo.setVn(annotationValue);
                //后台要求多传个字段，做冗余使用
                trackInfo.setE(annotationValue);
            }
        } else {
            if (fragment.getClass().getCanonicalName().startsWith(SUFFIX)) {
                notImplementsAnnotationError(fragment.getClass().getCanonicalName());
            }
            return;

        }
        KLog.d(TAG, "saveFragmentViewScreenEvent" + trackInfo.toString());
        handleData(trackInfo);
    }

    @Override
    public void saveOnClickEvent(String event, String id, String pagePath, String pageName) {
        if (XmAutoTracker.getInstance().getApplication() == null) {
            noInitAutoTrackerError();
            return;
        }
        AutoTrackInfo autoTrackInfo = setClickInfo(event, id, pagePath, pageName);
        KLog.d(TAG, "saveOnClickEvent" + autoTrackInfo.toString());
        handleData(autoTrackInfo);
    }
    //======================收集基础信息接口end============================


    //======================收集业务信息接口================================
    @Override
    public void uploadOnlineTimeEvent(long onlineTime, String businessType) {
        if (XmAutoTracker.getInstance().getApplication() == null) {
            noInitAutoTrackerError();
            return;
        }
        AutoTrackInfo autoTrackInfo = new AutoTrackInfo();
        setBaseInfo(autoTrackInfo);
        autoTrackInfo.setOt(TrackerEventType.BUSINESS.getEventValue());
        autoTrackInfo.setNt(onlineTime);
        autoTrackInfo.setBs(businessType);

        KLog.d(TAG, "uploadOnlineTimeEvent" + autoTrackInfo.toString());
        handleData(autoTrackInfo);
    }

    @Override
    public void uploadAppOnOffEvent(String businessType) {
        if (XmAutoTracker.getInstance().getApplication() == null) {
            noInitAutoTrackerError();
            return;
        }
        AutoTrackInfo autoTrackInfo = new AutoTrackInfo();
        setBaseInfo(autoTrackInfo);
        autoTrackInfo.setOt(TrackerEventType.BUSINESS.getEventValue());
        autoTrackInfo.setBs(businessType);
        KLog.d(TAG, "uploadAppOnOffEvent" + autoTrackInfo.toString());
        handleData(autoTrackInfo);
    }

    @Override
    public void uploadPlayTimeEvent(long playTime, String playType) {

        if (XmAutoTracker.getInstance().getApplication() == null) {
            noInitAutoTrackerError();
            return;
        }
        AutoTrackInfo autoTrackInfo = new AutoTrackInfo();
        setBaseInfo(autoTrackInfo);
        autoTrackInfo.setOt(TrackerEventType.BUSINESS.getEventValue());
        autoTrackInfo.setBs(BusinessType.PLAYTIME.getBusinessValue());
        autoTrackInfo.setNt(playTime);
        autoTrackInfo.setE(playType);
        KLog.d(TAG, "uploadPlayTimeEvent" + autoTrackInfo.toString());
        handleData(autoTrackInfo);

    }

    @Override
    public void uploadListenEvent(String c) {


        if (XmAutoTracker.getInstance().getApplication() == null) {
            noInitAutoTrackerError();
            return;
        }
        AutoTrackInfo autoTrackInfo = new AutoTrackInfo();
        setBaseInfo(autoTrackInfo);
        autoTrackInfo.setOt(TrackerEventType.BUSINESS.getEventValue());
        autoTrackInfo.setBs(BusinessType.LISTENINFO.getBusinessValue());
        autoTrackInfo.setC(c);
        KLog.d(TAG, "uploadListenEvent" + autoTrackInfo.toString());
        handleData(autoTrackInfo);

    }

    @Override
    public void uploadClubScore(String content) {
        if (XmAutoTracker.getInstance().getApplication() == null) {
            noInitAutoTrackerError();
            return;
        }
        AutoTrackInfo autoTrackInfo = new AutoTrackInfo();
        setBaseInfo(autoTrackInfo);
        autoTrackInfo.setOt(TrackerEventType.BUSINESS.getEventValue());
        autoTrackInfo.setBs(BusinessType.CLUBGROUPSCORE.getBusinessValue());
        autoTrackInfo.setC(content);
        KLog.d(TAG, "uploadClubScore" + autoTrackInfo.toString());
        //车信必须每条都保存数据库，故只走定时上报，不走100条缓存满上报
        database.save(autoTrackInfo);
    }

    //======================收集业务信息接口end============================


    //==============================外部调用方法====================================

    /**
     * 定时上传
     */
    public void timerUpload() {
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                asyncUpload(null);
                reportOnlineTime();
                ThreadDispatcher.getDispatcher().postDelayed(this, time);
            }
        }, time);
    }


    /**
     * 直接上报事件
     *
     * @param type
     * @param event
     * @param id
     * @param pagePath
     * @param pagePathDesc
     */
    public void directUploadEvent(TrackerEventType type, String event, String id, String pagePath, String pagePathDesc) {
        if (XmAutoTracker.getInstance().getApplication() == null) {
            noInitAutoTrackerError();
            return;
        }
        AutoTrackInfo autoTrackInfo = setDirectUpdateInfo(type, event, id, pagePath, pagePathDesc);
        KLog.d(TAG, "directUploadEvent" + autoTrackInfo.toString());
        uploadEvent(autoTrackInfo);
    }

    /**
     * 保存数据到数据库当应用遭到强杀时
     */
    public void saveCacheDataToDb() {
        if (autoTrackCache != null && autoTrackCache.size() > 0) {
            database.saveAll(autoTrackCache);
            autoTrackCache.clear();
        }

    }
    //==============================外部调用方法end====================================


    //==================================内部方法=============================================
    private void reportOnlineTime() {
        long curTime = currTimeMillis();
        long timeInterval = curTime - onlineTimeAt;
        onlineTimeAt = curTime;
        if (timeInterval > 0) {
            uploadOnlineTimeEvent(timeInterval, BusinessType.APPTIME.getBusinessValue());
            KLog.i(format("reportOnlineTime, timeInterval=%d", timeInterval));
        }
    }


    private long currTimeMillis() {
        return SystemClock.elapsedRealtime();
    }

    /**
     * 设置基本信息收集
     *
     * @param autoTrackInfo
     */
    private void setBaseInfo(AutoTrackInfo autoTrackInfo) {
        autoTrackInfo.setOd(System.currentTimeMillis());
        autoTrackInfo.setUid(XmAutoTracker.getInstance().getUserId());
    }

    /**
     * 设置点击信息 收集
     *
     * @param event
     * @param id
     * @param pagePath
     * @param pageName
     */
    private AutoTrackInfo setClickInfo(String event, String id, String pagePath, String pageName) {
        AutoTrackInfo autoTrackInfo = new AutoTrackInfo();
        setBaseInfo(autoTrackInfo);
        autoTrackInfo.setOt(TrackerEventType.ONCLICK.getEventValue());
        autoTrackInfo.setVn(pageName);
        autoTrackInfo.setVu(pagePath);
        autoTrackInfo.setE(event);
        autoTrackInfo.setC(id == null ? event : id);
        return autoTrackInfo;
    }

    private AutoTrackInfo setDirectUpdateInfo(TrackerEventType type, String event, String id, String pagePath, String pageName) {
        AutoTrackInfo autoTrackInfo = new AutoTrackInfo();
        setBaseInfo(autoTrackInfo);
        autoTrackInfo.setOt(type.getEventValue());
        autoTrackInfo.setVn(pageName);
        autoTrackInfo.setVu(pagePath);
        autoTrackInfo.setE(event);
        autoTrackInfo.setC(id == null ? event : id);
        return autoTrackInfo;
    }


    /**
     * 处理打点数据
     *
     * @param autoTrackInfo
     */
    private void handleData(AutoTrackInfo autoTrackInfo) {
        autoTrackCache.add(autoTrackInfo);
        if (autoTrackCache.size() >= cacheSize) {
            List<AutoTrackInfo> infos = new ArrayList<>();
            infos.addAll(autoTrackCache);
            autoTrackCache.clear();
            asyncUpload(infos);
        }

    }

    /**
     * 异步上报
     *
     * @param infos
     */
    private void asyncUpload(final List<AutoTrackInfo> infos) {
        ThreadDispatcher.getDispatcher().postSerial(new Runnable() {
            @Override
            public void run() {
                handleDbUpload(infos);
            }
        }, Priority.LOW);
    }


    /**
     * 上报
     *
     * @param infos
     */
    private synchronized void handleDbUpload(List<AutoTrackInfo> infos) {
        if (infos != null) {
            database.saveAll(infos);
        }
        long count = database.queryCount(AutoTrackInfo.class);
        if (count >= uploadCount) {
            List<AutoTrackInfo> uploadInfos = database.queryData(new QueryBuilder<>(AutoTrackInfo.class).orderBy(OPERATIONDATE).limit(0, uploadCount));
            batchUploadEvent(uploadInfos, UploadEventType.EVENT_COMPRESS);
        } else {
            if (infos == null) {
                List<AutoTrackInfo> uploadInfos = database.queryData(new QueryBuilder<>(AutoTrackInfo.class).orderBy(OPERATIONDATE).limit(0, (int) count));
                batchUploadEvent(uploadInfos, UploadEventType.EVENT_COMPRESS);
            }
        }
    }

    //==================================内部方法end=============================================

    private void notImplementsAnnotationError(String className) {
        String errorMsg = " you must be use annotation(PageDescComponent) \n class:"
                + className
                + "\n must be implements annotation for collect UI page event msg...please check..."
                + "\n example @PageDescComponent(EventConstants.PageDescClick.mainActivityPagePathDesc)"
                + "\n public class MainActivity extends BaseActivity"
                + "\n 参考AutoTracker module readme 关于页面切换场景统计：（请参考AppStore使用）";
        XmEventException.doException(errorMsg);
    }

    private void implementsPageDescribeAnnotationError(String className) {
        String errorMsg = " implements annotation PageDescComponent error, because annotation PageDescClick value is null or length is not one \n class:"
                + className
                + "\n must be implements annotation success for collect UI page event msg...please check...";
        XmEventException.doException(errorMsg);
    }

    private void noInitAutoTrackerError() {
        String errorMsg = " if you want to use AutoTracker you must in your application init AutoTracker \n for example:"
                + "XmAutoTracker.getInstance().init(AppHolder.getInstance().getAppContext());...please check...";
        XmEventException.doException(errorMsg);
    }

}
