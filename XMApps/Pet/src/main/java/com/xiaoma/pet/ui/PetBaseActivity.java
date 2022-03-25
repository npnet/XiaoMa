package com.xiaoma.pet.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.pet.R;
import com.xiaoma.pet.ui.view.PetExperienceView;
import com.xiaoma.pet.ui.view.PetToast;
import com.xiaoma.utils.KeyEventUtils;
import com.xiaoma.utils.NetworkUtils;

/**
 * Created by Gillben on 2018/12/27 0027
 * <p>
 * desc:
 */
public abstract class PetBaseActivity extends BaseActivity {


    private FrameLayout mContentLayout;
    private LinearLayout mCloseImage;
    private LinearLayout navigationLinear;
    private ConstraintLayout mPetPageStatusLayout;
    private Button mPetPageStatusBt;
    private TextView mPetPageStatusPromptText;
    private ImageView notNetworkPetIcon;
    private View mNavigationBack;
    private LinearLayout mPassChapterLayout;
    private PetExperienceView mPassChapterLoadingProgressView;
    private boolean isPassChapterLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().hideNavi();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pet_base);
        initBaseView();
        checkNetWork();
    }

    @Override
    protected boolean isAppNeedShowNaviBar() {
        return false;
    }

    private void initBaseView() {
        mContentLayout = findViewById(R.id.pet_base_layout);
        navigationLinear = findViewById(R.id.navigation_linear);
        mPetPageStatusLayout = findViewById(R.id.title_prompt_layout);
        mPetPageStatusBt = findViewById(R.id.prompt_bt);
        mPetPageStatusPromptText = findViewById(R.id.tv_prompt_title);
        notNetworkPetIcon = findViewById(R.id.iv_prompt_pet_icon);
        mCloseImage = findViewById(R.id.iv_close_layout);
        mNavigationBack = findViewById(R.id.pet_navigation_back);
        mPassChapterLayout = findViewById(R.id.pass_chapter_loading_layout);
        mPassChapterLoadingProgressView = findViewById(R.id.pass_chapter_loading_progress_view);


        mPetPageStatusBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        mCloseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PetBaseActivity.this.finish();
            }
        });

        mNavigationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyEventUtils.sendKeyEvent(PetBaseActivity.this, KeyEvent.KEYCODE_BACK);
            }
        });
        mContentLayout.addView(bindView());
    }


    protected boolean checkNetWork() {
        if (NetworkUtils.isConnected(this)) {
            loadSuccess();
            initData();
            return true;
        }
        loadFailed();
        networkWarn();
        return false;
    }


    protected final void loadSuccess() {
        if (isPassChapterLoading) {
            return;
        }
        mPassChapterLayout.setVisibility(View.GONE);
        mPetPageStatusLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        if (navigationLinear.getVisibility() == View.VISIBLE) {
            mCloseImage.setVisibility(View.GONE);
        } else {
            mCloseImage.setVisibility(View.VISIBLE);
        }
    }


    protected final void loadFailed() {
        mPassChapterLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.GONE);
        mCloseImage.setVisibility(View.VISIBLE);
        mPetPageStatusLayout.setVisibility(View.VISIBLE);
        mPetPageStatusPromptText.setVisibility(View.VISIBLE);
        mPetPageStatusBt.setVisibility(View.VISIBLE);
        mPetPageStatusPromptText.setText(R.string.network_connect_failed);
        mPetPageStatusBt.setText(R.string.refresh_network);
    }


    protected final void showPassChapterLoadingProgressLayout() {
        isPassChapterLoading = true;
        mCloseImage.setVisibility(View.GONE);
        mPetPageStatusBt.setVisibility(View.GONE);
        mPetPageStatusPromptText.setVisibility(View.INVISIBLE);
        navigationLinear.setVisibility(View.VISIBLE);
        mPetPageStatusLayout.setVisibility(View.VISIBLE);
        mPassChapterLayout.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.VISIBLE);

        //模拟游戏加载
        CountDownTimer countDownTimer = new CountDownTimer(10000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                long current = 10000 - millisUntilFinished;
                updatePassChapterLoadingProgress(10000, current);
            }

            @Override
            public void onFinish() {
                isPassChapterLoading = false;
                navigationLinear.setVisibility(View.GONE);
                loadSuccess();
            }
        };
        countDownTimer.start();
    }


    protected final void updatePassChapterLoadingProgress(long total, long current) {
        mPassChapterLoadingProgressView.updateExperience(total, current);
    }


    protected void refresh() {
        checkNetWork();
    }


    protected void networkWarn() {
        PetToast.showException(this, R.string.no_network);
    }


    protected void hideNavigationBar() {
        navigationLinear.setVisibility(View.GONE);
        mCloseImage.setVisibility(View.VISIBLE);
    }


    protected void showNavigationBar() {
        navigationLinear.setVisibility(View.VISIBLE);
        mCloseImage.setVisibility(View.GONE);
    }


    protected abstract View bindView();

    protected void initData() {
        //empty
    }

    public View getNavigationBack() {
        return mNavigationBack;
    }

}
