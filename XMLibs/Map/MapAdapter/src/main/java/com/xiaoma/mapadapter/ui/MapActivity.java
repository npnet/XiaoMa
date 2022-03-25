package com.xiaoma.mapadapter.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.mapadapter.R;
import com.xiaoma.mapadapter.constant.EventConstants;
import com.xiaoma.mapadapter.factory.CameraUpdateFactory;
import com.xiaoma.mapadapter.listener.OnCameraChangeListener;
import com.xiaoma.mapadapter.listener.OnLocationChangeListener;
import com.xiaoma.mapadapter.listener.OnPoiSearchListener;
import com.xiaoma.mapadapter.manager.LocationClient;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.manager.PoiSearch;
import com.xiaoma.mapadapter.model.CameraUpdateInfo;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.model.LatLonPoint;
import com.xiaoma.mapadapter.model.LocationClientOption;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.mapadapter.model.LocationMode;
import com.xiaoma.mapadapter.model.MarkerOption;
import com.xiaoma.mapadapter.model.PoiInfo;
import com.xiaoma.mapadapter.model.PoiResult;
import com.xiaoma.mapadapter.model.QueryBound;
import com.xiaoma.mapadapter.model.QueryOption;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.mapadapter.view.Map;
import com.xiaoma.mapadapter.view.MapView;
import com.xiaoma.mapadapter.view.UiSetting;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.ui.constract.ScrollBarDirection;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.SearchVoiceView;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.constant.IatError;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.tts.EventTtsManager;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.vr.utils.VrUtils;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.xiaoma.ui.progress.loading.XMProgress.dismissProgressDialog;

/**
 * @author taojin
 * @date 2019/1/8
 */
@PageDescComponent(EventConstants.PageDescribe.MapActivityPagePathDesc)
public class MapActivity extends BaseActivity implements OnCameraChangeListener, OnPoiSearchListener, OnLocationChangeListener, View.OnClickListener {
    private static final String TAG = MapActivity.class.getSimpleName();
    public static final String EXTRA_LOCATION_DATA = "location";
    private static final int NOT_OPERATING_TIME = 6 * 1000;
    private static final int NO_OPERATING_TIME = 10 * 1000;
    private static final int FIRST_AMPLIFICATION_FACTOR = 18;
    private static final int AMAPLOCATION_ERROR_CODE = 0;
    private static final int RANGE_FOR_POI_SEARCH = 10000;
    private SearchVoiceView mSearchVoiceView;
    private RecyclerView mPoiRv;
    private MapView mapView;
    private Map aMap;
    private CheckBox mBtnScaleUp;
    private CheckBox mBtnScaleDown;
    private LocationInfo locationInfo;
    private LocationClient mlocationClient;
    public LocationClientOption mLocationOption = null;
    private double mLatitude;
    private double mLongitude;
    private ImageButton btnLocation;
    private String mKeyword;
    public CountDownTimer countDownTimer;
    private PoiSearch poiSearch;
    private Animation centerAnimation;
    private ImageView ivLocation;
    private QueryOption query;
    private LatLng mFinalChoosePosition;

    private MapAdapter mapAdapter;
    private List<SearchAddressInfo> mSearchAddressInfos;
    private StateView stateView;
    private String provinceCity;
    private String province;
    private String city = "";
    private boolean isHandDrag = true;
    private boolean isSearchByKeyWord = false;
    private int pageNum = 0;
    private int pageSize = 30;
    private XmScrollBar mScrollBar;
    private float zoomInLaunch = FIRST_AMPLIFICATION_FACTOR;
    private EditText inputEt;
    public CountDownTimer autoCountDownTimer;
    private boolean isClickCancel;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        bindView();
        initView();
        initMap(savedInstanceState);
        initLocation();
        initData();
    }

    private void initData() {
        if (getIntent().getExtras() == null) return;
        if (getIntent().getExtras().getInt(VrPracticeConstants.SKILL_REQUEST_CODE, -1) != -1) {
            registerExit();
        }
    }

    private void registerExit() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("close_app_VR_PRACTICE");
        registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                exit();
            }
        }, intentFilter);
    }
    private void unRegisterExit() {
        if (mBroadcastReceiver == null) return;
        unregisterReceiver(mBroadcastReceiver);
    }
    private void exit() {
        finish();
    }

    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        UiSetting uiSettings = aMap.getUiSettings();
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setZoomControlsEnabled(false);
        aMap.setOnCameraChangeListener(this);
    }

    private void initLocation() {
        //todo 等待后续车机有GPS模块，删除这段代码
        try {
            if (locationInfo == null) {
                locationInfo = LocationManager.getInstance().getCurrentLocation();
                LatLng latLng = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomInLaunch));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        startLocation();
    }

    private void initView() {
        getNaviBar().getMiddleView().setImageDrawable(getResources().getDrawable(R.drawable.icon_location));
        getNaviBar().getMiddleView().setVisibility(View.INVISIBLE);
        mSearchAddressInfos = new ArrayList<>();
        mapAdapter = new MapAdapter(R.layout.item_map, mSearchAddressInfos);

        mapAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //点击条目直接确认地址
                EventBus.getDefault().post(mSearchAddressInfos.get(position), EXTRA_LOCATION_DATA);
                cancelCountDown(countDownTimer);
                RemoteIatManager.getInstance().stopListening();
                RemoteIatManager.getInstance().release();
                EventTtsManager.getInstance().stopSpeaking();
                Intent intent = new Intent();
                intent.putExtra(EXTRA_LOCATION_DATA, mSearchAddressInfos.get(position));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mPoiRv.setLayoutManager(new LinearLayoutManager(this));
        //添加自定义分割线
        XmVerticalDividerDecoration decor = new XmVerticalDividerDecoration(this, DividerItemDecoration.VERTICAL);
        int horizontal = (int) getResources().getDimension(R.dimen.divider_height);
        decor.setRect(0, 0, 0, horizontal);
        mPoiRv.addItemDecoration(decor);
        mPoiRv.setAdapter(mapAdapter);
        mScrollBar.setRecyclerView(mPoiRv);
        if (!NetworkUtils.isConnected(this)) {
            stateView.showNoNetwork();
        }

    }

    private void bindView() {
        mSearchVoiceView = findViewById(R.id.search_voice_view);
        mPoiRv = findViewById(R.id.rv_poi);
        mapView = findViewById(R.id.map_view);
        mBtnScaleUp = findViewById(R.id.btn_scale_up);
        mBtnScaleDown = findViewById(R.id.btn_scale_down);
        mBtnScaleUp.setChecked(true);
        mBtnScaleDown.setChecked(false);
        btnLocation = findViewById(R.id.btn_location);
        btnLocation.setOnClickListener(this);
        ivLocation = findViewById(R.id.center_image);
        stateView = findViewById(R.id.state_view);

        mScrollBar = findViewById(R.id.scrollbar);
        mScrollBar.setDirection(ScrollBarDirection.DIRECTION_VERTICAL);

        stateView.setNoNetworkView(R.layout.map_no_net);

        centerAnimation = AnimationUtils.loadAnimation(this, R.anim.center_anim);

        mSearchVoiceView.setOnVoiceClickListener(this);
        mBtnScaleUp.setOnClickListener(this);
        mBtnScaleDown.setOnClickListener(this);
        mSearchVoiceView.setOnRecordingCancelClickListener(this);
        mSearchVoiceView.setNormal();
        mSearchVoiceView.setHint(getString(R.string.search_voice_str));
        initEt();
        addSearchNowButton();
    }


    private void startLocation() {
        mlocationClient = new LocationClient(this);
        mLocationOption = new LocationClientOption();
        mlocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(LocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setNeedAddress(true);
        mlocationClient.setLocOption(mLocationOption);
        mlocationClient.start();
    }

    private MarkerOption createLocationMap() {
        MarkerOption markerOptions = new MarkerOption();
        LatLng latLng = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());
        markerOptions.anchor(0.5f, 0.1f)
                .position(latLng)
                .title(locationInfo.getProvince() + locationInfo.getCity())
                .icon(BitmapFactory.decodeResource(getResources(), R.drawable.check));
        return markerOptions;
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        //当activity不在前台是停止定时
        cancelCountDown(countDownTimer);
        RemoteIatManager.getInstance().cancelListening();
        RemoteIatManager.getInstance().release();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        mSearchVoiceView.clearEditText();
        initVoiceEngine();

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            registerIatListener();
        } else {
            unregisterIatLiatener();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        mlocationClient.destroy();
        //销毁时停止定时
        cancelCountDown(countDownTimer);
        cancelCountDown(autoCountDownTimer);
        RemoteIatManager.getInstance().stopListening();
        RemoteIatManager.getInstance().release();
        EventTtsManager.getInstance().stopSpeaking();
        unRegisterExit();
        super.onDestroy();
    }


    /**
     * 当使用键盘就会执行此方法
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        cancelCountDown(countDownTimer);
        cancelCountDown(autoCountDownTimer);
        return super.dispatchKeyEvent(event);
    }

    /**
     * 当触摸就会执行此方法
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        cancelCountDown(countDownTimer);
        cancelCountDown(autoCountDownTimer);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        cancelCountDown(autoCountDownTimer);
        int i = v.getId();
        if (i == R.id.btn_location) {
            locationInfo = LocationManager.getInstance().getCurrentLocation();
            if (locationInfo != null) {
                LatLng latLng = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomInLaunch));
            }
        } else if (i == R.id.tv_start_voice_search) {

            if (!NetworkUtils.isConnected(MapActivity.this)) {
                XMToast.toastException(MapActivity.this, R.string.no_network);
                return;
            }

            mSearchVoiceView.setVoiceContent(getString(R.string.is_listener));
            startRecord();
        } else if (i == R.id.tv_recording_cancel) {
            mSearchVoiceView.setNormal();
            RemoteIatManager.getInstance().cancelListening();
            isClickCancel = true;

        } else if (i == R.id.search_now) {
            if (inputEt == null) return;
            mKeyword = inputEt.getText().toString();
            if (TextUtils.isEmpty(mKeyword)) {
                XMToast.showToast(getApplication(), R.string.search_content_empty_str);
                return;
            }
            isHandDrag = false;
            getSearchResult(mKeyword);
        } else if (i == R.id.btn_scale_down) {
            mapScale(false);
        } else if (i == R.id.btn_scale_up) {
            mapScale(true);
        }
    }

    private void mapScale(boolean isScaleUp) {
        mBtnScaleUp.setChecked(isScaleUp);
        mBtnScaleDown.setChecked(!isScaleUp);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        if (mapView == null) {
            Log.e(TAG, "mapScale -> AMap is null");
            return;
        }
        if (isScaleUp) {
            Log.i(TAG, "mapScale -> Scale up map");
            aMap.zoomIn();
        } else {
            Log.i(TAG, "mapScale -> Scale down map");
            aMap.zoomOut();
        }
    }

    private void getSearchResult(final String keyword) {
        showProgressDialog(getString(R.string.searching_str));
        mSearchVoiceView.setNormalContentText(keyword);
        doSearchQueryByKeyWord(keyword);
    }

    @Override
    public void onCameraChangeFinish(LatLng latLng) {
        ivLocation.startAnimation(centerAnimation);
        mFinalChoosePosition = latLng;
        if (isHandDrag) {
            showProgressDialog(getString(R.string.searching_str));
            doSearchQueryByPosition();
        }

        isHandDrag = true;
        isSearchByKeyWord = false;

    }

    @Override
    public void onCameraChangeZoomFinish(float zoom) {
        zoomInLaunch = zoom;
    }


    @Override
    public void onPoiSearched(PoiResult result, int resultCode) {
        dismissProgress();
        if (result != null && !ListUtils.isEmpty(result.getPoiInfoList())) {
            List<PoiInfo> poiInfoList = result.getPoiInfoList();
            filterPoiList(poiInfoList);

            if (isSearchByKeyWord) {
                LatLonPoint latLonPoint = poiInfoList.get(0).getLatLonPoint();
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLonPoint.getLatitude(),
                        latLonPoint.getLongitude()), zoomInLaunch));
            }

            addDataToTempData(poiInfoList);
        } else {
            stateView.showEmpty();
        }
        KLog.d("onPoiSearched", result.toString());


    }

    @Override
    public void onLocationChange(LocationInfo locationInfo) {

        if (locationInfo != null) {
            if (locationInfo.getErrorCode() == AMAPLOCATION_ERROR_CODE) {
                this.locationInfo = locationInfo;
                mLatitude = locationInfo.getLatitude();
                mLongitude = locationInfo.getLongitude();
                aMap.animateCamera(new CameraUpdateInfo(new LatLng(mLatitude, mLongitude), zoomInLaunch));
                aMap.addMarker(createLocationMap());
                mlocationClient.stop();
            } else {
                KLog.e("location Error, ErrorCode:" + locationInfo.getErrorCode() + ", ErrorInfo:" + locationInfo.getErrorInfo());
            }
        }

    }


    private void doSearchQueryByPosition() {
        if (NetworkUtils.isConnected(this)) {
            isSearchByKeyWord = false;
            query = new QueryOption("", getString(R.string.poi_type_str), "");
            query.setPageSize(pageSize);
            query.setPageNum(pageNum);
            poiSearch = new PoiSearch(this);
            poiSearch.setQueryOption(query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new QueryBound(convertToLatLonPoint(mFinalChoosePosition), RANGE_FOR_POI_SEARCH, true));
            poiSearch.doPoiSearch();
        } else {
            dismissProgressDialog(this);
            stateView.showNoNetwork();
        }


    }

    private void doSearchQueryByKeyWord(String key) {
        if (NetworkUtils.isConnected(this)) {
            isSearchByKeyWord = true;
            mSearchVoiceView.setNormal();
            query = new QueryOption(key, getString(R.string.poi_type_str), locationInfo.getCity());
            query.setPageSize(pageSize);
            query.setPageNum(pageNum);
            poiSearch = new PoiSearch(this);
            poiSearch.setQueryOption(query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.doPoiSearch();
        } else {
            dismissProgress();
            stateView.showNoNetwork();
        }

    }

    private LatLonPoint convertToLatLonPoint(LatLng mLatlon) {
        return new LatLonPoint(mLatlon.latitude, mLatlon.longitude);
    }

    private LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }


    private void addDataToTempData(List<PoiInfo> poiItems) {
        SearchAddressInfo addressInfo;
        mSearchAddressInfos.clear();
        for (int i = 0; i < poiItems.size(); i++) {
            PoiInfo poiItem = poiItems.get(i);
            province = poiItem.getProvinceName();
            city = poiItem.getCityName();
            provinceCity = province + city;
            String otherAddressName;
            if (TextUtils.isEmpty(poiItem.getAddress())) {
                otherAddressName = getSpliceAddress(province, city, poiItem.getAdName(), poiItem.getSnippet());
            } else {
                otherAddressName = poiItem.getAddress();
            }
            if (i == 0) {
                addressInfo = new SearchAddressInfo(city, poiItem.getTitle(), otherAddressName, provinceCity,
                        true, poiItem.getLatLonPoint());
            } else {
                addressInfo = new SearchAddressInfo(city, poiItem.getTitle(), otherAddressName, provinceCity,
                        false, poiItem.getLatLonPoint());
            }

            mSearchAddressInfos.add(addressInfo);
        }
        mapAdapter.notifyDataSetChanged();
        stateView.showContent();
    }

    private String getSpliceAddress(String addressHead, String addressNeck,
                                    String addressBody, String addressFoot) {
        if (addressFoot.equals(addressBody)) {
            addressFoot = "";
        }
        if (addressNeck.equals(addressBody)) {
            addressBody = "";
        }
        if (addressHead.equals(addressNeck)) {
            addressNeck = "";
        }
        return addressHead + addressNeck + addressBody + addressFoot;
    }

    private void filterPoiList(List<PoiInfo> poiInfos) {
        Iterator iterator = poiInfos.iterator();
        while (iterator.hasNext()) {
            PoiInfo poiInfo = (PoiInfo) iterator.next();
            if (poiInfo.getLatLonPoint() == null || poiInfo.getLatLonPoint().getLatitude() <= 0 || poiInfo.getLatLonPoint().getLongitude() <= 0) {
                iterator.remove();
            }
        }
    }

    /**
     * 添加立即搜索按钮
     */
    private void addSearchNowButton() {
        ViewGroup ll = mSearchVoiceView.findViewById(R.id.ll_normal);
        if (ll == null) return;
        Button button = new Button(this);
        button.setId(R.id.search_now);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(-15, 0, 0, 8);
        button.setLayoutParams(lp);
        button.setBackgroundResource(R.drawable.selector_search_now);
        button.setText(getString(R.string.btn_search));
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
        button.setPadding(0, 0, 0, 6);
        button.setGravity(Gravity.CENTER);
        ll.addView(button, mSearchVoiceView.getChildCount() + 1);
        button.setOnClickListener(this);
    }

    private void initEt() {
        inputEt = mSearchVoiceView.findViewById(R.id.et_normal_content);
        if (inputEt != null) {
            inputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String text = v.getText().toString().trim();
                        if (TextUtils.isEmpty(text)) {
                            XMToast.showToast(getApplication(), getString(R.string.search_content_empty_str));
                            return false;
                        }
                        mKeyword = text;
                        isHandDrag = false;
                        getSearchResult(mKeyword);
                    }
                    return false;
                }
            });
            inputEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    cancelCountDown(countDownTimer);
                    cancelCountDown(autoCountDownTimer);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        EventTtsManager.getInstance().init(this);
        if (autoCountDownTimer == null) {
            autoCountDownTimer = new CountDownTimer(NO_OPERATING_TIME, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    KLog.d("time----" + millisUntilFinished);
                }

                @Override
                public void onFinish() {

                    if (!NetworkUtils.isConnected(MapActivity.this)) {
                        XMToast.toastException(MapActivity.this, R.string.no_network);
                        return;
                    }

                    mSearchVoiceView.setVoiceContent(getString(R.string.need_me_to_do));
                    EventTtsManager.getInstance().startSpeaking(getString(R.string.need_me_to_do), new OnTtsListener() {
                        @Override
                        public void onCompleted() {
                            mSearchVoiceView.setVoiceContent(getString(R.string.is_listener));
                            startRecord();
                        }

                        @Override
                        public void onBegin() {

                        }

                        @Override
                        public void onError(int code) {

                        }
                    });
                }
            };
            autoCountDownTimer.start();
        } else {
            autoCountDownTimer.start();
        }
    }

    private void cancelCountDown(CountDownTimer countDownTimer) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void startRecord() {
        RemoteIatManager.getInstance().startListeningRecord();
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(NOT_OPERATING_TIME, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    KLog.d("time----" + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    //mSearchVoiceView.setVoiceContent(getString(R.string.search_need_help));
                    RemoteIatManager.getInstance().cancelListening();
                    isClickCancel = false;
                    //                    mSearchVoiceView.setNormal();
                }
            };
            countDownTimer.start();
        } else {
            countDownTimer.start();
        }
    }

    private void initVoiceEngine() {
        RemoteIatManager.getInstance().init(this);
    }

    private void registerIatListener() {
        RemoteIatManager.getInstance().setOnIatListener(new OnIatListener() {
            @Override
            public void onComplete(String voiceText, String parseText) {
                KLog.d("voice onComplete " + voiceText);
                if (!TextUtils.isEmpty(voiceText)) {
                    String word = VrUtils.replaceFilter(voiceText);
                    if (!isClickCancel) {
                        isHandDrag = false;
                        mKeyword = word;
                        mSearchVoiceView.setNormalContentText(word);
                        getSearchResult(mKeyword);
                        //                        XmAutoTracker.getInstance().onEvent(EventConstants.ACTIVITY_RECORD_COMPLETED_EVENT, mKeyword,
                        //                                TAG, EventConstants.PageDescribe.ACTIVITY_SEARCH);
                    } else {
                        mSearchVoiceView.setNormal();
                    }
                } else {
                    handleVoiceEmpty();
                }
            }

            @Override
            public void onVolumeChanged(int volume) {
                KLog.d("voice change " + volume);
            }

            @Override
            public void onNoSpeaking() {
                KLog.d("voice onNoSpeaking");
            }

            @Override
            public void onError(int errorCode) {
                KLog.d("voice onError " + errorCode);
                if (errorCode == IatError.ERROR_MIC_FOCUS_LOSS) {
                    XMToast.toastException(MapActivity.this, R.string.get_mic_failed);
                }
                handleVoiceEmpty();
            }

            @Override
            public void onResult(String recognizerText, boolean isLast, String currentText) {
                KLog.d("voice onResult  " + recognizerText + " --- " + isLast + "  " + currentText);
            }

            @Override
            public void onWavFileComplete() {
                KLog.d("voice onWavFileComplete");
            }

            @Override
            public void onRecordComplete() {
                KLog.d("voice onRecordComplete");
            }
        });
    }

    private void unregisterIatLiatener() {
        RemoteIatManager.getInstance().setOnIatListener(null);
    }

    private void handleVoiceEmpty() {
        if (isClickCancel) {
            return;
        }
        mSearchVoiceView.setVoiceContent(getString(R.string.search_voice_tip));
        EventTtsManager.getInstance().startSpeaking(getString(R.string.search_voice_tip), new OnTtsListener() {
            @Override
            public void onCompleted() {
                mSearchVoiceView.setNormal();
            }

            @Override
            public void onBegin() {

            }

            @Override
            public void onError(int code) {

            }
        });
    }

}
