package com.xiaoma.personal.common;

import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.PageWrapper;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.personal.account.model.AvatarQRCode;
import com.xiaoma.personal.account.model.CurrentDate;
import com.xiaoma.personal.coin.model.CoinAndSignInfo;
import com.xiaoma.personal.coin.model.CoinRecord;
import com.xiaoma.personal.common.util.Utils;
import com.xiaoma.personal.feedback.model.MessageInfo;
import com.xiaoma.personal.newguide.model.AppInfo;
import com.xiaoma.personal.order.model.HotelPolicy;
import com.xiaoma.personal.order.model.OrderInfo;
import com.xiaoma.personal.qrcode.model.BindStatusBean;
import com.xiaoma.personal.qrcode.model.HologramQRCode;
import com.xiaoma.personal.qrcode.model.KeyQRCode;
import com.xiaoma.personal.taskcenter.constract.TaskType;
import com.xiaoma.personal.taskcenter.model.OperateTask;
import com.xiaoma.personal.taskcenter.model.TaskNote;
import com.xiaoma.utils.GsonHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/11/20
 * </pre>
 */
public class RequestManager {
    private static final String baseUrl = ConfigManager.EnvConfig.getEnv().getBusiness();
    private static final String PUBLISH_FEEDBACK_QUESTION_URL = baseUrl + "saveQuestionFeedback";
    private static final String FEEDBACK_MESSAGE_LIST_URL = baseUrl + "getFeedbackComments";
    private static final String FEEDBACK_SATISFACTION_URL = baseUrl + "saveSatisfaction";
    private static final String READ_MESSAGE_CHANGE_STATUS_URL = baseUrl + "changeCommentsStatus";
    private static final String COIN_RECORD_LIST_URL = baseUrl + "user/score/recordsV2.action";

    private static final String BIND_KEY_URL = baseUrl + "user/bindKey";
    private static final String UNBIND_KEY_URL = baseUrl + "user/unBindKey";
    private static final String DELETE_CHILD_ACCOUNT = baseUrl + "user/deleteSubUser";
    private static final String MODIFY_PASSWORD_URL = baseUrl + "user/checkOrEditPassword";
    private static final String MODIFY_AVATAR_URL = baseUrl + "user/getUpdateUserHeaderPicQrCode";

    private static final String PULL_SERVER_NEW_DATE_URL = baseUrl + "getNowDate";

    private static final String QUERY_MINE_ORDER = baseUrl + "orders/search";
    private static final String CANCEL_MINE_ORDER = baseUrl + "orders/cancel";
    private static final String DELETE_MINE_ORDER = baseUrl + "orders/delete";
    private static final String MINE_ORDER_DETAIL = baseUrl + "orders/findOrderById";
    private static final String ORDER_HOTEL_POLICY = baseUrl + "hotel/getPolicyByHotelId.action";
    private static final String GET_USER_BY_ID = baseUrl + "user/getUserById.action";

    private static final String NUMBER_KEY_URL = baseUrl + "qrCode/getNumberKeyQrCode";
    private static final String REMOTE_CONTROLLER_URL = baseUrl + "qrCode/getRemoteControlQrCode";
    private static final String GET_HOLOGRAM_URL = baseUrl + "qrCode/getHoLoAppQrCode";
    private static final String HOLOGRAM_BIND_STATUS_URL = baseUrl + "qrCode/getHoLoBindStatus";
    private static final String UNBIND_HOLOGRAM_URL = baseUrl + "qrCode/unbindHoLo";
    private static final String GET_GUIDE_APP_LIST = baseUrl + "appConfigGuide/getAppConfigGuideList.action";

    private static final String userEditUrl = baseUrl + "user/edit.action";
    private static final String carCoinUrl = baseUrl + "user/score/status.action";
    private static final String taskListUrl = baseUrl + "user/score/tasksV2.action";
    private static final String taskNoteUrl = baseUrl + "user/score/recordsV2.action";
    private static final String signInUrl = baseUrl + "user/score/signIn.action";
    private static final int COIN_RECORD_PAGE_SIZE = 10;

    private static <Bean> void request(String url, Map params, final ResultCallback<XMResult<Bean>> callback) {
        if (callback == null) {
            return;
        }
        XmHttp.getDefault().getString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
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
                callback.onFailure(response.code(), response.message());
            }
        });
    }


    /**
     * ??????????????????
     */
    public static void getUserById(long id, ResultCallback<XMResult<User>> loginCallback) {
        request(GET_USER_BY_ID, Collections.<String, Object>singletonMap("id", id), loginCallback);
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    public static void pullServerNewDate(ResultCallback<XMResult<CurrentDate>> callback) {
        request(PULL_SERVER_NEW_DATE_URL, null, callback);
    }


    public static void changeUserName(String name, ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>(2);
        params.put("id", LoginManager.getInstance().getLoginUserId());
        params.put("name", name);
        request(userEditUrl, params, callback);
    }

    public static void changeUserGender(int gender, ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>(2);
        params.put("id", LoginManager.getInstance().getLoginUserId());
        params.put("gender", String.valueOf(gender));
        request(userEditUrl, params, callback);
    }

    public static void changeUserAge(Long birthDayLong, ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>(3);
        params.put("id", LoginManager.getInstance().getLoginUserId());
        params.put("birthDayLong", String.valueOf(birthDayLong));
        params.put("birthDay", Utils.getDateFromStamp(birthDayLong));
        request(userEditUrl, params, callback);
    }

    public static void changeUserCarPlateNumber(String plateNumber, ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>(2);
        params.put("id", LoginManager.getInstance().getLoginUserId());
        params.put("plateNumber", plateNumber);
        request(userEditUrl, params, callback);
    }

    public static void getUserCarCoin(ResultCallback<XMResult<CoinAndSignInfo>> callback) {
        HashMap<String, Object> params = new HashMap<>(1);
        params.put("userId", LoginManager.getInstance().getLoginUserId());
        request(carCoinUrl, params, callback);
    }

    public static void getCoinRecordList(ResultCallback<XMResult<PageWrapper<CoinRecord>>> callback, int page) {
        HashMap<String, Object> params = new HashMap<>(3);
        params.put("userId", LoginManager.getInstance().getLoginUserId());
        params.put("page", String.valueOf((page)));
        params.put("size", String.valueOf((COIN_RECORD_PAGE_SIZE)));
        request(COIN_RECORD_LIST_URL, params, callback);
    }

    public static void publishFeedbackQuestion(String channelId, String question, String type, ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>(4);
        params.put("channelId", channelId);
        params.put("uid", LoginManager.getInstance().getLoginUserId());
        params.put("question", question);
        params.put("type", type);
        request(PUBLISH_FEEDBACK_QUESTION_URL, params, callback);
    }

    public static void getFeedbackMessageRecord(int pageNum, int pageSize, ResultCallback<XMResult<MessageInfo>> callback) {
        HashMap<String, Object> params = new HashMap<>(3);
        params.put("uid", LoginManager.getInstance().getLoginUserId());
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        request(FEEDBACK_MESSAGE_LIST_URL, params, callback);
    }

    /**
     * ??????????????????????????????
     *
     * @param satisfied 0????????????   1?????????
     * @param advice    ??????
     */
    public static void postCommentSatisfactionEvaluation(int messageId, int satisfied, String advice, ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>(3);
        params.put("id", messageId);
        params.put("isSatisfied", satisfied);
        params.put("advice", advice);
        request(FEEDBACK_SATISFACTION_URL, params, callback);
    }

    /**
     * ????????????????????????????????????
     *
     * @param commentsId ??????Id
     */
    public static void readMessageChangeStatus(long commentsId, ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>(1);
        params.put("commentsId", commentsId);
        request(READ_MESSAGE_CHANGE_STATUS_URL, params, callback);
    }

    /**
     * ??????
     *
     * @param callback
     */
    public static void signInToday(ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>(1);
        params.put("userId", LoginManager.getInstance().getLoginUserId());
        params.put("uid", Long.parseLong(LoginManager.getInstance().getLoginUserId()));
        request(signInUrl, params, callback);
    }

    /**
     * @param callback
     */
    public static void fetchTaskByType(@TaskType int catalog, int page, ResultCallback<XMResult<PageWrapper<OperateTask>>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("catalog", catalog);
        params.put("page", String.valueOf(page));
        params.put("deviceType", XmCarConfigManager.hasFaceRecognition() ? 0 : 1);
        request(taskListUrl, params, callback);
    }

    /**
     * ??????????????????
     */
    public static void fetchTaskNote(ResultCallback<XMResult<PageWrapper<TaskNote>>> callback, int page) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", String.valueOf((page)));
        params.put("type", "in");
        request(taskNoteUrl, params, callback);
    }

    /**
     * ?????????????????????
     *
     * @param uid      uid
     * @param carKey   carKey
     * @param passwd   passwd
     * @param type     ??????type
     * @param callback callback
     */
    public static void bindKey(String uid, String carKey, String passwd, int type,
                               final ResultCallback<XMResult<String>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("key", carKey);
        params.put("type", type);
        params.put("password", passwd);
        request(BIND_KEY_URL, params, callback);
    }


    /**
     * ????????????
     *
     * @param userId   ??????Id
     * @param keyType  ????????????
     * @param password ????????????
     */
    public static void unbindKey(long userId,
                                 int keyType,
                                 String password,
                                 ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", userId);
        params.put("keyType", keyType);
        params.put("password", password);
        request(UNBIND_KEY_URL, params, callback);
    }


    /**
     * ???????????????
     *
     * @param userId ??????Id
     */
    public static void deleteChildAccount(long userId, ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", userId);
        request(DELETE_CHILD_ACCOUNT, params, callback);
    }


    /**
     * ?????????????????????
     *
     * @param userId   ??????Id
     * @param password ????????????
     * @param type     ???????????? {@link com.xiaoma.personal.common.util.PasswordOperationType (VERIFY???MODIFY)}
     */
    public static void verifyOrModifyUserPassword(long userId,
                                                  String password,
                                                  int type,
                                                  ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", userId);
        params.put("password", password);
        params.put("type", type);
        request(MODIFY_PASSWORD_URL, params, callback);
    }

    public static void fetchChangeQR(String userId, ResultCallback<XMResult<AvatarQRCode>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", userId);
        request(MODIFY_AVATAR_URL, params, callback);
    }


    /**
     * ????????????????????????
     *
     * @param userId    ??????Id
     * @param orderType ???????????? 0:?????????   2:?????????  3:?????????
     * @param pageNum   ??????
     * @param pageSize  ??????????????????
     */
    public static void queryMineOrderList(long userId,
                                          int orderType,
                                          int pageNum,
                                          int pageSize,
                                          ResultCallback<XMResult<OrderInfo>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", userId);
        params.put("orderStatus", orderType);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        request(QUERY_MINE_ORDER, params, callback);
    }


    /**
     * ??????????????????
     *
     * @param id       ??????ID
     * @param statusId ????????????ID
     */
    public static void cancelMineOrder(long id, int statusId, ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("statusId", statusId);
        request(CANCEL_MINE_ORDER, params, callback);
    }


    /**
     * ????????????
     *
     * @param id       ??????ID
     * @param statusId ????????????ID
     */
    public static void deleteMineOrder(long id, int statusId, ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("statusId", statusId);
        request(DELETE_MINE_ORDER, params, callback);
    }


    /**
     * ??????????????????
     *
     * @param id ??????ID
     */
    public static void getMineOrderDetail(long id, ResultCallback<XMResult<OrderInfo.Order>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        request(MINE_ORDER_DETAIL, params, callback);
    }


    /**
     * ??????????????????????????????
     *
     * @param hotelId   ??????Id
     * @param channelId ???????????????
     */
    public static void getHotelPolicy(String hotelId, String channelId, ResultCallback<XMResult<HotelPolicy>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("hotelId", hotelId);
        params.put("channelId", channelId);
        request(ORDER_HOTEL_POLICY, params, callback);
    }


    /**
     * ???????????????????????????
     */
    public static void getNumberKeyQRCode(ResultCallback<XMResult<KeyQRCode>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        request(NUMBER_KEY_URL, params, callback);
    }


    /**
     * ???????????????????????????
     */
    public static void getRemoteControllerQRCode(ResultCallback<XMResult<KeyQRCode>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        request(REMOTE_CONTROLLER_URL, params, callback);
    }


    /**
     * ???????????????????????????
     */
    public static void getHologramQRCode(ResultCallback<XMResult<HologramQRCode>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        request(GET_HOLOGRAM_URL, params, callback);
    }


    /**
     * ??????????????????????????????
     */
    public static void loopObtainHologramBindStatus(ResultCallback<XMResult<BindStatusBean>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        request(HOLOGRAM_BIND_STATUS_URL, params, callback);
    }


    /**
     * ????????????
     */
    public static void unbindHologram(ResultCallback<XMResult<String>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        request(UNBIND_HOLOGRAM_URL, params, callback);
    }

    /**
     * ??????????????????????????????App??????
     * @param callback
     */
    public static void getGuideAppList(ResultCallback<XMResult<ArrayList<AppInfo.DataBean>>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        request(GET_GUIDE_APP_LIST, params, callback);
    }
}
