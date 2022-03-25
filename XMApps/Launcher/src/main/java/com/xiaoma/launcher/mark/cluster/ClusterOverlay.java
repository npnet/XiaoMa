package com.xiaoma.launcher.mark.cluster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.Projection;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.mark.cluster.bean.BitmapSave;
import com.xiaoma.launcher.mark.model.MarkPhotoBean;
import com.xiaoma.mapadapter.interfaces.IMarker;
import com.xiaoma.mapadapter.listener.OnCameraChangeListener;
import com.xiaoma.mapadapter.listener.OnMarkerClickListener;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.model.MarkerOption;
import com.xiaoma.mapadapter.utils.MapUtil;
import com.xiaoma.mapadapter.view.Map;
import com.xiaoma.mapadapter.view.Marker;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by yiyi.qi on 16/10/10.
 * 整体设计采用了两个线程,一个线程用于计算组织聚合数据,一个线程负责处理Marker相关操作
 */
public class ClusterOverlay implements OnCameraChangeListener, OnMarkerClickListener {
    private Map mAMap;
    private Context mContext;
    private List<ClusterItem> mClusterItems = new ArrayList<ClusterItem>();
    private List<Cluster> mClusters;
    private List<BitmapSave> mBitmapSave = new ArrayList<BitmapSave>();
    private int mClusterSize;
    private ClusterClickListener mClusterClickListener;
    private ClusterRender mClusterRender;
    private List<Marker> mAddMarkers = new ArrayList<Marker>();
    private double mClusterDistance;
    private LruCache<BitmapSave, Bitmap> mLruCache;
    private HandlerThread mMarkerHandlerThread = new HandlerThread("addMarker");
    private HandlerThread mSignClusterThread = new HandlerThread("calculateCluster");
    private Handler mMarkerhandler;
    private Handler mSignClusterHandler;
    private float mPXInMeters;
    private boolean mIsCanceled = false;
    private float mZoom = 7;


    /**
     * 构造函数
     *
     * @param amap
     * @param clusterSize 聚合范围的大小（指点像素单位距离内的点会聚合到一个点显示）
     * @param context
     */
    public ClusterOverlay(Map amap, int clusterSize, Context context) {
        this(amap, null, clusterSize, context);
    }

    /**
     * 构造函数,批量添加聚合元素时,调用此构造函数
     *
     * @param amap
     * @param clusterItems 聚合元素
     * @param clusterSize
     * @param context
     */
    public ClusterOverlay(Map amap, List<ClusterItem> clusterItems,
                          int clusterSize, Context context) {
//默认最多会缓存80张图片作为聚合显示元素图片,根据自己显示需求和app使用内存情况,可以修改数量
        //类似带有缓存的map
        mLruCache = new LruCache<BitmapSave, Bitmap>(80) {
            protected void entryRemoved(boolean evicted, BitmapSave key, Bitmap oldValue, Bitmap newValue) {
                oldValue.recycle();
            }
        };
        mClusterItems.clear();
        if (clusterItems != null) {
            mClusterItems.addAll(clusterItems);
        }
        mContext = context;
        mClusters = new ArrayList<Cluster>();
        this.mAMap = amap;
        mClusterSize = clusterSize;
        //mAMap.getScalePerPixel() 获取缩放级别
        mPXInMeters = mAMap.getScalePerPixel();
        mClusterDistance = mPXInMeters * mClusterSize;
        amap.setOnCameraChangeListener(this);
        amap.setOnMarkerClickListener(this);
        initThreadHandler();
    }


    /**
     * 设置聚合点的点击事件
     *
     * @param clusterClickListener
     */
    public void setOnClusterClickListener(
            ClusterClickListener clusterClickListener) {
        mClusterClickListener = clusterClickListener;
    }

    /**
     * 设置聚合元素的渲染样式，不设置则默认为气泡加数字形式进行渲染
     *
     * @param render
     */
    public void setClusterRenderer(ClusterRender render) {
        mClusterRender = render;
    }

    public void onDestroy() {
        mIsCanceled = true;
        mSignClusterHandler.removeCallbacksAndMessages(null);
        mMarkerhandler.removeCallbacksAndMessages(null);
        mSignClusterThread.quit();
        mMarkerHandlerThread.quit();
        for (Marker marker : mAddMarkers) {
            marker.remove();

        }
        mAddMarkers.clear();
        mLruCache.evictAll();
    }

    //初始化Handler
    private void initThreadHandler() {
        mMarkerHandlerThread.start();
        mSignClusterThread.start();
        mMarkerhandler = new MarkerHandler(mMarkerHandlerThread.getLooper());
        mSignClusterHandler = new SignClusterHandler(mSignClusterThread.getLooper());
    }


    private AlphaAnimation mADDAnimation = new AlphaAnimation(0, 1);

    /**
     * 将单个聚合元素添加至地图显示
     *
     * @param cluster
     * @param bitmap
     */
    private void addSingleClusterToMap(Cluster cluster, Bitmap bitmap) {
        LatLng latlng = cluster.getCenterLatLng();
        MarkerOption markerOptions = new MarkerOption();

        markerOptions.anchor(0.5f, 0.5f)
                .icon(bitmap).position(latlng);
        Marker marker = mAMap.addMarker(markerOptions);
        marker.setObject(cluster);

        cluster.setMarker(marker);
        mAddMarkers.add(marker);

    }

    /**
     * 对点进行聚合
     */
    private void assignClusters() {
        mIsCanceled = true;
        mSignClusterHandler.removeMessages(SignClusterHandler.CALCULATE_CLUSTER);
        mSignClusterHandler.sendEmptyMessageDelayed(SignClusterHandler.CALCULATE_CLUSTER, 300);
    }

    /**
     * 根据一个点获取是否可以依附的聚合点，没有则返回null
     *
     * @param latLng
     * @return
     */
    private Cluster getCluster(LatLng latLng, List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            LatLng clusterCenterPoint = cluster.getCenterLatLng();
            double distance = MapUtil.calculateLineDistance(latLng, clusterCenterPoint);
            if (distance < mClusterDistance) {
                return cluster;
            }
        }
        return null;
    }


    /**
     * 获取每个聚合点的绘制样式
     */
    private Bitmap getBitmapDes(int num, Cluster cluster) {
        Bitmap bitmap = null;
        LayoutInflater factory = LayoutInflater.from(mContext);
        View view = factory.inflate(R.layout.cluster_item, null);
        TextView textView = view.findViewById(R.id.cluster_text);
        ImageView img = view.findViewById(R.id.cluster_img);
        if (num > 1) {
            textView.setVisibility(View.VISIBLE);
            String tile = String.valueOf(num);
            textView.setText(tile);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }
        if (cluster != null && !ListUtils.isEmpty(cluster.getClusterItems())) {
            ClusterItem clusterItem = cluster.getClusterItems().get(0);
            if (clusterItem != null) {
                MarkPhotoBean markPhotoBean = clusterItem.getMarkPhoto();
                if (markPhotoBean != null) {
                    if (markPhotoBean.getPhotoPath() != null) {
                        Bitmap sdBitmap = readBitmapFromFile(markPhotoBean.getPhotoPath(), 187, 117); //从本地取图片(在cdcard中获取)  //
                        sdBitmap = getRoundedCornerBitmap(sdBitmap);
                        img.setImageBitmap(sdBitmap);
                    }
                }
            }
        }
        //启用绘图缓存
        view.setDrawingCacheEnabled(true);
        //调用下面这个方法非常重要，如果没有调用这个方法，得到的bitmap为null
        view.measure(View.MeasureSpec.makeMeasureSpec(187, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(117, View.MeasureSpec.EXACTLY));
        //这个方法也非常重要，设置布局的尺寸和位置
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        //获得绘图缓存中的Bitmap
        view.buildDrawingCache();
        bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 设置图片圆角
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),

                bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);

        final float roundPx = 10;

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;

    }

    /**
     * 获取缩放后的本地图片
     *
     * @param filePath 文件路径
     * @param width    宽
     * @param height   高
     * @return
     */
    public static Bitmap readBitmapFromFile(String filePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int inSampleSize = 1;

        if (srcHeight > height || srcWidth > width) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / height);
            } else {
                inSampleSize = Math.round(srcWidth / width);
            }
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(filePath, options);
    }


    @Override
    public void onCameraChangeFinish(LatLng latLng) {
        if (mZoom >= 7){
            mSignClusterHandler.removeMessages(SignClusterHandler.CALCULATE_CLUSTER);
            mPXInMeters = mAMap.getScalePerPixel();
            mClusterDistance = mPXInMeters * mClusterSize;
            assignClusters();
        }

    }

    @Override
    public void onCameraChangeZoomFinish(float zoom) {
             mZoom = zoom;
             if (zoom<7){
                 mSignClusterHandler.removeMessages(SignClusterHandler.CALCULATE_CLUSTER);
                 mPXInMeters = mAMap.getScalePerPixel();
                 mClusterDistance = mPXInMeters * mClusterSize;
                 assignClusters();
             }
    }

    /**
     * mark点击监听
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mClusterClickListener == null) {
            return true;
        }
        Cluster cluster = (Cluster) marker.getObject();
        if (cluster != null) {
            mClusterClickListener.onMarkClick(marker, cluster.getClusterItems());
            return true;
        }
        return false;
    }


//-----------------------辅助内部类用---------------------------------------------

    /**
     * marker渐变动画，动画结束后将Marker删除
     */
    class MyAnimationListener implements IMarker.MarkAnimationListener {
        private List<Marker> mRemoveMarkers;

        MyAnimationListener(List<Marker> removeMarkers) {
            mRemoveMarkers = removeMarkers;
        }

        @Override
        public void onAnimationStart() {
        }

        @Override
        public void onAnimationEnd() {
            for (Marker marker : mRemoveMarkers) {
                marker.remove();
            }
        }
    }

    /**
     * 处理market添加，更新等操作
     */
    class MarkerHandler extends Handler {

        static final int ADD_CLUSTER_LIST = 0;


        MarkerHandler(Looper looper) {
            super(looper);
        }

        public synchronized void handleMessage(Message message) {

            switch (message.what) {
                case ADD_CLUSTER_LIST:
                    List<Cluster> clusters = (List<Cluster>) message.obj;
                    addClusterToMap(clusters);
                    break;
            }
        }
    }

    /**
     * 将聚合元素添加至地图上
     */
    private void addClusterToMap(List<Cluster> clusters) {
        ArrayList<Marker> removeMarkers = new ArrayList<>();
        removeMarkers.addAll(mAddMarkers);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        MyAnimationListener myAnimationListener = new MyAnimationListener(removeMarkers);
        for (Marker marker : removeMarkers) {
            marker.setAnimation(alphaAnimation);
            marker.setAnimationListener(myAnimationListener);
            marker.startAnimation();
        }
        HashMap map = new HashMap();
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get(i);
            // 将InputStream转换成Bitmap
            Bitmap bitmap = getBitmapDes(cluster.getClusterCount(), cluster);
            map.put(i, bitmap);
        }

        for (Object i : map.keySet()) {
            if (map.get(i) != null) {
                addSingleClusterToMap(clusters.get((Integer) i), (Bitmap) map.get(i));
            }
        }

       /* for (Cluster cluster : clusters) {
            addSingleClusterToMap(cluster);
        }*/
    }


    /**
     * 处理聚合点算法线程
     */
    class SignClusterHandler extends Handler {
        static final int CALCULATE_CLUSTER = 0;

        SignClusterHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case CALCULATE_CLUSTER:
                    calculateClusters();
                    break;
            }
        }
    }

    // 计算集群
    private void calculateClusters() {
        mIsCanceled = false;
        mClusters.clear();
        Projection projection = (Projection) mAMap.getProjection();
        LatLngBounds visibleBounds = projection.getVisibleRegion().latLngBounds;
        for (ClusterItem clusterItem : mClusterItems) {
            if (mIsCanceled) {
                return;
            }
            LatLng latlng = clusterItem.getPosition();
            com.amap.api.maps.model.LatLng ampLat = new com.amap.api.maps.model.LatLng(latlng.latitude, latlng.longitude);
            if (visibleBounds.contains(ampLat)||mZoom <= 4) {
                Cluster cluster = getCluster(latlng, mClusters);
                if (cluster != null) {
                    cluster.addClusterItem(clusterItem);
                } else {
                    cluster = new Cluster(latlng);
                    mClusters.add(cluster);
                    cluster.addClusterItem(clusterItem);
                }
            }
        }
        mMarkerhandler.removeMessages(MarkerHandler.ADD_CLUSTER_LIST);
        //复制一份数据，规避同步
        List<Cluster> clusters = new ArrayList<Cluster>();
        clusters.addAll(mClusters);
        Message message = Message.obtain();
        message.what = MarkerHandler.ADD_CLUSTER_LIST;
        message.obj = clusters;
        if (mIsCanceled) {
            return;
        }
        mMarkerhandler.sendMessage(message);
    }

}