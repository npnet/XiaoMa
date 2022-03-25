package com.xiaoma.assistant.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoma.assistant.R;
import com.xiaoma.skin.views.XmSkinTextView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: iSun
 * @date: 2019/01/02 0029
 */
public class VoiceTextView extends XmSkinTextView {
    public static final String TAG = VoiceTextView.class.getSimpleName();
    private long ANIMATION_TIME = 200;
    private Bitmap[] animBitmaps;
    private int animResIndex = 0;
    private int animRedId = 0;
    private int imageIndex = 0;
    private int animHight;
    private float xAxisLeft, yAxisTop;
    private volatile float imageWidth = 0;
    private float tempImageWidth = 0;
    private float signleLineHeight = 0;
    private int lineSpace = 0;
    private int ellipsisLine = 0;
    private float defWidth = 0;
    private float textHeight;
    private Paint.FontMetricsInt textFm;
    private String ellipsisSign = "......";
    private float ellipsisWidth;
    private boolean imageHasfirst = false;
    private boolean animSwitch = false;
    private CharSequence mText;
    private List<TextParameter> mTextParameters = new CopyOnWriteArrayList<>();
    private boolean isEllipsis = false;
    private volatile boolean animStatus = false;
    private TextPaint mPaint;
    private Handler handler;
    private RectF imageRectF;
    private Rect imageRect = new Rect();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (animBitmaps != null) {
                if (getVisibility() == View.VISIBLE) {
                    invalidate(imageRect.left, imageRect.top, imageRect.right, imageRect.bottom);
                }
                if (animStatus) {
                    handler.postDelayed(this, ANIMATION_TIME);
                    imageIndex = ++animResIndex % animBitmaps.length;
                }
            }
        }
    };

    public VoiceTextView(Context context) {
        super(context);
        init(null);
    }

    public VoiceTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public VoiceTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    public void init(AttributeSet attrs) {
        initTextPaint();
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.VoiceTextView);
            if (ta.hasValue(R.styleable.VoiceTextView_ellipsisSign)) {
                ellipsisSign = ta.getString(R.styleable.VoiceTextView_ellipsisSign);
            }
            ellipsisLine = ta.getInt(R.styleable.VoiceTextView_ellipsisLine, 2);
            tempImageWidth = ta.getDimensionPixelSize(R.styleable.VoiceTextView_imageWidth, 0);
            animSwitch = ta.getBoolean(R.styleable.VoiceTextView_animSwitch, false);
            lineSpace = ta.getDimensionPixelSize(R.styleable.VoiceTextView_lineSpace, 5);
            animRedId = ta.getResourceId(R.styleable.VoiceTextView_imageList, R.drawable.voice_input_anim);
            if (ta.hasValue(R.styleable.VoiceTextView_imageList)) {
                AnimationDrawable animationDrawable = (AnimationDrawable) getResources().getDrawable(animRedId);
                if (animationDrawable != null) {
                    animBitmaps = new Bitmap[animationDrawable.getNumberOfFrames()];
                    for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
                        animationDrawable.getFrame(i);
                        Bitmap bitmap = drawableToBitmap(animationDrawable.getFrame(i));
                        animBitmaps[i] = bitmap;
                    }
                    if (animBitmaps.length > 0) {
                        animHight = animBitmaps[0].getHeight();
                    }
                }
            }
            ta.recycle();
        }
        signleLineHeight = getTextSize();
        textHeight = getTextSize();
        handler = new Handler();
        if (animSwitch) {
            startEllipsisAnimation();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isOpenEllipsis()) {
            handleText(canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    private boolean isOpenEllipsis() {
        return animBitmaps != null && ellipsisLine > 0 && animBitmaps.length > 0;
    }


    private Bitmap getAnimImage() {
        return animBitmaps[imageIndex];
    }


    public void startEllipsisAnimation() {
        animStatus = true;
        imageWidth = tempImageWidth;
        handler.removeCallbacksAndMessages(runnable);
        handler.post(runnable);
    }

    public void stopEllipsisAnimation() {
        if (handler != null && runnable != null && animStatus) {
            handler.removeCallbacksAndMessages(runnable);
            animStatus = false;
            imageWidth = 0;
            postInvalidate();
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isOpenEllipsis()) {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            float newWidth, newHeight;
            defWidth = getDefaultSize(0, widthMeasureSpec);
            if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
                newWidth = getNewWidth();
                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) newWidth, MeasureSpec.EXACTLY);
            }
            if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
                newHeight = getNewHight();
                heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) newHeight, MeasureSpec.EXACTLY);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    private float getMaxWidthForMeasureMode() {
//        if (defWidth > getMaxWidth()) {
            return getMaxWidth();
//        } else {
//            return defWidth;
//        }
    }


    private float getNewHight() {
        CharSequence text = getText();
        float newHeight = textHeight + getPaddingTop() + getPaddingBottom();
        if (StaticLayout.getDesiredWidth(text, getPaint()) + imageWidth > getContentMaxWidth()) {
            int imageForLine = imageHasfirst ? 1 : 0;
            newHeight = textHeight * (mTextParameters.size() + imageForLine) + (lineSpace) * (mTextParameters.size() + imageForLine - 1) + getPaddingTop() + getPaddingBottom();
        }
        return newHeight <= 0 ? textHeight : newHeight;
    }

    private float getContentMaxWidth() {
        return getMaxWidthForMeasureMode() - getPaddingLeft() - getPaddingRight();
    }


    private int getNewWidth() {
        CharSequence text = getText();
        int newWidth = 0;
        int textWidth = (int) (StaticLayout.getDesiredWidth(text, getPaint()) + imageWidth);
        if (textWidth < getContentMaxWidth()) {
            newWidth = textWidth + getPaddingLeft() + getPaddingRight();
        } else {
            newWidth = (int) getMaxWidthForMeasureMode();
        }
        return newWidth;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        mText = text;
        super.setText(text, type);
    }

    @Override
    public CharSequence getText() {
        return mText;
    }


    private float getSpaceWidth(int targetLine) {
        float splaceWidth = 0;
        if (ellipsisLine == 0) {
            return splaceWidth;
        } else if (ellipsisLine == 1) {
            splaceWidth = ellipsisWidth + imageWidth;
        } else if (ellipsisLine == 2 || targetLine == 1 || targetLine == ellipsisLine) {
            if (isEllipsis) {
                if (targetLine == 1) {
                    splaceWidth += imageWidth;//第一行 即反向第二行
                } else if (targetLine == ellipsisLine) {
                    splaceWidth += ellipsisWidth;
                }
            } else {
                if (targetLine == 1) {
                    splaceWidth += 0;//
                } else if (targetLine == ellipsisLine) {
                    splaceWidth += imageWidth;
                }
            }
        }
        return splaceWidth;
    }

    public void initTextPaint() {
        mPaint = getPaint();
        mPaint.setTextSize(getTextSize());
        mPaint.setColor(getCurrentTextColor());
    }

    private synchronized void handleText(Canvas canvas) {
//        KLog.e(TAG, "src Text : " + mText);
        textFm = mPaint.getFontMetricsInt();
        char[] textChars = new StringBuffer(getText()).toString().toCharArray();
        float lineWidth = 0.0f;
        float startL = getPaddingLeft();
        float startT = getPaddingTop();
        startT += signleLineHeight / 2 + (textFm.bottom - textFm.top) / 2 - textFm.bottom + getPaddingTop();
        StringBuffer sb = new StringBuffer();
        ellipsisWidth = mPaint.measureText(ellipsisSign);
        //是否超过了字符数
        float tempWidth = 0f;
        for (int i = 0; i < textChars.length; i++) {
            tempWidth += mPaint.measureText(textChars[i] + "");
        }
        if ((tempWidth + imageWidth) / ellipsisLine > getContentMaxWidth()) {
            isEllipsis = true;
            textChars = new StringBuffer(getText()).reverse().toString().toCharArray();
        } else {
            isEllipsis = false;
        }
        mTextParameters.clear();
        lineWidth = getSpaceWidth(mTextParameters.size() + 1);
        for (int i = 0; i < textChars.length; i++) {
            float charWidth = mPaint.measureText(textChars[i] + "");
            if (lineWidth + charWidth <= getContentMaxWidth()) {
                sb.append(textChars[i]);
                lineWidth += charWidth;
            } else {
                TextParameter textParameter = new TextParameter();
                textParameter.text = getLineText(sb);
                textParameter.startLeft = startL;
                textParameter.startTop = startT;
                textParameter.width = lineWidth;
                mTextParameters.add(textParameter);
                if (getLines() == ellipsisLine && isEllipsis) {
                    textParameter.text = ellipsisSign + textParameter.text;
                }
                if (getLines() >= ellipsisLine) {
                    //超过行数
                    break;
                }
                startT += signleLineHeight + lineSpace;
                sb = new StringBuffer();
                lineWidth = 0.0f;
                lineWidth += getSpaceWidth(mTextParameters.size() + 1) + charWidth;
                sb.append(textChars[i]);
            }
        }
        if (sb.toString().length() > 0 && mTextParameters.size() < ellipsisLine) {
            TextParameter textParameter = new TextParameter();
            textParameter.text = getLineText(sb);
            textParameter.startLeft = startL;
            textParameter.startTop = startT;
            textParameter.width = lineWidth;
            mTextParameters.add(textParameter);
        }
        drawSplitText(canvas, mPaint);

    }


    private void drawSplitText(Canvas canvas, Paint contentPaint) {
        String lastLineText = null;
        float startT = signleLineHeight / 2 + (textFm.bottom - textFm.top) / 2 - textFm.bottom + getPaddingTop();
        if (isEllipsis) {
            for (int i = mTextParameters.size() - 1; i >= 0; i--) {
                TextParameter temp = mTextParameters.get(i);
                canvas.drawText(temp.text, temp.startLeft, startT, contentPaint);
                startT += (signleLineHeight + lineSpace);
                lastLineText = temp.text;
            }
        } else {
            for (int i = 0; i < mTextParameters.size(); i++) {
                TextParameter temp = mTextParameters.get(i);
                canvas.drawText(temp.text, temp.startLeft, startT, contentPaint);
                startT += (signleLineHeight + lineSpace);
                lastLineText = temp.text;
            }
        }
        drawImage(canvas, lastLineText);
    }


    private float getImageT() {
        float top = (getLines() - 1) * (signleLineHeight + lineSpace) + getPaddingTop();
        if (textHeight > animHight) {
            top += (textHeight - animHight) / 2;
        }
        return top;
    }


    private void drawImage(Canvas canvas, String lastLineText) {
        if (imageWidth > 0) {
            float imageTop = getImageT();
            if (lastLineText != null && !TextUtils.isEmpty(lastLineText)) {
                StaticLayout layout = new StaticLayout(lastLineText, getPaint(), (int) (getMaxWidthForMeasureMode()), Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
                xAxisLeft = layout.getPrimaryHorizontal(lastLineText.length()) + getPaddingLeft();//字符左边x坐标
            } else {
                xAxisLeft = getPaddingLeft();
                yAxisTop = lineSpace;
            }
            if (xAxisLeft + imageWidth > getMaxWidthForMeasureMode() - getPaddingRight()) {
                imageHasfirst = true;
                imageTop = getImageT() + (signleLineHeight + lineSpace);
                xAxisLeft = getPaddingLeft();
                imageRectF = new RectF(xAxisLeft, imageTop, xAxisLeft + imageWidth, imageTop + animHight + 5);
                canvas.drawBitmap(getAnimImage(), null, imageRectF, mPaint);
            } else {
                imageHasfirst = false;
                imageRectF = new RectF(xAxisLeft, imageTop, xAxisLeft + imageWidth, imageTop + animHight);
                canvas.drawBitmap(getAnimImage(), null, imageRectF, mPaint);
            }
            imageRectF.round(imageRect);
            if (imageRectF.bottom > getHeight()) {
                requestLayout();
            }
        } else {
            if (getImageT() + signleLineHeight > getHeight()) {
                requestLayout();
            }
        }

    }

    @NonNull
    private String getLineText(StringBuffer sb) {
        if (isEllipsis) {
            return new StringBuffer(sb.toString()).reverse().toString();
        } else {
            return new StringBuffer(sb.toString()).toString();
        }
    }

    private int getLines() {
        return mTextParameters.size();
    }

    public static class TextParameter {
        public String text;
        public float startLeft = 0f;
        public float startTop = 0f;
        public float width = 0f;
    }


    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        if (maxLines > 0) {
            this.ellipsisLine = maxLines;
        }
    }
}
