package com.xiaoma.xting.koala;

import android.support.annotation.Nullable;
import android.util.Log;

import com.kaolafm.opensdk.api.BasePageResult;
import com.kaolafm.opensdk.api.broadcast.BroadcastDetails;
import com.kaolafm.opensdk.api.broadcast.BroadcastRequest;
import com.kaolafm.opensdk.api.broadcast.ProgramDetails;
import com.kaolafm.opensdk.api.live.LiveRequest;
import com.kaolafm.opensdk.api.live.model.LiveInfoDetail;
import com.kaolafm.opensdk.api.media.AlbumRequest;
import com.kaolafm.opensdk.api.media.AudioRequest;
import com.kaolafm.opensdk.api.media.RadioRequest;
import com.kaolafm.opensdk.api.media.model.AlbumDetails;
import com.kaolafm.opensdk.api.media.model.AudioDetails;
import com.kaolafm.opensdk.api.media.model.RadioDetails;
import com.kaolafm.opensdk.api.operation.OperationRequest;
import com.kaolafm.opensdk.api.operation.model.category.Category;
import com.kaolafm.opensdk.api.operation.model.category.CategoryMember;
import com.kaolafm.opensdk.api.operation.model.category.LeafCategory;
import com.kaolafm.opensdk.api.operation.model.column.Column;
import com.kaolafm.opensdk.api.operation.model.column.ColumnGrp;
import com.kaolafm.opensdk.api.operation.model.column.ColumnMember;
import com.kaolafm.opensdk.api.operation.model.column.RadioDetailColumnMember;
import com.kaolafm.opensdk.api.search.SearchRequest;
import com.kaolafm.opensdk.api.search.model.SearchProgramBean;
import com.kaolafm.opensdk.api.search.model.VoiceSearchResult;
import com.kaolafm.opensdk.http.core.HttpCallback;
import com.kaolafm.opensdk.http.error.ApiException;
import com.kaolafm.sdk.core.mediaplayer.OnPlayItemInfoListener;
import com.kaolafm.sdk.core.mediaplayer.PlayItem;
import com.kaolafm.sdk.core.mediaplayer.PlayerRadioListManager;
import com.xiaoma.xting.common.playerSource.utils.PrintInfo;
import com.xiaoma.xting.koala.bean.XMAlbumDetails;
import com.xiaoma.xting.koala.bean.XMAudioDetails;
import com.xiaoma.xting.koala.bean.XMBroadcastDetails;
import com.xiaoma.xting.koala.bean.XMCategory;
import com.xiaoma.xting.koala.bean.XMColumnMember;
import com.xiaoma.xting.koala.bean.XMKoalaColumn;
import com.xiaoma.xting.koala.bean.XMLeafCategory;
import com.xiaoma.xting.koala.bean.XMListPageAudioDetails;
import com.xiaoma.xting.koala.bean.XMListPageCategoryMemberList;
import com.xiaoma.xting.koala.bean.XMLiveInfoDetails;
import com.xiaoma.xting.koala.bean.XMProgramDetails;
import com.xiaoma.xting.koala.bean.XMRadioDetailColumnMember;
import com.xiaoma.xting.koala.bean.XMRadioDetails;
import com.xiaoma.xting.koala.bean.XMSearchProgramBean;
import com.xiaoma.xting.koala.bean.XMVoiceSearchResult;
import com.xiaoma.xting.koala.callback.KoalaHttpCallbackImpl;
import com.xiaoma.xting.koala.callback.KoalaListHttpCallbackImpl;
import com.xiaoma.xting.koala.contract.KoalaContentType;
import com.xiaoma.xting.sdk.bean.AbsXMDataCallback;
import com.xiaoma.xting.sdk.bean.XMDataCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <des>
 * koala FM ??????????????????
 *
 * @author YangGang
 * @date 2019/6/3
 */
public class KoalaRequest {

    private static final String TAG = KoalaRequest.class.getSimpleName();
    private static int retryCount;

    /**************??????***************/

    /**
     * ????????????????????????????????????
     *
     * @param contentType [Must] ?????????????????????????????????ResType?????????
     *                    ??????-ResType.TYPE_ALL????????????????????????;
     *                    ??????-ResType.TYPE_ALBUM
     *                    ??????-ResType.TYPE_BROADCAST
     *                    ??????-ResType.TYPE_LIVE
     *                    PGC-ResType.TYPE_RADIO
     *                    QQ??????-ResType.TYPE_QQ_MUSIC
     * @param zone        ???????????????
     * @param extraInfo   ???????????????????????????
     * @param callback    ????????????
     */
    public static void getCategoryTree(@KoalaContentType int contentType, String zone, Map<String, String> extraInfo, XMDataCallback<List<XMCategory>> callback) {
        new OperationRequest().getCategoryTree(contentType, zone, extraInfo, new KoalaListHttpCallbackImpl<XMCategory, Category>(callback) {
            @Override
            protected XMCategory handleConverter(Category category) {
                return new XMCategory(category);
            }
        });
    }

    public static void getCategoryTree(@KoalaContentType int contentType, String zone, XMDataCallback<List<XMCategory>> callback) {
        getCategoryTree(contentType, zone, null, callback);
    }

    /**
     * ????????????????????????????????????
     *
     * @param contentType
     * @param zone
     * @param callback
     */
    public static void getCategoryRoot(@KoalaContentType int contentType, String zone, Map<String, String> extraInfo, XMDataCallback<List<XMCategory>> callback) {
        new OperationRequest().getCategoryRoot(contentType, zone, extraInfo, new KoalaListHttpCallbackImpl<XMCategory, Category>(callback) {
            @Override
            protected XMCategory handleConverter(Category category) {
                return new XMCategory(category);
            }
        });
    }

    public static void getCategoryRoot(@KoalaContentType int contentType, String zone, XMDataCallback<List<XMCategory>> callback) {
        getCategoryRoot(contentType, zone, null, callback);
    }

    /**
     * ????????????????????????????????????
     *
     * @param parentCode ????????????????????????code
     * @param callback
     */
    public static void getSubcategoryList(String parentCode, XMDataCallback<List<XMLeafCategory>> callback) {
        new OperationRequest().getSubcategoryList(parentCode, new KoalaListHttpCallbackImpl<XMLeafCategory, LeafCategory>(callback) {
            @Override
            protected XMLeafCategory handleConverter(LeafCategory leafCategory) {
                return new XMLeafCategory(leafCategory);
            }
        });
    }

    /**
     * ????????????????????????
     *
     * @param code     ?????????code
     * @param pageNum  ???????????? 1???2???3???...
     * @param pageSize ????????????
     * @param callback ????????????
     */
    public static void getCategoryMemberList(String code, int pageNum, int pageSize, AbsXMDataCallback<XMListPageCategoryMemberList> callback) {
        new OperationRequest().getCategoryMemberList(code, pageNum, pageSize, new KoalaHttpCallbackImpl<XMListPageCategoryMemberList, BasePageResult<List<CategoryMember>>>(callback) {
            @Override
            protected XMListPageCategoryMemberList handleConverter(BasePageResult<List<CategoryMember>> listBasePageResult) {
                return new XMListPageCategoryMemberList(listBasePageResult);
            }
        });
    }

    /**
     * ????????????????????????
     *
     * @param code     ?????????code
     * @param callback ????????????
     */
    public static void getCategoryMemberNum(String code, XMDataCallback<Integer> callback) {
        new OperationRequest().getCategoryMemberNum(code, new HttpCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                if (callback != null) {
                    callback.onSuccess(integer);
                }
            }

            @Override
            public void onError(ApiException e) {
                if (callback != null) {
                    callback.onError(e.getCode(), e.getMessage());
                }
            }
        });
    }

    /**
     * ?????????????????????
     *
     * @param isWithMembers [Must] ???????????????(false??????????????????)??????????????????????????????
     * @param zone          ????????????
     * @param callback
     */
    public static void getColumnTree(boolean isWithMembers, String zone, XMDataCallback<List<XMColumnMember>> callback) {
        new OperationRequest().getColumnTree(isWithMembers, zone, new HttpCallback<List<ColumnGrp>>() {
            @Override
            public void onSuccess(List<ColumnGrp> columnGrps) {
                if (columnGrps == null || columnGrps.isEmpty()) {
                    callback.onSuccess(null);
                } else {
                    ColumnGrp columnGrp = columnGrps.get(0);
                    if (columnGrp != null && columnGrp instanceof Column) {
                        Column column = (Column) columnGrp;
                        List<? extends ColumnMember> childColumns = column.getColumnMembers();
                        if (childColumns == null || childColumns.isEmpty()) {
                            callback.onSuccess(null);
                        } else {
                            List<XMColumnMember> columnList = new ArrayList<>(childColumns.size());
                            for (ColumnMember member : childColumns) {
                                if (member instanceof RadioDetailColumnMember) {
                                    columnList.add(new XMRadioDetailColumnMember((RadioDetailColumnMember) member));
                                }
                            }
                            callback.onSuccess(columnList);
                        }

                    } else {
                        callback.onSuccess(null);
                    }

                }
            }

            @Override
            public void onError(ApiException e) {
                if (!KoalaFactory.getSDK().isActivate()) {
                    KoalaFactory.getSDK().reActivate(new HttpCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            getColumnTree(isWithMembers, zone, callback);
                        }

                        @Override
                        public void onError(ApiException e) {
                            callback.onError(e.getCode(),e.getMessage());
                        }
                    });
                }else {
                    callback.onError(e.getCode(),e.getMessage());
                }
            }
        });
    }

    /**
     * ?????????????????????
     *
     * @param parentCode
     * @param zone
     * @param callback
     */
    public static void getSubcolumnList(String parentCode, String zone, XMDataCallback<List<XMKoalaColumn>> callback) {
        new OperationRequest().getSubcolumnList(parentCode, zone, new KoalaListHttpCallbackImpl<XMKoalaColumn, Column>(callback) {
            @Override
            protected XMKoalaColumn handleConverter(Column column) {
                return new XMKoalaColumn(column);
            }
        });
    }

    /**
     * ????????????????????????
     *
     * @param code     ????????????
     * @param callback
     */
    public static void getColumnMemberList(String code, XMDataCallback<List<XMColumnMember>> callback) {
        new OperationRequest().getColumnMemberList(code, new HttpCallback<List<ColumnMember>>() {
            @Override
            public void onSuccess(List<ColumnMember> columnMembers) {
                if (columnMembers != null && !columnMembers.isEmpty()) {
                    List<XMColumnMember> columnMemberList = new ArrayList<>(columnMembers.size());
                    for (ColumnMember columnMember : columnMembers) {
                        columnMemberList.add(new XMColumnMember(columnMember));
                    }
                    if (callback != null) {
                        callback.onSuccess(columnMemberList);
                    }
                }
            }

            @Override
            public void onError(ApiException e) {

            }
        });
    }

    /**************??????***************/

    /**
     * ??????????????????
     *
     * @param albumId  ??????Id
     * @param callback ??????
     */
    public static void getAlbumDetails(long albumId, AbsXMDataCallback<XMAlbumDetails> callback) {
        new AlbumRequest().getAlbumDetails(albumId, new KoalaHttpCallbackImpl<XMAlbumDetails, AlbumDetails>(callback) {
            @Override
            protected XMAlbumDetails handleConverter(AlbumDetails albumDetails) {
                return new XMAlbumDetails(albumDetails);
            }
        });
    }

    public static void getAlbumDetails(Long[] albumIds, XMDataCallback<List<XMAlbumDetails>> callback) {
        new AlbumRequest().getAlbumDetails(albumIds, new KoalaListHttpCallbackImpl<XMAlbumDetails, AlbumDetails>(callback) {

            @Override
            protected XMAlbumDetails handleConverter(AlbumDetails albumDetails) {
                return new XMAlbumDetails(albumDetails);
            }
        });
    }

    /**
     * ???????????????????????????
     *
     * @param albumId   ??????id
     * @param audioId   ??????id???????????????id????????????
     * @param sortByAcs true ?????? false ??????
     * @param pageSize  ??????????????????????????????10
     * @param pageNum   ???????????? 1, 2, 3...????????????1
     * @param callback  ????????????
     */
    public static void getPlaylist(long albumId, long audioId, boolean sortByAcs, int pageSize, int pageNum, AbsXMDataCallback<XMListPageAudioDetails> callback) {
        try {
            new AlbumRequest().getPlaylist(albumId, audioId, sortByAcs ? AlbumRequest.SORT_ACS : AlbumRequest.SORT_DESC, pageSize, pageNum, new KoalaHttpCallbackImpl<XMListPageAudioDetails, BasePageResult<List<AudioDetails>>>(callback) {

                @Override
                protected long getAlbumId() {
                    return albumId;
                }

                @Override
                protected XMListPageAudioDetails handleConverter(BasePageResult<List<AudioDetails>> listBasePageResult) {
                    return new XMListPageAudioDetails(listBasePageResult);
                }
            });
        } catch (Exception e) {
            //?????????????????????koala ???????????????,??????crash
            PrintInfo.print(TAG, "getPlaylist Exception", e.getMessage());
            if (callback != null) {
                callback.onError(-1, e.getMessage());
            }
        }
    }

    public static void getPlayList(long albumId, boolean sortByAcs, int pageSize, int page, AbsXMDataCallback<XMListPageAudioDetails> callback) {
        new AlbumRequest().getPlaylist(albumId, sortByAcs ? AlbumRequest.SORT_ACS : AlbumRequest.SORT_DESC, pageSize, page, new KoalaHttpCallbackImpl<XMListPageAudioDetails, BasePageResult<List<AudioDetails>>>(callback) {
            @Override
            protected long getAlbumId() {
                return albumId;
            }

            @Override
            protected XMListPageAudioDetails handleConverter(BasePageResult<List<AudioDetails>> listBasePageResult) {
                return new XMListPageAudioDetails(listBasePageResult);
            }
        });
    }

    /**************????????????***************/

    /**
     * ??????PGC??????
     *
     * @param radioId  ????????????Id
     * @param callback
     */
    public static void getRadioDetails(long radioId, AbsXMDataCallback<XMRadioDetails> callback) {
        new RadioRequest().getRadioDetails(radioId, new KoalaHttpCallbackImpl<XMRadioDetails, RadioDetails>(callback) {
            @Override
            protected long getAlbumId() {
                return radioId;
            }

            @Override
            protected XMRadioDetails handleConverter(RadioDetails radioDetails) {
                return new XMRadioDetails(radioDetails);
            }
        });
    }

    /**
     * ??????????????????id??????????????????
     *
     * @param radioId  ????????????id
     * @param clockId  ????????????????????????clockId????????????????????????????????????????????????
     * @param callback ????????????
     */
    public static void getPlaylist(long radioId, String clockId, XMDataCallback<List<XMAudioDetails>> callback) {
        new RadioRequest().getPlaylist(radioId, (clockId == null) ? "" : clockId, new HttpCallback<List<AudioDetails>>() {
            @Override
            public void onSuccess(List<AudioDetails> audioDetails) {
                if (callback == null) {
                    return;
                }
                if (audioDetails == null || audioDetails.isEmpty()) {
                    callback.onSuccess(null);
                } else {
                    List<XMAudioDetails> audioList = new ArrayList<>(audioDetails.size());
                    for (AudioDetails bean : audioDetails) {
                        audioList.add(new XMAudioDetails(bean));
                    }
                    callback.onSuccess(audioList);
                }
            }

            @Override
            public void onError(ApiException e) {
                if (callback != null) {
                    callback.onError(e.getCode(), e.getMessage());
                }
            }
        });
    }

    /**
     * ??????????????????????????????????????????(?????????)????????????????????????????????????
     *
     * @param radioId  ????????????id
     * @param clockId  ????????????????????????clockId????????????????????????????????????????????????
     * @param areaCode ????????????
     * @param cityName ????????????
     * @param callback ????????????
     */
    public static void getPlaylist(long radioId, String clockId, String areaCode, String cityName, XMDataCallback<List<XMAudioDetails>> callback) {
        new RadioRequest().getPlaylist(radioId, clockId, areaCode, cityName, new KoalaListHttpCallbackImpl<XMAudioDetails, AudioDetails>(callback) {
            @Override
            protected XMAudioDetails handleConverter(AudioDetails audioDetails) {
                return new XMAudioDetails(audioDetails);
            }
        });
    }

    /**************??????***************/
    /**
     * //????????????????????????
     *
     * @param audioId  ??????id
     * @param callback ????????????
     */
    public static void getAudioDetails(long audioId, AbsXMDataCallback<XMAudioDetails> callback) {
        new AudioRequest().getAudioDetails(audioId, new KoalaHttpCallbackImpl<XMAudioDetails, AudioDetails>(callback) {

            @Override
            protected long getAlbumId() {
                return audioId;
            }

            @Override
            protected XMAudioDetails handleConverter(AudioDetails audioDetails) {
                return new XMAudioDetails(audioDetails);
            }
        });
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param callback
     */
    public static void getCurrentClockAudio(AbsXMDataCallback<XMAudioDetails> callback) {
        new AudioRequest().getCurrentClockAudio(new KoalaHttpCallbackImpl<XMAudioDetails, AudioDetails>(callback) {
            @Override
            protected XMAudioDetails handleConverter(AudioDetails audioDetails) {
                return new XMAudioDetails(audioDetails);
            }
        });
    }

    /**************??????***************/

    /**
     * ????????????id???????????????????????????
     *
     * @param broadcastId ???????????????id
     * @param callback
     */
    public static void getBroadcastDetails(long broadcastId, AbsXMDataCallback<XMBroadcastDetails> callback) {
        new BroadcastRequest().getBroadcastDetails(broadcastId, new KoalaHttpCallbackImpl<XMBroadcastDetails, BroadcastDetails>(callback) {

            @Override
            protected long getAlbumId() {
                return broadcastId;
            }

            @Override
            protected XMBroadcastDetails handleConverter(BroadcastDetails broadcastDetails) {
                return new XMBroadcastDetails(broadcastDetails);
            }
        });
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param broadcastId ????????????id
     * @param date        ????????????????????????????????????????????????2018-07-26???
     * @param callback    ????????????
     */
    public static void getBroadcastProgramList(long broadcastId, @Nullable String date, XMDataCallback<List<XMProgramDetails>> callback) {
        new BroadcastRequest().getBroadcastProgramList(broadcastId, date, new KoalaListHttpCallbackImpl<XMProgramDetails, ProgramDetails>(callback) {
            @Override
            protected XMProgramDetails handleConverter(ProgramDetails programDetails) {
                return new XMProgramDetails(programDetails);
            }
        });
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param programId ????????????Id
     * @param callback
     */
    public static void getBroadcastProgramDetails(long programId, AbsXMDataCallback<XMProgramDetails> callback) {
        new BroadcastRequest().getBroadcastProgramDetails(programId, new KoalaHttpCallbackImpl<XMProgramDetails, ProgramDetails>(callback) {

            @Override
            protected XMProgramDetails handleConverter(ProgramDetails programDetails) {
                return new XMProgramDetails(programDetails);
            }
        });
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param programId ????????????Id
     * @param callback
     */
    public static void getBroadcastCurrentProgramDetails(long programId, AbsXMDataCallback<XMProgramDetails> callback) {
        new BroadcastRequest().getBroadcastCurrentProgramDetails(programId, new KoalaHttpCallbackImpl<XMProgramDetails, ProgramDetails>(callback) {
            @Override
            protected XMProgramDetails handleConverter(ProgramDetails programDetails) {
                return new XMProgramDetails(programDetails);
            }
        });
    }

    /**************??????***************/
    /**
     * ????????????ID????????????????????????
     *
     * @param id       ??????id
     * @param callback
     */
    public static void getLiveInfo(String id, AbsXMDataCallback<XMLiveInfoDetails> callback) {
        new LiveRequest().getLiveInfo(id, new KoalaHttpCallbackImpl<XMLiveInfoDetails, LiveInfoDetail>(callback) {
            @Override
            protected XMLiveInfoDetails handleConverter(LiveInfoDetail liveInfoDetail) {
                return new XMLiveInfoDetails(liveInfoDetail);
            }
        });
    }

    /**************??????***************/

    /**
     * ???????????????????????????????????????json?????????????????????????????????????????????
     *
     * @param voiceSource [Must] ???????????? ????????????,?????????:txzing;?????????:sibichi;??????:wenwen;??????:moran;?????? ??????:kedaxunfei;
     * @param qualityType ??????????????????,0:???;1:???;
     * @param origJson    ?????????????????????????????????json???
     * @param field       [Must] ???????????? 1????????????2?????????; 6: ????????????
     * @param tag         ?????????????????? 0???????????????1???????????????1???????????????????????????????????????????????????????????????
     * @param artist      ?????????
     * @param audioName   ????????????
     * @param albumName   ????????????
     * @param category    ??????
     * @param keyword     [Must] ????????? ?????????????????????????????????,?????????
     * @param text        [Must] ????????????????????????
     * @param language    [Temp UnSupport] ?????????????????????
     * @param freq        [Temp UnSupport]?????????????????????????????????????????????????????????
     * @param area        [Temp UnSupport]??????text?????????????????????????????????????????????????????????
     * @param callback    ????????????
     */
    public static void searchBySemantics(String voiceSource, int qualityType, String origJson,
                                         int field, int tag, String artist,
                                         String audioName, String albumName, String category,
                                         String keyword, String text, String language,
                                         String freq, String area, AbsXMDataCallback<XMVoiceSearchResult> callback) {
        new SearchRequest().searchBySemantics(voiceSource, qualityType, origJson, field, tag, artist,
                audioName, albumName, category, keyword, text, language, freq, area,
                new KoalaHttpCallbackImpl<XMVoiceSearchResult, VoiceSearchResult>(callback) {
                    @Override
                    protected XMVoiceSearchResult handleConverter(VoiceSearchResult voiceSearchResult) {
                        return new XMVoiceSearchResult(voiceSearchResult);
                    }
                });
    }

    public static void searchBySemanticsSimple(String voiceSource, int qualityType, String origJson,
                                               int field, int tag, String artist,
                                               String audioName, String albumName, String category,
                                               String keyword, String text, AbsXMDataCallback<XMVoiceSearchResult> callback) {
        searchBySemantics(voiceSource, qualityType, origJson, field, tag, artist, audioName, albumName, category, keyword, text, null, null, null, callback);
    }

    /**
     * ?????????????????????????????????
     *
     * @param keyWord  ?????????
     * @param callback
     */
    public static void searchAll(String keyWord, AbsXMDataCallback<List<XMSearchProgramBean>> callback) {
        new SearchRequest().searchAll(keyWord, new KoalaListHttpCallbackImpl<XMSearchProgramBean, SearchProgramBean>(callback) {
            @Override
            protected XMSearchProgramBean handleConverter(SearchProgramBean searchProgramBean) {
                return new XMSearchProgramBean(searchProgramBean);
            }
        });
    }

    /**
     * ?????????????????????????????????
     *
     * @param word     ?????????
     * @param callback
     */
    public static void getSuggestedWords(String word, XMDataCallback<List<String>> callback) {
        new SearchRequest().getSuggestedWords(word, new HttpCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> strings) {
                if (callback != null) {
                    callback.onSuccess(strings);
                }
            }

            @Override
            public void onError(ApiException e) {
                if (callback != null) {
                    callback.onError(e.getCode(), e.getMessage());
                }
            }
        });
    }

    public static void getPlayList() {
        PlayerRadioListManager.getInstance().fetchMorePlaylist(new OnPlayItemInfoListener() {
            @Override
            public void onPlayItemReady(PlayItem playItem) {
                Log.d("Jir", "[ onPlayItemReady ] at " + Log.getStackTraceString(new Throwable())
                        + "\n * " + playItem);
            }

            @Override
            public void onPlayItemUnavailable() {

            }

            @Override
            public void onPlayItemReady(List<PlayItem> list) {

            }
        });
    }

}
