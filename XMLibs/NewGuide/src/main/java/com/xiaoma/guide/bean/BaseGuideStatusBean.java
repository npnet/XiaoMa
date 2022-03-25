package com.xiaoma.guide.bean;

public abstract class BaseGuideStatusBean {
    // 是否展示过引导
    boolean guideShow = false;
    // 是否是应用内第一个引导
    boolean firstGuide = true;

    public boolean isGuideShow() {
        return guideShow;
    }

    public void setGuideShow(boolean guideShow) {
        this.guideShow = guideShow;
    }

    public boolean isFirstGuide() {
        return firstGuide;
    }

    public void setFirstGuide(boolean firstGuide) {
        this.firstGuide = firstGuide;
    }
}
