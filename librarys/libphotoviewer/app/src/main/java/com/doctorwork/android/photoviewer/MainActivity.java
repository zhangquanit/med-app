package com.doctorwork.android.photoviewer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.medlinker.photoviewer.CasePhotoViewerActivity;
import com.medlinker.photoviewer.PhotoViewerActivity;
import com.medlinker.photoviewer.PictureViewerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import entity.FileEntity;

/**
 * @author hu
 */
public class MainActivity extends AppCompatActivity {

    private boolean sourceType;
    private String images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void photoViewerStrList(View view) {
        List<String> list = new ArrayList<>();
        list.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fdigitalbounds.com%2Fwp-content%2Fuploads%2F2014%2F09%2Fandroid-vs-apple.jpg&refer=http%3A%2F%2Fdigitalbounds.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1623485055&t=dfbb7e0309ed69fdc2b93d26ce4f64e0");
        list.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ftupian.enterdesk.com%2F2012%2F1112%2Fczs%2F04%2Fandroidjiqiren%2520%283%29.jpg&refer=http%3A%2F%2Ftupian.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1623485109&t=7956378d5f4c7de650ead97b0730b997");
        list.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1828929128,1490128173&fm=26&gp=0.jpg");
        list.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=4022971756,2019265665&fm=224&gp=0.jpg");
        PhotoViewerActivity.startPhotoViewerActivity(MainActivity.this, list, 0);
    }

    public void casePhotoViewer(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("选择")
                .setItems(new String[]{"单张", "多张", "隐藏标注，显示删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                images = "[{\"fileId\":\"1\",\"originPath\":\"https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1828929128,1490128173&fm=26&gp=0.jpg\",\"tagFileId\":\"2\",\"tagFileUrl\":\"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=4022971756,2019265665&fm=224&gp=0.jpg\",\"state\":0}]";
                                sourceType = true;
                                break;
                            case 1:
                                images = "[{\"fileId\":\"1\",\"originPath\":\"https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1828929128,1490128173&fm=26&gp=0.jpg\",\"tagFileId\":\"2\",\"tagFileUrl\":\"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=4022971756,2019265665&fm=224&gp=0.jpg\",\"state\":0},{\"fileId\":\"1\",\"originPath\":\"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fdigitalbounds.com%2Fwp-content%2Fuploads%2F2014%2F09%2Fandroid-vs-apple.jpg&refer=http%3A%2F%2Fdigitalbounds.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1623485055&t=dfbb7e0309ed69fdc2b93d26ce4f64e0\",\"tagFileId\":\"2\",\"tagFileUrl\":\"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ftupian.enterdesk.com%2F2012%2F1112%2Fczs%2F04%2Fandroidjiqiren%2520%283%29.jpg&refer=http%3A%2F%2Ftupian.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1623485109&t=7956378d5f4c7de650ead97b0730b997\",\"state\":0}]";
                                sourceType = true;
                                break;
                            case 2:
                                images = "[{\"fileId\":\"1\",\"originPath\":\"https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1828929128,1490128173&fm=26&gp=0.jpg\",\"tagFileId\":\"2\",\"tagFileUrl\":\"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=4022971756,2019265665&fm=224&gp=0.jpg\",\"state\":0},{\"fileId\":\"1\",\"originPath\":\"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fdigitalbounds.com%2Fwp-content%2Fuploads%2F2014%2F09%2Fandroid-vs-apple.jpg&refer=http%3A%2F%2Fdigitalbounds.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1623485055&t=dfbb7e0309ed69fdc2b93d26ce4f64e0\",\"tagFileId\":\"2\",\"tagFileUrl\":\"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ftupian.enterdesk.com%2F2012%2F1112%2Fczs%2F04%2Fandroidjiqiren%2520%283%29.jpg&refer=http%3A%2F%2Ftupian.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1623485109&t=7956378d5f4c7de650ead97b0730b997\",\"state\":0}]";
                                sourceType = false;
                                break;
                            default:
                                break;
                        }
                        CasePhotoViewerActivity.startPhotoViewerActivityForResult(MainActivity.this,
                                images,
                                0, sourceType, 521);
                    }
                })
                .show();
    }
}