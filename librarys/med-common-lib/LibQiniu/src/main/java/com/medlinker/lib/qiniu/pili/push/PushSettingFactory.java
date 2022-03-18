package com.medlinker.lib.qiniu.pili.push;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;
import com.qiniu.android.dns.local.AndroidDnsServer;
import com.qiniu.android.dns.local.Resolver;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting;
import com.qiniu.pili.droid.streaming.StreamingProfile;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 4.0
 * @description
 * @time 2016-08-17-10:38
 */
public class PushSettingFactory {


    /**
     * 初始化activity的信息
     *
     * @param activity
     */
    public static void initActivitySetting(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activity.requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        } else {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 语音相关对象创建
     *
     * @return
     */
    public static MicrophoneStreamingSetting getMicrophoneStreamingSetting() {
        MicrophoneStreamingSetting mMicrophoneStreamingSetting = new MicrophoneStreamingSetting();
        mMicrophoneStreamingSetting.setBluetoothSCOEnabled(false);
        return mMicrophoneStreamingSetting;
    }

    /**
     * 水印相关对象创建
     *
     * @param mContext
     * @return
     */
//    public static WatermarkSetting getwatermarksetting(Context mContext) {
//        WatermarkSetting watermarksetting = new WatermarkSetting(mContext);
//        watermarksetting.setResourceId(PackageUtil.getAppIcon())
//                .setAlpha(100)
//                .setSize(WatermarkSetting.WATERMARK_SIZE.SMALL)
//                .setCustomPosition(0.5f, 0.5f);
//        return watermarksetting;
//    }

    /**
     * 相机对象建立
     *
     * @return
     */
    public static CameraStreamingSetting getCameraStreamingSetting() {
        CameraStreamingSetting.CAMERA_FACING_ID cameraFacingId = chooseCameraFacingId();
        CameraStreamingSetting mCameraStreamingSetting = new CameraStreamingSetting();
        mCameraStreamingSetting.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
                .setContinuousFocusModeEnabled(true)
                .setRecordingHint(false)
                .setCameraFacingId(cameraFacingId)
                .setBuiltInFaceBeautyEnabled(true)
                .setResetTouchFocusDelayInMs(3000)
//                .setFocusMode(CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_PICTURE)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.SMALL)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9)
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.5f, 0.5f, 0.3f))
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY);
        return mCameraStreamingSetting;
    }

    /**
     * 推流信息流对象建立
     *
     * @return
     */
    public static StreamingProfile getStreamingProfile(Context ctx) {
        StreamingProfile mProfile = new StreamingProfile();
        StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 96 * 1024);
        StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(30, 1000 * 1024, 48);
        StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
        mProfile.setVideoQuality(StreamingProfile.VIDEO_ENCODING_HEIGHT_544)
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
//                .setPreferredVideoEncodingSize(720, 480)
                .setEncodingSizeLevel(Config.ENCODING_LEVEL)
                .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY)
                .setAVProfile(avProfile)
                .setDnsManager(getMyDnsManager(ctx))
                .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
//                .setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.PORT)
                .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000));
        return mProfile;
    }

    /**
     * dns校准
     *
     * @return
     */
    private static DnsManager getMyDnsManager(Context ctx) {
        IResolver r0 = new DnspodFree();
        IResolver r1 = AndroidDnsServer.defaultResolver(ctx);
        IResolver r2 = null;
        try {
            r2 = new Resolver(InetAddress.getByName("119.29.29.29"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new DnsManager(NetworkInfo.normal, new IResolver[]{r0, r1, r2});
    }

    /**
     * 得到目前选择的相机参数
     *
     * @return
     */
    public static CameraStreamingSetting.CAMERA_FACING_ID chooseCameraFacingId() {
        if (CameraStreamingSetting.hasCameraFacing(CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD)) {
            return CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD;
        } else if (CameraStreamingSetting.hasCameraFacing(CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT)) {
            return CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
        } else {
            return CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
        }
    }
}
