package com.xiaoma.assistant.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.HelpBean;
import com.xiaoma.assistant.ui.adapter.HelpAdapter;
import com.xiaoma.assistant.ui.adapter.HelpDetailAdapter;
import com.xiaoma.utils.AssetUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/9
 * Desc：语音助手帮助的view
 */
public class HelpView extends RelativeLayout {

    private RecyclerView rlvContent;
    private RelativeLayout rlDetail;
    private TextView tvTitle;
    private RecyclerView rlvDetail;
    private List<HelpBean> helpBeanList;
    private HelpAdapter helpAdapter;
    private HelpDetailAdapter detailAdapter;
    private boolean mDetailType = false;


    public HelpView(Context context) {
        super(context);
        initView();
    }

    public HelpView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HelpView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        inflate(getContext(), R.layout.view_assistant_help, this);
        rlvContent = findViewById(R.id.rlv_assistant_help);
        rlDetail = findViewById(R.id.fl_help_page_detail);
        rlvDetail = findViewById(R.id.rlv_assistant_help_detail);
        tvTitle = findViewById(R.id.tv_help_page_title);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rlvContent.setLayoutManager(manager);
        rlvContent.addItemDecoration(new HorizontalItemDecoration());
        GridLayoutManager manager1 = new GridLayoutManager(getContext(), 2);
        rlvDetail.setLayoutManager(manager1);
        rlvDetail.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position % 2 == 0) {
                    if (position < 2) {
                        outRect.set(0, 0, 100, 0);
                    } else {
                        outRect.set(0, 60, 100, 0);
                    }
                } else {
                    if (position < 2) {
                        outRect.set(0, 0, 0, 0);
                    } else {
                        outRect.set(0, 60, 0, 0);
                    }
                }
            }
        });
    }


    public void setData() {
        rlvContent.setVisibility(VISIBLE);
        rlDetail.setVisibility(GONE);
        if (ListUtils.isEmpty(helpBeanList)) {
            String textFromAsset = AssetUtils.getTextFromAsset(getContext(), "config/help.json");
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(textFromAsset);
                String helplist = jsonObject.getString("help");
                helpBeanList = GsonHelper.fromJsonToList(helplist, HelpBean[].class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (helpAdapter == null) {
            helpAdapter = new HelpAdapter(getContext(), helpBeanList);
            rlvContent.setAdapter(helpAdapter);
        } else {
            helpAdapter.setData(helpBeanList);
            helpAdapter.notifyDataSetChanged();
        }
        helpAdapter.setOnHelpItemClickListener(new HelpAdapter.OnHelpItemClickListener() {
            @Override
            public void OnItemClickListener(View itemView) {
                int position = rlvContent.getChildAdapterPosition(itemView);
                HelpBean bean = helpBeanList.get(position);
                tvTitle.setText(bean.getTitle());
                if (detailAdapter == null) {
                    detailAdapter = new HelpDetailAdapter(getContext(), bean.getMore());
                    rlvDetail.setAdapter(detailAdapter);
                } else {
                    detailAdapter.setData(bean.getMore());
                    detailAdapter.notifyDataSetChanged();
                }
                rlvContent.setVisibility(GONE);
                rlDetail.setVisibility(VISIBLE);
                mDetailType = true ;
            }
        });
    }

    public boolean getDetail() {
        return mDetailType;
    }

    public void setDetail() {
        mDetailType = false;
    }
}
