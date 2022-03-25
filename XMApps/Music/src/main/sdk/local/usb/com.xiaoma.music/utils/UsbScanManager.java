package com.xiaoma.music.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.xiaoma.music.Music;
import com.xiaoma.music.callback.UsbFileSearchListener;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.common.manager.MusicDbManager;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.TimeUtils;
import com.xiaoma.utils.receiver.UsbDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zs
 * @date 2018/10/25 0025.
 */
public class UsbScanManager {
    private static final String NOTIFY_DUALSCREEN_FINISH_ACTION = "com.xiaoma.dualscreen.scanusb.finish";
    private static final String NOTIFY_DUALSCREEN_FINISH_KEY = "usb_size";
    private CopyOnWriteArrayList<UsbFileSearchListener> listeners = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<UsbMusic> usbMusicList = new CopyOnWriteArrayList<>();

    public static UsbScanManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final UsbScanManager instance = new UsbScanManager();
    }

    private static final Set<String> musicTypeSet = new HashSet<>(Arrays.asList(MusicConstants.AUDIO_MP3,
            MusicConstants.AUDIO_AAC, MusicConstants.AUDIO_WMA, MusicConstants.AUDIO_WAV));


    public List<UsbMusic> getUsbMusicList() {
        return new ArrayList<>(usbMusicList);
    }


    public void releaseMusicList() {
        if (usbMusicList != null) {
            usbMusicList.clear();
        }
    }

    public ArrayList<UsbMusic> scanAudioFiles(Context context) {
        ArrayList<UsbMusic> usbMusics = new ArrayList<>();
        UsbMusic usbMusic;
        //查询媒体数据库
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media
                .DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return null;
        }
        //遍历媒体数据库
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //标题
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //显示的名称
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                //专辑
                int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //歌手
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //文件路径
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //时长
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //文件大小
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                String bitmapPath = getAlbumBitmapPath(context, albumId);

                String suffix = displayName.substring(displayName.length() - 4);
                //如果后缀为mp3，将其存入到map集合中
                if (suffix.endsWith(".mp3") || suffix.endsWith(".MP3")) {
                    usbMusic = new UsbMusic();
//                    usbMusic.setId(id);
                    usbMusic.setName(title);
                    usbMusic.setDisplayName(displayName);
                    usbMusic.setAlbum(album);
                    usbMusic.setArtist(artist);
                    usbMusic.setDuration(duration);
                    usbMusic.setPath(url);
                    usbMusic.setSize(size);
                    usbMusics.add(usbMusic);
                    usbMusic.setIconUrl(bitmapPath);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        //返回存储数据的集合
        return usbMusics;
    }

    private String getAlbumBitmapPath(Context context, int albumId) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cursor = context.getContentResolver()
                .query(Uri.parse(mUriAlbums + File.separator + albumId), projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        String bitmapPath = null;
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
            cursor.moveToNext();
            bitmapPath = cursor.getString(0);
        }
        cursor.close();
        return bitmapPath;
    }

    public void scanUsbMediaData(final String rootPath) {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                getUsbMediaData(rootPath);
            }
        });
    }

    private void getUsbMediaData(String rootPath) {
        List<File> fileList = new ArrayList<>();
        scanFileList(rootPath, fileList);
        ArrayList<UsbMusic> musicList = new ArrayList<>();
        for (File file : fileList) {
            UsbMusic usbMusic = new UsbMusic();
            String name = file.getName();
            int index = name.lastIndexOf(".");
            usbMusic.setName(name.substring(0, index));
            usbMusic.setPath(file.getPath());
            musicList.add(usbMusic);
        }
        if (UsbDetector.getInstance().isRemoveState()) {
            return;
        }
        usbMusicList.clear();
        usbMusicList.addAll(musicList);
        for (UsbFileSearchListener listener : listeners) {
            if (listener != null) {
                listener.onUsbMusicScanFinished(musicList);
            }
        }
        for (int i = 0; i < fileList.size(); i++) {
            if (UsbDetector.getInstance().isRemoveState()) {
                break;
            }
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                File file = fileList.get(i);
                UsbMusic usbMusic = musicList.get(i);
                if (!file.exists()) {
                    break;
                }
                usbMusic.setPath(file.getAbsolutePath());
                retriever.setDataSource(file.getAbsolutePath());
                usbMusic.setArtist(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                usbMusic.setAlbum(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                usbMusic.setGenre(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long time = 0;
                if (!TextUtils.isEmpty(duration)) {
                    time = TimeUtils.timeMsToSec(Long.valueOf(duration));
                }
                usbMusic.setDuration(time);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                retriever.release();
            }
        }
        if (UsbDetector.getInstance().isRemoveState()) {
            return;
        }
        usbMusicList.clear();
        usbMusicList.addAll(musicList);
        for (UsbFileSearchListener listener : listeners) {
            if (listener != null) {
                listener.onUsbMusicAnalyticFinished(musicList);
            }
        }
        //双屏需要，扫描完成发送一个广播通知
        Intent intent = new Intent();
        intent.setAction(NOTIFY_DUALSCREEN_FINISH_ACTION);
        intent.addFlags(0x01000000);
        intent.putExtra(NOTIFY_DUALSCREEN_FINISH_KEY, usbMusicList.size());
        Music.getInstance().getApplication().sendBroadcast(intent);
    }

    /**
     * 递归遍历音乐文件夹
     */
    public void scanFileList(String strPath, List<File> fileList) {
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanFileList(file.getAbsolutePath(), fileList);
            } else {
                String fileName = file.getName().toLowerCase();
                int dotIndex = fileName.lastIndexOf(".");
                String fileType = fileName.substring(dotIndex + 1);
                if (musicTypeSet.contains(fileType)) {
                    fileList.add(file);
                }
            }
        }
    }

    public UsbMusic getUsbMusicByPath(String path) {
        UsbMusic usbMusic = null;
        if (!ListUtils.isEmpty(usbMusicList) && !TextUtils.isEmpty(path)) {
            for (UsbMusic music : usbMusicList) {
                if (music.getPath().equals(path)) {
                    usbMusic = music;
                    break;
                }
            }
        }
        return usbMusic;
    }


    public void addUsbScanListener(UsbFileSearchListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeUsbScanListener(UsbFileSearchListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }
}
