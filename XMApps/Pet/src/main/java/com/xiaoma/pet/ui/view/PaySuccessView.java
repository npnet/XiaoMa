package com.xiaoma.pet.ui.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.image.ImageLoader;
import com.xiaoma.pet.R;
import com.xiaoma.pet.model.StoreGoodsInfo;

/**
 * Created by Gillben on 2019/1/2 0002
 * <p>
 * desc: 付款成功提示
 */
public class PaySuccessView extends DialogFragment {


    private static final String GOODS_INFO = "GOODS_INFO";
    private ImageView mFoodIcon;
    private TextView mFoodDescText;
    private OnCancelListener onCancelListener;

    public static PaySuccessView newInstance(StoreGoodsInfo storeGoodsInfo) {
        Bundle args = new Bundle();
        PaySuccessView fragment = new PaySuccessView();
        args.putParcelable(GOODS_INFO, storeGoodsInfo);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.pay_success_view, container, false);
        initView(contentView);
        return contentView;
    }

    private void initView(View contentView) {
        mFoodIcon = contentView.findViewById(R.id.iv_food_icon);
        mFoodDescText = contentView.findViewById(R.id.tv_food_desc);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();

        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(null);
            window.setWindowAnimations(R.style.PaySuccessViewAnimation);
        }

        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 1500);
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            StoreGoodsInfo storeGoodsInfo = bundle.getParcelable(GOODS_INFO);
            if (storeGoodsInfo != null) {
                ImageLoader.with(this)
                        .load(storeGoodsInfo.getGoodsIcon())
                        .placeholder(R.drawable.goods_list_placeholder)
                        .into(mFoodIcon);
                mFoodDescText.setText(getString(R.string.food_desc, storeGoodsInfo.getGoodsName()));
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onCancelListener != null) {
            onCancelListener.cancel();
        }
    }


    public void setOnCancelListener(OnCancelListener listener) {
        this.onCancelListener = listener;
    }


    public interface OnCancelListener {
        void cancel();
    }
}
