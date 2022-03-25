package com.xiaoma.faultreminder.sdk;


import com.xiaoma.faultreminder.sdk.model.CarFault;

import java.util.List;

/**
 * @author KY
 * @date 12/26/2018
 */
public interface FaultListener {
    void onFaultOccur(List<CarFault> carFault);
}
