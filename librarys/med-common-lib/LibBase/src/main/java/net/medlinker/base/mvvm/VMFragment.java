package net.medlinker.base.mvvm;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import net.medlinker.base.base.BaseFragment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author: pengdaosong
 * @CreateTime: 2020-09-17 15:22
 * @Email：pengdaosong@medlinker.com
 * @Description:
 */
public abstract class VMFragment<VM extends BaseViewModel> extends BaseFragment {

    protected VM mViewModel;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVM();
    }

    private void initVM() {
        Type superClassType = getClass().getGenericSuperclass();
        if (superClassType instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) superClassType).getActualTypeArguments();
            try {
                Class<VM> presenterClassType = (Class<VM>) types[0];
                mViewModel = new ViewModelProvider(this).get(presenterClassType);
                getLifecycle().addObserver(mViewModel);
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
