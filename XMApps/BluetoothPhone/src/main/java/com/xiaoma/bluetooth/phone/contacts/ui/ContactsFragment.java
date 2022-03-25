package com.xiaoma.bluetooth.phone.contacts.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.collection.vm.CollectionVM;
import com.xiaoma.bluetooth.phone.common.constants.EventBusTags;
import com.xiaoma.bluetooth.phone.common.constants.EventConstants;
import com.xiaoma.bluetooth.phone.common.manager.BluetoothPhoneDbManager;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.utils.OperateUtils;
import com.xiaoma.bluetooth.phone.common.views.ErrorDialog;
import com.xiaoma.bluetooth.phone.common.views.SideBar;
import com.xiaoma.bluetooth.phone.common.views.VerticalScrollBar;
import com.xiaoma.bluetooth.phone.contacts.adapter.ContactAdapter;
import com.xiaoma.bluetooth.phone.contacts.model.ContactSection;
import com.xiaoma.bluetooth.phone.main.ui.BaseBluetoothFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import static com.xiaoma.bluetooth.phone.common.constants.EventConstants.NormalClick.cancelCollect;
import static com.xiaoma.bluetooth.phone.common.constants.EventConstants.NormalClick.collect;
import static com.xiaoma.bluetooth.phone.common.constants.EventConstants.PageDescribe.contactFragmentPagePathDesc;

/**
 * 联系人
 *
 * @author: iSun
 * @date: 2018/11/16 0016
 */
@PageDescComponent(contactFragmentPagePathDesc)
public class ContactsFragment extends BaseBluetoothFragment implements PhoneStateManager.OnMacAddressChangedListener {

    public static char[] letters = {'#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z'};
    private List<ContactSection> items = new ArrayList<>();
    private ContactAdapter contactAdapter;
    private SideBar mSideBar;
    private boolean isRightPage;
    private List<ContactBean> collections;
    private boolean needCheckCollections = true;
    private CollectionVM collectionVM;
    private long lastTime = 0;
    private View noContactView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        collectionVM = ViewModelProviders.of(activity).get(CollectionVM.class);
        collections = collectionVM.getCollections().getValue();
        collectionVM.getCollections().observe(this, new Observer<List<ContactBean>>() {
            @Override
            public void onChanged(@Nullable List<ContactBean> list) {
                collections = new ArrayList<>(list);
//                if (!needCheckCollections) {
//                    needCheckCollections = true;
//                } else {
                checkIsCollected(items, list);
//                }
            }
        });
        PhoneStateManager.getInstance(mContext).addOnMacAddressChangedListener(this);
    }

    private void updateUI() {
        mSideBar.setVisibility(items.size() == 0 ? View.GONE : View.VISIBLE);
    }

    public void bindView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rv);
        VerticalScrollBar mScrollBar = view.findViewById(R.id.scroll_bar);
        noContactView = view.findViewById(R.id.no_contact_layout);
        mScrollBar.setRecyclerView(recyclerView);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);

        contactAdapter = new ContactAdapter(items);
        contactAdapter.setOnItemChildClickListener(new ContactAdapter.OnItemChildClickListener() {
            @Override
            public void OnItemClickListener(int position) {
                ContactBean bean = items.get(position).t;
                if (collections != null) {
                    if (collections.contains(bean)) {
                        //取消收藏
                        bean.setCollected(false);
                        contactAdapter.notifyDataSetChanged();
                        collections.remove(bean);
                        collectionVM.getCollections().setValue(collections);
                        needCheckCollections = false;
                        BluetoothPhoneDbManager.getInstance().deleteCollection(bean);
                        XmAutoTracker.getInstance().onEvent(cancelCollect, GsonHelper.toJson(bean), TAG, contactFragmentPagePathDesc);
                        return;
                    }
                    if (collections.size() >= 6) {
                        showDialog(R.string.excess_collection_quantity);
                        return;
                    }
                }
                bean.setCollected(true);
                contactAdapter.notifyDataSetChanged();
                collections.add(bean);
                collectionVM.getCollections().setValue(collections);
                needCheckCollections = false;
                BluetoothPhoneDbManager.getInstance().addCollection(bean);
                XmAutoTracker.getInstance().onEvent(collect, GsonHelper.toJson(bean), TAG, contactFragmentPagePathDesc);
            }

            @Override
            public void onItemChildClick(int position) {
                ContactSection section = items.get(position);
                /*if (section.isHeader) {
                    return;
                }*/
                ContactBean contactBean = section.t;
                OperateUtils.dial(contactBean.getPhoneNum());
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.call, GsonHelper.toJson(contactBean),
                        TAG, contactFragmentPagePathDesc);
            }
        });
       /* contactAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ContactSection section = items.get(position);
                if (section.isHeader) {
                    return;
                }
                ContactBean contactBean = section.t;
                OperateUtils.dial(contactBean.getPhoneNum());
            }
        });*/

        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.no_contact_layout, null);
//        contactAdapter.setEmptyView(emptyView);
        contactAdapter.isShowCollection(!isRightPage);
        recyclerView.setAdapter(contactAdapter);

        final TextView mTvLetter = view.findViewById(R.id.contact_dialog);
        mSideBar = view.findViewById(R.id.sidebar);
        mSideBar.setTextView(mTvLetter);

        mTvLetter.measure(0, 0);
        final int mTvLetterWidth = mTvLetter.getMeasuredWidth();
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s, float y) {
                int position = contactAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    linearLayoutManager.scrollToPositionWithOffset(position, 0);
                }

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTvLetter.getLayoutParams();
                int left = mSideBar.getLeft() - mTvLetterWidth - getResources().getDimensionPixelSize(R.dimen.width_letter_indicator_margin_right);
                int top = (int) (y - getResources().getDimensionPixelSize(R.dimen.height_letter_indicator_background_center));
                layoutParams.setMargins(left, top, 0, 0);
                mTvLetter.setLayoutParams(layoutParams);
            }
        });
    }

    private int[] getSpecialColorIndex() {
        int[] indexs = new int[letters.length];
        for (int i = 0; i < letters.length; i++) {
            int position = contactAdapter.getPositionForSection(letters[i]);
            indexs[i] = position == -1 ? 0 : 1;
        }
        return indexs;
    }

    private void showDialog(int msg) {
        ErrorDialog errorDialog = new ErrorDialog(getContext());
        errorDialog.show();
        errorDialog.setErrorImg(R.drawable.icon_collection_dialog);
        errorDialog.setErrorMsg(msg);
    }

    @Subscriber(tag = EventBusTags.PULL_STATE)
    private void pullState(boolean pullState) {
        items.clear();
        contactAdapter.notifyDataSetChanged();
        noContactView.setVisibility(pullState ? View.GONE : View.VISIBLE);
        updateUI();
    }


    @Subscriber(tag = EventBusTags.CONTACT_LIST_REFRESH)
    private void onContactListRefresh(List<ContactBean> contactItems) {
//        if (contactItems.size() == 0) return;
        // 目前EventBus存在重复回调bug,防止重复回调
        if (lastTime != 0 && System.currentTimeMillis() - lastTime < 5) {
            return;
        }
//        if (!contactItems.isEmpty()) {
//            noContactView.setVisibility(View.GONE);
//        }
        Log.d("phonebook", "onContactListRefresh,size: " + contactItems.size());
        lastTime = System.currentTimeMillis();
        if (contactItems.isEmpty()) {
            items.clear();
            contactAdapter.notifyDataSetChanged();
            noContactView.setVisibility(View.VISIBLE);
            updateUI();
            return;
        } else {
            noContactView.setVisibility(View.GONE);
        }
        dataCleanup(contactItems);
        /*items.clear();
        List<String> headList = new ArrayList<>();
        for (int i = 0; i < contactItems.size(); i++) {
            ContactBean bean = contactItems.get(i);
            String letter = bean.getFirstPinYin();
            if (!headList.contains(letter)) {
                items.add(new ContactSection(true, letter));
                headList.add(letter);
                if (i - 1 >= 0) {
                    contactItems.get(i - 1).setLastOne(true);
                }
            }
            items.add(new ContactSection(bean));
        }
        checkIsCollected(items, collections);
        contactAdapter.notifyDataSetChanged();
        updateUI();
        mSideBar.changeTextColorByIndexs(getContext().getResources().getColor(R.color.gray_side_bar_text),getSpecialColorIndex());*/
    }

    private void dataCleanup(final List<ContactBean> contactItems) {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                items.clear();
                List<String> headList = new ArrayList<>();
                for (int i = 0; i < contactItems.size(); i++) {
                    ContactBean bean = contactItems.get(i);
                    String letter = bean.getFirstPinYin();
                    if (collections != null) {
                        bean.setCollected(collections.contains(bean));
                    }
                    if (!headList.contains(letter)) {
                       /* items.add(new ContactSection(true, letter));

                        if (i - 1 >= 0) {
                            contactItems.get(i - 1).setLastOne(true);
                        }*/
                        headList.add(letter);
                        ContactSection contactSection = new ContactSection(true, letter);
                        contactSection.t = bean;
                        items.add(contactSection);
                    } else {
                        items.add(new ContactSection(bean));
                    }

                }
                ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        contactAdapter.notifyDataSetChanged();
                        updateUI();
                        mSideBar.changeTextColorByIndexs(getContext().getResources().getColor(R.color.gray_side_bar_text), getSpecialColorIndex());
                    }
                });

            }
        });
    }

    public void checkIsCollected(List<ContactSection> list, List<ContactBean> collectionList) {
        if (collectionList == null || list == null) return;
        for (ContactSection section : list) {
            if (section == null) continue;
            ContactBean bean = section.t;
            if (bean == null) continue;
            if (collectionList.contains(bean)) {
                bean.setCollected(true);
            } else {
                bean.setCollected(false);
            }
        }
        contactAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PhoneStateManager.getInstance(mContext).removeOnMacAddressChangedListener(this);
    }

    public void setRightPage(boolean isRightPage) {
        this.isRightPage = isRightPage;
    }

    @Override
    public void onMacAddressChanged(String macAddress) {
        Log.d("QBX", "onMacAddressChanged: ");
        collectionVM.getCollectionsFromDb();
    }

    @Override
    public boolean needPhoneStateCallback() {
        return false;
    }

    @Override
    public void onHfpConnected(BluetoothDevice device) {
        super.onHfpConnected(device);
        if (isDeviceConnected(device)) {
//            noContactView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onA2dpConnected(BluetoothDevice device) {
        super.onA2dpConnected(device);
        if (isDeviceConnected(device)) {
//            noContactView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onHfpDisConnected(BluetoothDevice device) {
        super.onHfpDisConnected(device);
        cleanData();
    }

    @Override
    public void onA2dpDisconnected(BluetoothDevice device) {
        super.onA2dpDisconnected(device);
        cleanData();
    }

    private void cleanData() {
        items.clear();
        contactAdapter.notifyDataSetChanged();
    }
}
