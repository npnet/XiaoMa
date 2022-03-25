package com.xiaoma.vr.recorder;


/**
 * @author: iSun
 * @date: 2019/3/28 0028
 */
public class RingBuffer {


    private final static int DEFAULT_SIZE = 1024;
    private byte[] buffer;
    private int head = 0;
    private int tail = 0;
    private int bufferSize;
    private volatile int index = 0;

    public RingBuffer() {
        this.bufferSize = DEFAULT_SIZE;
        this.buffer = new byte[bufferSize];
    }

    public RingBuffer(int initSize) {
        this.bufferSize = initSize;
        this.buffer = new byte[bufferSize];
    }

    private Boolean empty() {
        return index == 0;
    }

    private Boolean full() {
        return (tail + 1) % bufferSize == head;
    }

    public void clear() {
//        Arrays.fill(buffer, (byte) 0);
        this.buffer = new byte[bufferSize];
        this.head = 0;
        this.tail = 0;
        this.index = 0;
    }


    public Boolean put(Byte v) {
//        if (full()) {
//            return false;
//        }
        buffer[tail] = v;
        tail = (tail + 1) % bufferSize;
        index++;
        return true;
    }



    public Boolean putBytes(byte[] bytes) {
//        if (full()) {
//            return false;
//        }
        for (byte aByte : bytes) {
            put(aByte);
        }
        return true;
    }

    private Boolean putBytes(byte[] bytes, int index) {
        for (int i = index; i < bytes.length; i++) {
            put(bytes[i]);
        }
        return true;
    }


    public Object get() {
        if (empty()) {
            return null;
        }
        Object result = buffer[head];
        head = (head + 1) % bufferSize;
        return result;
    }

    public byte[] getBuffer(int start, int end) {
        byte[] temp = new byte[end - start];
        int count = 0;
        while (end - start > 0) {
            temp[count] = buffer[(start + 1) % bufferSize];
            count++;
            start++;
        }
        return temp;
    }

    public byte[] getBuffer() {
        if (index > bufferSize) {
            return buffer;
        } else {
            byte[] temp = new byte[index];
            for (int i = 0; i < index; i++) {
                temp[i] = buffer[i];
            }
            return buffer;
        }
    }

    public Object get(int index) {
        if (empty()) {
            return null;
        }
        int temp = (index + 1) % bufferSize;
        Object result = buffer[temp];
        return result;
    }

    public Object[] getAll() {
        if (empty()) {
            return new Object[0];
        }
        int copyTail = tail;
        int cnt = head < copyTail ? copyTail - head : bufferSize - head + copyTail;
        Object[] result = new Byte[cnt];
        if (head < copyTail) {
            for (int i = head; i < copyTail; i++) {
                result[i - head] = buffer[i];
            }
        } else {
            for (int i = head; i < bufferSize; i++) {
                result[i - head] = buffer[i];
            }
            for (int i = 0; i < copyTail; i++) {
                result[bufferSize - head + i] = buffer[i];
            }
        }
        head = copyTail;
        return result;
    }

    public int getTail() {
        return tail;
    }

    public int getIndex() {
        return index;
    }

}
