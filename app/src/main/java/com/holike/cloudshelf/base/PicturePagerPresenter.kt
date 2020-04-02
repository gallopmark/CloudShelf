package com.holike.cloudshelf.base

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.adapter.BottomPreviewImageAdapter
import com.holike.cloudshelf.adapter.PicturePagerAdapter
import com.holike.cloudshelf.widget.StereoPagerTransformer
import pony.xcode.mvp.BaseModel
import pony.xcode.mvp.BasePresenter
import pony.xcode.mvp.BaseView


abstract class PicturePagerPresenter<M : BaseModel, V : BaseView> : BasePresenter<M, V>() {

    val mParamWidth: Int
    var mParamHeight: Int
    val mBottomImageWidth: Int
    val mBottomImageHeight: Int

    init {
        //UI width = 526 height = 370
        val size = CurrentApp.getInstance().getMaxPixels() * 0.57f
        mParamWidth = size.toInt()
        mParamHeight = (size * 0.68f).toInt()
        mBottomImageWidth = CurrentApp.getInstance().resources.getDimensionPixelSize(R.dimen.dp_130)
        mBottomImageHeight = (mBottomImageWidth * 0.68f).toInt()
    }

    private val mPreviewImages = ArrayList<String>()
    private var mPreviewImageAdapter: PicturePagerAdapter? = null
    private val mBottomImages = ArrayList<String>()
    private var mBottomImageAdapter: BottomPreviewImageAdapter? = null
    private var mFirstVisibleItemPosition = -1
    private var mLastVisibleItemPosition = -1

    fun initVp(viewPager: ViewPager) {
        viewPager.setPageTransformer(false, StereoPagerTransformer(mParamWidth.toFloat()))
        mPreviewImageAdapter = PicturePagerAdapter(viewPager.context, mPreviewImages, mParamWidth, mParamHeight)
        viewPager.adapter = mPreviewImageAdapter
        viewPager.addOnPageChangeListener(mOnPageChangeListener)
    }

    private val mOnPageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            onPageSelected(position, mPreviewImages.size)
            mBottomImageAdapter?.setSelectPosition(position)
        }
    }

    fun initBottomRV(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        mBottomImageAdapter = BottomPreviewImageAdapter(recyclerView.context, mBottomImages).apply {
            setOnImageSelectListener(object : BottomPreviewImageAdapter.OnImageSelectListener {
                override fun onImageSelect(position: Int) {
                    onBottomImageSelected(position)
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

    abstract fun onPageSelected(position: Int, size: Int)

    abstract fun onBottomImageSelected(position: Int)

    open fun getLeftScrollNum(): Int {
        return if (mFirstVisibleItemPosition > 0) {
            if (mFirstVisibleItemPosition - 7 > 0)
                mFirstVisibleItemPosition - 7 else 0
        } else 0
    }

    open fun getRightScrollNum(): Int {
        return if (mLastVisibleItemPosition < mBottomImages.size - 1) {
            if (mLastVisibleItemPosition + 7 < mBottomImages.size - 1) {
                mLastVisibleItemPosition + 7
            } else {
                mBottomImages.size - 1
            }
        } else mBottomImages.size - 1
    }

    open fun updatePreviewImages(imageList: MutableList<String>) {
        mPreviewImages.clear()
        mPreviewImages.addAll(imageList)
        mPreviewImageAdapter?.notifyDataSetChanged()
        mOnPageChangeListener.onPageSelected(0)
        mBottomImages.clear()
        mBottomImages.addAll(imageList)
        mBottomImageAdapter?.notifyDataSetChanged()
    }
}