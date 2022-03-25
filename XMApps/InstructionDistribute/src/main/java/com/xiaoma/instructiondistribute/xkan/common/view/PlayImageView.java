package com.xiaoma.instructiondistribute.xkan.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.xiaoma.instructiondistribute.R;
import com.xiaoma.instructiondistribute.xkan.common.constant.XkanConstants;


/**
 * @author taojin
 * @date 2018/11/15
 */
public class PlayImageView extends AppCompatImageView {
    Paint mPaint;
    private Bitmap videoBitmap;
    private Bitmap gifBitmap;
    private Bitmap mLightBitmap;
    //由于倒影 造成的内容区中心点偏差
    private static final int PLAY_IMAGE_OFFSET = 30;
    //视频按钮半径
    private static final int PLAY_IMAGE_VIDEO_SIZE = 35;
    private int imageType = XkanConstants.FILE_TYPE_VIDEO;

    public PlayImageView(Context context) {
        super(context);
    }

    public PlayImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLightBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_highlight);
        videoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_video_play);
        gifBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_gif_play);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect src = null;
        Rect dst = null;
        if (imageType == XkanConstants.FILE_TYPE_VIDEO) {
            if (videoBitmap==null){
                videoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_video_play);
            }
            src = new Rect(0, 0, videoBitmap.getWidth(), videoBitmap.getHeight());
            dst = new Rect(getWidth() / 2 - PLAY_IMAGE_VIDEO_SIZE, getHeight() / 2 - PLAY_IMAGE_VIDEO_SIZE - PLAY_IMAGE_OFFSET, getWidth() / 2 + PLAY_IMAGE_VIDEO_SIZE, getHeight() / 2 + PLAY_IMAGE_VIDEO_SIZE - PLAY_IMAGE_OFFSET);
            canvas.drawBitmap(videoBitmap, src, dst, mPaint);
        } else if (imageType == XkanConstants.FILE_TYPE_PIC_GIF) {

            if (gifBitmap==null){
                gifBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_gif_play);
            }
            src = new Rect(0, 0, gifBitmap.getWidth(), gifBitmap.getHeight());
            dst = new Rect(0, 0, getWidth(), getHeight() - PLAY_IMAGE_OFFSET * 2);
            canvas.drawBitmap(gifBitmap, src, dst, mPaint);
        }
        //画上高光
        if (mLightBitmap==null){
            mLightBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_highlight);
        }
//        src = new Rect(0, 0, mLightBitmap.getWidth() , mLightBitmap.getHeight() );
//        // 指定图片在屏幕上显示的区域
//        dst = new Rect(0, 0, getWidth(), getHeight() - PLAY_IMAGE_OFFSET * 2);
//        canvas.drawBitmap(mLightBitmap, src, dst, mPaint);



    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
        invalidate();
    }
}
