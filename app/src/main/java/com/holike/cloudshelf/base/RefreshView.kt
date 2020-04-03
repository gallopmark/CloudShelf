package com.holike.cloudshelf.base

import com.holike.cloudshelf.mvp.view.IView

interface RefreshView<B> : IView {

    fun onSuccess(bean: B, isLoadMoreEnabled: Boolean)

    fun onNoResults()

    fun onFailure(failReason: String?, showErrorPage: Boolean)
}