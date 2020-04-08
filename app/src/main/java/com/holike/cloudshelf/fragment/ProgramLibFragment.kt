package com.holike.cloudshelf.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.holike.cloudshelf.R
import com.holike.cloudshelf.activity.ContentInfoActivity
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.HollyFragment
import com.holike.cloudshelf.bean.TableModelHouseBean
import com.holike.cloudshelf.mvp.presenter.fragment.ProgramLibPresenter
import com.holike.cloudshelf.mvp.view.fragment.ProgramLibView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import kotlinx.android.synthetic.main.fragment_multi_type.*
import kotlinx.android.synthetic.main.include_main_layout.*

//方案库
class ProgramLibFragment : HollyFragment<ProgramLibPresenter, ProgramLibView>(), ProgramLibView, OnLoadMoreListener {

    override fun getLayoutResourceId(): Int = R.layout.fragment_multi_type

    override fun setup(savedInstanceState: Bundle?) {
        initViewData()
    }

    private fun initViewData() {
        typeTView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_title_programee, 0, 0, 0)
        typeTView.text = mContext.getString(R.string.text_program_library)
        mPresenter.initRView(mContext, centerRView, bottomRView)
        startLayoutAnimation()
        mPresenter.initData()
        refreshLayout.setOnLoadMoreListener(this)
    }

    private fun startLayoutAnimation() {
        containerLayout.layoutAnimation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.la_layout_from_bottom)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter.onLoadMore()
    }

    override fun onTableModelHouseResponse(bean: TableModelHouseBean, isLoadMoreEnabled: Boolean) {
        countTView.text = String.format(getString(R.string.text_program_count, bean.total))
        hideDefaultPage()
        if (refreshLayout.visibility != View.VISIBLE) {
            refreshLayout.visibility = View.VISIBLE
        }
        refreshLayout.finishLoadMore()
        refreshLayout.setEnableLoadMore(isLoadMoreEnabled)
    }

    override fun onBottomSpecUpdate() {
        bottomRView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_from_bottom))
    }

    override fun onTableModelHouseFailure(failReason: String?, isInit: Boolean) {
        refreshLayout.finishLoadMore(false)
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

    override fun onShowLoading() {
        showLoading()
    }

    override fun onDismissLoading() {
        dismissLoading()
    }

    //无查询结果
    override fun onNoQueryResults() {
        refreshLayout.visibility = View.GONE
        onNoResult()
    }

    //打开方案详情页面
    override fun onOpenProgramLib(id: String?) {
        ContentInfoActivity.openProgramLib(mContext as BaseActivity, id)
    }

    override fun onReload() {
        mPresenter.initData()
    }
}