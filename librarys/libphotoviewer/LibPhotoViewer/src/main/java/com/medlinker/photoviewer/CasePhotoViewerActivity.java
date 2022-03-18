package com.medlinker.photoviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medlinker.photoviewer.entity.CaseImageEntry;
import com.medlinker.photoviewer.widget.NavigationBar;

import java.util.ArrayList;
import java.util.List;


public class CasePhotoViewerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CasePhotoViewerActivity";

    private static final String EXTRA_IMAGES = "photoviewer_extra_images";
    private static final String EXTRA_INDEX = "photoviewer_extra_index";
    private static final String EXTRA_SOURCETYPE = "photoviewer_extra_sourceType";

    private TextView bottomOperateView;
    private NavigationBar mNavigationBar;

    private static final String MARK = "标注";
    private static final String HIDEMARK = "隐藏标注";
    private static final String SHOWMARK = "显示标注";

    private ArrayList<CaseImageEntry> images;
    private int currentPosition;

    private static final int IMAGE_EDITOR_REQUEST = 2000;
    public static final String NEW_IMAGE_DATA = "new_image_data";

    private int index;
    private boolean sourceType = true;

    private PictureViewerView pvv_view;

    public static void startPhotoViewerActivityForResult(Activity context, String images, int index, boolean sourceType, int requestCode) {
        Intent intent = new Intent(context, CasePhotoViewerActivity.class);
        intent.putExtra(EXTRA_IMAGES, images);
        intent.putExtra(EXTRA_INDEX, index);
        intent.putExtra(EXTRA_SOURCETYPE, sourceType);
        if (sourceType) {
            context.startActivity(intent);
        } else {
            context.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_case_photoview_pager);

        mNavigationBar = findViewById(R.id.base_navigation_bar);
        mNavigationBar.setLeftIcon(R.mipmap.icon_naviback_gray);
        mNavigationBar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pvv_view = findViewById(R.id.pvv_view);

        bottomOperateView = (TextView) findViewById(R.id.text_operate);
        bottomOperateView.setOnClickListener(this);
        Intent intent = getIntent();
        final String imagesStr = intent.getStringExtra(EXTRA_IMAGES);
        index = intent.getIntExtra(EXTRA_INDEX, 0);
        sourceType = intent.getBooleanExtra(EXTRA_SOURCETYPE, true);
        if (TextUtils.isEmpty(imagesStr)) {
            finish();
        }
        images = new Gson().fromJson(imagesStr, new TypeToken<List<CaseImageEntry>>() {
        }.getType());

        if (null == images || images.isEmpty()) {
            mNavigationBar.setTitle("images data is empty!!!");
            finish();
            return;
        }


        if (sourceType) {
            mNavigationBar.setRightButtonEnable(false);
            mNavigationBar.setRightText("");
        } else {
            mNavigationBar.setRightVisible();
            mNavigationBar.setRightText(R.string.delete);
        }
        mNavigationBar.getTitle().setGravity(Gravity.CENTER);
        mNavigationBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null == images) {
                    finish();
                } else if (images.size() > currentPosition) {
                    images.remove(currentPosition);
                    pvv_view.remove(currentPosition);
                }

                if (images.size() < 1) {
                    finish();
                } else {
                    int position = currentPosition > 0 ? currentPosition - 1 : currentPosition;
                    mNavigationBar.setTitle(getString(R.string.image_index, position + 1, images.size()));
                    pvv_view.setCurrentItem(position);
                }
            }
        });
        setupViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.fade_out);
    }


    private void setupViews() {

        mNavigationBar.setTitle(getString(R.string.image_index, index + 1, images.size()));
        currentPosition = (index >= images.size() || index <= 0) ? 0 : index;

        //页面切换
        pvv_view.setPageChangeListener(position -> {
            currentPosition = position;
            mNavigationBar.setTitle(getString(R.string.image_index, position + 1, images.size()));
            initBottomMark(position);
        });

        //初始化
        pvv_view.initData(currentPosition, images.size(), new PictureViewerView.OnImageDataListener() {
            @Override
            public String onInitImageUrl(int position) {
                final String image;
                //开始自定义bean处理
                CaseImageEntry entity = images.get(position);
                String markUrl = entity.tagFileUrl;
                String originUrl = entity.originPath;
                if (!TextUtils.isEmpty(markUrl)) {
                    image = markUrl;
                } else {
                    image = originUrl;
                }
                //最终给控件String类型的图片地址
                return image;
            }
        });

        //设置底部默认弹框item点击回调
        pvv_view.setDefaultItems(new PictureViewerView.OnDefaultItemClickListener() {
            @Override
            public void onSaveImage(String url) {
                Toast.makeText(getApplicationContext(), "保存图片", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onScanQrcode(String scanResult) {
                Toast.makeText(getApplicationContext(), "二维码扫码结果", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "取消", Toast.LENGTH_LONG).show();
            }
        });

        initBottomMark(currentPosition);
    }

    private void initBottomMark(int pos) {
        CaseImageEntry entity = images.get(pos);
        bottomOperateView.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(entity.tagFileUrl)) {
            if (sourceType) {
                bottomOperateView.setVisibility(View.GONE);
            } else {
                bottomOperateView.setText(MARK);
            }
        } else {
            if (sourceType && !entity.tagFileUrl.equals(entity.originPath)) {
                if (entity.state == 0) {
                    bottomOperateView.setText(HIDEMARK);
                } else {
                    bottomOperateView.setText(SHOWMARK);
                }
            } else {
                bottomOperateView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        TextView v = (TextView) view;
        String s = v.getText().toString();

        CaseImageEntry entity = images.get(currentPosition);
        if (MARK.equals(s)) {
            //TODO 接口跳转
            //IMGEditActivity.startActivity(this, Uri.parse(entity.originPath),IMAGE_EDITOR_REQUEST);
        } else if (HIDEMARK.equals(s)) {
            bottomOperateView.setText(SHOWMARK);
            imageTranslate(entity.originPath);
            entity.state = 1;
        } else if (SHOWMARK.equals(s)) {
            bottomOperateView.setText(HIDEMARK);
            imageTranslate(entity.tagFileUrl);
            entity.state = 0;
        } else {

        }
    }

    private void imageTranslate(String url) {
        pvv_view.reloadUrl(url, currentPosition);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_EDITOR_REQUEST) {
//            String newPath = data.getStringExtra(IMGEditActivity.EXTRA_IMAGE_SAVE_PATH);
//            File file = new File(newPath);
//            if (file.exists()) {
//                imageTranslate(newPath);
//                bottomOperateView.setVisibility(View.GONE);
//            }
//            if (images.size() >= currentPosition) {
//                CaseImageEntry entry = images.get(currentPosition);
//                entry.tagFileId = "";
//                entry.tagFileUrl = Uri.fromFile(new File(newPath)).toString();
//            }
        }
    }


    @Override
    public void finish() {
        if (!sourceType) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(NEW_IMAGE_DATA, images);
            setResult(RESULT_OK, intent);
        }
        super.finish();
    }
}
