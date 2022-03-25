package com.xiaoma.xting.launcher;

import android.text.TextUtils;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.xiaoma.config.bean.SourceType;
import com.xiaoma.config.utils.SourceUtils;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.AppType;
import com.xiaoma.xting.common.LocalPlayList;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.control.AudioFocusManager;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.local.model.AMChannelBean;
import com.xiaoma.xting.local.model.BaseChannelBean;
import com.xiaoma.xting.local.model.FMChannelBean;
import com.xiaoma.xting.player.model.AcrRadioBean;
import com.xiaoma.xting.sdk.AcrFactory;
import com.xiaoma.xting.sdk.LocalFMFactory;
import com.xiaoma.xting.sdk.LocalFMStatusListenerImpl;
import com.xiaoma.xting.sdk.listener.OnRadioResultListener;
import com.xiaoma.xting.sdk.model.BandType;
import com.xiaoma.xting.sdk.model.XMRadioStation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/2/14
 */
public class LocalFMOperateManager {

    private static final int MAX_COUNT = 50;
    private String mLat, mLon;
    private int mBand = -1;
    private boolean mIsScanningF = false;
    private int mLauncherCategoryId = -1;
    private List<IOnAutoSearchOkListener> mAutoSearchOkListeners;

    private LocalFMOperateManager() {
        mAutoSearchOkListeners = new ArrayList<>();
    }

    public static LocalFMOperateManager newSingleton() {
        return Holder.sINSTANCE;
    }

    public int getLauncherCategoryId() {
        return mLauncherCategoryId;
    }

    public void setLauncherCategoryId(int mLauncherCategoryId) {
        this.mLauncherCategoryId = mLauncherCategoryId;
    }

    public boolean searchLocalFM(final int band) {
        this.mBand = band;
        LocalFMFactory.getSDK().addLocalFMStatusListener(new LocalFMStatusListenerImpl() {
            @Override
            public void onRadioOpen() {
                super.onRadioOpen();
                RemoteIatManager.getInstance().uploadPlayState(true, AppType.RADIO);
                SourceUtils.setSourceStatus(SourceType.FM, true);
                boolean b = autoSearchLocalAudio(mBand);
                if (!b) dispatchOpenFailed();
            }

            @Override
            public void onRadioClose(XMRadioStation xmRadioStation) {
                super.onRadioClose(xmRadioStation);
                RemoteIatManager.getInstance().uploadPlayState(false, AppType.RADIO);
                SourceUtils.setSourceStatus(SourceType.FM, false);
                mIsScanningF = false;
                dispatchOpenFailed();
                LocalFMFactory.getSDK().removeLocalFMStatusListener(this);
            }

            @Override
            public void onScanAllResult(List<XMRadioStation> stations) {
                LocalFMFactory.getSDK().removeLocalFMStatusListener(this);
                if (stations != null && stations.size() > 1) {
                    Collections.sort(stations);
                }

                if (band == XtingConstants.FMAM.TYPE_FM) {
                    XtingUtils.getDBManager(null).deleteAll(FMChannelBean.class);
                } else if (band == XtingConstants.FMAM.TYPE_AM) {
                    XtingUtils.getDBManager(null).deleteAll(AMChannelBean.class);
                }
                boolean b = false;
                for (int i = 0; i < stations.size(); i++) {
                    XMRadioStation station = stations.get(i);
                    BaseChannelBean channelBean = XtingUtils.getBaseChannelByValue(station.getChannel());
                    if (channelBean instanceof FMChannelBean) {
                        saveFMChannel(new FMChannelBean(station.getChannel()));
                        if (i == 0) {
                            b = LocalFMFactory.getSDK().tuneFM(station.getChannel());
                        }
                    } else if (channelBean instanceof AMChannelBean) {
                        saveAMChannel(new AMChannelBean(station.getChannel()));
                        if (i == 0) {
                            b = LocalFMFactory.getSDK().tuneAM(station.getChannel());
                        }
                    }
                }
                if (b) {
                    dispatchAutoSearchOk(mBand);
                } else {
                    dispatchOpenFailed();
                }
            }

            @Override
            public void onCancel() {
                super.onCancel();
                mIsScanningF = false;
                dispatchOpenFailed();
            }
        });

        if (!LocalFMFactory.getSDK().isRadioOpen()) {
            if (mIsScanningF) {
                return true;
            }
            mIsScanningF = true;

            return LocalFMFactory.getSDK().openRadio();
        } else {
            return autoSearchLocalAudio(mBand);
        }
    }

    private boolean autoSearchLocalAudio(int band) {
        getLatLng();
        return LocalFMFactory.getSDK().scanAll(band == XtingConstants.FMAM.TYPE_FM ? BandType.FM : BandType.AM);
    }

    private void getLatLng() {
        LocationInfo location = LocationManager.getInstance().getRealCurrentLocation();
        if (location != null) {
            mLat = String.valueOf(location.getLatitude());
            mLon = String.valueOf(location.getLongitude());
        }
    }


    public boolean saveFMChannel(FMChannelBean fmChannel) {
        FMChannelBean saveBean = XtingUtils.getDBManager(null).queryById(fmChannel.getChannelValue(), FMChannelBean.class);
        if (saveBean != null
                && !TextUtils.isEmpty(saveBean.getChannelName())
                && !TextUtils.isEmpty(saveBean.getCoverUrl())
                && !TextUtils.isEmpty(saveBean.getXmlyId())) {
            getChannelInfo(fmChannel, XtingConstants.FMAM.TYPE_FM, fmChannel.getChannelValue() / 100);
            return true;
        }
        saveChannel(fmChannel, XtingConstants.FMAM.TYPE_FM);
        getChannelInfo(fmChannel, XtingConstants.FMAM.TYPE_FM, fmChannel.getChannelValue() / 100);
        return true;
    }

    public boolean saveAMChannel(AMChannelBean amChannel) {
        AMChannelBean saveBean = XtingUtils.getDBManager(null).queryById(amChannel.getChannelValue(), AMChannelBean.class);
        if (saveBean != null) {
            return true;
        }
        saveChannel(amChannel, XtingConstants.FMAM.TYPE_AM);
        getChannelInfo(amChannel, XtingConstants.FMAM.TYPE_AM, amChannel.getChannelValue());
        return true;
    }


    private void getChannelInfo(final BaseChannelBean channelBean, final int radioType, int frequency) {
        if (XtingConstants.LOCAL_FM_TO_XMLY_TOGGLE &&
                !TextUtils.isEmpty(mLat) && !TextUtils.isEmpty(mLon)) {
            AcrFactory.getInstance().getSDK().requestRadioMetadata(radioType, mLat, mLon, frequency, new OnRadioResultListener() {
                @Override
                public void onRadioResult(String json) {
                    AcrRadioBean mRadioItem = GsonHelper.fromJson(json, AcrRadioBean.class);
                    if (mRadioItem != null
                            && mRadioItem.status.code == 0
                            && !CollectionUtil.isListEmpty(mRadioItem.data.list)
                            && mRadioItem.data.list.get(0) != null) {
                        channelBean.setChannelCover(mRadioItem.data.list.get(0).logo);
                        if (mRadioItem.data.list.get(0).external_ids != null
                                && !TextUtils.isEmpty(mRadioItem.data.list.get(0).external_ids.ximalaya)) {
                            channelBean.setXmlyId(mRadioItem.data.list.get(0).external_ids.ximalaya);
                        }
                        if (!TextUtils.isEmpty(mRadioItem.data.list.get(0).name)) {
                            channelBean.setChannelName(mRadioItem.data.list.get(0).name);
                        }
                    }
                    saveChannel(channelBean, radioType);
                }
            });
        }
    }


    public boolean contains(int type, int value) {
        if (type == XtingConstants.FMAM.TYPE_FM) {
            FMChannelBean channelValue = XtingUtils.getDBManager(null).queryById(value, FMChannelBean.class);
            return channelValue != null;
        } else {
            AMChannelBean channelValue = XtingUtils.getDBManager(null).queryById(value, AMChannelBean.class);
            return channelValue != null;
        }
    }

    public void saveChannel(BaseChannelBean channelBean, int radioType) {
        if (channelBean instanceof FMChannelBean) {
            long fmCount = XtingUtils.getDBManager(null).queryCount(FMChannelBean.class);
            if (fmCount >= MAX_COUNT) {
                QueryBuilder<FMChannelBean> queryBuilder = new QueryBuilder<>(FMChannelBean.class);
                queryBuilder.appendOrderAscBy("rowid").limit(String.valueOf(fmCount - (MAX_COUNT - 1)));
                XtingUtils.getDBManager(null).delete(XtingUtils.getDBManager(null).queryByWhere(FMChannelBean.class, queryBuilder));
            }
        } else if (channelBean instanceof AMChannelBean) {
            long amCount = XtingUtils.getDBManager(null).queryCount(AMChannelBean.class);
            if (amCount >= MAX_COUNT) {
                QueryBuilder<AMChannelBean> queryBuilder = new QueryBuilder<>(AMChannelBean.class);
                queryBuilder.appendOrderAscBy("rowid").limit(String.valueOf(amCount - (MAX_COUNT - 1)));
                XtingUtils.getDBManager(null).delete(XtingUtils.getDBManager(null).queryByWhere(AMChannelBean.class, queryBuilder));
            }
        }
        XtingUtils.getDBManager(null).save(channelBean);
        if (radioType == XtingConstants.FMAM.TYPE_FM) {
            fetchFMChannels();
        } else {
            fetchAMChannels();
        }
    }

    public void fetchFMChannels() {
        List<FMChannelBean> fmChannels = XtingUtils.getDBManager(null).queryAll(FMChannelBean.class);
        LocalPlayList.getInstance().setFmChannelBeans(fmChannels);
    }

    public void fetchAMChannels() {
        List<AMChannelBean> amChannels = XtingUtils.getDBManager(null).queryAll(AMChannelBean.class);
        LocalPlayList.getInstance().setAmChannelBeans(amChannels);
        dispatchAutoSearchOk(XtingConstants.FMAM.TYPE_AM);
    }

    public void playFMChannel() {
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
        BaseChannelBean channel = SharedPrefUtils.getLastYQPlayerInfo(true);
        if (channel instanceof FMChannelBean) {
            PlayerSourceFacade.newSingleton().getPlayerControl().playWithModel(channel);
        } else {
            List<FMChannelBean> fmChannelList = XtingUtils.getDBManager(null).queryAll(FMChannelBean.class);
            if (fmChannelList == null || fmChannelList.isEmpty()) {
                playChannel(XtingConstants.FMAM.getFMStart());
            } else {
                FMChannelBean fmChannelBean = fmChannelList.get(0);
                PlayerSourceFacade.newSingleton().getPlayerControl().playWithModel(fmChannelBean);
            }
        }
    }

    public void playAMChannel() {
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
        BaseChannelBean channel = SharedPrefUtils.getLastYQPlayerInfo(false);
        if (channel instanceof AMChannelBean) {
            PlayerSourceFacade.newSingleton().getPlayerControl().playWithModel(channel);
        } else {
            List<AMChannelBean> channelList = XtingUtils.getDBManager(null).queryAll(AMChannelBean.class);
            if (channelList == null || channelList.isEmpty()) {
                playChannel(XtingConstants.FMAM.getAMStart());
            } else {
                AMChannelBean bean = channelList.get(0);
                PlayerSourceFacade.newSingleton().getPlayerControl().playWithModel(bean);
            }
        }
    }

    public void listenSearchResultToLauncher(IOnAutoSearchOkListener listener) {
        List<FMChannelBean> fmChannelBeans = XtingUtils.getDBManager(null).queryAll(FMChannelBean.class);
        if (ListUtils.isEmpty(fmChannelBeans)) {
            if (!mAutoSearchOkListeners.contains(listener)) {
                mAutoSearchOkListeners.add(listener);
            }
            boolean b = searchLocalFM(XtingConstants.FMAM.TYPE_FM);
            if (!b) {
                listener.autoOpenFailed();
            }
        } else {
            LocalPlayList.getInstance().setFmChannelBeans(fmChannelBeans);
            int random = new Random().nextInt(fmChannelBeans.size());
            boolean b = LocalFMOperateManager.newSingleton().playLocalFMAtPos(XtingConstants.FMAM.TYPE_FM, random);
            if (!b) {
                listener.autoOpenFailed();
            } else {
                listener.autoSearchSuccess(XtingConstants.FMAM.TYPE_FM);
            }
        }
    }

    public void removeAutoSearchOkListener(IOnAutoSearchOkListener listener) {
        mAutoSearchOkListeners.remove(listener);
    }

    public void clearAutoSearchOkListener() {
        mAutoSearchOkListeners.clear();
    }

    public void dispatchAutoSearchOk(int bandType) {
        mIsScanningF = false;
        for (IOnAutoSearchOkListener listener : mAutoSearchOkListeners) {
            listener.autoSearchSuccess(bandType);
        }
    }

    private void dispatchOpenFailed() {
        mIsScanningF = false;
        for (IOnAutoSearchOkListener listener : mAutoSearchOkListeners) {
            listener.autoOpenFailed();
        }
    }

    public boolean playLocalFMAtPos(int band, int pos) {
        return LocalPlayList.getInstance().playLocalAtIndex(band, pos);
    }

    /*=======================直接给外部调用，比如语音助手============================*/

    public boolean collectCurFM(boolean saved) {
        if (!LocalFMFactory.getSDK().isRadioOpen()) return false;
        XMRadioStation station = LocalFMFactory.getSDK().getCurrentStation();
        if (station == null) return false;
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
        PlayerSourceFacade.newSingleton().getPlayerControl().subscribe(saved);
//        LocalFMOperateManager.newSingleton().favoriteOrNot(saved, station.getChannel());
        return true;
    }

    public void searchLocalFM() {
        if (!LocalFMFactory.getSDK().isRadioOpen()) {
            LocalFMFactory.getSDK().openRadio();
        } else {
            BandType currentBand = LocalFMFactory.getSDK().getCurrentBand();
            int band = (currentBand == null || currentBand == BandType.FM) ? XtingConstants.FMAM.TYPE_FM : XtingConstants.FMAM.TYPE_AM;
            searchLocalFM(band);
        }
    }

    public boolean playLocalFMAtFirst() {
        BandType currentBand = LocalFMFactory.getSDK().getCurrentBand();
        int band = (currentBand == null || currentBand == BandType.FM) ? XtingConstants.FMAM.TYPE_FM : XtingConstants.FMAM.TYPE_AM;
        return LocalPlayList.getInstance().playLocalAtIndex(band, 0);
    }

    public boolean playLocalFirstByBand(int band) {
        return LocalPlayList.getInstance().playLocalAtIndex(band, 0);
    }

    public boolean playLocalFMPreOrNext(boolean isNext) {
        if (LocalFMFactory.getSDK().isRadioOpen()) {
            if (isNext) {
                return LocalFMFactory.getSDK().scanUp();
            } else {
                return LocalFMFactory.getSDK().scanDown();
            }
        } else {
            return false;
        }
    }

    public BaseChannelBean getCurrentRadioInfo() {
        getLatLng();
        if (LocalFMFactory.getSDK().isRadioOpen()) {
            XMRadioStation station = LocalFMFactory.getSDK().getCurrentStation();
            final BaseChannelBean curChannel;
            BaseChannelBean queryChannel;
            if (station == null) {
                return null;
            }
            if (station.getRadioBand() == XtingConstants.FMAM.TYPE_FM) {
                curChannel = new FMChannelBean(station.getChannel());
                queryChannel = XtingUtils.getDBManager(null).queryById(station.getChannel(), FMChannelBean.class);
            } else {
                curChannel = new AMChannelBean(station.getChannel());
                queryChannel = XtingUtils.getDBManager(null).queryById(station.getChannel(), AMChannelBean.class);
            }
            if (queryChannel != null) {
                return queryChannel;
            } else {
                return curChannel;
            }
        } else {
            return null;
        }
    }

    public void getRadioInfo(final SimpleCallback<BaseChannelBean> callback) {
        getLatLng();
        if (LocalFMFactory.getSDK().isRadioOpen()) {
            XMRadioStation station = LocalFMFactory.getSDK().getCurrentStation();
            final BaseChannelBean curChannel;
            BaseChannelBean queryChannel;
            if (station == null) {
                callback.onError(-1, null);
                return;
            }
            if (station.getRadioBand() == XtingConstants.FMAM.TYPE_FM) {
                curChannel = new FMChannelBean(station.getChannel());
                queryChannel = XtingUtils.getDBManager(null).queryById(station.getChannel(), FMChannelBean.class);
            } else {
                curChannel = new AMChannelBean(station.getChannel());
                queryChannel = XtingUtils.getDBManager(null).queryById(station.getChannel(), AMChannelBean.class);
            }
            if (queryChannel != null) {
                callback.onSuccess(queryChannel);
            } else {
                AcrFactory.getInstance().getSDK().requestRadioMetadata(station.getRadioBand(), mLat, mLon,
                        station.getChannel(), new OnRadioResultListener() {
                            @Override
                            public void onRadioResult(String json) {
                                AcrRadioBean mRadioItem = GsonHelper.fromJson(json, AcrRadioBean.class);
                                if (mRadioItem != null
                                        && mRadioItem.status.code == 0
                                        && !CollectionUtil.isListEmpty(mRadioItem.data.list)
                                        && mRadioItem.data.list.get(0) != null) {
                                    curChannel.setChannelCover(mRadioItem.data.list.get(0).logo);
                                    if (mRadioItem.data.list.get(0).external_ids != null
                                            && !TextUtils.isEmpty(mRadioItem.data.list.get(0).external_ids.ximalaya)) {
                                        curChannel.setXmlyId(mRadioItem.data.list.get(0).external_ids.ximalaya);
                                    }
                                    if (!TextUtils.isEmpty(mRadioItem.data.list.get(0).name)) {
                                        curChannel.setChannelName(mRadioItem.data.list.get(0).name);
                                    }
                                }
                                callback.onSuccess(curChannel);
                            }
                        });
            }
        } else {
            callback.onError(-1, null);
        }
    }

    public void getRadioInfo(BaseChannelBean channelBean, final SimpleCallback<BaseChannelBean> callback) {
        getLatLng();
        int band = XtingConstants.FMAM.TYPE_FM;
        int frequency = channelBean.getChannelValue();
        if (channelBean instanceof AMChannelBean) {
            band = XtingConstants.FMAM.TYPE_AM;
        } else {
            frequency = frequency / 100;
        }
        AcrFactory.getInstance().getSDK().requestRadioMetadata(band, mLat, mLon,
                frequency, new OnRadioResultListener() {
                    @Override
                    public void onRadioResult(String json) {
                        AcrRadioBean mRadioItem = GsonHelper.fromJson(json, AcrRadioBean.class);
                        if (mRadioItem != null
                                && mRadioItem.status.code == 0
                                && !CollectionUtil.isListEmpty(mRadioItem.data.list)
                                && mRadioItem.data.list.get(0) != null) {
                            channelBean.setChannelCover(mRadioItem.data.list.get(0).logo);
                            if (mRadioItem.data.list.get(0).external_ids != null
                                    && !TextUtils.isEmpty(mRadioItem.data.list.get(0).external_ids.ximalaya)) {
                                channelBean.setXmlyId(mRadioItem.data.list.get(0).external_ids.ximalaya);
                            }
                            if (!TextUtils.isEmpty(mRadioItem.data.list.get(0).name)) {
                                channelBean.setChannelName(mRadioItem.data.list.get(0).name);
                            }
                        }
                        callback.onSuccess(channelBean);
                    }
                });
    }

    public boolean playChannel(int bandValue, int channel) {
        boolean b;
        if (!LocalFMFactory.getSDK().isRadioOpen()) {
            b = LocalFMFactory.getSDK().openRadio();
        } else {
            b = AudioFocusManager.getInstance().requestFMFocus();
        }
        if (!b) return false;

        BandType band = LocalFMFactory.getSDK().getCurrentBand();
        if (band != null && band.getBand() != bandValue) {
            LocalFMFactory.getSDK().switchBand(BandType.valueOf(bandValue));
        }

        if (bandValue == XtingConstants.FMAM.TYPE_FM) {
            LocalFMFactory.getSDK().tuneFM(channel);
        } else {
            LocalFMFactory.getSDK().tuneAM(channel);
        }
        return true;
    }

    public boolean playChannel(BaseChannelBean channelBean) {
        if (channelBean == null) return false;

        if (channelBean instanceof FMChannelBean) {
            return playChannel(XtingConstants.FMAM.TYPE_FM, channelBean.getChannelValue());
        } else {
            return playChannel(XtingConstants.FMAM.TYPE_AM, channelBean.getChannelValue());
        }
    }

    public boolean playChannel(int channel) {
        int band = getBandByChannel(channel);
        return playChannel(band, channel);
    }

    public int getBandByChannel(int channel) {
        if (channel >= XtingConstants.FMAM.getAMStart() && channel <= XtingConstants.FMAM.getAMEnd()) {
            return XtingConstants.FMAM.TYPE_AM;
        } else {
            return XtingConstants.FMAM.TYPE_FM;
        }
    }

    public void playLastRadio() {
        playChannel(SharedPrefUtils.getLastYQPlayerInfo(true));
    }

    public void switchBandAndPlayLast(BandType bandType) {
        if (!LocalFMFactory.getSDK().isRadioOpen()) LocalFMFactory.getSDK().openRadio();
        LocalFMFactory.getSDK().switchBand(bandType);
        if (bandType.getBand() == XtingConstants.FMAM.TYPE_FM) {
            playChannel(SharedPrefUtils.getCacheLastFM());
        } else {
            playChannel(SharedPrefUtils.getCacheLastAM());
        }
    }

    public boolean playClosestChannel(boolean isNext) {
        if (!LocalFMFactory.getSDK().isRadioOpen()) {
            boolean b = LocalFMFactory.getSDK().openRadio();
            if (!b) return false;
        }
        XMRadioStation station = LocalFMFactory.getSDK().getCurrentStation();
        if (station == null) return false;
        BaseChannelBean closestChannel;
        if (station.getRadioBand() == XtingConstants.FMAM.TYPE_FM) {
            List<FMChannelBean> fmChannels = LocalPlayList.getInstance().getFmChannelBeans();
            if (fmChannels.size() <= 1) {
                return false;
            }
            closestChannel = getClosestChannel(fmChannels, station.getChannel(), isNext);
        } else {
            List<AMChannelBean> amChannels = LocalPlayList.getInstance().getAmChannelBeans();
            if (amChannels.size() <= 1) {
                return false;
            }
            closestChannel = getClosestChannel(amChannels, station.getChannel(), isNext);
        }
        if (closestChannel == null) return false;
        if (closestChannel.getChannelValue() == station.getChannel()) return false;
        LocalFMOperateManager.newSingleton().playChannel(closestChannel);
        return true;
    }

    private <T extends BaseChannelBean> T getClosestChannel(List<T> list, int channel, boolean isNext) {
        if (CollectionUtil.isListEmpty(list)) return null;
        T closest = null;
        int max = 0;
        int min = 0;
        for (int i = 0; i < list.size(); i++) {
            T bean = list.get(i);
            if (bean.getChannelValue() == channel) {
                if (i == list.size() - 1 && isNext) {
                    return list.get(0);
                } else if (i == 0 && !isNext) {
                    return list.get(list.size() - 1);
                } else if (isNext) {
                    return list.get(i + 1);
                } else {
                    return list.get(i - 1);
                }
            }
            if (closest == null) {
                closest = bean;
            } else {
                if (isNext && channel < bean.getChannelValue()) {
                    int last = closest.getChannelValue() - channel;
                    int current = bean.getChannelValue() - channel;
                    closest = current < last ? bean : closest;
                } else if (!isNext && channel > bean.getChannelValue()) {
                    int last = channel - closest.getChannelValue();
                    int current = channel - bean.getChannelValue();
                    closest = current < last ? bean : closest;
                }
            }
            max = max > bean.getChannelValue() ? max : bean.getChannelValue();
            min = min < bean.getChannelValue() ? min : bean.getChannelValue();
        }

        if (channel < min && channel > max) {
            closest = list.get(0);
        }

        return closest;
    }

    interface Holder {
        LocalFMOperateManager sINSTANCE = new LocalFMOperateManager();
    }

    public interface IOnAutoSearchOkListener {

        void autoSearchSuccess(int bandType);

        void autoOpenFailed();
    }
}
