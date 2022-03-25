package com.xiaoma.music.common.util;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.common.model.UsbMusicRecord;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.thread.BgThreadFactory;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.tputils.TPUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/7/15 0015 17:30
 *   desc:   usb记忆
 * </pre>
 */
public class UsbMusicRecordManager {

    private static final String USB_MEMORY_CONFIG_FILE = "usb_memory.xmcfg";
    private static final String USB_MEMORY_BACKUP_KEY = "USB_MEMORY_BACKUP_KEY";
    private static final long LOOP_MEMORY_INTERVAL_TIME = 2000;
    private ReentrantLock reentrantLock = new ReentrantLock();
    private Context context;

    private static volatile UsbMusicRecordManager usbMusicRecordManager;
    private File mUsbMemoryFile;
    private ScheduledExecutorService scheduledExecutorService;


    private UsbMusicRecordManager() {
        String path = ConfigManager.FileConfig.getGlobalConfigFolder() + File.separator + USB_MEMORY_CONFIG_FILE;
        mUsbMemoryFile = new File(path);
        if (!mUsbMemoryFile.exists()) {
            try {
                mUsbMemoryFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static UsbMusicRecordManager getInstance() {
        if (usbMusicRecordManager == null) {
            synchronized (UsbMusicRecordManager.class) {
                if (usbMusicRecordManager == null) {
                    usbMusicRecordManager = new UsbMusicRecordManager();
                }
            }
        }
        return usbMusicRecordManager;
    }


    public void initApplicationAttribute(Context context) {
        this.context = context;
    }

    public void startUsbMemory() {
        if (scheduledExecutorService != null) {
            return;
        }
        //TODO 轮询记忆当前播放的usb music
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new BgThreadFactory());
        scheduledExecutorService.scheduleAtFixedRate(this::handlePlayingUsbMusic,
                LOOP_MEMORY_INTERVAL_TIME,
                LOOP_MEMORY_INTERVAL_TIME,
                TimeUnit.MILLISECONDS);
    }

    public void stopUsbMemory() {
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = null;
        }
    }

    public UsbMusic getUsbMusicFromRecord() {
        UsbMusicRecord usbMusicRecord = readUsbMusicRecordFromFile();
        if (usbMusicRecord == null) {
            return null;
        }
        return usbMusicRecord.getUsbMusic();
    }

    public long getCurrentPositionIfExists() {
        UsbMusicRecord usbMusicRecord = readUsbMusicRecordFromFile();
        if (usbMusicRecord == null) {
            return -1;
        }
        return usbMusicRecord.getCurPosition();
    }

    public int getPlayMode() {
        reentrantLock.lock();
        UsbMusicRecord usbMusicRecord = readUsbMusicRecordFromFile();
        reentrantLock.unlock();

        if (usbMusicRecord == null) {
            return -1;
        }
        return usbMusicRecord.getPlayMode();
    }

    private UsbMusicRecord readUsbMusicRecordFromFile() {
        String rawData = FileUtils.read(mUsbMemoryFile);
        if (TextUtils.isEmpty(rawData)) {
            UsbMusicRecord cacheRecord = TPUtils.getObject(context, USB_MEMORY_BACKUP_KEY, UsbMusicRecord.class);
            if (cacheRecord != null && cacheRecord.getUsbMusic() != null) {
                return cacheRecord;
            }
            return null;
        }
        return GsonHelper.fromJson(rawData, UsbMusicRecord.class);
    }


    private void writeUsbMusicRecordToFile(UsbMusicRecord usbMusicRecord) {
        String usbMusicJson = GsonHelper.toJson(usbMusicRecord, UsbMusicRecord.class);
        if (TextUtils.isEmpty(usbMusicJson)) {
            return;
        }

        TPUtils.putObject(context, USB_MEMORY_BACKUP_KEY, usbMusicRecord);
        FileUtils.write(usbMusicJson, mUsbMemoryFile, false);
    }


    private void handlePlayingUsbMusic() {
        if (UsbMusicFactory.getUsbPlayerProxy().isPlaying()) {
            UsbMusic usbMusic = UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic();
            long currentPos = UsbMusicFactory.getUsbPlayerProxy().getCurPosition();
            int playMode = UsbMusicFactory.getUsbPlayerListProxy().getPlayMode();

            UsbMusicRecord usbMusicRecord = new UsbMusicRecord();
            usbMusicRecord.setUsbMusic(usbMusic);
            usbMusicRecord.setCurPosition(currentPos);
            usbMusicRecord.setPlayMode(playMode);

            reentrantLock.lock();
            writeUsbMusicRecordToFile(usbMusicRecord);
            reentrantLock.unlock();
        }
    }

}
