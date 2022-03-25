package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMAnnouncerList extends XMBean<AnnouncerList> {
    public XMAnnouncerList(AnnouncerList announcerList) {
        super(announcerList);
    }

    public int getTotalPage() {
        return getSDKBean().getTotalPage();
    }

    public void setTotalPage(int totalPage) {
        getSDKBean().setTotalPage(totalPage);
    }

    public int getTotalCount() {
        return getSDKBean().getTotalCount();
    }

    public void setTotalCount(int totalCount) {
        getSDKBean().setTotalCount(totalCount);
    }

    public int getCurrentPage() {
        return getSDKBean().getCurrentPage();
    }

    public void setCurrentPage(int currentPage) {
        getSDKBean().setCurrentPage(currentPage);
    }

    public long getvCategoryId() {
        return getSDKBean().getvCategoryId();
    }

    public void setvCategoryId(long vCategoryId) {
        getSDKBean().setvCategoryId(vCategoryId);
    }

    public List<XMAnnouncer> getAnnouncerList() {
        List<Announcer> announcerList = getSDKBean().getAnnouncerList();
        List<XMAnnouncer> xmAnnouncers = new ArrayList<>();
        if (announcerList != null && !announcerList.isEmpty()) {
            for (Announcer announcer : announcerList) {
                if (announcer == null) {
                    continue;
                }
                xmAnnouncers.add(new XMAnnouncer(announcer));
            }
        }
        return xmAnnouncers;
    }

    public void setAnnouncerList(List<XMAnnouncer> xmAnnouncerList) {
        if (xmAnnouncerList == null) {
            getSDKBean().setAnnouncerList(null);
            return;
        }
        List<Announcer> announcerList = new ArrayList<>();
        for (XMAnnouncer xmAnnouncer : xmAnnouncerList) {
            if (xmAnnouncer == null) {
                continue;
            }
            announcerList.add(xmAnnouncer.getSDKBean());
        }
        getSDKBean().setAnnouncerList(announcerList);
    }
}
