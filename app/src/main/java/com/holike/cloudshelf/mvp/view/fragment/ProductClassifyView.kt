package com.holike.cloudshelf.mvp.view.fragment

import com.holike.cloudshelf.bean.ProductOptionBean
import com.holike.cloudshelf.mvp.view.IView

interface ProductClassifyView : IView {

    fun onProductProgramResponse(bean: ProductOptionBean, isLoadMoreEnabled: Boolean)
    fun onNoQueryResults()
    fun onProductProgramFailure(failReason: String?, isInit: Boolean)

    //打开方案详情
    fun onItemClick(id: String?)
}