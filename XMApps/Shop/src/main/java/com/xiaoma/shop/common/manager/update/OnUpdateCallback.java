package com.xiaoma.shop.common.manager.update;

import com.fsl.android.uniqueota.UniqueOtaConstants;
/**
 *  Author: Ljb
 *  Time  : 2019/6/30
 *  Description:
 */
public class OnUpdateCallback {
    /**
     * {@link UniqueOtaConstants.EcuId}
     */
   private  int ecu;

    public OnUpdateCallback(int ecu) {
        this.ecu = ecu;
    }

    public int getEcu() {
        return ecu;
    }

    public void setEcu(int ecu) {
        this.ecu = ecu;
    }

    public void onSuccess(UpdateOtaInfo info){

    }


    public void onFailure(UpdateOtaInfo  info ){

    }

    public void notifyDataSetChange(UpdateOtaInfo  info){

    }

}
