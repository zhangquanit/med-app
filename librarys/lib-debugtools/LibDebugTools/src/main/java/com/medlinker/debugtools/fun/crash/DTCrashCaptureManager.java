package com.medlinker.debugtools.fun.crash;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.didichuxing.doraemonkit.kit.core.AbsDokitView;
import com.didichuxing.doraemonkit.kit.core.DokitIntent;
import com.didichuxing.doraemonkit.kit.core.DokitViewManager;
import com.medlinker.debugtools.DTModule;

/**
 * Created by wanglikun on 2019-06-12
 * 系统崩溃异常捕获
 */
public class DTCrashCaptureManager implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashCaptureManager";

    private final Thread.UncaughtExceptionHandler mDefaultHandler;
    private final Handler mHandler;
    private Context mContext;

    private DTCrashCaptureManager() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    private static class Holder {
        private static final DTCrashCaptureManager INSTANCE = new DTCrashCaptureManager();
    }

    public static DTCrashCaptureManager getInstance() {
        return Holder.INSTANCE;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public void start() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void stop() {
        Thread.setDefaultUncaughtExceptionHandler(mDefaultHandler);
    }

    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        new Handler(Looper.getMainLooper()).post(()->{
            showCrashDialog(t,e);
        });
    }

    private void post(Runnable r) {
        mHandler.post(r);
    }

    private void showCrashDialog(final Thread t,final Throwable e) {
        DokitIntent intent = new DokitIntent(DTCrashCaptureView.class);
        intent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(intent);

        AbsDokitView absDokitView = DokitViewManager.getInstance().getDokitView(intent.activity,intent.getTag());
        if (absDokitView instanceof DTCrashCaptureView){
            DTCrashCaptureView dtCrashCaptureView = ((DTCrashCaptureView)absDokitView);
            dtCrashCaptureView.setContent(getStackTraceInfo(t,e));
            dtCrashCaptureView.setLeftOnClickListener(v -> hideCrashDialog());
            dtCrashCaptureView.setRightOnClickListener(v -> {
                hideCrashDialog();
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) DTModule.app().getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("dt_crash", dtCrashCaptureView.getContent());
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Toast.makeText(DTModule.app(),"已复制到剪贴板",Toast.LENGTH_SHORT).show();
                post(() -> {
                    if (mDefaultHandler != null) {
                        mDefaultHandler.uncaughtException(t, e);
                    }
                });
            });
        }
    }

    private void hideCrashDialog() {
        DokitViewManager.getInstance().detach(DTCrashCaptureView.class);
    }

    private String getStackTraceInfo(final Thread t,final Throwable e){
        StackTraceElement[]trace =  e.getStackTrace();
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement traceElement : trace)
            builder.append("\tat " + traceElement);
        return builder.toString();
    }
}