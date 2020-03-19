package com.holike.cloudshelf.mvp.presenter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.holike.cloudshelf.R
import com.holike.cloudshelf.adapter.BottomPreviewImageAdapter
import com.holike.cloudshelf.bean.TableModelDetailBean
import com.holike.cloudshelf.mvp.model.GeneralModel
import com.holike.cloudshelf.mvp.view.GeneralView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import pony.xcode.mvp.BasePresenter


class GeneralPresenter : BasePresenter<GeneralModel, GeneralView>() {

    private inner class PicturePreviewAdapter(private val context: Context, private val images: MutableList<String>)
        : PagerAdapter() {

        override fun getCount(): Int = images.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(context).inflate(R.layout.item_picture_preview, container, false)
            Glide.with(context).load(images[position]).into(view.findViewById(R.id.iv_pic))
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    private val mPreviewImages = ArrayList<String>()
    private var mPreviewAdapter: PicturePreviewAdapter? = null
    private val mBottomImages = ArrayList<String>()
    private var mBottomImageAdapter: BottomPreviewImageAdapter? = null
    private var mFirstVisibleItemPosition = -1
    private var mLastVisibleItemPosition = -1

    fun initVp(viewPager: ViewPager) {
        mPreviewAdapter = PicturePreviewAdapter(viewPager.context, mPreviewImages)
        viewPager.adapter = mPreviewAdapter
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

    /*方案库详情*/
    fun getTableModelDetail(id: String?) {
        mModel?.getTableModelDetail(id, object : HttpRequestCallback<TableModelDetailBean>() {

            override fun onSuccess(result: TableModelDetailBean, message: String?) {
                view?.onTableModelResponse(result)
                updateTableModelPictures(result.obtainImages())
                updateBottomPictures(result.obtainImages())
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onTableModelFailure(failReason)
            }
        })
    }

    private fun updateTableModelPictures(images: MutableList<String>) {
        if (images.isNotEmpty()) {
            mPreviewImages.clear()
            mPreviewImages.addAll(images)
            mPreviewAdapter?.notifyDataSetChanged()
            mOnPageChangeListener.onPageSelected(0)
        }
    }

    private fun updateBottomPictures(images: MutableList<String>) {
        mBottomImages.clear()
        if (images.isNotEmpty()) {
            mBottomImages.addAll(images)
        }
        mBottomImageAdapter?.notifyDataSetChanged()
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
}