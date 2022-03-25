package com.xiaoma.facerecognize.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.facerecognize.R;
import com.xiaoma.facerecognize.sdk.RecognizeFactory;
import com.xiaoma.facerecognize.sdk.RecognizeType;
import com.xiaoma.thread.ThreadDispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaka
 * on 19-4-10 下午8:10
 * <p>
 * desc: #a
 * </p>
 */
public class TestActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    private RadioGroup mTypes;
    private Button mBtn;
    private EditText mEditText;
    private List<RecognizeType> mCurrentType = new ArrayList<>();
    private CheckBox attention;
    private CheckBox smoking;
    private CheckBox phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        initView();
        initListener();
    }

    private void initListener() {
        mTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_1) {
                    mCurrentType.clear();
                    check(false);
                    mCurrentType.add(RecognizeType.LightFatigueDriving);
                } else if (checkedId == R.id.rb_2) {
                    mCurrentType.clear();
                    check(false);
                    mCurrentType.add(RecognizeType.MiddleFatigueDriving);
                } else if (checkedId == R.id.rb_3) {
                    mCurrentType.clear();
                    check(false);
                    mCurrentType.add(RecognizeType.HeavyFatigueDriving);
                }
            }
        });

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long delay;
                try {
                    delay = Long.parseLong(mEditText.getText().toString());
                } catch (Exception e) {
                    delay = 0;
                }
                if (delay == 0) {
                    RecognizeFactory.getSDK().mockHandleRecognize(mCurrentType.get(0));
                } else {
                    ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RecognizeFactory.dispatchListener(mCurrentType.get(0));
                        }
                    }, delay * 1000);
                }
                finish();
            }
        });
        attention.setOnCheckedChangeListener(this);
        smoking.setOnCheckedChangeListener(this);
        phone.setOnCheckedChangeListener(this);
    }

    private void check(boolean b) {
        attention.setChecked(b);
        smoking.setChecked(b);
        phone.setChecked(b);
    }

    private void initView() {
        mTypes = findViewById(R.id.types);
        mBtn = findViewById(R.id.btn);
        mEditText = findViewById(R.id.editText);
        attention = findViewById(R.id.cb_attention);
        smoking = findViewById(R.id.cb_smoking);
        phone = findViewById(R.id.cb_phone);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mTypes.clearCheck();
        mCurrentType.clear();
        if (attention.isChecked()) {
            mCurrentType.add(RecognizeType.Inattention);
        } else {
            mCurrentType.remove(RecognizeType.Inattention);
        }
        if (phone.isChecked()) {
            mCurrentType.add(RecognizeType.PhoneCall);
        } else {
            mCurrentType.remove(RecognizeType.PhoneCall);
        }
        if (smoking.isChecked()) {
            mCurrentType.add(RecognizeType.Smoking);
        } else {
            mCurrentType.remove(RecognizeType.Smoking);
        }
    }
}
