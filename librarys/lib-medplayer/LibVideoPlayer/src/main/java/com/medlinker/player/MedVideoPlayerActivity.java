package com.medlinker.player;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.medlinker.player.component.MedPlayerComponent;
import com.medlinker.player.component.MedPlayerComponentProxy;
import com.medlinker.player.entity.MedVideoInfo;
import com.medlinker.player.util.MedPlayerStatusBarUtil;
import com.medlinker.player.util.MedPlayerUtils;
import com.medlinker.player.view.MedPlayerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 点播视频播放页
 * <pre>
 *    ARouter.getInstance().build("/videoplayer/vod")
 *                 .withString("title", title)
 *                 .withString("url", url)
 *                 .withString("cover", cover)
 *                 .withBoolean("autoPlay", !"0".equals(autoPlay))
 *                 .withString("extra", extra)
 *                 .withString("components", componentsArray.toString())
 *                 .navigation();
 * </pre>
 * <p>
 * 参数说明:
 * url:视频链接  必传
 * title：视频标题
 * cover：视频封面
 * autoPlay 是否自动播放  1自动播放(默认)
 * extra：扩展参数：{seekTo:指定播放进度,单位秒, dragDisable:禁止拖动进度条，1禁止, autoClose:播放完毕自动关闭页面,1关闭， looping:是否循环播放，1循环}
 * components:扩展组件 参考MedPlayerComponent
 * </p
 *
 * @author zhangquan
 */
@Route(path = "/videoplayer/vod")
public class MedVideoPlayerActivity extends AppCompatActivity {
    private MedPlayerView mPlayer;
    String mVideoPath;
    String mTitle;
    String mCover;
    String mExtra;
    boolean mAutoPlay;
    MedPlayerComponent medPlayerComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.med_video_player_activity);
        MedPlayerStatusBarUtil.setStatusBarBgColor(this, Color.BLACK);
        MedPlayerStatusBarUtil.setStatusBarDarkMode(this, false);
        mPlayer = findViewById(R.id.videoView);

        mVideoPath = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(mVideoPath) || TextUtils.equals(mVideoPath, "null")) {
            MedPlayerUtils.showToast(this, "视频url为空");
            finish();
            return;
        }
        mTitle = getIntent().getStringExtra("title");
        mCover = getIntent().getStringExtra("cover");
        mAutoPlay = getIntent().getBooleanExtra("autoPlay", true);
        mExtra = getIntent().getStringExtra("extra");
        if (!TextUtils.isEmpty(mExtra)) {
            try {
                String extra = URLDecoder.decode(mExtra, "utf-8");
                JSONObject jsonObject = new JSONObject(extra);
                int seekTo = jsonObject.optInt("seekTo");
                if (seekTo > 0) {
                    mPlayer.setAVOptions(seekTo);
                }
                boolean dragDisable = jsonObject.optBoolean("dragDisable");
                if (dragDisable) {
                    mPlayer.getControllView().getProgressBar().setEnabled(false);
                }
                boolean autoClose = jsonObject.optBoolean("autoClose");
                mPlayer.setAutoCloseOnComplete(autoClose);

                boolean looping = jsonObject.optBoolean("looping");
                mPlayer.setLooping(looping);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String componentsClass = getIntent().getStringExtra("components");
        if (!TextUtils.isEmpty(componentsClass)) {
            try {
                List<MedPlayerComponent> components = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(componentsClass);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Class<MedPlayerComponent> cls = (Class<MedPlayerComponent>) Class.forName(jsonArray.optString(i));
                    components.add(cls.newInstance());
                }
                if (!components.isEmpty()) {
                    medPlayerComponent = new MedPlayerComponentProxy(components);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (null != medPlayerComponent) {
            MedVideoInfo medVideoInfo = new MedVideoInfo();
            medVideoInfo.title = mTitle;
            medVideoInfo.url = mVideoPath;
            medVideoInfo.coverUrl = mCover;
            medVideoInfo.extraInfo = mExtra;

            medPlayerComponent.onInited(this, mPlayer, medVideoInfo);
        }
        mPlayer.setPlayComponent(medPlayerComponent);
        mPlayer.setVideoPath(mVideoPath);
        mPlayer.setTitle(mTitle);
        if (mAutoPlay) {
            mPlayer.start();
        }
        if (null != medPlayerComponent) {
            medPlayerComponent.onActivityCreate(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlayer.onResume();
        if (null != medPlayerComponent) {
            medPlayerComponent.onActivityResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayer.onPause();
        if (null != medPlayerComponent) {
            medPlayerComponent.onActivityPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != medPlayerComponent) {
            medPlayerComponent.onActivityStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.onDestroy();
        if (null != medPlayerComponent) {
            medPlayerComponent.onActivityDestory();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MedPlayerStatusBarUtil.requestFullScreen(this);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            MedPlayerStatusBarUtil.quitFullScreen(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mPlayer.handleBackPressed()) {
            super.onBackPressed();
        }
    }
}
