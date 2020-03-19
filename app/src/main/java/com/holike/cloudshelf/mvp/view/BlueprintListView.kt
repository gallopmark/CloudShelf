package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.bean.BlueprintBean
import pony.xcode.mvp.BaseView


interface BlueprintListView : BaseView {

    fun onShowLoading()
    fun onDismissLoading()

    fun onSuccess(bean: BlueprintBean, isLoadMoreEnabled: Boolean)

    fun onNoResults()

    fun onFailure(failReason: String?, showErrorPage: Boolean)

    fun onPictureItemClick(id: String?)
}