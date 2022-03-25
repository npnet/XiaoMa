package com.xiaoma.assistant.callback;

import android.util.Log;

import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.thread.ThreadDispatcher;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/8/19 0019 16:57
 *   desc:
 * </pre>
 */
public class CarEventWithAssistantCallback implements ICarEvent {


    private CarEventWithAssistantCallback() {

    }

    private static class Holder {
        private static final CarEventWithAssistantCallback CAR_EVENT_CALLBACK = new CarEventWithAssistantCallback();
    }


    public static CarEventWithAssistantCallback getInstance() {
        return Holder.CAR_EVENT_CALLBACK;
    }


    @Override
    public void onCarEvent(CarEvent event) {
        if (event == null) {
            Log.d(CarEventWithAssistantCallback.class.getSimpleName(),
                    "CarEvent instance is null.");
            return;
        }

        if (event.id == SDKConstants.ID_CAMERA_STATUS) {
            ThreadDispatcher.getDispatcher().postOnMain(() -> AssistantManager.getInstance().closeAssistant());
        }
    }
}
