package com.xiaoma.cariflytek.iat;

import android.content.Context;
import android.os.Environment;

import com.xiaoma.cariflytek.WakeUpInfo;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.recorder.RecorderType;
import com.xiaoma.vr.recorder.RingBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author: iSun
 * @date: 2019/3/28 0028
 * 自定义oneshot音频处理
 */
public class IatAudioProcess {
    private static final String TAG = IatAudioProcess.class.getSimpleName();
    private boolean isSavePcm = false;
    private RingBuffer ivwBufferLeft = new RingBuffer(VrConfig.RINGBUFFER_SIZE);
    private RingBuffer ivwBufferRight = new RingBuffer(VrConfig.RINGBUFFER_SIZE);
    //    private RingBuffer<Byte> iatLeftBuffer = new RingBuffer<>(BUFFER_SIZE);
//    private RingBuffer<Byte> iatRightBuffer = new RingBuffer<>(BUFFER_SIZE);
    private RecorderType recorderType = RecorderType.IVW;
    private int HEAD_OFFSET = 0;//音频采样率偏移量
    private static IatAudioProcess instance;
    private IatProcessListener listener;
    private Context mContext;
    //    BlockingQueue<Byte> iatData = new ArrayBlockingQueue<>(BUFFER_SIZE * 4);
    private volatile int startByte;
    private volatile int mvwScene;
    private volatile int endByte;

    //log
    private FileOutputStream mFosIvw;
    private FileOutputStream mFosIat;
    private FileOutputStream mFosProcess;
    private int processCount = 0;
    private int iatCount = 0;
    private int ivwCount = 0;
    private String logPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/iflytek/xm/";
    private File fileIvw;
    private File fileIat;
    private File fileProcess;


    public static IatAudioProcess getInstance() {
        if (instance == null) {
            synchronized (IatAudioProcess.class) {
                if (instance == null) {
                    instance = new IatAudioProcess();
                }
            }
        }
        return instance;
    }

    private IatAudioProcess() {
        if (isSavePcm) {
            try {
                if (fileIvw == null) {
                    fileIvw = new File(VrConfig.IFLY_TEK_RES + "/ivw.pcm");
                }
                if (fileIat == null) {
                    fileIat = new File(VrConfig.IFLY_TEK_RES + "/iat.pcm");
                }
                if (fileProcess == null) {
                    fileProcess = new File(VrConfig.IFLY_TEK_RES + "/process.pcm");
                }
                mFosIvw = new FileOutputStream(fileIvw);
                mFosIat = new FileOutputStream(fileIat);
                mFosProcess = new FileOutputStream(fileProcess);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void appendIvwRingBuffer(byte[] left, byte[] right) {
        ivwBufferLeft.putBytes(left);
        ivwBufferRight.putBytes(right);
    }


    public synchronized void addBufferToProcess(byte[] buffer, byte[] left, byte[] right) {
        dispatchAudioData(buffer, left, right);
    }

    public void startWakeUp() {
        KLog.d(TAG, " startWakeUp : " + ivwBufferLeft.getIndex());
        clearIvwRingBuffer();
        clearFlag();
        recorderType = RecorderType.IVW;
    }

    public void stopWakeUp() {
        recorderType = RecorderType.IVW;
        KLog.d(TAG, " stopWakeUp : ");
        try {
            if (mFosIvw != null) {
                mFosIvw.close();
                mFosIvw = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFlag() {
        KLog.d(TAG, " clearFlag : ");
        startByte = -1;
        endByte = -1;
        mvwScene = -1;
        mFosProcess = null;
    }


    private byte[] getSubBuffer(byte[] buffer, int start, int end) {
        byte[] temp = new byte[end - start + 1];
        int count = 0;
        while (end - start > 0 && count < temp.length) {
            try {
                if (start >= VrConfig.RINGBUFFER_SIZE) {
                    start = (start) % VrConfig.RINGBUFFER_SIZE;
                }
                temp[count++] = buffer[start];
                start++;
            } catch (Exception e) {
                KLog.e(TAG, " getSubBuffer :error " + e.getMessage());
                e.printStackTrace();
            }
        }
        return temp;
    }


    public void onWakeUp(WakeUpInfo info) {
        KLog.d(TAG, " onWakeUp : " + GsonHelper.toJson(info));
        if (info != null) {
            mvwScene = info.getnMvwScene();
            startByte = info.getnStartBytes() + HEAD_OFFSET;
            endByte = info.getnEndBytes() + HEAD_OFFSET;
        } else {
            clearFlag();
        }
    }

    public void setProcessAudioDataListener(IatProcessListener listener) {
        this.listener = listener;
    }

    public void handleIatAudioData() {
        recorderType = RecorderType.IAT;
        if (mvwScene != VrConfig.ONESHOT_MVW_SCENE || startByte <= 0 || endByte <= 0) {
            KLog.d(TAG, "  handleIatAudioData :clear ");
            clearFlag();
            clearIvwRingBuffer();
        } else {
            KLog.d(TAG, "  handleIatAudioData :processIatHead ");
            processIatHead();
        }
    }

    public void processIatHead() {
        mvwScene = -1;
        try {
            byte[] leftBuffer = ivwBufferLeft.getBuffer();
            byte[] rightBuffer = ivwBufferRight.getBuffer();
            if (leftBuffer.length == rightBuffer.length) {
                int end = endByte;
                if (ivwBufferLeft.getIndex() >= endByte) {
                    end = ivwBufferLeft.getIndex() - 1;
                }
//                KLog.e(TAG, " processIatHead  type:" + VrAidlManager.getInstance().getRecorderType() + "   endByte  " + endByte + "  end:" + end);
                byte[] resultLeft = getSubBuffer(leftBuffer, startByte, end);
                byte[] resultRight = getSubBuffer(rightBuffer, startByte, end);
                dispatchAudioData(resultLeft, resultLeft, resultRight);
                clearIvwRingBuffer();
                clearFlag();
//                iatData.addAll(toObjects(resultLeft));
                saveProcessPcm(resultLeft, resultRight);
            } else {
                KLog.e(TAG, " iat audio error");
            }
        } catch (Exception e) {
            KLog.e(TAG, " processIatHead error :" + e.getMessage());
            e.printStackTrace();
        }
    }

    private Collection<? extends Byte> toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];

        int i = 0;
        for (byte b : bytesPrim) bytes[i++] = b;
        return Arrays.asList(bytes);
    }

    private void clearIvwRingBuffer() {
        KLog.d(TAG, " clearIvwRingBuffer : ");
        if (ivwBufferLeft.getIndex() != 0) {
            ivwBufferLeft.clear();
        }
        if (ivwBufferRight.getIndex() != 0) {
            ivwBufferRight.clear();
        }
    }


    private void saveProcessPcm(byte[] leftBuffer, byte[] rightBuffer) {
        if (isSavePcm) {
            if (mFosProcess == null) {
                try {
                    mFosProcess = new FileOutputStream(new File(logPath + "/process" + processCount++ + ".pcm"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            try {
                mFosProcess.write(leftBuffer);
                mFosProcess.flush();
                mFosProcess.close();
                mFosProcess = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public byte[] toLittle(byte[] data) {
        int dataLength = data.length;
        ByteBuffer byteBuffer = ByteBuffer.wrap(data, 0, dataLength).order(ByteOrder.LITTLE_ENDIAN);
        return byteBuffer.array();
    }


    private synchronized void dispatchAudioData(byte[] buffer, byte[] left, byte[] right) {
        if (recorderType == RecorderType.IVW) {
            saveIvwPcm(left, right);
            appendIvwRingBuffer(left, right);
        } else {
            saveProcessIatPcm(left, right);
//            iatData.addAll(toObjects(left));
        }
        if (listener != null) {
            listener.onIatHeadProcess(buffer, left, right);
        }
    }


    public void stopIat() {
        KLog.d(TAG, " stopIat : ");
        recorderType = RecorderType.IVW;
        try {
            if (mFosIat != null) {
                mFosIat.close();
                mFosIat = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startIat() {
        recorderType = RecorderType.IAT;
        KLog.d(TAG, " startIat : ");
    }

    public void init(Context context) {
        if (isSavePcm) {
            File file = new File(logPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    public void saveIatPcm(byte[] leftBuffer, byte[] rightBuffer) {
        if (isSavePcm) {
            if (mFosIat == null) {
                try {
                    mFosIat = new FileOutputStream(new File(logPath + "/iat" + iatCount++ + ".pcm"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            try {
                mFosIat.write(leftBuffer);
                mFosIat.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveProcessIatPcm(byte[] leftBuffer, byte[] rightBuffer) {
        if (isSavePcm) {
            if (mFosIat == null) {
                try {
                    mFosIat = new FileOutputStream(new File(logPath + "/iatProcess" + iatCount++ + ".pcm"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            try {
                mFosIat.write(leftBuffer);
                mFosIat.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveIvwPcm(byte[] leftBuffer, byte[] rightBuffer) {
        if (isSavePcm) {
            if (mFosIvw == null) {
                try {
                    mFosIvw = new FileOutputStream(new File(logPath + "/ivw" + ivwCount++ + ".pcm"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            try {
                mFosIvw.write(leftBuffer);
                mFosIvw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public interface IatProcessListener {
        public void onIatHeadProcess(byte[] buffer, byte[] left, byte[] right);
    }
}
