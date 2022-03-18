package net.medlinker.router;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;

import net.medlinker.router.router.RoutePath;

/**
 * @author zhangquan
 */
@Route(path = RoutePath.AROUTER_PATH1)
public class ArouterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        TextView textView=findViewById(R.id.textView);
        textView.setText("ArouterActivity");
    }
}
