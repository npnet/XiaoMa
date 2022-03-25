package com.xiaoma.faultreminder.common;

/**
 * @author KY
 * @date 12/26/2018
 */
public class FaultReminderConstants {
    private FaultReminderConstants() throws Exception {
        throw new Exception();
    }

    public interface IntentKey {
        String FAULT_KEY = "Fault_Key";
    }
}
