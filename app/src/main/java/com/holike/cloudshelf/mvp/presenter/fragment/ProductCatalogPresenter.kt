package com.holike.cloudshelf.mvp.presenter.fragment

import android.content.Context
import android.os.Handler
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.ProductCatalogBean
import com.holike.cloudshelf.mvp.BasePresenter
import com.holike.cloudshelf.mvp.model.fragment.ProductCatalogModel
import com.holike.cloudshelf.mvp.view.fragment.ProductCatalogView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import io.reactivex.disposables.Disposable
import pony.xcode.recycler.lib.CommonAdapter


//产品大全
class ProductCatalogPresenter : BasePresenter<ProductCatalogModel, ProductCatalogView>() {
    private var mHandler: Handler? = null

    //产品大全模块列表查询
    fun getPlanContentList() {
        mModel.getPlanContentList(object : HttpRequestCallback<ProductCatalogBean>() {
            override fun onStart(d: Disposable?) {
                view?.onShowLoading()
            }

            override fun onSuccess(result: ProductCatalogBean, message: String?) {
                view?.onResponse(result)
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onFailure(failReason)
            }

            override fun onCompleted() {
                view?.onDismissLoading()
            }
        })
    }

    private val mDataList = ArrayList<ProductCatalogBean.PlansContentsBean>()
    private var mAdapter: ItemListAdapter? = null

    fun attach(contentRView: RecyclerView, itemWidth: Int) {
        mAdapter = ItemListAdapter(contentRView.context, mDataList, itemWidth)
        contentRView.adapter = mAdapter
    }

    fun setItems(dataList: MutableList<ProductCatalogBean.PlansContentsBean>) {
        this.mDataList.clear()
        this.mDataList.addAll(dataList)
        mAdapter?.notifyDataSetChanged()
    }

    private inner class ItemListAdapter(context: Context, dataList: MutableList<ProductCatalogBean.PlansContentsBean>, private val itemWidth: Int)
        : CommonAdapter<ProductCatalogBean.PlansContentsBean>(context, dataList) {
        override fun getItemResourceId(viewType: Int): Int = R.layout.item_product_catalog

        override fun bindViewHolder(holder: RecyclerHolder, t: ProductCatalogBean.PlansContentsBean, position: Int) {
            val lp = holder.itemView.layoutParams as RecyclerView.LayoutParams
            if (position == 0) {
                lp.leftMargin = 0
            } else {
                lp.leftMargin = mContext.resources.getDimensionPixelSize(R.dimen.dp_10)
            }
            lp.width = itemWidth
            holder.itemView.layoutParams = lp
            val iconIView = holder.getView<ImageView>(R.id.iv_icon)
            val ilp = iconIView.layoutParams as FrameLayout.LayoutParams
            ilp.width = itemWidth / 2
            ilp.height = itemWidth / 2
            iconIView.layoutParams = ilp
            Glide.with(mContext).load(t.icon).placeholder(t.getPlaceholderIcon()).into(iconIView)
            Glide.with(mContext).load(t.pic).placeholder(t.getBackgroundPic()).into(holder.getView(R.id.iv_pic))
            holder.itemView.setOnClickListener {
                view?.onItemClick(t.dictCode, t.name)
            }
        }
    }

    override fun unregister() {
        mHandler?.removeCallbacksAndMessages(null)
        super.unregister()
    }
}