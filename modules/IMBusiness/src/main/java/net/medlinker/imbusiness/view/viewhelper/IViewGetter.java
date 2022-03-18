package net.medlinker.imbusiness.view.viewhelper;


import android.view.View;

/**
 * Created by heaven7 on 2016/9/2.
 */
public interface IViewGetter<T extends View> {

    void onGotView(T view , ViewHelper vp);
}
