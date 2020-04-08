package com.holike.cloudshelf.base

import android.view.View
import com.scwang.smartrefresh.horizontal.SmartRefreshHorizontal
import pony.xcode.mvp.BasePresenter
import pony.xcode.mvp.BaseView

//通用加载更多fragment
abstract class RefreshFragment<P : BasePresenter<*, V>, V : BaseView, B> : HollyFragment<P, V>(), RefreshView<B> {

    abstract fun getRefreshLayout(): SmartRefreshHorizontal

    //显示加载框
    override fun onShowLoading() {
        showLoading()
    }

    //销毁加载框
    override fun onDismissLoading() {
        dismissLoading()
    }

    /**
     * 加载成功
     * @param bean 实体类
     * @param isLoadMoreEnabled 是否还能加载更多
     */
    override fun onSuccess(bean: B, isLoadMoreEnabled: Boolean) {
        whenLoadSuccess(bean)
        hideDefaultPage()  //加载成功后 隐藏缺省页
        val refreshLayout = getRefreshLayout()
        if (refreshLayout.visibility != View.VISIBLE) {
            refreshLayout.visibility = View.VISIBLE
        }
        refreshLayout.finishLoadMore()
        refreshLayout.setEnableLoadMore(isLoadMoreEnabled)
    }

    //加载成功的其他处理
    abstract fun whenLoadSuccess(bean: B)

    //无结果
    override fun onNoResults() {
        getRefreshLayout().visibility = View.GONE
        onNoResult()
    }

    //加载失败
    override fun onFailure(failReason: String?, showErrorPage: Boolean) {
        val refreshLayout = getRefreshLayout()
        refreshLayout.finishLoadMore()
        if (showErrorPage) {
            whenLoadError(failReason)
            refreshLayout.visibility = View.GONE
            onNetworkError(failReason)
        } else {
            if (refreshLayout.visibility != View.VISIBLE) {
                refreshLayout.visibility = View.VISIBLE
            }
            showShortToast(failReason)
        }
    }

    abstract fun whenLoadError(failReason: String?)
}