package com.xiaoma.assistant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.manager.IAssistantManager;

/**
 * Created by qiuboxiang on 2019/2/18 17:48
 * Desc: 音乐识别控件
 */
public class MusicRecognitionView extends RelativeLayout implements View.OnClickListener {

    private Context context;
    private TextView mTvRecords;
    private ImageView mIvRecognition;
    private View mLayoutClose;

    public MusicRecognitionView(Context context) {
        this(context, null);
    }

    public MusicRecognitionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicRecognitionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public void initView(Context context) {
        this.context = context;
        inflate(getContext(), R.layout.view_music_recognition, this);
        mLayoutClose = findViewById(R.id.layout_close);
        mTvRecords = findViewById(R.id.tv_records);
        mIvRecognition = findViewById(R.id.iv_recognition);
        mLayoutClose.setOnClickListener(this);
        mTvRecords.setOnClickListener(this);
        mIvRecognition.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_close:
                //TODO
                break;
            case R.id.tv_records:
                //TODO
                break;
            case R.id.iv_recognition:
                //TODO
                break;
        }
    }
}
