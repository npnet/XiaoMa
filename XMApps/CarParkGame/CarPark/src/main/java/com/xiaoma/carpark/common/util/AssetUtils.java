package com.xiaoma.carpark.common.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetUtils {

    private static final String TAG = AssetUtils.class.getSimpleName();
    private static final int BYTE_BUF_SIZE = 2048;

    public static void copy(Context context, String assetName, String targetName) throws IOException {
        Log.d(TAG, "creating file " + targetName + " from " + assetName);

        File targetFile = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            AssetManager assets = context.getAssets();
            targetFile = new File(targetName);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }

            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            inputStream = assets.open(assetName);

            // TODO(kanlig): refactor log messages to make them more useful.
            Log.d(TAG, "Creating outputstream");
            outputStream = new FileOutputStream(targetFile, false /* append */);
            copy(inputStream, outputStream);

        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static void copy(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[BYTE_BUF_SIZE];
        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
        }
    }
}
