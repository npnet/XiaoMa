package com.xiaoma.launcher.service.adapter;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.service.model.NewServiceBean;

import java.util.List;
import java.util.Locale;

/**
 * @author taojin
 * @date 2019/1/18
 */
public class ServiceAdapter extends BaseMultiItemQuickAdapter<NewServiceBean.ItemsBean, BaseViewHolder> {


    public static final int CAR_TITLE = 1;
    public static final int FOOD_TITLE = 2;
    public static final int MOVIE_TITLE = 3;
    public static final int HOTEL_TITLE = 4;
    public static final int ATTRACTIONS_TITLE = 5;
    public static final int ITEM = 0;
    private RequestManager mRequestManager;
    private final String NEAR_BY_GAS_STATION_PAGE = "NearbyGasStationPage";
    private final String MAINTENANCE_PERIOD_PAGE = "MaintenancePeriodPage";
    private Locale locale;
    private String maintenance;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ServiceAdapter(List<NewServiceBean.ItemsBean> data, RequestManager requestManager) {
        super(data);
        addItemType(CAR_TITLE, R.layout.item_service_title);
        addItemType(FOOD_TITLE, R.layout.item_service_title);
        addItemType(MOVIE_TITLE, R.layout.item_service_title);
        addItemType(HOTEL_TITLE, R.layout.item_service_title);
        addItemType(ATTRACTIONS_TITLE, R.layout.item_service_title);
        addItemType(ITEM, R.layout.item_service_item);
        mRequestManager = requestManager;
    }

    @Override
    protected void convert(BaseViewHolder helper, NewServiceBean.ItemsBean item) {
        int type = item.getItemType();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = mContext.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = mContext.getResources().getConfiguration().locale;
        }
        switch (type) {
            case CAR_TITLE:
//                setImage(item.getImgUrl(), (ImageView) helper.getView(R.id.iv_item_service_car));
                setImage(R.drawable.bg_car, (ImageView) helper.getView(R.id.iv_item_service_car));
                helper.setText(R.id.tv_item_service_car, item.getName());
                helper.setText(R.id.tv_item_service_car_ens, item.getType());
                break;
            case FOOD_TITLE:
                View foodTitle = helper.getView(R.id.item_view);
                RelativeLayout.LayoutParams foodTitleLayoutParams = new RelativeLayout.LayoutParams(
                        mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_width),
                        mContext.getResources().getDimensionPixelSize(R.dimen.card_height));
                foodTitleLayoutParams.leftMargin = 160;
                foodTitle.setLayoutParams(foodTitleLayoutParams);

//                setImage(item.getImgUrl(), (ImageView) helper.getView(R.id.iv_item_service_car));
                setImage(R.drawable.bg_food, (ImageView) helper.getView(R.id.iv_item_service_car));
                helper.setText(R.id.tv_item_service_car, item.getName());
                helper.setText(R.id.tv_item_service_car_ens, item.getType());
                break;
            case MOVIE_TITLE:
                View movieTitle = helper.getView(R.id.item_view);
                RelativeLayout.LayoutParams movieTitleLayoutParams = new RelativeLayout.LayoutParams(
                        mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_width),
                        mContext.getResources().getDimensionPixelSize(R.dimen.card_height));
                movieTitleLayoutParams.leftMargin = 160;
                movieTitle.setLayoutParams(movieTitleLayoutParams);
//                setImage(item.getImgUrl(), (ImageView) helper.getView(R.id.iv_item_service_car));
                setImage(R.drawable.bg_movie, (ImageView) helper.getView(R.id.iv_item_service_car));
                helper.setText(R.id.tv_item_service_car, item.getName());
                helper.setText(R.id.tv_item_service_car_ens, item.getType());
                break;
            case HOTEL_TITLE:
                View hotelTitle = helper.getView(R.id.item_view);
                RelativeLayout.LayoutParams hotelTitleLayoutParams = new RelativeLayout.LayoutParams(
                        mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_width),
                        mContext.getResources().getDimensionPixelSize(R.dimen.card_height));
                hotelTitleLayoutParams.leftMargin = 160;
                hotelTitle.setLayoutParams(hotelTitleLayoutParams);
//                setImage(item.getImgUrl(), (ImageView) helper.getView(R.id.iv_item_service_car));
                setImage(R.drawable.bg_hotel, (ImageView) helper.getView(R.id.iv_item_service_car));
                helper.setText(R.id.tv_item_service_car, item.getName());
                helper.setText(R.id.tv_item_service_car_ens, item.getType());
                break;
            case ATTRACTIONS_TITLE:
                View attractionsTitle = helper.getView(R.id.item_view);
                RelativeLayout.LayoutParams attractionsTitleLayoutParams = new RelativeLayout.LayoutParams(
                        mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_width),
                        mContext.getResources().getDimensionPixelSize(R.dimen.card_height));
                attractionsTitleLayoutParams.leftMargin = 160;
                attractionsTitle.setLayoutParams(attractionsTitleLayoutParams);
//                setImage(item.getImgUrl(), (ImageView) helper.getView(R.id.iv_item_service_car));
                setImage(R.drawable.bg_scenic, (ImageView) helper.getView(R.id.iv_item_service_car));
                helper.setText(R.id.tv_item_service_car, item.getName());
                helper.setText(R.id.tv_item_service_car_ens, item.getType());
                break;
            default:
                if (NEAR_BY_GAS_STATION_PAGE.equals(item.getPageType())) {
                    //获取当前油量
                    int value = XmCarVendorExtensionManager.getInstance().getFuelWarning();
                    if (1 == value) {
                        helper.getView(R.id.tv_item_service_car_warn).setVisibility(View.VISIBLE);
                    } else {
                        helper.getView(R.id.tv_item_service_car_warn).setVisibility(View.INVISIBLE);
                    }
                } else {
                    helper.getView(R.id.tv_item_service_car_warn).setVisibility(View.INVISIBLE);
                }
                setImage(item.getImgUrl(), (ImageView) helper.getView(R.id.iv_item_service_car_one));

                if(MAINTENANCE_PERIOD_PAGE.equals(item.getPageType())){
                    helper.setText(R.id.tv_item_service_car_one, getMaintenance());
                }else {
                    helper.setText(R.id.tv_item_service_car_one, locale.getCountry().equals("CN")?item.getName():item.getCategoryName());
                }
                break;
        }
    }

    private void setImage(String resUrl, ImageView imageView) {

        mRequestManager.load(resUrl)
                .placeholder(R.drawable.service_bg)
                .error(R.drawable.service_bg)
                .into(imageView);
    }

    private void setImage(int resource, ImageView imageView) {
        imageView.setImageResource(resource);
    }

    public String getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(String maintenance) {
        this.maintenance = maintenance;
    }
}
