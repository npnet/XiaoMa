package com.xiaoma.vrpractice.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.ui
 *  @file_name:      AddSkillActivity
 *  @author:         Rookie
 *  @create_time:    2019/6/3 16:02
 *  @description：   TODO             */

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.mapadapter.ui.MapActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.pratice.SkillBean;
import com.xiaoma.model.pratice.SkillItemBean;
import com.xiaoma.model.pratice.UserSkillItemsBean;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.dialog.VpRecordDialog;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.vrpractice.R;
import com.xiaoma.vrpractice.adapter.AddSkillAdapter;
import com.xiaoma.vrpractice.adapter.ChooseAdapter;
import com.xiaoma.vrpractice.common.util.ActionsUtils;
import com.xiaoma.vrpractice.vm.AddSkillVM;

import org.simple.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddSkillActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvCancel;
    private Button btnSave;
    private EditText etVoice;
    private TextView tvCount;
    private RecyclerView rvAction;
    private AddSkillAdapter mAddSkillAdapter;
    private List<UserSkillItemsBean> mActionBeans;
    private PopupWindow mChooseSkillPop;
    private static final String SKILL_BEAN = "skill_bean";
    private static final String IS_EDIT = "is_edit";
    private boolean isEdit;
    private SkillBean mSkillBean;
    private AddSkillVM mAddSkillVM;
    private ChooseAdapter mChooseAdapter;

    private int skillItemPosition;
    private ImageView mIvVoice;
    private View mContainer;
    private ConfirmDialog mCancelDialog;

    private WeakReference<VpRecordDialog> mVoiceRecordDlgRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_skill);
        initView();
        initData();
        registerExit();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private void registerExit() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("close_app_VR_PRACTICE");
        registerReceiver(mBroadcastReceiver, intentFilter);
    }


    public static void skipAddSkill(Context context, SkillBean skillBean, boolean isEdit) {
        Intent intent = new Intent(context, AddSkillActivity.class);
        intent.putExtra(SKILL_BEAN, skillBean);
        intent.putExtra(IS_EDIT, isEdit);
        context.startActivity(intent);
    }

    private void initView() {
        tvCancel = findViewById(R.id.tv_cancel);
        btnSave = findViewById(R.id.btn_save);
        etVoice = findViewById(R.id.et_voice);
        tvCount = findViewById(R.id.tv_count);
        rvAction = findViewById(R.id.rv_action);
        mIvVoice = findViewById(R.id.iv_voice);
        mContainer = findViewById(R.id.container);
        btnSave.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        mIvVoice.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvAction.setLayoutManager(linearLayoutManager);
        rvAction.setHasFixedSize(true);
        mActionBeans = new ArrayList<>();
        mActionBeans.add(new UserSkillItemsBean(UserSkillItemsBean.TYPE_ADD_SKILL));
        mAddSkillAdapter = new AddSkillAdapter(mActionBeans);
        rvAction.setAdapter(mAddSkillAdapter);
        mAddSkillAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UserSkillItemsBean userSkillItemsBean = mAddSkillAdapter.getData().get(position);
                if (userSkillItemsBean.getItemType() == UserSkillItemsBean.TYPE_ACTION &&
                        userSkillItemsBean.getSkillItem().getId() != VrPracticeConstants.YOMI_GUIDE) {
                    //进入卡片详情,进行编辑
                    skipItemActivity(position, userSkillItemsBean);
                } else if (userSkillItemsBean.getItemType() == UserSkillItemsBean.TYPE_ADD_SKILL) {
                    showChooseSkillPop();
                }
            }
        });
        mAddSkillAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.iv_del) {
                    //刷新choose
                    filterChooseAfterDel(mActionBeans.get(position).getItemId());
                    mActionBeans.remove(position);
                    mAddSkillAdapter.setNewData(mActionBeans);
                }
            }
        });
        ActionsUtils.setEditTextInhibitInputSpace(etVoice, 30);
        etVoice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String word = s.toString();
                tvCount.setText(word.length() + "/30");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void skipItemActivity(int position, UserSkillItemsBean userSkillItemsBean) {
        Bundle bundle = new Bundle();
        bundle.putString(VrPracticeConstants.ACTION_JSON, userSkillItemsBean.getContent());
        bundle.putInt(VrPracticeConstants.ACTION_POSITION, position);
        bundle.putInt(VrPracticeConstants.SKILL_REQUEST_CODE, userSkillItemsBean.getItemId() + 10);
        if (!LaunchUtils.isAppInstalled(AddSkillActivity.this, userSkillItemsBean.getSkillItem().getPackageName())) {
            showToast(R.string.open_app_fail);
            return;
        }
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(userSkillItemsBean.getSkillItem().getPackageName(),
                userSkillItemsBean.getSkillItem().getClassName());
        intent.setComponent(componentName);
        intent.putExtra(LaunchUtils.EXTRA_BUNDLE, bundle);
        startActivity(intent);
    }


    private void initData() {
        Intent intent = getIntent();
        isEdit = intent.getBooleanExtra(IS_EDIT, false);
        if (isEdit) {
            mSkillBean = intent.getParcelableExtra(SKILL_BEAN);
            mActionBeans.addAll(mSkillBean.getUserSkillItems());
            Collections.sort(mActionBeans);
            mAddSkillAdapter.setNewData(mActionBeans);
            etVoice.setText(mSkillBean.getWord());
            etVoice.setSelection(mSkillBean.getWord().length());
        }
        mAddSkillVM = ViewModelProviders.of(this).get(AddSkillVM.class);
        mAddSkillVM.getAddSkill().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> stringXmResource) {
                stringXmResource.handle(new OnCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        showToast(R.string.save_success);
                        EventBus.getDefault().post("", VrPracticeConstants.EVENT_REFRESH_SKILL);
                        finish();
                    }

                    @Override
                    public void onError(int code, String message) {
                        showToast(getString(R.string.save_fail_msg, message));
                    }
                });
            }
        });

        mAddSkillVM.getEditSkill().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> stringXmResource) {
                stringXmResource.handle(new OnCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        EventBus.getDefault().post("", VrPracticeConstants.EVENT_REFRESH_SKILL);
                        startActivity(MainActivity.class);
                        showToast(R.string.save_success);
                        finish();
                    }

                    @Override
                    public void onError(int code, String message) {
                        showToast(getString(R.string.save_fail_msg, message));
                    }
                });
            }
        });
        mAddSkillVM.getSkillItems().observe(this, new Observer<XmResource<List<SkillItemBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<SkillItemBean>> listXmResource) {
                listXmResource.handle(new OnCallback<List<SkillItemBean>>() {
                    @Override
                    public void onSuccess(List<SkillItemBean> data) {
                        initChoosePop();
                        if (isEdit) {
                            //如果是编辑模式,需要初始化数据
                            mChooseAdapter.setDatas(filterChooseWithEdit(data));
                        } else {
                            mChooseAdapter.setDatas(data);
                        }
                        mChooseSkillPop.showAtLocation(mContainer, Gravity.START, 0, 0);

                    }

                    @Override
                    public void onError(int code, String message) {
                        showToast(getString(R.string.net_work_error));
                    }
                });
            }
        });
    }

    private void showChooseSkillPop() {
        if (!NetworkUtils.isConnected(this)) {
            //先判断网络
            showToast(R.string.net_work_error);
            return;
        }
        if (mChooseSkillPop == null) {
            mAddSkillVM.fetchSkillItems();
        } else {
            mChooseSkillPop.showAtLocation(mContainer, Gravity.START, 0, 0);
        }
    }

    private void initChoosePop() {
        View view = getLayoutInflater().inflate(R.layout.view_choose_skill, null);
        mChooseSkillPop = new PopupWindow(view, 900, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mChooseSkillPop.setAnimationStyle(R.style.PopAnimTranslate);
        mChooseSkillPop.setClippingEnabled(false);
        view.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChooseSkillPop != null) {
                    mChooseSkillPop.dismiss();
                }
            }
        });
        GridView gvSkill = view.findViewById(R.id.gv_skill);
        ArrayList<SkillItemBean> skillItemBeans = new ArrayList<>();
        mChooseAdapter = new ChooseAdapter(this, skillItemBeans);
        gvSkill.setAdapter(mChooseAdapter);
        gvSkill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //检查是否已添加同类型的skill
                if (mActionBeans.size() > 1 && checkSkill(position)) {
                    showToast(R.string.no_repeat_excute_skill);
                } else if (mChooseAdapter.getDatas().get(position).isDark()) {
                    showToast(checkSameTypeSkill(position));
                } else {
                    if (mChooseSkillPop != null) {
                        mChooseSkillPop.dismiss();
                    }
                    skipSkillActivity(position);
                }
            }
        });

    }

    /**
     * 判断技能类型是否存在重复
     *
     * @param position
     * @return
     */
    private boolean checkSkill(int position) {
        return getExistSkillCodes().contains(mChooseAdapter.getDatas().get(position).getId());
    }

    /**
     * 判断是否已存在同类型的技能,进行对应提示
     *
     * @param position
     * @return
     */
    private String checkSameTypeSkill(int position) {
        String darkTip = "";
        List<Integer> skillCodes = getExistSkillCodes();
        int skillCode = position + 1;
        if (skillCode == VrPracticeConstants.YOMI_GUIDE) {
            //如果点中了导航
            if (skillCodes.contains(VrPracticeConstants.YOMI_NEWS)) {
                darkTip = getString(R.string.remove_skill_before_add, getString(R.string.news));
            }
        } else if (skillCode == VrPracticeConstants.YOMI_NEWS) {
            if (skillCodes.contains(VrPracticeConstants.YOMI_GUIDE)) {
                darkTip = getString(R.string.remove_skill_before_add, getString(R.string.guide));
            } else if (skillCodes.contains(VrPracticeConstants.YOMI_PLAY_SOME_MUSIC)) {
                darkTip = getString(R.string.remove_skill_before_add, getString(R.string.music));
            } else if (skillCodes.contains(VrPracticeConstants.YOMI_PLAY_RADIO)) {
                darkTip = getString(R.string.remove_skill_before_add, getString(R.string.radio));
            }
        } else if (skillCode == VrPracticeConstants.YOMI_PLAY_SOME_MUSIC) {
            if (skillCodes.contains(VrPracticeConstants.YOMI_NEWS)) {
                darkTip = getString(R.string.remove_skill_before_add, getString(R.string.news));
            } else if (skillCodes.contains(VrPracticeConstants.YOMI_PLAY_RADIO)) {
                darkTip = getString(R.string.remove_skill_before_add, getString(R.string.radio));
            }
        } else if (skillCode == VrPracticeConstants.YOMI_PLAY_RADIO) {
            if (skillCodes.contains(VrPracticeConstants.YOMI_NEWS)) {
                darkTip = getString(R.string.remove_skill_before_add, getString(R.string.news));
            } else if (skillCodes.contains(VrPracticeConstants.YOMI_PLAY_SOME_MUSIC)) {
                darkTip = getString(R.string.remove_skill_before_add, getString(R.string.music));
            }
        }
        return darkTip;
    }

    /**
     * 刷新弹窗显示
     *
     * @return
     */
    private List<SkillItemBean> filterChoose() {
        List<UserSkillItemsBean> skillList = new ArrayList<>(mActionBeans);
        skillList.remove(skillList.size() - 1);
        mChooseAdapter.getDatas().get(skillItemPosition).setDark(true);
        int skillCode = skillItemPosition + 1;
        if (skillCode == VrPracticeConstants.YOMI_GUIDE) {
            //如果选中的是导航,新闻需要置灰
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_NEWS - 1).setDark(true);
        } else if (skillCode == VrPracticeConstants.YOMI_NEWS) {
            //如果选中的是新闻,导航需要置灰
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_GUIDE - 1).setDark(true);
            //音乐、电台、新闻同属一类
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_PLAY_SOME_MUSIC - 1).setDark(true);
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_PLAY_RADIO - 1).setDark(true);
        } else if (skillCode == VrPracticeConstants.YOMI_PLAY_SOME_MUSIC) {
            //音乐、电台、新闻同属一类
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_PLAY_RADIO - 1).setDark(true);
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_NEWS - 1).setDark(true);
        } else if (skillCode == VrPracticeConstants.YOMI_PLAY_RADIO) {
            //音乐、电台、新闻同属一类
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_PLAY_SOME_MUSIC - 1).setDark(true);
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_NEWS - 1).setDark(true);
        }
        return mChooseAdapter.getDatas();
    }

    /**
     * 刷新弹窗显示(编辑模式第一次进入)
     *
     * @return
     */
    private List<SkillItemBean> filterChooseWithEdit(List<SkillItemBean> data) {
        List<Integer> existSkillCodes = getExistSkillCodes();
        for (int i = 0; i < existSkillCodes.size(); i++) {
            chooseDarkByCode(existSkillCodes.get(i), data);
        }
        return data;
    }

    private void chooseDarkByCode(int skillCode, List<SkillItemBean> data) {
        if (skillCode == VrPracticeConstants.YOMI_GUIDE) {
            //如果选中的是导航,新闻需要置灰
            data.get(VrPracticeConstants.YOMI_NEWS - 1).setDark(true);
            data.get(VrPracticeConstants.YOMI_GUIDE - 1).setDark(true);
        } else if (skillCode == VrPracticeConstants.YOMI_NEWS) {
            //如果选中的是新闻,导航、音乐、电台需要置灰
            data.get(VrPracticeConstants.YOMI_NEWS - 1).setDark(true);
            data.get(VrPracticeConstants.YOMI_GUIDE - 1).setDark(true);
            data.get(VrPracticeConstants.YOMI_PLAY_SOME_MUSIC - 1).setDark(true);
            data.get(VrPracticeConstants.YOMI_PLAY_RADIO - 1).setDark(true);
        } else if (skillCode == VrPracticeConstants.YOMI_PLAY_SOME_MUSIC) {
            //音乐、电台、新闻同属一类
            data.get(VrPracticeConstants.YOMI_PLAY_SOME_MUSIC - 1).setDark(true);
            data.get(VrPracticeConstants.YOMI_PLAY_RADIO - 1).setDark(true);
            data.get(VrPracticeConstants.YOMI_NEWS - 1).setDark(true);
        } else if (skillCode == VrPracticeConstants.YOMI_PLAY_RADIO) {
            //音乐、电台、新闻同属一类
            data.get(VrPracticeConstants.YOMI_PLAY_RADIO - 1).setDark(true);
            data.get(VrPracticeConstants.YOMI_PLAY_SOME_MUSIC - 1).setDark(true);
            data.get(VrPracticeConstants.YOMI_NEWS - 1).setDark(true);
        } else {
            data.get(skillCode - 1).setDark(true);
        }
    }


    /**
     * 在删除某个执行技能后，刷新choose数据dark
     *
     * @param itemId
     */
    private void filterChooseAfterDel(int itemId) {
        if (mChooseAdapter == null || ListUtils.isEmpty(mChooseAdapter.getDatas())) {
            return;
        }
        if (itemId == VrPracticeConstants.YOMI_GUIDE) {
            //如果删除的是导航,导航恢复,新闻恢复与否要根据音乐和电台来判断
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_GUIDE - 1).setDark(false);

            List<Integer> existSkillCodes = getExistSkillCodes();
            if (!existSkillCodes.contains(VrPracticeConstants.YOMI_PLAY_SOME_MUSIC)
                    && !existSkillCodes.contains(VrPracticeConstants.YOMI_PLAY_RADIO)
                    && !existSkillCodes.contains(VrPracticeConstants.YOMI_NEWS)) {
                mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_NEWS - 1).setDark(false);
            }
        } else if (itemId == VrPracticeConstants.YOMI_NEWS) {
            //如果删除的是新闻,导航、音乐、电台、新闻都恢复
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_GUIDE - 1).setDark(false);
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_PLAY_SOME_MUSIC - 1).setDark(false);
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_PLAY_RADIO - 1).setDark(false);
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_NEWS - 1).setDark(false);
        } else if (itemId == VrPracticeConstants.YOMI_PLAY_SOME_MUSIC) {
            //音乐、电台、新闻同属一类
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_PLAY_RADIO - 1).setDark(false);
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_PLAY_SOME_MUSIC - 1).setDark(false);
            if (!getExistSkillCodes().contains(VrPracticeConstants.YOMI_GUIDE)) {
                mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_NEWS - 1).setDark(false);
            }
        } else if (itemId == VrPracticeConstants.YOMI_PLAY_RADIO) {
            //音乐、电台、新闻同属一类
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_PLAY_SOME_MUSIC - 1).setDark(false);
            mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_PLAY_RADIO - 1).setDark(false);
            if (!getExistSkillCodes().contains(VrPracticeConstants.YOMI_GUIDE)) {
                mChooseAdapter.getDatas().get(VrPracticeConstants.YOMI_NEWS - 1).setDark(false);
            }
        } else {
            mChooseAdapter.getDatas().get(itemId - 1).setDark(false);
        }
    }

    private List<Integer> getExistSkillCodes() {
        List<UserSkillItemsBean> skillList = new ArrayList<>(mActionBeans);
        skillList.remove(mActionBeans.size() - 1);
        List<Integer> skillCodes = new ArrayList<>();
        for (int i = 0; i < skillList.size(); i++) {
            skillCodes.add(skillList.get(i).getItemId());
        }
        return skillCodes;
    }

    private void skipSkillActivity(int position) {
        skillItemPosition = position;
        SkillItemBean skillItemBean = mChooseAdapter.getDatas().get(position);
        if (skillItemBean.getId() == VrPracticeConstants.YOMI_GUIDE) {
            startActivityForResult(new Intent(this, MapActivity.class), VrPracticeConstants.YOMI_GUIDE);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(VrPracticeConstants.SKILL_REQUEST_CODE, skillItemBean.getId());
            boolean flag = LaunchUtils.launchAppWithData(this, skillItemBean.getPackageName(),
                    skillItemBean.getClassName(), bundle);
            if (!flag) {
                showToast(getString(R.string.open_app_fail));
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_save) {
            String word = etVoice.getText().toString();
            if (mActionBeans.size() > 1 && !TextUtils.isEmpty(word)) {
                if (isEdit) {
                    mAddSkillVM.editSkills(String.valueOf(mSkillBean.getId()), mSkillBean.getWord(), word, getActionJson());
                } else {
                    mAddSkillVM.saveSkills(word, getActionJson());
                }
            } else if (!TextUtils.isEmpty(word)) {
                showToast(R.string.set_one_excute_at_least);
            } else {
                showToast(R.string.input_voice_text);
            }
        } else if (i == R.id.tv_cancel) {
            showCancelDialog();
        } else if (i == R.id.iv_voice) {

            if (isVoiceRecordShowing()) {
                return;
            }

            VpRecordDialog vpRecordDialog = new VpRecordDialog(this, new VpRecordDialog.Callback() {
                @Override
                public void onSend(VpRecordDialog dlg, String translation) {
                    dlg.dismiss();
                    etVoice.setText(translation);
                    if (!TextUtils.isEmpty(translation)) {
                        etVoice.setSelection(translation.length());
                    }
                }

                @Override
                public void onError(VpRecordDialog dlg, int errorCode) {
                    dlg.dismiss();
                    showToast(errorCode);
                }
            });
            vpRecordDialog.show();

            mVoiceRecordDlgRef = new WeakReference<>(vpRecordDialog);
        }
    }

    private boolean isVoiceRecordShowing() {
        if (mVoiceRecordDlgRef == null) {
            return false;
        }
        Dialog lastDlg = mVoiceRecordDlgRef.get();
        return lastDlg != null && lastDlg.isShowing();
    }

    private void showCancelDialog() {
        if (!StringUtil.isEmpty(etVoice.getText().toString()) || (!ListUtils.isEmpty(mAddSkillAdapter.getData()) && mAddSkillAdapter.getData().size() > 1)) {
            showGiveupDialog();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!StringUtil.isEmpty(etVoice.getText().toString()) || (!ListUtils.isEmpty(mAddSkillAdapter.getData()) && mAddSkillAdapter.getData().size() > 1)) {
            showGiveupDialog();
        } else {
            super.onBackPressed();
        }

    }

    private void showGiveupDialog() {
        mCancelDialog = new ConfirmDialog(this).setCancelable(true).setTitle(getString(R.string.tip)).setContent(getString(R.string.giveup_edit_skill));
        mCancelDialog.setPositiveButton(getString(R.string.give_up), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCancelDialog != null) {
                    mCancelDialog.dismiss();
                }
                finish();
            }
        });
        mCancelDialog.setNegativeButton(getString(R.string.cancel2), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCancelDialog != null) {
                    mCancelDialog.dismiss();
                }
            }
        });
        mCancelDialog.show();
    }

    private String getActionJson() {
        List<UserSkillItemsBean> skillList = new ArrayList<>(mActionBeans);
        skillList.remove(mActionBeans.size() - 1);
        return GsonHelper.toJson(skillList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == VrPracticeConstants.YOMI_GUIDE) {
            //增加skill
            UserSkillItemsBean actionBean = new UserSkillItemsBean(requestCode, UserSkillItemsBean.TYPE_ACTION);
            SkillItemBean skillItemBean = mChooseAdapter.getDatas().get(skillItemPosition);
            actionBean.setSkillItem(skillItemBean);
            actionBean.setItemId(skillItemBean.getId());
            actionBean.setType(skillItemBean.getType());
            SearchAddressInfo searchAddressInfo = data.getParcelableExtra(MapActivity.EXTRA_LOCATION_DATA);
            actionBean.setContent(GsonHelper.toJson(searchAddressInfo));
            mActionBeans.add(actionBean);
            filterChoose();
            Collections.sort(mActionBeans);
            mAddSkillAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundleExtra = intent.getBundleExtra(LaunchUtils.EXTRA_BUNDLE);
        if (bundleExtra == null) {
            return;
        }
        String actionJson = bundleExtra.getString(VrPracticeConstants.ACTION_JSON);
        int requestCode = bundleExtra.getInt(VrPracticeConstants.SKILL_REQUEST_CODE, 0);
        if (requestCode <= VrPracticeConstants.YOMI_NEWS) {
            //增加skill
            UserSkillItemsBean actionBean = new UserSkillItemsBean(requestCode, UserSkillItemsBean.TYPE_ACTION);
            SkillItemBean skillItemBean = mChooseAdapter.getDatas().get(skillItemPosition);
            actionBean.setSkillItem(skillItemBean);
            actionBean.setItemId(skillItemBean.getId());
            actionBean.setType(skillItemBean.getType());
            actionBean.setContent(actionJson);
            mActionBeans.add(actionBean);
            filterChoose();
        } else {
            //编辑skill卡片
            int actionPosition = bundleExtra.getInt(VrPracticeConstants.ACTION_POSITION);
            if (!TextUtils.isEmpty(actionJson)) {
                mActionBeans.get(actionPosition).setContent(actionJson);
            } else {
                filterChooseAfterDel(mActionBeans.get(actionPosition).getItemId());
                mActionBeans.remove(actionPosition);
            }
        }
        Collections.sort(mActionBeans);
        mAddSkillAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
