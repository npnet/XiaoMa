package com.xiaoma.instructiondistribute.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xiaoma.instructiondistribute.R;
import com.xiaoma.instructiondistribute.distribute.InstructionDispatcher;
import com.xiaoma.instructiondistribute.utils.DistributeConstants;

public class DisplayActivity extends Activity {
    // 用来显示颜色
    private ConstraintLayout layout;
    // 用来显示固定图片
    private ImageView displayIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_display);
        InstructionDispatcher.getInstance().destoryMap.put("DisplayActivity", this);
        initView();
        int type = getIntent().getIntExtra(DistributeConstants.DISPLAY_TYPE, -1);
        handleCmd(type);
    }

    private void initView() {
        layout = findViewById(R.id.bg);
        displayIv = findViewById(R.id.display_iv);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int type = intent.getIntExtra(DistributeConstants.DISPLAY_TYPE, -1);
        handleCmd(type);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InstructionDispatcher.getInstance().destoryMap.remove("DisplayActivity");
    }

    private void handleCmd(int type) {
        switch (type) {
            case DistributeConstants.BACK_TO_HOME_PAGE:
// TODO: 2019/7/23 返回home到哪？
                break;
            case DistributeConstants.TEST_WHITE_SCREEN:
                displayIv.setBackgroundResource(R.mipmap.pic_white);
                break;
            case DistributeConstants.TEST_BLACK_SCREEN:
                displayIv.setBackgroundResource(R.mipmap.pic_black);
                break;
            case DistributeConstants.TEST_RED_SCREEN:
                displayIv.setBackgroundResource(R.mipmap.pic_red);
                break;
            case DistributeConstants.TEST_GREEN_SCREEN:
                displayIv.setBackgroundResource(R.mipmap.pic_green);
                break;
            case DistributeConstants.TEST_BLUE_SCREEN:
                displayIv.setBackgroundResource(R.mipmap.pic_blue);
                break;
            case DistributeConstants.EGIHT_COLOR_BAR_SCREEN:
                displayIv.setBackgroundResource(R.mipmap.pic_eight_color_bar);
                break;
            case DistributeConstants.BIG_CHESS_SCREEN:
                displayIv.setBackgroundResource(R.mipmap.pic_big_window);
                break;
            case DistributeConstants.FISH_SCREEN:
                displayIv.setBackgroundResource(R.mipmap.pic_fish);
                break;
            case DistributeConstants.GRAY_BLACK_SCREEN:
                displayIv.setBackgroundResource(R.mipmap.pic_gray_black_fliecer);
                break;
            case DistributeConstants.H_GRAY_SCALE_SCREEN:
                displayIv.setBackgroundResource(R.mipmap.pic_h_gray_scale);
                break;
            case DistributeConstants.MID_GRAY_SCREEN:
                displayIv.setBackgroundResource(R.mipmap.pic_mid_grey);
                break;
            case DistributeConstants.V_BW_SCREEN:
                displayIv.setBackgroundResource(R.mipmap.pic_v_bw);
                break;
            case DistributeConstants.V_GRAY_SCREEN:
                displayIv.setBackgroundResource(R.mipmap.pic_v_gray_scale);
                break;
            case DistributeConstants.RESERVED_E:

                break;
            case DistributeConstants.RESERVED_F:

                break;
            case DistributeConstants.WRITE_LCD_LVDS_OUTPUT:
                displayIv.setBackgroundResource(R.mipmap.bg_common);
                break;
        }
    }

}
