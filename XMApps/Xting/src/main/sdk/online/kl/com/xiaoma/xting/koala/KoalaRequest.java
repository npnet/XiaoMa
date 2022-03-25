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
 * koala FM 请求接口封装
 *
 * @author YangGang
 * @date 2019/6/3
 */
public class KoalaRequest {

    private static final String TAG = KoalaRequest.class.getSimpleName();
    private static int retryCount;

    /**************运营***************/

    /**
     * 获取指定类型的整颗分类树
     *
     * @param contentType [Must] 要获取分类树的内容类型ResType，包括
     *                    综合-ResType.TYPE_ALL，等同于全部类型;
     *                    专辑-ResType.TYPE_ALBUM
     *                    广播-ResType.TYPE_BROADCAST
     *                    直播-ResType.TYPE_LIVE
     *                    PGC-ResType.TYPE_RADIO
     *                    QQ音乐-ResType.TYPE_QQ_MUSIC
     * @param zone        区域信息。
     * @param extraInfo   额外信息，暂时无用
     * @param callback    结果回调
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
     * 获取指定类型的根分类列表
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
     * 获取指定类型的子分类列表
     *
     * @param parentCode 需要获取子分类的code
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
     * 获取分类成员列表
     *
     * @param code     分类的code
     * @param pageNum  请求页码 1，2，3，...
     * @param pageSize 每页个数
     * @param callback 结果回调
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
     * 获取分类成员数量
     *
     * @param code     分类的code
     * @param callback 结果回调
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
     * 获取整颗栏目树
     *
     * @param isWithMembers [Must] 是否要成员(false表示不要成员)，不填表示不获取成员
     * @param zone          区域信息
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
     * 获取子栏目列表
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
     * 获取栏目成员列表
     *
     * @param code     栏目编码
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

    /**************专辑***************/

    /**
     * 获取专辑详情
     *
     * @param albumId  专辑Id
     * @param callback 回调
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
     * 获取专辑的播单列表
     *
     * @param albumId   专辑id
     * @param audioId   碎片id，根据碎片id定位分页
     * @param sortByAcs true 正序 false 倒序
     * @param pageSize  每页专辑个数，默认是10
     * @param pageNum   请求页码 1, 2, 3...，默认是1
     * @param callback  结果回调
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
            //考虑到可能出现koala 初始化失败,避免crash
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

    /**************智能电台***************/

    /**
     * 获取PGC详情
     *
     * @param radioId  智能电台Id
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
     * 根据智能电台id获取播单列表
     *
     * @param radioId  智能电台id
     * @param clockId  上一次请求返回的clockId，第一次请求为空，第二次请求必填
     * @param callback 结果回调
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
     * 根据城市的名称或城市地区编码(见附件)，获取智能电台的播单列表
     *
     * @param radioId  智能电台id
     * @param clockId  上一次请求返回的clockId，第一次请求为空，第二次请求必填
     * @param areaCode 城市编码
     * @param cityName 城市名称
     * @param callback 结果回调
     */
    public static void getPlaylist(long radioId, String clockId, String areaCode, String cityName, XMDataCallback<List<XMAudioDetails>> callback) {
        new RadioRequest().getPlaylist(radioId, clockId, areaCode, cityName, new KoalaListHttpCallbackImpl<XMAudioDetails, AudioDetails>(callback) {
            @Override
            protected XMAudioDetails handleConverter(AudioDetails audioDetails) {
                return new XMAudioDetails(audioDetails);
            }
        });
    }

    /**************碎片***************/
    /**
     * //获取单个碎片详情
     *
     * @param audioId  碎片id
     * @param callback 结果回调
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
     * 获取当前时间点的报时声音碎片
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

    /**************广播***************/

    /**
     * 根据广播id获取广播的信息详情
     *
     * @param broadcastId 广播电台的id
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
     * 获取指定广播电台的指定日期的节目单列表
     *
     * @param broadcastId 广播电台id
     * @param date        节目单的日期，默认是当天。格式“2018-07-26”
     * @param callback    结果回调
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
     * 获取广播电台的某个节目的信息详情。
     *
     * @param programId 广播节目Id
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
     * 获取某个广播电台正在播放的节目信息
     *
     * @param programId 广播电台Id
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

    /**************直播***************/
    /**
     * 根据节目ID获取直播具体信息
     *
     * @param id       直播id
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

    /**************搜索***************/

    /**
     * 根据语音商和语音上解析后的json串可以获取不同类型的音频资源。
     *
     * @param voiceSource [Must] 语音来源 公司标识,同行者:txzing;思必驰:sibichi;问问:wenwen;蓦然:moran;科大 讯飞:kedaxunfei;
     * @param qualityType 音频质量要求,0:低;1:高;
     * @param origJson    语音商解析后返回的原始json串
     * @param field       [Must] 场景类别 1：音乐，2：综合; 6: 传统广播
     * @param tag         参数可信标识 0：不可信，1：可信；为1时，表示场景分类和其他字段等信息可信度高。
     * @param artist      艺术家
     * @param audioName   音频名称
     * @param albumName   专辑名称
     * @param category    分类
     * @param keyword     [Must] 关键词 多个关键词以英文逗号“,”分隔
     * @param text        [Must] 用户声控的原始串
     * @param language    [Temp UnSupport] 语言，暂不支持
     * @param freq        [Temp UnSupport]电台频率，传统广播场景下使用，暂不支持
     * @param area        [Temp UnSupport]搜索text中的地点，传统广播场景下使用，暂不支持
     * @param callback    结果回调
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
     * 根据关键词搜索所有资源
     *
     * @param keyWord  关键词
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
     * 根据给定的词获取联想词
     *
     * @param word     关键词
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
