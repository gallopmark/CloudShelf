package com.holike.cloudshelf.mvp.presenter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ImageViewTarget
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.adapter.BottomPreviewImageAdapter
import com.holike.cloudshelf.bean.BlueprintInfoBean
import com.holike.cloudshelf.mvp.model.BlueprintModel
import com.holike.cloudshelf.mvp.view.BlueprintInfoView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.util.ImageUtil
import pony.xcode.mvp.BasePresenter


class BlueprintInfoPresenter : BasePresenter<BlueprintModel, BlueprintInfoView>() {
    private val mParamWidth: Int
    private val mParamHeight: Int
    private var mResourceWidth: Int = 0
    private var mResourceHeight: Int = 0

    init {
        val size = CurrentApp.getInstance().getMaxPixels() / 2f
        mParamWidth = size.toInt()
        mParamHeight = (size * 0.9f).toInt()
    }

    private inner class PicturePreviewAdapter(private val context: Context, private val images: MutableList<String>)
        : PagerAdapter() {


        override fun getCount(): Int = images.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(context).inflate(R.layout.item_picture_preview, container, false)
            Glide.with(context).load(images[position]).into(view.findViewById(R.id.iv_pic))
            val imageView = view.findViewById<ImageView>(R.id.iv_pic)
            Glide.with(context).load(images[position]).override(mResourceWidth, mResourceHeight).into(imageView)
//            Glide.with(context).asBitmap().load(images[position])
//                    .into(object : ImageViewTarget<Bitmap?>(imageView) {
//                        override fun setResource(resource: Bitmap?) {
//                            if (resource == null) return
//                            val width = resource.width
//                            val height = resource.height
//                            var newBitmap = resource
//                            if (width > mResourceWidth || height > mResourceHeight) {
//                                newBitmap = ImageUtil.zoomBitmap(resource, mResourceWidth, mResourceHeight)
//                            }
//                            setBitmap(imageView, newBitmap)
//                        }
//                    })
            container.addView(view)
            return view
        }

        private fun setBitmap(imageView: ImageView, bitmap: Bitmap?) {
            imageView.setImageBitmap(bitmap)
            if (bitmap != null) {
                val bw = bitmap.width
                val bh = bitmap.height
                val vw = imageView.width
                val vh = imageView.height
                if (bw != 0 && bh != 0 && vw != 0 && vh != 0) {
                    if (1.0f * bh / bw > 1.0f * vh / vw) {
                        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    } else {
                        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                }
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    private val mPreviewImages = ArrayList<String>()
    private var mPreviewImageAdapter: PicturePreviewAdapter? = null
    private val mBottomImages = ArrayList<String>()
    private var mBottomImageAdapter: BottomPreviewImageAdapter? = null
    private var mFirstVisibleItemPosition = -1
    private var mLastVisibleItemPosition = -1

    fun resizeContent(centerLayout: LinearLayout, pictureFL: FrameLayout) {
        val lp = centerLayout.layoutParams as FrameLayout.LayoutParams
        lp.width = mParamWidth
        lp.height = mParamHeight
        centerLayout.layoutParams = lp
        mResourceWidth = mParamWidth - pictureFL.context.resources.getDimensionPixelSize(R.dimen.dp_60) * 2
        mResourceHeight = (mResourceWidth * 0.75f).toInt()
        val plp = pictureFL.layoutParams as LinearLayout.LayoutParams
        plp.width = mResourceWidth
        plp.height = mResourceHeight
        pictureFL.layoutParams = plp
    }

    fun initVp(viewPager: ViewPager) {
        mPreviewImageAdapter = PicturePreviewAdapter(viewPager.context, mPreviewImages)
        viewPager.adapter = mPreviewImageAdapter
        viewPager.addOnPageChangeListener(mOnPageChangeListener)
    }

    private val mOnPageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            view?.onPageSelected(position, mPreviewImages.size)
            mBottomImageAdapter?.setSelectPosition(position)
        }
    }

    fun initBottomRV(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        mBottomImageAdapter = BottomPreviewImageAdapter(recyclerView.context, mBottomImages).apply {
            setOnImageSelectListener(object : BottomPreviewImageAdapter.OnImageSelectListener {
                override fun onImageSelect(position: Int) {
                    view?.onBottomImageSelected(position)
                }
            })
        }
        recyclerView.adapter = mBottomImageAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                mFirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                mLastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            }
        })
    }

    fun getLeftScrollNum(): Int {
        return if (mFirstVisibleItemPosition > 0) {
            if (mFirstVisibleItemPosition - 7 > 0)
                mFirstVisibleItemPosition - 7 else 0
        } else 0
    }

    fun getRightScrollNum(): Int {
        return if (mLastVisibleItemPosition < mBottomImages.size - 1) {
            if (mLastVisibleItemPosition + 7 < mBottomImages.size - 1) {
                mLastVisibleItemPosition + 7
            } else {
                mBottomImages.size - 1
            }
        } else mBottomImages.size - 1
    }

    fun getBlueprintInfo(blueprintId: String?) {
        view?.onShowLoading()
        mModel.getBlueprintInfo(blueprintId, object : HttpRequestCallback<BlueprintInfoBean>() {
            override fun onSuccess(result: BlueprintInfoBean, message: String?) {
                view?.onDismissLoading()
                view?.onSuccess(result)
                updatePreviewImages(result.obtainImageList())
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onDismissLoading()
                view?.onFailure(failReason)
            }
        })
    }

    private fun updatePreviewImages(imageList: MutableList<String>) {
        mPreviewImages.clear()
        mPreviewImages.addAll(imageList)
        mPreviewImageAdapter?.notifyDataSetChanged()
        mOnPageChangeListener.onPageSelected(0)
        mBottomImages.clear()
        mBottomImages.addAll(imageList)
        mBottomImageAdapter?.notifyDataSetChanged()
    }
}