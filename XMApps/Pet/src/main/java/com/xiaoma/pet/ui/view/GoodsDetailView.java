package com.xiaoma.pet.ui.view;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.pet.R;
import com.xiaoma.pet.common.RequestManager;
import com.xiaoma.pet.common.annotation.GoodsType;
import com.xiaoma.pet.common.callback.ILoadDataOfMallListener;
import com.xiaoma.pet.common.utils.PetConstant;
import com.xiaoma.pet.common.utils.ResourceUsedRegister;
import com.xiaoma.pet.model.RepositoryInfo;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Gillben on 2018/12/28 0028
 * <p>
 * desc: 宠物喂食窗口
 */
public class GoodsDetailView extends PopupWindow {

    private static final int DEFAULT_WIDTH = 1320;

    private Context mContext;
    private ImageView mFoodIcon;
    private TextView mFoodName;
    private TextView mFoodDesc;
    private TextView mEatFoodTime;
    private TextView mInventory;
    private TextView mWaitEat;
    private TextView mEatBt;
    private RepositoryInfo mRepositoryInfo;
    private OnDetailsResultCallback onDetailsResultCallback;
    private ILoadDataOfMallListener loadDataOfMallListener;

    public GoodsDetailView(Context context, RepositoryInfo repositoryInfo, ILoadDataOfMallListener loadDataOfMallListener) {
        this.mContext = context;
        this.mRepositoryInfo = repositoryInfo;
        this.loadDataOfMallListener = loadDataOfMallListener;
        View view = View.inflate(context, R.layout.dialog_eat_food, null);
        setContentView(view);
        initView(view);
        addListener();
        initAttrs();
    }


    private void initView(View view) {
        mFoodIcon = view.findViewById(R.id.iv_eat_food_icon);
        mFoodName = view.findViewById(R.id.tv_eat_food_name);
        mFoodDesc = view.findViewById(R.id.tv_eat_food_desc);
        mEatFoodTime = view.findViewById(R.id.tv_eat_food_time);
        mInventory = view.findViewById(R.id.tv_eat_food_inventory);
        mWaitEat = view.findViewById(R.id.tv_wait_eat_food);
        mEatBt = view.findViewById(R.id.eat_food_bt);

        setupContent();
    }


    private void initAttrs() {
        setWidth(DEFAULT_WIDTH);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setClippingEnabled(false);
        setOutsideTouchable(false);
        setTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.PetMapPopupAnimation);
    }


    private void setupContent() {
        ImageLoader.with(mContext)
                .load(mRepositoryInfo.getGoodsIcon())
                .placeholder(R.drawable.goods_list_placeholder)
                .into(mFoodIcon);
        mFoodName.setText(mRepositoryInfo.getGoodsName());

        if (GoodsType.FOOD.equals(mRepositoryInfo.getGoodsType())) {
            mFoodDesc.setText(Html.fromHtml(mContext.getString(R.string.help_pet_upgrade_desc, mRepositoryInfo.getEnergy())));
            mEatFoodTime.setText(Html.fromHtml(mContext.getString(R.string.eat_food_time, mRepositoryInfo.getFeedTime())));
            mInventory.setText(mContext.getString(R.string.repository_goods_number_text, mRepositoryInfo.getGoodsNumber()));
        } else {
            mFoodDesc.setText(mRepositoryInfo.getGoodsDesc());
            mEatFoodTime.setVisibility(View.GONE);
            mInventory.setVisibility(View.GONE);
            mEatBt.setText(R.string.use_goods);
            mWaitEat.setText(R.string.cancel_use_goods);
        }
    }

    private void addListener() {
        mEatBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleResult();
            }
        });

        mWaitEat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    private void handleResult() {
        if (!NetworkUtils.isConnected(mContext)) {
            dismiss();
            if (loadDataOfMallListener != null) {
                loadDataOfMallListener.loadFailedOfMall(PetConstant.PAY_FRAGMENT_TAG);
            }
        }

        switch (mRepositoryInfo.getGoodsType()) {
            case GoodsType.FOOD:
                eatFood();
                break;

            case GoodsType.DECORATOR:
            case GoodsType.COS_PLAY:
                ResourceUsedRegister.getInstance()
                        .getOnUsedGoodsCallback()
                        .usedDecoratorNeedDownload(mRepositoryInfo.getGoodsType(), mRepositoryInfo.getGoodsFilePath());
                dismiss();
                break;
        }
    }


    private void eatFood() {
        PetToast.showLoading(mContext, R.string.pet_loading_text);
        RequestManager.petEatFood(mRepositoryInfo.getId(), new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                PetToast.dismissLoading();
                int code = result.getResultCode();
                if (code == 1) {
                    if (ResourceUsedRegister.getInstance().getOnUsedGoodsCallback() != null) {
                        ResourceUsedRegister.getInstance().getOnUsedGoodsCallback().usedFood(false);
                        if (onDetailsResultCallback != null) {
                            onDetailsResultCallback.hasUsed();
                            XmTracker.getInstance().uploadEvent(-1, TrackerCountType.FEEDPET.getType());
                        }
                    }
                } else {
//                    暂时取消喂食toast提示
//                    XMToast.showToast(mContext, result.getResultMessage());
                    KLog.w("喂食失败.");
                }
                GoodsDetailView.this.dismiss();
            }

            @Override
            public void onFailure(int code, String msg) {
                PetToast.dismissLoading();
                GoodsDetailView.this.dismiss();
                if (code == PetConstant.EATING_FOOD_ERROR_CODE) {
                    ResourceUsedRegister.getInstance().getOnUsedGoodsCallback().usedFood(true);
//                    PetToast.showException(mContext, msg);
                } else if (code == PetConstant.FOOD_NOT_ENOUGH_ERROR_CODE) {
                    PetToast.showException(mContext, msg);
                } else {
                    //喂食失败
                    if (loadDataOfMallListener != null) {
                        loadDataOfMallListener.loadFailedOfMall(PetConstant.PAY_FRAGMENT_TAG);
                    }
//                    PetToast.showException(mContext, R.string.eat_food_failed);
                }
            }
        });
    }


    public void setOnDetailsResultCallback(OnDetailsResultCallback callback) {
        this.onDetailsResultCallback = callback;
    }


    public interface OnDetailsResultCallback {
        void hasUsed();
    }
}
