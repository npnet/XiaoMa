package com.xiaoma.shop.common;

import android.accounts.NetworkErrorException;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.xiaoma.component.AppHolder;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.*;
import com.xiaoma.shop.business.model.personalTheme.PagedHologramBean;
import com.xiaoma.shop.business.model.personalTheme.PagedSkinsBean;
import com.xiaoma.shop.business.model.personalTheme.PagedVoicesBean;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.ThemeContract;
import com.xiaoma.shop.common.constant.VehicleSoundType;
import com.xiaoma.utils.GsonHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/11/20
 * </pre>
 */
public class RequestManager {

    private static final String baseUrl = ConfigManager.EnvConfig.getEnv().getBusiness();
    private static final String ACTION_SKINS = baseUrl + "skin/getAllSkinPage.action";
    private static final String ACTION_SKINS_BY_PRICE = baseUrl + "skin/getAllSkinByPricePage.action";
    private static final String ACTION_VOICE_TONE = baseUrl + "sku/getAllSkuByCategoryPage.action";
    private static final String ACTION_ADD_SKIN_USER = baseUrl + "skin/addSkinUsedNum.action";
    private static final String ACTION_ADD_SKU_USER = baseUrl + "sku/addSkinUsedNum.action";
    private static final String ACTION_HOLOGRAMS = baseUrl + "holo/getAllHolo";
    private static final String ORDER_LOOP = baseUrl + "orders/getBesturnStoreOrderStatus";
    private static final String GET_ORDER_STATUS = baseUrl + "unicomBusiness/getOrderStatus";
    private static final String BUY_RESOURCE_CASH_QR_CODE = baseUrl + "orders/addBesturnStoreCashOrder";
    private static final String GET_PAY_URL = baseUrl + "unicomBusiness/getPayUrl";
    private static final String GET_ORDER_INFO_BY_ORDERNO = baseUrl + "orders/getOrderInfoByOrderNo";
    private static final String BUY_RESOURCE_CAR_COIN_ORDER = baseUrl + "orders/addBesturnStoreScoreOrder";
    private static final String BUY_RESOURCE_CAR_COIN_PAY = baseUrl + "user/score/scoreBuyBesturnStore.action";
    private static final String MINE_BOUGHT_LIST = baseUrl + "getUserBuyProduct";
    private static final String carCoinUrl = baseUrl + "user/score/status.action";
    private static final String LEFT_INFO_ACTION = baseUrl + "simcard/leftInfo.action";
    private static final String GET_SCORE_PRODUCT_LIST = baseUrl + "socreProduct/getScoreProductList";
    private static final String GET_COMMODITY = baseUrl + "unicomBusiness/getCommodity";
    private static final String ACTION_ADD_HOLOGRAM_USER = baseUrl + "holo/addHoloUsedNum";
    private static final String ACTION_HOLO_MAN_INFO = baseUrl + "holo/getAllSkillAndCloseByHoloId";
    private static final String ACTION_GET_GOWILD_HOLO = baseUrl + "holo/getGowildHolo";
    private static final String ACTION_GET_SOUND_EFFECT = baseUrl + "soundEffect/getSoundEffect";
    private static final String ACTION_SOUNDEFFECT_ADD_USED_NUM = baseUrl + "soundEffect/addUsedNum";
    private static final String ACTION_GET_SOUND_EFFECT_BY_PRICE_PAGE = baseUrl + "soundEffect/getSoundEffectByPricePage";
    private static final String ACTION_ADD_SCORE_ORDERS = baseUrl + "soundEffect/addScoreOrders";
    private static final String ACTION_SCORE_BUY_SOUND_EFFECT = baseUrl + "soundEffect/scoreBuySoundEffect";
    private static final String ACTION_GET_INSTRUMENT = baseUrl + "instrument/getInstrument";
    private static final String ACTION_INSTRUMENT_ADD_USED_NUM = baseUrl + "instrument/addUsedNum";
    private static final String ACTION_ADD_SKIN_TO_BUY_LIST = baseUrl + "userFreeSub/addSkinOrSkuFreeSubInfo";

    private static final String SKIN_TRIAL_REPORT = baseUrl + "skin/addSkinProbation.action";
    private static final String SKIN_CHECK_CAN_TRIAL = baseUrl + "skin/getSkinProbation.action";


    private static <Bean> void request(final String url, Map params, final
    ResultCallback<XMResult<Bean>> callback) {
        if (callback == null) {
            return;
        }
        XmHttp.getDefault().getString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0])
                        .getActualTypeArguments()[0];
                String data = response.body();
                XMResult<Bean> result = GsonHelper.fromJson(data, type);
                //??????????????????,????????????????????????resultCode = 1030 (????????????) ???toast????????????result parse failure
                if (result == null) {
                    callback.onFailure(-1, "result parse failure");
                    return;
                } else if (!result.isSuccess()) {
                    callback.onFailure(result.getResultCode(), result.getResultMessage());
                    return;
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                String errorMsg = response.message();
                if (response.getException() instanceof ConnectException //
                        || response.getException() instanceof TimeoutException //
                        || response.getException() instanceof NetworkErrorException //
                        || response.getException() instanceof SocketTimeoutException //
                        || response.getException() instanceof UnknownHostException) {
                    errorMsg = AppHolder.getInstance().getAppContext().getResources().getString(R.string.network_error);
                }
                callback.onFailure(response.code(), errorMsg);
            }
        });
    }

    /**
     * ????????????
     *
     * @param callback
     */
    public static void getFlowMargin(String vin,
                                     ResultCallback<XMResult<FlowMarginBean>> callback) {
        Map<String, Object> formData = new HashMap<>();
        formData.put("vin", vin);// Type: String; ?????????
        RequestManager.request(LEFT_INFO_ACTION, formData, callback);
    }

    public static void getTrafficMall(ResultCallback<XMResult<ScoreProductBean>> callback) {
        Map<String, Object> formData = new HashMap<>();
        formData.put("roleId", "");// ;Type: Long; ?????????
        formData.put("holoVersion", "");// Type: String; ?????????
        RequestManager.request(GET_SCORE_PRODUCT_LIST, formData, callback);
    }

    static void requestSkins(@ThemeContract.SortRule String sortRule, int page, int type, ResultCallback<XMResult<PagedSkinsBean>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("sort", sortRule);
        params.put("size", 20);
        if (type == 0 || type == 1) {
            params.put("type", type);
            request(ACTION_SKINS_BY_PRICE, params, callback);
        } else {
            request(ACTION_SKINS, params, callback);
        }
    }


    static void requestVoiceTones(@ThemeContract.SortRule String sortRule, int page, ResultCallback<XMResult<PagedVoicesBean>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("sort", sortRule);
        params.put("size", 20);
        request(ACTION_VOICE_TONE, params, callback);
    }

    static void requestHologram(@ThemeContract.SortRule String sortRule, int page, int type, ResultCallback<XMResult<PagedHologramBean>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("sort", sortRule);
        params.put("size", 20);
        request(ACTION_HOLOGRAMS, params, callback);
    }

    public static void requestHoloManInfo(long holoId, ResultCallback<XMResult<HoloManInfo>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("holoId", holoId);
        request(ACTION_HOLO_MAN_INFO, params, callback);
    }

    /**
     * ??????????????????--??????/??????
     *
     * @param sortRule
     * @param page
     * @param callback
     */
    public static void requestVehicleSound(@VehicleSoundType.ProductType String productType,
                                           @ThemeContract.SortRule String sortRule,
                                           int page,
                                           int pageCount,
                                           int type,
                                           int deploy,
                                           ResultCallback<XMResult<VehicleSoundEntity>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("sort", sortRule);
        params.put("size", pageCount);
        String url = "";
        if (VehicleSoundType.ProductType.INSTRUMENT_SOUND.equals(productType)) {//??????
            url = ACTION_GET_INSTRUMENT;
            params.put("type", deploy); //?????? 0???????????? 1????????????
        } else {//??????
            url = ACTION_GET_SOUND_EFFECT;
        }
        request(url, params, callback);
    }

    public static void addUseNum(@ResourceType int type, long resId, ResultCallback<XMResult<String>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        switch (type) {
            case ResourceType.SKIN:
                params.put("skinVersionId", resId);
                request(ACTION_ADD_SKIN_USER, params, callback);
                break;
            case ResourceType.ASSISTANT:
                params.put("skuId", resId);
                request(ACTION_ADD_SKU_USER, params, callback);
                break;
            case ResourceType.HOLOGRAM:
                params.put("holoId", resId);
                request(ACTION_ADD_HOLOGRAM_USER, params, callback);
                break;
            case ResourceType.VEHICLE_SOUND:
                params.put("soundEffectId", resId);
                request(ACTION_SOUNDEFFECT_ADD_USED_NUM, params, callback);
                break;
            case ResourceType.INSTRUMENT_SOUND:
                params.put("instrumentId", resId);
                request(ACTION_INSTRUMENT_ADD_USED_NUM, params, callback);
                break;
            default:
                Log.d("Jir", "addUseNum: undefined type " + type);
                break;
        }
    }


    /**
     * ????????????
     *
     * @param type      ????????????
     * @param productId ??????id
     */
    public static void resourceOrderWithScore(@ResourceType int type, long productId, ResultCallback<XMResult<BuyBean>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("productId", productId);
        request(BUY_RESOURCE_CAR_COIN_ORDER, params, callback);
    }


    /**
     * ????????????
     *
     * @param orderNo ?????????
     */
    public static void payResourceWithScore(String orderNo, ResultCallback<XMResult<BuyBean>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderNo", orderNo);
        request(BUY_RESOURCE_CAR_COIN_PAY, params, callback);
    }


    /**
     * ????????????
     *
     * @param type      ????????????
     * @param productId ??????id
     */
    public static void payResourceWithQRCode(@ResourceType int type, long productId, ResultCallback<XMResult<QrCodeBean>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("productId", productId);
        request(BUY_RESOURCE_CASH_QR_CODE, params, callback);
    }

    public static void payResourceWithQRCode(FetchQrCodeBean fetchQrCodeBean, ResultCallback<XMResult<QrCodeBean>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("data", GsonHelper.toJson(fetchQrCodeBean));
        request(GET_PAY_URL, params, callback);
    }

    public static void payResourceWithQRCode(String orderNum, ResultCallback<XMResult<QrCodeBean>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderNo", orderNum);
        request(GET_ORDER_INFO_BY_ORDERNO, params, callback);
    }

    /**
     * ????????????
     *
     * @param type ????????????
     * @param uid  ??????id
     * @param page ????????????????????????
     * @param size ??????????????????
     */
    public static void boughtList(@ResourceType int type, long uid, int page, int size, ResultCallback<XMResult<MineBought>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("uid", uid);
        params.put("page", page);
        params.put("size", size);
        request(MINE_BOUGHT_LIST, params, callback);
    }


    /**
     * 3
     * ??????????????????
     */
    public static void getUserCarCoin(ResultCallback<XMResult<CoinAndSignInfo>> callback) {
        HashMap<String, Object> params = new HashMap<>(1);
        params.put("userId", LoginManager.getInstance().getLoginUserId());
        request(carCoinUrl, params, callback);
    }


    /**
     * ??????????????????
     *
     * @param orderNo ?????????
     */
    public static void queryOrderInfo(String orderNo, ResultCallback<XMResult<QrCodeBean>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderNo", orderNo);
        request(ORDER_LOOP, params, callback);
    }

    /**
     * ??????????????????
     *
     * @param orderNo ?????????
     */
    public static void queryOrderInfoNew(String orderNo, ResultCallback<XMResult<QrCodeBean>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderId", orderNo);
        request(GET_ORDER_STATUS, params, callback);
    }


    public static void getActionGetGowildHolo(ResultCallback<XMResult<GowildHolo>> callback) {
        request(ACTION_GET_GOWILD_HOLO, null, callback);
    }

    /**
     * ??????????????????????????????????????????????????????????????????
     *
     * @param skuId ??????id
     * @param type  ??????: {@link ResourceType}
     */
    public static void addSkuToBuyList(long skuId, @ResourceType int type, ResultCallback<XMResult<Object>> callback) {
        Map<String, Object> params = new ArrayMap<>();
        params.put("skinVersionIdOrSkuId", skuId);
        params.put("type", type);
        request(ACTION_ADD_SKIN_TO_BUY_LIST, params, callback);
    }

    /**
     * ????????????????????????
     */
    public static void reportSkinTrial(int skinId, ResultCallback<XMResult<Object>> callback) {
        Map<String, Object> params = new ArrayMap<>();
        params.put("skinId", skinId);
        request(SKIN_TRIAL_REPORT, params, callback);
    }

    /**
     * ???????????????????????????
     */
    public static void checkSkinCanTrial(int skinId, ResultCallback<XMResult<SkinTrialResult>> callback) {
        Map<String, Object> params = new ArrayMap<>();
        params.put("skinId", skinId);
        request(SKIN_CHECK_CAN_TRIAL, params, callback);
    }

    /**
     * ?????????????????????????????????
     */
    public static void fetchTrafficMallFromUnicom(ResultCallback<XMResult<List<FlowItemForCash>>> callback) {
        Map<String, Object> formData = new HashMap<>();
        RequestManager.request(GET_COMMODITY, formData, callback);
    }
}
