package com.medlinker.lib.imagepicker;

import static android.widget.Toast.LENGTH_LONG;
import static com.medlinker.lib.imagepicker.PickerConstants.SELECT_BROADCAST_ACTION;
import static com.medlinker.lib.imagepicker.PickerConstants.SELECT_EVENT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ConvertUtils;
import com.medlinker.lib.imagepicker.adapter.PhotoGridAdapter;
import com.medlinker.lib.imagepicker.adapter.PopupDirectoryListAdapter;
import com.medlinker.lib.imagepicker.entity.ImageEntity;
import com.medlinker.lib.imagepicker.entity.PhotoDirectory;
import com.medlinker.lib.imagepicker.entity.SelectEvent;
import com.medlinker.lib.imagepicker.event.OnItemCheckListener;
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
 * 用于多张图片选择，不带裁剪功能
 *
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/22 14:00
 */
@Route(path = "/imagePicker/PhotoPickerActivity")
public class PhotoPickerActivity extends BaseActivity implements View.OnClickListener {

    private static final String KEY_IMAGE_PATH = "image_path";

    private ImageCaptureManager captureManager;
    private PhotoGridAdapter photoGridAdapter;
    private List<PhotoDirectory> directories;
    private PopupDirectoryListAdapter listAdapter;
    private RecyclerView recyclerView;

    /* private RelativeLayout relActionbar;
     private TextView switchDirectoryTv, doneTv;
     private ImageView btBack;*/
    private CommonNavigationBar mNavigationBar;
    private TextView titleTv;
    private ImageView arrowIv;

    private CommonPopupView mDirectoryPopup;
    private ListView mDirectoryList;
    private Context mContext;

    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";//最大的选择数
    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";


    public final static int DEFAULT_MAX_COUNT = 9;
    public final static int DEFAULT_SELECT_COUNT = 0;
    private int maxCount = DEFAULT_MAX_COUNT;
    private int selectCount = DEFAULT_SELECT_COUNT;

    private int showOriginalPic;
    private String mPermissionTipTxt;

    private boolean mCompleted;  // need complete to transport image to it.
    private final MediaStoreHelper.PhotoResultCallback mPhotoResultCallback = new MediaStoreHelper.PhotoResultCallback() {

        public void onResultCallback(List<PhotoDirectory> dirs) {
            directories.clear();
            directories.addAll(dirs);
            photoGridAdapter.notifyDataSetChanged();
            listAdapter.notifyDataSetChanged();
            if (mCompleted) {
                mCompleted = false;
                ArrayList<ImageEntity> list = new ArrayList<>();
                list.add(new ImageEntity(captureManager.getCurrentPhotoPath()));
                onComplete(list);
            }
        }
    };

    private final BroadcastReceiver mSelectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SelectEvent event = (SelectEvent) intent.getSerializableExtra(SELECT_EVENT);
            if (null == event) {
                return;
            }
            onPictureSelected(event);
        }
    };

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_photo_picker);
        Intent data = getIntent();
        if (data.hasExtra(PickerConstants.PERMISSION_TIP)) {
            mPermissionTipTxt = data.getStringExtra(PickerConstants.PERMISSION_TIP);
        } else {
            mPermissionTipTxt = getString(R.string.pick_permission_tip);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mSelectReceiver,
                new IntentFilter(SELECT_BROADCAST_ACTION));

        new MedPermissionUtil(this).requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onResult((accetp) -> {
                    if (accetp) {
                        initView(savedInstanceState);
                    } else {
                        finish();
                    }
                    return null;
                });
    }

    private void initView(Bundle bundle) {
        mContext = this;
        directories = new ArrayList<>();
        captureManager = new ImageCaptureManager(mContext);
        if (bundle != null) {
            captureManager.setPhotoPath(bundle.getString(KEY_IMAGE_PATH, null));
        }
        MediaStoreHelper.getPhotoDirs(this, null, mPhotoResultCallback);

        maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
        showOriginalPic = getIntent().getIntExtra(PickerConstants.MSG_SELECT_PIC, 0);

        /*relActionbar = (RelativeLayout) findViewById(R.id.rel_action_bar);
        switchDirectoryTv = (TextView) findViewById(R.id.button);
        doneTv = (TextView) findViewById(R.id.button_done);
        btBack = (ImageView) findViewById(R.id.button_back);
        switchDirectoryTv.setOnClickListener(this);
        doneTv.setOnClickListener(this);
        btBack.setOnClickListener(this);*/
        mNavigationBar = findViewById(R.id.commonNavigationBar);
        mNavigationBar
                .setTitleTextColor(R.color.ui_white)
                .showTitle(R.string.pick_all_image)
                .showBackWhiteIcon(v -> {
                    onBackPressed();
                })
                .setRightTextColor(R.color.navigation_text_white_selector)
                .showRightText(R.string.pick_done, v -> {
                    onComplete(null);
                })
                .setRightTextEnable(false);
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


        switchComState(selectCount > 0);
        recyclerView = (RecyclerView) findViewById(R.id.rv_photos);

        photoGridAdapter = new PhotoGridAdapter(mContext, directories);
        listAdapter = new PopupDirectoryListAdapter(mContext, directories);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration((int) getResources().getDimension(R.dimen.pick_photo_width)));
        recyclerView.setAdapter(photoGridAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //拍照点击事件
        photoGridAdapter.setOnCameraClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View view) {
                new MedPermissionUtil(PhotoPickerActivity.this).requestPermissions(Manifest.permission.CAMERA)
                        .onResult((accept) -> {
                            if (accept) {
                                try {
                                    Intent intent = captureManager.dispatchTakePictureIntent();
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

        photoGridAdapter.setOnItemCheckListener(new OnItemCheckListener() {
            @Override
            public boolean OnItemCheck(int position, ImageEntity photo, final boolean isCheck, int selectedItemCount) {

                selectCount = selectedItemCount + (isCheck ? -1 : 1);
                if (selectCount > maxCount) {
                    Toast.makeText(mContext, getString(R.string.pick_over_max_count_tips, maxCount),
                            LENGTH_LONG).show();
                    return false;
                }
                switchComState(selectCount > 0);
                return true;
            }
        });


        //查看大图
        photoGridAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
            @Override
            public void onClick(View v, int position, boolean showCamera) {
                int index = showCamera ? position - 1 : position;
                Intent intent = new Intent(mContext, PhotoPagerPickerActivity.class);
                intent.putExtra(PhotoPagerPickerActivity.EXTRA_CURRENT_ITEM_INDEX, index);
                intent.putExtra(PickerConstants.MSG_SELECT_PIC, showOriginalPic);
                intent.putExtra(PhotoPagerPickerActivity.EXTRA_DIRS_CURRENT_INDEX, photoGridAdapter.getCurrentDirectoryIndex());
                intent.putExtra(PhotoPagerPickerActivity.EXTRA_CURRENT_PHOTO, photoGridAdapter.getCurrentPhotos().get(index));
                intent.putParcelableArrayListExtra(PhotoPagerPickerActivity.EXTRA_PHOTOS_SELECT, (ArrayList<ImageEntity>) photoGridAdapter.getSelectedPhotos());
                intent.putExtra(PhotoPagerPickerActivity.EXTRA_PHOTOS_SELECT_NUM, maxCount);
                try {
                    startActivityForResult(intent, ImageCaptureManager.REQUEST_CODE_BIG_PIC);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
        /* switchDirectoryTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.pick_album_up_arrow, 0);*/
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
            case ImageCaptureManager.REQUEST_CODE_BIG_PIC:
                if (data != null) {
                    ArrayList<ImageEntity> selectedPic = data.getParcelableArrayListExtra(PickerConstants.BUNDLE_DATA);
                    onComplete(selectedPic);
                }
                break;
            case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                captureManager.galleryAddPic();
                if (directories == null || directories.size() == 0) {
                    mCompleted = true;
                    MediaStoreHelper.getPhotoDirs(this, null, mPhotoResultCallback);
                    return;
                }
                ImageEntity path = new ImageEntity(captureManager.getCurrentPhotoPath());
                PhotoDirectory directory = directories.get(MediaStoreHelper.INDEX_ALL_PHOTOS);
                directory.getPhotos().add(MediaStoreHelper.INDEX_ALL_PHOTOS, path);
                directory.setCoverPath(path.getFilePath());
                photoGridAdapter.getSelectedPhotos().clear();
                photoGridAdapter.notifyDataSetChanged();
                photoGridAdapter.getSelectedPhotos().add(path);
                onComplete(null);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        /*int id = v.getId();
        if (id == R.id.button) {
            showPopWindow();
        } else if (id == R.id.button_done) {
            onEvent(StringUtil.buildEvent("finish"));
            onComplete(null);
        } else if (id == R.id.button_back) {
            onEvent(StringUtil.buildEvent("back"));
            onBackPressed();
        }*/
    }


    //完成选择图片
    private void onComplete(ArrayList<ImageEntity> selectedPic) {
        Intent sIntent = new Intent();
        if (selectedPic != null) {
            sIntent.putParcelableArrayListExtra(KEY_SELECTED_PHOTOS, selectedPic);
            setResult(RESULT_OK, sIntent);
        } else {
            List<ImageEntity> selectedPhotos = photoGridAdapter.getSelectedPhotos();
            sIntent.putParcelableArrayListExtra(KEY_SELECTED_PHOTOS, (ArrayList<ImageEntity>) selectedPhotos);
            setResult(RESULT_OK, sIntent);
        }
        finish();
    }


    //完成按钮状态
    private void switchComState(boolean isClick) {
        if (isClick) {
           /* doneTv.setEnabled(true);
//            doneTv.setTextColor(getResources().getColor(R.color.font_color_007eff));
            doneTv.setText(getString(R.string.pick_done_with_count, selectCount, maxCount));*/
            mNavigationBar
                    .setRightTextEnable(true)
                    .updateRightText(getString(R.string.pick_done_with_count, selectCount, maxCount));
        } else {
          /*  doneTv.setEnabled(false);
//            doneTv.setTextColor(getResources().getColor(R.color.font_color_b3b3b3));
            doneTv.setText(R.string.pick_done_with_count_com);*/
            mNavigationBar
                    .setRightTextEnable(false)
                    .updateRightText(R.string.pick_done_with_count_com);
        }
    }


    private void onPictureSelected(SelectEvent event) {
        int position = event.getPostion();
        boolean isCheck = event.isCheck();
        selectCount = photoGridAdapter.getSelectedPhotos().size() + (isCheck ? 1 : -1);
        if (selectCount > maxCount) {
            Toast.makeText(mContext, getString(R.string.pick_over_max_count_tips, maxCount),
                    LENGTH_LONG).show();
            return;
        }
        ImageEntity photo = photoGridAdapter.getCurrentPhotos().get(position);
        photoGridAdapter.toggleSelection(photo);
        if (photoGridAdapter.showCamera()) {
            position = position + 1;
        }
        photoGridAdapter.notifyItemChanged(position);
        switchComState(selectCount > 0);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSelectReceiver);
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
