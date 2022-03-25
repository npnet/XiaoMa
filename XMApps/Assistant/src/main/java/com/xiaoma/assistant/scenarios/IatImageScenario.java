package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.IChooseCallback;
import com.xiaoma.assistant.model.ImageBean;
import com.xiaoma.assistant.model.StksCmd;
import com.xiaoma.assistant.model.StksCmdDimension;
import com.xiaoma.assistant.model.StksCmdNliScene;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.ui.adapter.ImageListAdapter;
import com.xiaoma.assistant.utils.Constants;
import com.xiaoma.assistant.view.ImageDetailView;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：图片场景
 */
public class IatImageScenario extends IatScenario implements BaseMultiPageAdapter.OnMultiPageItemClickListener {


    private ImageListAdapter adapter;
    private ImageBean imageBean;

    public IatImageScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        String answer;
        String images;
        if (StringUtil.isNotEmpty(parseResult.getSlots())) {
            try {
                JSONObject jsonObject = new JSONObject(parseResult.getSlots());
                if (jsonObject.has("data")) {
                    try {
//                        images = jsonObject.getString("images");
                        String data = jsonObject.getString("data");
                        if (!TextUtils.isEmpty(data)) {
                            imageBean = GsonHelper.fromJson(data, ImageBean.class);
                            adapter = new ImageListAdapter(context, imageBean.getImages());
                            adapter.setOnMultiPageItemClickListener(IatImageScenario.this);
                            showMultiPageData(adapter, context.getString(R.string.search_result_tips));
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        answer = getString(R.string.not_found_image);
                    }
                }
                answer = parseResult.getAnswer();
            } catch (JSONException e) {
                e.printStackTrace();
                answer = getString(R.string.not_found_image);
            }
        }
        //doResult(images, answer, Constants.VoiceParserType.ACTION_SEARCH_IMAGE, "QUERY", "picture", callback);
        answer = getString(R.string.not_found_image);
        speakContent(answer);
    }

    @Override
    public void onChoose(String voiceText) {
        final boolean firstPage = assistantManager.getMultiPageView().isFirstPage();
        final boolean lastPage = assistantManager.getMultiPageView().isLastPage();
        final boolean isDetailPage = assistantManager.getMultiPageView().isDetailPage();
        final ImageDetailView detailView = (ImageDetailView) assistantManager.getMultiPageView().getDetailView();
        switchChooseAction(voiceText, new IChooseCallback() {
            @Override
            public void previousPageAction() {
                if (isDetailPage) {
                    KLog.d("Detail page's pre page");
                    detailView.changePage(-1);
                } else {
                    if (firstPage) {
                        KLog.d("It is first page");
                    } else {
                        assistantManager.getMultiPageView().setPage(-1);
                    }
                }
            }

            @Override
            public void nextPageAction() {
                if (isDetailPage) {
                    KLog.d("Detail page's last page");
                    detailView.changePage(1);
                } else {
                    if (lastPage) {
                        KLog.d("It is last page");
                    } else {
                        assistantManager.getMultiPageView().setPage(1);
                    }
                }
            }

            @Override
            public void chooseItemAction(int position) {
                if (isDetailPage) {
                    detailView.setPage(position);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.BundleKey.POSITION, position);
                    bundle.putSerializable(Constants.BundleKey.LIST, (Serializable) adapter.getCurrentList());
                    assistantManager.getMultiPageView().setDetailPage(Constants.MultiplePageDetailType.IMAGE_DEATAIL, bundle);
                }
            }

            @Override
            public void lastAction() {

            }

            @Override
            public void cancelChooseAction() {
                if (isDetailPage) {
                    assistantManager.getMultiPageView().hideDetailPage();
                    assistantManager.startListeningForChoose(getSrSceneStksCmd());
                } else {
//                    stopListening();
//                    assistantManager.hideMultiPageView();
//                    startListening();
                    assistantManager.closeAssistant();
                }
            }

            @Override
            public void errorChooseActon() {

            }

            @Override
            public void assignPageAction(int page) {
                assistantManager.getMultiPageView().setPageForIndex(page);
            }
        });
    }

    @Override
    public boolean isIntercept() {
        return false;
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onItemClick(int position) {
        adapter.setSelectPosition(position);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKey.POSITION, position);
        bundle.putSerializable(Constants.BundleKey.LIST, (Serializable) adapter.getCurrentList());
        assistantManager.getMultiPageView().setDetailPage(Constants.MultiplePageDetailType.IMAGE_DEATAIL, bundle);
    }

    @Override
    protected String getSrSceneStksCmd() {
        if (assistantManager == null) {
            return "";
        }
        if (assistantManager.getMultiPageView() == null) {
            return "";
        }
        if (adapter == null) {
            return "";
        }
        if (!assistantManager.getMultiPageView().isDetailPage()) {
            List<ImageBean.ImagesBean> data = adapter.getCurrentList();
            if (ListUtils.isEmpty(data)) {
                return "";
            }
            StksCmd stksCmd = new StksCmd();
            stksCmd.setType("image");
            stksCmd.setNliScene("image");
            ArrayList<String> search = new ArrayList<>();
            search.add("semantic.slots.item");
            search.add("semantic.slots.default");
            stksCmd.setNliFieldSearch(search);
            ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
            int size = data.size();
            for (int i = 0; i < size; i++) {
                StksCmdNliScene stksCmdNliScene = new StksCmdNliScene();
                stksCmdNliScene.setId(i + 1);
                ArrayList<StksCmdDimension> dimensions = new ArrayList<>();
                addDefaultCmdByNumber(i, size, dimensions);
                if (!ListUtils.isEmpty(dimensions)) stksCmdNliScene.setDimension(dimensions);
                stksCmdNliScenes.add(stksCmdNliScene);
            }
            stksCmdNliScenes.addAll(getDefaultCmd(size));
            stksCmd.setList(stksCmdNliScenes);
            String result = GsonHelper.toJson(stksCmd);
            KLog.json(result);
            return result;
        }
        return "";
    }
}
