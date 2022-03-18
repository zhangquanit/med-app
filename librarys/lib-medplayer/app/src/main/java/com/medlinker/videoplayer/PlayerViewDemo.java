package com.medlinker.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.medlinker.player.component.SimpleMedPlayerComponent;
import com.medlinker.player.entity.ControllerStatus;
import com.medlinker.player.view.MedPlayerController;
import com.medlinker.player.view.MedPlayerView;

/**
 * 基于MedPlayerView
 *
 * @author zhangquan
 */
public class PlayerViewDemo extends AppCompatActivity {
    private MedPlayerView mPlayer;

    public static void start(Context ctx, String url) {
        Intent intent = new Intent(ctx, PlayerViewDemo.class);
        intent.putExtra("url", url);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playerview_layout);

        String url = getIntent().getStringExtra("url");

        mPlayer = findViewById(R.id.videoView);
        mPlayer.setPlayComponent(new SimpleMedPlayerComponent() {
            @Override
            public void onComplete() { //播放完成

            }
        });
        //隐藏标题栏
//        mPlayer.getControllView().getTopView().setVisibility(View.GONE);
        //控制器显示设置
        mPlayer.setControllerStatus(ControllerStatus.ALWAYS_SHOW);
        //隐藏播放按钮
//        mPlayer.getControllView().getPlayPauseView().setVisibility(View.GONE);
        //隐藏全屏播放按钮
//        mPlayer.getControllView().getFullScreenSwitchView().setVisibility(View.GONE);
        //禁止拖动进度条
//        mPlayer.getControllView().getProgressBar().setEnabled(false);
        mPlayer.setVideoPath(url);
        mPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.onDestroy();
    }
}
