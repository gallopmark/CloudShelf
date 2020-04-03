package com.holike.cloudshelf.mvp.presenter

import android.content.Context
import android.os.Handler
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.ProductCatalogBean
import com.holike.cloudshelf.mvp.model.ProductCatalogModel
import com.holike.cloudshelf.mvp.view.ProductCatalogView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import io.reactivex.disposables.Disposable
import pony.xcode.mvp.BasePresenter
import pony.xcode.recycler.CommonAdapter


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

    fun setLayoutAnimation(contentRView: RecyclerView) {
        //通过加载XML动画设置文件来创建一个Animation对象；
        val animation = AnimationUtils.loadAnimation(contentRView.context, R.anim.item_anim_from_left)
        //为ListView设置LayoutAnimationController属性；
        contentRView.layoutAnimation = LayoutAnimationController(animation).apply {
            //设置控件显示的顺序；
            order = LayoutAnimationController.ORDER_NORMAL
            //设置控件显示间隔时间；
            delay = 0.8f
        }
    }

    private inner class ItemListAdapter(context: Context, dataList: MutableList<ProductCatalogBean.PlansContentsBean>, private val itemWidth: Int)
        : CommonAdapter<ProductCatalogBean.PlansContentsBean>(context, dataList) {
        override fun getItemResourceId(viewType: Int): Int = R.layout.item_product_catalog

        override fun bindViewHolder(holder: RecyclerHolder, t: ProductCatalogBean.PlansContentsBean, position: Int) {
            val lp = holder.itemView.layoutParams as RecyclerView.LayoutParams
            lp.width = itemWidth
            holder.itemView.layoutParams = lp
            val iconIView = holder.getView<ImageView>(R.id.iv_icon)
            val ilp = iconIView.layoutParams as FrameLayout.LayoutParams
            ilp.width = itemWidth / 2
            ilp.height = itemWidth / 2
            iconIView.layoutParams = ilp
            Glide.with(mContext).load(t.icon).into(iconIView)
            Glide.with(mContext).load(t.pic).into(holder.getView(R.id.iv_pic))
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