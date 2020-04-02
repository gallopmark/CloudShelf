package com.holike.cloudshelf.mvp.presenter

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.ProductCatalogBean
import com.holike.cloudshelf.mvp.model.ProductCatalogModel
import com.holike.cloudshelf.mvp.view.ProductCatalogView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import pony.xcode.mvp.BasePresenter


//产品大全
class ProductCatalogPresenter : BasePresenter<ProductCatalogModel, ProductCatalogView>() {
    private var mHandler: Handler? = null

    //产品大全模块列表查询
    fun getPlanContentList() {
        mModel.getPlanContentList(object : HttpRequestCallback<ProductCatalogBean>() {
            override fun onSuccess(result: ProductCatalogBean, message: String?) {
                view?.onResponse(result)
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onFailure(failReason)
            }
        })
    }

    fun setContentList(contentLayout: LinearLayout, dataList: MutableList<ProductCatalogBean.PlansContentsBean>, itemWidth: Int) {
        contentLayout.removeAllViews()
        val context = contentLayout.context
        val dp10 = context.resources.getDimensionPixelSize(R.dimen.dp_10)
        var delayMillis = 0L
        mHandler = Handler()
        for (i in dataList.indices) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_product_catalog, contentLayout, false)
            Glide.with(context).load(dataList[i].pic).into(itemView.findViewById(R.id.iv_pic))
            Glide.with(context).load(dataList[i].icon).into(itemView.findViewById(R.id.iv_icon))
            val lp = LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.MATCH_PARENT)
            if (i > 0) {
                lp.leftMargin = dp10
            }
            itemView.layoutParams = lp
            itemView.visibility = View.INVISIBLE
            contentLayout.addView(itemView)
            itemView.setOnClickListener { view?.onItemClick(dataList[i].dictCode, dataList[i].name) }
            mHandler?.postDelayed({
                itemView.visibility = View.VISIBLE
                itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.translate_from_left))
            }, delayMillis)
            delayMillis += 600L
        }
    }

    override fun unregister() {
        mHandler?.removeCallbacksAndMessages(null)
        super.unregister()
    }
}