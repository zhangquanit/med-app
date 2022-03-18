package com.medlinker.lib.pay.core;

import android.os.AsyncTask;

/**
 * Created by heaven7 on 2016/1/27.
 */
public interface ITaskManager {

    void reset();

    void addTask(AsyncTask task);

    void removeTask(AsyncTask task);
}
