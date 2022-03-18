package com.doctorwork.android.logreport.http;

import android.util.Log;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : HuBoChao
 * @date : 2020/12/1
 * @desc : 请求线程管理
 */
public class ThreadManager {
    private static final String TAG = "ThreadManager";

    private static ThreadManager threadManager;
    private ThreadPoolExecutor threadPoolExecutor;
    private LinkedBlockingQueue<Runnable> exeQueue;
    private DelayQueue<HttpTask> retryQueue;

    /**
     * 执行线程
     */
    private Runnable exeRun = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Runnable httpTask = exeQueue.take();
                    threadPoolExecutor.execute(httpTask);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 重试线程
     */
    private Runnable retryRun = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    HttpTask httpTask = retryQueue.take();
                    if (httpTask.getRetryCount() < HttpTool.getInstance().getRetryCount()) {
                        httpTask.setRetryCount(httpTask.getRetryCount() + 1);
                        threadPoolExecutor.execute(httpTask);
                        Log.d(TAG, "重试机制，当前次数" + httpTask.getRetryCount());
                    } else {
                        httpTask.getiHttpBean().getListener().onFail();
                    }
                } catch (Exception e) {

                }
            }
        }
    };

    private ThreadManager() {
        threadPoolExecutor = new ThreadPoolExecutor(3, 10, 15, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(5), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                exeQueue.add(r);
            }
        });
        exeQueue = new LinkedBlockingQueue<>();
        retryQueue = new DelayQueue<>();
        threadPoolExecutor.execute(exeRun);
        threadPoolExecutor.execute(retryRun);
    }

    public static ThreadManager getInstance() {
        if (threadManager == null) {
            threadManager = new ThreadManager();
        }
        return threadManager;
    }

    public void addExe(Runnable runnable) {
        if (runnable != null) {
            exeQueue.add(runnable);
        }
    }

    public void addRetry(HttpTask httpTask) {
        if (httpTask != null) {
            httpTask.setDelayTime(2000);
            retryQueue.offer(httpTask);
        }
    }
}
