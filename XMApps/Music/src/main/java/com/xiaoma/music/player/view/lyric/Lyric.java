package com.xiaoma.music.player.view.lyric;

import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * <pre>
 *  author : Jir
 *  date : 2018/8/13
 *  description :
 * </pre>
 */
public class Lyric implements Comparable<Lyric> {

    private long curTime;
    private String strTime;
    private String content;
    private StaticLayout staticLayout;
    private float offset = Float.MIN_VALUE;

    public void init(TextPaint paint, int width) {
        staticLayout = new StaticLayout(content, paint, width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
    }

    public long getCurTime() {
        return curTime;
    }

    public void setCurTime(long curTime) {
        this.curTime = curTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStrTime() {
        return strTime;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }

    public StaticLayout getStaticLayout() {
        return staticLayout;
    }

    public int getHeight() {
        if (staticLayout == null) {
            return 0;
        }
        return staticLayout.getHeight();
    }

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "curTime=" + curTime +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Lyric o) {
        if (o == null) {
            return -1;
        }
        return (int) (this.getCurTime() - o.getCurTime());
    }
}
