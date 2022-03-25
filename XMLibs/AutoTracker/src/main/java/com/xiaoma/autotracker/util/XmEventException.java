package com.xiaoma.autotracker.util;

import com.xiaoma.autotracker.exception.NotImplementsEventAnnotationException;
import com.xiaoma.utils.log.KLog;

/**
 * @author taojin
 * @date 2018/12/7
 */
public class XmEventException {

    protected static final String TAG = "com.xiaoma.autotracker";

    public static void doException(String errorMessage) {
        try {
            throw new NotImplementsEventAnnotationException(errorMessage);
        } catch (NotImplementsEventAnnotationException e) {
            KLog.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }
}
