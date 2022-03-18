package com.medlinker.photoviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.medlinker.photoviewer.entity.FileEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 大图浏览界面
 *
 * @author jiantao
 * @date 2017/11/14
 */
public class PhotoViewerActivity extends AppCompatActivity {
    private static final String EXTRA_POSITION = "photoviewer_extra_position";
    private static final String EXTRA_IMAGES = "photoviewer_extra_images";
    private static final String TAG = PhotoViewerActivity.class.getSimpleName();
    private PictureViewerView pvv_view;

    /**
     *
     */
    public static void startPhotoViewerActivity(Context context, Collection<String> images, int position) {
        ArrayList<FileEntity> imageList;
        if (images != null) {
            imageList = new ArrayList<>(images.size());
            for (String image : images) {
                FileEntity fileEntity = new FileEntity();
                fileEntity.setFileUrl(image);
                imageList.add(fileEntity);
            }
        } else {
            imageList = null;
        }
        startPhotoViewerActivity(context, imageList, position);
    }

    /**
     *
     */
    public static void startPhotoViewerActivity(Context context, List<FileEntity> images, int position) {
        Intent intent = new Intent(context, PhotoViewerActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_IMAGES, new ArrayList<Parcelable>(images));
        intent.putExtra(EXTRA_POSITION, position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        pvv_view = findViewById(R.id.pvv_view);
        ArrayList<FileEntity> images = getIntent().getParcelableArrayListExtra(EXTRA_IMAGES);
        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);
        ArrayList<String> tt = new ArrayList<>();
        for (FileEntity image : images) {
            tt.add(image.getFileUrl());
        }
        pvv_view.initData(position, tt);

//        pvv_view.setItems(new String[]{"test1", "2", "cancel"}, new PictureViewerView.OnItemsClickListener() {
//            @Override
//            public void onItemClick(int index, String url) {
//                switch (index) {
//                    case 0:
//                        Toast.makeText(getApplicationContext(), "test1\n" + url, Toast.LENGTH_LONG).show();
//                        break;
//                    case 1:
//                        Toast.makeText(getApplicationContext(), "2\n" + url, Toast.LENGTH_LONG).show();
//                        break;
//                    case 2:
//                        Toast.makeText(getApplicationContext(), "取消\n" + url, Toast.LENGTH_LONG).show();
//                        break;
//                }
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (Exception e) {
            finish();
        }
        overridePendingTransition(0, R.anim.fade_out);
    }


}
