package com.holike.cloudshelf.mvp.presenter

import androidx.recyclerview.widget.RecyclerView
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.adapter.PictureDisplayAdapter
import com.holike.cloudshelf.bean.BlueprintBean
import com.holike.cloudshelf.bean.internal.PictureDisplayItem
import com.holike.cloudshelf.mvp.model.BlueprintModel
import com.holike.cloudshelf.mvp.view.BlueprintListView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.util.ListUtils
import pony.xcode.mvp.BasePresenter


class BlueprintListPresenter : BasePresenter<BlueprintModel, BlueprintListView>() {

    private val mDataList: MutableList<PictureDisplayItem> = ArrayList()
    private var mImageAdapter: PictureDisplayAdapter? = null
    private val mPageSize = 20  //一次加载20条
    private var mPageNo = 1 //页码

    fun initRecyclerView(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val itemWidth = (CurrentApp.getInstance().getMaxPixels()
                - context.resources.getDimensionPixelSize(R.dimen.dp_45)
                - context.resources.getDimensionPixelSize(R.dimen.dp_16) * 4) / 4.2f
        mImageAdapter = PictureDisplayAdapter(context, mDataList, itemWidth.toInt()).apply {
            setOnPictureItemClickListener(object : PictureDisplayAdapter.OnPictureItemClickListener {
                override fun onPictureItemClick(id: String?, images: MutableList<String>?) {
                    view?.onPictureItemClick(id)
                }
            })
        }
        recyclerView.adapter = mImageAdapter
    }

    fun initData() {
        mPageNo = 1
        view?.onShowLoading()
        getBlueprintList()
    }

    private fun getBlueprintList() {
        mModel.getBlueprintList(mPageNo, mPageSize, object : HttpRequestCallback<BlueprintBean>() {
            override fun onSuccess(result: BlueprintBean, message: String?) {
                view?.onDismissLoading()
                view?.onSuccess(result, result.obtainDataList().size >= mPageSize)
                updatePage(result)
                mPageNo += 1  //数据加载成功 页面自增
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onDismissLoading()
                view?.onFailure(failReason, mPageNo == 1)
            }
        })
    }

    private fun updatePage(bean: BlueprintBean) {
        if (mPageNo == 1) {
            mDataList.clear()
            if (bean.obtainDataList().isEmpty()) {  //加载无数据
                view?.onNoResults()
            } else {
                updateBlueprintList(bean.obtainDataList())
            }
        } else {
            updateBlueprintList(bean.obtainDataList())
        }
    }

    private fun updateBlueprintList(dataList: MutableList<BlueprintBean.DataBean>) {
        val sList = ListUtils.averageAssignFixLength(dataList, 2)
        for (i in sList.indices) {
            val item: PictureDisplayItem
            if (sList[i].size > 1) {
                item = PictureDisplayItem(sList[i][0].image, sList[i][0].title, sList[i][1].image, sList[i][1].title)
                item.topId = sList[i][0].id
                item.bottomId = sList[i][1].id
            } else {
                item = PictureDisplayItem(sList[i][0].image, sList[i][0].title, null, null)
                item.topId = sList[i][0].id
            }
            mDataList.add(item)
        }
        mImageAdapter?.notifyDataSetChanged()
    }

    fun onLoadMore() {
        getBlueprintList()
    }
}