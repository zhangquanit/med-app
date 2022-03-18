package com.medlinker.lib.imagepicker;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.medlinker.lib.imagepicker.entity.ImageEntity;
import com.medlinker.lib.imagepicker.utils.ImageCaptureManager;
import com.medlinker.lib.permission.ext.MedPermissionUtil;


public class ClipCameraActivity extends ClipPhotoPickerActivity {

    private boolean mCaptureOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        isClipCamera = true;
        super.onCreate(savedInstanceState);
        mCaptureOnly = getIntent().getBooleanExtra("capture_only", false);

        new MedPermissionUtil(ClipCameraActivity.this).requestPermissions(Manifest.permission.CAMERA)
                .onResult((result) -> {
                    if (result) {
                        try {
                            Intent intent = captureManager.dispatchTakePictureIntent(mCameraId);
                            startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
                        } catch (Exception e) {
                            e.printStackTrace();
                            finish();
                        }
                    }else{
                        finish();
                    }
                    return null;
                });
    }

    @Override
    protected int getContentView() {
        return 0;
    }

    @Override
    protected void initView(Bundle bundle) {
        //do nothing
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, 0, data);
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }
        switch (requestCode) {
            case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                captureManager.galleryAddPic();
                if (mCaptureOnly) {
                    Intent intent = new Intent();
                    intent.putExtra(PickerConstants.BUNDLE_PARAMS, captureManager.getCurrentPhotoPath());
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                }
                ImageEntity path = new ImageEntity(captureManager.getCurrentPhotoPath());
                Intent clipIntent = new Intent(this, ClipImageActivity.class);
                clipIntent.putExtra(PickerConstants.BUNDLE_DATA, path.getFilePath());
                clipIntent.putExtra("clip_width", mClipWidth);
                clipIntent.putExtra("clip_height", mClipHeight);
                startActivityForResult(clipIntent, PickerConstants.REQUEST_CODE_CLIP_PIC);
                break;
            case PickerConstants.REQUEST_CODE_CLIP_PIC:
                String fileName = data.getStringExtra(PickerConstants.BUNDLE_PARAMS);
                Intent intent = new Intent();
                intent.putExtra(PickerConstants.BUNDLE_PARAMS, fileName);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
