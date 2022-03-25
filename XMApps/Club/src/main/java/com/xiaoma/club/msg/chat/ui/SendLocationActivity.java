package com.xiaoma.club.msg.chat.ui;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Tip;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.util.ArrayUtil;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.msg.chat.vm.PoiSearchVM;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.permission.PermissionUtils;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import java.util.Arrays;

import static com.xiaoma.club.common.model.ClubEventConstants.NormalClick.sendLoaction;

/**
 * Created by LKF on 2018/10/11 0011.
 */
@PageDescComponent(ClubEventConstants.PageDescribe.sendLocationActivity)
public class SendLocationActivity extends BaseActivity {
    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_SEND_BTN_TEXT = "sendBtnText";

    public static final String RESULT_POI_ITEM = "poiItem";

    private static final String TAG = SendLocationActivity.class.getSimpleName();
    public static final int REQ_PERMISSION_CODE_LOCATION = 1;

    private SearchLocationFragment mSearchLocationFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String[] requirePermissions = PermissionUtils.getDeniedPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        });
        if (requirePermissions == null || requirePermissions.length <= 0) {
            init();
        } else {
            requestPermissions(requirePermissions, REQ_PERMISSION_CODE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.logI(TAG, String.format("onRequestPermissionsResult(requestCode: %s, permissions: %s, grantResults: %s)",
                requestCode, Arrays.toString(permissions), Arrays.toString(grantResults)));
        if (REQ_PERMISSION_CODE_LOCATION == requestCode) {
            if (!ArrayUtil.contains(grantResults, PackageManager.PERMISSION_DENIED)) {
                init();
            } else {
                XMToast.showToast(this, R.string.permission_req_location);
                finish();
            }
        }
    }

    public void init() {
        final Location location = getIntent().getParcelableExtra(EXTRA_LOCATION);
        if (location != null) {
            ViewModelProviders.of(this).get(PoiSearchVM.class)
                    .getLocationSearch().setValue(new PoiSearchVM.LocationSearch("", location));
        }
        setContentView(R.layout.act_chat_send_location);
        final PoiSearchFragment poiSearchFragment = (PoiSearchFragment) getSupportFragmentManager().findFragmentById(R.id.fmt_poi_search);
        poiSearchFragment.setCallback(new PoiSearchFragment.Callback() {
            @Override
            public void onPoiSelect(PoiItem poiItem) {
                setResult(RESULT_OK, new Intent().putExtra(RESULT_POI_ITEM, poiItem));
                XmAutoTracker.getInstance().onEvent(sendLoaction, poiItem.getSnippet(), TAG,
                        ClubEventConstants.PageDescribe.sendLocationActivity);
                finish();
            }

            @Override
            public void onSearchLocationClick() {
                final SearchLocationFragment fmt = new SearchLocationFragment();
                PoiSearchVM searchVM = ViewModelProviders.of(SendLocationActivity.this).get(PoiSearchVM.class);
                PoiSearchVM.LocationSearch search = searchVM.getLocationSearch().getValue();
                if (search != null && search.getLocation() != null && search.getLocation() instanceof AMapLocation) {
                    AMapLocation aMapLocation = (AMapLocation) search.getLocation();
                    Bundle args = new Bundle();
                    args.putParcelable(SearchLocationFragment.EXTRA_LATLONPOINT, aMapLocation);
                    fmt.setArguments(args);
                }
                fmt.setCallback(new SearchLocationFragment.Callback() {
                    @Override
                    public void onTipSelect(Tip tip) {
                        if (tip != null) {
                            final AMapLocation location = new AMapLocation("");
                            location.reset();
                            final LatLonPoint latLonPoint = tip.getPoint();
                            if (latLonPoint != null) {
                                location.setLatitude(latLonPoint.getLatitude());
                                location.setLongitude(latLonPoint.getLongitude());
                            }
                            location.setAddress(tip.getAddress());
                            location.setAdCode(tip.getAdcode());
                            location.setDistrict(tip.getDistrict());
                            location.setPoiName(tip.getName());
                            location.setLocationType(AMapLocation.LOCATION_TYPE_FIX_CACHE);
                            ViewModelProviders.of(SendLocationActivity.this).get(PoiSearchVM.class)
                                    .getLocationSearch().setValue(new PoiSearchVM.LocationSearch(tip.getName(), location));
                            LogUtil.logI(TAG, "onActivityResult() tip: { %s }, point: { %s }", tip, latLonPoint);
                        }
                        getSupportFragmentManager().beginTransaction()
                                .remove(fmt)
                                .commitNow();
                    }
                });
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fmt_container, fmt)
                        .addToBackStack(fmt.getClass().getName())
                        .commit();
                        /*startActivityForResult(new Intent(SendLocationActivity.this, SearchLocationActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), REQ_CODE_LOCATION_TIP);*/
                mSearchLocationFragment = fmt;
            }

            @Override
            public String getSendBtnText() {
                return getIntent().getStringExtra(EXTRA_SEND_BTN_TEXT);
            }
        });
    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQ_CODE_LOCATION_TIP == requestCode) {
            if (RESULT_OK == resultCode && data != null) {
                final Tip tip = data.getParcelableExtra(SearchLocationActivity.RESULT_LOCATION_TIP);
                if (tip != null) {
                    final AMapLocation location = new AMapLocation("");
                    location.reset();
                    final LatLonPoint latLonPoint = tip.getPoint();
                    if (latLonPoint != null) {
                        location.setLatitude(latLonPoint.getLatitude());
                        location.setLongitude(latLonPoint.getLongitude());
                    }
                    location.setAddress(tip.getAddress());
                    location.setAdCode(tip.getAdcode());
                    location.setDistrict(tip.getDistrict());
                    location.setPoiName(tip.getName());
                    location.setLocationType(AMapLocation.LOCATION_TYPE_FIX_CACHE);
                    ViewModelProviders.of(this).get(PoiSearchVM.class)
                            .getLocationSearch().setValue(new PoiSearchVM.LocationSearch(tip.getName(), location));
                    LogUtil.logI(TAG, "onActivityResult() tip: { %s }, point: { %s }", tip, latLonPoint);
                }
            }
        }
    }*/

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mSearchLocationFragment != null && !mSearchLocationFragment.isDestroy()) {
            mSearchLocationFragment.onWindowFocusChanged(hasFocus);
        }
    }
}
