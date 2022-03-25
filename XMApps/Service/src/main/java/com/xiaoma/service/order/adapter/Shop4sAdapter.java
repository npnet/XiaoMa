package com.xiaoma.service.order.adapter;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.utils.MapUtil;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.common.manager.ServiceBlueToothPhoneManager;
import com.xiaoma.service.order.model.ShopBean;
import com.xiaoma.service.order.ui.ShopDetailActivity;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;

import java.text.DecimalFormat;

/**
 * 4s店 adapter
 * Created by zhushi.
 * Date: 2018/11/16
 */
public class Shop4sAdapter extends XMBaseAbstractBQAdapter<ShopBean, BaseViewHolder> {
    private int mSelectedPos = -1;
    private double distance;
    private SelectShopListener mSelectShopListener;
    public static String INTENT_SHOP = "intent_shop";

    public Shop4sAdapter() {
        super(R.layout.item_shop_v2);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final ShopBean item) {
        LatLng locationInfo = LocationManager.getInstance().getCurrentPosition();
        final ImageView checkIv = helper.getView(R.id.check_iv);
        final ImageView shopPhone = helper.getView(R.id.shop_phone);
        final ImageView shopNavigation = helper.getView(R.id.shop_navigation);
        final TextView shopName = helper.getView(R.id.shop_name);
        final TextView shopAddress = helper.getView(R.id.shop_address);
        final TextView shopDistance = helper.getView(R.id.shop_distance);
        final LinearLayout detailsLinear = helper.getView(R.id.details_linear);
        final RelativeLayout shopItemLinear = helper.getView(R.id.shop_item_linear);
        checkIv.setSelected(mSelectedPos == helper.getAdapterPosition());
        shopItemLinear.setSelected(mSelectedPos == helper.getAdapterPosition());
        shopName.setText(item.getVDEALERNAME());
        shopAddress.setText(item.getVADDRESS());
        detailsLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShopDetailActivity.class);
                intent.putExtra(INTENT_SHOP, item);
                mContext.startActivity(intent);
            }
        });

        if (locationInfo != null) {
            distance = MapUtil.calculateLineDistance(new LatLng(locationInfo.latitude, locationInfo.longitude),
                    new LatLng(item.getPATHLAT(), item.getPATHLNG())) / 1000;
            shopDistance.setText(mContext.getString(R.string.shop_address_distance, new DecimalFormat("0.00").format(distance)));
        } else {
            shopDistance.setText(mContext.getString(R.string.location_ing));
        }
        checkIv.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.select4S})
            public void onClick(View v) {
                if (mSelectedPos != helper.getAdapterPosition()) {
                    checkIv.setSelected(true);
                    shopItemLinear.setSelected(true);
                    if (mSelectedPos != -1) {
                        notifyItemChanged(mSelectedPos, 0);
                    }
                    mSelectedPos = helper.getAdapterPosition();
                    if (mSelectShopListener != null) {
                        mSelectShopListener.setSelectShop(true);
                    }
                } else {
                    checkIv.setSelected(false);
                    shopItemLinear.setSelected(false);
                    mSelectedPos = -1;
                    if (mSelectShopListener != null) {
                        mSelectShopListener.setSelectShop(false);
                    }
                }
            }
        });
        shopPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.phone})
            public void onClick(View v) {
                showPhoneDialog(mContext.getString(R.string.store_phone), item.getVTEL());
            }
        });
        shopNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.navi})
            public void onClick(View v) {
                showNaviDialog(mContext.getString(R.string.prompt), mContext.getString(R.string.open_navigation), item);
            }
        });
    }

    public double getLocationDistance() {
        return distance;
    }

    public void setSelectedPos(int pos) {
        this.mSelectedPos = pos;
    }

    public int getSelectedPos() {
        return mSelectedPos;
    }

    private void showPhoneDialog(String title, final String message) {
        final ConfirmDialog dialog = new ConfirmDialog((FragmentActivity) mContext);
        dialog.setTitle(title)
                .setContent(message)
                .setPositiveButton(mContext.getString(R.string.dial), new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.phoneSure})
                    public void onClick(View v) {
                        ServiceBlueToothPhoneManager.getInstance().callPhone(message);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(mContext.getString(R.string.dialog_cancel), new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.phoneSure})
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showNaviDialog(String title, String message, final ShopBean item) {
        final ConfirmDialog dialog = new ConfirmDialog((FragmentActivity) mContext);
        dialog.setTitle(title)
                .setContent(message)
                .setPositiveButton(mContext.getString(R.string.sure), new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.naviSure})
                    public void onClick(View v) {
                        int ret = XmMapNaviManager.getInstance().startNaviToPoi(item.getVDEALERNAME(), item.getVADDRESS(), item.getPATHLNG(), item.getPATHLAT());
                        if (ret == -1) {
                            XMToast.showToast(mContext, R.string.open_navi);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(mContext.getString(R.string.dialog_cancel), new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.naviCancel})
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getVDEALERNAME(), getData().get(position).getPKEY() + "");
    }

    public interface SelectShopListener {
        void setSelectShop(boolean select);
    }

    public void setSelectShopListener(SelectShopListener selectShopListener) {
        this.mSelectShopListener = selectShopListener;
    }
}
