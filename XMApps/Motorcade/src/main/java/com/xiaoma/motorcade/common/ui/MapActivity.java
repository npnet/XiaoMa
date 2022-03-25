package com.xiaoma.motorcade.common.ui;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.motorcade.R;
import com.xiaoma.motorcade.common.utils.LocationManager;
import com.xiaoma.motorcade.common.utils.UserUtil;
import com.xiaoma.motorcade.setting.ui.FriendDetailsFragment;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("Registered")
public class MapActivity extends BaseActivity implements View.OnClickListener {

    private static final int LAT_LON_BOUNDS = 100;
    private static final String TAG = MapActivity.class.getName();
    private static final String HXACCOUNT_KEY = "hx_account_key";
    private static final String USERID_KEY = "user_id_key";
    private static final String USER_NICK_KEY = "user_nick_key";
    private MapView mMapView;
    private AMap aMap;
    private AnimationDrawable animat;

    private RelativeLayout clickStartRl;
    private TextView startText;

    private LinearLayout noNetworkLl;

    private LinearLayout robbingLl;
    private ImageView wave;
    private TextView robbingText, lineNumber;
    private Button cancleBtn;

    private TextView onlineText;
    private Marker locationMarker;
    private LatLngBounds.Builder builder;
    private Map<String, Marker> allMarkers = new HashMap<>();
    private float zoom = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorcade_map);
        initView();
        initMap(savedInstanceState);
        LocationManager.getInstance().startLocation();
    }

    private void initMap(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setMapType(AMap.MAP_TYPE_NIGHT);
            aMap.getUiSettings().setScaleControlsEnabled(true);
            aMap.showBuildings(true);
            aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                @Override
                public void onMapLoaded() {

                }
            });
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.setOnMarkerClickListener(markerClickListener);
            aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {

                }

                @Override
                public void onCameraChangeFinish(CameraPosition cameraPosition) {
                    zoom = cameraPosition.zoom;
                }
            });
        }
        LocationManager.getInstance().initLocation(this, new LocationManager.OnLocationChangedListener() {

            @Override
            public void onLocationChanged(LatLng latLng) {
                KLog.d("MrMine", "onLocationChanged: " + latLng.latitude + "  " + latLng.longitude);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            }
        });
    }

    private void initView() {
        mMapView = findViewById(R.id.map);
        findViewById(R.id.btn_location).setOnClickListener(this);
        onlineText = findViewById(R.id.map_online_text);

        clickStartRl = findViewById(R.id.click_start_rl);
        startText = findViewById(R.id.satrt_rob_tv);

        noNetworkLl = findViewById(R.id.no_network_ll);

        robbingLl = findViewById(R.id.robbing_layout);
        wave = findViewById(R.id.speak_wave);
        robbingText = findViewById(R.id.rob_mic_text);
        lineNumber = findViewById(R.id.rob_line_number);
        cancleBtn = findViewById(R.id.cancel_rob_tv);

        clickStartRl.setOnClickListener(this);
        noNetworkLl.setOnClickListener(this);
        cancleBtn.setOnClickListener(this);
        findViewById(R.id.online_display_ll).setOnClickListener(this);
        showClickStart();
    }

    public void showClickStart() {
        clickStartRl.setVisibility(View.VISIBLE);
        noNetworkLl.setVisibility(View.GONE);
        robbingLl.setVisibility(View.GONE);
        startText.setText(getString(R.string.click_to_rob_mic));
    }

    public void showRobFailed() {
        clickStartRl.setVisibility(View.VISIBLE);
        noNetworkLl.setVisibility(View.GONE);
        robbingLl.setVisibility(View.GONE);
        startText.setText(getString(R.string.rob_mic_retry));
    }

    public void showNoNet() {
        clickStartRl.setVisibility(View.GONE);
        noNetworkLl.setVisibility(View.VISIBLE);
        robbingLl.setVisibility(View.GONE);
    }

    public void showRobbingMicLoading(String count) {
        clickStartRl.setVisibility(View.GONE);
        noNetworkLl.setVisibility(View.GONE);
        robbingLl.setVisibility(View.VISIBLE);
        wave.clearAnimation();
        if (animat == null) {
//            animat = (AnimationDrawable) getResources().getDrawable(R.drawable.mic_voice_anim);
            animat = (AnimationDrawable) XmSkinManager.getInstance().getDrawable(R.drawable.mic_voice_anim);

        }
        wave.setBackground(animat);
        if (animat.isRunning()) {
            animat.stop();
        }
        robbingText.setText(getString(R.string.start_speak));
        lineNumber.setText(getString(R.string.click_to_hang_out));
        cancleBtn.setText(getString(R.string.hang_out));
    }

    public void showRobbingMicSuccess() {
        clickStartRl.setVisibility(View.GONE);
        noNetworkLl.setVisibility(View.GONE);
        robbingLl.setVisibility(View.VISIBLE);
        wave.clearAnimation();
        if (animat == null) {
//            animat = (AnimationDrawable) getResources().getDrawable(R.drawable.mic_voice_anim);
            animat = (AnimationDrawable) XmSkinManager.getInstance().getDrawable(R.drawable.mic_voice_anim);
        }
        wave.setBackground(animat);
        animat.start();
        robbingText.setText(getString(R.string.start_speak));
        lineNumber.setText(getString(R.string.click_to_hang_out));
        cancleBtn.setText(getString(R.string.hang_out));
    }

    public void setCountDownText(final int time) {
        runOnUiThread(new Runnable() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void run() {
                if (lineNumber != null) {
                    String text = getResources().getString(R.string.count_down_second, time);
                    SpannableString spannableString = new SpannableString(text);
                    spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 4, text.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    lineNumber.setText(spannableString);
                }
            }
        });
    }

    public void setOnlineText(final String text) {
        if (onlineText != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onlineText.setText(getString(R.string.online_numbers) + text);
                }
            });
        }
    }

    protected Marker findMarker(String hxAccount) {
        if (!allMarkers.containsKey(hxAccount)) {
            return null;
        }
        return allMarkers.get(hxAccount);
    }

    protected synchronized void createMarker(final String nick, final String header, final long id, final String hxAccount, final LatLng latLng) {
        CustomTarget<Drawable> target = new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if (isDestroy())
                    return;
                createLocalMarker(nick, id, hxAccount, latLng, resource);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                if (isDestroy())
                    return;
                createLocalMarker(nick, id, hxAccount, latLng, getDrawable(R.drawable.ic_avater));
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        };
        Glide.with(this).load(header)
                .transform(new CircleCrop())
                .into(target);
    }

    private synchronized void createLocalMarker(String nick, long id, String hxAccount, LatLng latLng, Drawable drawable) {
        Bundle bundle = new Bundle();
        bundle.putString(USER_NICK_KEY, nick);
        bundle.putString(HXACCOUNT_KEY, hxAccount);
        bundle.putLong(USERID_KEY, id);
        if (allMarkers.containsKey(hxAccount)) {
            allMarkers.get(hxAccount).setPosition(latLng);
            allMarkers.get(hxAccount).setObject(bundle);
            return;
        }
        MarkerOptions userMarker = createUserMarker(latLng, drawable, hxAccount.equals(UserUtil.getCurrentUser().getHxAccountService()));
        Marker createdMarker = aMap.addMarker(userMarker);
        createdMarker.setObject(bundle);
        allMarkers.put(hxAccount, createdMarker);
    }

    private MarkerOptions createUserMarker(LatLng latLng, Drawable drawable, boolean isMine) {
        MarkerOptions markerOptions = new MarkerOptions();
        View view;
        if (isMine) {
            view = View.inflate(this, R.layout.view_location_car_mine, null);
        } else {
            view = View.inflate(this, R.layout.view_location_car_friend, null);
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_user_head);
        imageView.setImageDrawable(drawable);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
        markerOptions.position(latLng)
                .icon(bitmapDescriptor);
        return markerOptions;
    }

    public void removeMarker(final String hxId) {
        if (isFinishing() || allMarkers == null) {
            return;
        }
        if (allMarkers.containsKey(hxId)) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    KLog.d(TAG, "remove:" + hxId);
                    allMarkers.get(hxId).remove();
                    allMarkers.remove(hxId);
                }
            });
        }
    }

    AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
        // marker 对象被点击时回调的接口
        // 返回 true 则表示接口已响应事件，否则返回false
        @Override
        public boolean onMarkerClick(Marker marker) {
            Bundle bundle = (Bundle) marker.getObject();
            if (bundle == null) {
                return false;
            }
            String hxAccount = bundle.getString(HXACCOUNT_KEY);
            long id = bundle.getLong(USERID_KEY);
            String nick = bundle.getString(USER_NICK_KEY);
            if (!TextUtils.isEmpty(hxAccount) && !hxAccount.equals(UserUtil.getCurrentUser().getHxAccountService())) {
                showFragment(FriendDetailsFragment.newInstance(hxAccount, id, nick), true);
            }
            return false;
        }
    };

    private void showFragment(Fragment fragment, boolean slideIn) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (slideIn) {
            transaction.setCustomAnimations(
                    R.anim.slide_in_left, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_left);
        }
        transaction.add(R.id.view_content, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void doCameraUpdateToPosition(LatLng userLatLng) {
        builder.include(userLatLng);
        KLog.d("TEST_SHARE_LOCATION", userLatLng.latitude + " : " + userLatLng.longitude);
        doCameraUpdate();
    }

    private void doCameraUpdate() {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), LAT_LON_BOUNDS);
        aMap.animateCamera(cameraUpdate);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        mMapView.onDestroy();
        allMarkers.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
    }

    public void locationPosition() {
        if (!LocationManager.getInstance().locationSucess) {
            XMToast.toastException(this, R.string.locate_failed);
        }
        LocationManager.getInstance().startLocation();
    }
}
