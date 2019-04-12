package com.liuchuanzheng.livedemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.liteav.demo.play.SuperPlayerConst;
import com.tencent.liteav.demo.play.SuperPlayerGlobalConfig;
import com.tencent.liteav.demo.play.SuperPlayerModel;
import com.tencent.liteav.demo.play.SuperPlayerUrl;
import com.tencent.liteav.demo.play.SuperPlayerView;
import com.tencent.rtmp.TXLiveConstants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //超级播放器View
    private SuperPlayerView mSuperPlayerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        checkPermission();
        initView();
    }
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();

            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 100);
            }
        }
    }
    private void initView() {


        mSuperPlayerView = (SuperPlayerView) findViewById(R.id.superVodPlayerView);
        initSuperVodGlobalSetting();
        mSuperPlayerView.setPlayerViewCallback(playerViewCallback);

        SuperPlayerModel superPlayerModel = new SuperPlayerModel();
        superPlayerModel.title = "刘传政测试";
//        superPlayerModel.videoURL = "http://5815.liveplay.myqcloud.com/live/5815_89aad37e06ff11e892905cb9018cf0d4.flv";
//        superPlayerModel.videoURL = "http://192.168.1.100:9012/api/HLS/Live_00026/stream.m3u8";
        superPlayerModel.videoURL = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";

        superPlayerModel.multiVideoURLs = new ArrayList<>();

        superPlayerModel.multiVideoURLs.add(new SuperPlayerUrl("超清","http://5815.liveplay.myqcloud.com/live/5815_89aad37e06ff11e892905cb9018cf0d4.flv"));

        superPlayerModel.multiVideoURLs.add(new SuperPlayerUrl("高清","http://5815.liveplay.myqcloud.com/live/5815_89aad37e06ff11e892905cb9018cf0d4_900.flv"));

        superPlayerModel.multiVideoURLs.add(new SuperPlayerUrl("标清","http://5815.liveplay.myqcloud.com/live/5815_89aad37e06ff11e892905cb9018cf0d4_550.flv"));
        mSuperPlayerView.playWithMode(superPlayerModel);

    }
    /**
     * 初始化超级播放器全局配置
     */
    private void initSuperVodGlobalSetting() {
        SuperPlayerGlobalConfig prefs = SuperPlayerGlobalConfig.getInstance();
        // 开启悬浮窗播放
        prefs.enableFloatWindow = true;
        // 设置悬浮窗的初始位置和宽高
        SuperPlayerGlobalConfig.TXRect rect = new SuperPlayerGlobalConfig.TXRect();
        rect.x = 0;
        rect.y = 0;
        rect.width = 810;
        rect.height = 540;
        prefs.floatViewRect = rect;
        // 播放器默认缓存个数
        prefs.maxCacheItem = 5;
        // 设置播放器渲染模式
        prefs.enableHWAcceleration = true;
        prefs.renderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
    }
    SuperPlayerView.PlayerViewCallback playerViewCallback = new SuperPlayerView.PlayerViewCallback() {
        @Override
        public void hideViews() {

        }

        @Override
        public void showViews() {

        }

        @Override
        public void onQuit(int playMode) {
            if (playMode == SuperPlayerConst.PLAYMODE_FLOAT) {
                mSuperPlayerView.resetPlayer();
                finish();
            } else if (playMode == SuperPlayerConst.PLAYMODE_WINDOW) {
                if (mSuperPlayerView.getPlayState() == SuperPlayerConst.PLAYSTATE_PLAY) {
                    /*// 返回桌面
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);*/
                    // 把当前Activity运行至后台
                    moveTaskToBack(true);
                } else {
                    mSuperPlayerView.resetPlayer();
                    finish();
                }
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        if (mSuperPlayerView.getPlayState() == SuperPlayerConst.PLAYSTATE_PLAY) {
            mSuperPlayerView.onResume();
            if (mSuperPlayerView.getPlayMode() == SuperPlayerConst.PLAYMODE_FLOAT) {
                mSuperPlayerView.requestPlayMode(SuperPlayerConst.PLAYMODE_WINDOW);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSuperPlayerView.getPlayMode() != SuperPlayerConst.PLAYMODE_FLOAT) {
            mSuperPlayerView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSuperPlayerView.release();
        if (mSuperPlayerView.getPlayMode() != SuperPlayerConst.PLAYMODE_FLOAT) {
            mSuperPlayerView.resetPlayer();
        }
    }
}
