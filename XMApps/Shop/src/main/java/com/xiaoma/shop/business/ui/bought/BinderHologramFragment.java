package com.xiaoma.shop.business.ui.bought;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.shop.R;
import com.xiaoma.ui.dialog.OnViewClickListener;
import com.xiaoma.ui.dialog.XmDialog;

import java.util.Objects;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/5 0005 11:43
 *   desc:   绑定全息影像
 * </pre>
 */
public class BinderHologramFragment extends BaseFragment implements View.OnClickListener {


    private ConstraintLayout mBindAccountLayout;
    private ImageView mDownloadAppImage;
    private ImageView mBindAccountImage;

    private ConstraintLayout mBoundAccountLayout;
    private ImageView mBoundAccountImage;
    private TextView mBoundAccountDesc;
    private Button mCancelBoundAccountBt;

    public static BinderHologramFragment newInstance() {
        return new BinderHologramFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_binder_hologram, container, false);
        return onCreateWrapView(contentView);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

    }

    private void initView(View view) {
        mBindAccountLayout = view.findViewById(R.id.bind_hologram_account_root);
        mDownloadAppImage = view.findViewById(R.id.iv_load_app_qr_code);
        mBindAccountImage = view.findViewById(R.id.iv_phone_app_qr_code);

        mBoundAccountLayout = view.findViewById(R.id.bound_hologram_account_root);
        mBoundAccountImage = view.findViewById(R.id.iv_bound_hologram_icon);
        mBoundAccountDesc = view.findViewById(R.id.tv_bound_account_content);
        mCancelBoundAccountBt = view.findViewById(R.id.bt_cancel_bound);

        mCancelBoundAccountBt.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_cancel_bound) {
            cancelBoundDialog();
        }
    }


    private void cancelBoundDialog() {
        View childView = View.inflate(mContext, R.layout.dialog_car_coin_pay, null);
        TextView contentDesc = childView.findViewById(R.id.tv_pay_message);
        TextView cancelBoundBt = childView.findViewById(R.id.confirm_bt);
        contentDesc.setText(R.string.cancel_bound_account_desc);
        cancelBoundBt.setText(R.string.confirm_cancel_bound);
        contentDesc.setGravity(Gravity.START);

        XmDialog dialog = new XmDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(childView)
                .setWidth(mContext.getResources().getDimensionPixelOffset(R.dimen.width_big_dialog))
                .setHeight(mContext.getResources().getDimensionPixelOffset(R.dimen.height_big_dialog))
                .setCancelableOutside(false)
                .addOnClickListener(R.id.confirm_bt, R.id.cancel_bt)
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(View view, XmDialog xmDialog) {
                        xmDialog.dismiss();
                        if (view.getId() == R.id.confirm_bt) {
                            //TODO 处理解绑全息账号逻辑
                        }
                    }
                })
                .create();
        dialog.show();
    }
}
