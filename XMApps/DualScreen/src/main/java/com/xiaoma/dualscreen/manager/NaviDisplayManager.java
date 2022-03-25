package com.xiaoma.dualscreen.manager;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.os.Handler;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xiaoma.dualscreen.utils.PresentationUtils;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

/**
 * @author: iSun
 * @date: 2019/2/26 0026
 * 导航虚拟内存管理器
 */
public class NaviDisplayManager implements DisplayManager.DisplayListener {
    private Context mContext;
    private DisplayManager mDisplayManager;
    private VirtualDisplay mNaviDisplay, mLeftNaviDisplay;
    private String displayName = "XM", leftDisplayName = "XM_LEFT";
    private int defWidth = 1920;
    private int defHeight = 720;
    private int leftDefWidth = 534;
    private int leftDefHeight = 534;
    private String TAG = NaviDisplayManager.class.getSimpleName();
    private Handler mHandler;
    private ImageReader.OnImageAvailableListener mImageListener;
    private ImageReader mImageReader;
    private Surface mSurface, mLeftSurface;
    private static NaviDisplayManager instance;
    private volatile boolean isInit = false;
    private volatile boolean isInitLeft = false;

    public static NaviDisplayManager getInstance() {
        if (instance == null) {
            synchronized (NaviDisplayManager.class) {
                if (instance == null) {
                    instance = new NaviDisplayManager();
                }
            }
        }
        return instance;
    }

    public synchronized boolean isInit() {
        return isInit;
    }


    public DisplayManager getDisplayManager() {
        if (mDisplayManager == null && isInit) {
            mDisplayManager = mContext.getApplicationContext().getSystemService(DisplayManager.class);
        }
        return mDisplayManager;
    }

    /**
     * 输出到ImageReader
     *
     * @param context
     * @param handler
     */
    public void init(Context context, Handler handler) {
        if (context == null || isInit) {
            return;
        }
        isInit = true;
        this.mContext = context;
        mHandler = handler;
        mImageListener = new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
//                Image image = reader.acquireLatestImage();
//                if (image != null) {
//                    ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
//                    byte[] bytes = new byte[byteBuffer.remaining()];
//                    byteBuffer.get(bytes);
//                    Bitmap temp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                    image.close();
//                }
            }
        };

        mImageReader = ImageReader.newInstance(defWidth, defHeight, PixelFormat.RGBA_8888, 2);
        mImageReader.setOnImageAvailableListener(mImageListener, mHandler);
        mSurface = mImageReader.getSurface();
        getNaviVirtualDisplay(mSurface);
    }


    /**
     * 输出到surfaceView
     *
     * @param surfaceView
     */
    public void init(Context context, SurfaceView surfaceView) {
        if (context == null || isInit) {
            return;
        }
        this.mContext = context;
        isInit = true;
        mSurface = surfaceView.getHolder().getSurface();
        getNaviVirtualDisplay(mSurface);
        surfaceView.getHolder().addCallback(callBack);
        mContext.getApplicationContext().getSystemService(DisplayManager.class).registerDisplayListener(this, new Handler());
    }

    public void initLeft(Context context, SurfaceView surfaceView) {
        KLog.d("initLeft start");
        if (context == null || isInitLeft) {
            return;
        }
        this.mContext = context;
        isInitLeft = true;
        mLeftSurface = surfaceView.getHolder().getSurface();
        getLeftNaviVirtualDisplay(mLeftSurface);
        surfaceView.getHolder().addCallback(leftCallBack);
        KLog.d("initLeft done");
    }


    public synchronized VirtualDisplay getNaviVirtualDisplay(Surface surface) {
        if (mNaviDisplay == null) {
            if (DualViewManager.getInstance().isHigh()) {
                mNaviDisplay = createVirtualDisplay(mContext, displayName, surface, defWidth, defHeight);
            } else {
                mNaviDisplay = createVirtualDisplay(mContext, displayName, surface, 532, 542);
            }
            ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                @Override
                public void run() {
                    DualScreenMapManager.getInstance().setICMapMode(101);
                    KLog.e("getNaviVirtualDisplay 101");
                }
            }, 3000);
        }
        KLog.d("getNaviVirtualDisplay mNaviDisplay=" + mNaviDisplay);
        return mNaviDisplay;
    }

    public synchronized VirtualDisplay getLeftNaviVirtualDisplay(Surface surface) {
        if (mLeftNaviDisplay == null) {
            mLeftNaviDisplay = createVirtualDisplay(mContext, leftDisplayName, surface, leftDefWidth, leftDefHeight);
        }
        KLog.d("getLeftNaviVirtualDisplay mLeftNaviDisplay=" + mLeftNaviDisplay);
        return mLeftNaviDisplay;
    }

    public Display getNaviDisplay() {
        Display naviDisplay = null;
        if (naviDisplay == null) {
            naviDisplay = PresentationUtils.getDisplayForName(mContext, displayName);
        }
        return naviDisplay;
    }


    public VirtualDisplay createVirtualDisplay(Context context, String Displayname, Surface surface, int width, int height) {
//        DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC // 公开的虚拟显示 需要添加  CAPTURE_VIDEO_OUTPUT&CAPTURE_SECURE_VIDEO_OUTPUT权限
//        DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION //双屏异显 Presentation display
//        DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY //只展示display私有的内容
//        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR
        return createVirtualDisplay(context, DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION, Displayname, surface, width, height, null, null);
    }

    public VirtualDisplay createVirtualDisplay(Context context, int flags, String Displayname, Surface surface, int width, int height, VirtualDisplay.Callback callback, Handler handler) {
        if (context == null) {
            return null;
        }
        DisplayManager mDisplayManager = context.getSystemService(DisplayManager.class);
        return mDisplayManager.createVirtualDisplay(Displayname, width, height, 160, surface,
                flags, callback, handler);
    }

    public void onDestory() {
        isInit = false;
        if (mNaviDisplay != null) {
            mNaviDisplay.setSurface(null);
            mNaviDisplay.release();
            mNaviDisplay = null;
        }
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        mImageListener = null;
        mDisplayManager = null;
    }

    SurfaceHolder.Callback callBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            KLog.e(" surfaceCreated : ");
            holder.setFormat(PixelFormat.TRANSPARENT);
            if (mNaviDisplay != null && mNaviDisplay.getSurface() == null) {
                mNaviDisplay.setSurface(holder.getSurface());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            KLog.e(" surfaceChanged : ");
            holder.setFormat(PixelFormat.TRANSPARENT);
            if (mNaviDisplay == null) {
                mNaviDisplay = createVirtualDisplay(mContext, displayName, holder.getSurface(), width, height);
            } else {
                mNaviDisplay.setSurface(holder.getSurface());
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            KLog.e(" surfaceDestroyed : ");
            if (mNaviDisplay != null) {
                mNaviDisplay.setSurface(null);
            }
        }
    };

    SurfaceHolder.Callback leftCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            KLog.e(" leftCallBack surfaceCreated : ");
            holder.setFormat(PixelFormat.TRANSPARENT);
            if (mLeftNaviDisplay != null && mLeftNaviDisplay.getSurface() == null) {
                mLeftNaviDisplay.setSurface(holder.getSurface());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            KLog.e(" leftCallBack surfaceChanged : ");
            holder.setFormat(PixelFormat.TRANSPARENT);
            if (mLeftNaviDisplay == null) {
                mLeftNaviDisplay = createVirtualDisplay(mContext, leftDisplayName, holder.getSurface(), width, height);
            } else {
                mLeftNaviDisplay.setSurface(holder.getSurface());
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            KLog.e(" leftCallBack surfaceDestroyed : ");
            if (mLeftNaviDisplay != null) {
                mLeftNaviDisplay.setSurface(null);
            }
        }
    };

    public void onDisplayAdded(int displayId) {
        if(mLeftNaviDisplay != null &&  mLeftNaviDisplay.getDisplay() != null && mLeftNaviDisplay.getDisplay().getDisplayId() == displayId){
            KLog.e("=======>onDisplayAdded mLeftNaviDisplay display be add<=======");
        }
        if(mNaviDisplay != null &&  mNaviDisplay.getDisplay() != null && mNaviDisplay.getDisplay().getDisplayId() == displayId){
            KLog.e("=======>onDisplayAdded mNaviDisplay display be add<=======");
        }
    }

    public void onDisplayRemoved(int displayId) {
        if(mLeftNaviDisplay != null &&  mLeftNaviDisplay.getDisplay() != null  && mLeftNaviDisplay.getDisplay().getDisplayId() == displayId){
            KLog.e("=======>onDisplayRemoved mLeftNaviDisplay display be remove<=======");
        }
        if(mNaviDisplay != null &&  mNaviDisplay.getDisplay() != null && mNaviDisplay.getDisplay().getDisplayId() == displayId){
            KLog.e("=======>onDisplayRemoved mNaviDisplay display be remove<=======");
        }

    }

    public void onDisplayChanged(int displayId) {
        if(mLeftNaviDisplay != null &&  mLeftNaviDisplay.getDisplay() != null && mLeftNaviDisplay.getDisplay().getDisplayId() == displayId){
            KLog.e("=======>onDisplayChanged mLeftNaviDisplay display be change<=======");
        }
        if(mNaviDisplay != null &&  mNaviDisplay.getDisplay() != null && mNaviDisplay.getDisplay().getDisplayId() == displayId){
            KLog.e("=======>onDisplayChanged mNaviDisplay display be change<=======");
        }
    }
}
