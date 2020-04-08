package com.holike.cloudshelf.mvp.presenter.fragment

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.PlotTypeBean
import com.holike.cloudshelf.bean.internal.PictureDisplayItem
import com.holike.cloudshelf.mvp.model.fragment.PlotTypeListModel
import com.holike.cloudshelf.mvp.view.fragment.PlotTypeListView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.util.ListUtils
import pony.xcode.mvp.BasePresenter
import pony.xcode.recycler.CommonAdapter


class PlotTypeListPresenter : BasePresenter<PlotTypeListModel, PlotTypeListView>() {
    private inner class ImageListAdapter(context: Context, dataList: MutableList<PictureDisplayItem>)
        : CommonAdapter<PictureDisplayItem>(context, dataList) {
        override fun getItemResourceId(viewType: Int): Int = R.layout.item_plottype

        override fun bindViewHolder(holder: RecyclerHolder, t: PictureDisplayItem, position: Int) {
            val lp = holder.itemView.layoutParams as RecyclerView.LayoutParams
            lp.width = mImageSize
            holder.itemView.layoutParams = lp
            holder.itemView.layoutParams = lp
            val topIView = holder.getView<ImageView>(R.id.iv_pic_top)
            val tlp = topIView.layoutParams as LinearLayout.LayoutParams
            tlp.height = mImageSize
            topIView.layoutParams = tlp
            Glide.with(mContext).load(t.topUrl).into(topIView)
            holder.setText(R.id.tv_name_top, t.topName)
            val bottomIView = holder.getView<ImageView>(R.id.iv_pic_bottom)
            val blp = bottomIView.layoutParams as LinearLayout.LayoutParams
            blp.height = mImageSize
            bottomIView.layoutParams = blp
            Glide.with(mContext).load(t.bottomUrl).into(bottomIView)
            holder.setText(R.id.tv_name_bottom, t.bottomName)
            holder.setOnClickListener(R.id.top_layout) {
                if (!t.topId.isNullOrEmpty()) {
                    view?.onItemClick(t.topId)
                }
            }
            holder.setOnClickListener(R.id.bottom_layout) {
                if (!t.bottomId.isNullOrEmpty()) {
                    view?.onItemClick(t.bottomId)
                }
            }
        }
    }

    private var mImageSize = 0
    private val mDataList = ArrayList<PictureDisplayItem>()
    private var mAdapter: ImageListAdapter? = null
    private var mPageNo = 1
    private val mPageSize = 20

    private var mCommunityId: String? = null
    fun attachRecyclerView(recyclerView: RecyclerView) {
        val context = recyclerView.context
        mImageSize = ((CurrentApp.getInstance().getMaxPixels()
                - context.resources.getDimensionPixelSize(R.dimen.dp_45)
                - context.resources.getDimensionPixelSize(R.dimen.dp_16) * 5) / 6f).toInt()
        mAdapter = ImageListAdapter(context, mDataList)
        recyclerView.adapter = mAdapter
    }

    fun setExtraId(communityId: String?) {
        mCommunityId = communityId
        initData()
    }

    fun initData() {
        if (mPageNo == 1) {
            view?.onShowLoading()
        }
        mModel.getPlotTypeList(mCommunityId, mPageNo, mPageSize, object : HttpRequestCallback<PlotTypeBean>() {
            override fun onSuccess(result: PlotTypeBean, message: String?) {
                view?.onDismissLoading()
                view?.onSuccess(result, result.obtainDataList().size >= mPageSize)
                updatePage(result)
                mPageNo += 1
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onDismissLoading()
                view?.onFailure(failReason, mPageNo == 1)
            }
        })
    }


    private fun updatePage(bean: PlotTypeBean) {
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

    private fun updateBlueprintList(dataList: MutableList<PlotTypeBean.DataBean>) {
        val sList = ListUtils.averageAssignFixLength(dataList, 2)
        for (i in sList.indices) {
            val item: PictureDisplayItem
            if (sList[i].size > 1) {
                item = PictureDisplayItem(sList[i][0].getImage(mImageSize), sList[i][0].getShowName(), sList[i][1].getImage(mImageSize), sList[i][1].getShowName())
                item.topId = sList[i][0].id
                item.bottomId = sList[i][1].id
            } else {
                item = PictureDisplayItem(sList[i][0].getImage(mImageSize), sList[i][0].getShowName(), null, null)
                item.topId = sList[i][0].id
            }
            mDataList.add(item)
        }
        mAdapter?.notifyDataSetChanged()
    }
}