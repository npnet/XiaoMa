package com.xiaoma.vr.model;

import java.io.Serializable;

/**
 * @author: iSun
 * @date: 2019/3/15 0015
 */
public class Score implements Serializable {
    public int nScore;//得分值
    public float fPower;//能量值
    public boolean isMainDrive = true;//默认主驾驶
    public int nMvwId;//id
    public int nMvwScene;//场景
    public String lParam;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Score:").append(nScore).append(" Power:").append(fPower).append("isMainDrive:").append(isMainDrive)
                .append(" MvwId:").append(nMvwId).append(" MvwScene:").append(nMvwScene).append(" lParam:").append(lParam);
        return sb.toString();
    }
}
