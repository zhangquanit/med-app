package com.medlinker.video.presenter;

import com.medlinker.video.entity.TRTCVideoStream;
import com.medlinker.video.entity.VideoRoomEntity;
import com.medlinker.video.widget.TRTCVideoLayoutManager;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;

import java.util.ArrayList;

/**
 * @author hmy
 */
public class VideoCallPresenter {
    final static int DEFAULT_BITRATE = 600;
    final static int DEFAULT_FPS = 15;

    private TRTCCloud mTRTCCloud;
    private VideoRoomEntity mIntentData;
    private TRTCCloudDef.TRTCParams mTRTCParams; // 进房参数
    private int mAppScene = TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL; // 推流模式，文件头第三点注释
    private IVideoCallView mView;
    /**
     * 自定义采集和渲染相关
     */
    private boolean mIsCustomCaptureAndRender = false;  // 是否使用外部采集和渲染
    private boolean mIsEnableVideo, mIsEnableAudio; // 是否开启视频\音频上行
    /**
     * 混流配置相关
     */
    private ArrayList<TRTCVideoStream> mTRTCVideoStreams;          // 记录当前视频上行（大画面、辅路）的信息，用于配置混流参数

    public VideoCallPresenter(IVideoCallView iView) {
        mView = iView;
    }

    public void initTRTC(TRTCCloud trtcCloud, VideoRoomEntity intentEntity) {
        mTRTCVideoStreams = new ArrayList<>();
        mTRTCCloud = trtcCloud;
        mIntentData = intentEntity;
        mTRTCParams = new TRTCCloudDef.TRTCParams(mIntentData.getSdkAppId(),
                mIntentData.getUserId(), mIntentData.getUserSig(), mIntentData.getRoomId(), "", "");
        mTRTCParams.role = TRTCCloudDef.TRTCRoleAnchor;
        // 初始化的时候，确定是否开启自定义采集
        mTRTCCloud.enableCustomVideoCapture(mIsCustomCaptureAndRender);
    }

    public TRTCCloudDef.TRTCParams getTRTCParams() {
        return mTRTCParams;
    }

    /**
     * 进房
     */
    public void enterRoom(String recordId) {
        // 是否为自采集，请在调用 SDK 相关配置前优先设置好，避免时序导致的异常问题。
        mTRTCCloud.enableCustomVideoCapture(mIsCustomCaptureAndRender);
        // 是否开启音量回调 需要在 startLocalAudio 之前调用
        enableAudioVolumeEvaluation(true);
        // 开启本地预览
        startLocalPreview(true);
        mIsEnableVideo = true;
        mTRTCCloud.startLocalAudio();
        mIsEnableAudio = true;
        // 设置美颜参数
        mTRTCCloud.setBeautyStyle(TRTCCloudDef.TRTC_BEAUTY_STYLE_SMOOTH, 5, 5, 5);

        // 设置视频渲染模式
        setVideoFillMode(true);
        // 设置视频旋转角
        setVideoRotation(true);
        // 是否开启免提
        enableAudioHandFree(true);
        // 是否开启重力感应
        enableGSensor(false);
        // 是否开启推流画面镜像
        enableVideoEncMirror(false);
        // 设置本地画面是否镜像预览
        setLocalViewMirror(TRTCCloudDef.TRTC_VIDEO_MIRROR_TYPE_AUTO);
        // 【关键】设置 TRTC 推流参数
        setTRTCCloudParam();

        mTRTCParams.userDefineRecordId = recordId;
        // 【关键】 TRTC进房
        mTRTCCloud.enterRoom(mTRTCParams, mAppScene);
    }

    public void setMixTranscodingConfig() {
        TRTCCloudDef.TRTCTranscodingConfig config = new TRTCCloudDef.TRTCTranscodingConfig();
        config.videoWidth = 720;
        config.videoHeight = 1280;
        config.videoBitrate = 1500;
        config.videoFramerate = 20;
        config.videoGOP = 2;
        config.audioSampleRate = 48000;
        config.audioBitrate = 64;
        config.audioChannels = 2;
        // 采用预排版模式
        config.mode = TRTCCloudDef.TRTC_TranscodingConfigMode_Template_PresetLayout;
        config.mixUsers = new ArrayList<>();
        // 主播摄像头的画面位置
        TRTCCloudDef.TRTCMixUser local = new TRTCCloudDef.TRTCMixUser();
        local.userId = "$PLACE_HOLDER_LOCAL_MAIN$";
        local.zOrder = 0;   // zOrder 为0代表主播画面位于最底层

        int[] mainParam = mView.getMainVideoParam();
        local.x = mainParam[0];
        local.y = mainParam[1];
        local.width = mainParam[2];
        local.height = mainParam[3];
        local.roomId = null; // 本地用户不用填写 roomID，远程需要
        config.mixUsers.add(local);

        TRTCCloudDef.TRTCMixUser remote1 = new TRTCCloudDef.TRTCMixUser();
        remote1.userId = "$PLACE_HOLDER_REMOTE$";
        remote1.zOrder = 1;
        remote1.roomId = String.valueOf(mIntentData.getRoomId());
        int[] remoteParam = mView.getRemoteVideoParam();
        remote1.x = remoteParam[0];
        remote1.y = remoteParam[1];
        remote1.width = remoteParam[2];
        remote1.height = remoteParam[3];
        config.mixUsers.add(remote1);

        mTRTCCloud.setMixTranscodingConfig(config);
    }

    /**
     * 退房
     */
    public void exitRoom() {
        startLocalPreview(false);
        if (mTRTCCloud != null) {
            mTRTCCloud.exitRoom();
        }
    }

    /**
     * 更新混流参数
     */
    public void updateCloudMixtureParams() {
        // 背景大画面宽高
        int videoWidth = 720;
        int videoHeight = 1280;

        // 小画面宽高
        int subWidth = 180;
        int subHeight = 320;

        int offsetX = 5;
        int offsetY = 50;

        int bitrate = 200;
        TRTCCloudDef.TRTCTranscodingConfig config = new TRTCCloudDef.TRTCTranscodingConfig();
        ///【字段含义】腾讯云直播 AppID
        ///【推荐取值】请在 [实时音视频控制台](https://console.cloud.tencent.com/rav) 选择已经创建的应用，单击【帐号信息】后，在“直播信息”中获取
        config.appId = mIntentData.getLiveAppId();
        ///【字段含义】腾讯云直播 bizid
        ///【推荐取值】请在 [实时音视频控制台](https://console.cloud.tencent.com/rav) 选择已经创建的应用，单击【帐号信息】后，在“直播信息”中获取
        config.bizId = mIntentData.getLiveBizId();
        config.videoWidth = videoWidth;
        config.videoHeight = videoHeight;
        config.videoGOP = 1;
        config.videoFramerate = 15;
        config.videoBitrate = bitrate;
        config.audioSampleRate = 48000;
        config.audioBitrate = 64;
        config.audioChannels = 1;

        // 设置混流后主播的画面位置
        TRTCCloudDef.TRTCMixUser mixUser = new TRTCCloudDef.TRTCMixUser();
        mixUser.userId = mTRTCParams.userId; // 以主播uid为broadcaster为例
        mixUser.zOrder = 0;
        mixUser.x = 0;
        mixUser.y = 0;
        mixUser.width = videoWidth;
        mixUser.height = videoHeight;

        config.mixUsers = new ArrayList<>();
        config.mixUsers.add(mixUser);

        boolean isEnableCloudMixture = true;
        // 设置混流后各个小画面的位置
        if (isEnableCloudMixture) {
            int index = 0;
            for (TRTCVideoStream userStream : mTRTCVideoStreams) {
                TRTCCloudDef.TRTCMixUser _mixUser = new TRTCCloudDef.TRTCMixUser();

                _mixUser.userId = userStream.userId;
                _mixUser.streamType = userStream.streamType;
                _mixUser.zOrder = 1 + index;
                if (index < 3) {
                    // 前三个小画面靠右从下往上铺
                    _mixUser.x = videoWidth - offsetX - subWidth;
                    _mixUser.y = videoHeight - offsetY - index * subHeight - subHeight;
                    _mixUser.width = subWidth;
                    _mixUser.height = subHeight;
                } else if (index < 6) {
                    // 后三个小画面靠左从下往上铺
                    _mixUser.x = offsetX;
                    _mixUser.y = videoHeight - offsetY - (index - 3) * subHeight - subHeight;
                    _mixUser.width = subWidth;
                    _mixUser.height = subHeight;
                } else {
                    // 最多只叠加六个小画面
                }
                config.mixUsers.add(_mixUser);
                ++index;
            }
        }
        mTRTCCloud.setMixTranscodingConfig(config);
    }

    /**
     * 开启\关闭本地预览
     *
     * @param enable
     */
    private void startLocalPreview(boolean enable) {
        try {
            TRTCVideoLayoutManager layoutManager = mView.getTRTCVideoLayoutManager();
            if (enable) {
                TXCloudVideoView localVideoView = layoutManager.allocCloudVideoView(
                        getTRTCParams().userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
                // 获取一个当前空闲的 View
                if (localVideoView != null) {
                    mTRTCCloud.startLocalPreview(true, localVideoView);
                }
            } else {
                mTRTCCloud.stopLocalPreview();
                layoutManager.recyclerCloudViewView(getTRTCParams().userId,
                        TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否开启音量回调
     *
     * @param bEnable
     */
    private void enableAudioVolumeEvaluation(boolean bEnable) {
        if (bEnable) {
            mTRTCCloud.enableAudioVolumeEvaluation(300);
        } else {
            mTRTCCloud.enableAudioVolumeEvaluation(0);
        }
    }

    /**
     * 设置本地渲染模式：全屏铺满\自适应
     *
     * @param bFillMode
     */
    private void setVideoFillMode(boolean bFillMode) {
        if (bFillMode) {
            // 全屏铺满模式
            mTRTCCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
        } else {
            // 自适应模式
            mTRTCCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FIT);
        }
    }

    /**
     * 设置竖屏\横屏推流
     *
     * @param bVertical
     */
    private void setVideoRotation(boolean bVertical) {
        if (bVertical) {
            // 竖屏直播
            mTRTCCloud.setLocalViewRotation(TRTCCloudDef.TRTC_VIDEO_ROTATION_0);
        } else {
            // 横屏直播
            mTRTCCloud.setLocalViewRotation(TRTCCloudDef.TRTC_VIDEO_ROTATION_90);
        }
    }

    /**
     * 是否开启免提
     *
     * @param bEnable
     */
    private void enableAudioHandFree(boolean bEnable) {
        if (bEnable) {
            mTRTCCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_SPEAKER);
        } else {
            mTRTCCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_EARPIECE);
        }
    }

    /**
     * 是否开启重力该应
     *
     * @param bEnable
     */
    private void enableGSensor(boolean bEnable) {
        if (bEnable) {
            mTRTCCloud.setGSensorMode(TRTCCloudDef.TRTC_GSENSOR_MODE_UIFIXLAYOUT);
        } else {
            mTRTCCloud.setGSensorMode(TRTCCloudDef.TRTC_GSENSOR_MODE_DISABLE);
        }
    }

    /**
     * 是否开启画面镜像推流
     * <p>
     * 开启后，画面将会进行左右镜像，推到远端
     *
     * @param bMirror
     */
    private void enableVideoEncMirror(boolean bMirror) {
        mTRTCCloud.setVideoEncoderMirror(bMirror);
    }

    /**
     * 是否开启本地画面镜像
     */
    private void setLocalViewMirror(int mode) {
        mTRTCCloud.setLocalViewMirror(mode);
    }


    /**
     * 设置视频通话的视频参数：需要 TRTCSettingDialog 提供的分辨率、帧率和流畅模式等参数
     */
    private void setTRTCCloudParam() {
        // 大画面的编码器参数设置
        // 设置视频编码参数，包括分辨率、帧率、码率等等，这些编码参数来自于 TRTCSettingDialog 的设置
        // 注意（1）：不要在码率很低的情况下设置很高的分辨率，会出现较大的马赛克
        // 注意（2）：不要设置超过25FPS以上的帧率，因为电影才使用24FPS，我们一般推荐15FPS，这样能将更多的码率分配给画质
        TRTCCloudDef.TRTCVideoEncParam encParam = new TRTCCloudDef.TRTCVideoEncParam();
        encParam.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360;
        encParam.videoFps = DEFAULT_FPS;
        encParam.videoBitrate = DEFAULT_BITRATE;
        encParam.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT;
        mTRTCCloud.setVideoEncoderParam(encParam);

        TRTCCloudDef.TRTCNetworkQosParam qosParam = new TRTCCloudDef.TRTCNetworkQosParam();
        qosParam.controlMode = TRTCCloudDef.VIDEO_QOS_CONTROL_SERVER;
        qosParam.preference = TRTCCloudDef.TRTC_VIDEO_QOS_PREFERENCE_CLEAR;
        mTRTCCloud.setNetworkQosParam(qosParam);

        //小画面的编码器参数设置
        //TRTC SDK 支持大小两路画面的同时编码和传输，这样网速不理想的用户可以选择观看小画面
        //注意：iPhone & Android 不要开启大小双路画面，非常浪费流量，大小路画面适合 Windows 和 MAC 这样的有线网络环境
        TRTCCloudDef.TRTCVideoEncParam smallParam = new TRTCCloudDef.TRTCVideoEncParam();
        smallParam.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_160_90;
        smallParam.videoFps = DEFAULT_FPS;
        smallParam.videoBitrate = 100;
        smallParam.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT;

        mTRTCCloud.enableEncSmallVideoStream(false, smallParam);
        mTRTCCloud.setPriorRemoteVideoStreamType(TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
    }


    /**
     * 开始\停止 SDK 渲染
     *
     * @param userId
     * @param enable
     */
    public void startSDKRender(String userId, boolean enable, int streamType) {
        if (mView == null) {
            return;
        }
        TRTCVideoLayoutManager videoLayoutManager = mView.getTRTCVideoLayoutManager();
        if (enable) {
            TXCloudVideoView renderView = videoLayoutManager.findCloudViewView(userId, streamType);
            if (renderView == null)
                renderView = videoLayoutManager.allocCloudVideoView(userId, streamType);
            // 启动远程画面的解码和显示逻辑，FillMode 可以设置是否显示黑边
            if (renderView != null) {
                // 设置日志调试窗口的边距
                mTRTCCloud.setDebugViewMargin(userId, new TRTCCloud.TRTCViewMargin(0.0f, 0.0f, 0.1f, 0.0f));
                //  TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FIT模式下，当 View 的宽高比与视频宽高此不一致时，有黑边。
                //  若您想铺满可以使用 TRTC_VIDEO_RENDER_MODE_FILL
                // 启动渲染
                if (streamType == TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG) {
                    mTRTCCloud.setRemoteViewFillMode(userId, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FIT);
                    mTRTCCloud.startRemoteView(userId, renderView);
                } else if (streamType == TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SUB) {
                    mTRTCCloud.setRemoteSubStreamViewFillMode(userId, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FIT);
                    mTRTCCloud.startRemoteSubStreamView(userId, renderView);
                }
            }
        } else {
            // 停止渲染
            if (streamType == TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG) {
                mTRTCCloud.stopRemoteView(userId);
            } else if (streamType == TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SUB) {
                mTRTCCloud.stopRemoteSubStreamView(userId);
                // 辅路直接移除画面，不会更新状态。主流需要更新状态，所以保留
                videoLayoutManager.recyclerCloudViewView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SUB);
            }
        }
    }

    public void removeVideoStream(String userId, int streamType) {
        int indexRemove = -1;
        for (int i = 0; i < mTRTCVideoStreams.size(); i++) {
            TRTCVideoStream stream = mTRTCVideoStreams.get(i);
            if (stream != null && stream.userId != null && stream.userId.equals(userId) && stream.streamType == streamType) {
                indexRemove = i;
                break;
            }
        }
        if (indexRemove != -1) {
            mTRTCVideoStreams.remove(indexRemove);
        }
    }

    public boolean isCustomCaptureAndRender() {
        return mIsCustomCaptureAndRender;
    }

    public boolean isContainVideoStream(String userId, int streamType) {
        for (TRTCVideoStream stream : mTRTCVideoStreams) {
            if (stream != null && stream.userId != null && stream.userId.equals(userId) && stream.streamType == streamType) {
                return true;
            }
        }
        return false;
    }

    public void switchCamera() {
        mTRTCCloud.switchCamera();
    }

    public ArrayList<TRTCVideoStream> getTRTCVideoStreams() {
        if (mTRTCVideoStreams == null) {
            mTRTCVideoStreams = new ArrayList<>();
        }
        return mTRTCVideoStreams;
    }

}
