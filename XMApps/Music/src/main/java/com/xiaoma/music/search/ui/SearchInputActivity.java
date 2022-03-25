package com.xiaoma.music.search.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.music.R;


public class SearchInputActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_input);
        initView();
    }

    private void initView() {
        ImageView mIvMic = findViewById(R.id.iv_mic);
        EditText mEt = findViewById(R.id.et);
        mIvMic.setOnClickListener(this);
        mEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String text = v.getText().toString().trim();
                    if (TextUtils.isEmpty(text)) {
                        return false;
                    }
                    Intent intent = new Intent();
                    intent.putExtra(SearchActivity.INTENT_TEXT, text);
                    setResult(SearchActivity.REQUEST_CODE, intent);
                    finish();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_mic:
                break;
        }
    }
}
