package com.qiming.fawcard.synthesize.core.drivescore.presenter;

import android.os.Build;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.qiming.fawcard.synthesize.BaseTestCase;
import com.qiming.fawcard.synthesize.DrivedApiMock;
import com.qiming.fawcard.synthesize.base.BaseResponse;
import com.qiming.fawcard.synthesize.base.application.QmApplication;
import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.base.system.service.DriverService;
import com.qiming.fawcard.synthesize.base.util.DeviceUtils;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;
import com.qiming.fawcard.synthesize.data.entity.DrivedResponse;
import com.qiming.fawcard.synthesize.data.entity.SnapShotResponse;
import com.qiming.fawcard.synthesize.data.source.remote.IDrivedApi;
import com.xiaoma.utils.log.KLog;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.qiming.fawcard.synthesize.base.constant.QMConstant.RequestFailMessage.SERVER_ERROR;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

@Config(sdk = Build.VERSION_CODES.JELLY_BEAN_MR2)
@RunWith(PowerMockRunner.class)
@PrepareForTest({DeviceUtils.class, DriveScorePresenter.class})
public class DriveScorePresenterTest extends BaseTestCase {
    private static final String TAG = "DriveScorePresenterTest";
    private boolean mDriveScoreDataIsExist = false;
    private boolean mDriveInfoDataIsExist = false;
    private QMConstant.RequestFailMessage mNotifyErrorMsgType = SERVER_ERROR;
    private DrivedResponse mDrivedResponse;
    private IDrivedApi mApi;
    //    private MockExecutor mockExecutor;
    private DrivedApiMock mockDrivedApi;

    public class DriverServiceMock extends DriverService {
        @Override
        public void updateDriveScore(DriveScoreHistoryEntity data) {
            if (data.isValid) {
                mDriveScoreDataIsExist = true;
            }
        }

        @Override
        public void updateDriveInfo(DriveScoreHistoryDetailEntity data) {
            if (data.isValid) {
                mDriveInfoDataIsExist = true;
            }
        }

        @Override
        public void onRequestFailed(QMConstant.RequestFailMessage msg) {
            mNotifyErrorMsgType = msg;
        }
    }

    private DriveScorePresenter mDriveScorePresenter;
    private DriverServiceMock mServiceMock;

    @Before
    public void setUp() {
        ShadowLog.stream = System.out;

        mServiceMock = new DriverServiceMock();
//        mockExecutor = new MockExecutor();
//        //mServiceMock.setExecutor(mockExecutor);
        mockDrivedApi = new DrivedApiMock();
        setUpMockDrivedApi(mockDrivedApi);
        mApi = mockDrivedApi;
        mDriveScorePresenter = new DriveScorePresenter(mServiceMock, mApi);
    }

    public void setUpDrivedResponse(BaseResponse.Status status) {
        DrivedResponse response = new DrivedResponse();
        response.status = status;
        DrivedResponse.Bean bean = new DrivedResponse.Bean();
        bean.startTime = 0L;
        bean.endTime = 0L;
        bean.travelTime = 0L;
        bean.travelOdograph = 20.0;
        bean.score = 100.0;
        bean.rapidAccelerateNum = 0L;
        bean.rapidDecelerationNum = 0L;
        bean.sharpTurnNum = 0L;
        bean.avgSpeed = 0.0;
        bean.avgFuelConsumer = 0.0;
        List<DrivedResponse.Bean> result = new ArrayList<>();
        result.add(bean);
        response.setResult(result);
        mockDrivedApi.setDriveScore(response);
    }

    public void setUpSnapShotResponse(BaseResponse.Status status) {
        SnapShotResponse.ResultEntity entity = new SnapShotResponse().new ResultEntity();
        mockDrivedApi.setSnapShotResponse(entity, "1", "2", "30",
                "5", "msg01", "sc01", status, "err01", "errMsg01");
    }

    public void setUpMockDrivedApi(DrivedApiMock mockDrivedApi) {
        setUpDrivedResponse(BaseResponse.Status.SUCCEED);
        setUpSnapShotResponse(BaseResponse.Status.SUCCEED);
        mockDrivedApi.setDriveScoreTokenResponse("tk001", "res002", "resMsg003");
        mockDrivedApi.setDriveScoreTboxSnResponse("resTBoxSn001", "resMsg002", "vin003", "tboxSN004");
        mockDrivedApi.setDriveScoreCheckAvnResponse("checkAvn_token");
        mockDrivedApi.setUploadScoreResponse("ss", "ook");
    }

    public void setDriveScoreResponse(boolean isResObjValid) {
        if (isResObjValid) {
            List<DrivedResponse.Bean> result = new ArrayList<>();
            DrivedResponse.Bean bean = new DrivedResponse.Bean();
            bean.startTime = 1508849859848L;
            bean.endTime = 2608849859848L;
            bean.travelTime = 3508849859848L;
            bean.travelOdograph = 120.0;
            bean.score = 99.0;
            bean.rapidAccelerateNum = 2L;
            bean.rapidDecelerationNum = 2L;
            bean.sharpTurnNum = 3L;
            bean.avgSpeed = 60.0;
            bean.avgFuelConsumer = 3.0;
            result.add(bean);
            mDrivedResponse = new DrivedResponse();
            mDrivedResponse.setResult(result);
        } else {
            mDrivedResponse = null;
        }
    }

    // 转换数据
    public void setDriveScore4Test(DrivedResponse in, DriveScoreHistoryEntity out) {
        DrivedResponse.Bean bean = in.getResult().get(0);

        out.startTime = bean.startTime;
        out.endTime = bean.endTime;
        out.travelTime = bean.travelTime;
        out.travelDist = bean.travelOdograph;
        out.score = bean.score;
        out.accNum = bean.rapidAccelerateNum;
        out.decNum = bean.rapidDecelerationNum;
        out.turnNum = bean.sharpTurnNum;
        out.avgSpeed = bean.avgSpeed;
        out.avgFuel = bean.avgFuelConsumer;
    }

    // checkTspToken
    @Test
    public void checkTspTokenTest_errorCodeIs_user0032() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        PowerMockito.mockStatic(DeviceUtils.class);
        Mockito.when(DeviceUtils.hasInternet(QmApplication.getContext())).thenReturn(true);

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("checkTspToken", new Class[]{String.class});
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, "user.0032");

        assertEquals(QMConstant.RequestFailMessage.UNKNOWN, mNotifyErrorMsgType);
        KLog.d(TAG, "checkTspTokenTest_errorCodeIs_user0032: 测试成功");
    }

    @Test
    public void checkTspTokenTest_errorCodeIsNot_user0032() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("checkTspToken", new Class[]{String.class});
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, "user.0099");

        assertNotEquals(QMConstant.RequestFailMessage.UNKNOWN, mNotifyErrorMsgType);
        KLog.d(TAG, "checkTspTokenTest_errorCodeIsNot_user0032: 测试成功");
    }

    // checkNetwork
    @Test
    public void checkNetworkTest_HasNotInternet() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        PowerMockito.mockStatic(DeviceUtils.class);
        Mockito.when(DeviceUtils.hasInternet(QmApplication.getContext())).thenReturn(false);

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("checkNetwork", new Class[0]);
        method.setAccessible(true);
        Object retObject;
        retObject = method.invoke(mDriveScorePresenter, new Object[]{});
        boolean expectedRet = (boolean) retObject;

        assertEquals(QMConstant.RequestFailMessage.NETWORK_DISCONNECT, mNotifyErrorMsgType);
        assertFalse(expectedRet);
        KLog.d(TAG, "checkNetworkTest_HasNotInternet: 测试成功");
    }

    @Test
    public void checkNetworkTest_HasInternet() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        PowerMockito.mockStatic(DeviceUtils.class);
        Mockito.when(DeviceUtils.hasInternet(QmApplication.getContext())).thenReturn(true);

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("checkNetwork", new Class[0]);
        method.setAccessible(true);
        Object retObject;
        retObject = method.invoke(mDriveScorePresenter, new Object[]{});
        boolean expectedRet = (boolean) retObject;

        assertNotEquals(QMConstant.RequestFailMessage.NETWORK_DISCONNECT, mNotifyErrorMsgType);
        assertTrue(expectedRet);
        KLog.d(TAG, "checkNetworkTest_HasInternet: 测试成功");
    }

    // notifyError
    @Test
    public void notifyErrorTest_NetworkBadSignal() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("notifyError", new Class[]{Throwable.class});
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, new SocketTimeoutException());

        assertEquals(QMConstant.RequestFailMessage.NETWORK_BADSIGNAL, mNotifyErrorMsgType);
        KLog.d(TAG, "notifyErrorTest_NetworkBadSignal: 测试成功");
    }

    @Test
    public void notifyErrorTest_ServerError() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        HttpException e = new HttpException(Response.error(403, ResponseBody.create(MediaType.parse("application/json"), "Forbidden")));
        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("notifyError", new Class[]{Throwable.class});
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, e);
        assertEquals(QMConstant.RequestFailMessage.SERVER_ERROR, mNotifyErrorMsgType);
        KLog.d(TAG, "notifyErrorTest_ServerError: 测试成功");
    }

    @Test
    public void notifyErrorTest_Unknown() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("notifyError", new Class[]{Throwable.class});
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, new Exception());

        assertEquals(QMConstant.RequestFailMessage.UNKNOWN, mNotifyErrorMsgType);
        KLog.d(TAG, "notifyErrorTest_Unknown: 测试成功");
    }

    @Test
    public void getTboxSnByVin() throws Exception {
        String vin = "1";
        String token = "1";

        PowerMockito.mockStatic(DeviceUtils.class);
        PowerMockito.when(DeviceUtils.hasInternet(QmApplication.getContext())).thenReturn(true);

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("getTboxSnByVin", new Class[]{String.class, String.class});
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, vin, token);

        assertEquals("vin003", mDriveScorePresenter.getCacheVin());
        assertEquals("tboxSN004", mDriveScorePresenter.getCacheTboxSn());

        KLog.d(TAG, "getTboxSnByVinTest: 测试成功");
    }

    @Test
    public void checkAvnTest() throws Exception {

        PowerMockito.mockStatic(DeviceUtils.class);
        PowerMockito.when(DeviceUtils.hasInternet(QmApplication.getContext())).thenReturn(true);

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("checkAvn", new Class[0]);
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, new Object[]{});

        assertEquals("checkAvn_token", mDriveScorePresenter.getCacheTspToken());

        KLog.d(TAG, "checkAvnTest: 测试成功");
    }

    @Test
    public void getTokenTest() throws Exception {
        PowerMockito.mockStatic(DeviceUtils.class);
        PowerMockito.when(DeviceUtils.hasInternet(QmApplication.getContext())).thenReturn(true);

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("getToken", new Class[0]);
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, new Object[]{});

        assertEquals("tk001", mDriveScorePresenter.getCacheToken());

        KLog.d(TAG, "getTokenTest: 测试成功");
    }

    @Test
    public void reportDriveScoreTest() throws Exception {
        PowerMockito.mockStatic(DeviceUtils.class);
        PowerMockito.when(DeviceUtils.hasInternet(QmApplication.getContext())).thenReturn(true);

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("getToken", new Class[0]);
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, new Object[]{});

        // onNext函数无实现
        KLog.d(TAG, "reportDriveScoreTest: 测试成功");
    }

    @Test
    public void getDriveInfoTest_ResponseStatusFAILED() throws Exception {
        PowerMockito.mockStatic(DeviceUtils.class);
        PowerMockito.when(DeviceUtils.hasInternet(QmApplication.getContext())).thenReturn(true);

        setUpSnapShotResponse(BaseResponse.Status.FAILED);
        mDriveScorePresenter.mApi = mockDrivedApi;
        mDriveScorePresenter.isDebugMode = false;

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("getDriveInfo", new Class[0]);
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, new Object[]{});

        assertFalse(mDriveInfoDataIsExist);

        KLog.d(TAG, "getDriveInfoTest_ResponseStatusFAILED: 测试成功");
    }

    @Test
    public void getDriveInfoTest_ResponseStatusSUCCEED() throws Exception {
        PowerMockito.mockStatic(DeviceUtils.class);
        PowerMockito.when(DeviceUtils.hasInternet(QmApplication.getContext())).thenReturn(true);

        setUpDrivedResponse(BaseResponse.Status.SUCCEED);
        mDriveScorePresenter.mApi = mockDrivedApi;

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("getDriveInfo", new Class[0]);
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, new Object[]{});

        assertTrue(mDriveInfoDataIsExist);

        KLog.d(TAG, "getDriveInfoTest_ResponseStatusSUCCEED: 测试成功");
    }

    @Test
    public void getDriveScoreTest_ResponseStatusFAILED() throws Exception {
        PowerMockito.mockStatic(DeviceUtils.class);
        PowerMockito.when(DeviceUtils.hasInternet(QmApplication.getContext())).thenReturn(true);

        setUpDrivedResponse(BaseResponse.Status.FAILED);
        mDriveScorePresenter.mApi = mockDrivedApi;
        mDriveScorePresenter.isDebugMode = false;

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("getDriveScore", new Class[0]);
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, new Object[]{});

        assertFalse(mDriveScoreDataIsExist);

        KLog.d(TAG, "getDriveScoreTest_ResponseStatusFAILED: 测试成功");
    }

    @Test
    public void getDriveScoreTest_ResponseStatusSUCCEED() throws Exception {
        PowerMockito.mockStatic(DeviceUtils.class);
        PowerMockito.when(DeviceUtils.hasInternet(QmApplication.getContext())).thenReturn(true);

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("getDriveScore", new Class[0]);
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, new Object[]{});

        assertTrue(mDriveScoreDataIsExist);

        KLog.d(TAG, "getDriveScoreTest_ResponseStatusSUCCEED: 测试成功");
    }

    // updateDriveScore
    @Test
    public void updateDriveScoreTest_DrivedResponseIsValid() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        setDriveScoreResponse(true);
        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("updateDriveScore", new Class[]{DrivedResponse.class});
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, mDrivedResponse);

        assertTrue(mDriveScoreDataIsExist);
        KLog.d(TAG, "updateDriveScoreTest_DrivedResponseIsValid: 测试成功");
    }

    @Test
    public void updateDriveScoreTest_DrivedResponseIsInvalid() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        setDriveScoreResponse(false);
        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("updateDriveScore", new Class[]{DrivedResponse.class});
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, mDrivedResponse);

        assertFalse(mDriveScoreDataIsExist);
        KLog.d(TAG, "updateDriveScoreTest_DrivedResponseIsInvalid: 测试成功");
    }

    // setDriveInfo
    @Test
    public void setDriveInfoTest_avgFuel_avgSpeed_BothNotNull() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        SnapShotResponse in = new SnapShotResponse();
        SnapShotResponse.ResultEntity entity = in.new ResultEntity();
        HashMap<String, SnapShotResponse.ResultEntity.DetailEntity> mapData = new HashMap<String, SnapShotResponse.ResultEntity.DetailEntity>();
        SnapShotResponse.ResultEntity.DetailEntity avgFuel = entity.new DetailEntity();
        avgFuel.setVal("22.0");
        mapData.put("V179", avgFuel);

        SnapShotResponse.ResultEntity.DetailEntity avgSpeed = entity.new DetailEntity();
        avgSpeed.setVal("66.6");
        mapData.put("V180", avgSpeed);

        // 设定SnapShotResponse
        entity.setMapData(mapData);
        in.setResult(entity);

        DriveScoreHistoryDetailEntity out = new DriveScoreHistoryDetailEntity();

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("setDriveInfo", new Class[]{SnapShotResponse.class, DriveScoreHistoryDetailEntity.class});
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, in, out);

        Double expectedFuel = 22.0;
        Double expectedSpeed = 66.6;
        assertEquals(expectedFuel, out.avgFuel);
        assertEquals(expectedSpeed, out.avgSpeed);
        KLog.d(TAG, "setDriveInfoTest_avgFuel_avgSpeed_BothNotNull: 测试成功");
    }

    @Test
    public void setDriveInfoTest_avgFuel_avgSpeed_BothNull() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        SnapShotResponse in = new SnapShotResponse();
        SnapShotResponse.ResultEntity entity = in.new ResultEntity();
        HashMap<String, SnapShotResponse.ResultEntity.DetailEntity> mapData = new HashMap<String, SnapShotResponse.ResultEntity.DetailEntity>();

        // 设定SnapShotResponse
        entity.setMapData(mapData);
        in.setResult(entity);

        DriveScoreHistoryDetailEntity out = new DriveScoreHistoryDetailEntity();

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("setDriveInfo", new Class[]{SnapShotResponse.class, DriveScoreHistoryDetailEntity.class});
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, in, out);

        Double expectedFuel = 0.0;
        Double expectedSpeed = 0.0;
        assertEquals(expectedFuel, out.avgFuel);
        assertEquals(expectedSpeed, out.avgSpeed);
        KLog.d(TAG, "setDriveInfoTest_avgFuel_avgSpeed_BothNull: 测试成功");
    }

    @Test
    public void setDriveInfoTest_avgFuelNotNull_avgSpeedIsNull() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        SnapShotResponse in = new SnapShotResponse();
        SnapShotResponse.ResultEntity entity = in.new ResultEntity();
        HashMap<String, SnapShotResponse.ResultEntity.DetailEntity> mapData = new HashMap<String, SnapShotResponse.ResultEntity.DetailEntity>();
        SnapShotResponse.ResultEntity.DetailEntity avgFuel = entity.new DetailEntity();
        avgFuel.setVal("22.0");
        mapData.put("V179", avgFuel);

        // 设定SnapShotResponse
        entity.setMapData(mapData);
        in.setResult(entity);

        DriveScoreHistoryDetailEntity out = new DriveScoreHistoryDetailEntity();

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("setDriveInfo", new Class[]{SnapShotResponse.class, DriveScoreHistoryDetailEntity.class});
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, in, out);

        Double expectedFuel = 22.0;
        Double expectedSpeed = 0.0;
        assertEquals(expectedFuel, out.avgFuel);
        assertEquals(expectedSpeed, out.avgSpeed);
        KLog.d(TAG, "setDriveInfoTest_avgFuelNotNull_avgSpeedIsNull: 测试成功");
    }

    @Test
    public void setDriveInfoTest_avgFuelIsNull_avgSpeedNotNull() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        SnapShotResponse in = new SnapShotResponse();
        SnapShotResponse.ResultEntity entity = in.new ResultEntity();
        HashMap<String, SnapShotResponse.ResultEntity.DetailEntity> mapData = new HashMap<String, SnapShotResponse.ResultEntity.DetailEntity>();

        SnapShotResponse.ResultEntity.DetailEntity avgSpeed = entity.new DetailEntity();
        avgSpeed.setVal("66.6");
        mapData.put("V180", avgSpeed);

        // 设定SnapShotResponse
        entity.setMapData(mapData);
        in.setResult(entity);

        DriveScoreHistoryDetailEntity out = new DriveScoreHistoryDetailEntity();

        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("setDriveInfo", new Class[]{SnapShotResponse.class, DriveScoreHistoryDetailEntity.class});
        method.setAccessible(true);
        method.invoke(mDriveScorePresenter, in, out);

        Double expectedFuel = 0.0;
        Double expectedSpeed = 66.6;
        assertEquals(expectedFuel, out.avgFuel);
        assertEquals(expectedSpeed, out.avgSpeed);
        KLog.d(TAG, "setDriveInfoTest_avgFuelIsNull_avgSpeedNotNull: 测试成功");
    }

    // setDriveScore
    @Test
    public void setDriveScoreTest() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        setDriveScoreResponse(true);
        Method method = Class.forName(DriveScorePresenter.class.getName()).
                getDeclaredMethod("setDriveScore", new Class[]{DrivedResponse.class, DriveScoreHistoryEntity.class});
        method.setAccessible(true);
        DriveScoreHistoryEntity tarData = new DriveScoreHistoryEntity();
        tarData.avgSpeed = 60.00;
        method.invoke(mDriveScorePresenter, mDrivedResponse, tarData);

        DriveScoreHistoryEntity srcData = new DriveScoreHistoryEntity();
        setDriveScore4Test(mDrivedResponse, srcData);

        assertEquals(tarData.toString(), srcData.toString());
        KLog.d(TAG, "setDriveScoreTest: 测试成功");
    }
}

