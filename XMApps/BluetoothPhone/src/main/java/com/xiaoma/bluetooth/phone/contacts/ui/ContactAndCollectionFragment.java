package com.xiaoma.bluetooth.phone.contacts.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.collection.ui.CollectionFragment;
import com.xiaoma.bluetooth.phone.common.constants.EventBusTags;
import com.xiaoma.bluetooth.phone.common.constants.EventConstants;
import com.xiaoma.bluetooth.phone.common.constants.ViewState;
import com.xiaoma.bluetooth.phone.common.utils.BluetoothUtils;
import com.xiaoma.bluetooth.phone.main.ui.BaseBluetoothFragment;
import com.xiaoma.bluetooth.phone.main.ui.MainActivity;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.tts.EventTtsManager;

import org.simple.eventbus.Subscriber;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by qiuboxiang on 2018/12/4 19:59
 */
public class ContactAndCollectionFragment extends BaseBluetoothFragment implements View.OnClickListener {
    public static final String TAG = "ContactAndCollectionFragment";
    private List<Fragment> fragments = new ArrayList<>();
    private int prePosition = 0;
    private int defPosition = 0;
    private FragmentManager fManager;
    private LinearLayout mDisconnectBluetoothLayout;
    private LinearLayout mContentLayout;
    private TabLayout tab;
    private MainActivity activity;
    private boolean isRightPage;
    private ContactsFragment contactsFragment;
    private CollectionFragment collectionFragment;
    //    private View noAccessToContactView;
    private int[] checkContactWord = new int[]{R.string.opend, R.string.ok};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_and_collection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();
        bindView(view);
    }

    private void bindView(View view) {
        mDisconnectBluetoothLayout = view.findViewById(R.id.layout_disconnect_bluetooth);
//        noAccessToContactView = view.findViewById(R.id.no_access_to_contact_layout);
        mContentLayout = view.findViewById(R.id.layout_content);
        view.findViewById(R.id.to_connect_bluetooth).setOnClickListener(this);
        fManager = getChildFragmentManager();
        if (contactsFragment == null) {
            contactsFragment = new ContactsFragment();
        }
        fragments.add(contactsFragment);
        fragments.add(collectionFragment = new CollectionFragment());
        initTabLayout(view);
        displayDisconnectLayout(!BluetoothUtils.isBTConnectDevice());
        /*--------测试代码----------*/
//        displayDisconnectLayout(false);
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.connectBluetooth})
    @ResId({R.id.to_connect_bluetooth})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.to_connect_bluetooth:
                connectBluetooth();
                break;
        }
    }

    private void initTabLayout(View view) {
        tab = view.findViewById(R.id.tabLayout);
        String[] tabName = getResources().getStringArray(R.array.contact_and_colelction_menu);
        tab.addTab(tab.newTab().setCustomView(new BluetoothTabView(getContext(), tabName[0])));
        tab.addTab(tab.newTab().setCustomView(new BluetoothTabView(getContext(), tabName[1])));
        tab.addOnTabSelectedListener(new XmTrackerOnTabSelectedListener() {

            private CharSequence mTabText;

            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(mTabText.toString(), "0");
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setSelected(tab, true);
                mTabText = tab.getText();
                switchMenu(prePosition, tab.getPosition());
                prePosition = tab.getPosition();
                if (isRightPage) {
                    if (prePosition == 0) {
                        activity.setSecondViewState(ViewState.Contacts, false);
                    } else {
                        activity.setSecondViewState(ViewState.Collection, false);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                setSelected(tab, false);
            }
        });
//        tab.getTabAt(defPosition).select();
        BluetoothTabView itemTab = (BluetoothTabView) tab.getTabAt(defPosition).getCustomView();
        itemTab.setSelect(true);
        switchMenu(prePosition, defPosition);
        int marginLeft = getResources().getDimensionPixelSize(R.dimen.width_tab_margin_left);
        int marginRight = getResources().getDimensionPixelSize(R.dimen.width_tab_margin_right);
        setUpIndicatorWidth(tab, marginLeft, marginRight);
    }

    private void setSelected(TabLayout.Tab tab, boolean isSelected) {
        BluetoothTabView tabView = (BluetoothTabView) tab.getCustomView();
        tabView.setSelect(isSelected);
    }

    private void displayDisconnectLayout(boolean show) {
        mDisconnectBluetoothLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mContentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /*private void displayNoAccessToContactLayout(boolean show) {
        noAccessToContactView.setVisibility(show ? View.VISIBLE : View.GONE);
        mContentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }*/


    @Override
    public void onHfpConnected(BluetoothDevice device) {
        super.onHfpConnected(device);
        if (isDeviceConnected(device)) {
            displayDisconnectLayout(false);
        }
    }

    @Override
    public void onHfpDisConnected(BluetoothDevice device) {
        super.onHfpDisConnected(device);
//        if (isDeviceDisconnected(device)) {
            displayDisconnectLayout(true);
//        }
    }

    @Override
    public void onA2dpConnected(BluetoothDevice device) {
        super.onA2dpConnected(device);
       /* if (isDeviceConnected(device)) {
            displayDisconnectLayout(false);
        }*/
    }

    @Override
    public void onA2dpDisconnected(BluetoothDevice device) {
        super.onA2dpDisconnected(device);
       /* if (isDeviceDisconnected(device)) {
            displayDisconnectLayout(true);
        }*/
    }

    public void switchMenu(int from, int to) {
        int viewSize = fragments.size();
        if (to < 0 || from < 0 || to >= viewSize || from > viewSize || fManager == null) {
            KLog.e("switchMenu error:" + to + " fManager is null:" + (fManager == null));
            return;
        }
        Fragment fromFragment = fragments.get(from);
        Fragment toFragment = fragments.get(to);
        switchFragment(fManager, R.id.fragment_container, fromFragment, toFragment);
    }

    public void switchFragment(FragmentManager fManager, int containerViewId, Fragment from, Fragment to) {
        FragmentTransaction transaction = fManager.beginTransaction();
        if (!to.isAdded()) {
            transaction.hide(from);
            transaction.add(containerViewId, to, to.getTag());
            transaction.show(to).commit();
        } else {
            transaction.hide(from).show(to).commit();
        }
    }

    private void setUpIndicatorWidth(TabLayout tabLayout, int marginLeft, int marginRight) {
        Class<?> tabLayoutClass = tabLayout.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayoutClass.getDeclaredField("mTabStrip");
            tabStrip.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        LinearLayout layout = null;
        try {
            if (tabStrip != null) {
                layout = (LinearLayout) tabStrip.get(tabLayout);
                for (int i = 0; i < layout.getChildCount(); i++) {
                    View child = layout.getChildAt(i);
                    child.setPadding(0, 0, 0, 0);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        params.setMarginStart(marginLeft);
                        params.setMarginEnd(marginRight);
                    }
                    child.setLayoutParams(params);
                    child.invalidate();
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setRightPage(boolean rightPage) {
        isRightPage = rightPage;
        if (contactsFragment == null) {
            contactsFragment = new ContactsFragment();
        }
        contactsFragment.setRightPage(rightPage);
    }

    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.BluetoothPhone.CONTACT:
                EventTtsManager.getInstance().startSpeaking(mContext.getString(getCheckContactWord()) + "," + mContext.getString(R.string.example_word));
                switchTab(0);
                return true;
            case NodeConst.BluetoothPhone.COLLECTION:
                if (prePosition != 1) {
                    collectionFragment.setNeedTtsCollectionCount(true);
                    switchTab(1);
                } else {
                    collectionFragment.ttsCollectionCount();
                }
                return true;
            default:
                return false;
        }
    }

    void switchTab(int index) {
        if (prePosition != index) {
            tab.getTabAt(index).select();
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.BluetoothPhone.CONTACT_AND_COLLECTION;
    }

    private int getCheckContactWord() {
        int index = new Random().nextInt(checkContactWord.length);
        return checkContactWord[index];
    }

    @Override
    public boolean needPhoneStateCallback() {
        return false;
    }

}
