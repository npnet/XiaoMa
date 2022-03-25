package com.xiaoma.xting.local.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.view.ControllableViewPager;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.xting.MainActivity;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.control.AudioFocusManager;
import com.xiaoma.xting.common.playerSource.control.IPlayerControl;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.local.model.AMChannelBean;
import com.xiaoma.xting.local.model.BaseChannelBean;
import com.xiaoma.xting.local.model.FMChannelBean;
import com.xiaoma.xting.local.vm.LocalFMVM;
import com.xiaoma.xting.sdk.LocalFMFactory;
import com.xiaoma.xting.sdk.LocalFMStatusListenerImpl;
import com.xiaoma.xting.sdk.model.BandType;
import com.xiaoma.xting.sdk.model.XMRadioStation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author KY
 * @date 2018/10/10
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_FM_LOCAL)
public class LocalFMFragment extends VisibilityFragment {
    private ControllableViewPager vpLocalFm;
    private RadioGroup rgFmAm;
    private RadioButton rbFm;
    private RadioButton rbAm;
    private Button ibAutoSaveChannel;
    private Button ibManualTuning;
    private ImageButton ibPower;

    private List<Fragment> fragments = new ArrayList<>(2);
    private String[] titles;
    private LocalFMVM localFMVM;

    public static BaseFragment newInstance() {
        return new LocalFMFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_fm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        initListener();
        initData();
    }

    private void bindView(View view) {
        rgFmAm = view.findViewById(R.id.rg_fm_am);
        rbFm = view.findViewById(R.id.rb_fm);
        rbAm = view.findViewById(R.id.rb_am);
        ibAutoSaveChannel = view.findViewById(R.id.ib_auto_save_channel);
        ibManualTuning = view.findViewById(R.id.ib_manual_tuning);
        ibPower = view.findViewById(R.id.ib_power);
        vpLocalFm = view.findViewById(R.id.vp_local_fm);
    }

    private void initListener() {
        ibAutoSaveChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.ACTION_AUTO_SEARCH_PROGRAM})
            @ResId({R.id.ib_auto_save_channel})
            public void onClick(View v) {
                PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
                boolean b;
                if (!LocalFMFactory.getSDK().isRadioOpen()) {
                    b = LocalFMFactory.getSDK().openRadio();
                } else {
                    b = AudioFocusManager.getInstance().requestFMFocus();
                }
                if (!b) return;//如果因为焦点或其他原因未能打开fm则不做操作
                getProgressDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        getProgressDialog().setOnCancelListener(null);
                        LocalFMFactory.getSDK().cancel();
                    }
                });
                showProgressDialog(R.string.channel_searching);
                final int band = rbFm.isChecked() ? XtingConstants.FMAM.TYPE_FM : XtingConstants.FMAM.TYPE_AM;
                final BandType currentBand = LocalFMFactory.getSDK().getCurrentBand();
                if (currentBand != null && currentBand.getBand() != band) {
                    LocalFMFactory.getSDK().switchBand(BandType.valueOf(band));
                }
                LocalFMFactory.getSDK().addLocalFMStatusListener(new LocalFMStatusListenerImpl() {
                    @Override
                    public void onError(int code, String msg) {
                        threadSafelyDismissProgress();
                    }

                    @Override
                    public void onScanAllResult(List<XMRadioStation> stations) {
                        dismissProgress();
                        LocalFMFactory.getSDK().removeLocalFMStatusListener(this);
                        if (CollectionUtil.isListEmpty(stations)) {
                            LocalFMOperateManager.newSingleton().playLastRadio();
                            return;
                        }
                        if (stations.size() > 1) {
                            Collections.sort(stations);
                        }
                        if (band == XtingConstants.FMAM.TYPE_FM) {
                            XtingUtils.getDBManager(null).deleteAll(FMChannelBean.class);
                        } else if (band == XtingConstants.FMAM.TYPE_AM) {
                            XtingUtils.getDBManager(null).deleteAll(AMChannelBean.class);
                        }
                        for (int i = 0; i < stations.size(); i++) {
                            XMRadioStation station = stations.get(i);
                            BaseChannelBean channelBean = XtingUtils.getBaseChannelByValue(station.getChannel());
                            if (channelBean instanceof FMChannelBean) {
                                localFMVM.saveFMChannel((FMChannelBean) channelBean);
                                if (i == 0) {
                                    LocalFMOperateManager.newSingleton().playChannel(channelBean);
                                }
                            } else if (channelBean instanceof AMChannelBean) {
                                localFMVM.saveAMChannel((AMChannelBean) channelBean);
                                if (i == 0) {
                                    LocalFMOperateManager.newSingleton().playChannel(channelBean);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        dismissProgress();
                    }
                });
                localFMVM.getLocation();
                LocalFMFactory.getSDK().scanAll(band == XtingConstants.FMAM.TYPE_FM ? BandType.FM : BandType.AM);
            }
        });
        ibManualTuning.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.ACTION_MANUAL_SELECT_PROGRAM})
            @ResId({R.id.ib_manual_tuning})
            public void onClick(View v) {
                FragmentActivity activity = getActivity();
                PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
                if (activity != null) {
                    if (!LocalFMFactory.getSDK().isRadioOpen()) {
                        PlayerSourceFacade.newSingleton().getPlayerControl().playWithModel(SharedPrefUtils.getLastYQPlayerInfo(true));
//                        LocalFMOperateManager.newSingleton().OpenRadioAndPlayLast();
                    }
                    ((MainActivity) activity).launchManualTune();
                }
            }
        });

        vpLocalFm.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                rgFmAm.check(rgFmAm.getChildAt(position).getId());
            }
        });

        rgFmAm.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_fm:
                        rbFm.setTextAppearance(mContext, R.style.manual_switch_light_blue);
                        rbAm.setTextAppearance(mContext, R.style.manual_switch_normal_gray);
                        break;
                    case R.id.rb_am:
                        rbAm.setTextAppearance(mContext, R.style.manual_switch_light_blue);
                        rbFm.setTextAppearance(mContext, R.style.manual_switch_normal_gray);
                        break;
                    default:
                        break;
                }

                vpLocalFm.setCurrentItem(group.indexOfChild(group.findViewById(checkedId)));
            }
        });

        ibPower.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.ACTION_LOCAL_RADIO_POWER})
            @ResId({R.id.ib_power})
            public void onClick(View v) {
                PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
                IPlayerControl playerControl = PlayerSourceFacade.newSingleton().getPlayerControl();
                if (ibPower.isSelected()) {
                    playerControl.exitPlayer();
                } else {
                    playerControl.playWithModel(SharedPrefUtils.getLastYQPlayerInfo(true));
                }
            }
        });

        ibPower.setSelected(LocalFMFactory.getSDK().isRadioOpen());

        LocalFMFactory.getSDK().addLocalFMStatusListener(new LocalFMStatusListenerImpl() {
            @Override
            public void onRadioOpen() {
                super.onRadioOpen();
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        ibPower.setSelected(true);
                    }
                });
            }

            @Override
            public void onRadioClose(XMRadioStation xmRadioStation) {
                super.onRadioClose(xmRadioStation);
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        ibPower.setSelected(false);
                    }
                });

            }
        });
    }

    private void threadSafelyDismissProgress() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    dismissProgress();
                }
            });
        } else {
            dismissProgress();
        }
    }

    private void initData() {
        rgFmAm.check(R.id.rb_fm);
        titles = new String[]{getString(R.string.fm), getString(R.string.am)};
        fragments.add(FMFragment.newInstance());
        fragments.add(AMFragment.newInstance());
        localFMVM = ViewModelProviders.of(getActivity()).get(LocalFMVM.class);
        vpLocalFm.setScrollable(false);
        vpLocalFm.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
    }

    @Override
    public String getThisNode() {
        return NodeConst.Xting.FGT_LOCAL;
    }

    @Override
    public boolean handleJump(String nextNode) {
        if (nextNode != null && nextNode.equals(NodeConst.Xting.FGT_LOCAL_SEARCH)) {
            ibAutoSaveChannel.performClick();
            return true;
        } else {
            return super.handleJump(nextNode);
        }
    }
}
