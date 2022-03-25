package com.xiaoma.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoma.ui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Author: loren
 * Date: 2018/11/20 0020
 */

public class MusicWaveView extends View {

    //竖条个数，根据控件宽度变化
    private int waveCount;
    //每个竖条内最多方格数,根据控件高度变化
    private int maxCellCount;
    //画布原点位置占控件高的倍率
    private float RATE = 0.5f;
    //固定每个竖条间距
    private float WAVE_SPACE = 8;
    //默认每个方格边长
    private float CELL_WIDTH = 30;
    //默认每个方格颜色
    private int CELL_COLOR = getResources().getColor(R.color.music_wave_cell_color);
    //刷新绘制
    private Handler handler = new Handler();
    //动效律动的刷新时间（ms）
    private long REFRESH_DELAY = 100;
    //随机数组，需实现方块递减-递增-递减无限循环
    private List<CountBean> countList;
    //是否第一次创建view
    boolean isFirst = true;
    //是否律动
    boolean isWave = false;


    public MusicWaveView(Context context) {
        this(context, null);
    }

    public MusicWaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MusicWaveView);
        CELL_WIDTH = (int) ta.getDimension(R.styleable.MusicWaveView_cellWidth, 30);
        WAVE_SPACE = (int) ta.getDimension(R.styleable.MusicWaveView_waveSpace, 8);
        CELL_COLOR = ta.getColor(R.styleable.MusicWaveView_cellColor, getResources().getColor(R.color.music_wave_cell_color));
        REFRESH_DELAY = (long) ta.getFloat(R.styleable.MusicWaveView_waveDuration, 100);
        RATE = ta.getFloat(R.styleable.MusicWaveView_rate, 0.5f);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        waveCount = (int) (getWidth() / (CELL_WIDTH + WAVE_SPACE));
        maxCellCount = (int) (getHeight() * RATE / (CELL_WIDTH + WAVE_SPACE));
        if (isFirst) {
            initCounts();
            isFirst = false;
        }

        canvas.save();
        canvas.translate(0, (getHeight() * RATE));
        canvas.scale(1, -1);

        Paint paint = new Paint();
        paint.setColor(CELL_COLOR);
        paint.setAlpha(200);

        if (waveCount > 0) {
            for (int i = 0; i < waveCount; i++) {
                //绘制多个竖条
                for (int j = 0; j < countList.get(i).getCount(); j++) {
                    //绘制每一个竖条
                    float left = i * (WAVE_SPACE + CELL_WIDTH);
                    float top = j * (WAVE_SPACE + CELL_WIDTH);
                    float right = CELL_WIDTH + i * (WAVE_SPACE + CELL_WIDTH);
                    float bottom = (CELL_WIDTH + j * (WAVE_SPACE + CELL_WIDTH));
                    canvas.drawRect(left, top, right, bottom, paint);
                }
            }
        }
        canvas.save();
        //绘制倒影
        canvas.scale(1, -1);
        if (waveCount > 0) {
            for (int i = 0; i < waveCount; i++) {
                //绘制多个竖条
                for (int j = 0; j < countList.get(i).getCount(); j++) {
                    //绘制每一个竖条
                    paint.setAlpha(60 - 20 * j <= 0 ? 20 : 60 - 20 * j);
                    float left = i * (WAVE_SPACE + CELL_WIDTH);
                    float top = j * (WAVE_SPACE + CELL_WIDTH) + WAVE_SPACE;
                    float right = CELL_WIDTH + i * (WAVE_SPACE + CELL_WIDTH);
                    float bottom = (CELL_WIDTH + j * (WAVE_SPACE + CELL_WIDTH)) + WAVE_SPACE;
                    canvas.drawRect(left, top, right, bottom, paint);
                }
            }
        }
        if (isWave){
            handler.postDelayed(runnable, REFRESH_DELAY);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshCounts();
            invalidate();
        }
    };

    /**
     * 停止律动
     */
    public void stopWave() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            isWave = false;
        }
    }

    /**
     * 开始或停止律动
     */

    public void controlWave(boolean wave) {
        if (!wave) {
            stopWave();
        } else {
            isWave = true;
            invalidate();
        }
    }

    /**
     * 更新随机数组
     * 规则：元素小于最大值就递增，等于最大值就递减
     */
    private void refreshCounts() {
        if (countList.size() == 0) {
            return;
        }
        for (int i = 0; i < countList.size(); i++) {
            if (countList.get(i).isOperation()) {
                //递增
                if (countList.get(i).getCount() < maxCellCount) {
                    countList.get(i).up();
                } else {
                    countList.get(i).down();
                    countList.get(i).setOperation(false);
                }
            } else {
                //递减
                if (countList.get(i).getCount() > 1) {
                    countList.get(i).down();
                } else {
                    countList.get(i).up();
                    countList.get(i).setOperation(true);
                }
            }
        }
    }

    private void initCounts() {
        if (waveCount <= 0) {
            return;
        }
        countList = new ArrayList<>();
        for (int i = 0; i < waveCount; i++) {
            Random random = new Random();
            int count = random.nextInt(maxCellCount);
            if (i > 0) {
                //防止相邻的竖条数量相同
                count = checkCount(countList.get(i - 1).getCount(), count);
            }
            //最小为1
            count = count == 0 ? 1 : count;
            countList.add(new CountBean(i % 2 == 0, count));
        }
    }

    private int checkCount(int preCount, int result) {
        if (result == preCount) {
            Random random = new Random();
            result = random.nextInt(maxCellCount);
        } else {
            return result;
        }
        return checkCount(preCount, result);
    }

    class CountBean {

        boolean operation;
        int count;

        public CountBean(boolean operation, int count) {
            this.operation = operation;
            this.count = count;
        }

        public boolean isOperation() {
            return operation;
        }

        public void setOperation(boolean operation) {
            this.operation = operation;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int up() {
            return this.count++;
        }

        public int down() {
            return this.count--;
        }
    }

}
