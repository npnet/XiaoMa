package com.xiaoma.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xiaoma.thread.BgThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


/**
 * Created by LKF on 2019-4-3 0003.
 * 支持帧动画的SurfaceView,一般用来播放帧数很多,图片很大的帧动画,可以防止OOM
 */
public class FrameAnimView extends SurfaceView {
    private static final String TAG = "FrameAnimView";

    private static final int DEFAULT_FRAME_INTERVAL = 50;
    private static final int DEFAULT_CACHED_FRAME_COUNT = 5;

    private ScheduledExecutorService mLoadingExecutor;
    private ScheduledExecutorService mRenderingExecutor;
    private int mCachedFrameCount = DEFAULT_CACHED_FRAME_COUNT;
    private int mFrameInterval = DEFAULT_FRAME_INTERVAL;
    private final FrameAnimTarget mFrameAnimTarget = new FrameAnimTarget();
    private final RenderTask mRenderTask = new RenderTask();
    private LinkedBlockingQueue<Drawable> mFrameDrawables;
    private AnimateState mAnimateState = AnimateState.STOP;
    private boolean mOneShot = false;

    public FrameAnimView(Context context) {
        super(context);
        init();
    }

    public FrameAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FrameAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public synchronized void setFrames(@DrawableRes int[] frames) {
        setFrames(frames, DEFAULT_FRAME_INTERVAL);
    }

    public synchronized void setFrames(@DrawableRes int[] frames, int frameInterval) {
        mFrameAnimTarget.setFrames(frames);
        mFrameInterval = frameInterval;
    }

    public synchronized void setFrameIndex(int frameIndex) {
        mFrameDrawables.clear();
        mFrameAnimTarget.setFrameIndex(frameIndex);
        if (!isPlaying()) {
            Drawable dr = mFrameAnimTarget.getCurFrame();
            if (dr != null) {
                mRenderTask.drawFrame(dr);
            }
        }
    }

    public synchronized void setCachedFrameCount(int cachedFrameCount) {
        mCachedFrameCount = cachedFrameCount;
    }

    public synchronized void start() {
        if (isPlaying())
            return;
        releaseExecutor();
        initExecutor();
        mLoadingExecutor.scheduleAtFixedRate(mFrameAnimTarget,
                0, Math.max(mFrameInterval / mCachedFrameCount, 1), TimeUnit.MILLISECONDS);
        mRenderingExecutor.scheduleAtFixedRate(mRenderTask,
                0, Math.max(mFrameInterval, 1), TimeUnit.MILLISECONDS);
        mAnimateState = AnimateState.PLAYING;
    }

    public synchronized void stop() {
        if (!isPlaying())
            return;
        releaseExecutor();
        mAnimateState = AnimateState.STOP;
    }

    public synchronized void reset() {
        stop();
        setFrameIndex(0);
    }

    public synchronized void restart() {
        reset();
        start();
    }

    public synchronized boolean isPlaying() {
        return AnimateState.PLAYING == mAnimateState;
    }

    public synchronized void setOneShot(boolean oneShot) {
        mOneShot = oneShot;
    }

    private void init() {
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mFrameDrawables = new LinkedBlockingQueue<>(mCachedFrameCount);
    }

    private void initExecutor() {
        ThreadFactory threadFactory = new BgThreadFactory();
        mLoadingExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
        mRenderingExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    private void releaseExecutor() {
        if (mRenderingExecutor != null) {
            mRenderingExecutor.shutdownNow();
            mRenderingExecutor = null;
        }
        if (mLoadingExecutor != null) {
            mLoadingExecutor.shutdownNow();
            mLoadingExecutor = null;
        }
    }

    private class FrameAnimTarget implements Runnable {
        private int[] mFramesRes;
        private int mFrameIndex = -1;

        @Override
        public void run() {
            if (mFramesRes == null || mFramesRes.length <= 0)
                return;
            int curIdx = mFrameIndex;
            if (mOneShot &&
                    curIdx >= mFramesRes.length - 1) {
                stop();
                return;
            }
            int nextIdx = nextIndex();
            int resId = mFramesRes[nextIdx];
            if (resId > 0) {
                try {
                    //Log.i(TAG, String.format("load index: %s", nextIdx));
                    mFrameDrawables.put(getContext().getDrawable(resId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Drawable getCurFrame() {
            if (mFramesRes == null || mFrameIndex < 0 || mFrameIndex >= mFramesRes.length)
                return null;
            return getContext().getDrawable(mFramesRes[mFrameIndex]);
        }

        private void setFrames(int[] framesRes) {
            mFramesRes = framesRes;
        }

        private void setFrameIndex(int frameIndex) {
            mFrameIndex = frameIndex;
        }

        private int nextIndex() {
            if (mFramesRes == null ||
                    mFramesRes.length <= 0) {
                return -1;
            }
            int nextIndex = mFrameIndex + 1;
            if (nextIndex >= mFramesRes.length) {
                nextIndex = 0;
            } else if (nextIndex < 0) {
                nextIndex = 0;
            }
            mFrameIndex = nextIndex;
            return nextIndex;
        }
    }

    private class RenderTask implements Runnable {
        @Override
        public void run() {
            try {
                drawFrame(mFrameDrawables.take());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void drawFrame(Drawable frame) {
            SurfaceHolder holder = null;
            Canvas canvas = null;
            try {
                frame.setBounds(0, 0, frame.getIntrinsicWidth(), frame.getIntrinsicHeight());
                holder = getHolder();
                canvas = holder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                canvas.setMatrix(getMatrix());// 根据当前View的矩阵进行绘制
                frame.draw(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (holder != null && canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    private enum AnimateState {
        PLAYING,
        STOP
    }
}
