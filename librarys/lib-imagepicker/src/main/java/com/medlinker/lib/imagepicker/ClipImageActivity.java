package com.medlinker.lib.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import com.medlinker.widget.navigation.CommonNavigationBar;
import com.medlinker.lib.imagepicker.utils.PictureUtil;
import com.medlinker.lib.imagepicker.widget.ClipImageView;
import com.medlinker.lib.utils.MedImmersiveModeUtil;
import com.medlinker.lib.utils.MedPictureUtil;
import com.medlinker.lib.utils.MedToastUtil;

import net.medlinker.base.base.BaseActivity;

import java.io.File;
import java.io.IOException;

/**
 * @Description: 图片裁剪
 * @author: lichaofeng
 * @since: V3.3
 * @copyright © Medlinker
 * @Date: 2016/5/25 14:41
 */
public class ClipImageActivity extends BaseActivity implements View.OnClickListener {

    private ClipImageView mClipImageView;

    private CommonNavigationBar mCommonNavigationBar;

    // 图片保存路径
    private String mSavePath;
    // 图片原始路径
    private String mPath;
    // 图片最大尺寸
    private int mMaxWidth = 800;

    // 图片被旋转的角度
    private int mDegree;
    // 大图被设置之前的缩放比例
    private int mSampleSize;
    private int mSourceWidth;
    private int mSourceHeight;
    // 裁剪框的宽度
    private int mBorderWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_pick_clip_image);
        MedImmersiveModeUtil.setStatusBarTransparent(this);
        MedImmersiveModeUtil.setStatusBarDarkMode(this, false);

        mClipImageView = (ClipImageView) findViewById(R.id.clip_image_view);

       /* ImageView mLeftIcon = (ImageView) findViewById(R.id.left_icon);
        TextView mRightText = (TextView) findViewById(R.id.right_text);
        mLeftIcon.setOnClickListener(this);
        mRightText.setOnClickListener(this);
        mRightText.setEnabled(false);*/
        mCommonNavigationBar=findViewById(R.id.commonNavigationBar);
        mCommonNavigationBar
                .showBackWhiteIcon(v -> {
                    onBackPressed();
                })
                .setRightTextColor(R.color.navigation_text_white_selector)
                .showRightText(R.string.pick_select,v -> {
                    clipImage();
                })
                .setRightTextEnable(false);



        Intent data = getIntent();
        if (data != null) {
            mPath = data.getStringExtra(PickerConstants.BUNDLE_DATA);
            int width = data.getIntExtra("clip_width", 0);
            int height = data.getIntExtra("clip_height", 0);
            if (width > 0 && height > 0) {
                mClipImageView.setClipScale(width, height);
            }
        }

        if (!TextUtils.isEmpty(mPath)) {
            File file = new File(mPath);
            if (file.exists()) {
                setImageAndClipParams(); //大图裁剪
                mCommonNavigationBar.setRightTextEnable(true);
                /*mRightText.setEnabled(true);*/
            }
        }

    }

    private void setImageAndClipParams() {
        mClipImageView.post(new Runnable() {
            @Override
            public void run() {
                mClipImageView.setMaxOutputWidth(mMaxWidth);
                mBorderWidth = mClipImageView.getClipBorder().width();

                setClipImage();
            }
        });
    }

    private void setClipImage() {
        mDegree = readPictureDegree(mPath);

        final boolean isRotate = (mDegree == 90 || mDegree == 270);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPath, options);

        mSourceWidth = options.outWidth;
        mSourceHeight = options.outHeight;

        // 如果图片被旋转，则宽高度置换
        int w = isRotate ? options.outHeight : options.outWidth;

        // 裁剪是宽高比例1:1，只考虑宽度情况，这里按border宽度的两倍来计算缩放。
        mSampleSize = findBestSample(w, mBorderWidth);
        options.inJustDecodeBounds = false;
        options.inSampleSize = mSampleSize;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        final Bitmap source = BitmapFactory.decodeFile(mPath, options);

        // 解决图片被旋转的问题
        Bitmap target;
        if (mDegree == 0) {
            target = source;
        } else {
            final Matrix matrix = new Matrix();
            matrix.postRotate(mDegree);
            target = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
            if (target != source && !source.isRecycled()) {
                source.recycle();
            }
        }
        mClipImageView.setImageBitmap(target);
    }


    /**
     * 计算最好的采样大小。
     *
     * @param origin 当前宽度
     * @param target 限定宽度
     * @return sampleSize
     */
    private static int findBestSample(int origin, int target) {
        int sample = 1;
        for (int out = origin / 2; out > target; out /= 2) {
            sample *= 2;
        }
        return sample;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    @Override
    public void onClick(View v) {
       /* final int id = v.getId();
        if (id == R.id.left_icon) {
            onBackPressed();
        } else if (id == R.id.right_text) {
            clipImage();
        }*/
    }

    /**
     * 裁剪图片
     */
    private void clipImage() {
        //showDialogLoading();
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Bitmap bitmap = createClippedBitmap();
                    mSavePath = MedPictureUtil.saveImgToTemp(bitmap, PictureUtil.getTempFilePath());
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    Intent intent = new Intent();
                    intent.putExtra(PickerConstants.BUNDLE_PARAMS, mSavePath);
                    setResult(Activity.RESULT_OK, intent);
                } catch (Exception e) {
                    MedToastUtil.showMessage(mContext, R.string.pick_clip_image_failed);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
               // hideDialogLoading();
                finish();
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private Bitmap createClippedBitmap() {
        if (mSampleSize <= 1) {
            return mClipImageView.clip();
        }

        // 获取缩放位移后的矩阵值
        final float[] matrixValues = mClipImageView.getClipMatrixValues();
        final float scale = matrixValues[Matrix.MSCALE_X];
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        // 获取在显示的图片中裁剪的位置
        final Rect border = mClipImageView.getClipBorder();
        final float cropX = ((-transX + border.left) / scale) * mSampleSize;
        final float cropY = ((-transY + border.top) / scale) * mSampleSize;
        final float cropWidth = (border.width() / scale) * mSampleSize;
        final float cropHeight = (border.height() / scale) * mSampleSize;

        // 获取在旋转之前的裁剪位置
        final RectF srcRect = new RectF(cropX, cropY, cropX + cropWidth, cropY + cropHeight);
        final Rect clipRect = getRealRect(srcRect);

        final BitmapFactory.Options ops = new BitmapFactory.Options();
        final Matrix outputMatrix = new Matrix();

        outputMatrix.setRotate(mDegree);
        // 如果裁剪之后的图片宽高仍然太大,则进行缩小
        if (mMaxWidth > 0 && cropWidth > mMaxWidth) {
            ops.inSampleSize = findBestSample((int) cropWidth, mMaxWidth);

            final float outputScale = mMaxWidth / (cropWidth / ops.inSampleSize);
            outputMatrix.postScale(outputScale, outputScale);
        }

        // 裁剪
        BitmapRegionDecoder decoder = null;
        try {
            decoder = BitmapRegionDecoder.newInstance(mPath, false);
            final Bitmap source = decoder.decodeRegion(clipRect, ops);
            recycleImageViewBitmap();
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), outputMatrix, false);
        } catch (Exception e) {
            return mClipImageView.clip();
        } finally {
            if (decoder != null && !decoder.isRecycled()) {
                decoder.recycle();
            }
        }
    }

    private Rect getRealRect(RectF srcRect) {
        switch (mDegree) {
            case 90:
                return new Rect((int) srcRect.top, (int) (mSourceHeight - srcRect.right),
                        (int) srcRect.bottom, (int) (mSourceHeight - srcRect.left));
            case 180:
                return new Rect((int) (mSourceWidth - srcRect.right), (int) (mSourceHeight - srcRect.bottom),
                        (int) (mSourceWidth - srcRect.left), (int) (mSourceHeight - srcRect.top));
            case 270:
                return new Rect((int) (mSourceWidth - srcRect.bottom), (int) srcRect.left,
                        (int) (mSourceWidth - srcRect.top), (int) srcRect.right);
            default:
                return new Rect((int) srcRect.left, (int) srcRect.top, (int) srcRect.right, (int) srcRect.bottom);
        }
    }

    private void recycleImageViewBitmap() {
        mClipImageView.post(new Runnable() {
            @Override
            public void run() {
                mClipImageView.setImageBitmap(null);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
