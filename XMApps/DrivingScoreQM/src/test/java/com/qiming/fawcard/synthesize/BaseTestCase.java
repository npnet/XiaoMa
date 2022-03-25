package com.qiming.fawcard.synthesize;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.qiming.fawcard.synthesize.base.BaseResponse;
import com.qiming.fawcard.synthesize.base.RxJavaRule;
import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.base.system.callback.HistoryRegistrationCenter;
import com.qiming.fawcard.synthesize.base.system.service.DriverService;
import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreHomeActivity;
import com.qiming.fawcard.synthesize.core.drivescore.presenter.DriveScoreHomePresenter;
import com.qiming.fawcard.synthesize.core.drivescore.presenter.DriveScorePresenter;
import com.qiming.fawcard.synthesize.dagger2.component.ComponentHolder;
import com.qiming.fawcard.synthesize.dagger2.component.DaggerDriveScoreHomeComponent;
import com.qiming.fawcard.synthesize.dagger2.component.DaggerDriveServiceComponent;
import com.qiming.fawcard.synthesize.dagger2.component.DriveScoreHomeComponent;
import com.qiming.fawcard.synthesize.dagger2.component.DriveServiceComponent;
import com.qiming.fawcard.synthesize.dagger2.module.DriveScoreHistoryDetailModule;
import com.qiming.fawcard.synthesize.dagger2.module.DriveScoreHomeModule;
import com.qiming.fawcard.synthesize.dagger2.module.DriveServiceModule;
import com.qiming.fawcard.synthesize.data.entity.DrivedResponse;
import com.qiming.fawcard.synthesize.data.source.local.ORMLiteHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowLog.class}, sdk = Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BaseTestCase {
    public ActivityController<DriveScoreHomeActivity> homeActivityController;
    public DriveScoreHomePresenter presenter;
    public DriveScorePresenter driveScorePresenter;
    public ServiceController<DriverService> driverServiceController;
    public DrivedApiMock mockDrivedApi;
    public MockExecutor mockExecutor;
    ComponentHolder instanceMock;

    @Rule
    public RxJavaRule rule = new RxJavaRule();

    @Before
    public void setUp() {
        ShadowLog.stream = System.out;
        ORMLiteHelper.getInstance(getApplication());
        mockDrivedApi = new DrivedApiMock();
        mockExecutor = new MockExecutor();

        instanceMock = PowerMockito.mock(ComponentHolder.class);
        Whitebox.setInternalState(ComponentHolder.class, "INSTANCE", instanceMock);
        Mockito.doCallRealMethod().when(instanceMock).getDriveScoreHistoryDetailComponent(any(DriveScoreHistoryDetailModule.class));

    }
    @After
    public void tearDown(){
        TestUtils.resetSingleton(ORMLiteHelper.class, "mInstance");
        TestUtils.resetSingleton(ComponentHolder.class, "INSTANCE");
        HistoryRegistrationCenter.clear();
    }

    protected void bindService() {
        DriverService.DriverBinder mockBinder = Mockito.mock(DriverService.DriverBinder.class);
        Mockito.when(mockBinder.getService()).thenReturn(driverServiceController.get());
        presenter.setBinder(mockBinder);
    }

    protected void setUpHomeActivity() {
        homeActivityController = Robolectric.buildActivity(DriveScoreHomeActivity.class);
        injectHomeActivityDependency();

        homeActivityController.create().start().resume();
    }

    private void injectHomeActivityDependency(){
        DriveScoreHomeActivity homeActivity = homeActivityController.get();
        DriveScoreHomeModule mockDriveScoreHomeModule = spy(new DriveScoreHomeModule(homeActivity, homeActivity, homeActivity));
        presenter = mockDriveScoreHomeModule.provideDriveScoreHomePresenter();
        Mockito.when(mockDriveScoreHomeModule.provideDriveScoreHomePresenter()).thenReturn(presenter);
        DriveScoreHomeComponent component = DaggerDriveScoreHomeComponent.builder().driveScoreHomeModule(mockDriveScoreHomeModule).build();
        Mockito.doReturn(component).when(instanceMock).getDriveScoreHomeComponent(any(DriveScoreHomeModule.class));
    }

    protected void setUpService() {
        driverServiceController = Robolectric.buildService(DriverService.class);
        injectDriverServiceDependency();
        driverServiceController.create();
//        controller.get().setExecutor(mockExecutor);
//        ShadowApplication.getInstance().setComponentNameAndServiceForBindService(null, controller.get().new DriverBinder());
    }
    private void injectDriverServiceDependency(){
        driveScorePresenter = spy(new DriveScorePresenter(driverServiceController.get(), mockDrivedApi));
        driveScorePresenter.isDebugMode = false;
        DriveServiceModule mockDriveServiceModule= spy(new DriveServiceModule(driverServiceController.get()));
        Mockito.when(mockDriveServiceModule.provideDriveScorePresenter()).thenReturn(driveScorePresenter);
        DriveServiceComponent component = DaggerDriveServiceComponent.builder().driveServiceModule(mockDriveServiceModule).build();
        Mockito.doReturn(component).when(instanceMock).getDriveServiceComponent(any(DriveServiceModule.class));
    }
    // 点火
    public  void ignition()
    {
        Intent intent = new Intent(getContext(), DriverService.class);
        intent.putExtra(QMConstant.DRIVER_SERVICE_KEY,QMConstant.DRIVER_SERVICE_START);
        driverServiceController.get().onStartCommand(intent, 0,0);
    }

    // 熄火
    protected void stalled(){
        Intent intent = new Intent(getContext(), DriverService.class);
        intent.putExtra(QMConstant.DRIVER_SERVICE_KEY,QMConstant.DRIVER_SERVICE_END);
        driverServiceController.get().onStartCommand(intent, 0,0);
    }

    public Application getApplication() {
        return RuntimeEnvironment.application;
    }

    public Context getContext() {
        return RuntimeEnvironment.application;
    }

    // in: 急加速次数 急减速次数 急转弯次数 得分 行驶里程
    public void setDriveInfo(Long rapidAccelerateNum,
                              Long rapidDecelerationNum,
                              Long sharpTurnNum,
                              Double score,
                              Double travelDistance){
        DrivedResponse response = new DrivedResponse();
        response.status = BaseResponse.Status.SUCCEED;
        DrivedResponse.Bean bean = new DrivedResponse.Bean();
        bean.startTime = 0L;
        bean.endTime = 0L;
        bean.travelTime = 0L;
        if (travelDistance == null){
            travelDistance = 30.0;  // 默认值设定
        }
        bean.travelOdograph = travelDistance;
        bean.score = score;
        bean.rapidAccelerateNum = rapidAccelerateNum;
        bean.rapidDecelerationNum = rapidDecelerationNum;
        bean.sharpTurnNum = sharpTurnNum;
        bean.avgSpeed = 0.0;
        bean.avgFuelConsumer = 0.0;
        List<DrivedResponse.Bean> result = new ArrayList<>();
        result.add(bean);
        response.setResult(result);
        mockDrivedApi.setDriveScore(response);
    }

    // in: 急加速次数 急减速次数 急转弯次数 得分
    public void setDriveInfo(Long rapidAccelerateNum,
                             Long rapidDecelerationNum,
                             Long sharpTurnNum,
                             Double score){
        setDriveInfo(rapidAccelerateNum, rapidDecelerationNum, sharpTurnNum,score, 30.0);
    }
}
