package com.xiaoma.bluetooth.phone.collection.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ThreadedRenderer;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.collection.adapter.CollectionAdapter;
import com.xiaoma.bluetooth.phone.collection.vm.CollectionVM;
import com.xiaoma.bluetooth.phone.common.constants.EventBusTags;
import com.xiaoma.bluetooth.phone.common.constants.EventConstants;
import com.xiaoma.bluetooth.phone.common.manager.BluetoothPhoneDbManager;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.utils.BluetoothUtils;
import com.xiaoma.bluetooth.phone.common.utils.OperateUtils;
import com.xiaoma.bluetooth.phone.common.views.VerticalScrollBar;
import com.xiaoma.bluetooth.phone.main.ui.BaseBluetoothFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.vr.tts.EventTtsManager;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by qiuboxiang on 2018/12/4 19:59
 */
@PageDescComponent(EventConstants.PageDescribe.collectionFragmentPagePathDesc)
public class CollectionFragment extends BaseBluetoothFragment {

    private RecyclerView recyclerView;
    private CollectionAdapter collectionAdapter;
    private CollectionVM collectionVM;
    private List<ContactBean> collections;
    private VerticalScrollBar mScrollBar;
    private View noContactView;
    private View mContentLayout;
    private boolean needTtsCollectionCount;
    private int[] finishWord = new int[]{R.string.ok, R.string.opend};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        collectionVM = ViewModelProviders.of(activity).get(CollectionVM.class);
        collections = collectionVM.getCollections().getValue();
        bindView(view);
        collectionVM.getCollections().observe(this, new Observer<List<ContactBean>>() {
            @Override
            public void onChanged(@Nullable List<ContactBean> list) {
                if (list == null || list.size() == 0) {
                    if (BluetoothUtils.isBTConnectDevice()) {
                        displayNoContactLayout(true);
                    }
                } else {
                    displayNoContactLayout(false);
                }
                Iterator<ContactBean> iterator = list.iterator();
                while (iterator.hasNext()) {
                    ContactBean current = iterator.next();
                    if(!isExists(current)){
                        BluetoothPhoneDbManager.getInstance().deleteCollection(current);
                        iterator.remove();
                    }
                }
                collections = new ArrayList<>(list);
                collectionAdapter.setNewData(list);
                collectionAdapter.notifyDataSetChanged();
            }
        });
    }

    private boolean isExists(ContactBean contactBean) {
        List<ContactBean> contactBeans = PhoneStateManager.getInstance(getContext()).getAllContact();
        Iterator<ContactBean> iterator = contactBeans.iterator();
        while (iterator.hasNext()) {
            ContactBean current = iterator.next();
            if (contactBean.getId().equals(current.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            if (needTtsCollectionCount) {
                ttsCollectionCount();
            }
        } else {
            if (collectionAdapter != null) {
                collectionAdapter.closeMenu();
            }
        }
        super.onHiddenChanged(hidden);
    }

    public void bindView(View view) {
        recyclerView = view.findViewById(R.id.rv);
        mScrollBar = view.findViewById(R.id.scroll_bar);
        mScrollBar.setRecyclerView(recyclerView);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        collectionAdapter = new CollectionAdapter(R.layout.item_collection, collections);
        collectionAdapter.setListener(new CollectionAdapter.Listener() {
            @Override
            public void onClick(ContactBean bean) {
                OperateUtils.dial(bean.getPhoneNum());
            }

            @Override
            public void onDelete(ContactBean bean) {
                //取消收藏
                BluetoothPhoneDbManager.getInstance().deleteCollection(bean);
                collections.remove(bean);
                collectionVM.getCollections().setValue(collections);
            }
        });
        recyclerView.setAdapter(collectionAdapter);
        mContentLayout = view.findViewById(R.id.layout_content);
        mContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionAdapter.closeMenu();
            }
        });

        noContactView = view.findViewById(R.id.no_contact_layout);
    }

    private void displayNoContactLayout(boolean show) {
        noContactView.setVisibility(show ? View.VISIBLE : View.GONE);
        mContentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Subscriber(tag = EventBusTags.CONTACT_LIST_REFRESH)
    private void onContactListRefresh(List<ContactBean> contactList) {
        if (contactList == null || contactList.size() == 0) {
            if (BluetoothUtils.isBTConnectDevice()) {
                displayNoContactLayout(true);
            }
            return;
        }
        if (collections.size() == 0) return;
        displayNoContactLayout(false);
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                collectionVM.getCollections().setValue(collections);
            }
        });
//        boolean changed = false;
//        boolean deleted = false;
//        List<Boolean> matchList = new ArrayList<>();
//        for (int i = 0; i < collections.size(); i++) {
//            ContactBean collectionBean = collections.get(i);
//            matchList.add(false);
//            for (ContactBean contactBean : contactList) {
//                if (contactBean.getPhoneNum().equals(collectionBean.getPhoneNum())) {
//                    matchList.set(i, true);
//                    if (!contactBean.getName().equals(collectionBean.getName())) {
//                        changed = true;
//                        collectionBean.setName(contactBean.getName());
//                    }
//                    break;
//                }
//            }
//        }
//        for (int i = 0; i < matchList.size(); i++) {
//            if (!matchList.get(i)) {
//                collections.remove(i);
//                matchList.remove(i--);
//                deleted = true;
//            }
//        }
//        if (deleted || changed) {
//            BluetoothPhoneDbManager.getInstance().setCollectionList((ArrayList<ContactBean>) collections);
//            collectionVM.getCollections().setValue(this.collections);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private int getFinishWord() {
        int index = new Random().nextInt(finishWord.length);
        return finishWord[index];
    }

    public void setNeedTtsCollectionCount(boolean needTtsCollectionCount) {
        this.needTtsCollectionCount = needTtsCollectionCount;
    }

    public void ttsCollectionCount() {
        EventTtsManager.getInstance().startSpeaking(getString(getFinishWord()) + "," + StringUtil.format(getString(R.string.collection_count), collections.size()));
    }

    @Override
    public boolean needPhoneStateCallback() {
        return false;
    }

    @Subscriber(tag = EventBusTags.PULL_STATE)
    private void pullState(boolean pullState) {
        collectionAdapter.notifyDataSetChanged();
        displayNoContactLayout(!pullState);
    }
}
