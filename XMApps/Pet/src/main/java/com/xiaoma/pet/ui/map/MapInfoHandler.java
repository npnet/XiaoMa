package com.xiaoma.pet.ui.map;

import android.content.Context;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.pet.R;
import com.xiaoma.pet.common.RequestManager;
import com.xiaoma.pet.common.utils.ConvertMapTimeCoordinate;
import com.xiaoma.pet.common.utils.SavePetInfoUtils;
import com.xiaoma.pet.model.PetInfo;
import com.xiaoma.pet.model.PetMapInfo;
import com.xiaoma.pet.ui.view.PetToast;
import com.xiaoma.thread.ThreadDispatcher;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/22 0022 15:58
 *   desc:   任务地图信息处理
 * </pre>
 */
public final class MapInfoHandler {


    private MapInfoHandler() {

    }

    public static MapInfoHandler getInstance() {
        return Holder.MAP_INFO_HANDLER;
    }

    public void request(final Context context, final PassChapterObserver passChapterObserver) {
        PetToast.showLoading(context, R.string.pet_loading_text);
        RequestManager.getPetInfo(new ResultCallback<XMResult<PetInfo>>() {
            @Override
            public void onSuccess(final XMResult<PetInfo> result) {
                ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PetInfo petInfo = result.getData();
                        SavePetInfoUtils.save(petInfo);
                        handleMapInfo(context, petInfo, passChapterObserver);
                    }
                }, 500);
            }

            @Override
            public void onFailure(int code, String msg) {
                PetToast.dismissLoading();
                PetToast.showException(context, R.string.no_network);
            }
        });
    }

    private void handleMapInfo(final Context context, final PetInfo petInfo, final PassChapterObserver passChapterObserver) {
        RequestManager.getGameChapterInfo("V1.0", petInfo.getChapterId(), new ResultCallback<XMResult<PetMapInfo>>() {
            @Override
            public void onSuccess(XMResult<PetMapInfo> result) {
                PetToast.dismissLoading();
                PetMapInfo petMapInfo = result.getData();
                if (petMapInfo == null) {
                    PetToast.showException(context, R.string.map_data_exception);
                    return;
                }
                int totalKm = Integer.parseInt(petMapInfo.getMileage());
                int surplusKM = ConvertMapTimeCoordinate.getSurplusKM(totalKm, petInfo.getTimeAccumulation());
                if (passChapterObserver != null) {
                    passChapterObserver.passChapter(surplusKM <= 0, petInfo, petMapInfo);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                PetToast.dismissLoading();
                PetToast.showException(context, R.string.no_network);
            }
        });
    }

    private static class Holder {
        private static final MapInfoHandler MAP_INFO_HANDLER = new MapInfoHandler();
    }
}
