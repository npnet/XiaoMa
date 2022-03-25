package com.xiaoma.xting.sdk.test;

import android.support.annotation.Nullable;

import com.xiaoma.utils.GsonHelper;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.sdk.OnlineFM;
import com.xiaoma.xting.sdk.OnlineFMFactory;
import com.xiaoma.xting.sdk.OnlineFMPlayerFactory;
import com.xiaoma.xting.sdk.TestPanelActivity;
import com.xiaoma.xting.sdk.bean.AbsXMDataCallback;
import com.xiaoma.xting.sdk.bean.XMAlbumList;
import com.xiaoma.xting.sdk.bean.XMAnnouncerList;
import com.xiaoma.xting.sdk.bean.XMBatchAlbumList;
import com.xiaoma.xting.sdk.bean.XMBatchTrackList;
import com.xiaoma.xting.sdk.bean.XMCategory;
import com.xiaoma.xting.sdk.bean.XMCategoryList;
import com.xiaoma.xting.sdk.bean.XMCityList;
import com.xiaoma.xting.sdk.bean.XMColumnDetail;
import com.xiaoma.xting.sdk.bean.XMColumnDetailAlbum;
import com.xiaoma.xting.sdk.bean.XMColumnList;
import com.xiaoma.xting.sdk.bean.XMDataCallback;
import com.xiaoma.xting.sdk.bean.XMGussLikeAlbumList;
import com.xiaoma.xting.sdk.bean.XMHotWordList;
import com.xiaoma.xting.sdk.bean.XMMetaDataList;
import com.xiaoma.xting.sdk.bean.XMProvinceList;
import com.xiaoma.xting.sdk.bean.XMRadioCategoryList;
import com.xiaoma.xting.sdk.bean.XMRadioList;
import com.xiaoma.xting.sdk.bean.XMRadioListByCategory;
import com.xiaoma.xting.sdk.bean.XMRadioListById;
import com.xiaoma.xting.sdk.bean.XMRankAlbumList;
import com.xiaoma.xting.sdk.bean.XMRankList;
import com.xiaoma.xting.sdk.bean.XMRankTrackList;
import com.xiaoma.xting.sdk.bean.XMRelativeAlbums;
import com.xiaoma.xting.sdk.bean.XMScheduleList;
import com.xiaoma.xting.sdk.bean.XMSearchAlbumList;
import com.xiaoma.xting.sdk.bean.XMSearchAll;
import com.xiaoma.xting.sdk.bean.XMSearchTrackList;
import com.xiaoma.xting.sdk.bean.XMSuggestWords;
import com.xiaoma.xting.sdk.bean.XMTagList;
import com.xiaoma.xting.sdk.bean.XMTrackHotList;
import com.xiaoma.xting.sdk.bean.XMTrackList;
import com.xiaoma.xting.sdk.bean.XMUpdateBatchList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/12
 */
public class XMLYTestPanelActivity extends TestPanelActivity {

    @Override
    protected void setupItemData() {
        final OnlineFM sdk = OnlineFMFactory.getInstance().getSDK();
        itemList.add(new Item("点播相关接口 ▼"));

        itemList.add(new Item("getCategories").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCategories(new XMDataCallback<XMCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable XMCategoryList data) {
                        setResult(GsonHelper.toJson(data));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getTags").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCategories(new XMDataCallback<XMCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable XMCategoryList data) {
                        XMCategory xmCategory = data.getCategories().get(0);
                        sdk.getTags(xmCategory.getId(), 0, new XMDataCallback<XMTagList>() {
                            @Override
                            public void onSuccess(@Nullable XMTagList data) {
                                setResult(GsonHelper.toJson(data));
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getAlbumList").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCategories(new XMDataCallback<XMCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable XMCategoryList data) {
                        XMCategory xmCategory = data.getCategories().get(0);
                        sdk.getAlbumList(xmCategory.getId(), 1, "", 1, new XMDataCallback<XMAlbumList>() {
                            @Override
                            public void onSuccess(@Nullable XMAlbumList data) {
                                setResult(GsonHelper.toJson(data));
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getTracks").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCategories(new XMDataCallback<XMCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable XMCategoryList data) {
                        XMCategory xmCategory = data.getCategories().get(0);
                        sdk.getAlbumList(xmCategory.getId(), 1, "", 1, new XMDataCallback<XMAlbumList>() {
                            @Override
                            public void onSuccess(@Nullable XMAlbumList data) {
                                long albumId = data.getAlbums().get(0).getId();
                                sdk.getTracks(albumId, "asc", 1, new AbsXMDataCallback<XMTrackList>() {
                                    @Override
                                    public void onSuccess(@Nullable XMTrackList data) {
                                        setResult(GsonHelper.toJson(data));
                                        OnlineFMPlayerFactory.getPlayer().playList(data.getTracks(), 0);
                                    }

                                    @Override
                                    public void onError(int code, String msg) {
                                        setResult("code: " + code + " msg: " + msg);
                                    }
                                });

                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getBatch").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCategories(new XMDataCallback<XMCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable XMCategoryList data) {
                        XMCategory xmCategory = data.getCategories().get(0);
                        sdk.getAlbumList(xmCategory.getId(), 1, "", 1, new XMDataCallback<XMAlbumList>() {
                            @Override
                            public void onSuccess(@Nullable XMAlbumList data) {
                                List<String> ids = new ArrayList<>();
                                ids.add(String.valueOf(data.getAlbums().get(0).getId()));
                                ids.add(String.valueOf(data.getAlbums().get(1).getId()));
                                ids.add(String.valueOf(data.getAlbums().get(2).getId()));
                                sdk.getBatch(ids, new XMDataCallback<XMBatchAlbumList>() {
                                    @Override
                                    public void onSuccess(@Nullable XMBatchAlbumList data) {
                                        setResult(GsonHelper.toJson(data));
                                    }

                                    @Override
                                    public void onError(int code, String msg) {
                                        setResult("code: " + code + " msg: " + msg);
                                    }
                                });

                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getHotTracks").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCategories(new XMDataCallback<XMCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable XMCategoryList data) {
                        sdk.getHotTracks(data.getCategories().get(0).getId(), "", 1, new XMDataCallback<XMTrackHotList>() {
                            @Override
                            public void onSuccess(@Nullable XMTrackHotList data) {
                                setResult(GsonHelper.toJson(data));
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getUpdateBatch").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCategories(new XMDataCallback<XMCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable XMCategoryList data) {
                        XMCategory xmCategory = data.getCategories().get(0);
                        sdk.getAlbumList(xmCategory.getId(), 1, "", 1, new XMDataCallback<XMAlbumList>() {
                            @Override
                            public void onSuccess(@Nullable XMAlbumList data) {
                                List<String> ids = new ArrayList<>();
                                ids.add(String.valueOf(data.getAlbums().get(0).getId()));
                                ids.add(String.valueOf(data.getAlbums().get(1).getId()));
                                ids.add(String.valueOf(data.getAlbums().get(2).getId()));
                                sdk.getUpdateBatch(ids, new XMDataCallback<XMUpdateBatchList>() {
                                    @Override
                                    public void onSuccess(@Nullable XMUpdateBatchList data) {
                                        setResult(GsonHelper.toJson(data));
                                    }

                                    @Override
                                    public void onError(int code, String msg) {
                                        setResult("code: " + code + " msg: " + msg);
                                    }
                                });

                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getBatchTracks").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCategories(new XMDataCallback<XMCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable XMCategoryList data) {
                        XMCategory xmCategory = data.getCategories().get(0);
                        sdk.getAlbumList(xmCategory.getId(), 1, "", 1, new XMDataCallback<XMAlbumList>() {
                            @Override
                            public void onSuccess(@Nullable XMAlbumList data) {
                                long albumId = data.getAlbums().get(0).getId();
                                sdk.getTracks(albumId, "asc", 1, new AbsXMDataCallback<XMTrackList>() {
                                    @Override
                                    public void onSuccess(@Nullable XMTrackList data) {
                                        List<String> ids = new ArrayList<>();
                                        ids.add(String.valueOf(data.getTracks().get(0).getDataId()));
                                        ids.add(String.valueOf(data.getTracks().get(1).getDataId()));
                                        ids.add(String.valueOf(data.getTracks().get(2).getDataId()));
                                        sdk.getBatchTracks(ids, new XMDataCallback<XMBatchTrackList>() {
                                            @Override
                                            public void onSuccess(@Nullable XMBatchTrackList data) {
                                                setResult(GsonHelper.toJson(data));
                                            }

                                            @Override
                                            public void onError(int code, String msg) {
                                                setResult("code: " + code + " msg: " + msg);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int code, String msg) {
                                        setResult("code: " + code + " msg: " + msg);
                                    }
                                });

                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getMetadataList").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCategories(new XMDataCallback<XMCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable XMCategoryList data) {
                        sdk.getMetadataList(data.getCategories().get(0).getId(), new XMDataCallback<XMMetaDataList>() {
                            @Override
                            public void onSuccess(@Nullable XMMetaDataList data) {
                                setResult(GsonHelper.toJson(data));
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("直播相关接口 ▼"));

        itemList.add(new Item("getProvinces").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getProvinces(new XMDataCallback<XMProvinceList>() {
                    @Override
                    public void onSuccess(@Nullable XMProvinceList data) {
                        setResult(GsonHelper.toJson(data));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getCitys").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getProvinces(new XMDataCallback<XMProvinceList>() {
                    @Override
                    public void onSuccess(@Nullable XMProvinceList data) {
                        sdk.getCitys(data.getProvinceList().get(2).getProvinceCode(), new XMDataCallback<XMCityList>() {
                            @Override
                            public void onSuccess(@Nullable XMCityList data) {
                                setResult(GsonHelper.toJson(data));
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getCountryRadios").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCountryRadios(1, new XMDataCallback<XMRadioList>() {
                    @Override
                    public void onSuccess(@Nullable XMRadioList data) {
                        setResult(GsonHelper.toJson(data));
                        OnlineFMPlayerFactory.getPlayer().playRadio(data.getRadios().get(0));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getProvinceRadios").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getProvinces(new XMDataCallback<XMProvinceList>() {
                    @Override
                    public void onSuccess(@Nullable XMProvinceList data) {
                        long provinceCode = data.getProvinceList().get(0).getProvinceCode();
                        sdk.getProvinceRadios(provinceCode, 1, new XMDataCallback<XMRadioList>() {
                            @Override
                            public void onSuccess(@Nullable XMRadioList data) {
                                setResult(GsonHelper.toJson(data));
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getNetworkRadios").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getNetworkRadios(1, new XMDataCallback<XMRadioList>() {
                    @Override
                    public void onSuccess(@Nullable XMRadioList data) {
                        setResult(GsonHelper.toJson(data));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getTodaySchedules").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCountryRadios(1, new XMDataCallback<XMRadioList>() {
                    @Override
                    public void onSuccess(@Nullable XMRadioList data) {
                        sdk.getTodaySchedules(data.getRadios().get(0).getDataId(), new AbsXMDataCallback<XMScheduleList>() {
                            @Override
                            public void onSuccess(@Nullable XMScheduleList data) {
                                setResult(GsonHelper.toJson(data));
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getWeekdaySchedules").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCountryRadios(1, new XMDataCallback<XMRadioList>() {
                    @Override
                    public void onSuccess(@Nullable XMRadioList data) {
                        sdk.getWeekdaySchedules(data.getRadios().get(0).getDataId(), XtingConstants.WeekDay.MONDAY, new XMDataCallback<XMScheduleList>() {
                            @Override
                            public void onSuccess(@Nullable XMScheduleList data) {
                                setResult(GsonHelper.toJson(data));
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getRadiosByIds").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCountryRadios(1, new XMDataCallback<XMRadioList>() {
                    @Override
                    public void onSuccess(@Nullable XMRadioList data) {
                        List<String> ids = new ArrayList<>();
                        ids.add(String.valueOf(data.getRadios().get(0).getDataId()));
                        ids.add(String.valueOf(data.getRadios().get(1).getDataId()));
                        ids.add(String.valueOf(data.getRadios().get(2).getDataId()));
                        sdk.getRadiosByIds(ids, new XMDataCallback<XMRadioListById>() {
                            @Override
                            public void onSuccess(@Nullable XMRadioListById data) {
                                setResult(GsonHelper.toJson(data));
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getRadiosByCity").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getProvinces(new XMDataCallback<XMProvinceList>() {
                    @Override
                    public void onSuccess(@Nullable XMProvinceList data) {
                        sdk.getCitys(data.getProvinceList().get(2).getProvinceCode(), new XMDataCallback<XMCityList>() {
                            @Override
                            public void onSuccess(@Nullable XMCityList data) {
                                int cityCode = data.getCities().get(0).getCityCode();
                                sdk.getRadiosByCity(cityCode, 1, new XMDataCallback<XMRadioList>() {
                                    @Override
                                    public void onSuccess(@Nullable XMRadioList data) {
                                        setResult(GsonHelper.toJson(data));
                                    }

                                    @Override
                                    public void onError(int code, String msg) {
                                        setResult("code: " + code + " msg: " + msg);
                                    }
                                });
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getRadioCategory").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getRadioCategory(new XMDataCallback<XMRadioCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable XMRadioCategoryList data) {
                        setResult(GsonHelper.toJson(data));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getRadiosByCategory").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getRadioCategory(new XMDataCallback<XMRadioCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable XMRadioCategoryList data) {
                        sdk.getRadiosByCategory(data.getRadioCategories().get(0).getId(), 1, new XMDataCallback<XMRadioListByCategory>() {
                            @Override
                            public void onSuccess(@Nullable XMRadioListByCategory data) {
                                setResult(GsonHelper.toJson(data));
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("排行榜相关接口 ▼"));

        itemList.add(new Item("getRankList").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getRankList(1, new XMDataCallback<XMRankList>() {
                    @Override
                    public void onSuccess(@Nullable XMRankList data) {
                        setResult(GsonHelper.toJson(data));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getRankAlbumList").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getRankList(1, new XMDataCallback<XMRankList>() {
                    @Override
                    public void onSuccess(@Nullable XMRankList data) {
                        long rankListId = data.getRankList().get(1).getRankListId();
                        sdk.getRankAlbumList(rankListId, 1, new XMDataCallback<XMRankAlbumList>() {
                            @Override
                            public void onSuccess(@Nullable XMRankAlbumList data) {
                                setResult(GsonHelper.toJson(data));
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getRankTrackList").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getRankList(1, new XMDataCallback<XMRankList>() {
                    @Override
                    public void onSuccess(@Nullable XMRankList data) {
                        String rankKey = data.getRankList().get(1).getRankKey();
                        sdk.getRankTrackList(rankKey, 1, new XMDataCallback<XMRankTrackList>() {
                            @Override
                            public void onSuccess(@Nullable XMRankTrackList data) {
                                setResult(GsonHelper.toJson(data));
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getRankRadios").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getRankRadios(10, new XMDataCallback<XMRadioList>() {
                    @Override
                    public void onSuccess(@Nullable XMRadioList data) {
                        setResult(GsonHelper.toJson(data));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("听单数据相关接口 ▼"));

        itemList.add(new Item("getColumnList").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getColumnList(1, new XMDataCallback<XMColumnList>() {
                    @Override
                    public void onSuccess(@Nullable XMColumnList data) {
                        setResult(GsonHelper.toJson(data));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getComlumnDetail").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getColumnList(1, new XMDataCallback<XMColumnList>() {
                    @Override
                    public void onSuccess(@Nullable XMColumnList data) {
                        long columnId = data.getColumns().get(0).getColumnId();
                        sdk.getComlumnDetail(columnId, new XMDataCallback<XMColumnDetail>() {
                            @Override
                            public void onSuccess(@Nullable XMColumnDetail data) {
                                XMColumnDetailAlbum albumList = data.getColumnDetailAlbum();
                                if (albumList != null) {
                                    setResult(GsonHelper.toJson(albumList));
                                    return;
                                }
                                setResult(GsonHelper.toJson(data.getColumnDetailTrack()));
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("搜索相关接口 ▼"));

        itemList.add(new Item("getSearchedAlbums").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getSearchedAlbums("因为爱情来之不易", 0, 4, 1, 20, new XMDataCallback<XMSearchAlbumList>() {
                    @Override
                    public void onSuccess(@Nullable XMSearchAlbumList data) {
                        setResult(GsonHelper.toJson(data));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getSearchedTracks").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getSearchedTracks("爱情", 0, 4, 1, 20, new XMDataCallback<XMSearchTrackList>() {
                    @Override
                    public void onSuccess(@Nullable XMSearchTrackList data) {
                        setResult(GsonHelper.toJson(data));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getSearchedRadios").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getRadioCategory(new XMDataCallback<XMRadioCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable XMRadioCategoryList data) {
                        long radioCategoryId = data.getRadioCategories().get(0).getId();
                        sdk.getSearchedRadios("交通", radioCategoryId, 1, 20, new XMDataCallback<XMRadioList>() {
                            @Override
                            public void onSuccess(@Nullable XMRadioList data) {
                                setResult(GsonHelper.toJson(data));
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getHotWords").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getHotWords(10, new XMDataCallback<XMHotWordList>() {
                    @Override
                    public void onSuccess(@Nullable XMHotWordList data) {
                        setResult(GsonHelper.toJson(data));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getSuggestWord").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getSuggestWord("考试", new XMDataCallback<XMSuggestWords>() {
                    @Override
                    public void onSuccess(@Nullable XMSuggestWords data) {
                        setResult(GsonHelper.toJson(data));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getSearchAnnouncers").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getSearchAnnouncers("儿", 4, 1, 20, new XMDataCallback<XMAnnouncerList>() {
                    @Override
                    public void onSuccess(@Nullable XMAnnouncerList data) {
                        setResult(GsonHelper.toJson(data));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getSearchAll").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getSearchAll("音乐", 1, 20, new XMDataCallback<XMSearchAll>() {
                    @Override
                    public void onSuccess(@Nullable XMSearchAll data) {
                        setResult(GsonHelper.toJson(data));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("搜索相似节目相关 ▼"));

        itemList.add(new Item("getRelativeAlbums").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCategories(new XMDataCallback<XMCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable XMCategoryList data) {
                        XMCategory xmCategory = data.getCategories().get(0);
                        sdk.getAlbumList(xmCategory.getId(), 1, "", 1, new XMDataCallback<XMAlbumList>() {
                            @Override
                            public void onSuccess(@Nullable XMAlbumList data) {
                                long albumId = data.getAlbums().get(0).getId();
                                sdk.getRelativeAlbums(albumId, new XMDataCallback<XMRelativeAlbums>() {
                                    @Override
                                    public void onSuccess(@Nullable XMRelativeAlbums data) {
                                        setResult(GsonHelper.toJson(data));
                                    }

                                    @Override
                                    public void onError(int code, String msg) {
                                        setResult("code: " + code + " msg: " + msg);
                                    }
                                });
                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getRelativeAlbumsUseTrackId").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getCategories(new XMDataCallback<XMCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable XMCategoryList data) {
                        XMCategory xmCategory = data.getCategories().get(0);
                        sdk.getAlbumList(xmCategory.getId(), 1, "", 1, new XMDataCallback<XMAlbumList>() {
                            @Override
                            public void onSuccess(@Nullable XMAlbumList data) {
                                long albumId = data.getAlbums().get(0).getId();
                                sdk.getTracks(albumId, "asc", 1, new AbsXMDataCallback<XMTrackList>() {
                                    @Override
                                    public void onSuccess(@Nullable XMTrackList data) {
                                        long trackId = data.getTracks().get(0).getDataId();
                                        sdk.getRelativeAlbumsUseTrackId(trackId, new XMDataCallback<XMRelativeAlbums>() {
                                            @Override
                                            public void onSuccess(@Nullable XMRelativeAlbums data) {
                                                setResult(GsonHelper.toJson(data));
                                            }

                                            @Override
                                            public void onError(int code, String msg) {
                                                setResult("code: " + code + " msg: " + msg);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int code, String msg) {
                                        setResult("code: " + code + " msg: " + msg);
                                    }
                                });

                            }

                            @Override
                            public void onError(int code, String msg) {
                                setResult("code: " + code + " msg: " + msg);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));

        itemList.add(new Item("getGuessLikeAlbum").setTask(new Task() {
            @Override
            protected void run() {
                sdk.getGuessLikeAlbum(10, new XMDataCallback<XMGussLikeAlbumList>() {
                    @Override
                    public void onSuccess(@Nullable XMGussLikeAlbumList data) {
                        setResult(GsonHelper.toJson(data));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        setResult("code: " + code + " msg: " + msg);
                    }
                });
            }
        }));
    }
}
