package com.xiaoma.xting.common;

import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.local.model.AMChannelBean;
import com.xiaoma.xting.local.model.FMChannelBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/12/05
 *     desc   :
 * </pre>
 */
public class LocalPlayList {

    public int fmPosition;
    public int amPosition;
    public List<FMChannelBean> fmChannelBeans = new ArrayList<>();
    public List<AMChannelBean> amChannelBeans = new ArrayList<>();
    private List<ChannelChangeListener> listeners = new CopyOnWriteArrayList<>();

    public interface ChannelChangeListener {
        void onFMChange(List<FMChannelBean> fmChannelBeans);

        void onAMChange(List<AMChannelBean> amChannelBeans);
    }

    private LocalPlayList() {
    }

    public static LocalPlayList getInstance() {
        return SingletonHolder.sInstance;
    }

    public void addListener(ChannelChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ChannelChangeListener listener) {
        listeners.remove(listener);
    }

    public int getAmPosition() {
        return amPosition;
    }

    public void setAmPosition(int amPosition) {
        this.amPosition = amPosition;
    }

    public int getFmPosition() {
        return fmPosition;
    }

    public void setFmPosition(int fmPosition) {
        this.fmPosition = fmPosition;
    }

    public List<FMChannelBean> getFmChannelBeans() {
        if (fmChannelBeans == null) {
            return new ArrayList<>();
        }

        if (CollectionUtil.isListEmpty(fmChannelBeans)) {
            List<FMChannelBean> fmChannelBeans = XtingUtils.getDBManager(null).queryAll(FMChannelBean.class);
            setFmChannelBeans(fmChannelBeans);
        }

        return fmChannelBeans;
    }

    public void setFmChannelBeans(List<FMChannelBean> fmChannelBeans) {
        this.fmChannelBeans = fmChannelBeans;
        for (ChannelChangeListener listener : listeners) {
            try {
                listener.onFMChange(fmChannelBeans);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<AMChannelBean> getAmChannelBeans() {
        if (amChannelBeans == null) {
            return new ArrayList<>();
        }

        if (CollectionUtil.isListEmpty(amChannelBeans)) {
            List<AMChannelBean> amChannelBeans = XtingUtils.getDBManager(null).queryAll(AMChannelBean.class);
            setAmChannelBeans(amChannelBeans);
        }
        return amChannelBeans;
    }

    public void setAmChannelBeans(List<AMChannelBean> amChannelBeans) {
        this.amChannelBeans = amChannelBeans;
        for (ChannelChangeListener listener : listeners) {
            try {
                listener.onAMChange(amChannelBeans);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteFM(FMChannelBean fmChannelBean) {
        XtingUtils.getDBManager(null).delete(fmChannelBean);
        fmChannelBeans.remove(fmChannelBean);
        for (ChannelChangeListener listener : listeners) {
            try {
                listener.onFMChange(fmChannelBeans);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteAM(AMChannelBean amChannelBean) {
        XtingUtils.getDBManager(null).delete(amChannelBean);
        amChannelBeans.remove(amChannelBean);
        for (ChannelChangeListener listener : listeners) {
            try {
                listener.onAMChange(amChannelBeans);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteFM(FMChannelBean fmChannelBean, boolean trigger) {
        XtingUtils.getDBManager(null).delete(fmChannelBean);
        fmChannelBeans.remove(fmChannelBean);
        if (trigger) {
            for (ChannelChangeListener listener : listeners) {
                try {
                    listener.onFMChange(fmChannelBeans);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteAM(AMChannelBean amChannelBean, boolean trigger) {
        XtingUtils.getDBManager(null).delete(amChannelBean);
        amChannelBeans.remove(amChannelBean);
        if (trigger) {
            for (ChannelChangeListener listener : listeners) {
                try {
                    listener.onAMChange(amChannelBeans);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean playLocalAtIndex(int band, int index) {
        if (band == XtingConstants.FMAM.TYPE_FM) {
            FMChannelBean fmChannelBean;
            if (CollectionUtil.isListEmpty(fmChannelBeans)) {
                fmChannelBean = new FMChannelBean(XtingConstants.FMAM.getFMStart());
            } else {
                fmChannelBean = fmChannelBeans.get(index);
            }
            boolean b = LocalFMOperateManager.newSingleton().playChannel(fmChannelBean);
            if (!b) return false;
            LocalPlayList.getInstance().setFmPosition(index);
        } else if (band == XtingConstants.FMAM.TYPE_AM) {
            AMChannelBean amChannelBean;
            if (CollectionUtil.isListEmpty(fmChannelBeans)) {
                amChannelBean = new AMChannelBean(XtingConstants.FMAM.getAMStart());
            } else {
                amChannelBean = amChannelBeans.get(index);
            }
            boolean b = LocalFMOperateManager.newSingleton().playChannel(amChannelBean);
            if (!b) return false;
            LocalPlayList.getInstance().setAmPosition(index);
        }
        return true;
    }

    /**
     * 静态内部类
     */
    private static class SingletonHolder {
        private final static LocalPlayList sInstance = new LocalPlayList();
    }
}
