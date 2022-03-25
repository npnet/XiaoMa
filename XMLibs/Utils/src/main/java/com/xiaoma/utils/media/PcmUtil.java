package com.xiaoma.utils.media;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by LKF on 2019-4-9 0009.
 */
public class PcmUtil {
    private static final String AAC_MIME_TYPE = MediaFormat.MIMETYPE_AUDIO_AAC;
    private static final int AAC_PROFILE = MediaCodecInfo.CodecProfileLevel.AACObjectLC;

    /**
     * pcm转aac格式
     *
     * @param inFile     pcm文件
     * @param outFile    输出文件
     * @param sampleRate 采样率
     */
    public static void pcm2Aac(File inFile, File outFile, int sampleRate, int channelCount) throws Exception {
        MediaCodec codec = null;
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            MediaFormat mediaFormat = new MediaFormat();
            mediaFormat.setString(MediaFormat.KEY_MIME, AAC_MIME_TYPE);
            mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, AAC_PROFILE);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, (int) (sampleRate * 1.525f));
            mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channelCount);
            mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate);

            codec = MediaCodec.createEncoderByType(AAC_MIME_TYPE);
            codec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            codec.start();

            inputStream = new FileInputStream(inFile);
            outputStream = new FileOutputStream(outFile);
            byte[] buf = new byte[4096];
            int readLen;

            while ((readLen = inputStream.read(buf)) > 0) {
                // 写入pcm流
                int inputBufferIndex = codec.dequeueInputBuffer(-1);
                if (inputBufferIndex >= 0) {
                    ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferIndex);
                    inputBuffer.clear();
                    inputBuffer.put(buf);
                    inputBuffer.limit(readLen);
                    codec.queueInputBuffer(inputBufferIndex, 0, readLen, System.nanoTime(), 0);
                }
                // 输出编码流
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                int outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, 0);
                while (outputBufferIndex >= 0) {
                    ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferIndex);
                    // 给adts头字段空出7的字节
                    int length = bufferInfo.size + 7;
                    byte[] frameByte = new byte[length];
                    addADTStoPacket(frameByte, length, sampleRate, channelCount);
                    outputBuffer.get(frameByte, 7, bufferInfo.size);
                    outputStream.write(frameByte, 0, length);
                    codec.releaseOutputBuffer(outputBufferIndex, false);
                    outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, 0);
                }
            }
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception ignored) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception ignored) {
                }
            }
            if (codec != null) {
                try {
                    codec.release();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * 添加头部信息
     **/
    public static void addADTStoPacket(byte[] packet, int packetLen, int sampleRate, int chanCfg /*通道数*/) {
        int profile = 2;  //AAC LC
        // 采样率索引,参考:https://blog.csdn.net/jay100500/article/details/52955232
        // 8是16Khz
        int freqIdx = 0;
        switch (sampleRate) {
            case 96000:
                freqIdx = 0x0;
                break;
            case 88200:
                freqIdx = 0x1;
                break;
            case 64000:
                freqIdx = 0x2;
                break;
            case 48000:
                freqIdx = 0x3;
                break;
            case 44100:
                freqIdx = 0x4;
                break;
            case 32000:
                freqIdx = 0x5;
                break;
            case 24000:
                freqIdx = 0x6;
                break;
            case 22050:
                freqIdx = 0x7;
                break;
            case 16000:
                freqIdx = 0x8;
                break;
            case 12000:
                freqIdx = 0x9;
                break;
            case 11025:
                freqIdx = 0xa;
                break;
            case 8000:
                freqIdx = 0xb;
                break;
            case 7350:
                freqIdx = 0xc;
                break;
        }
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }


    /**
     * PCM文件转WAV文件
     * @param inPcmFile 输入PCM文件
     * @param outWavFile 输出WAV文件
     * @param sampleRate 采样率，例如44100
     * @param channels 声道数 单声道：1或双声道：2
     * @param bitNum 采样位数，8或16
     */
    public static void convertPcm2Wav(File inPcmFile, File outWavFile, int sampleRate,
                                      int channels, int bitNum) {

        FileInputStream in = null;
        FileOutputStream out = null;
        byte[] data = new byte[1024];

        try {
            //采样字节byte率
            long byteRate = sampleRate * channels * bitNum / 8;

            in = new FileInputStream(inPcmFile);
            out = new FileOutputStream(outWavFile);

            //PCM文件大小
            long totalAudioLen = in.getChannel().size();

            //总大小，由于不包括RIFF和WAV，所以是44 - 8 = 36，在加上PCM文件大小
            long totalDataLen = totalAudioLen + 36;

            writeWaveFileHeader(out, totalAudioLen, totalDataLen, sampleRate, channels, byteRate);

            int length = 0;
            while ((length = in.read(data)) > 0) {
                out.write(data, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 输出WAV文件
     * @param out WAV输出文件流
     * @param totalAudioLen 整个音频PCM数据大小
     * @param totalDataLen 整个数据大小
     * @param sampleRate 采样率
     * @param channels 声道数
     * @param byteRate 采样字节byte率
     * @throws IOException
     */
    private static void writeWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                            long totalDataLen, int sampleRate, int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (channels * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

}
