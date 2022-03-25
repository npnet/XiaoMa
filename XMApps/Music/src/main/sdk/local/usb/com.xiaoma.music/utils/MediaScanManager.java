package com.xiaoma.music.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.xiaoma.music.callback.UsbFileSearchListener;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.TimeUtils;
import com.xiaoma.utils.receiver.UsbDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ZYao.
 * Date ：2019/1/4 0004
 */
public class MediaScanManager {

    private String mPath;
    private UsbFileSearchListener mScanListener;
    private static final Set<String> musicTypeSet = new HashSet<>(Arrays.asList(MusicConstants.AUDIO_MP3,
            MusicConstants.AUDIO_AAC, MusicConstants.AUDIO_WMA, MusicConstants.AUDIO_WAV));
    private ScanUsbFilesReceiver scanReceiver;
    private boolean isRegister = false;
    private boolean isHandScan = false;

    public static MediaScanManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final MediaScanManager instance = new MediaScanManager();
    }

    public void scanUsbMedia(Context context, String path, final UsbFileSearchListener listener) {
        if (!path.equalsIgnoreCase(mPath)) {
            mPath = path;
        }
        if (listener != null) {
            mScanListener = listener;
        }
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addDataScheme("file");
        scanReceiver = new ScanUsbFilesReceiver();
        context.registerReceiver(scanReceiver, intentFilter);
        scanFile(context, path);
        isRegister = true;
    }

    public void setHandScan(boolean handScan) {
        isHandScan = handScan;
    }

    private void scanFile(Context context, String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 判断SDK版本是不是4.4或者高于4.4
            String[] paths = new String[]{path};
            MediaScannerConnection.scanFile(context, paths, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    if (mScanListener != null && isHandScan) {
                        scanAudioFiles(context, path, mScanListener);
                        isHandScan = false;
                    }
                }
            });
        } else {
            final Intent intent;
            File usbRootFile = new File(path);
            if (usbRootFile.isDirectory()) {
                intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver");
                intent.setData(Uri.fromFile(usbRootFile));
            } else {
                intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(usbRootFile));
            }
            context.sendBroadcast(intent);
        }
    }

    public void unRegisterReceiver(Context context) {
        if (scanReceiver != null && isRegister) {
            context.unregisterReceiver(scanReceiver);
            isRegister = false;
        }
    }

    private class ScanUsbFilesReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
                ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
                    @Override
                    public void run() {
                        scanAudioFiles(context, mPath == null ? "" : mPath, mScanListener);
                    }
                });
                unRegisterReceiver(context);
            }
        }
    }

    private void scanAudioFiles(Context context, String path, UsbFileSearchListener mScanListener) {
        ArrayList<UsbMusic> usbMusics = new ArrayList<>();
        //查询媒体数据库
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return;
        }
        //遍历媒体数据库
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                if (url.contains(path)) {
                    UsbMusic usbMusic = new UsbMusic();
                    File file = new File(url);
                    String name = file.getName();
                    int index = name.lastIndexOf(".");
                    usbMusic.setName(name.substring(0, index));
                    usbMusic.setAlbum(album);
                    usbMusic.setArtist(artist);
                    usbMusic.setDuration(TimeUtils.timeMsToSec(duration));
                    usbMusic.setPath(url);
                    usbMusic.setSize(size);
                    if (musicTypeSet.contains(getFileType(name, index))) {
                        usbMusics.add(usbMusic);
                    }
                }
                cursor.moveToNext();
            }
            if (mScanListener != null && !UsbDetector.getInstance().isRemoveState()) {
                mScanListener.onUsbMusicAnalyticFinished(usbMusics);
            }
        }
        cursor.close();
    }

    @NonNull
    private String getFileType(String name, int index) {
        String fileName = name.toLowerCase();
        return fileName.substring(index + 1);
    }
}
