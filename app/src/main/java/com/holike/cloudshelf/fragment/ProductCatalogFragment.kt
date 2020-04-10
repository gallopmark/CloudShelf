package com.holike.cloudshelf.fragment

import android.os.Bundle
import android.view.animation.AnimationUtils
import com.bumptech.glide.Glide
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.activity.MultiTypeActivity
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.HollyFragment
import com.holike.cloudshelf.bean.ProductCatalogBean
import com.holike.cloudshelf.mvp.presenter.fragment.ProductCatalogPresenter
import com.holike.cloudshelf.mvp.view.fragment.ProductCatalogView
import kotlinx.android.synthetic.main.fragment_product_catalog.*
import kotlinx.android.synthetic.main.include_qrcode_layout.*

//产品大全
class ProductCatalogFragment : HollyFragment<ProductCatalogPresenter, ProductCatalogView>(), ProductCatalogView {

    override fun getLayoutResourceId(): Int = R.layout.fragment_product_catalog

    override fun getBacktrackResource(): Int = R.layout.include_backtrack

    override fun setup(savedInstanceState: Bundle?) {
        initLayoutParams()
        startLayoutAnimation()
        mPresenter.getPlanContentList()
    }

    private fun initLayoutParams() {
        val itemWidth = ((CurrentApp.getInstance().getMaxPixels()
                - getDimensionPixelSize(R.dimen.dp_45) * 2
                - getDimensionPixelSize(R.dimen.dp_20)
                - getDimensionPixelSize(R.dimen.dp_80)
                - getDimensionPixelSize(R.dimen.dp_10) * 3) / 4f).toInt()
        mPresenter.attach(contentRView, itemWidth)
    }

    private fun startLayoutAnimation() {
        productTView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_from_bottom))
        rightRL.scheduleLayoutAnimation()
    }

    override fun onShowLoading() {
        showLoading(true)
    }

    override fun onDismissLoading() {
        dismissLoading()
    }

    override fun onResponse(bean: ProductCatalogBean) {
        showContentView()
        Glide.with(this).load(bean.miniQrUrl).error(R.mipmap.ic_wxacode).into(miniQrUrlIView)
        mPresenter.setItems(bean.getModuleContentList())
        contentRView.scheduleLayoutAnimation()
    }

    override fun onFailure(failReason: String?) {
        showShortToast(failReason)
    }

    override fun onItemClick(dictCode: String?, name: String?) {
        var title = getString(R.string.text_product_catalog)
        if (!name.isNullOrEmpty()) {
            title += " > $name"
        }
        val extras = Bundle().apply {
            putString("title", title)
            putString("dictCode", dictCode)
        }
        MultiTypeActivity.open(mContext as BaseActivity, MultiTypeActivity.TYPE_PRODUCT_MODULE, extras)
    }
}