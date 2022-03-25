package com.xiaoma.shop.common.track;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.model.TrackerEventType;
import com.xiaoma.shop.common.constant.ResourceType;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/4
 */
public class ShopTrackManager {

    private @ResourceType
    int mResourceType;
    private String mPagePath;
    private String mAppendInfo;

    private ShopTrackManager() {
    }

    public static ShopTrackManager newSingleton() {
        return Holder.sINSTANCE;
    }

    public void setBaseInfo(@ResourceType int resourceType, String appendInfo, String pagePath) {
        mResourceType = resourceType;
        mPagePath = pagePath;
        mAppendInfo = appendInfo;
    }

    public void manualUpdateHint(String eventAction) {
        try {
            XmAutoTracker.getInstance().onEvent(eventAction, mAppendInfo, mPagePath, getBuyHint());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void manualUpdateEvent(TrackerEventType type, String eventAction) {
        try {
            if (type == TrackerEventType.EXPOSE) {
                XmAutoTracker.getInstance().onEventDirectUpload(type, eventAction, mAppendInfo, mPagePath, getBuyPage());
            } else {
                XmAutoTracker.getInstance().onEvent(eventAction, mAppendInfo, mPagePath, getBuyPage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getBuyPage() {
        String buyPage = "";
        if (mResourceType == ResourceType.SKIN) {
            buyPage = EventConstant.PageDesc.PERSONAL_SYSTEM_SKIN_BUY;
        } else if (mResourceType == ResourceType.ASSISTANT) {
            buyPage = EventConstant.PageDesc.VOICE_BUY;
        } else if (mResourceType == ResourceType.HOLOGRAM) {
            buyPage = EventConstant.PageDesc.HOLOGRAM_BUY;
        } else if (mResourceType == ResourceType.VEHICLE_SOUND) {
            buyPage = EventConstant.PageDesc.VOICE_EFFECT_BUY;
        } else if (mResourceType == ResourceType.FLOW) {
            buyPage = EventConstant.PageDesc.FRAGMENT_GROW_STORE;
        } else if (mResourceType == ResourceType.INSTRUMENT_SOUND) {
//            buyPage=EventConstant.PageDesc.VOICE_BUY;
        }
        return buyPage;
    }

    private String getBuyHint() {
        String buyHint = "";
        if (mResourceType == ResourceType.SKIN) {
            buyHint = EventConstant.PageDesc.PERSONAL_SYSTEM_SKIN_COIN_BUY_TIP;
        } else if (mResourceType == ResourceType.ASSISTANT) {
            buyHint = EventConstant.PageDesc.VOICE_COIN_BUY_TIP;
        } else if (mResourceType == ResourceType.HOLOGRAM) {
            buyHint = EventConstant.PageDesc.HOLOGRAM_BUY_COIN_BUY_TIP;
        } else if (mResourceType == ResourceType.VEHICLE_SOUND) {
            buyHint = EventConstant.PageDesc.VOICE_EFFECT_BUY_COIN_BUY_TIP;
        } else if (mResourceType == ResourceType.FLOW) {
            buyHint = EventConstant.PageDesc.FLOW_SHOP_COIN_BUY_TIP;
        } else if (mResourceType == ResourceType.INSTRUMENT_SOUND) {
//            buyHint=EventConstant.PageDesc.VOICE_BUY;
        }
        return buyHint;
    }

    interface Holder {
        ShopTrackManager sINSTANCE = new ShopTrackManager();
    }
}
