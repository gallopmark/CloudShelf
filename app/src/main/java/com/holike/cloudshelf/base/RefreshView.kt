package com.holike.cloudshelf.base

import pony.xcode.mvp.BaseView

interface RefreshView<B> : BaseView {

    fun onShowLoading()
    fun onDismissLoading()

    fun onSuccess(bean: B, isLoadMoreEnabled: Boolean)

    fun onNoResults()

    fun onFailure(failReason: String?, showErrorPage: Boolean)
}