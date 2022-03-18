package com.medlinker.lib.imagepicker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.medlinker.widget.navigation.CommonNavigationBar;


import com.medlinker.lib.imagepicker.adapter.PhotoPagerAdapter;
import com.medlinker.lib.imagepicker.adapter.ShowWholePicAdapter;
import com.medlinker.lib.imagepicker.entity.ImageEntity;
import com.medlinker.lib.imagepicker.entity.PhotoDirectory;
import com.medlinker.lib.imagepicker.entity.SelectEvent;
import com.medlinker.lib.imagepicker.utils.MediaStoreHelper;
import com.medlinker.lib.utils.MedPictureUtil;

import java.util.ArrayList;
import java.util.List;


import static android.widget.Toast.LENGTH_LONG;
import static com.medlinker.lib.imagepicker.PickerConstants.SELECT_BROADCAST_ACTION;
import static com.medlinker.lib.imagepicker.PickerConstants.SELECT_EVENT;

/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述 .查看大图
 * @time 2015/10/26 15:57
 */
public class PhotoPagerPickerActivity extends BaseCompatActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private Context mContext;
    private int currentItem = 0;//当前图片
    // 图片文件夹索引
    private int directoriesIndex = 0;
    private List<ImageEntity> photos = new ArrayList<>();
    private ArrayList<ImageEntity> selectPhotos = new ArrayList<>();
    private ImageEntity mCurrentPhotoEntity;
    private ImageView mImageSelect;

    private ViewGroup mLayOriginal;
    private CheckBox mCbOriginal;


    // 图片文件夹索引 key
    public final static String EXTRA_DIRS_CURRENT_INDEX = "extra_dirs_current_index";
    public final static String EXTRA_CURRENT_ITEM_INDEX = "current_item_index";
    public final static String EXTRA_CURRENT_PHOTO = "current_photo";
    public final static String EXTRA_PHOTOS_SELECT = "photos_select";
    public final static String EXTRA_PHOTOS_SELECT_NUM = "photos_select_num";
    public final static int DEFAULT_MAX_COUNT = 9;
    private int maxCount = DEFAULT_MAX_COUNT;
    private boolean isShowOriginalLay; //是否显示选择原图按钮
    private final MediaStoreHelper.PhotoResultCallback mPhotoResultCallback = new MediaStoreHelper.PhotoResultCallback() {

        public void onResultCallback(List<PhotoDirectory> dirs) {
            if (dirs != null && dirs.size() > directoriesIndex) {
                photos.clear();
                photos.addAll(dirs.get(directoriesIndex).getPhotos());
                mPagerAdapter.notifyDataSetChanged();
                mViewPager.setCurrentItem(currentItem, false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_image_pager_picker);
        mContext = this;
        currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM_INDEX, 0);
        directoriesIndex = getIntent().getIntExtra(EXTRA_DIRS_CURRENT_INDEX, MediaStoreHelper.INDEX_ALL_PHOTOS);
        mCurrentPhotoEntity = getIntent().getParcelableExtra(EXTRA_CURRENT_PHOTO);
        selectPhotos = getIntent().getParcelableArrayListExtra(EXTRA_PHOTOS_SELECT);
        maxCount = getIntent().getIntExtra(EXTRA_PHOTOS_SELECT_NUM, DEFAULT_MAX_COUNT);
        isShowOriginalLay = getIntent().getIntExtra(PickerConstants.MSG_SELECT_PIC, 0) == 1;

        mImageSelect = (ImageView) findViewById(R.id.v_selected);
        mLayOriginal = (ViewGroup) findViewById(R.id.ll_original_img);
        mCbOriginal = (CheckBox) findViewById(R.id.cb_select_original_img);

        photos.add(mCurrentPhotoEntity);
        mImageSelect.setOnClickListener(this);
        switchComState(selectPhotos.size() > 0);
        if (isShowOriginalLay) {
            mPagerAdapter = new ShowWholePicAdapter(photos);
        } else {
            mPagerAdapter = new PhotoPagerAdapter(mContext, photos);
        }


        mViewPager = (ViewPager) findViewById(R.id.vp_photos);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(currentItem);
        mViewPager.setOffscreenPageLimit(1);
        if (selectPhotos.contains(mCurrentPhotoEntity)) {
            mImageSelect.setSelected(true);
        } else {
            mImageSelect.setSelected(false);
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                mCurrentPhotoEntity = photos.get(currentItem);
                if (selectPhotos.contains(mCurrentPhotoEntity)) {
                    mImageSelect.setSelected(true);
                } else {
                    mImageSelect.setSelected(false);
                }

                if (mCurrentPhotoEntity.getIsSupportOriginal() == 1) {
                    mCbOriginal.setChecked(true);
                } else {
                    mCbOriginal.setChecked(false);
                }
                showOriginalPicSize(mCurrentPhotoEntity);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (isShowOriginalLay) {
            mLayOriginal.setVisibility(View.VISIBLE);
            mCbOriginal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (selectPhotos.contains(mCurrentPhotoEntity)) {
                            selectPhotos.remove(mCurrentPhotoEntity);
                            mCurrentPhotoEntity.setIsSupportOriginal(1);
                            selectPhotos.add(mCurrentPhotoEntity);
                        } else {
                            mCurrentPhotoEntity.setIsSupportOriginal(1);
                            mImageSelect.performClick();
                        }
                    } else {
                        mCurrentPhotoEntity.setIsSupportOriginal(0);
                    }
                }
            });
            showOriginalPicSize(mCurrentPhotoEntity);
        }

        MediaStoreHelper.getPhotoDirs(this, null, mPhotoResultCallback);
    }

    private void showOriginalPicSize(ImageEntity photo) {
        if (mLayOriginal.getVisibility() == View.VISIBLE) {
            String size = MedPictureUtil.getFileSize(photo.getFilePath());
            mCurrentPhotoEntity.setFileSize(size);
            mCbOriginal.setText(getString(R.string.pick_msg_original_img_size, size));
        }
    }

    @Override
    protected void initActionBar(CommonNavigationBar navigation) {
        super.initActionBar(navigation);
         /*setNavigationDarkTheme();
        mNavigationBar.setRightText(R.string.pick_done);
        mNavigationBar.setRightButtonOnClickListener(this);
        mNavigationBar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

        setNavigationDarkTheme();
        navigation
                .showRightText(R.string.pick_done, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent();
                        bundle.putParcelableArrayList(PickerConstants.BUNDLE_DATA, selectPhotos);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .setRightTextEnable(false);

        navigation.showBackWhiteIcon(v -> onBackPressed());
    }

    @Override
    protected boolean needShadow() {
        return false;
    }

    //完成按钮状态
    private void switchComState(boolean isClick) {
        if (isClick) {
            mNavigationBar.setRightTextEnable(true);
            mNavigationBar.showRightText(getString(R.string.pick_done_with_count, selectPhotos.size(), maxCount), null);
        } else {
            mNavigationBar.setRightTextEnable(false);
            mNavigationBar.showRightText(R.string.pick_done_with_count_com, null);
        }
        /*if (isClick) {
            mNavigationBar.setRightButtonEnable(true);
            mNavigationBar.getRightText().setText(getString(R.string.pick_done_with_count, selectPhotos.size(), maxCount));
        } else {
            mNavigationBar.setRightButtonEnable(false);
            mNavigationBar.getRightText().setText(R.string.pick_done_with_count_com);
        }*/

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.v_selected) {
            boolean isCheck;
            if (selectPhotos.contains(mCurrentPhotoEntity)) {
                selectPhotos.remove(mCurrentPhotoEntity);
                mImageSelect.setSelected(false);
                mCbOriginal.setChecked(false);
                isCheck = false;
            } else {
                if (selectPhotos.size() >= maxCount) {
                    Toast.makeText(mContext, getString(R.string.pick_over_max_count_tips, maxCount),
                            LENGTH_LONG).show();
                    return;
                }
                selectPhotos.add(mCurrentPhotoEntity);
                mImageSelect.setSelected(true);
                isCheck = true;
            }
            //发送消息
            Intent intent = new Intent(SELECT_BROADCAST_ACTION);
            intent.putExtra(SELECT_EVENT, new SelectEvent(currentItem, isCheck));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            switchComState(selectPhotos.size() > 0);
        } /*else if (id == R.id.right_button_layout) {
            onEvent(StringUtil.buildEvent("finish"));
            Bundle bundle = new Bundle();
            Intent intent = new Intent();
            bundle.putParcelableArrayList(PickerConstants.BUNDLE_DATA, selectPhotos);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }*/
    }
}
