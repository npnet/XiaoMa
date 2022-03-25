package com.xiaoma.music.practice;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.ui
 *  @file_name:      PlayMusicActivity
 *  @author:         Rookie
 *  @create_time:    2019/6/4 16:43
 *  @description：   TODO             */

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.pratice.PlayMusicBean;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.music.R;
import com.xiaoma.ui.dialog.VpRecordDialog;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.ViewUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PlayMusicActivity extends BaseActivity implements View.OnClickListener {

    private EditText etMusic;
    private Button ivVoice;
    private Button btnSearch, btnDelete;
    private TextView tvSearchResultTitle;
    private RecyclerView rvSearchResult;
    private XmScrollBar scrollBar;
    private PlayMusicAdapter playMusicAdapter;
    private PlayMusicVM mPlayMusicVM;
    private int mActionPosition;
    private int mRequestCode;
    private MusicHistoryAdapter mMusicHistoryAdapter;

    private int selectIndex = -1;

    private WeakReference<VpRecordDialog> mVoiceRecordDlgRef;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        initView();
        initData();
        registerExit();
    }

    @Override
    protected void onDestroy() {
        unRegisterExit();
        super.onDestroy();
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

    private void initView() {
        etMusic = findViewById(R.id.et_music);
        ivVoice = findViewById(R.id.bt_voice);
        btnSearch = findViewById(R.id.btn_search);
        btnDelete = findViewById(R.id.btn_delete);
        tvSearchResultTitle = findViewById(R.id.tv_search_result_title);
        rvSearchResult = findViewById(R.id.rv_search_result);
        scrollBar = findViewById(R.id.scroll_bar);

        btnDelete.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        ivVoice.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvSearchResult.setLayoutManager(linearLayoutManager);


        mMusicHistoryAdapter = new MusicHistoryAdapter(HistoryDBManager.getInstance().findMusicAll());
        mMusicHistoryAdapter.setEmptyView(R.layout.view_empty_music_history, rvSearchResult);
        mMusicHistoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String name = mMusicHistoryAdapter.getData().get(position).getName();
                etMusic.setText(name);
                etMusic.setSelection(name.length());
                rvSearchResult.setVisibility(View.INVISIBLE);
                mPlayMusicVM.fetchPlayMusicList(etMusic.getText().toString());
                btnDelete.setVisibility(View.GONE);
                if (rvSearchResult.getAdapter() instanceof MusicHistoryAdapter) {
                    rvSearchResult.setAdapter(playMusicAdapter);
                    scrollBar.setRecyclerView(rvSearchResult);
                }
            }
        });
        mMusicHistoryAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.btn_history_delete) {
                    HistoryDBManager.getInstance().deleteMusicItem(mMusicHistoryAdapter.getData().get(position));
                    mMusicHistoryAdapter.setNewData(HistoryDBManager.getInstance().findMusicAll());
                }
            }
        });

        playMusicAdapter = new PlayMusicAdapter(new ArrayList<>());
        playMusicAdapter.setEmptyView(R.layout.view_empty_music, rvSearchResult);
        playMusicAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (ViewUtils.isFastClick()) {
                    return;
                }
                backToSkill(position);
            }
        });
        rvSearchResult.setAdapter(mMusicHistoryAdapter);
        scrollBar.setRecyclerView(rvSearchResult);
        setEditTextInhibitInputSpace(etMusic);
    }

    /**
     * 跳转到skill
     *
     * @param position
     */
    private void backToSkill(int position) {
        if (selectIndex != -1) {
            playMusicAdapter.getData().get(selectIndex).setSelected(false);
        }
        selectIndex = position;
        playMusicAdapter.getData().get(selectIndex).setSelected(true);
        playMusicAdapter.notifyDataSetChanged();
        PlayMusicBean playMusicBean = playMusicAdapter.getData().get(position);
        playMusicBean.setKey(etMusic.getText().toString());
        Bundle bundle = new Bundle();
        bundle.putString(VrPracticeConstants.ACTION_JSON, GsonHelper.toJson(playMusicBean));
        bundle.putInt(VrPracticeConstants.SKILL_REQUEST_CODE, mRequestCode);
        bundle.putInt(VrPracticeConstants.ACTION_POSITION, mActionPosition);
        LaunchUtils.launchAppWithData(this, VrPracticeConstants.PACKAGE_NAME,
                VrPracticeConstants.SKILL_CLASS_NAME, bundle);
        finish();
    }

    private void initData() {
        mPlayMusicVM = ViewModelProviders.of(this).get(PlayMusicVM.class);
        mPlayMusicVM.getPlayMusics().observe(this, new Observer<XmResource<List<PlayMusicBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<PlayMusicBean>> listXmResource) {
                listXmResource.handle(new OnCallback<List<PlayMusicBean>>() {
                    @Override
                    public void onSuccess(List<PlayMusicBean> data) {
                        playMusicAdapter.setXmMusics(mPlayMusicVM.getMusicList());
                        playMusicAdapter.setNewData(data);
                        tvSearchResultTitle.setText(getString(R.string.search_result));
                        btnDelete.setVisibility(View.GONE);
                        selectIndex = -1;
                        rvSearchResult.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(int code, String message) {
                        tvSearchResultTitle.setText(getString(R.string.search_result));
                        btnDelete.setVisibility(View.GONE);
                        playMusicAdapter.setNewData(null);
                        selectIndex = -1;
                        rvSearchResult.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(String msg) {
                        tvSearchResultTitle.setText(getString(R.string.search_result));
                        btnDelete.setVisibility(View.GONE);
                        playMusicAdapter.setNewData(null);
                        selectIndex = -1;
                        rvSearchResult.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(LaunchUtils.EXTRA_BUNDLE);
        if (bundleExtra == null) {
            return;
        }
        String text = bundleExtra.getString(VrPracticeConstants.ACTION_JSON);
        if (!TextUtils.isEmpty(text)) {
            parseIntent(GsonHelper.fromJson(text, PlayMusicBean.class));
        }
        mActionPosition = bundleExtra.getInt(VrPracticeConstants.ACTION_POSITION, 0);
        mRequestCode = bundleExtra.getInt(VrPracticeConstants.SKILL_REQUEST_CODE, 0);
    }

    private void parseIntent(PlayMusicBean playRadioBean) {
        if (playRadioBean == null || StringUtil.isEmpty(playRadioBean.getKey())) {
            return;
        }
        etMusic.setText(playRadioBean.getKey());
        etMusic.setSelection(playRadioBean.getKey().length());
        mPlayMusicVM.fetchPlayMusicList(playRadioBean.getKey());
        rvSearchResult.setAdapter(playMusicAdapter);
        scrollBar.setRecyclerView(rvSearchResult);
        tvSearchResultTitle.setText(getString(R.string.search_result));
        btnDelete.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:

                if (!StringUtil.isEmpty(etMusic.getText().toString())) {
                    if (rvSearchResult.getAdapter() instanceof MusicHistoryAdapter) {
                        rvSearchResult.setAdapter(playMusicAdapter);
                        scrollBar.setRecyclerView(rvSearchResult);
                        rvSearchResult.setVisibility(View.INVISIBLE);
                    }
                    tvSearchResultTitle.setText(getString(R.string.search_result));
                    btnDelete.setVisibility(View.GONE);

                    HistoryDBManager.getInstance().addMusicHistory(new MusicHistoryBean(etMusic.getText().toString()));
                    mPlayMusicVM.fetchPlayMusicList(etMusic.getText().toString());
                    btnDelete.setVisibility(View.GONE);
                } else {
                    showToast(R.string.please_input_search_content);
                }
                break;
            case R.id.btn_delete:
                if (mMusicHistoryAdapter != null) {
                    mMusicHistoryAdapter.setNewData(null);
                }
                HistoryDBManager.getInstance().deleteMusicHistory();
                break;
            case R.id.bt_voice:

                if (isVoiceRecordShowing()) {
                    return;
                }

                VpRecordDialog mVpRecordDialog = new VpRecordDialog(this, new VpRecordDialog.Callback() {
                    @Override
                    public void onSend(VpRecordDialog dlg, String translation) {
                        dlg.dismiss();
                        etMusic.setText(translation);
                        if (!TextUtils.isEmpty(translation)) {
                            etMusic.setSelection(translation.length());
                        }
                    }

                    @Override
                    public void onError(VpRecordDialog dlg, int errorCode) {
                        dlg.dismiss();
                    }
                });
                mVpRecordDialog.show();

                mVoiceRecordDlgRef = new WeakReference<>(mVpRecordDialog);
                break;
            default:
                break;
        }
    }

    private boolean isVoiceRecordShowing() {
        if (mVoiceRecordDlgRef == null)
            return false;
        Dialog lastDlg = mVoiceRecordDlgRef.get();
        return lastDlg != null && lastDlg.isShowing();
    }

    /**
     * 禁止EditText输入空格
     *
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }
}
