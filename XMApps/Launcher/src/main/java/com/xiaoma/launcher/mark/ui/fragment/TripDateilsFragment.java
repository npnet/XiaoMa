package com.xiaoma.launcher.mark.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.AppHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.LauncherUtils;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.views.VisibilityFragment;
import com.xiaoma.launcher.mark.model.MarkPhotoBean;
import com.xiaoma.launcher.mark.ui.activity.MarkMainActivity;
import com.xiaoma.launcher.mark.vm.TripAlblumVM;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.network.utils.HttpUtils;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;

@PageDescComponent(EventConstants.PageDescribe.TripDateilsActivityPagePathDesc)
public class TripDateilsFragment extends VisibilityFragment implements View.OnClickListener {

    private static final String TAG = "ripDateilsFragment";
    private Button mDelete;
    private MarkPhotoBean mMarkPhotoBean;
    private ImageView mFacialImg;
    private ImageView mCamPic;
    private ImageView mWeatherImg;
    private TextView mFacialText;
    private TextView mTripTime;
    private TextView mTripAddress;
    private static final int COUNT_DOWN_TIME = 1000;
    private static final int FINISH_TOAST = 500;
    private static MarkMainActivity mMarkMainActivity;
    private static TripAlbumFragment mTripAlbumFragment;
    private static TripMarkListFragment mTripMarkListFragment;
    private TripAlblumVM mTripAlbumVM;
    private Runnable countDownFinish = new Runnable() {
        @Override
        public void run() {
            popBack();
        }
    };
    private Runnable finishToase = new Runnable() {
        @Override
        public void run() {
            XMToast.cancelToast();
        }
    };

    private int[] drawableIds = {R.drawable.icon_weather_sunny,
            R.drawable.icon_weather_cloudy,
            R.drawable.icon_weather_overcast,
            R.drawable.icon_weather_shower,
            R.drawable.icon_weather_thunderstorms,
            R.drawable.icon_weather_light_rain,
            R.drawable.icon_weather_moderate_rain,
            R.drawable.icon_weather_heavy_rain,
            R.drawable.icon_weather_storm_rain,
            R.drawable.icon_weather_light_snow,
            R.drawable.icon_weather_moderate_snow,
            R.drawable.icon_weather_heavy_snow,
            R.drawable.icon_weather_sleet,
            R.drawable.icon_weather_hail,
            R.drawable.icon_weather_sandstorm};
    private Button mPhotoUpload;
    private TextView mPhotoSaveType;


    public static TripDateilsFragment newInstance(TripMarkListFragment tripMarkListFragment, TripAlbumFragment tripAlbumFragment, MarkMainActivity markMainActivity) {
        mMarkMainActivity = markMainActivity;
        mTripAlbumFragment = tripAlbumFragment;
        mTripMarkListFragment = tripMarkListFragment;
        return new TripDateilsFragment();
    }

    public String getTAG() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_dateils, container, false);
        return super.onCreateWrapView(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initView();
        initData();
    }


    private void bindView(View view) {
        mDelete = view.findViewById(R.id.delete);
        mFacialImg = view.findViewById(R.id.facial_img);
        mCamPic = view.findViewById(R.id.cam_pic);
        mWeatherImg = view.findViewById(R.id.weather_img);
        mFacialText = view.findViewById(R.id.facial_text);
        mTripTime = view.findViewById(R.id.trip_time);
        mTripAddress = view.findViewById(R.id.trip_address);
        mPhotoUpload = view.findViewById(R.id.photo_upload);
        mPhotoSaveType = view.findViewById(R.id.photo_save_type);
    }

    private void initView() {
        mDelete.setOnClickListener(this);
        mPhotoUpload.setOnClickListener(this);
    }

    private void initData() {
        mMarkPhotoBean = mMarkMainActivity.getMarkPhotoBean();
        if (mMarkPhotoBean != null) {
            if (StringUtil.isNotEmpty(mMarkPhotoBean.getPhotoPath())) {
                ImageLoader.with(this).load(mMarkPhotoBean.getPhotoPath()).into(mCamPic);
            }
            setFacial(mMarkPhotoBean.getFacialType());
            if (StringUtil.isNotEmpty(mMarkPhotoBean.getPhotoTime())) {
                mTripTime.setText(mMarkPhotoBean.getPhotoTime());
            }
            if (StringUtil.isNotEmpty(mMarkPhotoBean.getPhotoAddress())) {
                mTripAddress.setText(mMarkPhotoBean.getPhotoAddress());
            }
            if (StringUtil.isNotEmpty(mMarkPhotoBean.getWeather())) {
                setWeatherIcon(mMarkPhotoBean.getWeather());
            }
            if (mMarkPhotoBean.isSaveType()) {
                mPhotoUpload.setEnabled(false);
                mPhotoUpload.setTextColor(getResources().getColor(R.color.color_home_menus_unchecked));
                mPhotoSaveType.setText(getString(R.string.already_save_service));
            } else {
                mPhotoUpload.setEnabled(true);
                mPhotoSaveType.setText(getString(R.string.click_save_service));
            }
        }

        mTripAlbumVM = ViewModelProviders.of(this).get(TripAlblumVM.class);
        mTripAlbumVM.getPhotoUpload().observe(this, new Observer<XmResource<Boolean>>() {
            @Override
            public void onChanged(@Nullable XmResource<Boolean> booleanXmResource) {
                if (booleanXmResource == null) {
                    return;
                }

                booleanXmResource.handle(new OnCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        if (data) {
                            mPhotoUpload.setEnabled(false);
                            mPhotoUpload.setTextColor(getResources().getColor(R.color.color_home_menus_unchecked));
                            mPhotoSaveType.setText(getString(R.string.already_save_service));
                        } else {
                            mPhotoSaveType.setText(getString(R.string.click_save_service));
                        }
                    }

                    @Override
                    public void onFailure(String msg) {

                    }

                    @Override
                    public void onError(int code, String message) {

                    }
                });

            }
        });

        mTripAlbumVM.getUploadPhoto().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> stringXmResource) {
                if (stringXmResource == null) {
                    return;
                }
                stringXmResource.handle(new OnCallback<String>() {
                    @Override
                    public void onSuccess(String data) {

                        mPhotoUpload.setEnabled(false);
                        mPhotoUpload.setTextColor(getResources().getColor(R.color.color_home_menus_unchecked));
                        mPhotoSaveType.setText(getString(R.string.already_save_service));

                        if (LauncherConstants.MARK_SAVE_SUCCESS.equals(data)) {
                            XMToast.showToast(getContext(), getString(R.string.saved_to_cloud_disk), mContext.getDrawable(R.drawable.toast_success));
                        } else if (LauncherConstants.MARK_SAVE_SUCCESS_UPPERLIMIT.equals(data)) {
                            XMToast.showToast(getContext(), getString(R.string.fast_cloud_storage_limit), mContext.getDrawable(R.drawable.toast_error));
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        if ("-1".equals(message)) {
                            XMToast.showToast(getContext(), getString(R.string.photo_damage), mContext.getDrawable(R.drawable.toast_error));
                            return;
                        }
                        if (LauncherConstants.MARK_SAVE_ERROR_UPPERLIMIT.equals(message)) {
                            XMToast.showToast(getContext(), getString(R.string.upload_file_aleary_upper_limit), mContext.getDrawable(R.drawable.toast_error));
                        } else if (LauncherConstants.MARK_SAVE_ERROR.equals(message)) {
                            XMToast.showToast(getContext(), getString(R.string.network_anomaly), mContext.getDrawable(R.drawable.toast_error));
                        }
                    }
                });
            }
        });

        mTripAlbumVM.getPhoto(mMarkPhotoBean);
    }

    private void setWeatherIcon(String desc) {
        if (desc == null || desc.isEmpty()) {
            return;
        }
        String[] weatherArray = AppHolder.getInstance().getAppContext().getResources().getStringArray(R.array.weatherInfo);
        for (int i = 0; i < weatherArray.length; i++) {
            if (desc.contains(weatherArray[i])) {
                mWeatherImg.setImageResource(drawableIds[i]);
            }
        }
    }

    private void setFacial(int facialType) {
        switch (facialType) {
            case 0:
                mFacialImg.setImageResource(R.drawable.happy_flat);
                mFacialText.setText(R.string.happy_flat_trip_text);
                break;
            case 1:
                mFacialImg.setImageResource(R.drawable.stupid_flat);
                mFacialText.setText(R.string.stupid_flat_trip_text);
                break;
            case 2:
                mFacialImg.setImageResource(R.drawable.cry_flat);
                mFacialText.setText(R.string.cry_flat_trip_text);
                break;
            default:
        }
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.MARK_RECORD_PHOTO_DATEILS_DELETE})//按钮对应的名称
    @ResId({R.id.delete})//按钮对应的R文件id
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete:
                if (mMarkPhotoBean != null) {
                    showClearDialog();
                } else {
                    XMToast.showToast(mContext, R.string.clean_failure);
                }
                break;
            case R.id.photo_upload:
                if (!NetworkUtils.isConnected(mContext)) {
                    XMToast.showToast(getContext(), getString(R.string.network_anomaly_save_local), mContext.getDrawable(R.drawable.toast_error));
                    return;
                }
                if (mMarkPhotoBean != null) {
                    if (mTripAlbumVM != null) {
                        mTripAlbumVM.upPhoto(mMarkPhotoBean);
                    }
                } else {
                    XMToast.showToast(mContext, R.string.not_photo_falue);
                }
                break;
        }
    }

    private void showClearDialog() {
        ConfirmDialog dialog = new ConfirmDialog(mMarkMainActivity);
        dialog.setContent(getString(R.string.mark_clean_message))
                .setPositiveButton(getString(R.string.mark_clean_ok), new View.OnClickListener() {
                    @Override
                    @NormalOnClick(EventConstants.NormalClick.MARK_RECORD_PHOTO_DATEILS_DELETE_SURE)
                    public void onClick(View v) {
                        if (StringUtil.isNotEmpty(mMarkPhotoBean.getPhotoPath())) {
                            HttpUtils.deleteFile(mMarkPhotoBean.getPhotoPath());
                        }
                        LauncherUtils.getDBManager().delete(mMarkPhotoBean);
                        XMToast.showToast(mContext, R.string.trip_date_delete);
                        ThreadDispatcher.getDispatcher().postOnMainDelayed(countDownFinish, COUNT_DOWN_TIME);
                        ThreadDispatcher.getDispatcher().postOnMainDelayed(finishToase, FINISH_TOAST);
                        if (mTripAlbumFragment != null) {
                            mTripAlbumFragment.adapterDeleteListener();
                        }
                        if (mTripMarkListFragment != null) {
                            mTripMarkListFragment.adapterDeleteListener(mMarkPhotoBean);
                        }

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.merk_clean_no), new View.OnClickListener() {
                    @Override
                    @NormalOnClick(EventConstants.NormalClick.MARK_RECORD_PHOTO_DATEILS_DELETE_CANCEL)
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
