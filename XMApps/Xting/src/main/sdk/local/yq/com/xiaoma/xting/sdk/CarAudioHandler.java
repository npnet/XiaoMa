/**
 * Copyright (C) 2018 The Android Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaoma.xting.sdk;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.media.CarAudioManager;
import android.car.media.CarAudioPatchHandle;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.media.AudioAttributes;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CarAudioHandler {
    private static final String HARD_CODED_TUNER_ADDRESS = "tuner0";
    private final Object lock = new Object();
    private final Car car;
    @Nullable
    private CarAudioManager carAudioManager;
    @Nullable
    private CarAudioPatchHandle audioPatch; // 每次获取音频时都会生成一个, 可以看做是能否播放的一个标志, 为null说明没有
    @Nullable
    private Boolean pendingMuteOperation; // 当还未连接成功时调用了setMuted方法,保留参数,等到连接成功时再设置
    private boolean isConnected = false;

    private final ServiceConnection carConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isConnected = true;
            synchronized (lock) {
                try {
                    carAudioManager = (CarAudioManager) car.getCarManager(Car.AUDIO_SERVICE);
                    if (pendingMuteOperation != null) {
                        boolean mute = pendingMuteOperation;
                        pendingMuteOperation = null;
                        setMuted(mute);
                    }
                } catch (CarNotConnectedException e) {
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnected = false;
            synchronized (lock) {
                carAudioManager = null;
                audioPatch = null;
            }
        }
    };

    // 包可读权限
    CarAudioHandler(Context context) {
        car = Car.createCar(context, carConn);
        car.connect();
    }

    // 设置是否静音
    public boolean setMuted(boolean muted) {
        synchronized (lock) {
            if (carAudioManager == null) {
                // 还没有连接成功,仅保留标志位
                pendingMuteOperation = muted;
                return true;
            }

            // 如果已经设置了对应的参数则不需要再次设置
            if ((audioPatch == null) == muted) {
                return true;
            }

            try {
                if (!muted) {
                    if (!isSourceAvailableLocked(HARD_CODED_TUNER_ADDRESS)) {
                        return false;
                    }
                    audioPatch = carAudioManager.createAudioPatch(
                            HARD_CODED_TUNER_ADDRESS,
                            AudioAttributes.USAGE_MEDIA,
                            0);
                } else {
                    carAudioManager.releaseAudioPatch(audioPatch);
                    audioPatch = null;
                }
                return true;
            } catch (CarNotConnectedException e) {
                return false;
            }
        }
    }

    // 判断是否静音
    public boolean isMuted() {
        synchronized (lock) {
            if (pendingMuteOperation != null) {
                return pendingMuteOperation;
            }
            return audioPatch == null;
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    // 断开服务
    public void close() {
        car.disconnect();
        isConnected = false;
    }

    private boolean isSourceAvailableLocked(@NonNull String address) throws CarNotConnectedException {
        String[] sources = carAudioManager.getExternalSources();
        // return Stream.of(sources).anyMatch(source -> address.equals(source));
        if (sources == null || sources.length <= 0) {
            return false;
        }
        for (String source : sources) {
            if (source == null) {
                continue;
            }
            if (address.equals(source)) {
                return true;
            }
        }
        return false;
    }
}
