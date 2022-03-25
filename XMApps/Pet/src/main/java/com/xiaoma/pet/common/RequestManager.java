package com.xiaoma.pet.common;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.pet.model.ChapterResourceInfo;
import com.xiaoma.pet.model.CoinAndSignInfo;
import com.xiaoma.pet.model.OpenTreasureInfo;
import com.xiaoma.pet.model.PetGiftInfo;
import com.xiaoma.pet.model.PetInfo;
import com.xiaoma.pet.model.PetMapInfo;
import com.xiaoma.pet.model.RepositoryInfo;
import com.xiaoma.pet.model.StoreGoodsInfo;
import com.xiaoma.pet.model.UpgradeRewardInfo;
import com.xiaoma.utils.GsonHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gillben on 2018/12/29 0029
 * <p>
 * desc:  网络请求管理
 * <p>
 * TODO 所接口请求参数待定
 */
public final class RequestManager {

    private static final String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();
    private static final String INIT_PET_URL = BASE_URL + "pet/initPet";
    private static final String PET_INFO_URL = BASE_URL + "pet/getPetInfo";
    private static final String PET_EAT_FOOD_URL = BASE_URL + "pet/petEating";
    private static final String PET_UPGRADE_URL = BASE_URL + "pet/petUpgrade";
    private static final String GEN_PET_RANDOM_GIFT_URL = BASE_URL + "pet/randomReward";
    private static final String GET_SHOP_FOOD_URL = BASE_URL + "pet/getShopList";
    private static final String GET_REPOSITORY_URL = BASE_URL + "pet/getRepositoryList";
    private static final String BUY_FOOD_URL = BASE_URL + "pet/buyGoods";
    private static final String POST_MAP_FINISH_PERCENT_URL = BASE_URL + "pet/reportTimeAccumulation";
    private static final String GET_BOX_GIFT_URL = BASE_URL + "pet/getBoxRecord";
    private static final String OPEN_BOX_GIFT_URL = BASE_URL + "pet/openBoxRecord";
    private static final String GAME_MAP_CHAPTER_URL = BASE_URL + "pet/getMapList";
    private static final String DOWNLOAD_LEVEL_RESOURCE_URL = BASE_URL + "pet/getResources";
    private static final String carCoinUrl = BASE_URL + "user/score/status.action";

    private static <T> void request(String url, Map<String, Object> params, final ResultCallback<XMResult<T>> callback) {
        if (callback == null) {
            return;
        }
//        //宠物只与主账户绑定
//        User user = UserBindManager.getInstance().getCachedMasterUser();
//        if (user != null) {
//            params.put("uid", user.getId());
//        }

        XmHttp.getDefault().getString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                String data = response.body();
                XMResult<T> result = GsonHelper.fromJson(data, type);
                if (result == null || !result.isSuccess()) {
                    callback.onFailure(result == null ? response.code() : result.getResultCode(),
                            result == null ? response.message() : result.getResultMessage());
                    return;
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onFailure(-1, response.body());
            }
        });
    }


    /**
     * 初始化宠物信息
     *
     * @param petName 宠物名称
     * @param petDesc 宠物秒速
     */
    public static void initPet(String petName, String petDesc, ResultCallback<XMResult<String>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("petName", petName);
        params.put("petDesc", petDesc);
        request(INIT_PET_URL, params, callback);
    }


    /**
     * 获取主页信息（宠物、礼物、宝箱）
     */
    public static void getPetInfo(ResultCallback<XMResult<PetInfo>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        request(PET_INFO_URL, params, callback);
    }


    /**
     * 生成随机礼物
     */
    public static void genRandomGift(ResultCallback<XMResult<String>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        request(GEN_PET_RANDOM_GIFT_URL, params, callback);
    }


    /**
     * 获取宠物礼物列表
     */
    public static void getBoxGiftList(ResultCallback<XMResult<List<PetGiftInfo>>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        request(GET_BOX_GIFT_URL, params, callback);
    }


    /**
     * 开宝盒礼物
     */
    public static void openPetGift(ResultCallback<XMResult<OpenTreasureInfo>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        request(OPEN_BOX_GIFT_URL, params, callback);
    }


    /**
     * 上报地图完成时间
     *
     * @param timeAccumulation 当前时间
     * @param chapterId        关卡Id
     */
    public static void postMapCompleteTime(long timeAccumulation, long chapterId, ResultCallback<XMResult<String>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("timeAccumulation", timeAccumulation);
        params.put("chapterId", chapterId);
        request(POST_MAP_FINISH_PERCENT_URL, params, callback);
    }


    /**
     * 获取宠物游戏 地图章节信息
     *
     * @param gameVersion 游戏版本
     * @param chapterId   关卡版本
     */
    public static void getGameChapterInfo(String gameVersion, long chapterId, ResultCallback<XMResult<PetMapInfo>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("gameVersion", gameVersion);
        params.put("chapterId", chapterId);
        request(GAME_MAP_CHAPTER_URL, params, callback);
    }


    /**
     * 下载关卡资源
     *
     * @param gameChapterId 游戏关卡Id
     * @param resourcesType 资源类型  1、场景资源   2、礼物资源
     */
    public static void downloadLevelResource(long gameChapterId, String resourcesType, ResultCallback<XMResult<ChapterResourceInfo>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("gameChapterId", gameChapterId);
        params.put("resourcesType", resourcesType);
        request(DOWNLOAD_LEVEL_RESOURCE_URL, params, callback);
    }


    /**
     * 获取商城商品列表
     *
     * @param goodsType 商品类型 1、食品 2、饰品 3、角色
     */
    public static void getShopPetGoods(String goodsType, ResultCallback<XMResult<List<StoreGoodsInfo>>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("goodsType", goodsType);
        request(GET_SHOP_FOOD_URL, params, callback);
    }


    /**
     * 获取仓库食物列表
     *
     * @param goodsType 商品类型
     */
    public static void getRepositoryPetGoods(String goodsType, ResultCallback<XMResult<List<RepositoryInfo>>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("goodsType", goodsType);
        request(GET_REPOSITORY_URL, params, callback);
    }


    /**
     * 购买食物
     *
     * @param foodId 食物Id
     * @param number 购买数量
     */
    public static void buyPetFood(long foodId, int number, ResultCallback<XMResult<String>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("foodId", foodId);
        params.put("number", number);
        request(BUY_FOOD_URL, params, callback);
    }


    /**
     * 宠物进食上报
     *
     * @param foodId 食物Id
     */
    public static void petEatFood(long foodId, ResultCallback<XMResult<String>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("foodId", foodId);
        request(PET_EAT_FOOD_URL, params, callback);
    }


    /**
     * 宠物升级
     *
     * @param upgrade    升级后的等级
     * @param experience 升级后的经验值
     */
    public static void petUpgrade(int upgrade, long experience, ResultCallback<XMResult<UpgradeRewardInfo>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("grade", upgrade);
        params.put("experience", experience);
        request(PET_UPGRADE_URL, params, callback);

    }


    public static void getUserCarCoin(ResultCallback<XMResult<CoinAndSignInfo>> callback) {
        HashMap<String, Object> params = new HashMap<>(1);
        params.put("userId", LoginManager.getInstance().getLoginUserId());
        request(carCoinUrl, params, callback);
    }

}
