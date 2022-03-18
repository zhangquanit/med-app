package com.medlinker.app.business;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.medlinker.video.entity.VideoRoomEntity;
import com.medlinker.video.entity.VideoSessionBean;

/**
 * @author hmy
 * @time 12/11/21 15:21
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        val intent = Intent(this, VideoDataLoadingActivity::class.java)
//        val param = VideoCallIntentEntity()
//        param.userId = "xxxx"
//        param.userType = 2
//        param.roomId = 0
//        intent.putExtra("DATA_KEY", param)
//        startActivity(intent)

//
//        VideoRoomEntity entity = new VideoRoomEntity();
//        VideoSessionBean sessionBean = new VideoSessionBean();
//        sessionBean.setSessionId("1111");
//        sessionBean.setDuration(50);
//        entity.setSession(sessionBean);
//
//        Intent intent = new Intent(this, TestActivity.class);
//        intent.putExtra("DATA_KEY", entity);
//
//        startActivity(intent);
    }
}
