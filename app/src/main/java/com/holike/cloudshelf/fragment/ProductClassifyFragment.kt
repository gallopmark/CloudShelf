package com.holike.cloudshelf.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.holike.cloudshelf.R
import com.holike.cloudshelf.activity.PicturePreviewActivity
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.HollyFragment
import com.holike.cloudshelf.bean.ProductOptionBean
import com.holike.cloudshelf.mvp.presenter.fragment.ProductClassifyPresenter
import com.holike.cloudshelf.mvp.view.fragment.ProductClassifyView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import kotlinx.android.synthetic.main.fragment_multi_type.*
import kotlinx.android.synthetic.main.include_backtrack2.*
import kotlinx.android.synthetic.main.include_main_layout.*

//产品大全-全屋定制、橱柜定制、木门定制、家居家品
class ProductClassifyFragment : HollyFragment<ProductClassifyPresenter, ProductClassifyView>(),
        ProductClassifyView, OnLoadMoreListener {
    override fun getLayoutResourceId(): Int = R.layout.fragment_multi_type

    override fun setup(savedInstanceState: Bundle?) {
        typeTView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_title_products, 0, 0, 0)
        val extras = arguments
        extras?.let {
            typeTView.text = it.getString("title")
            mPresenter.setDictCode(it.getString("dictCode"))
        }
        mPresenter.initRView(mContext, centerRView, bottomRView)
        startAnim()
        mPresenter.initData()
        refreshLayout.setOnLoadMoreListener(this)
    }

    private fun startAnim() {
        topLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_bottom_to_top))
        centerRView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_bottom_to_top_slow))
        bottomRView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_bottom_to_top_more_slow))
        view_back.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_bottom_to_top_more_slow))
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter.onLoadMore()
    }

    override fun onReload() {
        mPresenter.initData()
    }

    override fun onShowLoading() {
        showLoading()
    }

    override fun onDismissLoading() {
        dismissLoading()
    }

    override fun onProductProgramResponse(bean: ProductOptionBean, isLoadMoreEnabled: Boolean) {
        countTView.text = String.format(getString(R.string.text_program_count, bean.pageTotal))
        hideDefaultPage()
        if (refreshLayout.visibility != View.VISIBLE) {
            refreshLayout.visibility = View.VISIBLE
        }
        refreshLayout.finishLoadMore()
        refreshLayout.setEnableLoadMore(isLoadMoreEnabled)
    }

    override fun onProductProgramFailure(failReason: String?, isInit: Boolean) {
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
        multiTypeContent.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.translate_to_bottom))
        multiTypeContent.postDelayed({ PicturePreviewActivity.openProductClassify(mContext as BaseActivity, id) }, 400)
    }
}