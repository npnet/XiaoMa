package com.qiming.fawcard.synthesize.core.drivescore;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.TestUtils;
import com.qiming.fawcard.synthesize.base.system.callback.HistoryRegistrationCenter;
import com.qiming.fawcard.synthesize.base.widget.BackButton;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDao;
import com.qiming.fawcard.synthesize.data.source.local.ORMLiteHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLog;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowLog.class}, sdk = Build.VERSION_CODES.JELLY_BEAN_MR2)


public class DriveScoreHistoryActivityTest {

    private RecyclerView rlvHistoryList;
    private DriveScoreHistoryActivity mActivity;

    @Before
    public void setUp() {
        ShadowLog.stream = System.out;
        ORMLiteHelper.getInstance(RuntimeEnvironment.application);
    }

    @After
    public void tearDown() {
        TestUtils.resetSingleton(ORMLiteHelper.class, "mInstance");
        HistoryRegistrationCenter.clear();
    }

    //存在历史记录，进入历史记录画面，验证历史记录正确显示
    @Test
    public void dataToView() {

        TestUtils.creatHistoryEntity(1559550037077L, 79.9d, 5L, 4L, 8L);
        TestUtils.creatHistoryEntity(1458942409L, 30d, 1L, 2L, 8L);

        mActivity = Robolectric.setupActivity(DriveScoreHistoryActivity.class);
        rlvHistoryList = mActivity.findViewById(R.id.history_list);

        TextView tvDate = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvDate);
        TextView tvTime = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvTime);
        TextView tvScore = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvScore);
        TextView tvAccNum = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvAccNum);
        TextView tvDecNum = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvDecNum);
        TextView tvTurnNum = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvTurnNum);

        assertEquals("2019年06月03日", tvDate.getText());
        assertEquals("16:20", tvTime.getText());
        assertEquals(("79" + mActivity.getResources().getString(R.string.history_score)), tvScore.getText());
        assertEquals((8 + mActivity.getResources().getString(R.string.count)), tvAccNum.getText());
        assertEquals((4 + mActivity.getResources().getString(R.string.count)), tvDecNum.getText());
        assertEquals((5 + mActivity.getResources().getString(R.string.count)), tvTurnNum.getText());

        TextView tvDate1 = rlvHistoryList.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.tvDate);
        TextView tvTime1 = rlvHistoryList.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.tvTime);
        TextView tvScore1 = rlvHistoryList.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.tvScore);
        TextView tvAccNum1 = rlvHistoryList.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.tvAccNum);
        TextView tvDecNum1 = rlvHistoryList.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.tvDecNum);
        TextView tvTurnNum1 = rlvHistoryList.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.tvTurnNum);

        assertEquals("1970年01月18日", tvDate1.getText());
        assertEquals("05:15", tvTime1.getText());
        assertEquals(("30" + mActivity.getResources().getString(R.string.history_score)), tvScore1.getText());
        assertEquals((8 + mActivity.getResources().getString(R.string.count)), tvAccNum1.getText());
        assertEquals((2 + mActivity.getResources().getString(R.string.count)), tvDecNum1.getText());
        assertEquals((1 + mActivity.getResources().getString(R.string.count)), tvTurnNum1.getText());

    }


    //无历史记录时进入历史记录画面，验证界面显示应无记录。
    @Test
    public void noDataToView() {
        mActivity = Robolectric.setupActivity(DriveScoreHistoryActivity.class);
        rlvHistoryList = mActivity.findViewById(R.id.history_list);

        RecyclerView.ViewHolder viewHolder = rlvHistoryList.findViewHolderForAdapterPosition(0);

        assertNull(viewHolder);
    }


    //验证进行删除历史记录功能，
    @Test
    public void del() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        DriveScoreHistoryDao mDriveScoreHistoryDao = new DriveScoreHistoryDao(activity);

        TestUtils.creatHistoryEntity(1559550037077L, 79.9d, 1L, 2L, 8L);
        TestUtils.creatHistoryEntity(1458942409L, 30d, 1L, 2L, 8L);
        mActivity = Robolectric.setupActivity(DriveScoreHistoryActivity.class);
        rlvHistoryList = mActivity.findViewById(R.id.history_list);

        int num = mDriveScoreHistoryDao.queryHistoryList().size();
        assertEquals(2, num);

        rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvDelete).performClick();
        num = mDriveScoreHistoryDao.queryHistoryList().size();

        //验证条目数量
        assertEquals(1, num);

        TextView tvDate = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvDate);
        TextView tvTime = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvTime);
        TextView tvScore = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvScore);
        TextView tvAccNum = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvAccNum);
        TextView tvDecNum = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvDecNum);
        TextView tvTurnNum = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvTurnNum);

        //验证条目内容
        assertEquals("1970年01月18日", tvDate.getText());
        assertEquals("05:15", tvTime.getText());
        assertEquals(("30" + mActivity.getResources().getString(R.string.history_score)), tvScore.getText());

        assertEquals((1 + mActivity.getResources().getString(R.string.count)), tvTurnNum.getText());
        assertEquals((2 + mActivity.getResources().getString(R.string.count)), tvDecNum.getText());
        assertEquals((8 + mActivity.getResources().getString(R.string.count)), tvAccNum.getText());
    }

    /*
        删除数据后，验证日期显示。
     */
    @Test
    public void delDateView() {
        TestUtils.creatHistoryEntity(1559550037077L, 79.9d, 1L, 2L, 8L);
        TestUtils.creatHistoryEntity(1458942409L, 30d, 1L, 2L, 8L);
        TestUtils.creatHistoryEntity(1458942409L, 50d, 1L, 2L, 1L);
        TestUtils.creatHistoryEntity(1559550037177L, 88d, 1L, 2L, 8L);

        mActivity = Robolectric.setupActivity(DriveScoreHistoryActivity.class);
        rlvHistoryList = mActivity.findViewById(R.id.history_list);

        //第一条数据与第二条数据日期相同，验证删除第一条数据后，日期显示是否正确
        TextView tv1 = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvDate);
        String data1 = tv1.getText().toString();

        rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvDelete).performClick();

        TextView tv2 = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvDate);
        String data2 = tv2.getText().toString();

        assertEquals(data1, data2);

        //第一条数据与第二条数据日期不相同，验证删除第一条数据后，日期显示是否正确
        rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvDelete).performClick();
        TextView tv3 = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tvDate);
        data2 = tv3.getText().toString();

        assertNotEquals(data1, data2);
    }

    /*
         验证日期标签显示
    */
    @Test
    public void dateToView() {
        //将信息记录插入到数据库中
        TestUtils.creatHistoryEntity(1559550037077L, 79.9d, 1L, 2L, 8L);
        TestUtils.creatHistoryEntity(1458942409L, 30d, 1L, 2L, 8L);
        TestUtils.creatHistoryEntity(1458942409L, 50d, 1L, 2L, 1L);

        mActivity = Robolectric.setupActivity(DriveScoreHistoryActivity.class);
        rlvHistoryList = mActivity.findViewById(R.id.history_list);

        //第一条数据显示日期标签
        int visibility = rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.clDate).getVisibility();
        assertEquals(View.VISIBLE, visibility);

        //一条数据与前一条数据日期不相同，日期标签显示
        visibility = rlvHistoryList.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.clDate).getVisibility();
        assertEquals(View.VISIBLE, visibility);

        rlvHistoryList.scrollToPosition(2);
        //一条数据与前一条数据日期相同，日期标签不显示
        visibility = rlvHistoryList.findViewHolderForAdapterPosition(2).itemView.findViewById(R.id.clDate).getVisibility();
        assertEquals(View.GONE, visibility);
    }

    /*
        测试点击历史记录条目
    */
    @Test
    public void onClickItem() {

        TestUtils.creatHistoryEntity(1559550037077L, 79.9d, 1L, 2L, 8L);

        mActivity = Robolectric.setupActivity(DriveScoreHistoryActivity.class);
        rlvHistoryList = mActivity.findViewById(R.id.history_list);

        rlvHistoryList.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.llItemClick).performClick();

        ShadowApplication shadowApplication = ShadowApplication.getInstance();
        Intent act = shadowApplication.getNextStartedActivity();
        assertEquals(DriveScoreHistoryDetailActivity.class.getName(), act.getComponent().getClassName());
    }

    /*
    验证点击“BACK”按钮
     */
    @Test
    public void onClickBackTest() {
        mActivity = Robolectric.setupActivity(DriveScoreHistoryActivity.class);
        BackButton backButton = mActivity.findViewById(R.id.ibBackButton);
        backButton.performClick();
        assertTrue(mActivity.isFinishing());
    }
}