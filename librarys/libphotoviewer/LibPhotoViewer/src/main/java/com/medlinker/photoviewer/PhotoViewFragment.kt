package com.medlinker.photoviewer

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bm.library.PhotoView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.DefaultOnImageEventListener
import com.medlinker.photoviewer.widget.DragPhotoView.OnPhotoViewActionListener
import com.medlinker.photoviewer.utils.ImageUtil
import com.medlinker.photoviewer.utils.ScanImageUtils
import kotlinx.android.synthetic.main.fragment_photoview_pager_item.*
import kotlinx.coroutines.*
import java.io.File

//import com.github.chrisbanes.photoview.OnPhotoTapListener;
//import com.github.chrisbanes.photoview.PhotoView;
/**
 * @author jiantao
 * @date 2017/11/14.
 */
class PhotoViewFragment : Fragment(), OnPhotoViewActionListener {

    /**
     * 部分机型使用SubScaleImage组件有问题，这个用于加载备用组件photoView
     */
    private var mViewGroup: ViewGroup? = null

    /**
     * 已下载好的图片
     */
    private var tempImageFile: File? = null
    var mUrl: String? = null
    private var imageLoadCall: ImageLoadCall? = null

    interface ImageLoadCall {
        fun onFail()
        fun onSuccess()
    }

    open class SimpleImageLoadCall : ImageLoadCall {
        override fun onFail() {}
        override fun onSuccess() {}
    }

    companion object {
        private const val ARGUMENTS_IMAGE = "argumens-image"
        private var mOnScanQrcode: ((String) -> Unit?)? = null
        private var mOnLongClick: (() -> Unit?)? = null
        fun newInstance(imageUrl: String?, onLongClick: () -> Unit, onScanQrcode: (String) -> Unit): PhotoViewFragment {
            mOnLongClick = onLongClick
            mOnScanQrcode = onScanQrcode
            val rawImageViewerFragment = PhotoViewFragment()
            val bundle = Bundle()
            bundle.putString(ARGUMENTS_IMAGE, imageUrl)
            rawImageViewerFragment.arguments = bundle
            return rawImageViewerFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photoview_pager_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mUrl = arguments?.getString(ARGUMENTS_IMAGE)
        mViewGroup = view.findViewById(R.id.fl_photoview_content)
        siv_raw_imageview.setDismissListener(this)
        siv_raw_imageview.setDoubleTapZoomScale(2.0f)
        siv_raw_imageview.maxScale = 3.0f
        siv_raw_imageview.setOnImageEventListener(object : DefaultOnImageEventListener() {
            override fun onImageLoaded() {
                super.onImageLoaded()
                progressbar.visibility = View.GONE
            }

            override fun onImageLoadError(e: Exception) {
                super.onImageLoadError(e)
                progressbar.visibility = View.GONE
                if (tempImageFile != null) {
                    val photoView = createPhotoView()
                    siv_raw_imageview.visibility = View.GONE
                    Glide.with(photoView).load(tempImageFile).into(photoView)
                    mViewGroup?.addView(photoView)
                } else {
                    siv_raw_imageview.setImage(ImageSource.resource(R.mipmap.ic_broken_image_black))
                }
            }
        })
        loadThumb()
        loadImageView(mUrl)
    }

    private fun createPhotoView(): PhotoView {
        val photoView = PhotoView(context)
        photoView.setBackgroundColor(Color.BLACK)
        photoView.setOnClickListener { onDismiss() }
        photoView.setOnLongClickListener(this)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        photoView.layoutParams = params
        return photoView
    }

    fun setImageLoadCall(imageLoadCalla: ImageLoadCall?): PhotoViewFragment {
        imageLoadCall = imageLoadCalla
        return this
    }

    fun loadImageView(url: String?) {
        if (mUrl != url) {
            mUrl = url
        }
        Glide.with(this).downloadOnly()
                .load(getCheckedUrl(url))
                .apply(RequestOptions()
                        .placeholder(R.mipmap.ic_photo_black)
                        .error(R.mipmap.ic_broken_image_black))
                .into(object : SimpleTarget<File?>() {

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        siv_raw_imageview.setImage(ImageSource.resource(R.mipmap.ic_broken_image_black))
                        imageLoadCall?.onFail()
                    }

                    override fun onResourceReady(resource: File, transition: Transition<in File?>?) {
                        tempImageFile = resource
                        siv_raw_imageview.setImage(ImageSource.uri(Uri.fromFile(resource)))
                        imageLoadCall?.onSuccess()
                    }
                })
    }

    /**
     * 缩放为原图的75%
     *
     * @return
     */
    private fun getCheckedUrl(url: String?): String = ImageUtil.checkUrl(url, 75)

    private fun loadThumb() {}

    private fun decodeBarByFile(file: File) {
        if (file != null && file.exists()) {
            CoroutineScope(Dispatchers.Main).launch {
                var task = async {
                    return@async ScanImageUtils.decodeByPath(file.absolutePath)
                }
                var result = task.await()
                result?.let {
                    mOnScanQrcode?.invoke(result.toString())
                }
            }
        }
    }

    override fun onDismiss() {
        activity?.onBackPressed()
    }

    override fun onClick(v: View) {
        onDismiss()
    }

    override fun onLongClick(v: View): Boolean {
        if (context == null) {
            return true
        }
        mOnLongClick?.invoke()
        mUrl?.let {
            if (it.startsWith("http")) {
                Glide.with(v).asFile().onlyRetrieveFromCache(true)
                        .load(getCheckedUrl(it))
                        .into(object : SimpleTarget<File?>() {
                            override fun onResourceReady(resource: File, transition: Transition<in File?>?) {
                                decodeBarByFile(resource)
                            }
                        })
            } else {
                decodeBarByFile(File(it))
            }
        }
        return true
    }
}