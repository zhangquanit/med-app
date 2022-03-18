package com.medlinker.lib.imagepicker.utils;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.medlinker.lib.utils.MedAppInfo;


import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/19 14:20
 */
public class ImageCaptureManager {

    private String mCurrentPhotoPath;
    private Context mContext;


    public static final int REQUEST_TAKE_PHOTO = 1;//相机拍照
    public final static int REQUEST_CODE_BIG_PIC = 2;//查看相册大图



    public ImageCaptureManager(Context mContext) {
        this.mContext = mContext;
    }

    private File createImageFile() throws IOException {
        File image=new File(FileUtil.getImagesDir(),UUID.randomUUID().toString()+".jpg");
        mCurrentPhotoPath = image.getPath();
        return image;
    }

    public Intent dispatchTakePictureIntent() throws IOException{
        return dispatchTakePictureIntent(-1);
    }

    public Intent dispatchTakePictureIntent(int faceType) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (faceType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // TODO: 2021/4/29 打开前置相机，目前不能打开系统相机的前置相机。后面考虑自己实现相机
        }
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                //----2017.3.6 modified by luhaiyang
                Uri uri = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(mContext, MedAppInfo.getApplicationId() +".provider",photoFile);
                }else{
                    uri = Uri.fromFile(photoFile);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                //----
            }
        }
        return takePictureIntent;
    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }



    public void galleryAddPic() {
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = null;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                contentUri = FileProvider.getUriForFile(mContext, MedAppInfo.getApplicationId().concat(".provider"), f);
                mediaScanIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }else{
                contentUri = Uri.fromFile(f);
            }
            mediaScanIntent.setData(contentUri);
            mContext.sendBroadcast(mediaScanIntent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setPhotoPath(String image) {
         mCurrentPhotoPath = image;
    }
}
