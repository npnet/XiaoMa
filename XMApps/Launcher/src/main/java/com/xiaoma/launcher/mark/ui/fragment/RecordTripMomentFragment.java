package com.xiaoma.launcher.mark.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
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
import com.xiaoma.launcher.common.model.Weather;
import com.xiaoma.launcher.common.views.VisibilityFragment;
import com.xiaoma.launcher.mark.model.MarkPhotoBean;
import com.xiaoma.launcher.mark.ui.activity.MarkMainActivity;
import com.xiaoma.launcher.mark.vm.TripAlblumVM;
import com.xiaoma.launcher.schedule.utils.DateUtil;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.network.utils.HttpUtils;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeUtils;
import com.xiaoma.utils.tputils.TPUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.RecordTripMomentActivityPagePathDesc)
public class RecordTripMomentFragment extends VisibilityFragment implements View.OnClickListener {

    private static final String TAG = "RecordTripMomentFragment";
    private Button mSaveBut;
    private Button mCleanBut;
    private ImageView mHappyTripImg;
    private ImageView mDullTripImg;
    private ImageView mLoseTripImg;
    private ImageView mWeatherImg;
    private int facial_type = 0;
    private static final int HAPPY_FACIAL = 0;
    private static final int DULL_FACIAL = 1;
    private static final int LOSE_FACIAL = 2;
    private int SAVE_PHOTO_NUM = 0;
    private ImageView mCamPic;
    private TextView mPhotoTime;
    private TextView mPhotoAddress;
    private LocationInfo mLocationInfo;
    private String mFileTime;
    private String mPath;
    int countdownSeconds = 10;// 开始倒计时剩余时间
    public static final String CITY_WEATHER_DETAIL = "city_weather";
    private Weather mWeather;
    public static MarkMainActivity mMarkMainActivity;
    public boolean saveType = false;
    private TextView mMostPrompt;
    private TripAlblumVM mTripAlbumVM;

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

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (countdownSeconds > 0) {
                countdownSeconds--;
                mSaveBut.setText(String.format(getString(R.string.mark_save_dwon), countdownSeconds));
                ThreadDispatcher.getDispatcher().postOnMainDelayed(mRunnable, 1000);
            } else {

                savePhoto();
                saveType = true;
            }
        }
    };


    public static RecordTripMomentFragment newInstance(MarkMainActivity markMainActivity) {
        mMarkMainActivity = markMainActivity;
        return new RecordTripMomentFragment();
    }

    public String getTAG() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_trip, container, false);
        return super.onCreateWrapView(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initView();
        initVM();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (saveType) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }

    }

    private void bindView(View view) {
        mCamPic = view.findViewById(R.id.cam_pic);
        mSaveBut = view.findViewById(R.id.save_but);
        mCleanBut = view.findViewById(R.id.clean_but);
        mPhotoTime = view.findViewById(R.id.photo_time);
        mPhotoAddress = view.findViewById(R.id.photo_address);
        mHappyTripImg = view.findViewById(R.id.happy_trip_img);
        mDullTripImg = view.findViewById(R.id.dull_trip_img);
        mLoseTripImg = view.findViewById(R.id.lose_trip_img);
        mWeatherImg = view.findViewById(R.id.weather_img);
        mMostPrompt = view.findViewById(R.id.most_prompt);
    }

    private void initView() {
        mHappyTripImg.setSelected(true);
        mSaveBut.setOnClickListener(this);
        mCleanBut.setOnClickListener(this);
        mHappyTripImg.setOnClickListener(this);
        mDullTripImg.setOnClickListener(this);
        mLoseTripImg.setOnClickListener(this);
        mMostPrompt.setOnClickListener(this);
    }

    private void initVM() {
        mWeather = TPUtils.getObject(AppHolder.getInstance().getAppContext(), CITY_WEATHER_DETAIL, Weather.class);
        mTripAlbumVM = ViewModelProviders.of(this).get(TripAlblumVM.class);
        mTripAlbumVM.getTripAlbumList().observe(this, new Observer<XmResource<List<MarkPhotoBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<MarkPhotoBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<MarkPhotoBean>>() {
                    @Override
                    public void onSuccess(List<MarkPhotoBean> data) {
                        if (!ListUtils.isEmpty(data)) {
                            SAVE_PHOTO_NUM = data.size();
                        }
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
                        if (isShowing()) {
                            if (LauncherConstants.MARK_SAVE_SUCCESS.equals(data)) {
                                // XMToast.showToast(getContext(), getString(R.string.saved_to_cloud_disk), mContext.getDrawable(R.drawable.toast_success));
                            } else if (LauncherConstants.MARK_SAVE_SUCCESS_UPPERLIMIT.equals(data)) {
                                XMToast.showToast(getContext(), getString(R.string.fast_cloud_storage_limit), mContext.getDrawable(R.drawable.toast_error));
                            }

                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        if (isShowing()) {
                            if (LauncherConstants.MARK_SAVE_ERROR_UPPERLIMIT.equals(message)) {
                                XMToast.showToast(getContext(), getString(R.string.upload_file_aleary_upper_limit), mContext.getDrawable(R.drawable.toast_error));
                            } else if (LauncherConstants.MARK_SAVE_ERROR.equals(message)) {
                                XMToast.showToast(getContext(), getString(R.string.network_anomaly_save_local), mContext.getDrawable(R.drawable.toast_error));
                            }

                        }
                    }
                });
            }
        });
        mTripAlbumVM.getAllTrip();
    }


    private void initData() {
        mLocationInfo = LocationManager.getInstance().getCurrentLocation();

        if (mWeather != null) {
            if (StringUtil.isNotEmpty(mWeather.getWeather())) {
                setWeatherIcon(mWeather.getWeather());
            }
        }
        mPath = mMarkMainActivity.getPhotoPath();
        if (StringUtil.isNotEmpty(mPath)) {
            ImageLoader.with(this).load(mPath).into(mCamPic);
        }
        if (mLocationInfo != null) {
            mLocationInfo.getLongitude();
            mPhotoAddress.setText(mLocationInfo.getAddress());
        } else {
            mPhotoAddress.setText(getString(R.string.not_nvi_info));
        }
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm "); // 格式化时间
        mFileTime = format.format(date) + TimeUtils.getChineseWeek(date);
        if (StringUtil.isNotEmpty(mFileTime)) {
            mPhotoTime.setText(mFileTime);
        }

        ThreadDispatcher.getDispatcher().postOnMain(mRunnable);
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

    @Override
    @NormalOnClick({EventConstants.NormalClick.MARK_RECORD_SAVE, EventConstants.NormalClick.MARK_RECORD_CLEAN, EventConstants.NormalClick.MARK_RECORD_HAPPY, EventConstants.NormalClick.MARK_RECORD_DULL, EventConstants.NormalClick.MARK_RECORD_LOSE})
    //按钮对应的名称
    @ResId({R.id.save_but, R.id.clean_but, R.id.happy_trip_img, R.id.dull_trip_img, R.id.lose_trip_img})
    //按钮对应的R文件id
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_but:
                savePhoneNum();
                break;
            case R.id.clean_but:
                deletePhoto();
                if (mRunnable != null) {
                    ThreadDispatcher.getDispatcher().removeOnMain(mRunnable);
                }
                popBack();
                break;
            case R.id.happy_trip_img:
                mHappyTripImg.setSelected(true);
                mDullTripImg.setSelected(false);
                mLoseTripImg.setSelected(false);
                facial_type = HAPPY_FACIAL;
                break;
            case R.id.dull_trip_img:
                mHappyTripImg.setSelected(false);
                mDullTripImg.setSelected(true);
                mLoseTripImg.setSelected(false);
                facial_type = DULL_FACIAL;
                break;
            case R.id.lose_trip_img:
                mHappyTripImg.setSelected(false);
                mDullTripImg.setSelected(false);
                mLoseTripImg.setSelected(true);
                facial_type = LOSE_FACIAL;
                break;
            case R.id.most_prompt:
                if (mRunnable != null) {
                    ThreadDispatcher.getDispatcher().removeOnMain(mRunnable);
                }
                showMostPromptDialog();

                break;
        }
    }

    private void savePhoneNum() {
        if (SAVE_PHOTO_NUM >= 800) {
            if (SAVE_PHOTO_NUM >= 1000) {
                XMToast.showToast(getContext(), getString(R.string.save_local_aleary_finish), mContext.getDrawable(R.drawable.toast_error));
                popBack();
                return;
            }
            if ((SAVE_PHOTO_NUM - 800) % 10 == 0) {
                XMToast.showToast(getContext(), getString(R.string.save_local_will_finish), mContext.getDrawable(R.drawable.toast_error));
                savePhoto();
            }
        } else {
            savePhoto();
        }
    }

    /**
     * 保存图片
     */
    private void savePhoto() {
        if (mRunnable != null) {
            ThreadDispatcher.getDispatcher().removeOnMain(mRunnable);
        }

        MarkPhotoBean markPhotoBean = new MarkPhotoBean();
        if (mLocationInfo != null) {
            markPhotoBean.setPhotoAddress(mLocationInfo.getAddress());
            if (StringUtil.isNotEmpty(mLocationInfo.getProvince())&&StringUtil.isNotEmpty(mLocationInfo.getCity())){
                if (mLocationInfo.getCity().endsWith("市")) {
                    String city = mLocationInfo.getCity();
                    markPhotoBean.setLocation(mLocationInfo.getProvince() + " " + city.substring(0, city.length() - 1));
                } else {
                    markPhotoBean.setLocation(mLocationInfo.getProvince() + " " + mLocationInfo.getCity());
                }
            }else {
                markPhotoBean.setSaveType(false);
                markPhotoBean.setPhotoAddress(getString(R.string.not_location_information_was_obtained));
            }

            //markPhotoBean.setLocation(mLocationInfo.getProvince() + " " + strings[i]);
            /*double lat = Math.random()*10 + mLocationInfo.getLatitude();
            double lon = Math.random()*10  + mLocationInfo.getLongitude();*/
            markPhotoBean.setLatitude(mLocationInfo.getLatitude());
            markPhotoBean.setLongitude(mLocationInfo.getLongitude());

            markPhotoBean.setSaveType(false);
        } else {
            markPhotoBean.setSaveType(false);
            markPhotoBean.setPhotoAddress(getString(R.string.not_location_information_was_obtained));
        }

        if (StringUtil.isNotEmpty(mPath)) {
            markPhotoBean.setPhotoPath(mPath);
        }
        markPhotoBean.setFacialType(facial_type);
        if (StringUtil.isNotEmpty(mFileTime))
            markPhotoBean.setPhotoTime(mFileTime);

        int year = DateUtil.getCurrentYear();
        int month = DateUtil.getCurrentMonth();
        int day = DateUtil.getCurrentDay();
        markPhotoBean.setYear(String.valueOf(year));
        markPhotoBean.setMonth(String.valueOf(month));
        markPhotoBean.setDay(String.valueOf(day));
        if (mWeather != null) {
            if (StringUtil.isNotEmpty(mWeather.getWeather())) {
                markPhotoBean.setWeather(mWeather.getWeather());
            }
        }
        LauncherUtils.getDBManager().save(markPhotoBean);
        if (mRunnable != null) {
            ThreadDispatcher.getDispatcher().removeOnMain(mRunnable);
        }
        if (!NetworkUtils.isConnected(mContext)) {
            XMToast.showToast(getContext(), getString(R.string.network_anomaly_save_local), mContext.getDrawable(R.drawable.toast_error));
            popBack();
        } else {
            if (mTripAlbumVM != null) {
                mTripAlbumVM.upPhoto(markPhotoBean);
            }
        }
        popBack();
    }

    /**
     * 点击取消，删除本地保存
     */
    private void deletePhoto() {
        if (mPath != null) {
            HttpUtils.deleteFile(mPath);
        }
    }

    /**
     * 提示dialog
     */
    private void showMostPromptDialog() {
        View view = View.inflate(getActivity(), R.layout.dialog_record_prompti, null);
        Button confirm = view.findViewById(R.id.btn_confirm_sure);
        TextView messageText = view.findViewById(R.id.confirm_dialog_content);
        messageText.setText(Html.fromHtml(getString(R.string.most_prompt_text)));
        final XmDialog builder = new XmDialog.Builder(getActivity())
                .setView(view)
                .setWidth(711)
                .setHeight(489)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (mRunnable != null) {
                            ThreadDispatcher.getDispatcher().postOnMainDelayed(mRunnable, 1000);
                        }
                    }
                })
                .create();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.show();

    }

    @Override
    public boolean onBackPressed() {
        deletePhoto();
        if (mRunnable != null) {
            ThreadDispatcher.getDispatcher().removeOnMain(mRunnable);
        }
        return super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRunnable != null) {
            ThreadDispatcher.getDispatcher().removeOnMain(mRunnable);
        }
    }
}
