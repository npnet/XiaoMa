package com.xiaoma.xting.sdk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.xting.R;
import com.xiaoma.xting.sdk.bean.XMAdvertis;
import com.xiaoma.xting.sdk.bean.XMAdvertisList;
import com.xiaoma.xting.sdk.bean.XMPlayableModel;

import java.util.ArrayList;
import java.util.List;

import static com.xiaoma.utils.GsonHelper.jsonFormat;

/**
 * @author youthyJ
 * @date 2018/12/4
 */
public abstract class TestPanelActivity extends BaseActivity {
    private RecyclerView listView;
    private TextView tvPlayerStatus;
    private TextView tvResult;
    protected List<Item> itemList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_panel);
        setupItemData();
        listView = findViewById(R.id.rv_list);
        tvResult = findViewById(R.id.tv_result);
        tvPlayerStatus = findViewById(R.id.tv_player_status);
        initView();
        setupPlayerStatusListen();
    }

    protected void setResult(String result) {
        tvResult.setText(jsonFormat(result));
    }

    abstract protected void setupItemData();

    private void setupPlayerStatusListen() {
        OnlineFMPlayerFactory.getPlayer().addPlayerStatusListener(new PlayerStatusListener() {
            @Override
            public void onPlayStart() {
                updatePlayerStatus("onPlayStart");
            }

            @Override
            public void onPlayPause() {
                updatePlayerStatus("onPlayPause");
            }

            @Override
            public void onPlayStop() {
                updatePlayerStatus("onPlayStop");
            }

            @Override
            public void onSoundPlayComplete() {
                updatePlayerStatus("onSoundPlayComplete");
            }

            @Override
            public void onSoundPrepared() {
                updatePlayerStatus("onSoundPrepared");
            }

            @Override
            public void onSoundSwitch(XMPlayableModel curSound) {
                updatePlayerStatus("onSoundSwitch");
            }

            @Override
            public void onBufferingStart() {
                updatePlayerStatus("onBufferingStart");
            }

            @Override
            public void onBufferingStop() {
                updatePlayerStatus("onBufferingStop");
            }

            @Override
            public void onBufferProgress(int percent) {
                updatePlayerStatus("onBufferProgress");
            }

            @Override
            public void onPlayProgress(int currPos, int duration) {
                updatePlayerStatus("onPlayProgress");
            }

            @Override
            public void onError(Exception exception) {
                updatePlayerStatus("onError: " + exception.getMessage());
            }

            @Override
            public void onStartGetAdsInfo() {
                updatePlayerStatus("onStartGetAdsInfo");
            }

            @Override
            public void onGetAdsInfo(XMAdvertisList ads) {
                updatePlayerStatus("onGetAdsInfo");
            }

            @Override
            public void onAdsStartBuffering() {
                updatePlayerStatus("onAdsStartBuffering");
            }

            @Override
            public void onAdsStopBuffering() {
                updatePlayerStatus("onAdsStopBuffering");
            }

            @Override
            public void onStartPlayAds(XMAdvertis ad, int position) {
                updatePlayerStatus("onStartPlayAds");
            }

            @Override
            public void onCompletePlayAds() {
                updatePlayerStatus("onCompletePlayAds");
            }

            @Override
            public void onError(int what, int extra) {
                updatePlayerStatus("onPlayStart - what:" + what + " extra: " + extra);
            }
        });
    }

    private void updatePlayerStatus(String status) {
        tvPlayerStatus.setText("状态: " + status);
    }

    private void initView() {
        Adapter adapter = new Adapter();
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);
        listView.setNestedScrollingEnabled(false);
        adapter.notifyDataSetChanged();
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(TestPanelActivity.this);
            View view = inflater.inflate(R.layout.item_xmly_test, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Item item = itemList.get(position);
            holder.setPosition(position);
            holder.setName(item.btnName);
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private Button btnName;
        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            this.btnName = itemView.findViewById(R.id.btn_start);
            this.btnName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemList.get(position).run();

                }
            });
        }

        private void setPosition(int position) {
            this.position = position;
        }

        private void setName(String name) {
            btnName.setText(name);
        }
    }

    public class Item {
        private String btnName;
        private Task task;

        public Item(String btnName) {
            this.btnName = btnName;
        }

        private void run() {
            if (task != null) {
                task.run();
            }
        }

        public Item setTask(Task task) {
            this.task = task;
            this.task.bindItem(this);
            return this;
        }
    }

    public abstract class Task {
        protected Item item;

        private void bindItem(Item item) {
            this.item = item;
        }

        abstract protected void run();
    }
}
