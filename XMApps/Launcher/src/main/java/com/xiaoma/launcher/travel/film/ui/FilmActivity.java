package com.xiaoma.launcher.travel.film.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.discretescrollview.DSVOrientation;
import com.discretescrollview.DiscreteScrollView;
import com.discretescrollview.transform.ScaleTransformer;
import com.xiaoma.ad.utils.GsonUtil;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.travel.film.adapter.FilmAdapter;
import com.xiaoma.launcher.travel.film.utils.CarInfoManager;
import com.xiaoma.launcher.travel.film.vm.FilmVM;
import com.xiaoma.launcher.travel.itemevent.CinemaTrackerBean;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.common.TripConstant;
import com.xiaoma.trip.movie.response.FilmsBean;
import com.xiaoma.trip.movie.response.FilmsPageDataBean;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2019/2/13 0013
 * 影片资源
 */
@PageDescComponent(EventConstants.PageDescribe.FilmActivityPagePathDesc)
public class FilmActivity extends BaseActivity implements DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>, DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder> {

    private static final int CAR_SPEED_LIMIT = 10;
    private static Boolean mIsRecommend = false;
    private List<FilmsBean> mList = new ArrayList();
    private DiscreteScrollView mItemFilm;
    private FilmVM mFilmVM;
    private FilmAdapter mFilmAdapter;
    private TextView mFilmName;
    private Button mBuyFilmTicket;
    private Button mLookTrailer;
    private int pageNum = 1;
    private int maxPageNum;
    private TextView filmTitle;
    private LinearLayout filmLayout;
    private RelativeLayout mNotdataView;
    private LinearLayout mHasdataSelectView;
    private TextView mTvTips;
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film_activity);
        bindView();
        initView();
        initData();
    }

    private void bindView() {
        mNotdataView = findViewById(R.id.notdata_view);
        mHasdataSelectView = findViewById(R.id.hasdata_select_view);
        mTvTips = findViewById(R.id.tv_tips);
        filmLayout = findViewById(R.id.film_layout);
        mItemFilm = findViewById(R.id.film_rv);
        filmTitle = findViewById(R.id.film_title);
        mFilmName = findViewById(R.id.film_name);
        mBuyFilmTicket = findViewById(R.id.buy_film_ticket);
        mLookTrailer = findViewById(R.id.look_trailer);
    }

    private void initView() {
        mIsRecommend = getIntent().getBooleanExtra(LauncherConstants.TravelConstants.FILM_DATA_RECOMMEND_TYPE, false);
        if (mIsRecommend) {
            filmTitle.setText(R.string.recom_film);
            mTvTips.setText(String.format(getString(R.string.no_recommend), getString(R.string.move)));
        } else {
            filmTitle.setText(R.string.hot_film);
            mTvTips.setText(String.format(getString(R.string.no_hotel), getString(R.string.move)));
        }
        mFilmAdapter = new FilmAdapter();
        mItemFilm.setOrientation(DSVOrientation.HORIZONTAL);
        mItemFilm.addOnItemChangedListener(this);
        mItemFilm.addScrollStateChangeListener(this);
        mItemFilm.setSlideOnFling(true);
        mItemFilm.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        mFilmAdapter.setEnableLoadMore(true);
        mFilmAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, mItemFilm);
        mFilmAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mItemFilm.smoothScrollToPosition(position);
            }
        });
        mItemFilm.setAdapter(mFilmAdapter);
    }

    private void loadMore() {
        if (mFilmVM == null) {
            return;
        }
        if (pageNum >= maxPageNum) {
            notifyLoadState(LauncherConstants.END);
        } else {
            pageNum++;
            mFilmVM.SearchFilm(type, TripConstant.FILM_TYPE_SHOWING, pageNum, LauncherConstants.PAGE_SIZE);
        }
    }

    private void notifyLoadState(int state) {
        if (LauncherConstants.COMPLETE == state) {
            mFilmAdapter.loadMoreComplete();
        } else if (LauncherConstants.END == state) {
            mFilmAdapter.loadMoreEnd();
        } else if (LauncherConstants.FAILED == state) {
            mFilmAdapter.loadMoreFail();
        }
    }


    private void initData() {
        type = getIntent().getStringExtra("type");
        mFilmVM = ViewModelProviders.of(this).get(FilmVM.class);
        mFilmVM.getFilmsPagerData().observe(this, new Observer<XmResource<FilmsPageDataBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<FilmsPageDataBean> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<FilmsPageDataBean>() {
                    @Override
                    public void onSuccess(FilmsPageDataBean data) {
                        if (!ListUtils.isEmpty(data.getFilms())) {
                            filmLayout.setVisibility(View.VISIBLE);
                            mHasdataSelectView.setVisibility(View.VISIBLE);

                            if (data.getPageInfo() != null) {
                                maxPageNum = data.getPageInfo().getTotalPage();
                            }
                            mList.addAll(data.getFilms());
                            mFilmAdapter.addData(data.getFilms());
                            if (pageNum == 1) {
                                if (mList.size() >= 4) {
                                    mItemFilm.scrollToPosition(3);
                                } else {
                                    mItemFilm.scrollToPosition(mList.size() - 1);
                                }
                            }
                            notifyLoadState(LauncherConstants.COMPLETE);
                        } else {
                            mItemFilm.removeScrollStateChangeListener(FilmActivity.this);
                            mItemFilm.removeItemChangedListener(FilmActivity.this);
                            mNotdataView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                        notifyLoadState(LauncherConstants.FAILED);
                    }
                });
            }
        });
        if (!NetworkUtils.isConnected(this)) {
            showNoNetView();
            return;
        }
        if (mIsRecommend) {
            mFilmVM.fetchRecommendByFilm();
        } else {
            mFilmVM.SearchFilm(type, TripConstant.FILM_TYPE_SHOWING, pageNum, LauncherConstants.PAGE_SIZE);
        }

    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        if (ListUtils.isEmpty(mList)) {
            return;
        }
        if (adapterPosition < mList.size()) {
            XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.LISTITEM,       //按钮名称(如播放,音乐列表)
                    GsonUtil.toJson(setFilmTracker(mList.get(adapterPosition))),          //对应id值(如音乐列表对应item id值)
                    EventConstants.PagePath.filmActivityPath,    //页面路径
                    EventConstants.PageDescribe.FilmActivityPagePathDesc);//页面路径中午意思
            onItemChanged(mList.get(adapterPosition));
        }

    }

    private void onItemChanged(final FilmsBean filmsBean) {
        mFilmName.setText(filmsBean.getTitle());
        mBuyFilmTicket.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.FILM_BUY_TICKETS, GsonUtil.toJson(setFilmTracker(filmsBean)));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (!NetworkUtils.isConnected(FilmActivity.this)) {
                    showToastException(R.string.net_work_error);
                    return;
                }

                if (filmsBean != null) {
                    TPUtils.put(FilmActivity.this, LauncherConstants.FILM_TYPE, filmsBean.getFilmType().replaceAll(",", "/"));
                    TPUtils.put(FilmActivity.this, LauncherConstants.PATH_TYPE, getString(R.string.select_cinema));
                    Intent intent = new Intent(FilmActivity.this, SelectCinemaActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(LauncherConstants.ActionExtras.MOVIE_TAG, filmsBean.getFilmId());
                    bundle.putString(LauncherConstants.ActionExtras.MOVIE_NAME, filmsBean.getTitle());
                    intent.putExtras(bundle);
                    intent.putExtra(LauncherConstants.ActionExtras.MOVIE_BUNDLE, bundle);
                    startActivity(intent);
                }
            }
        });

        mLookTrailer.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.FILM_LOOK_TRAILER, GsonUtil.toJson(setFilmTracker(filmsBean)));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (filmsBean.getFilmDetailForm() == null || StringUtil.isEmpty(filmsBean.getFilmDetailForm().getTrailersUrl())) {
                    XMToast.showToast(FilmActivity.this, R.string.not_find_film);
                } else {
                    if (CarInfoManager.getInstance().getIsWatchVideoInDriving()
                            && CarInfoManager.getInstance().getCurrentSpeedData() >= CAR_SPEED_LIMIT) {
                        XMToast.showToast(FilmActivity.this, R.string.driving_forbid_watch_video);
                        return;
                    }

                    if (!CarInfoManager.getInstance().getIsWatchVideoInDriving() &&
                            CarInfoManager.getInstance().getCurrentSpeedData() >= CAR_SPEED_LIMIT) {
                        showWarnPrompt(filmsBean);
                        return;
                    }

                    previewTrailer(filmsBean, false);
                }
            }
        });
    }

    @Override
    public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
        if (adapterPosition == mList.size() && pageNum >= maxPageNum) {
            XMToast.showToast(this, R.string.no_more_data);
            mItemFilm.smoothScrollToPosition(adapterPosition - 1);
        } else if (adapterPosition == mList.size()) {
            mItemFilm.scrollToPosition(adapterPosition - 1);
        }
    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {

    }

    private CinemaTrackerBean setFilmTracker(FilmsBean filmsBean) {
        CinemaTrackerBean deliciousTrackerBean = new CinemaTrackerBean();
        deliciousTrackerBean.id = filmsBean.getFilmId();
        deliciousTrackerBean.value = filmsBean.getTitle();
        deliciousTrackerBean.h = filmsBean.getFilmType();
        return deliciousTrackerBean;
    }

    public static Boolean getmIsRecommend() {
        return mIsRecommend;
    }


    private void previewTrailer(FilmsBean filmsBean, boolean allow) {
        Intent intent = new Intent(FilmActivity.this, FilmTrailerActivity.class);
        intent.putExtra(LauncherConstants.ActionExtras.FILMS_BEAN, filmsBean);
        if (allow) {
            intent.putExtra(LauncherConstants.ActionExtras.ALLOW_PREVIEW_TRAILER, true);
        }
        startActivity(intent);
    }


    private void showWarnPrompt(FilmsBean filmsBean) {
        final ConfirmDialog warnDialog = new ConfirmDialog(this, false);
        warnDialog.setContent(getString(R.string.driving_watch_video_message) + "\n" + getString(R.string.driving_watch_video_tip))
                .setPositiveButton(getString(R.string.driving_watch_video_replay), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.CONTINUE_PLAY, null);
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        //todo 继续播放
                        warnDialog.dismiss();
                        previewTrailer(filmsBean, true);
                    }
                })
                .setNegativeButton(getString(R.string.driving_watch_video_stop_close), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.STOP_CLOSE, null);
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        //todo 停止播放 并关闭当前播放页面
                        warnDialog.dismiss();

                    }
                })
                .show();
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        if (NetworkUtils.isConnected(this)) {
            if (mIsRecommend) {
                mFilmVM.fetchRecommendByFilm();
            } else {
                mFilmVM.SearchFilm(type, TripConstant.FILM_TYPE_SHOWING, pageNum, LauncherConstants.PAGE_SIZE);
            }
        }
    }
}
