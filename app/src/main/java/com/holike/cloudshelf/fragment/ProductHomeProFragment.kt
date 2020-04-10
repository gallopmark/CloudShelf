package com.holike.cloudshelf.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.holike.cloudshelf.R
import com.holike.cloudshelf.activity.MultiTypeActivity
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.HollyFragment
import com.holike.cloudshelf.bean.ProductHomeProBean
import com.holike.cloudshelf.mvp.presenter.fragment.ProductHomeProPresenter
import com.holike.cloudshelf.mvp.view.fragment.ProductHomeProView
import kotlinx.android.synthetic.main.fragment_product_homepro.*
import kotlinx.android.synthetic.main.include_backtrack.*


//产品大全-家居家品
class ProductHomeProFragment : HollyFragment<ProductHomeProPresenter, ProductHomeProView>(), ProductHomeProView {

    override fun getLayoutResourceId(): Int = R.layout.fragment_product_homepro

    override fun getBacktrackResource(): Int = R.layout.include_backtrack

    override fun setup(savedInstanceState: Bundle?) {
        typeTView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_title_products, 0, 0, 0)
        val extras = arguments
        extras?.let { typeTView.text = it.getString("title") }
        initViewData()
    }

    private fun initViewData() {
        mPresenter.initRView(mContext, bottomRView)
        mPresenter.initScrollController(centerRView, backTopTView)
        mPresenter.initData()
        backtrack.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_from_bottom))
        homeProContainer.scheduleLayoutAnimation()
    }

    override fun onDictSelect(name: String?) {
        var title = getString(R.string.text_product_catalog)
        if (!name.isNullOrEmpty()) {
            title += " > $name"
        }
        typeTView.text = title
    }

    override fun onShowLoading() {
        showLoading()
        centerRView.adapter = null
    }

    override fun onDismissLoading() {
        dismissLoading()
    }

    override fun onSuccess(isCurtain: Boolean, data: ArrayList<ProductHomeProBean>) {
        hideDefaultPage()
        if (centerRView.visibility != View.VISIBLE) {
            centerRView.visibility = View.VISIBLE
        }
        mPresenter.setAdapter(mContext, centerRView, data, isCurtain)
    }

    override fun onNoQueryResults() {
        centerRView.visibility = View.GONE
        onNoResult()
    }

    override fun onFailure(failReason: String?) {
        onNetworkError(failReason)
    }

    override fun onItemClick(parentId: String?, id: String?, classification: String?, title: String?, isShowNavigation: Boolean) {
        MultiTypeActivity.open(mContext as BaseActivity, MultiTypeActivity.TYPE_PRODUCT_MODULE, Bundle().apply {
            putString("title", title)
            putString("dictCode", classification)
            putBoolean("isShowNavigation", isShowNavigation)
            putString("space_furnished", parentId)
            putString("categoryId", id)
        })
    }
}