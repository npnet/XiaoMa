package com.xiaoma.launcher.main.manager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.mapbar.android.launchersupport.NaviSupportFragment;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.app.ui.AppFragment;
import com.xiaoma.launcher.player.ui.AudioMainFragment;
import com.xiaoma.launcher.service.ui.ServiceFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2019/6/26 0026
 */

public class MainFragmentManager {

    private static MainFragmentManager mainFragmentManager = new MainFragmentManager();
    private ServiceFragment serviceFragment;
    private AppFragment appFragment;
    private NaviSupportFragment mapFragment;
    private AudioMainFragment audioFragment;
    private List<Fragment> fragments = new ArrayList<>();

    public static MainFragmentManager getInstance() {
        return mainFragmentManager;
    }

    private MainFragmentManager() {
        mapFragment = NaviSupportFragment.newInstance();
        audioFragment = AudioMainFragment.newInstance();
        serviceFragment = ServiceFragment.newInstance();
        appFragment = AppFragment.newInstance();
        fragments.add(mapFragment);
    }

    public NaviSupportFragment getMapFragment() {
        return mapFragment;
    }

    public AudioMainFragment getAudioFragment() {
        return audioFragment;
    }

    public ServiceFragment getServiceFragment() {
        return serviceFragment;
    }

    public AppFragment getAppFragment() {
        return appFragment;
    }

    private boolean isContainFragment(Fragment fragment) {
        return fragments.contains(fragment);
    }

    private void addFragment(Fragment fragment) {
        if (!isContainFragment(fragment)) {
            fragments.add(fragment);
        }
    }

    public void clearFragments(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //fragments.remove(mapFragment);
        }
    }

    public void setCustomAnimations(FragmentTransaction fragmentTransaction, Fragment fragment) {
        if (fragmentTransaction == null || fragment == null) {
            return;
        }
        if (isContainFragment(fragment)) {
            fragmentTransaction.setCustomAnimations(R.animator.slide_scale_in_nomal, R.animator.slide_scale_out_normal);
        } else {
            fragmentTransaction.setCustomAnimations(R.animator.slide_scale_other_in_init, R.animator.slide_scale_other_out_init);
            addFragment(fragment);
        }
    }

}
