package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.api.VehicleConditionApiManager;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.utils.AssistantUtils;
import com.xiaoma.assistant.utils.Constants;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.utils.ConvertUtils;
import com.xiaoma.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: iSun
 * @date: 2019/2/14 0014
 * 车辆状态
 */
class IatVehicleScenario extends IatScenario {
    private static final String TAG = IatVehicleScenario.class.getSimpleName();

    public IatVehicleScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        String key = null;
        try {
            JSONObject jsonObject = new JSONObject(parseResult.getSlots());
            if (jsonObject.has("name")) {
                key = jsonObject.getString("name");
            }
            checkVehicle(key);
        } catch (JSONException e) {
            e.printStackTrace();
            speakContent(context.getString(R.string.query_vehicle_error));
        }
    }

    private void checkVehicle(String key) {
        String speakText = null;
        if (TextUtils.isEmpty(key)) {
            closeVoicePopup();
            return;
        }
        switch (key) {
            case Constants.ParseKey.OIL_QUANTITY:
                VehicleConditionApiManager.getInstance().remainingMileageRequest();
                break;
            case Constants.ParseKey.TIRE_PRESSURE:
                VehicleConditionApiManager.getInstance().tirePressureRequest();
                break;
            case Constants.ParseKey.ENGINE:
                VehicleConditionApiManager.getInstance().engineStateRequest();
                break;
            case Constants.ParseKey.AIR_CONDITIONING:
                VehicleConditionApiManager.getInstance().acStateRequest();
                break;
            case Constants.ParseKey.WATER_TEMPERATURE:
                VehicleConditionApiManager.getInstance().waterTemperatureRequest();
                break;
            case Constants.ParseKey.SKID:
                VehicleConditionApiManager.getInstance().SKIDStateRequest();
                break;
            case Constants.ParseKey.BRAKE:
                VehicleConditionApiManager.getInstance().brakeStateRequest();
                break;
            case Constants.ParseKey.OVERALL:
                VehicleConditionApiManager.getInstance().overAllState();
                break;
            case Constants.ParseKey.FUEL_CONSUMPTION:
//                VehicleConditionApiManager.getInstance().averageFuelConsumptionRequest();
                setRobAction(AssistantConstants.RobActionKey.DRIVING_INFORMATION);
                int fuelConsumption = (int) (XmCarVendorExtensionManager.getInstance().getFuelConsumption() * 0.1);
                speakText = StringUtil.format(context.getString(R.string.fuel_consumption), fuelConsumption);
                break;
            case Constants.ParseKey.MILEAGE:
//                VehicleConditionApiManager.getInstance().totalMileageRequest();
                setRobAction(AssistantConstants.RobActionKey.DRIVING_INFORMATION);
                int odometerData = (int) (XmCarFactory.getCarVendorExtensionManager().getOdometer() * 0.1);
                String value = ConvertUtils.intToString(odometerData);
                speakText = StringUtil.format(context.getString(R.string.total_odometer), value);
                break;
            case Constants.ParseKey.REMAINING_MILEAGE:
                setRobAction(AssistantConstants.RobActionKey.DRIVING_INFORMATION);
                int odometerResidual = (int) (XmCarVendorExtensionManager.getInstance().getOdometerResidual()*0.1);
                String value2 = ConvertUtils.intToString(odometerResidual);
                speakText = StringUtil.format(context.getString(R.string.odometer_residual), value2);
                break;
            default:
        }
        if (TextUtils.isEmpty(speakText)) {
           closeVoicePopup();
        } else {
            closeAfterSpeak(speakText);
        }
    }

    @Override
    public void onChoose(String voiceText) {

    }

    @Override
    public boolean isIntercept() {
        return false;
    }

    @Override
    public void onEnd() {

    }
}
