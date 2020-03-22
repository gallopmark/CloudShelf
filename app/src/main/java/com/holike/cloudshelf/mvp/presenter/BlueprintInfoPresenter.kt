package com.holike.cloudshelf.mvp.presenter

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.adapter.BottomPreviewImageAdapter
import com.holike.cloudshelf.base.BasePagerAdapter
import com.holike.cloudshelf.bean.BlueprintInfoBean
import com.holike.cloudshelf.mvp.model.BlueprintModel
import com.holike.cloudshelf.mvp.view.BlueprintInfoView
import com.holike.cloudshelf.netapi.HttpRequestCallback
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

    private inner class PicturePreviewAdapter(context: Context, images: MutableList<String>) :
        BasePagerAdapter<String>(context, images) {

        override fun getLayoutResourceId(): Int = R.layout.item_blueprint_info
        override fun convert(convertView: View, bean: String, position: Int) {
            /**
             * 如果想设置图片固定大小，又想保持图片宽高比
            解决方案如下：
            1)ImageView的width和height都设为wrap_content
            2)设置ImageView的maxWidth和maxHeight
            3)设置adjustViewBounds为true；
             */
            val imageView = convertView.findViewById<ImageView>(R.id.iv_pic)
            imageView.maxWidth = mResourceWidth
            imageView.maxHeight = mResourceHeight
            Glide.with(context).load(dataList[position]).into(imageView)
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
        mResourceWidth =
            mParamWidth - pictureFL.context.resources.getDimensionPixelSize(R.dimen.dp_60) * 2
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