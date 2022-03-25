package com.xiaoma.pet.ui.mall.pay;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.guide.listener.GuideCallBack;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.pet.R;
import com.xiaoma.pet.common.RequestManager;
import com.xiaoma.pet.common.annotation.GoodsType;
import com.xiaoma.pet.common.callback.ILoadDataOfMallListener;
import com.xiaoma.pet.common.utils.PetConstant;
import com.xiaoma.pet.model.StoreGoodsInfo;
import com.xiaoma.pet.ui.mall.PetMallActivity;
import com.xiaoma.pet.ui.view.PetToast;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Gillben on 2018/12/25 0025
 * <p>
 * desc: 支付页
 */
public class PayFragment extends BaseFragment {

    private static final String TAG = PayFragment.class.getSimpleName();
    private static final String NEED_PAY_FOOD_INFO = "NEED_PAY_FOOD_INFO";
    private static final String TOTAL_CAR_COIN_TAG = "TOTAL_CAR_COIN_TAG";
    private ImageView mGoodsIcon;
    private TextView mGoodsName;
    private TextView mGoodsDesc;
    private TextView mGoodsPrice;
    private TextView mPayCounter;
    private TextView mCoinTotal;
    private TextView mNeedPayPrice;
    private TextView mAddText;
    private TextView mLessText;
    //    private TextView mNotPayPrompt;
    private LinearLayout buyCounterLinearLayout;
    private Button mConfirmPay;

    private OnPayCallback onPayCallback;
    private StoreGoodsInfo mStoreGoodsInfo;
    private int totalCarCoin;
    private NewGuide newGuide;
    private ILoadDataOfMallListener loadDataOfMallListener;


    public static PayFragment newInstance(StoreGoodsInfo storeGoodsInfo, int totalCarCoin) {
        PayFragment payFragment = new PayFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(NEED_PAY_FOOD_INFO, storeGoodsInfo);
        bundle.putInt(TOTAL_CAR_COIN_TAG, totalCarCoin);
        payFragment.setArguments(bundle);
        return payFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onPayCallback = (OnPayCallback) getActivity();
        loadDataOfMallListener = (ILoadDataOfMallListener) getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mStoreGoodsInfo = bundle.getParcelable(NEED_PAY_FOOD_INFO);
            totalCarCoin = bundle.getInt(TOTAL_CAR_COIN_TAG);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay, container, false);
        initView(view);
        addListener();
        initData();
        return view;
    }

    private void initView(View view) {
        mGoodsIcon = view.findViewById(R.id.iv_pay_food_icon);
        mGoodsName = view.findViewById(R.id.tv_pay_food_name);
        mGoodsDesc = view.findViewById(R.id.tv_pay_food_desc);
        mGoodsPrice = view.findViewById(R.id.tv_pay_price_text);
        mPayCounter = view.findViewById(R.id.tv_pay_counter);
        mCoinTotal = view.findViewById(R.id.mine_coin_total_text);
        mNeedPayPrice = view.findViewById(R.id.need_pay_coin_total_text);
        mAddText = view.findViewById(R.id.tv_add_text);
        mLessText = view.findViewById(R.id.tv_less_text);
//        mNotPayPrompt = view.findViewById(R.id.tv_not_pay);
        buyCounterLinearLayout = view.findViewById(R.id.pay_counter_linear);
        mConfirmPay = view.findViewById(R.id.confirm_pay_bt);

    }


    private void addListener() {
        mConfirmPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissGuideWindow();
                pay();
            }
        });

        mAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculationShoppingCount(true);
            }
        });

        mLessText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculationShoppingCount(false);
            }
        });

        mPayCounter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                limitPayCounter(text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void initData() {
        if (mStoreGoodsInfo == null) {
            KLog.e(TAG, "StoreGoodsInfo is null.");
            return;
        }

        ImageLoader.with(this)
                .load(mStoreGoodsInfo.getGoodsIcon())
                .placeholder(R.drawable.goods_list_placeholder)
                .into(mGoodsIcon);
        mGoodsName.setText(mStoreGoodsInfo.getGoodsName());
        int tempPrice = mStoreGoodsInfo.getGoodsPrice();
        mGoodsPrice.setText(String.valueOf(tempPrice));
        mNeedPayPrice.setText(getString(R.string.need_pay_car_coin, tempPrice));
        mCoinTotal.setText(getString(R.string.total_car_coin, totalCarCoin));
        if (GoodsType.FOOD.equals(mStoreGoodsInfo.getGoodsType())) {
            mGoodsDesc.setText(Html.fromHtml(getString(R.string.help_pet_supply_energy_desc, mStoreGoodsInfo.getEnergy())));
        } else {
            mGoodsDesc.setText(mStoreGoodsInfo.getGoodsDesc());
        }


        //进入检查商品单价是否高于我的车币
        checkTotalPriceAndMineCoin((int) Double.parseDouble(mGoodsPrice.getText().toString()));

        //检测是否已拥有,针对于非食物系商品
        if (!mStoreGoodsInfo.getGoodsType().equals(GoodsType.FOOD)) {
            if (mStoreGoodsInfo.isBuy()) {
                mConfirmPay.setText(R.string.has_current_resource);
                confirmPayButton(false);
//                mNotPayPrompt.setVisibility(View.GONE);
            }
        }

        if (!mStoreGoodsInfo.getGoodsType().equals(GoodsType.FOOD)) {
            buyCounterLinearLayout.setVisibility(View.GONE);
        }

        shouldShowGuideWindow();
    }


    private void calculationShoppingCount(boolean isAdd) {
        String curCount = mPayCounter.getText().toString();

        if (TextUtils.isEmpty(curCount)) {
            KLog.w(TAG, "No specific purchase quantity.");
            return;
        }

        int convert = Integer.parseInt(curCount);

        if (isAdd) {
            int temp = convert + 1;
            if (temp > 99) {
                PetToast.showToast(mContext, R.string.max_buy_goods_number);
                return;
            }
            mPayCounter.setText(String.valueOf(++convert));
        } else {
            mPayCounter.setText(String.valueOf(--convert));
        }
    }


    private void limitPayCounter(CharSequence count) {
        if (!TextUtils.isEmpty(count)) {
            int tempNumber = Integer.parseInt(count.toString());
            if (tempNumber == 1) {
                mLessText.setEnabled(false);
            } else {
                mLessText.setEnabled(true);
            }

            //计算支付总价格
            String tempPrice = mGoodsPrice.getText().toString();
            int unitPrice = Integer.parseInt(tempPrice);
            int payTotalPrice = tempNumber * unitPrice;
            mNeedPayPrice.setText(getString(R.string.need_pay_car_coin, payTotalPrice));

            checkTotalPriceAndMineCoin(payTotalPrice);
        }
    }

    private void checkTotalPriceAndMineCoin(int payTotalPrice) {
        String tempCoin = mCoinTotal.getText().toString();
        if (TextUtils.isEmpty(tempCoin)) {
            KLog.w(TAG, "Car coin is null.");
            return;
        }

        int mineCarCoin = Integer.parseInt(tempCoin);
        if (mineCarCoin < payTotalPrice) {
            confirmPayButton(false);
        } else {
            confirmPayButton(true);
        }
    }


    private void confirmPayButton(boolean enable) {
        mConfirmPay.setEnabled(enable);
        if (enable) {
            mConfirmPay.setAlpha(1f);
//            mNotPayPrompt.setVisibility(View.GONE);
        } else {
            mConfirmPay.setAlpha(0.5f);
//            mNotPayPrompt.setVisibility(View.VISIBLE);
        }
    }


    // 支付处理
    private void pay() {
        if (!NetworkUtils.isConnected(mContext)) {
            if (loadDataOfMallListener != null) {
                loadDataOfMallListener.loadFailedOfMall(PetConstant.PAY_FRAGMENT_TAG);
            }
            return;
        }

        Bundle bundle = getArguments();

        if (bundle == null) {
            KLog.w("PayFragment pay() -------------- getArguments is null --------------");
            return;
        }

        final StoreGoodsInfo storeGoodsInfo = bundle.getParcelable(NEED_PAY_FOOD_INFO);
        int buyNumbers = Integer.parseInt(mPayCounter.getText().toString());

        if (storeGoodsInfo == null) {
            KLog.w("PayFragment pay() -------------- StoreGoodsInfo is null --------------");
            return;
        }

        PetToast.showLoading(mContext, R.string.pet_loading_text);
        RequestManager.buyPetFood(storeGoodsInfo.getId(), buyNumbers, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PetToast.dismissLoading();
                        if (onPayCallback != null) {
                            onPayCallback.paySuccess(storeGoodsInfo);
                            if (storeGoodsInfo.getGoodsType().equals(GoodsType.DECORATOR)) {
                                XmTracker.getInstance().uploadEvent(-1, TrackerCountType.BUYACCESSORIES.getType());
                            } else if (storeGoodsInfo.getGoodsType().equals(GoodsType.COS_PLAY)) {
                                XmTracker.getInstance().uploadEvent(-1, TrackerCountType.BUYROLE.getType());
                            }
                        }
                    }
                }, 1000);
            }

            @Override
            public void onFailure(int code, String msg) {
                PetToast.dismissLoading();
                PetToast.showException(mContext, R.string.buy_failed);
            }
        });
    }


    public interface OnPayCallback {
        void paySuccess(StoreGoodsInfo storeGoodsInfo);
    }

    private void shouldShowGuideWindow() {
        boolean shouldShowGuide = GuideDataHelper.shouldShowGuide(getActivity(), GuideConstants.PET_SHOWED, GuideConstants.PET_GUIDE_FIRST, false);
        if (!shouldShowGuide) return;
        showGuideWindow();
    }

    private void showGuideWindow() {
        final PetMallActivity petMallActivity = (PetMallActivity) getActivity();
        final View targetView = petMallActivity.getNavigationBack();
        if (targetView == null) return;
        targetView.post(new Runnable() {
            @Override
            public void run() {
                Rect viewRect = NewGuide.getViewRect(targetView);
                Rect targetRect = new Rect(viewRect.left, viewRect.top + (viewRect.height() / 2 - 92), viewRect.right, viewRect.top + (viewRect.height() / 2 + 92));
                newGuide = NewGuide.with(getActivity())
                        .setLebal(GuideConstants.PET_SHOWED)
                        .setTargetView(targetView)
                        .setHighLightRect(targetRect)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .setGuideLayoutId(R.layout.guide_view_confirm_pay)
                        .setViewHandId(R.id.iv_gesture)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setTargetViewTranslationX(0.05f)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .setCallBack(new GuideCallBack() {
                            @Override
                            public void onHighLightClicked() {
                                dismissGuideWindow();
                                petMallActivity.onBackPressed();
                                petMallActivity.showRepositoryGuide();
                            }
                        })
                        .build();
                newGuide.showGuide();
            }
        });
    }

    private void dismissGuideWindow() {
        if (newGuide == null) return;
        newGuide.dismissGuideWindow();
        newGuide = null;
    }

}
