package net.medlinker.base.common;

import io.reactivex.Observable;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.operators.observable.ObservableFromIterable;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Author: AllenWen
 * CreateTime: 2017/11/6
 * Email: wenxueguo@medlinker.com
 * Description: 自定义的Observable，主要作用是对Iterable数据提供from方法
 * @author AllenWen
 */

public abstract class Observable2<T> extends Observable<T> {

    @CheckReturnValue
    @SchedulerSupport(SchedulerSupport.NONE)
    public static <T> Observable<T> from(Iterable<? extends T> source) {
        ObjectHelper.requireNonNull(source, "source is null");
        return RxJavaPlugins.onAssembly(new ObservableFromIterable<T>(source));
    }

}
