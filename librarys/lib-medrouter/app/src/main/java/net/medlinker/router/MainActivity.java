package net.medlinker.router;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.medlinker.router.MedRouterHelper;

import net.medlinker.router.router.RoutePath;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test1(View view) {
        MedRouterHelper.navigation(RoutePath.MED_PATH1,this);
    }

    public void test2(View view) {
        MedRouterHelper.navigation(RoutePath.MED_PATH2,this);
    }

    public void test3(View view) {
        MedRouterHelper.navigation(RoutePath.AROUTER_PATH1,this);
    }

    public void test4(View view) {
        MedRouterHelper.navigation("/model/page1/page2",this);
    }

    public void test5(View view) {
        MedRouterHelper.navigation("/model2/page1",this);
    }
}