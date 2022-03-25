package com.xiaoma.assistant.callback;

/**
 * Created by qiuboxiang on 2019/4/18 11:37
 * Desc:
 */
public abstract class SortChooseCallback implements IChooseCallback {

    public void sortByPrice() {
    }

    public void sortByDistance() {
    }

    public void sortByStarLevel() {
    }

    public void sortByScore() {
    }

    public void filterByStarLevel(String text) {

    }

    public void filterByPrice(boolean isMinPrice) {

    }

    public void filterByHighScore() {

    }

    public void filterByDistance(boolean isMinDistance) {

    }

    public void filterByHeadNumber(String headNumber) {

    }

    public void confirm() {

    }

    public void cancel() {

    }

    public void correct() {

    }

}
