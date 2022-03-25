package com.xiaoma.assistant.manager.api;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.StringRes;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.Response;

/**
 * @Author ZiXu Huang
 * @Data 2019/3/5
 */
public class AirConditionerApiManager extends ApiManager {
    private static AirConditionerApiManager instance;

    private AirConditionerApiManager() {
    }

    public static AirConditionerApiManager getInstance() {
        if (instance == null) {
            synchronized (AirConditionerApiManager.class) {
                if (instance == null) {
                    instance = new AirConditionerApiManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void initRemote() {
        super.initRemote(CenterConstants.AIR_CONDITIONER, CenterConstants.AIR_CONDITIONER_PORT);
    }

    public void isTurnOnAirConditioner(final boolean isTurnOn) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(CenterConstants.AirConditionerThirdBundleKey.IS_TURN_ON_AC, isTurnOn);
        request(CenterConstants.AirConditionThirdAction.AIR_CONDITIONER_STATE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.AirConditionerThirdBundleKey.AC_TURN_ON_OR_OFF_RESULT);
                if (isSuccess) {
                    AssistantManager.getInstance().speakContent(context.getString(isTurnOn ? R.string.ac_open_success : R.string.ac_close_success));
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_AIR_CONDITIONER_STATE);
                } else {
                    AssistantManager.getInstance().speakContent(context.getString(isTurnOn ? R.string.ac_open_failed : R.string.ac_close_failed));
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_AIR_CONDITIONER_STATE);
                }
            }
        });
    }

    public void changeTemperature(final CenterConstants.ChangeAcTempOperation acOperation, final float specificTemperature) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CenterConstants.AirConditionerThirdBundleKey.AC_OPERATION, acOperation);
        bundle.putFloat(CenterConstants.AirConditionerThirdBundleKey.AC_SPECIFIC_TEMPERATURE, specificTemperature);

        request(CenterConstants.AirConditionThirdAction.AIR_CONDITION_OPERATION, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.AirConditionerThirdBundleKey.AC_TEMPERATURE_CHANGE_RESULT);
                String speakText = "";
                if (acOperation.equals(CenterConstants.ChangeAcTempOperation.MAX)) {
                    speakText = isSuccess ? context.getString(R.string.temperature_max_success) : context.getString(R.string.temperature_max_failed);
                } else if (acOperation.equals(CenterConstants.ChangeAcTempOperation.MIN)) {
                    speakText = isSuccess ? context.getString(R.string.temperature_min_success) : context.getString(R.string.temperature_min_failed);
                } else if (acOperation.equals(CenterConstants.ChangeAcTempOperation.SPECIFIC_NUM)) {
                    speakText = isSuccess ? String.format(context.getString(R.string.change_temperature_to_specific_num), specificTemperature) : String.format(context.getString(R.string.change_to_specific_num_failed), specificTemperature);
                } else {
                    // TODO 此处speakText适合在操作端返回,因为不同的温度区间同样的操作会有不同的TTS
                    speakText = response.getExtra().getString(CenterConstants.AirConditionerThirdBundleKey.AC_TEMPERATURE_CHANGE_RESULT_TEXT);
                }
                AssistantManager.getInstance().speakContent(speakText);
            }
        });
    }

    public void changeAcWindDirectionModel(final CenterConstants.ChangeAcWindDirectionModelOperation model) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CenterConstants.AirConditionerThirdBundleKey.AC_WIND_DIRECTION_MODEL_CHANGE, model);
        request(CenterConstants.AirConditionThirdAction.AIR_CONDITIONER_WIND_DIRECTION_MODEL_CHANGE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.AirConditionerThirdBundleKey.AC_WIND_DIRECTION_MODEL_CHANGE_RESULT);
                String speakText = isSuccess ? getString(R.string.change_wind_direction_success, model.getOperation()) : getString(R.string.change_wind_direction_failed, model.getOperation());
                AssistantManager.getInstance().speakContent(speakText);
            }
        });
    }

    public void changeAcModel(final CenterConstants.ChangeAcModelOperation model, boolean isOpen) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CenterConstants.AirConditionerThirdBundleKey.AC_MODEL_CHANGE, model);
        bundle.putBoolean(CenterConstants.AirConditionerThirdBundleKey.AC_MODEL_IS_OPEN, isOpen);
        request(CenterConstants.AirConditionThirdAction.AIR_CONDITIONER_MODEL_CHANGE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.AirConditionerThirdBundleKey.AC_MODEL_CHANGE_RESULT);
                if (isSuccess) {
                    AssistantManager.getInstance().speakContent(String.format(context.getString(R.string.ac_model_change_success), model.getOperation()));
                } else {
                    AssistantManager.getInstance().speakContent(String.format(context.getString(R.string.ac_model_change_failed), model.getOperation()));
                }
            }
        });
    }

    public void changeDefogModel(final CenterConstants.ChangeDefogModel model, final boolean isOpen) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CenterConstants.AirConditionerThirdBundleKey.AC_DEFOG_MODEL_CHANGE, model);
        bundle.putBoolean(CenterConstants.AirConditionerThirdBundleKey.AC_DEFOG_MODEL_IS_OPEN, isOpen);
        request(CenterConstants.AirConditionThirdAction.AIR_CONDITIONER_DEFOG_MODEL_CHANGE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.AirConditionerThirdBundleKey.AC_DEFOG_MODEL_CHANGE_RESULT);
                if (isSuccess) {
                    if (isOpen) {
                        AssistantManager.getInstance().speakContent(String.format(context.getString(R.string.open_defog_model_success), model.getOperation()));
                    } else {
                        AssistantManager.getInstance().speakContent(String.format(context.getString(R.string.close_defog_model_success), model.getOperation()));
                    }
                } else {
                    if (isOpen) {
                        AssistantManager.getInstance().speakContent(String.format(context.getString(R.string.open_defog_model_failed), model.getOperation()));
                    } else {
                        AssistantManager.getInstance().speakContent(String.format(context.getString(R.string.close_defog_model_failed), model.getOperation()));
                    }
                }
            }
        });
    }

    public void changeAcWindSpeedModel(final CenterConstants.ChangeAcWindSpeed model, final float speedValue) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CenterConstants.AirConditionerThirdBundleKey.AC_WIND_SPEED_CHANGE, model);
        bundle.putFloat(CenterConstants.AirConditionerThirdBundleKey.AC_WIND_SPEED_VALUE, speedValue);
        request(CenterConstants.AirConditionThirdAction.AIR_CONDITIONER_WIND_SPEED_CHAGE, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.AirConditionerThirdBundleKey.AC_WIND_SPEED_CHANGE_RESULT);
                String speakText = "";
                if (model.equals(CenterConstants.ChangeAcWindSpeed.MAX_SPEED)) {
                    speakText = isSuccess ? getString(R.string.wind_speed_max_success) : getString(R.string.wind_speed_max_failed);
                } else if (model.equals(CenterConstants.ChangeAcWindSpeed.MIN_SPEED)) {
                    speakText = isSuccess ? getString(R.string.wind_speed_min_success) : getString(R.string.wind_speed_min_failed);
                } else if (model.equals(CenterConstants.ChangeAcWindSpeed.SPECIFIC_SPEED)) {
                    speakText = isSuccess ? getString(R.string.wind_speed_specific_num_success, speedValue) : getString(R.string.wind_speed_specific_num_failed, speedValue);
                } else {
                    speakText = response.getExtra().getString(CenterConstants.AirConditionerThirdBundleKey.AC_WIND_SPEED_CHANGE_RESULT_TEXT);
                }
                AssistantManager.getInstance().speakContent(speakText);
            }
        });
    }

    private String getString(@StringRes int stringId) {
        return context.getString(stringId);
    }

    private String getString(@StringRes int stringId, float insertString) {
        return String.format(getString(stringId), insertString);
    }

    private String getString(@StringRes int stringId, String text) {
        return String.format(getString(stringId, text));
    }
}
