package com.holike.cloudshelf.mvp.view.fragment

import com.holike.cloudshelf.bean.ProductOptionBean
import pony.xcode.mvp.BaseView


interface ProductClassifyView : BaseView {

    fun onShowLoading()
    fun onDismissLoading()
    fun onProductProgramResponse(bean: ProductOptionBean, isLoadMoreEnabled: Boolean)
    fun onNoQueryResults()
    fun onProductProgramFailure(failReason: String?, isInit: Boolean)

    //打开方案详情
    fun onItemClick(id: String?)
}