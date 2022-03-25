package com.xiaoma.instructiondistribute.test;

import android.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.xiaoma.instructiondistribute.R;

import java.util.ArrayList;
import java.util.List;


/**
 * @author youthyJ
 * @date 2018/12/4
 */
public abstract class TestPanelActivity extends FragmentActivity {
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
        tvResult.setText(result);
    }

    abstract protected void setupItemData();

    private void setupPlayerStatusListen() {

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
