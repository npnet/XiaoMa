package com.xiaoma.music.common.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.music.R;
import com.xiaoma.music.common.adapter.TitleTableAdapter;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.common.vm.LeftTableVM;
import com.xiaoma.music.search.ui.SearchActivity;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/9 0009
 */
@PageDescComponent(EventConstants.PageDescribe.leftTableFragment)
public class LeftTableFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView rvTitleTable;
    private LeftTableVM leftTableVM;
    private TitleTableAdapter adapter;
    private RelativeLayout tvSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_left_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initEvent();
        initVM();
    }

    private void initVM() {
        leftTableVM = ViewModelProviders.of(this).get(LeftTableVM.class);
        leftTableVM.getTableTitles().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                if (strings != null) {
                    adapter.setDatas(strings);
                }
            }
        });
        leftTableVM.getTableIndex().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null) {
                    adapter.setSelectIndex(integer);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        leftTableVM.initTableTitleList();
    }

    public void skipMine() {
        leftTableVM.getTableIndex().setValue(2);
    }


    public void skipOnline() {
        leftTableVM.getTableIndex().setValue(0);
    }

    public void skipLocal() {
        leftTableVM.getTableIndex().setValue(1);
    }
    private void initEvent() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        rvTitleTable.setLayoutManager(layoutManager);
        adapter = new TitleTableAdapter(mContext, new ArrayList<>(), R.layout.item_left_title);
        adapter.setOnItemClickListener(new XMBaseAbstractRyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View view, RecyclerView.ViewHolder holder, int position) {
                leftTableVM.getTableIndex().setValue(position);
                switch (position) {
                    case MusicConstants.TableType.ONLINE_MUSIC:
                        mCallback.onlineMusicClick();
                        break;
                    case MusicConstants.TableType.LOCAL_MUSIC:
                        mCallback.localMusicClick();
                        break;
                    case MusicConstants.TableType.MINE_MUSIC:
                        mCallback.mineClick();
                        break;
                }
            }
        });
        DividerItemDecoration decoration = new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 0);
            }
        };
        decoration.setDrawable(new ColorDrawable());
        rvTitleTable.addItemDecoration(decoration);
        rvTitleTable.setAdapter(adapter);
        tvSearch.setOnClickListener(this);
    }


    private void bindView(View view) {
        rvTitleTable = view.findViewById(R.id.rv_title_table);
        tvSearch = view.findViewById(R.id.rl_search);
    }

    @NormalOnClick(EventConstants.NormalClick.searchStart)
    @ResId(R.id.rl_search)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_search:
                if (MusicConstants.SHOW_ONLINE_TABLE) {
                    SearchActivity.startActivity(mContext);
                } else {
                    showToast(getString(R.string.function_not_complete));
                }
                break;
        }
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public Callback getCallback() {
        return mCallback;
    }

    public interface Callback {
        void onlineMusicClick();

        void localMusicClick();

        void mineClick();
    }
}
