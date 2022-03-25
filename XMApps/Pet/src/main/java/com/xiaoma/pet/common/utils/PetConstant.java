package com.xiaoma.pet.common.utils;

/**
 * Created by Gillben on 2019/1/2 0002
 * <p>
 * desc:
 */
public class PetConstant {

    public static final String STORE_AND_REPOSITORY_FLAG = "STORE_AND_REPOSITORY_FLAG";
    public static final int GO_TO_STORE = 0;
    public static final int GO_TO_REPOSITORY = 1;


    //Fragment tag
    public static final String PAY_FRAGMENT_TAG = "PAY_FRAGMENT_TAG";
    public static final String MALL_HOME_FRAGMENT_TAG = "MALL_HOME_FRAGMENT_TAG";
    public static final String STORE_FRAGMENT_TAG = "STORE_FRAGMENT_TAG";
    public static final String REPOSITORY_FRAGMENT_TAG = "REPOSITORY_FRAGMENT_TAG";

    //PetHomeActivity ->  PetMallActivity   requestCode&resultCode
    public static final int REFRESH_HOME_PET_INFO = 99;
    public static final int HOME_MALL_RESULT_CODE = 100;
    public static final int SHOW_LAST_GUIDE = 1001;

    //error code
    public static final int EATING_FOOD_ERROR_CODE = 10035;
    public static final int FOOD_NOT_ENOUGH_ERROR_CODE = 10036;
    public static final int PET_NOT_EXISTS_ERROR_CODE = 10037;

}
