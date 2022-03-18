package net.medlinker.base.mvvm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-17 15:29
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public abstract class MVVMFragment<VM extends BaseViewModel, DB extends ViewDataBinding> extends VMFragment<VM> {

    protected DB mVDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initDataBinding(inflater,container,savedInstanceState);
    }

    private View initDataBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Type superClassType = getClass().getGenericSuperclass();
        if (superClassType instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) superClassType).getActualTypeArguments();
            try {
                Class<DB> presenterClassType = (Class<DB>) types[1];
                if (ViewDataBinding.class != presenterClassType && ViewDataBinding.class.isAssignableFrom(presenterClassType)) {
                    mVDB = DataBindingUtil.inflate(inflater, layoutId(),container,false);
                    if (null != mVDB){
                        return mVDB.getRoot();
                    }
                }
            } catch (Exception e) {
                throw e;
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected abstract int layoutId();
}
