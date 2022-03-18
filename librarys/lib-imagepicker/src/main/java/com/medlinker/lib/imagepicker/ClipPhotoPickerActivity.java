package com.medlinker.lib.imagepicker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ConvertUtils;
import com.medlinker.lib.imagepicker.adapter.ClipPhotoGridAdapter;
import com.medlinker.lib.imagepicker.adapter.PopupDirectoryListAdapter;
import com.medlinker.lib.imagepicker.entity.ImageEntity;
import com.medlinker.lib.imagepicker.entity.PhotoDirectory;
import com.medlinker.lib.imagepicker.event.OnPhotoClickListener;
import com.medlinker.lib.imagepicker.utils.ImageCaptureManager;
import com.medlinker.lib.imagepicker.utils.MediaStoreHelper;
import com.medlinker.lib.imagepicker.widget.CommonPopupView;
import com.medlinker.lib.permission.ext.MedPermissionUtil;
import com.medlinker.widget.navigation.CommonNavigationBar;

import net.medlinker.base.base.BaseActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 图片选择器，用于选择单张图片裁剪
 * @author: lichaofeng
 * @since: V3.3
 * @copyright © Medlinker
 * @Date: 2016/6/6 16:17
 */
@Route(path = "/imagePicker/ClipPhotoPickerActivity")
public class ClipPhotoPickerActivity extends BaseActivity implements View.OnClickListener {
    private static final String KEY_IMAGE_PATH = "image_path";

    protected ImageCaptureManager captureManager;
    private ClipPhotoGridAdapter photoGridAdapter;
    private List<PhotoDirectory> directories;
    private PopupDirectoryListAdapter listAdapter;
    private RecyclerView recyclerView;

    /*private RelativeLayout relActionbar;
    private TextView switchDirectoryTv, doneTv;
    private ImageView btBack;*/
    private CommonNavigationBar mNavigationBar;
    private TextView titleTv;
    private ImageView arrowIv;


    private CommonPopupView mDirectoryPopup;
    private ListView mDirectoryList;
    private Context mContext;

    //裁剪图片宽高
    protected int mClipWidth, mClipHeight;

    protected int mCameraId = -1;


    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";
    protected String mPermissionTipTxt;
    protected boolean isClipCamera;

    private boolean mCompleted;  // need complete to transport image to it.
    private final MediaStoreHelper.PhotoResultCallback mPhotoResultCallback = new MediaStoreHelper.PhotoResultCallback() {

        public void onResultCallback(List<PhotoDirectory> dirs) {
            directories.clear();
            directories.addAll(dirs);
            photoGridAdapter.notifyDataSetChanged();
            listAdapter.notifyDataSetChanged();
            if (mCompleted) {
                mCompleted = false;
                // 跳转图片裁剪
                Intent clipIntent = new Intent(mContext, ClipImageActivity.class);
                clipIntent.putExtra(PickerConstants.BUNDLE_DATA, captureManager.getCurrentPhotoPath());
                clipIntent.putExtra("clip_width", mClipWidth);
                clipIntent.putExtra("clip_height", mClipHeight);
                startActivityForResult(clipIntent, PickerConstants.REQUEST_CODE_CLIP_PIC);
            }
        }
    };

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = getIntent();
        if (data != null) {
            mClipWidth = data.getIntExtra("clip_width", 0);
            mClipHeight = data.getIntExtra("clip_height", 0);
            mCameraId = data.getIntExtra("camera_id", -1);
        }

        captureManager = new ImageCaptureManager(this);
        if (savedInstanceState != null) {
            captureManager.setPhotoPath(savedInstanceState.getString(KEY_IMAGE_PATH, null));
        }

        if (data.hasExtra(PickerConstants.PERMISSION_TIP)) {
            mPermissionTipTxt = data.getStringExtra(PickerConstants.PERMISSION_TIP);
        } else {
            mPermissionTipTxt = getString(R.string.pick_permission_tip);
        }

        int contentView = getContentView();
        if (contentView > 0) {
            setContentView(R.layout.activity_pick_photo_picker);
        }

        if (isClipCamera) {
            initView(savedInstanceState);
        } else {
            new MedPermissionUtil(this).requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .onResult((accept) -> {
                        if (accept) {
                            initView(savedInstanceState);
                        } else {
                            finish();
                        }
                        return null;
                    });
        }
    }

    protected int getContentView() {
        return R.layout.activity_pick_photo_picker;
    }

    protected void initView(Bundle bundle) {
        mContext = this;
        directories = new ArrayList<>();
        MediaStoreHelper.getPhotoDirs(this, null, mPhotoResultCallback);

        /*relActionbar = (RelativeLayout) findViewById(R.id.rel_action_bar);
        switchDirectoryTv = (TextView) findViewById(R.id.button);
        doneTv = (TextView) findViewById(R.id.button_done);
        btBack = (ImageView) findViewById(R.id.button_back);
        switchDirectoryTv.setOnClickListener(this);
        btBack.setOnClickListener(this);
        doneTv.setVisibility(View.GONE);*/
        mNavigationBar = findViewById(R.id.commonNavigationBar);
        mNavigationBar
                .setTitleTextColor(R.color.ui_white)
                .showTitle(R.string.pick_all_image)
                .showBackWhiteIcon(v -> {
                    onBackPressed();
                });
        titleTv = mNavigationBar.getNavigationView(CommonNavigationBar.NavigationId.CENTER_TV_TITLE);
        arrowIv = mNavigationBar.getNavigationView(CommonNavigationBar.NavigationId.CENTER_STATE_IV_ICON);
        int left = ConvertUtils.dp2px(12);
        arrowIv.setPadding(left, left, left, left);
        arrowIv.setImageResource(R.mipmap.pick_icon_arrow_down_white);
        arrowIv.setOnClickListener(v -> {
            showPopWindow();
        });
        titleTv.setOnClickListener(v -> {
            showPopWindow();
        });


        recyclerView = (RecyclerView) findViewById(R.id.rv_photos);

        photoGridAdapter = new ClipPhotoGridAdapter(mContext, directories);
        listAdapter = new PopupDirectoryListAdapter(mContext, directories);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration((int) getResources().getDimension(R.dimen.pick_photo_width)));
        recyclerView.setAdapter(photoGridAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //拍照点击事件
        photoGridAdapter.setOnCameraClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MedPermissionUtil(ClipPhotoPickerActivity.this).requestPermissions(Manifest.permission.CAMERA)
                        .onResult((accept) -> {
                            if (accept) {
                                try {
                                    Intent intent = captureManager.dispatchTakePictureIntent(mCameraId);
                                    startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                finish();
                            }
                            return null;
                        });
            }
        });

        // 图片裁剪
        photoGridAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
            @Override
            public void onClick(View v, int position, boolean showCamera) {
                int index = showCamera ? position - 1 : position;
                ImageEntity photo = photoGridAdapter.getCurrentPhotos().get(index);
                Intent intent = new Intent(mContext, ClipImageActivity.class);
                intent.putExtra(PickerConstants.BUNDLE_DATA, photo.getFilePath());
                intent.putExtra("clip_width", mClipWidth);
                intent.putExtra("clip_height", mClipHeight);
                startActivityForResult(intent, PickerConstants.REQUEST_CODE_CLIP_PIC);
            }
        });
    }

    //初始相册选择
    private void showPopWindow() {
        if (mDirectoryPopup == null) {
            mDirectoryPopup = new CommonPopupView(mContext);
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.pick_album_list_directory, null);
            mDirectoryList = contentView.findViewById(R.id.album_directory_list);
            mDirectoryList.setAdapter(listAdapter);
            mDirectoryPopup.setContentView(contentView);
            contentView.setOnTouchListener(null);
            mDirectoryPopup.setAnimationRes(R.anim.pick_push_top_in, R.anim.pick_push_top_out);
        }
        if (mDirectoryPopup.isShowing()) {
            mDirectoryPopup.dismiss();
        }
        mDirectoryPopup.showAsDropDown(mNavigationBar);
        /*switchDirectoryTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.pick_album_up_arrow, 0);*/
        arrowIv.setImageResource(R.mipmap.pick_icon_arrow_up_white);
        mDirectoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDirectoryPopup.dismiss();
                PhotoDirectory directory = directories.get(position);
                /*switchDirectoryTv.setText(directory.getName());*/
                titleTv.setText(directory.getName());
                photoGridAdapter.setCurrentDirectoryIndex(position);
                photoGridAdapter.notifyDataSetChanged();
            }
        });
        mDirectoryPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                /*switchDirectoryTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.pick_album_down_arrow, 0);*/
                arrowIv.setImageResource(R.mipmap.pick_icon_arrow_down_white);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_IMAGE_PATH, captureManager.getCurrentPhotoPath());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        captureManager.setPhotoPath(savedInstanceState.getString(KEY_IMAGE_PATH, null));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                captureManager.galleryAddPic();
                if (directories == null || directories.size() == 0) {
                    mCompleted = true;
                    MediaStoreHelper.getPhotoDirs(this, null, mPhotoResultCallback);
                    return;
                }
                ImageEntity path = new ImageEntity(captureManager.getCurrentPhotoPath());
                PhotoDirectory directory = directories.get(MediaStoreHelper.INDEX_ALL_PHOTOS);
                directory.getPhotos().add(path);
                directory.setCoverPath(path.getFilePath());
                photoGridAdapter.getSelectedPhotos().clear();
                photoGridAdapter.notifyDataSetChanged();
                photoGridAdapter.getSelectedPhotos().add(path);

                Intent clipIntent = new Intent(mContext, ClipImageActivity.class);
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

    @Override
    public void onClick(View v) {
        /*int id = v.getId();
        if (id == R.id.button) {
            showPopWindow();
        } else if (id == R.id.button_back) {
            onBackPressed();
        }*/
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //点评病例处需要返回
    @Override
    public void onBackPressed() {
        Intent sIntent = new Intent();
        setResult(RESULT_OK, sIntent);
        super.onBackPressed();
    }


    class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {

            if ((parent.getChildLayoutPosition(view) + 1) % 3 == 0) {
                outRect.right = space;
            } else {
                outRect.right = space;
            }
            outRect.bottom = space;
        }
    }
}
