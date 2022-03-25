package com.xiaoma.assistant.manager.api;

import com.xiaoma.center.logic.CenterConstants;

/**
 * @Author ZiXu Huang
 * @Data 2019/3/5
 */
public class VehicleConditionApiManager extends ApiManager {
    private static VehicleConditionApiManager instance;

    private VehicleConditionApiManager() {
    }

    public static VehicleConditionApiManager getInstance() {
        if (instance == null) {
            synchronized (VehicleConditionApiManager.class) {
                if (instance == null) {
                    instance = new VehicleConditionApiManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void initRemote() {
        super.initRemote(CenterConstants.VEHICLE_CONDITION, CenterConstants.VEHICLE_CONDITION_PORT);
    }

    public void totalMileageRequest() {
        /*Bundle bundle = new Bundle();
        request(CenterConstants.VehicleThirdAction.TOTAL_MILEAGE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                float totalMileageResult = response.getExtra().getFloat(CenterConstants.VehicleConditionThirdBundleKey.TOTAL_MILEAGE_RESULT);
                AssistantManager.getInstance().speakContent(context.getString(R.string.cur_total_mileage, new DecimalFormat("0").format(totalMileageResult)));
            }
        });*/
    }

    public void averageFuelConsumptionRequest() {
        /*Bundle bundle = new Bundle();
        request(CenterConstants.VehicleThirdAction.MEAN_FUEL_CONSUMPTION, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                float average_fuel_consumption = response.getExtra().getFloat(CenterConstants.VehicleConditionThirdBundleKey.MEAN_FUEL_CONSUMPTION_RESULT);
                AssistantManager.getInstance().speakContent(context.getString(R.string.cur_average_fuel_consumption, new DecimalFormat("0").format(average_fuel_consumption)));
            }
        });*/
    }

    public void remainingMileageRequest() {
       /* Bundle bundle = new Bundle();
        request(CenterConstants.VehicleThirdAction.REMAINING_MILEAGE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                float remainingMileage = response.getExtra().getFloat(CenterConstants.VehicleConditionThirdBundleKey.REMAINING_MILEAGE_RESULT);
                AssistantManager.getInstance().speakContent(context.getString(R.string.cur_remain_mileage, new DecimalFormat("0").format(remainingMileage)));
            }
        });*/
    }

    public void tirePressureRequest(){
        /*Bundle bundle = new Bundle();
        request(CenterConstants.VehicleThirdAction.TIRE_PRESSURE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                float tirePressure = response.getExtra().getFloat(CenterConstants.VehicleConditionThirdBundleKey.TIRE_PRESSURE_RESULT);
                AssistantManager.getInstance().speakContent(context.getString(R.string.cur_tire_pressure, new DecimalFormat("0").format(tirePressure)));
            }
        });*/
    }

    public void engineStateRequest(){

    }

    public void acStateRequest(){

    }

    public void waterTemperatureRequest(){}

    public void SKIDStateRequest(){}

    public void brakeStateRequest(){}

    public void overAllState(){}
}
