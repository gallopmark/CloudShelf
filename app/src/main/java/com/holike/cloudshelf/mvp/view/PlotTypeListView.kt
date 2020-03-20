package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.bean.PlotTypeBean
import pony.xcode.mvp.BaseView


interface PlotTypeListView : BaseView {

    fun onShowLoading()
    fun onDismissLoading()

    fun onSuccess(bean: PlotTypeBean, isLoadMoreEnabled: Boolean)

    fun onNoResults()
    fun onFailure(failReason: String?, isShowError: Boolean)

    fun onItemClick(id: String?)
}