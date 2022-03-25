package com.xiaoma.dialect.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.dialect.R;
import com.xiaoma.dialect.adapter.ChooseAnswerAdapter;
import com.xiaoma.dialect.model.QuestionBean;
import com.xiaoma.dialect.vm.MainVM;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.RemoteIatManager;

import java.util.List;

/**
 * Created by Thomas on 2018/11/6 0006
 */

public class MainActivity extends BaseActivity {


    private TextView tvExit;
    private TextView tvRole;
    private TextView tvTips;
    private GridView gvChoose;
    private ViewStub vsResult;
    private ViewStub vsFinalResult;
    private LinearLayout llQues;
    private TextView tvQuesIndex;
    private TextView tvEnd;

    private View resultView;
    private View finalResultView;
    private TextView tvResult;
    private TextView tvWait;
    private TextView tvLeftChance;

    private TextView tvAnswerCount;
    private TextView tvAnswerScore;
    private TextView tvDefeatPercent;

    private MainVM mainVM;

    private List<String> listAnswer;
    private List<QuestionBean> listQuestion;
    private ChooseAnswerAdapter chooseAnswerAdapter;
    //题目索引
    private int index = 0;
    //错误次数统计
    private int miss = 0;
    private final static int TOTAL_CHANCE = 3;
    private final static int WAIT_TIME = 3000;
    private final static int WAIT_TIME_INTERVAL = 1000;

    //答对次数统计
    private int rightCount = 0;
    private CountDownTimer countDownTimer;
    private Button btnRetry;
    private Button btnSkip;

    //创建一个SoundPool对象 播放音频文件
    private SoundPool soundPool;
    private Button btnReListen;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_todo);
//        initView();
//        initData();

        RemoteIatManager.getInstance().init(this);
        RemoteIatManager.getInstance().setOnIatListener(new OnIatListener() {
            @Override
            public void onComplete(String voiceText, String parseText) {
                KLog.d("voice onComplete: " + voiceText);
                XMToast.showToast(MainActivity.this, voiceText);
            }

            @Override
            public void onVolumeChanged(int volume) {
            }

            @Override
            public void onNoSpeaking() {
                KLog.d("voice onNoSpeaking ");
            }

            @Override
            public void onError(int errorCode) {
                KLog.d("voice onError " + errorCode);
            }

            @Override
            public void onResult(String recognizerText, boolean isLast, String currentText) {
                KLog.d("voice onResult  " + recognizerText + " --- " + isLast + "  " + currentText);
            }

            @Override
            public void onWavFileComplete() {
                KLog.d("voice onWavFileComplete");
            }

            @Override
            public void onRecordComplete() {
                KLog.d("voice onRecordComplete");
            }
        });
    }

    public void onRecord(View view) {
        XMToast.showToast(this, "开始录音");
        RemoteIatManager.getInstance().startListeningRecord();
    }
//    @SuppressLint("StringFormatMatches")
//    private void initView() {
//        tvExit = findViewById(R.id.tv_exit);
//        tvRole = findViewById(R.id.tv_role);
//        tvTips = findViewById(R.id.tv_tips);
//        gvChoose = findViewById(R.id.gv_choose);
//        vsResult = findViewById(R.id.vs_result);
//        vsFinalResult = findViewById(R.id.vs_final_result);
//        llQues = findViewById(R.id.ll_ques);
//        tvQuesIndex = findViewById(R.id.tv_ques_index);
//        tvEnd = findViewById(R.id.tv_end);
//        btnReListen = findViewById(R.id.btn_re_listen);
//
//        btnReListen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                soundPool.play(1, 15, 15, 0, 0, 1);
//            }
//        });
//        //初始化soundPool
//        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
//        soundPool.load(this, R.raw.demo2, 1);
//
//
//        tvTips.setText(String.format(getString(R.string.listen_question, index + 1)));
//        tvQuesIndex.setText(String.format(getString(R.string.index_question, index + 1)));
//        listAnswer = new ArrayList<>();
//        listQuestion = new ArrayList<>();
//        chooseAnswerAdapter = new ChooseAnswerAdapter(listAnswer, this);
//        gvChoose.setAdapter(chooseAnswerAdapter);
//        gvChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                soundPool.stop(1);
//                if (listQuestion.get(index).rightIndex == position) {
//                    //如果选择了正确答案
//                    handlerRightChoose(position);
//                } else {
//                    //如果选择了错误答案
//                    handlerWrongAnswer();
//                }
//                index++;
//            }
//        });
//    }


//    private void initData() {
//        countDownTimer = new CountDownTimer(WAIT_TIME, WAIT_TIME_INTERVAL) {
//            @SuppressLint("StringFormatMatches")
//            @Override
//            public void onTick(long millisUntilFinished) {
//                tvWait.setText(String.format(getString(R.string.wait_to_next_question, millisUntilFinished / 1000, index + 1)));
//            }
//
//            @Override
//            public void onFinish() {
//                resetQuestion();
//            }
//        };
//
//        mainVM = ViewModelProviders.of(this).get(MainVM.class);
//        mainVM.getQuestionList().observe(this, new Observer<XmResource<List<QuestionBean>>>() {
//            @Override
//            public void onChanged(@Nullable XmResource<List<QuestionBean>> listXmResource) {
//                if (listXmResource == null) {
//                    return;
//                }
//                listXmResource.handle(new OnCallback<List<QuestionBean>>() {
//                    @SuppressLint("StringFormatMatches")
//                    @Override
//                    public void onSuccess(List<QuestionBean> data) {
//                        if (!ListUtils.isEmpty(data)) {
//                            //初始化数据
//                            listQuestion.addAll(data);
//                            listAnswer.addAll(data.get(0).chooseList);
//                            chooseAnswerAdapter.notifyDataSetChanged();
//                            gvChoose.setVisibility(View.VISIBLE);
//                            vsFinalResult.setVisibility(View.GONE);
//                            vsResult.setVisibility(View.GONE);
//
//                            soundPool.play(1, 15, 15, 0, 0, 1);
//
//                        }
//                    }
//                });
//            }
//        });
//        mainVM.fetchRecommendList();
//    }


    @SuppressLint("StringFormatMatches")
    private void handlerWrongAnswer() {
        miss++;
        tvTips.setText(String.format(getString(R.string.wrong_answer, listAnswer.get(listQuestion.get(index).rightIndex))));
        try {
            initVsResult();
        } catch (Exception e) {
            vsResult.setVisibility(View.VISIBLE);
        } finally {
            vsFinalResult.setVisibility(View.GONE);
            gvChoose.setVisibility(View.GONE);
            tvResult.setText(R.string.no_matter);
            tvLeftChance.setVisibility(View.VISIBLE);
            tvLeftChance.setText(String.format(getString(R.string.left_chance), TOTAL_CHANCE - miss));
            countDownTimer.start();


        }
    }

    @SuppressLint("StringFormatMatches")
    private void handlerRightChoose(int position) {
        rightCount++;
        tvTips.setText(String.format(getString(R.string.con_right_answer, listAnswer.get(position))));
        try {
            initVsResult();
        } catch (Exception e) {
            vsResult.setVisibility(View.VISIBLE);
        } finally {
            vsFinalResult.setVisibility(View.GONE);
            gvChoose.setVisibility(View.GONE);
            tvResult.setText(R.string.good_job);
            tvLeftChance.setVisibility(View.GONE);
            countDownTimer.start();


        }

    }

    private void initVsResult() {
        resultView = vsResult.inflate();
        tvResult = resultView.findViewById(R.id.tv_answer_result);
        tvWait = resultView.findViewById(R.id.tv_wait);
        tvLeftChance = resultView.findViewById(R.id.tv_left_chance);
    }


    @SuppressLint("StringFormatMatches")
    private void resetQuestion() {
        if (miss >= TOTAL_CHANCE) {
            //已经用完所有机会
            try {
                initVsFinal();
            } catch (Exception e) {
                vsFinalResult.setVisibility(View.VISIBLE);
            } finally {
                vsResult.setVisibility(View.GONE);
                gvChoose.setVisibility(View.GONE);
                tvAnswerCount.setText(String.format(getString(R.string.right_answer_count), rightCount));
                tvAnswerScore.setText(String.format(getString(R.string.right_score_count), rightCount));
                tvDefeatPercent.setText(String.format(getString(R.string.right_score_count), rightCount * 10));
                tvTips.setText(R.string.game_over);
                tvEnd.setVisibility(View.VISIBLE);
                llQues.setVisibility(View.GONE);
            }
        } else if (index < listQuestion.size()) {
            listAnswer.clear();
            listAnswer.addAll(listQuestion.get(index).chooseList);
            chooseAnswerAdapter.notifyDataSetChanged();
            vsResult.setVisibility(View.GONE);
            gvChoose.setVisibility(View.VISIBLE);
            tvTips.setText(String.format(getString(R.string.listen_question, index + 1)));
            tvQuesIndex.setText(String.format(getString(R.string.index_question, index + 1)));

            soundPool.play(1, 15, 15, 0, 0, 1);
        } else {
            //已经答完所有题目
            try {
                initVsFinal();
            } catch (Exception e) {
                vsFinalResult.setVisibility(View.VISIBLE);
            } finally {
                vsResult.setVisibility(View.GONE);
                gvChoose.setVisibility(View.GONE);
                tvAnswerCount.setText(String.format(getString(R.string.right_answer_count), rightCount));
                tvAnswerScore.setText(String.format(getString(R.string.right_score_count), rightCount));
                tvDefeatPercent.setText(String.format(getString(R.string.right_score_count), rightCount * 10));
                tvTips.setText(R.string.game_over);
                tvEnd.setVisibility(View.VISIBLE);
                llQues.setVisibility(View.GONE);
            }
        }
    }

    private void initVsFinal() {
        finalResultView = vsFinalResult.inflate();
        tvAnswerCount = finalResultView.findViewById(R.id.tv_answer_count);
        tvAnswerScore = finalResultView.findViewById(R.id.tv_answer_score);
        tvDefeatPercent = finalResultView.findViewById(R.id.tv_defeat_percent);
        btnRetry = finalResultView.findViewById(R.id.btn_retry);
        btnSkip = finalResultView.findViewById(R.id.btn_skip);

    }

    @SuppressLint("StringFormatMatches")
    public void retry(View v) {
        miss = 0;
        rightCount = 0;
        index = 0;
        listQuestion.clear();
        listAnswer.clear();
        tvEnd.setVisibility(View.GONE);
        llQues.setVisibility(View.VISIBLE);
        tvTips.setText(String.format(getString(R.string.listen_question, index + 1)));
        tvQuesIndex.setText(String.format(getString(R.string.index_question, index + 1)));
        mainVM.fetchRecommendList();
    }

    public void leader(View view) {
        startActivity(new Intent(this, LeaderboardActivity.class));
    }

    @SuppressLint("StringFormatMatches")
    public void exitGame(View view) {
        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.tip))
                    .setMessage(String.format(getString(R.string.exit_dialog_msg, rightCount * 10)))
                    .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
            alertDialog = builder.create();
        }
        alertDialog.show();
    }

    @Override
    protected boolean isAppNeedShowNaviBar() {
        return false;
    }
}
