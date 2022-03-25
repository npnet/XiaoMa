package com.xiaoma.assistant.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.utils.log.KLog;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/12
 * Desc:语音助手联系人列表
 */
public class ContactView extends RelativeLayout {

    private static final int PAGE_SIZE = 3;
    private TextView tvTitle;
    private TextView tvResult;
    private RecyclerView rlvResult;
    private ImageView ivVoice;
    private BaseMultiPageAdapter mAdapter;
    private TextView page;
    private int[] micImages;
    private int firstPosition;
    private int lastPosition;

    public ContactView(Context context) {
        super(context);
        initView();
    }

    public ContactView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ContactView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initAnimations() {
        micImages = new int[]{R.drawable.listening_anim0, R.drawable.listening_anim1, R.drawable.listening_anim2,
                R.drawable.listening_anim3, R.drawable.listening_anim4, R.drawable.listening_anim5,
                R.drawable.listening_anim6, R.drawable.listening_anim7, R.drawable.listening_anim8,
                R.drawable.listening_anim9};
    }

    private void initView() {
        inflate(getContext(), R.layout.view_assistant_contact, this);
        tvTitle = findViewById(R.id.tv_assistant_contact_title);
        tvResult = findViewById(R.id.tv_assistant_contact_search_result);
        rlvResult = findViewById(R.id.rlv_assistant_contact);
        ivVoice = findViewById(R.id.iv_assistant_contact_voice);
        page = findViewById(R.id.page);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rlvResult.setLayoutManager(manager);
        rlvResult.addItemDecoration(new HorizontalItemDecoration());
        initAnimations();
        rlvResult.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                //判断是当前layoutManager是否为LinearLayoutManager
                // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取最后一个可见view的位置
                    lastPosition = linearManager.findLastVisibleItemPosition();
                    //获取第一个可见view的位置
                    firstPosition = linearManager.findFirstVisibleItemPosition();
                    KLog.d("zhs---" + lastPosition + "   " + firstPosition);
                }
                setPageNum();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    public boolean setData(BaseMultiPageAdapter adapter) {
        if (adapter == null) {
            return false;
        }
        this.mAdapter = adapter;
        rlvResult.setAdapter(adapter);
        firstPosition = 0;
        lastPosition = PAGE_SIZE - 1;
        setPageNum();
        return true;
    }


    public void setPage(int page) {
        if (mAdapter != null) {
            int size = PAGE_SIZE;
            if (page > 0) {
                if (lastPosition - firstPosition >= PAGE_SIZE) {
                    size = PAGE_SIZE - 1;
                }
                rlvResult.smoothScrollToPosition(lastPosition + size);
            } else {
                if (lastPosition - firstPosition >= PAGE_SIZE) {
                    size = PAGE_SIZE - 1;
                }
                rlvResult.smoothScrollToPosition(firstPosition - size >= 0 ? firstPosition - size : 0);
            }
        }
    }

    public void setVoiceAnim(int volume) {
        volume = (int) (volume * 1.0f);
        if (micImages == null) initAnimations();
        if (volume >= micImages.length) {
            volume = micImages.length - 1;
        } else if (volume < 0) {
            volume = 0;
        }
        ivVoice.setImageResource(micImages[volume]);
    }

    public boolean isFirstPage() {
        return firstPosition == 0 && lastPosition - firstPosition < PAGE_SIZE;
    }

    public boolean isLastPage() {
        return lastPosition == mAdapter.getItemCount() && lastPosition - firstPosition < PAGE_SIZE;
    }

    public void setPageNum() {
        int totalPage = mAdapter.getItemCount() % PAGE_SIZE == 0 ? mAdapter.getItemCount() / PAGE_SIZE : (mAdapter.getItemCount() / PAGE_SIZE) + 1;
        int curPage = -1 ;
        if (lastPosition == mAdapter.getItemCount() - 1) {
            curPage = totalPage;
        } else {
            curPage = (lastPosition + 1) / PAGE_SIZE;
        }
        page.setText(curPage + "/" + totalPage);
    }
}
