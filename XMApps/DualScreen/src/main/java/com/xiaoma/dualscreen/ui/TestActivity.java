//package com.xiaoma.dualscreen.ui;
//
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.widget.MediaController;
//import android.widget.VideoView;
//
//import com.xiaoma.component.base.BaseActivity;
//import com.xiaoma.dualscreen.R;
//
///**
// * Created by Administrator on 2019/1/14 0014.
// * 废弃
// */
//
//public class TestActivity extends BaseActivity {
//
//    private VideoView videoview;
//    private String url="http://flashmedia.eastday.com/newdate/news/2016-11/shznews1125-19.mp4";
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//        videoview = (VideoView) findViewById(R.id.video);
//        initVideoPath();//初始化
//        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                Log.i("tag", "准备完毕,可以播放了");
//                mp.start();
//            }
//        });
//        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                Log.i("tag", "播放完毕");
//            }
//        });
//        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                Log.i("tag", "播放失败");
//                return false;
//            }
//        });
//    }
//
//    private void initVideoPath() {
//        /**媒体控制面版常用方法：MediaController:
//         hide();     隐藏MediaController;
//         show();     显示MediaController
//         show(int timeout);设置MediaController显示的时间，以毫秒计算，如果设置为0则一直到调用hide()时隐藏；
//         */
//        videoview.setMediaController(new MediaController(this));//这样就有滑动条了
//        /*File file = new File(Environment.getExternalStorageDirectory(),"movie.3gp");
//        videoView.setVideoPath(file.getPath()); // 指定视频文件的路径*/
//        videoview.setVideoURI(Uri.parse(url));//播放网络视频
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (videoview != null) {
//            videoview.suspend();
//        }
//    }
//}
