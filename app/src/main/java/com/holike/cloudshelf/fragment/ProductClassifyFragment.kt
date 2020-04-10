package com.holike.cloudshelf.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.holike.cloudshelf.R
import com.holike.cloudshelf.activity.ContentInfoActivity
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.HollyFragment
import com.holike.cloudshelf.bean.ProductOptionBean
import com.holike.cloudshelf.mvp.presenter.fragment.ProductClassifyPresenter
import com.holike.cloudshelf.mvp.view.fragment.ProductClassifyView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import kotlinx.android.synthetic.main.fragment_multi_type.*
import kotlinx.android.synthetic.main.include_backtrack_light.*
import kotlinx.android.synthetic.main.include_main_layout.*

//产品大全-全屋定制、橱柜定制、木门定制
open class ProductClassifyFragment : HollyFragment<ProductClassifyPresenter, ProductClassifyView>(),
        ProductClassifyView, OnLoadMoreListener {
    override fun getLayoutResourceId(): Int = R.layout.fragment_multi_type

    override fun getBacktrackResource(): Int = R.layout.include_backtrack_light

    override fun setup(savedInstanceState: Bundle?) {
        initViewData()
    }

    private fun initViewData() {
        typeTView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_title_products, 0, 0, 0)
        val extras = arguments
        extras?.let {
            typeTView.text = it.getString("title")
            mPresenter.setDictCode(it, it.getString("dictCode"))
        }
        mPresenter.initRView(mContext, centerRView, bottomRView)
        initData()
        startLayoutAnimation()
        refreshLayout.setOnLoadMoreListener(this)
    }

    private fun startLayoutAnimation() {
        containerLayout.layoutAnimation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.la_layout_from_bottom)
        backtrack.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_from_bottom))
    }

    override fun onShowNavigation(isShow: Boolean) {
        bottomRView.visibility = if (isShow) View.VISIBLE else View.INVISIBLE
    }

    override fun onBottomSpecUpdate() {
        if (bottomRView.visibility != View.INVISIBLE) {
            bottomRView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_from_bottom))
        }
    }

    private fun initData() {
        mPresenter.initData()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter.onLoadMore()
    }

    override fun onReload() {
        initData()
    }

    override fun onShowLoading() {
        showLoading()
    }

    override fun onDismissLoading() {
        dismissLoading()
    }

    override fun onProductClassifyResponse(bean: ProductOptionBean, isLoadMoreEnabled: Boolean) {
        countTView.text = String.format(getString(R.string.text_total_count, bean.totalCount))
        hideDefaultPage()
        if (refreshLayout.visibility != View.VISIBLE) {
            refreshLayout.visibility = View.VISIBLE
        }
        refreshLayout.finishLoadMore()
        refreshLayout.setEnableLoadMore(isLoadMoreEnabled)
    }

    override fun onProductClassifyFailure(failReason: String?, isInit: Boolean) {
        refreshLayout.finishLoadMore()
        if (isInit) {
            countTView.text = String.format(getString(R.string.text_program_count, "0"))
            refreshLayout.visibility = View.GONE
            onNetworkError(failReason)
        } else {
            if (refreshLayout.visibility != View.VISIBLE) {
                refreshLayout.visibility = View.VISIBLE
            }
            showShortToast(failReason)
        }
    }

    //无查询结果
    override fun onNoQueryResults() {
        refreshLayout.visibility = View.GONE
        onNoResult()
    }

    override fun onItemClick(id: String?) {
        ContentInfoActivity.openProductClassify(mContext as BaseActivity, id)
    }
}