package com.medlinker.lib.imagepicker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.medlinker.lib.imagepicker.adapter.ShowWholePicAdapter;
import com.medlinker.lib.imagepicker.entity.ImageEntity;
import com.medlinker.widget.dialog.MLConfirmDialog;


import net.medlinker.base.base.BaseActivity;

import java.util.ArrayList;

/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/26 14:00
 */
public class PhotoPagerActivity extends BaseActivity {


    private ViewPager mViewPager;
    private ShowWholePicAdapter mPagerAdapter;
    //    private NavigationBar mNavigationBar;
    private Context mContext;

    private int currentItem = 0;//当前图片
    private ArrayList<ImageEntity> paths;
    public final static String EXTRA_CURRENT_ITEM = "current_item";
    public final static String EXTRA_PHOTOS = "photos";
    private View mTitleGroup;
    private TextView mTitle, mDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_image_local_pager);
        mContext = this;
        initActionBar();
        currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);
        paths = getIntent().getParcelableArrayListExtra(EXTRA_PHOTOS);

        mPagerAdapter = new ShowWholePicAdapter(paths);
        mViewPager = (ViewPager) findViewById(R.id.vp_photos);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(currentItem);
        mViewPager.setOffscreenPageLimit(1);
        updateActionBarTitle();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateActionBarTitle();
            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    protected void initActionBar() {
        mTitleGroup = findViewById(R.id.titlegroup);
        mTitle = mTitleGroup.findViewById(R.id.button);
        mTitleGroup.findViewById(R.id.button_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delAlertDialog();
            }
        });
        findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    public void updateActionBarTitle() {
        mTitle.setText(
                getString(R.string.pick_image_index, mViewPager.getCurrentItem() + 1,
                        paths.size()));
    }

    public void showNavigationbar() {
        if (mTitleGroup.getVisibility() == View.VISIBLE)
            mTitleGroup.setVisibility(View.GONE);
        else
            mTitleGroup.setVisibility(View.VISIBLE);

    }

    private void delAlertDialog() {
        MLConfirmDialog dialogFragment = MLConfirmDialog.newInstance();
        dialogFragment.setMessage(getString(R.string.pick_confirm_to_delete));
        dialogFragment.setCancelButton(getString(R.string.pick_cancel), null);
        dialogFragment.setConfirmButton(getString(R.string.pick_yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paths.remove(mViewPager.getCurrentItem());
                mViewPager.getAdapter().notifyDataSetChanged();
                Intent intent = new Intent();
                intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, paths);
                setResult(RESULT_OK, intent);
                if (paths.size() == 0) {
                    finish();
                }
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, false);
            }
        });
        dialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void finish() {
        super.finish();
        System.gc();
    }
}
