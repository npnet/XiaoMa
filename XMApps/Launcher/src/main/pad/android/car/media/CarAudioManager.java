//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package android.car.media;

import android.annotation.SystemApi;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;

public final class CarAudioManager  {
    public static final int VOLUME_ADJUST_DOWN = 0;
    public static final int VOLUME_ADJUST_UP = 1;
    private static final String VOLUME_SETTINGS_KEY_FOR_GROUP_PREFIX = "android.car.VOLUME_GROUP/";
    public static final String VOLUME_SETTINGS_KEY_MASTER_MUTE = "android.car.MASTER_MUTE";

    public static String getVolumeSettingsKeyForGroup(int groupId) {
        return "android.car.VOLUME_GROUP/" + groupId;
    }

    @SystemApi
    public void registerVolumeChangeObserver(ContentObserver observer) {
    }

    @SystemApi
    public void unregisterVolumeChangeObserver(ContentObserver observer) {
    }

    @SystemApi
    public void setGroupVolume(int groupId, int index, int flags) throws Exception {
    }

    public void adjustCarVolume(int direction, int flags) throws Exception {
    }

    public void setCarVolume(int volume, int flags) throws Exception {
    }

    public int getCarVolumeGroupId() throws Exception {
        return 0;
    }

    @SystemApi
    public int getGroupMaxVolume(int groupId) throws Exception {
        return 0;
    }

    @SystemApi
    public int getGroupMinVolume(int groupId) throws Exception {
        return 0;
    }

    @SystemApi
    public int getGroupVolume(int groupId) throws Exception {
        return 0;
    }

    @SystemApi
    public boolean isMasterMute() throws Exception {
        return false;
    }

    @SystemApi
    public void setMasterMute(boolean mute, int flags) throws Exception {
    }

    @SystemApi
    public void setFadeTowardFront(float value) throws Exception {
    }

    @SystemApi
    public void setBalanceTowardRight(float value) throws Exception {
    }

    @SystemApi
    public String[] getExternalSources() throws Exception {
        return null;
    }

    @SystemApi
    public Object createAudioPatch(String sourceAddress, int usage, int gainInMillibels) throws Exception {
        return null;
    }

    @SystemApi
    public void releaseAudioPatch(Object patch) throws Exception {
    }

    @SystemApi
    public int getVolumeGroupCount() throws Exception {
        return 0;
    }

    @SystemApi
    public int getVolumeGroupIdForUsage(int usage) throws Exception {
        return 0;
    }

    @SystemApi
    public int[] getUsagesForVolumeGroupId(int groupId) throws Exception {
        return null;
    }

    @SystemApi
    public void registerVolumeCallback(IBinder binder) throws Exception {
    }

    @SystemApi
    public void unregisterVolumeCallback(IBinder binder) throws Exception {
    }

    public void onCarDisconnected() {
    }

    public CarAudioManager(IBinder service, Context context, Handler handler) {
    }
}
