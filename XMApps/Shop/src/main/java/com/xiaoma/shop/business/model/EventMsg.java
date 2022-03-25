package com.xiaoma.shop.business.model;

/**
 * Author      :ljb
 * Date        :2019/8/11
 * Description :
 */
public class EventMsg {
    private  @ACTION  String  action;

    public static final String ACTION_KEY="action_key";
    public EventMsg(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public @interface ACTION{
        String ACTION_SKIN="action_skin";
        String ACTION_VOICE="action_voice";
        String ACTION_INSTRUMENT="action_instrument";
        String ACTION_VEHICLE="action_vehicle";
        String ACTION_COIN="action_coin";
        String ACTION_CASH="action_cash";

    }
}
