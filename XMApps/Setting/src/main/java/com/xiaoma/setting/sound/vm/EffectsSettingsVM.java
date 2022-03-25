package com.xiaoma.setting.sound.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.IVendorExtension;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.utils.DebugUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuboxiang on 2018/10/13 11:10
 * description:
 */
public class EffectsSettingsVM extends AndroidViewModel {

    private static final String TAG = "EffectsSettingsVM";
    public static final int SOUND_EFFECTS_INDEX_0 = 0;
    public static final int SOUND_EFFECTS_INDEX_1 = 1;
    public static final int SOUND_EFFECTS_INDEX_2 = 2;
    public static final int SOUND_EFFECTS_INDEX_3 = 3;
    public static final int SOUND_EFFECTS_INDEX_4 = 4;
    private MutableLiveData<List<Integer>> mStandardEffect;
    private MutableLiveData<List<Integer>> mPopEffect;
    private MutableLiveData<List<Integer>> mClassicEffect;
    private MutableLiveData<List<Integer>> mJazzEffect;
    private MutableLiveData<List<Integer>> mCustomEffect;
    private MutableLiveData<Integer> mSoundEffectsMode;
    private MutableLiveData<Integer> mArkamys3D;
    private MutableLiveData<Boolean> mSubwoofer;
    private MutableLiveData<Integer> mSoundFieldMode;
    private MutableLiveData<Point> mSoundEffectPositionAtAnyPoint;
    private Context mContext;
//    private OnChangeEvent event;

    public EffectsSettingsVM(@NonNull Application application) {
        super(application);
        mContext = application;
//        getSDKManager().addValueChangeListener(this);
    }

    public boolean setSoundEffectsMode(int index) {
        int mode = caseIndexToSoundEffectsMode(index);
        getSDKManager().setSoundEffectsMode(mode);
        getSoundEffectsMode().setValue(index);
        DebugUtils.e(TAG, mContext.getString(R.string.log_sound_effect_mode) + index);
        return true;
    }

    public boolean setCustomSoundEffects(int zoneIndex, int levelIndex) {
        List<Integer> currentEffects = getCustomEffect().getValue();
        if (currentEffects == null || currentEffects.size() <= zoneIndex || currentEffects.get(zoneIndex) == levelIndex)
            return false;
        setCustomListValue(zoneIndex, levelIndex);

        ArrayList<Integer> tempList = new ArrayList<>(getCustomEffect().getValue());
        tempList.add(0, SDKConstants.VALUE.SOUND_EFFECTS_STANDARD);
        getSDKManager().setCustomSoundEffects(tempList.toArray(new Integer[0]));
        DebugUtils.e(TAG, String.format(mContext.getString(R.string.custom_sound_effects_settings), zoneIndex, levelIndex));
        return true;
    }

    public void setCustomSoundEffects(int zoneIndex, int levelIndex, Integer[] integers) {
        integers[zoneIndex + 1] = levelIndex;
        getSDKManager().setCustomSoundEffects(integers);
        DebugUtils.e(TAG, String.format(mContext.getString(R.string.custom_sound_effects_settings), zoneIndex, levelIndex));
    }

    private void setCustomListValue(int index, int value) {
        List<Integer> list = getCustomEffect().getValue();
        list.set(index, value);
        getCustomEffect().setValue(list);
    }

    public void setArkamys3D(int index) {
        int value = caseIndexToArkamys3dValue(index);
        getSDKManager().setArkamys3D(value);
        getArkamys3D().setValue(index);
        DebugUtils.e(TAG, mContext.getString(R.string.Arkamys_3D_sound_effect) + ": " + index);
    }

    public void setSubwoofer(boolean opened) {
        getSDKManager().setSubwoofer(opened);
        getSubwoofer().setValue(opened);
    }

    public boolean setSoundEffectPositionAtAnyPoint(Point point) {
        int x = point.x;
        int y = point.y;
        getSDKManager().setSoundEffectPositionAtAnyPoint(x, y);
        getSoundEffectPositionAtAnyPoint().setValue(point);
        DebugUtils.e(TAG, mContext.getString(R.string.log_sound_effect_position_at_any_point) + "(" + x + "," + y + ")");
        return true;
    }

    public MutableLiveData<Integer> getArkamys3D() {
        if (mArkamys3D == null) {
            mArkamys3D = new MutableLiveData<>();
            mArkamys3D.setValue(caseArkamys3dValueToIndex(getSDKManager().getArkamys3D()));
        }
        return mArkamys3D;
    }

    public MutableLiveData<Boolean> getSubwoofer() {
        if (mSubwoofer == null) {
            mSubwoofer = new MutableLiveData<>();
            mSubwoofer.setValue(getSDKManager().getSubwoofer());
        }
        return mSubwoofer;
    }

    public MutableLiveData<Integer> getSoundFieldMode() {
        if (mSoundFieldMode == null) {
            mSoundFieldMode = new MutableLiveData<>();
            mSoundFieldMode.setValue(castSoundFieldModeToIndex(getSDKManager().getSoundFieldMode()));
        }
        return mSoundFieldMode;
    }

    public MutableLiveData<Point> getSoundEffectPositionAtAnyPoint() {
        if (mSoundEffectPositionAtAnyPoint == null) {
            mSoundEffectPositionAtAnyPoint = new MutableLiveData<>();
            mSoundEffectPositionAtAnyPoint.setValue(getSDKManager().getSoundEffectPositionAtAnyPoint());
        }
        return mSoundEffectPositionAtAnyPoint;
    }

    public MutableLiveData<Integer> getSoundEffectsMode() {
        if (mSoundEffectsMode == null) {
            mSoundEffectsMode = new MutableLiveData<>();
            mSoundEffectsMode.setValue(caseSoundEffectsModeToIndex(getSDKManager().getSoundEffectsMode()));
        }
        return mSoundEffectsMode;
    }

    public List<Integer> getCurrentSoundEffects(int mode) {
        List<Integer> list = getSDKManager().getCurrentSoundEffects(mode);
        String log = "";
        for (int i = 1; i < list.size(); i++) {
            int index = caseSoundEffectsLevelToIndex(list.get(i));
            list.set(i, index);
            log += ", index: " + i +", value: " + list.get(i);
        }
        Log.d("sound_get","sound mode: " + mode +", list value: " + log);
        return list;
    }

    public MutableLiveData<List<Integer>> getStandardEffect() {
        if (mStandardEffect == null) {
            mStandardEffect = new MutableLiveData<>();
            initSoundEffects(SDKConstants.VALUE.SOUND_EFFECTS_STANDARD, mStandardEffect);
        }
        return mStandardEffect;
    }

    public MutableLiveData<List<Integer>> getPopEffect() {
        if (mPopEffect == null) {
            mPopEffect = new MutableLiveData<>();
            initSoundEffects(SDKConstants.VALUE.SOUND_EFFECTS_POP, mPopEffect);
        }
        return mPopEffect;
    }

    public MutableLiveData<List<Integer>> getClassicEffect() {
        if (mClassicEffect == null) {
            mClassicEffect = new MutableLiveData<>();
            initSoundEffects(SDKConstants.VALUE.SOUND_EFFECTS_CLASSIC, mClassicEffect);
        }
        return mClassicEffect;
    }

    public MutableLiveData<List<Integer>> getJazzEffect() {
        if (mJazzEffect == null) {
            mJazzEffect = new MutableLiveData<>();
            initSoundEffects(SDKConstants.VALUE.SOUND_EFFECTS_JAZZ, mJazzEffect);
        }
        return mJazzEffect;
    }

    public MutableLiveData<List<Integer>> getCustomEffect() {
        if (mCustomEffect == null) {
            mCustomEffect = new MutableLiveData<>();
            initSoundEffects(SDKConstants.VALUE.SOUND_EFFECTS_USER, mCustomEffect);
        }
        return mCustomEffect;
    }

    private void initSoundEffects(int mode, final MutableLiveData<List<Integer>> effects) {
        try {
            effects.setValue(getCurrentSoundEffects(mode));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IVendorExtension getSDKManager() {
        return XmCarFactory.getCarVendorExtensionManager();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mStandardEffect = null;
        mPopEffect = null;
        mClassicEffect = null;
        mJazzEffect = null;
        mCustomEffect = null;
        mSoundEffectsMode = null;
        mArkamys3D = null;
        mSubwoofer = null;
        mSoundFieldMode = null;
        mSoundEffectPositionAtAnyPoint = null;
    }

    private int castIndexToSoundFieldMode(int index) {
        int mode;
        switch (index) {
            case 0:
            default:
                mode = SDKConstants.VALUE.STANDARD;
                break;
            case 1:
                mode = SDKConstants.VALUE.CINEMA;
                break;
            case 2:
                mode = SDKConstants.VALUE.ODEUM;
                break;
        }
        return mode;
    }

    private int castSoundFieldModeToIndex(int mode) {
        int index;
        switch (mode) {
            case SDKConstants.VALUE.STANDARD:
            default:
                index = 0;
                break;
            case SDKConstants.VALUE.CINEMA:
                index = 1;
                break;
            case SDKConstants.VALUE.ODEUM:
                index = 2;
                break;
        }
        return index;
    }

    private int caseSoundEffectsLevelToIndex(int level) {
        int index;
        switch (level) {
            case SDKConstants.VALUE.LEVEL_0:
            default:
                index = 0;
                break;
            case SDKConstants.VALUE.LEVEL_1:
                index = 1;
                break;
            case SDKConstants.VALUE.LEVEL_2:
                index = 2;
                break;
            case SDKConstants.VALUE.LEVEL_3:
                index = 3;
                break;
            case SDKConstants.VALUE.LEVEL_4:
                index = 4;
                break;
            case SDKConstants.VALUE.LEVEL_5:
                index = 5;
                break;
            case SDKConstants.VALUE.LEVEL_6:
                index = 6;
                break;
            case SDKConstants.VALUE.LEVEL_7:
                index = 7;
                break;
            case SDKConstants.VALUE.LEVEL_8:
                index = 8;
                break;
            case SDKConstants.VALUE.LEVEL_9:
                index = 9;
                break;
            case SDKConstants.VALUE.LEVEL_10:
                index = 10;
                break;
            case SDKConstants.VALUE.LEVEL_11:
                index = 11;
                break;
            case SDKConstants.VALUE.LEVEL_12:
                index = 12;
                break;
            case SDKConstants.VALUE.LEVEL_13:
                index = 13;
                break;
            case SDKConstants.VALUE.LEVEL_14:
                index = 14;
                break;
        }
        return index;
    }

    private int caseIndexToSoundEffectsLevel(int index) {
        int level;
        switch (index) {
            case 0:
            default:
                level = SDKConstants.VALUE.LEVEL_0;
                break;
            case 1:
                level = SDKConstants.VALUE.LEVEL_1;
                break;
            case 2:
                level = SDKConstants.VALUE.LEVEL_2;
                break;
            case 3:
                level = SDKConstants.VALUE.LEVEL_3;
                break;
            case 4:
                level = SDKConstants.VALUE.LEVEL_4;
                break;
            case 5:
                level = SDKConstants.VALUE.LEVEL_5;
                break;
            case 6:
                level = SDKConstants.VALUE.LEVEL_6;
                break;
            case 7:
                level = SDKConstants.VALUE.LEVEL_7;
                break;
            case 8:
                level = SDKConstants.VALUE.LEVEL_8;
                break;
            case 9:
                level = SDKConstants.VALUE.LEVEL_9;
                break;
            case 10:
                level = SDKConstants.VALUE.LEVEL_10;
                break;
            case 11:
                level = SDKConstants.VALUE.LEVEL_11;
                break;
            case 12:
                level = SDKConstants.VALUE.LEVEL_12;
                break;
            case 13:
                level = SDKConstants.VALUE.LEVEL_13;
                break;
            case 14:
                level = SDKConstants.VALUE.LEVEL_14;
                break;
        }
        return level;
    }

    private int caseIndexToSoundEffectsMode(int index) {
        int mode;
        switch (index) {
            case 0:
            default:
                mode = SDKConstants.VALUE.SOUND_EFFECTS_STANDARD;
                break;
            case 1:
                mode = SDKConstants.VALUE.SOUND_EFFECTS_POP;
                break;
            case 2:
                mode = SDKConstants.VALUE.SOUND_EFFECTS_CLASSIC;
                break;
            case 3:
                mode = SDKConstants.VALUE.SOUND_EFFECTS_JAZZ;
                break;
            case 4:
                mode = SDKConstants.VALUE.SOUND_EFFECTS_USER;
                break;
        }
        return mode;
    }

    public int caseSoundEffectsModeToIndex(int mode) {
        KLog.d(TAG, "caseSoundEffectsModeToIndex : " + mode);
        int index;
        switch (mode) {
            case SDKConstants.VALUE.SOUND_EFFECTS_STANDARD:
                index = 0;
                break;
            case SDKConstants.VALUE.SOUND_EFFECTS_POP:
                index = 1;
                break;
            case SDKConstants.VALUE.SOUND_EFFECTS_CLASSIC:
                index = 2;
                break;
            case SDKConstants.VALUE.SOUND_EFFECTS_JAZZ:
                index = 3;
                break;
            case SDKConstants.VALUE.SOUND_EFFECTS_USER:
                index = 4;
                break;
            default:
                index = 0;
                break;
        }
        return index;
    }

    public int caseArkamys3dValueToIndex(int value) {
        int index;
        switch (value) {
            case SDKConstants.VALUE.ARKAMYS_3D_ALL_ON:
            default:
                index = 0;
                break;
            case SDKConstants.VALUE.ARKAMYS_3D_DRV_ON:
                index = 1;
                break;
            case SDKConstants.VALUE.ARKAMYS_3D_OFF:
                index = 2;
                break;
        }
        return index;
    }

    private int caseIndexToArkamys3dValue(int index) {
        int mode;
        switch (index) {
            case 0:
            default:
                mode = SDKConstants.VALUE.ARKAMYS_3D_ALL_ON;
                break;
            case 1:
                mode = SDKConstants.VALUE.ARKAMYS_3D_DRV_ON;
                break;
            case 2:
                mode = SDKConstants.VALUE.ARKAMYS_3D_OFF;
                break;
        }
        return mode;
    }

    /*@Override
    public void onChange(int id, Object value) {
        if (event != null) {
            event.onChange(id, value);
        }
    }

    public void setOnChangeEvent(OnChangeEvent event) {
        this.event = event;
    }

    public interface OnChangeEvent {
        void onChange(int id, Object value);
    }*/
}
