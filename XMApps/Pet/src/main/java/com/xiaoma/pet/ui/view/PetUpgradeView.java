package com.xiaoma.pet.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiaoma.pet.R;
import com.xiaoma.pet.adapter.UpgradeRewardAdapter;
import com.xiaoma.pet.common.annotation.PetEvolution;
import com.xiaoma.pet.common.annotation.UnityAction;
import com.xiaoma.pet.common.utils.AUBridgeDispatcher;
import com.xiaoma.pet.common.utils.UpgradeEnergyHandler;
import com.xiaoma.pet.model.RewardDetails;
import com.xiaoma.pet.model.UpgradeRewardInfo;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * Created by Gillben on 2018/12/29 0029
 * <p>
 * desc: 宠物升级窗口
 */
public class PetUpgradeView extends PopupWindow {

    private static final String TAG = PetUpgradeView.class.getSimpleName();
    private static final int DEFAULT_WIDTH = 1320;
    private Context mContext;
    private TextView mTitleText;
    private TextView currentLevelText;
    private TextView nextLevelText;
    private RecyclerView mUpgradeRV;
    private RecyclerView mNextUpgradeRV;
    private View mView;
    private UpgradeRewardAdapter currentRewardAdapter;
    private UpgradeRewardAdapter nextLevelRewardAdapter;
    private UpgradeRewardInfo mUpgradeRewardInfo;
    private int newPetLevel;

    public PetUpgradeView(Context context, int newPetLevel, UpgradeRewardInfo upgradeRewardInfo) {
        this.mContext = context;
        this.mUpgradeRewardInfo = upgradeRewardInfo;
        this.newPetLevel = newPetLevel;
        initAttrs();
        initView(context);
        updatePrompt();
        getUpgradeReward();
    }


    private void initView(Context context) {
        View view = View.inflate(context, R.layout.pet_upgrade_view, null);
        mTitleText = view.findViewById(R.id.pet_upgrade_title);
        currentLevelText = view.findViewById(R.id.upgrade_reward_text);
        nextLevelText = view.findViewById(R.id.next_upgrade_reward_text);
        mView = view.findViewById(R.id.upgrade_background_view);
        mUpgradeRV = view.findViewById(R.id.rv_upgrade_reward);
        mNextUpgradeRV = view.findViewById(R.id.rv_next_upgrade_reward);
        setContentView(view);

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager nextManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mUpgradeRV.setLayoutManager(manager);
        mNextUpgradeRV.setLayoutManager(nextManager);

        mUpgradeRV.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 100, 0);
            }
        });

        mNextUpgradeRV.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 100, 0);
            }
        });

        currentRewardAdapter = new UpgradeRewardAdapter();
        nextLevelRewardAdapter = new UpgradeRewardAdapter();
        mUpgradeRV.setAdapter(currentRewardAdapter);
        mNextUpgradeRV.setAdapter(nextLevelRewardAdapter);
    }


    private void initAttrs() {
        setWidth(DEFAULT_WIDTH);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setClippingEnabled(false);
        setTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.PetMapPopupAnimation);
    }


    private void updatePrompt() {
        String titlePrompt = UpgradeEnergyHandler.getInstance().getPetEvolutionStatus(newPetLevel);
        if (TextUtils.isEmpty(titlePrompt)) {
            mTitleText.setText(R.string.upgrade_prompt);
            currentLevelText.setText(R.string.upgrade_reward);
        } else {
            //宠物进化提示
            mTitleText.setText(R.string.evolution_prompt);
            currentLevelText.setText(R.string.evolution_reward);

            //unity更新宠物徽章
            sendAccessoryToUnity(titlePrompt);
        }
    }

    private void sendAccessoryToUnity(String evolution) {
        Object[] params = new Object[2];
        params[0] = "Accessory";
        if (PetEvolution.CHILDHOOD.equals(evolution)) {
            params[1] = 1;
        } else if (PetEvolution.JUVENILE.equals(evolution)) {
            params[1] = 2;
        } else if (PetEvolution.YOUNG.equals(evolution)) {
            params[1] = 3;
        } else if (PetEvolution.STRONG.equals(evolution)) {
            params[1] = 4;
        }
        AUBridgeDispatcher.getInstance().callUnity(System.currentTimeMillis(), UnityAction.INTERNAL_REFRESH, params);
    }


    private void getUpgradeReward() {
        if (mUpgradeRewardInfo != null) {
            if (mUpgradeRewardInfo.getUpgradeReward() != null) {
                List<RewardDetails> currentLevelRewards = mUpgradeRewardInfo.getUpgradeReward().getGameRewardVoList();
                if (currentLevelRewards != null && currentLevelRewards.size() > 0) {
                    currentLevelText.setVisibility(View.VISIBLE);
                    currentRewardAdapter.setNewData(currentLevelRewards);
                } else {
                    KLog.w(TAG, currentLevelRewards != null ? "Current level rewards len 0." : "currentLevelRewards instance is null.");
                }
            }

            if (mUpgradeRewardInfo.getNextLevelReward() != null) {
                List<RewardDetails> nextLevelRewards = mUpgradeRewardInfo.getNextLevelReward().getGameRewardVoList();
                if (nextLevelRewards != null && nextLevelRewards.size() > 0) {
                    nextLevelText.setVisibility(View.VISIBLE);
                    nextLevelRewardAdapter.setNewData(nextLevelRewards);
                } else {
                    KLog.w(TAG, nextLevelRewards != null ? "Next level rewards len 0." : "nextLevelRewards instance is null.");
                }
            }
        }
    }

}
