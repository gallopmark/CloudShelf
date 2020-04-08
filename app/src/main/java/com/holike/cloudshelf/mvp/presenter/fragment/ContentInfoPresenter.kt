package com.holike.cloudshelf.mvp.presenter.fragment

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.adapter.BottomPreviewImageAdapter
import com.holike.cloudshelf.base.BasePagerAdapter
import com.holike.cloudshelf.bean.ProductCatalogInfoBean
import com.holike.cloudshelf.bean.TableModelDetailBean
import com.holike.cloudshelf.mvp.model.fragment.ContentInfoModel
import com.holike.cloudshelf.mvp.view.fragment.ContentInfoView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.widget.StereoPagerTransformer
import io.reactivex.disposables.Disposable
import pony.xcode.mvp.BasePresenter


class ContentInfoPresenter : BasePresenter<ContentInfoModel, ContentInfoView>() {

    private inner class PicturePreviewAdapter(context: Context, images: MutableList<String>) :
        BasePagerAdapter<String>(context, images) {

        override fun getLayoutResourceId(): Int = R.layout.item_picture_preview
        override fun convert(convertView: View, bean: String, position: Int) {
            Glide.with(context).load(bean).into(convertView.findViewById(R.id.iv_pic))
        }
    }

    private val mBottomImageWidth = CurrentApp.getInstance().resources.getDimensionPixelSize(R.dimen.dp_130)
    private val mBottomImageHeight = (mBottomImageWidth * 0.68f).toInt()
    private val mPreviewImages = ArrayList<String>()
    private var mPreviewAdapter: PicturePreviewAdapter? = null
    private val mBottomImages = ArrayList<String>()
    private var mBottomImageAdapter: BottomPreviewImageAdapter? = null
    private var mFirstVisibleItemPosition = -1
    private var mLastVisibleItemPosition = -1

    fun initVp(viewPager: ViewPager) {
        val context = viewPager.context
        val pageWidth = CurrentApp.getInstance().getMaxPixels() -
                context.resources.getDimensionPixelSize(R.dimen.dp_35) * 2 -
                context.resources.getDimensionPixelSize(R.dimen.dp_80) * 4
        mPreviewAdapter = PicturePreviewAdapter(context, mPreviewImages)
        viewPager.setPageTransformer(false, StereoPagerTransformer(pageWidth.toFloat()))
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
        mModel.getTableModelDetail(id, object : HttpRequestCallback<TableModelDetailBean>() {
            override fun onStart(d: Disposable?) {
                view?.onShowLoading()
            }

            override fun onSuccess(result: TableModelDetailBean, message: String?) {
                view?.onTableModelResp(result)
                updatePictures(result.obtainImages())
                updateBottomPictures(result.obtainImages(mBottomImageWidth, mBottomImageHeight))
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onFailure(failReason)
            }

            override fun onCompleted() {
                view?.onDismissLoading()
            }
        })
    }

    /*产品大全模块详情*/
    fun getProductCatalogInfo(id: String?) {
        mModel.getProductCatalogInfo(id, object : HttpRequestCallback<ProductCatalogInfoBean>() {
            override fun onStart(d: Disposable?) {
                view?.onShowLoading()
            }

            override fun onSuccess(result: ProductCatalogInfoBean, message: String?) {
                view?.onProductCatalogResp(result)
                updatePictures(result.info?.getImageList())
                updateBottomPictures(
                    result.info?.getImageList(
                        mBottomImageWidth,
                        mBottomImageHeight
                    )
                )
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onFailure(failReason)
            }

            override fun onCompleted() {
                view?.onDismissLoading()
            }
        })
    }

    private fun updatePictures(images: MutableList<String>?) {
        if (!images.isNullOrEmpty()) {
            mPreviewImages.clear()
            mPreviewImages.addAll(images)
            mPreviewAdapter?.notifyDataSetChanged()
            mOnPageChangeListener.onPageSelected(0)
        }
    }

    private fun updateBottomPictures(images: MutableList<String>?) {
        if (!images.isNullOrEmpty()) {
            mBottomImages.clear()
            if (images.isNotEmpty()) {
                mBottomImages.addAll(images)
            }
            mBottomImageAdapter?.notifyDataSetChanged()
        }
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