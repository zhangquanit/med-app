package com.medlinker.lib.permission.ext;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.BarUtils;
import com.medlinker.lib.permission.R;

import org.jetbrains.annotations.NotNull;

/**
 * 权限说明弹框
 * @author zhangquan
 */
public class MedPermissionExplainDialog extends Dialog {
    private MedPermissionInfo mInfo;

    public MedPermissionExplainDialog(@NotNull Context context, MedPermissionInfo info) {
        this(context, R.style.permission_dialogTransparent);
        this.mInfo = info;
    }

    public MedPermissionExplainDialog(@NonNull @NotNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permisson_expain_layout);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; //设置为屏幕宽度
        lp.height = WindowManager.LayoutParams.MATCH_PARENT; //设置为屏幕高度
//        lp.dimAmount = 0.3f;//外围遮罩透明度0.0f-1.0f
        lp.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | lp.flags);

        dialogWindow.setAttributes(lp);
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0); //设置边距

//        View container = findViewById(R.id.container);
//        container.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(mInfo.getTitle());
        TextView tv_content = findViewById(R.id.tv_content);
        tv_content.setText(mInfo.getContent());


    }
}
