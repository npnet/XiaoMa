package com.xiaoma.bdmap;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.fence.CreateFenceRequest;
import com.baidu.trace.api.fence.CreateFenceResponse;
import com.baidu.trace.api.fence.DeleteFenceRequest;
import com.baidu.trace.api.fence.DeleteFenceResponse;
import com.baidu.trace.api.fence.FenceListResponse;
import com.baidu.trace.api.fence.HistoryAlarmResponse;
import com.baidu.trace.api.fence.MonitoredStatus;
import com.baidu.trace.api.fence.MonitoredStatusByLocationRequest;
import com.baidu.trace.api.fence.MonitoredStatusByLocationResponse;
import com.baidu.trace.api.fence.MonitoredStatusResponse;
import com.baidu.trace.api.fence.OnFenceListener;
import com.baidu.trace.api.fence.UpdateFenceResponse;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.mapadapter.constant.MapConstant;
import com.xiaoma.mapadapter.interfaces.IGeoFence;
import com.xiaoma.mapadapter.listener.OnFenceStatusChangeListener;
import com.xiaoma.mapadapter.listener.OnGeoFenceListener;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by minxiwen on 2017/12/18 0018.
 */

public class BDFenceClient implements IGeoFence {
    private Context context;
    private LBSTraceClient client;
    private HashMap<Long, String> fences = new HashMap<>();
    //轨迹服务
//    private static Trace mTrace;
    // 轨迹服务ID
    private static final long serviceId = 147476;
    //requestId 生成器
    private AtomicInteger mSequenceGenerator = new AtomicInteger();
    //Entity标识
    public String entityName = "myTrace";
    //坐标type
    private CoordType coordType = CoordType.gcj02;
    private OnFenceStatusChangeListener onFenceStatusChangeListener;
    private OnGeoFenceListener onGeoFenceListener;

    // 初始化轨迹服务监听器
    private OnTraceListener mTraceListener = new OnTraceListener() {
        @Override
        public void onBindServiceCallback(int i, String s) {
            KLog.d("====================>" + i + s);
        }

        // 开启服务回调
        @Override
        public void onStartTraceCallback(int status, String message) {
            KLog.d("====================>" + status + message);
        }

        // 停止服务回调
        @Override
        public void onStopTraceCallback(int status, String message) {
            KLog.d("====================>" + status + message);
        }

        // 开启采集回调
        @Override
        public void onStartGatherCallback(int status, String message) {
            KLog.d("====================>" + status + message);
        }

        // 停止采集回调
        @Override
        public void onStopGatherCallback(int status, String message) {
            KLog.d("====================>" + status + message);
        }

        // 推送回调
        @Override
        public void onPushCallback(byte messageNo, PushMessage message) {
            if (message != null) {
                KLog.d("====================> onPushCallback" + message.getMessage() + ", " + message.getFenceAlarmPushInfo().getMonitoredAction());
            }
        }

        @Override
        public void onInitBOSCallback(int i, String s) {

        }
    };

    // 初始化围栏监听器
    private OnFenceListener mFenceListener = new OnFenceListener() {
        // 创建围栏回调
        @Override
        public void onCreateFenceCallback(CreateFenceResponse response) {
            if (StatusCodes.SUCCESS == response.getStatus()) {
                KLog.d("=======================> create fence success" + response.getFenceType() + response.getFenceName());
                // 创建查询本地围栏监控状态请求
//                fences.put(response.getFenceId(), response.getFenceName());
//                ArrayList<Long> fenceIds = new ArrayList<>();
//                fenceIds.add(Long.valueOf(response.getFenceId()));
//                MonitoredStatusRequest request = MonitoredStatusRequest.buildLocalRequest(getTag(), serviceId, entityName, fenceIds);
//                // 查询围栏监控者状态
//                mTraceClient.queryMonitoredStatus(request, mFenceListener);

                // 创建查询服务端围栏监控状态请求
                fences.put(response.getFenceId(), response.getFenceName());
                ArrayList<Long> fenceIds = new ArrayList<>();
                fenceIds.add(Long.valueOf(response.getFenceId()));
                com.baidu.trace.model.LatLng latLng = new com.baidu.trace.model.LatLng(LocationManager.getInstance().getCurrentPosition().latitude, LocationManager.getInstance().getCurrentPosition().longitude);
//                MonitoredStatusByLocationRequest request = MonitoredStatusByLocationRequest.buildServerRequest(getTag(), serviceId, entityName, fenceIds, latLng, coordType);
                MonitoredStatusByLocationRequest request = MonitoredStatusByLocationRequest.buildLocalRequest(getTag(), serviceId, entityName, fenceIds, latLng, coordType);
                client.queryMonitoredStatusByLocation(request, mFenceListener);
                if (onGeoFenceListener != null) {
                    onGeoFenceListener.onGeoFenceCreateFinished(MapConstant.CREATE_GEOFENCE_SUCCESS, response.getMessage());
                }
            } else {
                KLog.d("=======================> create fence failed");
                if (onGeoFenceListener != null) {
                    onGeoFenceListener.onGeoFenceCreateFinished(MapConstant.CREATE_GEOFENCE_FAIL, response.getMessage());
                }
            }
        }

        // 更新围栏回调
        @Override
        public void onUpdateFenceCallback(UpdateFenceResponse response) {

        }

        // 删除围栏回调
        @Override
        public void onDeleteFenceCallback(DeleteFenceResponse response) {
            if (StatusCodes.SUCCESS == response.getStatus()) {
                KLog.d("=======================> delete fence success" + response.getFenceType() + response.getMessage() + response.getFenceIds());
            } else {
                KLog.d("=======================> delete fence failed");
            }
        }

        // 围栏列表回调
        @Override
        public void onFenceListCallback(FenceListResponse response) {
        }

        // 监控状态回调
        @Override
        public void onMonitoredStatusCallback(MonitoredStatusResponse response) {
            if (StatusCodes.SUCCESS == response.getStatus()) {
                if (!ListUtils.isEmpty(response.getMonitoredStatusInfos())) {
                    MonitoredStatus status = response.getMonitoredStatusInfos().get(0).getMonitoredStatus();
                    long fenceId = response.getMonitoredStatusInfos().get(0).getFenceId();
                    String customId = fences.get(fenceId);
                    KLog.d("=======================>onMonitoredStatusCallback success" + status + ", " + customId + ", " + fenceId);
                    int action = 0;
                    if (status == MonitoredStatus.in) {
                        KLog.d("========================> onMonitoredStatusCallback action in");
                        action = MapConstant.GEO_FENCE_IN;
                    } else if (status == MonitoredStatus.out) {
                        KLog.d("========================> onMonitoredStatusCallback action out");
                        action = MapConstant.GEO_FENCE_OUT;
                    }
                    if (onFenceStatusChangeListener != null) {
                        onFenceStatusChangeListener.onFenceStatusChange(action, customId);
                    }
                }
            }
        }

        // 指定位置监控状态回调
        @Override
        public void onMonitoredStatusByLocationCallback(MonitoredStatusByLocationResponse response) {
            if (StatusCodes.SUCCESS == response.getStatus()) {
                if (!ListUtils.isEmpty(response.getMonitoredStatusInfos())) {
                    MonitoredStatus status = response.getMonitoredStatusInfos().get(0).getMonitoredStatus();
                    long fenceId = response.getMonitoredStatusInfos().get(0).getFenceId();
                    String customId = fences.get(fenceId);
                    KLog.d("=======================>queryMonitoredStatusByLocation success" + status + ", " + customId + ", " + fenceId);
                    int action = 0;
                    if (status == MonitoredStatus.in) {
                        action = MapConstant.GEO_FENCE_IN;
                        KLog.d("========================> queryMonitoredStatusByLocation action in");
                    } else if (status == MonitoredStatus.out) {
                        action = MapConstant.GEO_FENCE_OUT;
                        KLog.d("========================> queryMonitoredStatusByLocation action out");
                    }
                    if (onFenceStatusChangeListener != null) {
                        onFenceStatusChangeListener.onFenceStatusChange(action, customId);
                    }
                }
            }
        }

        // 历史报警回调
        @Override
        public void onHistoryAlarmCallback(HistoryAlarmResponse response) {
        }
    };

    public BDFenceClient(Context context) {
        this.context = context;
        client = new LBSTraceClient(context.getApplicationContext());
        // 设备标识
        String iccid = ConfigManager.DeviceConfig.getICCID(context);
        entityName = iccid;
        if (TextUtils.isEmpty(iccid)) {
            entityName = ConfigManager.DeviceConfig.getIMEI(context);
        }
        // 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
//        boolean isNeedObjectStorage = false;
        // 初始化轨迹服务
//        mTrace = new Trace(serviceId, entityName, isNeedObjectStorage);
    }

    @Override
    public void start() {
//        client.setLocationMode(LocationMode.High_Accuracy);
//
//        // 定位周期(单位:秒)
//        int gatherInterval = 120;
//        // 打包回传周期(单位:秒)
//        int packInterval = 5;
//        // 设置定位和打包周期
//        client.setInterval(gatherInterval, packInterval);
//
//        // 开启服务
//        client.startTrace(mTrace, mTraceListener);
//        // 开启采集
//        client.startGather(mTraceListener);
    }

    @Override
    public void stop() {
//        // 停止轨迹服务
//        client.stopTrace(mTrace, mTraceListener);
//        // 停止采集
//        client.stopGather(mTraceListener);
    }

    @Override
    public void setOnFenceStatusChangeListener(OnFenceStatusChangeListener onFenceStatusChangeListener) {
        this.onFenceStatusChangeListener = onFenceStatusChangeListener;
    }

    @Override
    public void setGeoFenceListener(OnGeoFenceListener listener) {
        this.onGeoFenceListener = listener;
    }

    @Override
    public boolean removeGeoFence(String messageId) {
        long fenceId = getFenceId(messageId);
        if (0 != fenceId) {
            ArrayList<Long> fenceIds = new ArrayList<>();
            fenceIds.add(Long.valueOf(fenceId));
//            DeleteFenceRequest deleteFenceRequest = DeleteFenceRequest.buildServerRequest(getTag(), serviceId, entityName, fenceIds);
            DeleteFenceRequest deleteFenceRequest = DeleteFenceRequest.buildLocalRequest(getTag(), serviceId, entityName, fenceIds);
            client.deleteFence(deleteFenceRequest, mFenceListener);
        }
        return true;
    }

    @Override
    public void addGeoFence(LatLng latLng, float radius, String customId) {
        // 请求标识
        int tag = getTag();
        // 围栏名称
        String fenceName = customId;
        // 监控对象
        String monitoredPerson = entityName;
        // 围栏圆心
        com.baidu.trace.model.LatLng center = new com.baidu.trace.model.LatLng(latLng.latitude, latLng.longitude);
        // 围栏半径（单位 : 米）
        double radius1 = radius;
        // 去噪精度, 各定位模式的平均精度供开发者参考：GPS定位精度均值为10米，WIFI定位精度均值为24米，基站定位精度均值为210米
        int denoise = 100;
        // 创建服务端圆形围栏请求实例
//        CreateFenceRequest localCircleFenceRequest = CreateFenceRequest.buildServerCircleRequest(tag, serviceId, fenceName, monitoredPerson, center, radius1, denoise, coordType);
        // 创建本地圆形围栏
        CreateFenceRequest localCircleFenceRequest = CreateFenceRequest.buildLocalCircleRequest(tag, serviceId, fenceName, monitoredPerson, center, radius, denoise, coordType);
        client.createFence(localCircleFenceRequest, mFenceListener);
    }

    /**
     * 获取请求标识
     *
     * @return
     */
    public int getTag() {
        return mSequenceGenerator.incrementAndGet();
    }

    private long getFenceId(String messageId) {
        if (fences.containsValue(messageId)) {
            for (long fenceId : fences.keySet()) {
                if (fences.get(fenceId).equals(messageId)) {
                    return fenceId;
                }
            }
        }
        return 0;
    }
}
