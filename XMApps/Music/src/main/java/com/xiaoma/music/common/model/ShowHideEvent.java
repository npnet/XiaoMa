package com.xiaoma.music.common.model;

/**
 * Created by ZYao.
 * Date ：2018/12/27 0027
 */
public class ShowHideEvent {
    private boolean showOnline;
    private boolean showLocal;
    private boolean showMine;

    public ShowHideEvent(boolean showOnline, boolean showLocal, boolean showMine) {
        this.showOnline = showOnline;
        this.showLocal = showLocal;
        this.showMine = showMine;
    }

    public boolean isShowOnline() {
        return showOnline;
    }

    public boolean isShowLocal() {
        return showLocal;
    }

    public boolean isShowMine() {
        return showMine;
    }

    public static ShowHideEvent createOnline() {
        return new ShowHideEvent(true, false, false);
    }

    public static ShowHideEvent createLocal() {
        return new ShowHideEvent(false, true, false);
    }

    public static ShowHideEvent createMine() {
        return new ShowHideEvent(false, false, true);
    }
}
