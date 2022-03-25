package com.xiaoma.launcher.recommend.model;

/**
 * @author taojin
 * @date 2019/2/22
 */
public enum RecommendType {
    FILM("film"),
    FOOD("food"),
    PARKING("parking"),
    RADIO("radio"),
    SCENERY("scenery"),
    HOTEL("hotel"),
    MAP("map"),
    MUSIC("music"),
    GAS("gas"),
    NULL("null");

    private String value;

    RecommendType(String value) {
        this.value = value;
    }


    /*
     * 匹配操作码
     * */
    public static RecommendType matchRecommendType(String type) {
        for (RecommendType recommendType : RecommendType.values()) {
            if (recommendType.value.equalsIgnoreCase(type)) {
                return recommendType;
            }
        }
        return RecommendType.NULL;
    }


}
